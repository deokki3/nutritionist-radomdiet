package com.example.dietRandom.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dietRandom.domain.Menu;
import com.example.dietRandom.repository.MenuRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuRepository menuRepository;

    // 1. 전체 메뉴 조회 (관리자용)
    @GetMapping
    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    // 2. 메뉴 추가 (반찬 등록)
    @PostMapping
    public Menu createMenu(@RequestBody Menu menu) {
        return menuRepository.save(menu);
    }
    
    // 3. 카테고리별 조회 (식단 짤 때 필요)
    // 예: /api/menus/category/SIDE -> 부찬 후보들만 쫙 줌
    @GetMapping("/category/{category}")
    public List<Menu> getMenusByCategory(@PathVariable String category) {
        return menuRepository.findByCategory(category);
    }
}