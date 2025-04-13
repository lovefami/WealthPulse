package com.tradingacademy.trading_system.repository;

import com.tradingacademy.trading_system.model.entity.LessonAttendance;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonAttendanceRepository extends JpaRepository<LessonAttendance, Long> {
  List<LessonAttendance> findByUserSessionId(Long userSessionId);
}