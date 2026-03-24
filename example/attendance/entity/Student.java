package com.example.attendance.entity;

public class Student {
    private  String studentId;   //学号
    private  String name;        //姓名
    private  String className;   //班级
    private  Integer age;        //年龄

    public Student() {
    }

    public Student(String studentId, String name, String className, Integer age) {
        this.studentId = studentId;
        this.name = name;
        this.className = className;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
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
