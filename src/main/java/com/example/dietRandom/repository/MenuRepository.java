package com.example.dietRandom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dietRandom.domain.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    // "반찬(SIDE)만 다 가져와!", "국(SOUP)만 다 가져와!" 할 때 씁니다.
    List<Menu> findByCategory(String category);
}