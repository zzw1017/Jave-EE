package com.example.attendance.Repository;

import com.example.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.sql.Timestamp;
import java.util.List;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long>, JpaSpecificationExecutor<Attendance> {
    List<Attendance> findByStudentId(Long studentId);
    List<Attendance> findByCourseId(Long courseId);
    List<Attendance> findByStudentIdAndStatus(Long studentId, String status);
    @Query("select a from Attendance a where a.checkInTime between :start and :end")
    List<Attendance> findByTimeRange(@Param("start") Timestamp start, @Param("end") Timestamp end);

    @Query("select a from Attendance a where a.courseId=:cid and a.checkInTime between :start and :end")
    List<Attendance> findByCourseAndTime(@Param("cid") Long cid,
                                         @Param("start") Timestamp start,
                                         @Param("end") Timestamp end);
    List<Attendance> findByCourseIdAndStudentId(Long courseId, Long studentId);
    long countByStudentId(Long studentId);
    long countByStudentIdAndStatus(Long studentId, String status);
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
}