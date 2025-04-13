package com.tradingacademy.trading_system.repository;

import com.tradingacademy.trading_system.model.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
  Page<Lesson> findByCourseId(@NonNull Long courseId, Pageable pageable);

  List<Lesson> findBySessionId(@NonNull Long sessionId);
}