package com.tradingacademy.trading_system.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_session")
@Getter
@Setter
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    @Column(nullable = false)
    private Integer classesLeft; // 剩余课时数（每节课12个课时）

    @Column(nullable = false)
    private Integer classesTaken; // 已上课时数

    @OneToMany(mappedBy = "userSession", cascade = CascadeType.ALL)
    private List<LessonAttendance> attendances = new ArrayList<>();

//    @OneToOne
//    @JoinColumn(name = "payment_id", nullable = false)
//    private Payment payment; // 关联购买课程的支付记录
}