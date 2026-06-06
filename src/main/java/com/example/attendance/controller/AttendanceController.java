package com.example.attendance.controller;

import com.example.attendance.dto.StatisticsDTO;
import com.example.attendance.result.ImportResult;
import com.example.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
@Controller
public class AttendanceController {

    @Value("${file.upload.path}")
    private String uploadPath;

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/attendance/import")
    public String toImportPage() {
        return "attendance-import";
    }

    @PostMapping("/attendance/import")
    public String importExcel(@RequestParam("file") MultipartFile file,
                              RedirectAttributes redirectAttributes) {
        // 1. 判断文件是否为空

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "请选择要上传的 Excel 文件");
            return "redirect:/attendance/import";
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            redirectAttributes.addFlashAttribute("errorMsg", "仅支持 .xlsx / .xls 格式文件");
            return "redirect:/attendance/import";
        }

        try {
            // 3. 创建上传目录（不存在则新建）
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            // 拼接完整文件路径
            String fullPath = uploadPath + fileName;
            // 保存文件到本地
            file.transferTo(new File(fullPath));

            // 4. 调用 Service 解析 Excel 并导入数据库
            ImportResult result = attendanceService.importFromExcel(fullPath);
            redirectAttributes.addFlashAttribute("successMsg",
                    "导入完成！成功：" + result.getSuccessCount() + " 条，失败：" + result.getFailCount() + " 条");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "导入失败：" + e.getMessage());
        }

        return "redirect:/attendance/import";
    }
}
