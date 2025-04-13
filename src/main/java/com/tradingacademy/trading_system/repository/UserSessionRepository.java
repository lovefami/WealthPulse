package com.tradingacademy.trading_system.repository;

import com.tradingacademy.trading_system.model.entity.UserSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByUserIdAndSessionId(@NonNull Long userId, @NonNull Long sessionId);

    Page<UserSession> findByUserId(@NonNull Long userId, Pageable pageable);
}