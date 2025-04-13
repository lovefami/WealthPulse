package com.tradingacademy.trading_system.repository;

import com.tradingacademy.trading_system.model.entity.UserLessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLessonProgressRepository extends JpaRepository<UserLessonProgress, Long> {
  @NonNull
  Optional<UserLessonProgress> findByUserIdAndLessonId(@NonNull Long userId, @NonNull Long lessonId);

  @NonNull
  List<UserLessonProgress> findByUserIdAndLessonSessionId(@NonNull Long userId, @NonNull Long sessionId);

  @Query("SELECT COUNT(p) FROM UserLessonProgress p WHERE p.user.id = :userId AND p.lesson.session.id = :sessionId AND p.completed = true")
  int countCompletedByUserIdAndSessionId(@NonNull Long userId, @NonNull Long sessionId);

  @Query("SELECT COUNT(p) FROM UserLessonProgress p WHERE p.user.id = :userId AND p.lesson.session.id IN :sessionIds AND p.completed = true")
  int countCompletedByUserIdAndSessionIds(@NonNull Long userId, @NonNull List<Long> sessionIds);
}