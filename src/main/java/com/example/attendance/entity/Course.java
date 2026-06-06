package com.example.attendance.entity;


import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_name",nullable = false)
    private String courseName;

    @Column(name = "start_time",nullable = false)
    private Timestamp startTime;

    @Column(name = "end_time",nullable = false)
    private Timestamp endTime;

    public Course() {}

    public Course(Long id, String courseName, Timestamp startTime, Timestamp endTime) {
        this.id = id;
        this.courseName = courseName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
}
