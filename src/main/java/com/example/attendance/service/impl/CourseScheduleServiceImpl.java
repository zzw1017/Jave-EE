package com.example.attendance.service.impl;

import com.example.attendance.entity.CourseSchedule;
import com.example.attendance.repository.CourseScheduleRepository;
import com.example.attendance.service.CourseScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseScheduleServiceImpl implements CourseScheduleService {

    @Autowired
    private CourseScheduleRepository courseScheduleRepository;

    @Override
    public CourseSchedule save(CourseSchedule schedule) {
        if (schedule.getCourseId() == null) {
            throw new RuntimeException("课程ID不能为空");
        }

        if (schedule.getWeekDay() == null || schedule.getWeekDay() < 1 || schedule.getWeekDay() > 7) {
            throw new RuntimeException("星期必须是1到7");
        }

        if (schedule.getStartTime() == null || schedule.getEndTime() == null) {
            throw new RuntimeException("上课开始时间和结束时间不能为空");
        }

        if (!schedule.getEndTime().isAfter(schedule.getStartTime())) {
            throw new RuntimeException("结束时间必须晚于开始时间");
        }

        return courseScheduleRepository.save(schedule);
    }

    @Override
    public List<CourseSchedule> getByCourseId(Long courseId) {
        return courseScheduleRepository.findByCourseIdOrderByWeekDayAscStartTimeAsc(courseId);
    }

    @Override
    public void delete(Long id) {
        courseScheduleRepository.deleteById(id);
    }

    @Override
    public void deleteByCourseId(Long courseId) {
        courseScheduleRepository.deleteByCourseId(courseId);
    }
}