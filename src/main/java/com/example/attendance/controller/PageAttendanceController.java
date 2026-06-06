package com.example.attendance.controller;

import com.example.attendance.dto.StatisticsDTO;
import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Course;
import com.example.attendance.result.ImportResult;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.CourseService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.List;

@Controller
@RequestMapping("/attendance")
public class PageAttendanceController{
   // 注入接口，不注入实现类
    @Resource
    private AttendanceService attendanceService;
    @Resource
    private CourseService courseService;

    @GetMapping("/checkin")
    public String toCheckin(Model model){
        List<Course> courseList = courseService.findAll();
        model.addAttribute("courseList",courseList);
        return "checkin";
    }

    @PostMapping("/checkin")
    public String doCheckin(Attendance attendance, HttpServletRequest request, Model model){
        String ip = request.getRemoteAddr();
        String msg = attendanceService.checkIn(attendance,ip);
        model.addAttribute("msg",msg);
        return "result";
    }

    @PostMapping("/checkout/{id}")
    public String checkout(@PathVariable Long id,Model model){
        String msg = attendanceService.checkOut(id);
        model.addAttribute("msg",msg);
        return "result";
    }

    @GetMapping("/list")
    public String list(@RequestParam(required = false) Long courseId,
                       @RequestParam(defaultValue = "all") String timeType,
                       Model model){
        List<Attendance> list = attendanceService.filterAttendance(courseId,timeType);
        model.addAttribute("attList",list);
        model.addAttribute("courseId",courseId);
        model.addAttribute("timeType",timeType);
        return "list";
    }
    @GetMapping("/statistics")
    public String statisticsPage(@RequestParam Long studentId, Model model) {
        StatisticsDTO stats = attendanceService.getStudentStatistics(studentId);
        model.addAttribute("stats", stats);
        model.addAttribute("studentId", studentId);
        return "attendance-statistics";
    }

    @GetMapping("/total")
    public String totalStats(Model model) {
        long total = attendanceService.getTotalAttendanceCount();
        model.addAttribute("total", total);
        return "total-stats";
    }
}