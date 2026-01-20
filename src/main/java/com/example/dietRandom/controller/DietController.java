package com.example.dietRandom.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}