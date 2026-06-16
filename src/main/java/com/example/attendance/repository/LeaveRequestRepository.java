package com.example.attendance.repository;

import com.example.attendance.entity.LeaveRequest;
import com.example.attendance.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long>,
        JpaSpecificationExecutor<LeaveRequest> {

    List<LeaveRequest> findByStudentIdOrderByApplyTimeDesc(String studentId);

    List<LeaveRequest> findByStudentIdAndStatusOrderByApplyTimeDesc(String studentId, LeaveStatus status);
}