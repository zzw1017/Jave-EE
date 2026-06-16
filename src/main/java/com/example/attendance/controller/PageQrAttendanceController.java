package com.example.attendance.controller;

import com.example.attendance.repository.AttendanceQrCodeRepository;
import com.example.attendance.config.AuthorizationInterceptor;
import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.AttendanceQrCode;
import com.example.attendance.entity.Course;
import com.example.attendance.entity.User;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.CourseService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/attendance/page")
public class PageQrAttendanceController {

    @Resource
    private AttendanceQrCodeRepository attendanceQrCodeRepository;

    @Resource
    private CourseService courseService;

    @Resource
    private AttendanceService attendanceService;

    @Value("${app.base-url:}")
    private String appBaseUrl;

    @GetMapping("/qrcode")
    public String qrCodePage(Model model, HttpSession session) {

        User loginUser = getLoginUser(session);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!isManager(loginUser)) {
            return "redirect:/dashboard?permissionDenied=true";
        }

        List<Course> courseList = courseService.findAll();

        model.addAttribute("courseList", courseList);

        return "attendance-qrcode";
    }

    @PostMapping("/qrcode/generate")
    public String generateQrCode(@RequestParam Long courseId,
                                 HttpServletRequest request,
                                 HttpSession session,
                                 Model model) {

        User loginUser = getLoginUser(session);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!isManager(loginUser)) {
            return "redirect:/dashboard?permissionDenied=true";
        }

        Course course = courseService.findById(courseId);

        if (course == null) {
            model.addAttribute("errorMsg", "课程不存在");
            model.addAttribute("courseList", courseService.findAll());
            return "attendance-qrcode";
        }

        try {
            String token = UUID.randomUUID().toString().replace("-", "");

            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            Timestamp expireTime = Timestamp.valueOf(LocalDateTime.now().plusMinutes(10));

            AttendanceQrCode qrCode = new AttendanceQrCode();
            qrCode.setToken(token);
            qrCode.setCourseId(course.getId());
            qrCode.setCourseName(course.getCourseName());
            qrCode.setTeacherId(loginUser.getId());
            qrCode.setTeacherName(loginUser.getRealName());
            qrCode.setCreateTime(now);
            qrCode.setExpireTime(expireTime);
            qrCode.setStatus("ACTIVE");

            attendanceQrCodeRepository.save(qrCode);

            String scanUrl = getQrBaseUrl(request)
                    + "/attendance/page/qr-checkin?token="
                    + token;

            String qrCodeImage = generateQrCodeBase64(scanUrl);

            model.addAttribute("courseList", courseService.findAll());
            model.addAttribute("qrCodeImage", qrCodeImage);
            model.addAttribute("scanUrl", scanUrl);
            model.addAttribute("courseName", course.getCourseName());
            model.addAttribute("expireTime", expireTime);
            model.addAttribute("successMsg", "二维码生成成功，有效期 10 分钟");

            return "attendance-qrcode";

        } catch (Exception e) {
            model.addAttribute("errorMsg", "二维码生成失败：" + e.getMessage());
            model.addAttribute("courseList", courseService.findAll());
            return "attendance-qrcode";
        }
    }

    @GetMapping("/qr-checkin")
    public String qrCheckin(@RequestParam String token,
                            HttpServletRequest request,
                            HttpSession session,
                            Model model) {

        User loginUser = getLoginUser(session);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!isStudent(loginUser)) {
            model.addAttribute("msg", "只有学生账号可以扫码打卡");
            return "result";
        }

        AttendanceQrCode qrCode = attendanceQrCodeRepository.findByToken(token).orElse(null);

        if (qrCode == null) {
            model.addAttribute("msg", "二维码无效");
            return "result";
        }

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        if (!"ACTIVE".equalsIgnoreCase(qrCode.getStatus())) {
            model.addAttribute("msg", "二维码已失效");
            return "result";
        }

        if (qrCode.getExpireTime() != null && now.after(qrCode.getExpireTime())) {
            qrCode.setStatus("EXPIRED");
            attendanceQrCodeRepository.save(qrCode);

            model.addAttribute("msg", "二维码已过期，请联系教师重新生成");
            return "result";
        }

        try {
            Attendance attendance = new Attendance();

            attendance.setUserId(loginUser.getId());
            attendance.setUsername(loginUser.getUsername());
            attendance.setRealName(loginUser.getRealName());

            attendance.setCourseId(qrCode.getCourseId());
            attendance.setCourseName(qrCode.getCourseName());

            attendance.setIp(getClientIp(request));
            attendance.setStatus("已打卡");

            attendanceService.create(attendance);

            model.addAttribute("msg", "扫码打卡成功，课程：" + qrCode.getCourseName());

            return "result";

        } catch (Exception e) {
            model.addAttribute("msg", "扫码打卡失败：" + e.getMessage());
            return "result";
        }
    }

    private User getLoginUser(HttpSession session) {
        return (User) session.getAttribute(AuthorizationInterceptor.SESSION_USER);
    }

    private boolean isManager(User user) {
        return user != null
                && ("TEACHER".equalsIgnoreCase(user.getRole())
                || "ADMIN".equalsIgnoreCase(user.getRole()));
    }

    private boolean isStudent(User user) {
        return user != null && "STUDENT".equalsIgnoreCase(user.getRole());
    }

    private String getQrBaseUrl(HttpServletRequest request) {
        if (appBaseUrl != null && !appBaseUrl.trim().isEmpty()) {
            return appBaseUrl.trim();
        }

        return buildBaseUrl(request);
    }

    private String buildBaseUrl(HttpServletRequest request) {

        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int port = request.getServerPort();
        String contextPath = request.getContextPath();

        boolean isDefaultPort =
                ("http".equalsIgnoreCase(scheme) && port == 80)
                        || ("https".equalsIgnoreCase(scheme) && port == 443);

        if (isDefaultPort) {
            return scheme + "://" + serverName + contextPath;
        }

        return scheme + "://" + serverName + ":" + port + contextPath;
    }

    private String generateQrCodeBase64(String content) throws Exception {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        BitMatrix bitMatrix = qrCodeWriter.encode(
                content,
                BarcodeFormat.QR_CODE,
                260,
                260
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        MatrixToImageWriter.writeToStream(
                bitMatrix,
                "PNG",
                outputStream
        );

        return "data:image/png;base64,"
                + Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    private String getClientIp(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");

        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0];
        }

        ip = request.getHeader("X-Real-IP");

        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        return request.getRemoteAddr();
    }
}