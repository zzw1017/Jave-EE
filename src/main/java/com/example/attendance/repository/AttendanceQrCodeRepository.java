package com.example.attendance.repository;

import com.example.attendance.entity.AttendanceQrCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttendanceQrCodeRepository extends JpaRepository<AttendanceQrCode, Long> {

    Optional<AttendanceQrCode> findByToken(String token);
}