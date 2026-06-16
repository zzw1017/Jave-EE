package com.example.attendance.controller;

import com.example.attendance.config.AuthorizationInterceptor;
import com.example.attendance.entity.CourseSchedule;
import com.example.attendance.entity.User;
import com.example.attendance.result.Result;
import com.example.attendance.service.CourseScheduleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courseSchedule")
public class CourseScheduleController {

    @Autowired
    private CourseScheduleService courseScheduleService;

    @PostMapping("/save")
    public Result<CourseSchedule> save(@RequestBody CourseSchedule schedule,
                                       HttpSession session) {
        User user = getLoginUser(session);

        if (!isManager(user)) {
            return fail("无权限操作");
        }

        CourseSchedule saved = courseScheduleService.save(schedule);

        return ok(saved);
    }

    @GetMapping("/course/{courseId}")
    public Result<List<CourseSchedule>> getByCourseId(@PathVariable Long courseId,
                                                      HttpSession session) {
        User user = getLoginUser(session);

        if (!isManager(user)) {
            return fail("无权限操作");
        }

        List<CourseSchedule> list = courseScheduleService.getByCourseId(courseId);

        return ok(list);
    }

    @PostMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               HttpSession session) {
        User user = getLoginUser(session);

        if (!isManager(user)) {
            return fail("无权限操作");
        }

        courseScheduleService.delete(id);

        return ok(null);
    }

    private User getLoginUser(HttpSession session) {
        User user = (User) session.getAttribute(AuthorizationInterceptor.SESSION_USER);

        if (user == null) {
            throw new RuntimeException("用户未登录");
        }

        return user;
    }

    private boolean isManager(User user) {
        return "ADMIN".equalsIgnoreCase(user.getRole())
                || "TEACHER".equalsIgnoreCase(user.getRole());
    }

    private <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    private <T> Result<T> fail(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }
}