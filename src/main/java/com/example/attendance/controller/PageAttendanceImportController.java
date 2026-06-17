package com.example.attendance.controller;

import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.config.AuthorizationInterceptor;
import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.User;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.*;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/attendance/page")
public class PageAttendanceImportController {

    @Resource
    private AttendanceRepository attendanceRepository;

    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 进入考勤导入页面
     */
    @GetMapping("/import")
    public String importPage(HttpSession session) {
        User loginUser = getLoginUser(session);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!isManager(loginUser)) {
            return "redirect:/dashboard?permissionDenied=true";
        }

        return "attendance-import";
    }

    /**
     * 上传并导入考勤 Excel
     */
    @PostMapping("/import")
    public String importAttendance(MultipartFile file,
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes ra) {

        User loginUser = getLoginUser(session);

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!isManager(loginUser)) {
            return "redirect:/dashboard?permissionDenied=true";
        }

        if (file == null || file.isEmpty()) {
            model.addAttribute("error", "请选择要导入的 Excel 文件");
            return "attendance-import";
        }

        String filename = file.getOriginalFilename();

        if (filename == null ||
                (!filename.toLowerCase().endsWith(".xlsx")
                        && !filename.toLowerCase().endsWith(".xls"))) {
            model.addAttribute("error", "文件格式错误，请上传 .xlsx 或 .xls 文件");
            return "attendance-import";
        }

        int successCount = 0;
        int failCount = 0;
        List<String> errors = new ArrayList<>();

        DataFormatter formatter = new DataFormatter();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            if (sheet == null || sheet.getPhysicalNumberOfRows() <= 1) {
                model.addAttribute("error", "Excel 内容为空");
                return "attendance-import";
            }

            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                model.addAttribute("error", "Excel 第一行必须是表头");
                return "attendance-import";
            }

            Map<String, Integer> headerMap = buildHeaderMap(headerRow, formatter);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null || isEmptyRow(row, formatter)) {
                    continue;
                }

                int rowNum = i + 1;

                try {
                    Attendance attendance = parseAttendance(row, headerMap, formatter, rowNum);
                    attendanceRepository.save(attendance);
                    successCount++;
                } catch (Exception e) {
                    failCount++;

                    if (errors.size() < 10) {
                        errors.add("第 " + rowNum + " 行：" + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            model.addAttribute("error", "导入失败：" + e.getMessage());
            return "attendance-import";
        }

        if (successCount == 0) {
            model.addAttribute("error", "导入失败，成功 0 条；失败 " + failCount + " 条：" + String.join("；", errors));
            return "attendance-import";
        }

        ra.addFlashAttribute("msg", "考勤数据导入完成，成功 " + successCount + " 条，失败 " + failCount + " 条");

        if (!errors.isEmpty()) {
            ra.addFlashAttribute("errorMsg", "部分数据导入失败：" + String.join("；", errors));
        }

        return "redirect:/attendance/page/list";
    }

    private Attendance parseAttendance(Row row,
                                       Map<String, Integer> headerMap,
                                       DataFormatter formatter,
                                       int rowNum) {

        String userIdText = getCellText(row, headerMap, formatter,
                "用户ID", "学生用户ID", "user_id", "userId");

        String username = getCellText(row, headerMap, formatter,
                "用户名", "账号", "username");

        String realName = getCellText(row, headerMap, formatter,
                "学生姓名", "姓名", "realName", "real_name");

        String courseIdText = getCellText(row, headerMap, formatter,
                "课程ID", "course_id", "courseId");

        String courseName = getCellText(row, headerMap, formatter,
                "课程", "课程名称", "courseName", "course_name");

        String ip = getCellText(row, headerMap, formatter,
                "打卡IP", "IP", "ip");

        String status = getCellText(row, headerMap, formatter,
                "状态", "考勤状态", "status");

        if (username == null || username.isBlank()) {
            throw new RuntimeException("用户名不能为空");
        }

        Long userId = parseLong(userIdText);

        if (userId == null) {
            userId = findUserIdByUsername(username);

            if (userId == null) {
                throw new RuntimeException("找不到用户名对应的用户：" + username);
            }
        }

        if (realName == null || realName.isBlank()) {
            realName = findRealNameByUsername(username);
        }

        Long courseId = parseLong(courseIdText);

        if (courseId == null) {
            if (courseName == null || courseName.isBlank()) {
                throw new RuntimeException("课程名称不能为空");
            }

            courseId = findCourseIdByCourseName(courseName);

            if (courseId == null) {
                throw new RuntimeException("找不到课程：" + courseName);
            }
        }

        if (courseName == null || courseName.isBlank()) {
            courseName = findCourseNameById(courseId);
        }

        Timestamp checkInTime = parseTimestamp(row, headerMap, formatter, rowNum,
                "打卡时间", "签到时间", "checkInTime", "check_in_time");

        if (checkInTime == null) {
            throw new RuntimeException("打卡时间不能为空");
        }

        Timestamp checkOutTime = parseTimestamp(row, headerMap, formatter, rowNum,
                "签退时间", "checkOutTime", "check_out_time");

        Attendance attendance = new Attendance();

        attendance.setUserId(userId);
        attendance.setUsername(username);
        attendance.setRealName(realName);

        attendance.setCourseId(courseId);
        attendance.setCourseName(courseName);

        attendance.setCheckInTime(checkInTime);
        attendance.setCheckOutTime(checkOutTime);

        attendance.setIp(ip == null || ip.isBlank() ? "Excel导入" : ip);
        attendance.setStatus(status == null || status.isBlank() ? "已打卡" : status);

        return attendance;
    }

    private Map<String, Integer> buildHeaderMap(Row headerRow, DataFormatter formatter) {
        Map<String, Integer> map = new HashMap<>();

        for (Cell cell : headerRow) {
            String text = formatter.formatCellValue(cell);

            if (text != null && !text.trim().isEmpty()) {
                map.put(normalize(text), cell.getColumnIndex());
            }
        }

        return map;
    }

    private String getCellText(Row row,
                               Map<String, Integer> headerMap,
                               DataFormatter formatter,
                               String... names) {

        for (String name : names) {
            Integer index = headerMap.get(normalize(name));

            if (index != null) {
                Cell cell = row.getCell(index);

                if (cell == null) {
                    return "";
                }

                String value = formatter.formatCellValue(cell);

                return value == null ? "" : value.trim();
            }
        }

        return "";
    }

    private Timestamp parseTimestamp(Row row,
                                     Map<String, Integer> headerMap,
                                     DataFormatter formatter,
                                     int rowNum,
                                     String... names) {

        Cell cell = null;

        for (String name : names) {
            Integer index = headerMap.get(normalize(name));

            if (index != null) {
                cell = row.getCell(index);
                break;
            }
        }

        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return Timestamp.valueOf(cell.getLocalDateTimeCellValue());
        }

        String text = formatter.formatCellValue(cell);

        if (text == null) {
            return null;
        }

        text = text.trim();

        if (text.isEmpty()
                || "未签退".equals(text)
                || "-".equals(text)
                || "无".equals(text)) {
            return null;
        }

        text = text.replace("/", "-").replace("T", " ");

        List<DateTimeFormatter> formatters = Arrays.asList(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ofPattern("yyyy-M-d H:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-M-d H:mm")
        );

        for (DateTimeFormatter dateTimeFormatter : formatters) {
            try {
                LocalDateTime localDateTime = LocalDateTime.parse(text, dateTimeFormatter);
                return Timestamp.valueOf(localDateTime);
            } catch (Exception ignored) {
            }
        }

        throw new RuntimeException("时间格式错误：" + text + "，建议格式：yyyy-MM-dd HH:mm:ss");
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            value = value.trim();

            if (value.contains(".")) {
                value = value.substring(0, value.indexOf("."));
            }

            return Long.parseLong(value);
        } catch (Exception e) {
            return null;
        }
    }

    private Long findUserIdByUsername(String username) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id FROM `user` WHERE username = ? LIMIT 1",
                    Long.class,
                    username
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private String findRealNameByUsername(String username) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT real_name FROM `user` WHERE username = ? LIMIT 1",
                    String.class,
                    username
            );
        } catch (EmptyResultDataAccessException e) {
            return "";
        }
    }

    private Long findCourseIdByCourseName(String courseName) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id FROM course WHERE course_name = ? LIMIT 1",
                    Long.class,
                    courseName
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private String findCourseNameById(Long courseId) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT course_name FROM course WHERE id = ? LIMIT 1",
                    String.class,
                    courseId
            );
        } catch (EmptyResultDataAccessException e) {
            return "";
        }
    }

    private boolean isEmptyRow(Row row, DataFormatter formatter) {
        for (Cell cell : row) {
            String value = formatter.formatCellValue(cell);

            if (value != null && !value.trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value.trim()
                .toLowerCase()
                .replace(" ", "")
                .replace("_", "")
                .replace("-", "");
    }

    private User getLoginUser(HttpSession session) {
        return (User) session.getAttribute(AuthorizationInterceptor.SESSION_USER);
    }

    private boolean isManager(User user) {
        return user != null
                && ("TEACHER".equalsIgnoreCase(user.getRole())
                || "ADMIN".equalsIgnoreCase(user.getRole()));
    }
}