package com.example.attendance.service;

import com.example.attendance.entity.Course;

import java.util.List;

public interface CourseService {

    List<Course> findAll();

    Course findById(Long id);

    String saveCourse(Long id, String courseName, String startTime, String endTime);

    void deleteById(Long id);
}
