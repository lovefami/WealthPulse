package com.tradingacademy.trading_system.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String name; // Eg. session 1, session2

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<Lesson> lessons = new ArrayList<>();

    @OneToMany(mappedBy = "session",cascade = CascadeType.ALL)
    private List<UserSession> userSessions = new ArrayList<>();
}