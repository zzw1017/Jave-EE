package com.example.attendance.Repository;

import com.example.attendance.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository  extends JpaRepository<Course,Long>{
}
