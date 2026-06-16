package com.example.attendance.controller;

import com.example.attendance.config.AuthorizationInterceptor;
import com.example.attendance.dto.AttendanceQueryDTO;
import com.example.attendance.dto.CheckinOptionDTO;
import com.example.attendance.dto.StatisticsDTO;
import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Course;
import com.example.attendance.entity.CourseSchedule;
import com.example.attendance.entity.User;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.CourseRepository;
import com.example.attendance.repository.CourseScheduleRepository;
import com.example.attendance.result.Result;
import com.example.attendance.service.AttendanceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseScheduleRepository courseScheduleRepository;

    @GetMapping("/checkin/options")
    public Result<List<CheckinOptionDTO>> getTodayCheckinOptions(HttpSession session) {
        User user = getLoginUser(session);

        if (user == null) {
            return fail("请先登录");
        }

        if (!isStudent(user)) {
            return fail("只有学生账号可以进行考勤打卡");
        }

        int weekDay = LocalDate.now().getDayOfWeek().getValue();

        List<CourseSchedule> schedules =
                courseScheduleRepository.findByWeekDayOrderByStartTimeAsc(weekDay);

        List<CheckinOptionDTO> resultList = new ArrayList<>();

        for (CourseSchedule schedule : schedules) {
            Optional<Course> courseOptional = courseRepository.findById(schedule.getCourseId());

            if (courseOptional.isEmpty()) {
                continue;
            }

            Course course = courseOptional.get();

            CheckinOptionDTO dto = new CheckinOptionDTO();
            dto.setCourseId(course.getId());
            dto.setScheduleId(schedule.getId());
            dto.setCourseName(course.getCourseName());
            dto.setWeekDay(schedule.getWeekDay());
            dto.setStartTime(schedule.getStartTime().toString());
            dto.setEndTime(schedule.getEndTime().toString());
            dto.setLocation(schedule.getLocation());

            String label = course.getCourseName()
                    + "｜"
                    + weekText(schedule.getWeekDay())
                    + " "
                    + schedule.getStartTime()
                    + "-"
                    + schedule.getEndTime();

            if (schedule.getLocation() != null && !schedule.getLocation().isBlank()) {
                label += "｜" + schedule.getLocation();
            }

            dto.setLabel(label);

            resultList.add(dto);
        }

        return ok(resultList);
    }

    @PostMapping("/create")
    public Result<Attendance> create(@RequestBody Attendance attendance,
                                     HttpSession session) {
        return doCheckIn(attendance, session);
    }

    @PostMapping("/checkin")
    public Result<Attendance> checkin(@RequestBody Attendance attendance,
                                      HttpSession session) {
        return doCheckIn(attendance, session);
    }

    private Result<Attendance> doCheckIn(Attendance attendance,
                                         HttpSession session) {
        try {
            User user = getLoginUser(session);

            if (user == null) {
                return fail("请先登录");
            }

            if (!isStudent(user)) {
                return fail("只有学生账号可以进行考勤打卡");
            }

            if (attendance.getCourseId() == null) {
                return fail("请选择课程");
            }

            if (attendance.getCourseScheduleId() == null) {
                return fail("请选择具体上课时间");
            }

            CourseSchedule schedule = courseScheduleRepository
                    .findById(attendance.getCourseScheduleId())
                    .orElse(null);

            if (schedule == null) {
                return fail("上课时间不存在");
            }

            if (!schedule.getCourseId().equals(attendance.getCourseId())) {
                return fail("课程和上课时间不匹配");
            }

            Course course = courseRepository.findById(attendance.getCourseId())
                    .orElse(null);

            if (course == null) {
                return fail("课程不存在");
            }

            LocalDate today = LocalDate.now();
            LocalTime nowTime = LocalTime.now();

            int todayWeekDay = today.getDayOfWeek().getValue();

            if (!schedule.getWeekDay().equals(todayWeekDay)) {
                return fail("今天不是该课程的上课日");
            }

            LocalTime allowStart = schedule.getStartTime().minusMinutes(30);
            LocalTime allowEnd = schedule.getEndTime();

            if (nowTime.isBefore(allowStart) || nowTime.isAfter(allowEnd)) {
                return fail("当前不在该课程打卡时间范围内");
            }

            Timestamp dayStart = Timestamp.valueOf(today.atStartOfDay());
            Timestamp dayEnd = Timestamp.valueOf(today.atTime(23, 59, 59));

            Optional<Attendance> existing =
                    attendanceRepository.findFirstByUserIdAndCourseIdAndCourseScheduleIdAndCheckInTimeBetween(
                            user.getId(),
                            attendance.getCourseId(),
                            attendance.getCourseScheduleId(),
                            dayStart,
                            dayEnd
                    );

            if (existing.isPresent()) {
                return fail("该课程今天已经打过卡了");
            }

            Attendance record = new Attendance();

            record.setUserId(user.getId());
            record.setCourseId(attendance.getCourseId());
            record.setCourseScheduleId(attendance.getCourseScheduleId());

            // 必填字段
            record.setCourseName(course.getCourseName());
            record.setRealName(user.getRealName());
            record.setUsername(user.getUsername());

            record.setCheckInTime(Timestamp.valueOf(LocalDateTime.now()));

            if (nowTime.isAfter(schedule.getStartTime())) {
                record.setStatus("LATE");
            } else {
                record.setStatus("NORMAL");
            }

            Attendance saved = attendanceRepository.save(record);

            return ok(saved);

        } catch (Exception e) {
            e.printStackTrace();

            String errorMessage = e.getMessage();

            if (errorMessage == null || errorMessage.isBlank()) {
                errorMessage = e.getClass().getSimpleName();
            }

            return fail("打卡失败：" + errorMessage);
        }
    }

    @PostMapping("/checkout")
    public Result<Attendance> checkout(@RequestBody Attendance attendance,
                                       HttpSession session) {
        try {
            User user = getLoginUser(session);

            if (user == null) {
                return fail("请先登录");
            }

            if (!isStudent(user)) {
                return fail("只有学生账号可以进行签退");
            }

            if (attendance.getCourseId() == null) {
                return fail("请选择课程");
            }

            if (attendance.getCourseScheduleId() == null) {
                return fail("请选择具体上课时间");
            }

            LocalDate today = LocalDate.now();

            Timestamp dayStart = Timestamp.valueOf(today.atStartOfDay());
            Timestamp dayEnd = Timestamp.valueOf(today.atTime(23, 59, 59));

            Optional<Attendance> existing =
                    attendanceRepository.findFirstByUserIdAndCourseIdAndCourseScheduleIdAndCheckInTimeBetweenAndCheckOutTimeIsNull(
                            user.getId(),
                            attendance.getCourseId(),
                            attendance.getCourseScheduleId(),
                            dayStart,
                            dayEnd
                    );

            if (existing.isEmpty()) {
                return fail("请先完成打卡，或今天已经签退");
            }

            Attendance record = existing.get();
            record.setCheckOutTime(Timestamp.valueOf(LocalDateTime.now()));

            Attendance saved = attendanceRepository.save(record);

            return ok(saved);

        } catch (Exception e) {
            e.printStackTrace();

            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isBlank()) {
                errorMessage = e.getClass().getSimpleName();
            }

            return fail("签退失败：" + errorMessage);
        }
    }

    @GetMapping("/all")
    public Result<List<Attendance>> getAll(HttpSession session) {
        User user = getLoginUser(session);

        if (user == null) {
            return fail("请先登录");
        }

        if (!isManager(user)) {
            return fail("无权查看全部考勤记录");
        }

        return ok(attendanceService.getAll());
    }

    @GetMapping("/student/{studentId}")
    public Result<List<Attendance>> getByStudentId(@PathVariable Long studentId,
                                                   HttpSession session) {
        User user = getLoginUser(session);

        if (user == null) {
            return fail("请先登录");
        }

        if (isStudent(user) && !user.getId().equals(studentId)) {
            return fail("学生账号只能查看自己的考勤记录");
        }

        if (!isStudent(user) && !isManager(user)) {
            return fail("无权查看考勤记录");
        }

        return ok(attendanceService.getByStudentId(studentId));
    }

    @GetMapping("/course/{courseId}")
    public Result<List<Attendance>> getByCourseId(@PathVariable Long courseId,
                                                  HttpSession session) {
        User user = getLoginUser(session);

        if (user == null) {
            return fail("请先登录");
        }

        if (!isManager(user)) {
            return fail("无权按课程查看考勤记录");
        }

        return ok(attendanceService.getByCourseId(courseId));
    }

    @GetMapping("/page")
    public Result<Page<Attendance>> getPage(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "5") int size,
                                            @RequestParam(defaultValue = "checkInTime") String sortBy,
                                            @RequestParam(defaultValue = "desc") String direction,
                                            HttpSession session) {
        User user = getLoginUser(session);

        if (user == null) {
            return fail("请先登录");
        }

        if (!isManager(user)) {
            return fail("无权分页查看考勤记录");
        }

        return ok(attendanceService.getPage(page, size, sortBy, direction));
    }

    @PostMapping("/search")
    public Result<Page<Attendance>> search(@RequestBody AttendanceQueryDTO queryDTO,
                                           HttpSession session) {
        User user = getLoginUser(session);

        if (user == null) {
            return fail("请先登录");
        }

        if (!isManager(user)) {
            return fail("无权进行考勤条件查询");
        }

        return ok(attendanceService.queryByCondition(queryDTO));
    }

    @GetMapping("/statistics/student/{studentId}")
    public Result<StatisticsDTO> getStudentStatistics(@PathVariable Long studentId,
                                                      HttpSession session) {
        User user = getLoginUser(session);

        if (user == null) {
            return fail("请先登录");
        }

        if (isStudent(user) && !user.getId().equals(studentId)) {
            return fail("学生账号只能查看自己的考勤统计");
        }

        if (!isStudent(user) && !isManager(user)) {
            return fail("无权查看考勤统计");
        }

        StatisticsDTO statisticsDTO = (StatisticsDTO) attendanceService.getStudentStatistics(studentId);

        return ok(statisticsDTO);
    }

    private String weekText(Integer weekDay) {
        if (weekDay == null) {
            return "";
        }

        switch (weekDay) {
            case 1:
                return "周一";
            case 2:
                return "周二";
            case 3:
                return "周三";
            case 4:
                return "周四";
            case 5:
                return "周五";
            case 6:
                return "周六";
            case 7:
                return "周日";
            default:
                return "";
        }
    }

    private User getLoginUser(HttpSession session) {
        return (User) session.getAttribute(AuthorizationInterceptor.SESSION_USER);
    }

    private boolean isStudent(User user) {
        return user != null && "STUDENT".equalsIgnoreCase(user.getRole());
    }

    private boolean isManager(User user) {
        return user != null
                && ("TEACHER".equalsIgnoreCase(user.getRole())
                || "ADMIN".equalsIgnoreCase(user.getRole()));
    }

    private <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    private <T> Result<T> fail(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }
}