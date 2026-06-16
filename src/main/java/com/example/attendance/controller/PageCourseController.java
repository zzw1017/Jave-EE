package com.example.attendance.controller;
import com.example.attendance.config.AuthorizationInterceptor;
import com.example.attendance.entity.Course;
import com.example.attendance.entity.User;
import com.example.attendance.service.CourseService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/course/page")
public class PageCourseController {

    @Resource
    private CourseService courseService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    /**
     * 课程列表
     */
    @GetMapping("/list")
    public String list(Model model, HttpSession session) {

        User loginUser = getLoginUser(session);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!isManager(loginUser)) {
            return "redirect:/dashboard?permissionDenied=true";
        }

        List<Course> courseList = courseService.findAll();
        model.addAttribute("courseList", courseList);

        return "course-list";
    }

    /**
     * 新增课程页面
     */
    @GetMapping("/add")
    public String toAdd(Model model, HttpSession session) {

        User loginUser = getLoginUser(session);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!isManager(loginUser)) {
            return "redirect:/dashboard?permissionDenied=true";
        }

        model.addAttribute("course", new Course());
        model.addAttribute("startTimeStr", "");
        model.addAttribute("endTimeStr", "");

        return "course-form";
    }

    /**
     * 编辑课程页面
     */
    @GetMapping("/edit/{id}")
    public String toEdit(@PathVariable Long id,
                         Model model,
                         HttpSession session,
                         RedirectAttributes ra) {

        User loginUser = getLoginUser(session);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!isManager(loginUser)) {
            return "redirect:/dashboard?permissionDenied=true";
        }

        Course course = courseService.findById(id);

        if (course == null) {
            ra.addFlashAttribute("errorMsg", "课程不存在");
            return "redirect:/course/page/list";
        }

        model.addAttribute("course", course);
        model.addAttribute("startTimeStr", formatDateTime(course.getStartTime()));
        model.addAttribute("endTimeStr", formatDateTime(course.getEndTime()));

        return "course-form";
    }

    /**
     * 保存课程
     */
    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam String courseName,
                       @RequestParam String startTime,
                       @RequestParam String endTime,
                       Model model,
                       HttpSession session,
                       RedirectAttributes ra) {

        User loginUser = getLoginUser(session);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!isManager(loginUser)) {
            return "redirect:/dashboard?permissionDenied=true";
        }

        String error = courseService.saveCourse(id, courseName, startTime, endTime);

        if (error != null) {
            Course course = new Course();
            course.setId(id);
            course.setCourseName(courseName);

            model.addAttribute("course", course);
            model.addAttribute("startTimeStr", startTime);
            model.addAttribute("endTimeStr", endTime);
            model.addAttribute("errorMsg", error);

            return "course-form";
        }

        ra.addFlashAttribute("msg", "课程保存成功");
        return "redirect:/course/page/list";
    }

    /**
     * 删除课程
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         HttpSession session,
                         RedirectAttributes ra) {

        User loginUser = getLoginUser(session);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!isManager(loginUser)) {
            return "redirect:/dashboard?permissionDenied=true";
        }

        courseService.deleteById(id);

        ra.addFlashAttribute("msg", "课程删除成功");
        return "redirect:/course/page/list";
    }

    private User getLoginUser(HttpSession session) {
        return (User) session.getAttribute(AuthorizationInterceptor.SESSION_USER);
    }

    private boolean isManager(User user) {
        return user != null
                && ("TEACHER".equalsIgnoreCase(user.getRole())
                || "ADMIN".equalsIgnoreCase(user.getRole()));
    }

    private String formatDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }

        return timestamp.toLocalDateTime().format(DATE_TIME_FORMATTER);
    }
}