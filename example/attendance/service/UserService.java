package com.example.attendance.service;

import com.example.attendance.entity.User;
import java.util.List;

public interface UserService {
    int addUser(User user);
    User getUserById(Long id);
    User getUserByUsername(String username);
    List<User> getAllTeachers();
    int updateUser(User user);
    int deleteUser(Long id);
}
