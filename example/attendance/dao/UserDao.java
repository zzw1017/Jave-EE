package com.example.attendance.dao;

import com.example.attendance.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int insert(User user) {
        String sql = "INSERT INTO user (username, password, real_name, role) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getRealName(),
                user.getRole());
    }


    public User findById(Long id){
        String sql = "SELECT * FROM user WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class),id);
    }

    public User findByUsername(String username){
        String sql ="SELECT * FROM user WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class),username);
    }

    public List<User> findAllTeachers(){
        String sql = "SELECT * FROM user WHERE role = 'TEACHER'";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    public int update(User user) {
        String sql = "UPDATE user SET username = ?, password = ?, real_name = ?, role = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getRealName(),
                user.getRole(),
                user.getId());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM user WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
