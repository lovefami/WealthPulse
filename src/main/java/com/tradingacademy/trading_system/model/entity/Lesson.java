package com.tradingacademy.trading_system.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "lesson")
public class Lesson {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  @ManyToOne
  @JoinColumn(name = "session_id", nullable = false)
  private Session session;


  @ManyToOne
  @JoinColumn(name = "course_id", nullable = false)
  private Course course;
  @Column(nullable = false)
  private LocalDateTime date;
}
