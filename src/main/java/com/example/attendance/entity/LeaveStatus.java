package com.example.attendance.entity;

public enum LeaveStatus {
    PENDING,    // 待审批
    APPROVED,   // 已通过
    REJECTED,   // 已拒绝
    CANCELLED   // 已撤回
}