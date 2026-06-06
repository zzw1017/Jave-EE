package com.example.attendance.service.impl;

import com.example.attendance.dto.AttendanceQueryDTO;
import com.example.attendance.dto.StatisticsDTO;
import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Course;
import com.example.attendance.Repository.AttendanceRepository;
import com.example.attendance.Repository.CourseRepository;
import com.example.attendance.service.AttendanceService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.example.attendance.result.ImportResult;
import org.apache.poi.ss.usermodel.*;
import java.io.FileInputStream;
import java.time.format.DateTimeFormatter;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Resource
    private AttendanceRepository attendanceRepository;
    @Resource
    private CourseRepository courseRepository;

    @Override
    public Attendance create(Attendance attendance) {
        return null;
    }

    @Override
    public List<Attendance> getAll() {
        return List.of();
    }

    @Override
    public List<Attendance> getByStudentId(Long studentId) {
        return List.of();
    }

    @Override
    public List<Attendance> getByCourseId(Long courseId) {
        return List.of();
    }

    @Override
    public Page<Attendance> getPage(int page, int size, String soryBy, String direction) {
        return null;
    }

    @Override
    public Page<Attendance> queryByCondition(AttendanceQueryDTO QueryDTO) {
        return null;
    }

    @Override
    public String checkIn(Attendance attendance, String ip) {
        Optional<Course> courseOpt = courseRepository.findById(attendance.getCourseId());
        if(courseOpt.isEmpty()){
            return "失败：该课程不存在";
        }
        Course course = courseOpt.get();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime courseStart = course.getStartTime().toLocalDateTime();

        LocalDateTime validBegin = courseStart.minusMinutes(15);
        LocalDateTime validEnd = courseStart.plusMinutes(30);

        if(now.isBefore(validBegin)){
            return "失败：未到签到时间，课程开始前15分钟才可签到";
        }
        if(now.isAfter(validEnd)){
            return "失败：已超出签到时间，课程开始30分钟后禁止签到";
        }

        Timestamp nowTime = Timestamp.valueOf(now);
        attendance.setCheckInTime(nowTime);
        attendance.setCreateTime(nowTime);
        attendance.setIp(ip);

        if(now.isAfter(courseStart)){
            attendance.setStatus("迟到");
        }else{
            attendance.setStatus("正常");
        }
        attendanceRepository.save(attendance);
        return "签到成功，状态："+attendance.getStatus();
    }

    @Override
    public String checkOut(Long id) {
        Optional<Attendance> attOpt = attendanceRepository.findById(id);
        if(attOpt.isEmpty()) return "失败：无此签到记录";
        Attendance att = attOpt.get();
        if(att.getCheckInTime() == null) return "失败：未签到无法签退";

        Optional<Course> courseOpt = courseRepository.findById(att.getCourseId());
        if(courseOpt.isEmpty()) return "失败：课程信息不存在";
        Course course = courseOpt.get();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime courseEnd = course.getEndTime().toLocalDateTime();

        if(now.isBefore(courseEnd)){
            att.setStatus("早退");
        }else{
            att.setStatus("正常离校");
        }
        attendanceRepository.save(att);
        return "签退成功，状态："+att.getStatus();
    }

    @Override
    public List<Attendance> filterAttendance(Long courseId, String timeType) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime;
        switch (timeType){
            case "today": startTime = now.toLocalDate().atStartOfDay();break;
            case "week": startTime = now.minusWeeks(1);break;
            case "month": startTime = now.minusMonths(1);break;
            default: startTime = now.minusYears(10);
        }
        Timestamp start = Timestamp.valueOf(startTime);
        Timestamp end = Timestamp.valueOf(now);
        if(courseId != null){
            return attendanceRepository.findByCourseAndTime(courseId,start,end);
        }else{
            return attendanceRepository.findByTimeRange(start,end);
        }
    }
    public AttendanceServiceImpl(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public ImportResult importFromExcel(String filePath) throws Exception {
        ImportResult result = new ImportResult();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            // 跳过表头，从第2行开始
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    // 读取 Excel 列
                    String studentId = getCellValue(row.getCell(0));
                    String courseId = getCellValue(row.getCell(1));
                    String checkInTime = getCellValue(row.getCell(2));
                    String seatRow = getCellValue(row.getCell(3));
                    String seatCol = getCellValue(row.getCell(4));
                    String status = getCellValue(row.getCell(5));

                    // 非空校验
                    if (studentId.isEmpty() || courseId.isEmpty()) {
                        result.incrementFail();
                        continue;
                    }

                    // 构建考勤对象
                    Attendance attendance = new Attendance();
                    attendance.setStudentId(Long.parseLong(studentId));
                    attendance.setCourseId(Long.parseLong(courseId));
                    attendance.setCheckInTime(Timestamp.valueOf(
                            LocalDateTime.parse(checkInTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    ));
                    attendance.setSeatRow(Integer.parseInt(seatRow));
                    attendance.setSeatCol(Integer.parseInt(seatCol));
                    attendance.setStatus(status);
                    attendance.setIp("0.0.0.0");

                    // 保存
                    attendanceRepository.save(attendance);
                    result.incrementSuccess();

                } catch (Exception e) {
                    result.incrementFail();
                }
            }
        }
        return result;
    }

    @Override
    public long getTotalAttendance(Long studentId) {
        return attendanceRepository.countByStudentId(studentId);
    }

    @Override
    public long getCountByStatus(Long studentId, String status) {
        return attendanceRepository.countByStudentIdAndStatus(studentId, status);
    }

    // 工具方法：读取单元格
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    @Override
    public StatisticsDTO getStudentStatistics(Long studentId) {
        long total = attendanceRepository.countByStudentId(studentId);
        long normal = attendanceRepository.countByStudentIdAndStatus(studentId, "正常");
        long late = attendanceRepository.countByStudentIdAndStatus(studentId, "迟到");
        long absent = attendanceRepository.countByStudentIdAndStatus(studentId, "缺勤");
        double rate = 0.0;
        if (total > 0) {
            rate = (double) normal / total * 100;
        }
        return new StatisticsDTO(total, normal, late, absent, rate);
    }

    @Override
    public long getTotalAttendanceCount() {
        return attendanceRepository.count();
    }
}
