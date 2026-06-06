package com.example.attendance.service.impl;
import com.example.attendance.entity.Student;
import com.example.attendance.Repository.StudentRepository;
import com.example.attendance.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Student createStudent(Student student) {
        if (student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            throw new RuntimeException("学号不能为空");
        }
        return studentRepository.save(student);
    }

    @Override
    public Student getById(String studentId) {
        return studentRepository.findById(studentId).orElse(null);
    }

    @Override
    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    @Override
    public List<Student> getByClassName(String className) {
        return studentRepository.findByClassName(className);
    }

    @Override
    public void deleteById(String studentId) {
        studentRepository.deleteById(studentId);
    }

    public Page<Student> findPage(String keyword, int page, int size, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField).ascending();
        if ("desc".equals(sortDir)) {
            sort = Sort.by(sortField).descending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        if (keyword == null || keyword.isBlank()) {
            return studentRepository.findAll(pageable);
        } else {
            return studentRepository.findByNameContainingOrStudentIdContaining(keyword, keyword, pageable);
        }
    }
    @Override
    public void batchDelete(List<String> ids) {
        studentRepository.deleteAllById(ids);}
}