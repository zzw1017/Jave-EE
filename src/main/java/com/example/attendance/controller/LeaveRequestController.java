package com.example.attendance.controller;

import com.example.attendance.config.AuthorizationInterceptor;
import com.example.attendance.dto.LeaveApproveDTO;
import com.example.attendance.dto.LeaveQueryDTO;
import com.example.attendance.entity.LeaveRequest;
import com.example.attendance.entity.User;
import com.example.attendance.result.Result;
import com.example.attendance.service.LeaveRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leave")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @PostMapping("/apply")
    public Result<LeaveRequest> apply(@RequestBody LeaveRequest leaveRequest,
                                      HttpSession session) {
        User user = getLoginUser(session);

        if (!"STUDENT".equalsIgnoreCase(user.getRole())) {
            return fail("只有学生可以提交请假申请");
        }

        // 这里保存 username，也就是学号
        leaveRequest.setStudentId(user.getUsername());

        LeaveRequest saved = leaveRequestService.apply(leaveRequest);

        return ok(saved);
    }

    @GetMapping("/my")
    public Result<List<LeaveRequest>> myLeaves(HttpSession session) {
        User user = getLoginUser(session);

        // 这里用 username 查询，也就是学号
        List<LeaveRequest> list = leaveRequestService.getByStudentId(user.getUsername());

        return ok(list);
    }

    @GetMapping("/student/{studentId}")
    public Result<List<LeaveRequest>> getByStudentId(@PathVariable String studentId,
                                                     HttpSession session) {
        User user = getLoginUser(session);

        if ("STUDENT".equalsIgnoreCase(user.getRole()) &&
                !user.getUsername().equals(studentId)) {
            return fail("学生只能查看自己的请假记录");
        }

        List<LeaveRequest> list = leaveRequestService.getByStudentId(studentId);

        return ok(list);
    }

    @PostMapping("/search")
    public Result<Page<LeaveRequest>> search(@RequestBody LeaveQueryDTO queryDTO,
                                             HttpSession session) {
        User user = getLoginUser(session);

        if (!isManager(user)) {
            return fail("无权限操作");
        }

        Page<LeaveRequest> page = leaveRequestService.search(queryDTO);

        return ok(page);
    }

    @PostMapping("/approve")
    public Result<LeaveRequest> approve(@RequestBody LeaveApproveDTO approveDTO,
                                        HttpSession session) {
        User user = getLoginUser(session);

        if (!isManager(user)) {
            return fail("无权限操作");
        }

        LeaveRequest updated = leaveRequestService.approve(approveDTO, user.getId());

        return ok(updated);
    }

    @PostMapping("/cancel/{leaveId}")
    public Result<LeaveRequest> cancel(@PathVariable Long leaveId,
                                       HttpSession session) {
        User user = getLoginUser(session);

        if (!"STUDENT".equalsIgnoreCase(user.getRole())) {
            return fail("只有学生可以撤回请假申请");
        }

        LeaveRequest updated = leaveRequestService.cancel(leaveId, user.getUsername());

        return ok(updated);
    }

    private User getLoginUser(HttpSession session) {
        User user = (User) session.getAttribute(AuthorizationInterceptor.SESSION_USER);

        if (user == null) {
            throw new RuntimeException("用户未登录");
        }

        return user;
    }

    private boolean isManager(User user) {
        return "ADMIN".equalsIgnoreCase(user.getRole())
                || "TEACHER".equalsIgnoreCase(user.getRole());
    }

    private <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    private <T> Result<T> fail(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }
}