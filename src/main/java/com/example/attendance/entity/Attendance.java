package com.example.attendance.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "real_name", nullable = false)
    private String realName;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "check_in_time", nullable = false)
    private Timestamp checkInTime;

    @Column(name = "check_out_time")
    private Timestamp checkOutTime;

    @Column(name = "ip")
    private String ip;

    @Column(name = "status")
    private String status;

    public Attendance() {
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRealName() {
        return realName;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public Timestamp getCheckInTime() {
        return checkInTime;
    }

    public Timestamp getCheckOutTime() {
        return checkOutTime;
    }

    public String getIp() {
        return ip;
    }

    public String getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCheckInTime(Timestamp checkInTime) {
        this.checkInTime = checkInTime;
    }

    public void setCheckOutTime(Timestamp checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "course_schedule_id")
    private Long courseScheduleId;

    public Long getCourseScheduleId() {
        return courseScheduleId;
    }

    public void setCourseScheduleId(Long courseScheduleId) {
        this.courseScheduleId = courseScheduleId;
    }

}