package com.example.attendance.service;

import com.example.attendance.entity.Student;
import com.example.attendance.result.ImportResult;
import org.springframework.data.domain.Page;
import java.util.List;
import java.io.File;

public interface StudentService {
    Student createStudent(Student student);

    Student getById(String studentId);

    List<Student> getAll();

    List<Student> getByClassName(String className);

    void deleteById(String studentId);

    Page<Student> findPage(String keyword, int page, int size, String sortField, String sortDir);

    void batchDelete(List<String> ids);

    ImportResult importStudentsFromExcel(File file);
}