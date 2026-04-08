package com.example.attendance.controller;
import com.example.attendance.entity.User;
import com.example.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public String addUser(@RequestBody User user) {
        int rows = userService.addUser(user);
        return rows > 0 ? "添加成功" : "添加失败";
    }

    @GetMapping("/get/{id}")
    public User getUserById(@RequestBody Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/findByUsername/{username}")
    public User findByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @GetMapping("/teachers")
    public List<User> getAllTeachers() {
        return userService.getAllTeachers();
    }

    @PutMapping("/update")
    public String updateUser(@RequestBody User user) {
        int rows = userService.updateUser(user);
        return rows > 0 ? "更新成功" : "更新失败";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        int rows = userService.deleteUser(id);
        return rows > 0 ? "删除成功" : "删除失败";
    }
}

