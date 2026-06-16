package com.example.attendance.controller;

import com.example.attendance.config.AuthorizationInterceptor;
import com.example.attendance.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/leave/page")
public class LeavePageController {

    @GetMapping("/apply")
    public String applyPage(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute(AuthorizationInterceptor.SESSION_USER);

        if (loginUser == null) {
            return "redirect:/page/login";
        }

        if (!"STUDENT".equalsIgnoreCase(loginUser.getRole())) {
            boolean isManager = "TEACHER".equalsIgnoreCase(loginUser.getRole())
                    || "ADMIN".equalsIgnoreCase(loginUser.getRole());

            model.addAttribute("title", "无权限");
            model.addAttribute("loginUser", loginUser);
            model.addAttribute("isManager", isManager);
            model.addAttribute("errorMsg", "只有学生可以提交请假申请");

            return "dashboard";
        }

        model.addAttribute("title", "我的请假");
        model.addAttribute("loginUser", loginUser);

        return "leave-apply";
    }

    @GetMapping("/list")
    public String listPage(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute(AuthorizationInterceptor.SESSION_USER);

        if (loginUser == null) {
            return "redirect:/page/login";
        }

        boolean isManager = "TEACHER".equalsIgnoreCase(loginUser.getRole())
                || "ADMIN".equalsIgnoreCase(loginUser.getRole());

        if (!isManager) {
            model.addAttribute("title", "无权限");
            model.addAttribute("loginUser", loginUser);
            model.addAttribute("isManager", false);
            model.addAttribute("errorMsg", "学生不能进入请假管理页面");

            return "dashboard";
        }

        model.addAttribute("title", "请假管理");
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("isManager", true);

        return "leave-manage";
    }
}