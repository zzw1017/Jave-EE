package com.example.attendance.repository;

import com.example.attendance.entity.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Long> {

    List<CourseSchedule> findByCourseIdOrderByWeekDayAscStartTimeAsc(Long courseId);

    List<CourseSchedule> findByWeekDayOrderByStartTimeAsc(Integer weekDay);

    void deleteByCourseId(Long courseId);
}