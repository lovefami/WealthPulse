package com.tradingacademy.trading_system.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "course")
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

//    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
//    private List<Lesson> lessons;

    @ManyToMany(mappedBy = "courses")
    private List<com.tradingacademy.trading_system.model.entity.User> users;

    @Column(nullable = false)
    private Double price; // 已添加

    @Column(nullable =false)
    private Integer totalDuration;
}