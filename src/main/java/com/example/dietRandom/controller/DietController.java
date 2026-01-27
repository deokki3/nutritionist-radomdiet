package com.example.dietRandom.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dietRandom.domain.MealPlan; // 이거 import 확인!
import com.example.dietRandom.dto.DietResponse;
import com.example.dietRandom.service.DietService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/diets")
@RequiredArgsConstructor
public class DietController {

    private final DietService dietService;

    // 랜덤 식단 생성 요청
    // POST http://localhost:8080/api/diets/random?sideCount=3
    @PostMapping("/random")
    public DietResponse generateRandomDiet(@RequestParam(value = "sideCount", defaultValue = "3") int sideCount) {
    return dietService.createRandomDiet(sideCount);
    }

 // 1. 월간 식단 생성 (POST)
    // http://localhost:8080/api/diets/month?year=2026&month=1&sideCount=3
    @PostMapping("/month")
    public List<MealPlan> createMonthlyDiet(
            @RequestParam(value="year") int year,
            @RequestParam(value="month") int month,
            @RequestParam(value="sideCount", defaultValue = "3") int sideCount
    ) {
        return dietService.createMonthlyPlan(year, month, sideCount);
    }

    // 2. 월간 식단 조회 (GET)
    // http://localhost:8080/api/diets/month?year=2026&month=1
    @GetMapping("/month")
    public List<MealPlan> getMonthlyDiet(
            @RequestParam(value="year") int year,
            @RequestParam(value="month") int month
    ) {
        return dietService.getMonthlyPlan(year, month);
    }
}