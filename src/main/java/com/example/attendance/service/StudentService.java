package com.example.attendance.service;


import com.example.attendance.entity.Student;
import org.springframework.data.domain.Page;
import java.util.List;

public interface StudentService {
    Student createStudent(Student student);

    Student getById(String studentId);

    List<Student> getAll();

    List<Student> getByClassName(String className);

    void deleteById(String studentId);
    Page<Student> findPage(String keyword, int page, int size, String sortField, String sortDir);

    // 批量删除
    void batchDelete(List<String> ids);
}