package com.example.attendance.controller;

import com.example.attendance.config.AuthorizationInterceptor;
import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Course;
import com.example.attendance.entity.User;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.CourseService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/attendance/page")
public class PageAttendanceController {

    @Resource
    private AttendanceService attendanceService;

    @Resource
    private CourseService courseService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/checkin")
    public String doCheckin(@RequestParam("courseId") Long courseId,
                            HttpSession session,
                            HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {

        User loginUser = (User) session.getAttribute(AuthorizationInterceptor.SESSION_USER);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!"STUDENT".equalsIgnoreCase(loginUser.getRole())) {
            return "redirect:/dashboard?permissionDenied=true";
        }

        try {
            String ip = getClientIp(request);

            String msg = attendanceService.checkIn(courseId, loginUser, ip);

            redirectAttributes.addFlashAttribute("successMsg", msg);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }

        return "redirect:/attendance/page/checkin";
    }

    @GetMapping("/checkin")
    public String toCheckin(Model model, HttpSession session) {

        User loginUser = (User) session.getAttribute(AuthorizationInterceptor.SESSION_USER);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!"STUDENT".equalsIgnoreCase(loginUser.getRole())) {
            return "redirect:/dashboard?permissionDenied=true";
        }

        List<Course> courseList = courseService.findAll();

        model.addAttribute("courseList", courseList);
        model.addAttribute("loginUser", loginUser);

        return "checkin";
    }

    @GetMapping("/list")
    public String list(@RequestParam(required = false) Long courseId,
                       @RequestParam(defaultValue = "all") String timeType,
                       Model model,
                       HttpSession session) {

        User loginUser = (User) session.getAttribute(AuthorizationInterceptor.SESSION_USER);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!isManager(loginUser)) {
            return "redirect:/dashboard?permissionDenied=true";
        }

        List<Attendance> list = attendanceService.filterAttendance(courseId, timeType);
        List<Course> courseList = courseService.findAll();

        model.addAttribute("attList", list);
        model.addAttribute("courseList", courseList);
        model.addAttribute("courseId", courseId);
        model.addAttribute("timeType", timeType);

        return "attendance-list";
    }

    @PostMapping("/checkout")
    public String doCheckout(@RequestParam(value = "courseId", required = false) Long courseId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        User loginUser = (User) session.getAttribute(AuthorizationInterceptor.SESSION_USER);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!"STUDENT".equalsIgnoreCase(loginUser.getRole())) {
            return "redirect:/dashboard?permissionDenied=true";
        }

        if (courseId == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "请先选择课程");
            return "redirect:/attendance/page/checkin";
        }

        try {
            String msg = attendanceService.checkOut(courseId, loginUser);
            redirectAttributes.addFlashAttribute("successMsg", msg);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }

        return "redirect:/attendance/page/checkin";
    }

    @GetMapping("/export")
    public void exportExcel(@RequestParam(required = false) Long courseId,
                            @RequestParam(defaultValue = "all") String timeType,
                            HttpSession session,
                            HttpServletResponse response) throws IOException {

        User loginUser = (User) session.getAttribute(AuthorizationInterceptor.SESSION_USER);

        if (loginUser == null) {
            response.sendRedirect("/login");
            return;
        }

        if (!isManager(loginUser)) {
            response.sendRedirect("/dashboard?permissionDenied=true");
            return;
        }

        List<Attendance> list = attendanceService.filterAttendance(courseId, timeType);

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("考勤记录");

            String[] headers = {
                    "序号",
                    "学生姓名",
                    "用户名",
                    "课程",
                    "打卡时间",
                    "签退时间",
                    "打卡IP",
                    "状态"
            };

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < list.size(); i++) {
                Attendance a = list.get(i);
                Row row = sheet.createRow(i + 1);

                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(nullToEmpty(a.getRealName()));
                row.createCell(2).setCellValue(nullToEmpty(a.getUsername()));
                row.createCell(3).setCellValue(nullToEmpty(a.getCourseName()));
                row.createCell(4).setCellValue(formatTime(a.getCheckInTime()));
                row.createCell(5).setCellValue(formatTime(a.getCheckOutTime()));
                row.createCell(6).setCellValue(nullToEmpty(a.getIp()));
                row.createCell(7).setCellValue(nullToEmpty(a.getStatus()));
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i), 18 * 256));
            }

            String fileName = "考勤记录.xlsx";
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment; filename*=UTF-8''" + encodedFileName);

            workbook.write(response.getOutputStream());
        }
    }

    private boolean isManager(User user) {
        return user != null
                && ("ADMIN".equalsIgnoreCase(user.getRole())
                || "TEACHER".equalsIgnoreCase(user.getRole()));
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String formatTime(Timestamp timestamp) {
        if (timestamp == null) {
            return "未签退";
        }
        return timestamp.toLocalDateTime().format(DATE_TIME_FORMATTER);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("X-Real-IP");

        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        return request.getRemoteAddr();
    }
}