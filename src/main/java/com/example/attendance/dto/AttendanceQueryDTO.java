package com.example.attendance.dto;


public class AttendanceQueryDTO {
    private Long studentId;
    private Long courseId;
    private String status;
    private String startTime;
    private String endTime;
    private Integer page = 1;
    private Integer size = 5;
    private String sortBy = "checkInTime";
    private String direction = "desc";

    public AttendanceQueryDTO() {
    }

    public AttendanceQueryDTO(String status, Long studentId, Long courseId, String endTime, String startTime, Integer page, String direction, String sortBy, Integer size) {
        this.status = status;
        this.studentId = studentId;
        this.courseId = courseId;
        this.endTime = endTime;
        this.startTime = startTime;
        this.page = page;
        this.direction = direction;
        this.sortBy = sortBy;
        this.size = size;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}