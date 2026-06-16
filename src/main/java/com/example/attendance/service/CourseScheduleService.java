package com.example.attendance.service;

import com.example.attendance.entity.CourseSchedule;

import java.util.List;

public interface CourseScheduleService {

    CourseSchedule save(CourseSchedule schedule);

    List<CourseSchedule> getByCourseId(Long courseId);

    void delete(Long id);

    void deleteByCourseId(Long courseId);
}