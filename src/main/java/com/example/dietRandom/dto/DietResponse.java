package com.example.dietRandom.dto;

import java.util.List;

import com.example.dietRandom.domain.Menu;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DietResponse {
    // 한 끼 식단 구성
    private Menu rice;
    private Menu soup;
    private Menu main;
    private List<Menu> sides; // 반찬은 여러 개니까 List
    private Menu kimchi;
    
    // 총 칼로리 계산 (서비스용)
    public int getTotalCalories() {
        int total = 0;
        if(rice != null) total += rice.getCalories();
        if(soup != null) total += soup.getCalories();
        if(main != null) total += main.getCalories();
        if(kimchi != null) total += kimchi.getCalories();
        for(Menu s : sides) total += s.getCalories();
        return total;
    }
}