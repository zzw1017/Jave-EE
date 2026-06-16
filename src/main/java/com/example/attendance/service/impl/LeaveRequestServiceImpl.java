package com.example.attendance.service.impl;

import com.example.attendance.dto.LeaveApproveDTO;
import com.example.attendance.dto.LeaveQueryDTO;
import com.example.attendance.entity.LeaveRequest;
import com.example.attendance.entity.LeaveStatus;
import com.example.attendance.repository.LeaveRequestRepository;
import com.example.attendance.service.LeaveRequestService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Override
    public LeaveRequest apply(LeaveRequest leaveRequest) {
        if (!StringUtils.hasText(leaveRequest.getStudentId())) {
            throw new RuntimeException("学生学号不能为空");
        }

        if (!StringUtils.hasText(leaveRequest.getReason())) {
            throw new RuntimeException("请假原因不能为空");
        }

        if (leaveRequest.getStartTime() == null || leaveRequest.getEndTime() == null) {
            throw new RuntimeException("请假开始时间和结束时间不能为空");
        }

        if (!leaveRequest.getEndTime().isAfter(leaveRequest.getStartTime())) {
            throw new RuntimeException("结束时间必须晚于开始时间");
        }

        leaveRequest.setStatus(LeaveStatus.PENDING);
        leaveRequest.setApplyTime(LocalDateTime.now());
        leaveRequest.setApproveTime(null);
        leaveRequest.setApproverId(null);
        leaveRequest.setRejectReason(null);

        return leaveRequestRepository.save(leaveRequest);
    }

    @Override
    public List<LeaveRequest> getByStudentId(String studentId) {
        return leaveRequestRepository.findByStudentIdOrderByApplyTimeDesc(studentId);
    }

    @Override
    public Page<LeaveRequest> search(LeaveQueryDTO queryDTO) {
        int page = Math.max(queryDTO.getPage() - 1, 0);
        int size = queryDTO.getSize() <= 0 ? 10 : queryDTO.getSize();

        Sort.Direction direction = "asc".equalsIgnoreCase(queryDTO.getDirection())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        String sortBy = queryDTO.getSortBy() == null || queryDTO.getSortBy().isBlank()
                ? "applyTime"
                : queryDTO.getSortBy();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Specification<LeaveRequest> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(queryDTO.getStudentId())) {
                predicates.add(criteriaBuilder.equal(root.get("studentId"), queryDTO.getStudentId()));
            }

            if (queryDTO.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), queryDTO.getStatus()));
            }

            if (queryDTO.getStartTime() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startTime"), queryDTO.getStartTime()));
            }

            if (queryDTO.getEndTime() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endTime"), queryDTO.getEndTime()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return leaveRequestRepository.findAll(specification, pageable);
    }

    @Override
    public LeaveRequest approve(LeaveApproveDTO approveDTO, Long approverId) {
        if (approveDTO.getLeaveId() == null) {
            throw new RuntimeException("请假ID不能为空");
        }

        if (approveDTO.getStatus() == null) {
            throw new RuntimeException("审批状态不能为空");
        }

        if (approveDTO.getStatus() != LeaveStatus.APPROVED &&
                approveDTO.getStatus() != LeaveStatus.REJECTED) {
            throw new RuntimeException("审批状态只能是 APPROVED 或 REJECTED");
        }

        LeaveRequest leaveRequest = leaveRequestRepository.findById(approveDTO.getLeaveId())
                .orElseThrow(() -> new RuntimeException("请假申请不存在"));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("只有待审批的请假申请可以审批");
        }

        leaveRequest.setStatus(approveDTO.getStatus());
        leaveRequest.setApproverId(approverId);
        leaveRequest.setApproveTime(LocalDateTime.now());

        if (approveDTO.getStatus() == LeaveStatus.REJECTED) {
            leaveRequest.setRejectReason(approveDTO.getRejectReason());
        } else {
            leaveRequest.setRejectReason(null);
        }

        return leaveRequestRepository.save(leaveRequest);
    }

    @Override
    public LeaveRequest cancel(Long leaveId, String studentId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("请假申请不存在"));

        if (!leaveRequest.getStudentId().equals(studentId)) {
            throw new RuntimeException("只能撤回自己的请假申请");
        }

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("只有待审批的请假申请可以撤回");
        }

        leaveRequest.setStatus(LeaveStatus.CANCELLED);

        return leaveRequestRepository.save(leaveRequest);
    }
}