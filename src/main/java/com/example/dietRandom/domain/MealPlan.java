package com.example.dietRandom.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class MealPlan {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 날짜 (YYYY-MM-DD) - 중복 방지를 위해 유니크 걸면 좋지만 일단 패스
    @Column(nullable = false)
    private LocalDate date;

    // 메뉴들과의 관계 맺기 (Foreign Key)
    // 밥, 국, 메인, 김치는 하나씩이니까 @ManyToOne
    
    @ManyToOne 
    @JoinColumn(name = "rice_id")
    private Menu rice;

    @ManyToOne
    @JoinColumn(name = "soup_id")
    private Menu soup;

    @ManyToOne
    @JoinColumn(name = "main_id")
    private Menu main;

    @ManyToOne
    @JoinColumn(name = "kimchi_id")
    private Menu kimchi;

    // 반찬은 여러 개니까 @ManyToMany (중간 테이블 자동 생성됨)
    @ManyToMany
    @JoinTable(
        name = "meal_plan_sides",
        joinColumns = @JoinColumn(name = "meal_plan_id"),
        inverseJoinColumns = @JoinColumn(name = "menu_id")
    )
    private List<Menu> sideDishes = new ArrayList<>();
}