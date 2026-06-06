package com.example.attendance.entity;
import java.time.LocalDateTime;
public class AttendanceRecord {
    private String studentId;
    private String coutseId;
    private String status;

    public AttendanceRecord() {
    }

    public AttendanceRecord(String studentId, String coutseId, LocalDateTime checkTime, String status) {
        this.studentId = studentId;
        this.coutseId = coutseId;
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
}
