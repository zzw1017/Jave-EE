package com.example.attendance.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student")
public class Student {
    @Id
    @Column(name = "student_id", nullable = false, length = 50)
    private String studentId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "class_name", nullable = false, length = 50)
    private String className;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender", length = 10)
    private String gender; // 性别

    @Column(name = "birth_date")
    private LocalDate birthDate; // 出生日期

    @Column(name = "phone", length = 20)
    private String phone; // 联系方式

    public Student() {
    }

    public Student(String studentId, String name, String className, Integer age, String phone, LocalDate birthDate, String gender) {
        this.studentId = studentId;
        this.name = name;
        this.className = className;
        this.age = age;
        this.phone = phone;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
