package com.example.attendance.controller;


import com.example.attendance.entity.Student;
import com.example.attendance.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/student")
public class PageStudentController{
    private final StudentService studentService;

    public PageStudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // 列表页（带搜索、排序、分页）
    @GetMapping("/list")
    public String list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "studentId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        // 调用 service
        Page<Student> studentPage = studentService.findPage(keyword, page-1, size, sortField, sortDir);

        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("totalElements", studentPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        return "student-list";
    }

    // 新增页面
    @GetMapping("/add")
    public String toAdd(Model model) {
        model.addAttribute("student", new Student());
        return "student-form";
    }

    // 编辑页面
    @GetMapping("/edit/{studentId}")
    public String toEdit(@PathVariable String studentId, Model model) {
        Student student = studentService.getById(studentId);
        model.addAttribute("student", student);
        return "student-form";
    }

    // 保存
    @PostMapping("/save")
    public String save(Student student, RedirectAttributes ra) {
        studentService.createStudent(student);
        ra.addFlashAttribute("msg", "操作成功！");
        return "redirect:/student/list";
    }

    // 删除
    @GetMapping("/delete/{studentId}")
    public String delete(@PathVariable String studentId, RedirectAttributes ra) {
        studentService.deleteById(studentId);
        ra.addFlashAttribute("msg", "删除成功！");
        return "redirect:/student/list";
    }

    // 批量删除
    @PostMapping("/batchDelete")
    public String batchDelete(@RequestParam("ids") String[] ids, RedirectAttributes ra) {
        List<String> idList = Arrays.asList(ids);
        studentService.batchDelete(idList);
        ra.addFlashAttribute("msg", "批量删除成功！");
        return "redirect:/student/list";
    }
}
