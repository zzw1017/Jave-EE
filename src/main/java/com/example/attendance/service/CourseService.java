package com.example.attendance.service;

import com.example.attendance.entity.Course;
import java.util.List;
import java.util.Optional;

public interface CourseService {
    List<Course> findAll();
    Optional<Course> findById(Long id);
}
