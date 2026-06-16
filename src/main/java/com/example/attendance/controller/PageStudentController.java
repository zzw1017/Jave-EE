package com.example.attendance.controller;

import com.example.attendance.entity.Student;
import com.example.attendance.result.ImportResult;
import com.example.attendance.service.StudentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/student")
public class PageStudentController {

    private final StudentService studentService;

    @Value("${file.upload.path}")
    private String uploadPath;

    public PageStudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/page/list")
    public String list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "studentId") String sortField,
                       @RequestParam(defaultValue = "asc") String sortDir,
                       Model model) {

        if (page < 1) {
            page = 1;
        }

        if (size < 1) {
            size = 10;
        }

        List<String> allowSortFields = Arrays.asList(
                "studentId", "name", "gender", "className", "age", "birthDate", "phone"
        );

        if (!allowSortFields.contains(sortField)) {
            sortField = "studentId";
        }

        if (!"asc".equalsIgnoreCase(sortDir) && !"desc".equalsIgnoreCase(sortDir)) {
            sortDir = "asc";
        }

        Page<Student> studentPage = studentService.findPage(
                keyword,
                page - 1,
                size,
                sortField,
                sortDir
        );

        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("totalElements", studentPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        return "student-list";
    }

    @GetMapping("/page/add")
    public String toAdd(Model model) {
        model.addAttribute("student", new Student());
        return "student-form";
    }

    @GetMapping("/page/edit/{studentId}")
    public String toEdit(@PathVariable String studentId,
                         Model model,
                         RedirectAttributes ra) {

        Student student = studentService.getById(studentId);

        if (student == null) {
            ra.addFlashAttribute("errorMsg", "学生不存在！");
            return "redirect:/student/page/list";
        }

        model.addAttribute("student", student);
        return "student-form";
    }

    @PostMapping("/page/save")
    public String save(Student student,
                       Model model,
                       RedirectAttributes ra) {

        String error = validateStudent(student);

        if (error != null) {
            model.addAttribute("errorMsg", error);
            model.addAttribute("student", student);
            return "student-form";
        }

        studentService.createStudent(student);

        ra.addFlashAttribute("msg", "操作成功！");
        return "redirect:/student/page/list";
    }

    @PostMapping("/page/delete/{studentId}")
    public String delete(@PathVariable String studentId,
                         RedirectAttributes ra) {

        studentService.deleteById(studentId);

        ra.addFlashAttribute("msg", "删除成功！");
        return "redirect:/student/page/list";
    }

    @PostMapping("/page/batchDelete")
    public String batchDelete(@RequestParam(value = "ids", required = false) String[] ids,
                              RedirectAttributes ra) {

        if (ids == null || ids.length == 0) {
            ra.addFlashAttribute("errorMsg", "请先选择要删除的学生！");
            return "redirect:/student/page/list";
        }

        List<String> idList = Arrays.asList(ids);
        studentService.batchDelete(idList);

        ra.addFlashAttribute("msg", "批量删除成功！");
        return "redirect:/student/page/list";
    }

    @GetMapping("/page/import")
    public String importPage() {
        return "student-import";
    }

    @PostMapping("/page/import")
    public String importStudentFile(@RequestParam("file") MultipartFile file,
                                    RedirectAttributes ra) {

        if (file == null || file.isEmpty()) {
            ra.addFlashAttribute("error", "请选择要上传的 Excel 文件！");
            return "redirect:/student/page/import";
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            ra.addFlashAttribute("error", "文件名不能为空！");
            return "redirect:/student/page/import";
        }

        String lowerName = originalFilename.toLowerCase();

        if (!lowerName.endsWith(".xlsx") && !lowerName.endsWith(".xls")) {
            ra.addFlashAttribute("error", "无效文件！请上传 .xlsx 或 .xls 格式的 Excel 文件");
            return "redirect:/student/page/import";
        }

        try {
            String cleanFilename = StringUtils.cleanPath(originalFilename);

            if (cleanFilename.contains("..")) {
                ra.addFlashAttribute("error", "文件名不合法！");
                return "redirect:/student/page/import";
            }

            File dir = new File(uploadPath);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            File dest = new File(dir, cleanFilename);
            file.transferTo(dest);

            ImportResult result = studentService.importStudentsFromExcel(dest);

            String msg = "学生导入成功：" + result.getSuccessCount() + " 人。失败：" + result.getFailCount() + " 人。";

            if (result.getFailCount() > 0
                    && result.getFailReports() != null
                    && !result.getFailReports().isEmpty()) {

                int end = Math.min(3, result.getFailReports().size());

                msg += " 失败原因示例："
                        + String.join(" | ", result.getFailReports().subList(0, end));
            }

            ra.addFlashAttribute("success", msg);

        } catch (Exception e) {
            ra.addFlashAttribute("error", "服务器解析异常：" + e.getMessage());
        }

        return "redirect:/student/page/import";
    }

    private String validateStudent(Student student) {

        if (student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            return "学号不能为空";
        }

        if (student.getName() == null || student.getName().trim().isEmpty()) {
            return "姓名不能为空";
        }

        if (student.getGender() == null || student.getGender().trim().isEmpty()) {
            return "性别不能为空";
        }

        if (student.getClassName() == null || student.getClassName().trim().isEmpty()) {
            return "班级不能为空";
        }

        if (student.getAge() == null || student.getAge() < 1 || student.getAge() > 100) {
            return "年龄必须在 1 到 100 之间";
        }

        if (student.getBirthDate() == null) {
            return "出生日期不能为空";
        }

        if (student.getPhone() == null || student.getPhone().trim().isEmpty()) {
            return "联系方式不能为空";
        }

        if (!student.getPhone().matches("^1[3-9]\\d{9}$")) {
            return "联系方式格式不正确，请输入 11 位手机号";
        }

        return null;
    }
}