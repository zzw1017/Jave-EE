package com.example.attendance.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "check_in_time", nullable = false)
    private Timestamp checkInTime;

    @Column(name = "seat_row", nullable = false)
    private Integer seatRow;

    @Column(name = "seat_col", nullable = false)
    private Integer seatCol;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "ip", length = 50)
    private String ip;

    @Column(name = "create_time")
    private Timestamp createTime;

    public Attendance() {
    }

    public Attendance(Long id, Long courseId, Long studentId, Timestamp checkInTime, Integer seatRow, Integer seatCol, String status, String ip, Timestamp createTime) {
        this.id = id;
        this.courseId = courseId;
        this.studentId = studentId;
        this.checkInTime = checkInTime;
        this.seatRow = seatRow;
        this.seatCol = seatCol;
        this.status = status;
        this.ip = ip;
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Timestamp getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Timestamp checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Integer getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(Integer seatRow) {
        this.seatRow = seatRow;
    }

    public Integer getSeatCol() {
        return seatCol;
    }

    public void setSeatCol(Integer seatCol) {
        this.seatCol = seatCol;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}