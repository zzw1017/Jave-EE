package com.example.attendance.service;

import com.example.attendance.dto.LeaveApproveDTO;
import com.example.attendance.dto.LeaveQueryDTO;
import com.example.attendance.entity.LeaveRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LeaveRequestService {

    LeaveRequest apply(LeaveRequest leaveRequest);

    List<LeaveRequest> getByStudentId(String studentId);

    Page<LeaveRequest> search(LeaveQueryDTO queryDTO);

    LeaveRequest approve(LeaveApproveDTO approveDTO, Long approverId);

    LeaveRequest cancel(Long leaveId, String studentId);
}