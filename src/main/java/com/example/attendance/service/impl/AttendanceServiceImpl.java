package com.example.attendance.service.impl;

import com.example.attendance.dto.AttendanceQueryDTO;
import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Course;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.entity.User;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.CourseService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import com.example.attendance.result.ImportResult;


@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Resource
    private AttendanceRepository attendanceRepository;

    @Resource
    private CourseService courseService;

    @Override
    public String checkIn(Long courseId, User loginUser, String ip) {

        if (courseId == null) {
            return "请选择课程";
        }

        if (loginUser == null) {
            return "请先登录";
        }

        Course course = findCourseById(courseId);

        if (course == null) {
            return "课程不存在";
        }

        Timestamp todayStart = Timestamp.valueOf(LocalDate.now().atStartOfDay());
        Timestamp tomorrowStart = Timestamp.valueOf(LocalDate.now().plusDays(1).atStartOfDay());

        boolean alreadyChecked = attendanceRepository.existsByUserIdAndCourseIdAndCheckInTimeBetween(
                loginUser.getId(),
                courseId,
                todayStart,
                tomorrowStart
        );

        if (alreadyChecked) {
            return "今天该课程已经打过卡，不能重复打卡";
        }

        Attendance attendance = new Attendance();

        attendance.setUserId(loginUser.getId());
        attendance.setUsername(loginUser.getUsername());
        attendance.setRealName(loginUser.getRealName());

        attendance.setCourseId(course.getId());
        attendance.setCourseName(course.getCourseName());

        attendance.setCheckInTime(new Timestamp(System.currentTimeMillis()));
        attendance.setIp(ip);
        attendance.setStatus("已打卡");

        attendanceRepository.save(attendance);

        return "打卡成功，课程：" + course.getCourseName();
    }

    @Override
    public String checkOut(Long id, User loginUser) {

        if (id == null) {
            return "考勤记录不存在";
        }

        Attendance attendance = attendanceRepository.findById(id).orElse(null);

        if (attendance == null) {
            return "考勤记录不存在";
        }

        if (loginUser == null) {
            return "请先登录";
        }

        if (!attendance.getUserId().equals(loginUser.getId())) {
            return "无权操作该考勤记录";
        }

        if (attendance.getCheckOutTime() != null) {
            return "已经签退，不能重复签退";
        }

        attendance.setCheckOutTime(new Timestamp(System.currentTimeMillis()));
        attendance.setStatus("已签退");

        attendanceRepository.save(attendance);

        return "签退成功";
    }

    @Override
    public Object getStudentStatistics(Long studentId) {
        return null;
    }

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
    public List<Attendance> filterAttendance(Long courseId, String timeType) {

        Timestamp[] range = getTimeRange(timeType);

        boolean hasCourse = courseId != null;
        boolean hasTime = range != null;

        if (hasCourse && hasTime) {
            return attendanceRepository.findByCourseIdAndCheckInTimeBetweenOrderByCheckInTimeDesc(
                    courseId,
                    range[0],
                    range[1]
            );
        }

        if (hasCourse) {
            return attendanceRepository.findByCourseIdOrderByCheckInTimeDesc(courseId);
        }

        if (hasTime) {
            return attendanceRepository.findByCheckInTimeBetweenOrderByCheckInTimeDesc(
                    range[0],
                    range[1]
            );
        }

        return attendanceRepository.findAllByOrderByCheckInTimeDesc();
    }

    @Override
    public ImportResult importFromExcel(String filePath) throws Exception {
        return null;
    }

    private Course findCourseById(Long courseId) {
        List<Course> courses = courseService.findAll();

        for (Course course : courses) {
            if (course.getId().equals(courseId)) {
                return course;
            }
        }

        return null;
    }

    private Timestamp[] getTimeRange(String timeType) {

        if (timeType == null || "all".equalsIgnoreCase(timeType)) {
            return null;
        }

        LocalDateTime start;
        LocalDateTime end;

        LocalDate today = LocalDate.now();

        switch (timeType) {
            case "today":
                start = today.atStartOfDay();
                end = today.plusDays(1).atStartOfDay();
                break;

            case "week":
                LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                start = monday.atStartOfDay();
                end = monday.plusDays(7).atStartOfDay();
                break;

            case "month":
                LocalDate firstDay = today.withDayOfMonth(1);
                start = firstDay.atStartOfDay();
                end = firstDay.plusMonths(1).atStartOfDay();
                break;

            default:
                return null;
        }

        return new Timestamp[]{
                Timestamp.valueOf(start),
                Timestamp.valueOf(end)
        };
    }
}
