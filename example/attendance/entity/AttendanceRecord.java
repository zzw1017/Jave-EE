package com.example.attendance.entity;
import java.time.LocalDateTime;
public class AttendanceRecord {
    private String studentId;
    private String coutseId;
    private String status;
    private LocalDateTime checkTime;

    public AttendanceRecord() {
    }

    public AttendanceRecord(String studentId, String coutseId, LocalDateTime checkTime, String status) {
        this.studentId = studentId;
        this.coutseId = coutseId;
        this.checkTime = checkTime;
        this.status = status;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCoutseId() {
        return coutseId;
    }

    public void setCoutseId(String coutseId) {
        this.coutseId = coutseId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(LocalDateTime checkTime) {
        this.checkTime = checkTime;
    }
}
