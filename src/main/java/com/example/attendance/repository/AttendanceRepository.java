package com.example.attendance.repository;

import com.example.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.sql.Timestamp;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;


public interface AttendanceRepository extends JpaRepository<Attendance, Long>, JpaSpecificationExecutor<Attendance> {

    boolean existsByUserIdAndCourseIdAndCheckInTimeBetween(
            Long userId,
            Long courseId,
            Timestamp start,
            Timestamp end
    );

    List<Attendance> findAllByOrderByCheckInTimeDesc();

    List<Attendance> findByCourseIdOrderByCheckInTimeDesc(Long courseId);

    List<Attendance> findByCheckInTimeBetweenOrderByCheckInTimeDesc(
            Timestamp start,
            Timestamp end
    );

    List<Attendance> findByCourseIdAndCheckInTimeBetweenOrderByCheckInTimeDesc(
            Long courseId,
            Timestamp start,
            Timestamp end
    );

    Optional<Attendance> findFirstByUserIdAndCourseIdAndCourseScheduleIdAndCheckInTimeBetween(
            Long userId,
            Long courseId,
            Long courseScheduleId,
            Timestamp start,
            Timestamp end
    );

    Optional<Attendance> findFirstByUserIdAndCourseIdAndCourseScheduleIdAndCheckInTimeBetweenAndCheckOutTimeIsNull(
            Long userId,
            Long courseId,
            Long courseScheduleId,
            Timestamp start,
            Timestamp end
    );
}