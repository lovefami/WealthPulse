package com.tradingacademy.trading_system.repository;

import com.tradingacademy.trading_system.model.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
  List<Course> findByTitleContaining(String keyword);
  List<Course> findByUsersId(Long userId);
}