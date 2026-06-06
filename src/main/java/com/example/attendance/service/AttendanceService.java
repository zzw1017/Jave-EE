package com.example.attendance.service;


import com.example.attendance.dto.StatisticsDTO;
import com.example.attendance.entity.Attendance;
import com.example.attendance.dto.AttendanceQueryDTO;
import com.example.attendance.result.ImportResult;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface AttendanceService {

    Attendance create(Attendance attendance);
    List<Attendance> getAll();
    List<Attendance> getByStudentId(Long studentId);
    List<Attendance> getByCourseId(Long courseId);
    Page<Attendance> getPage(int page, int size, String soryBy, String direction);
    Page<Attendance> queryByCondition(AttendanceQueryDTO QueryDTO);
    String checkIn(Attendance attendance, String ip);
    String checkOut(Long id);
    List<Attendance> filterAttendance(Long courseId, String timeType);
    ImportResult importFromExcel(String filePath) throws Exception;

    // 统计学生总考勤
    long getTotalAttendance(Long studentId);

    // 统计正常/迟到/缺勤
    long getCountByStatus(Long studentId, String status);
    StatisticsDTO getStudentStatistics(Long studentId);
    long getTotalAttendanceCount();
}
