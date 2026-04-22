package com.example.attendance.controller;

import com.example.attendance.entity.Student;
import com.example.attendance.service.StudentService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/save")
    public Student save(@RequestBody Student student) {
        return studentService.save(student);
    }

    @GetMapping("/{id}")
    public Optional<Student> getById(@PathVariable Long id) {
        return studentService.findById(id);
    }

    @GetMapping("/username/{username}")
    public Optional<Student> getByUsername(@PathVariable String username) {
        return studentService.findByUsername(username);
    }

    @GetMapping("/list")
    public List<Student> list() {
        return studentService.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        studentService.deleteById(id);
    }
}


/*
import com.example.attendance.entity.AttendanceRecord;
import com.example.attendance.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import com.example.attendance.entity.Student;
import com.example.attendance.result.Result;

@RestController
@RequestMapping("/student")
public class StudentController {
   /* @GetMapping("/student/info")
    public String getstudentinfo() {
        return "姓名：朱正威，学号：42411121，班级：计算机科学与技术3班";
    }

    @PostMapping("/student/attendance")
    public String studentAttendance(@RequestBody String s) {
        return "学号为" + s + "的学生打卡成功";
    }

    @GetMapping("/student/course")
    public List<String> getstudentCourse() {
        return Arrays.asList("java EE开发实践", "高等数学", "机器学习");
    }



    @GetMapping("/student/info/{studenntId}")
    public Result<Student>
    getStudentInfo(@PathVariable String studentId) {
        Student student = new Student();
        student.setStudentId(studentId);
        student.setName("朱正威");
        student.setClassName("java EE开发实践");
        student.setAge(20);
        return Result.success(student);
    }
    @GetMapping("/student/list")
    public Result<List<Student>>
    getStudentList(
            @RequestParam String className,
            @RequestParam(defaultValue = "1")int page){
        List<Student>list = new ArrayList<>();
        Student student = new Student();
        student.setStudentId("42411121");
        student.setName("朱正威");
        student.setClassName("java EE开发实践");
        student.setAge(20);
        list.add(student);
        System.out.println("查询的班级：" + className + "， 页码："+ page);
        return Result.success(list);
    }
    @PostMapping("/attendance/update")
    public Result<String>
    updateAttendance(@RequestBody AttendanceRecord record){
        String updateMsg = String.format("学生%s在课程%s的考勤状态已更新为：%s",record.getStudentId(),record.getCoutseId(),record.getStatus());
        return Result.success(updateMsg);
    }*/
/*

    @Autowired
    private StudentService studentService;

    @PostMapping("/create")
    public Result<String> create(@RequestBody Student student){
        return Result.success(studentService.createStudent(student));
    }

    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable String id){
        return Result.success(studentService.getStudentId(id));
    }
}
*/