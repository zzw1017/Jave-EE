package com.example.attendance.dto;

public class StatisticsDTO {
    private long totalCount;
    private long normalCount;
    private long lateCount;
    private long absentCount;
    private double attendanceRate;

    public StatisticsDTO() {
    }

    public StatisticsDTO(long totalCount, long normalCount, long lateCount, long absentCount, double attendanceRate) {
        this.totalCount = totalCount;
        this.normalCount = normalCount;
        this.lateCount = lateCount;
        this.attendanceRate = attendanceRate;
        this.absentCount = absentCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getNormalCount() {
        return normalCount;
    }

    public void setNormalCount(long normalCount) {
        this.normalCount = normalCount;
    }

    public long getLateCount() {
        return lateCount;
    }

    public void setLateCount(long lateCount) {
        this.lateCount = lateCount;
    }

    public long getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(long absentCount) {
        this.absentCount = absentCount;
    }

    public double getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(double attendanceRate) {
        this.attendanceRate = attendanceRate;
    }
}
