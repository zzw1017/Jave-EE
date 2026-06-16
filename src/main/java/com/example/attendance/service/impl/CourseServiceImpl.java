package com.example.attendance.service.impl;

import com.example.attendance.repository.CourseRepository;
import com.example.attendance.entity.Course;
import com.example.attendance.service.CourseService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Resource
    private CourseRepository courseRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Override
    public List<Course> findAll() {
        return courseRepository.findAllByOrderByStartTimeDesc();
    }

    @Override
    public Course findById(Long id) {
        if (id == null) {
            return null;
        }
        return courseRepository.findById(id).orElse(null);
    }

    @Override
    public String saveCourse(Long id, String courseName, String startTime, String endTime) {

        if (courseName == null || courseName.trim().isEmpty()) {
            return "课程名称不能为空";
        }

        if (startTime == null || startTime.trim().isEmpty()) {
            return "开始时间不能为空";
        }

        if (endTime == null || endTime.trim().isEmpty()) {
            return "结束时间不能为空";
        }

        Timestamp start;
        Timestamp end;

        try {
            start = parseDateTime(startTime);
            end = parseDateTime(endTime);
        } catch (Exception e) {
            return "时间格式不正确";
        }

        if (!end.after(start)) {
            return "结束时间必须晚于开始时间";
        }

        Course course;

        if (id == null) {
            course = new Course();
        } else {
            course = courseRepository.findById(id).orElse(null);
            if (course == null) {
                return "课程不存在";
            }
        }

        course.setCourseName(courseName.trim());
        course.setStartTime(start);
        course.setEndTime(end);

        courseRepository.save(course);

        return null;
    }

    @Override
    public void deleteById(Long id) {
        if (id != null) {
            courseRepository.deleteById(id);
        }
    }

    private Timestamp parseDateTime(String value) {
        LocalDateTime localDateTime = LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        return Timestamp.valueOf(localDateTime);
    }
}