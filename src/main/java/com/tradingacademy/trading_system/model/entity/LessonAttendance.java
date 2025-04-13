package com.tradingacademy.trading_system.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "lesson_attendance")
@Getter
@Setter
public class LessonAttendance {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_session_id", nullable = false)
  private UserSession userSession;

  @ManyToOne
  @JoinColumn(name = "lesson_id", nullable = false)
  private Lesson lesson;

  @Column(nullable = false)
  private String status; // "PRESENT" 或 "ABSENT"

  @Column(nullable = false)
  private LocalDateTime attendanceDate;
}