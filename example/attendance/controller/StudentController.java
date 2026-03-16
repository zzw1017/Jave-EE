package com.example.attendance.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.*;

@RestController
public class StudentController {
    @GetMapping("/student/info")
    public String getstudentinfo(){
        return "姓名：朱正威，学号：42411121，班级：计算机科学与技术3班";
    }
    @PostMapping("/student/attendance")
    public String studentAttendance(@RequestBody String s){
        return "学号为"+ s +"的学生打卡成功";
    }
    @GetMapping("/student/course")
    public List<String> getstudentCourse(){
        return Arrays.asList("java EE开发实践","高等数学","机器学习");
    }
}
