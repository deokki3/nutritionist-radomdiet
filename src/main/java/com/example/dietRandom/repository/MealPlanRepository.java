package com.example.dietRandom.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dietRandom.domain.MealPlan;

public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    
    // 🔍 특정 기간의 식단 조회 (예: 2026-05-01 ~ 2026-05-31)
    // select * from meal_plan where date between ? and ? order by date
    List<MealPlan> findAllByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);

    // (선택) 특정 날짜의 식단이 이미 있는지 확인 (중복 생성 방지용)
    boolean existsByDate(LocalDate date);
    
    // (선택) 특정 기간 식단 삭제 (다시 짤 때 필요)
    void deleteByDateBetween(LocalDate startDate, LocalDate endDate);
}