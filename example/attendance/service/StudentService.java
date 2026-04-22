package com.example.attendance.service;

import com.example.attendance.entity.Student;
import com.example.attendance.entity.StudentRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    // 用构造器注入（解决 IDEA 不建议字段注入的警告）
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student saveStudent(Student student) {
        // 自动填充时间（适配你的 Student 实体字段）
        if (student.getId() == null) {
            student.setCreateTime(LocalDateTime.now());
        }
        student.setUpdateTime(LocalDateTime.now());
        return studentRepository.save(student);
    }

    public Student findById(Long id) {
        Optional<Student> studentOpt = studentRepository.findById(id);
        return studentOpt.orElse(null);
    }

    public Student findByUsername(String username) {
        Optional<Student> studentOpt = studentRepository.findByUsername(username);
        return studentOpt.orElse(null);
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }
}