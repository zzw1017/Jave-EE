package com.example.attendance.Repository;

import com.example.attendance.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    List<Student> findByClassName(String className);
    List<Student> findByNameContaining(String keyword);
    Page<Student> findByNameContainingOrStudentIdContaining(String name, String studentId, Pageable pageable);
}