package com.example.dietRandom.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayDeque;   // Deque 구현체
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;        // 양쪽 삽입/삭제 가능한 큐
import java.util.List;
import java.util.stream.Collectors; // stream 필터링용

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dietRandom.domain.MealPlan;
import com.example.dietRandom.domain.Menu;
import com.example.dietRandom.dto.DietResponse;
import com.example.dietRandom.repository.MealPlanRepository;
import com.example.dietRandom.repository.MenuRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DietService {

    private final MenuRepository menuRepository;

    private final MealPlanRepository mealPlanRepository;


    // 🌟 [추가 기능] 월간 식단 생성 및 저장
    @Transactional
public List<MealPlan> createMonthlyPlan(int year, int month, int sideCount) {

    YearMonth yearMonth = YearMonth.of(year, month);
    LocalDate startDate = yearMonth.atDay(1);
    LocalDate endDate = yearMonth.atEndOfMonth();

    mealPlanRepository.deleteByDateBetween(startDate, endDate);

    // 카테고리별 메뉴를 한 번만 DB에서 조회 (매일 조회하면 비효율)
    List<Menu> rices  = menuRepository.findByCategory("RICE");
    List<Menu> soups  = menuRepository.findByCategory("SOUP");
    List<Menu> mains  = menuRepository.findByCategory("MAIN");
    List<Menu> sides  = menuRepository.findByCategory("SIDE");
    List<Menu> kimchis = menuRepository.findByCategory("KIMCHI");

    // 최근 3일 내 사용된 메인 메뉴 ID를 기억하는 덱
    // 예) 1일:ID=5, 2일:ID=12, 3일:ID=7 이 들어있으면
    //     4일 추첨 시 이 세 개는 후보에서 제외
    int NO_REPEAT_DAYS = 3;
    Deque<Long> recentMainIds = new ArrayDeque<>();

    List<MealPlan> monthlyPlans = new ArrayList<>();

    for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
        LocalDate currentDate = yearMonth.atDay(day);

        MealPlan plan = new MealPlan();
        plan.setDate(currentDate);

        // 밥 / 국 / 김치는 기존과 동일하게 순수 랜덤
        if (!rices.isEmpty()) {
            Collections.shuffle(rices);
            plan.setRice(rices.get(0));
        }
        if (!soups.isEmpty()) {
            Collections.shuffle(soups);
            plan.setSoup(soups.get(0));
        }
        if (!kimchis.isEmpty()) {
            Collections.shuffle(kimchis);
            plan.setKimchi(kimchis.get(0));
        }

        // 메인 메뉴 — 연속 중복 방지 적용
        if (!mains.isEmpty()) {
            Menu selected = pickWithoutRepeat(mains, recentMainIds);
            plan.setMain(selected);

            // 선택된 메인 ID를 덱에 추가
            recentMainIds.addLast(selected.getId());

            // 덱 크기가 N을 초과하면 가장 오래된 항목 제거
            if (recentMainIds.size() > NO_REPEAT_DAYS) {
                recentMainIds.pollFirst();
            }
        }

        // 반찬
        if (!sides.isEmpty()) {
            Collections.shuffle(sides);
            int limit = Math.min(sideCount, sides.size());
            plan.setSideDishes(new ArrayList<>(sides.subList(0, limit)));
        } else {
            plan.setSideDishes(Collections.emptyList());
        }

        monthlyPlans.add(plan);
    }

    return mealPlanRepository.saveAll(monthlyPlans);
}
    
    // 🌟 [추가 기능] 월간 식단 조회
    public List<MealPlan> getMonthlyPlan(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return mealPlanRepository.findAllByDateBetweenOrderByDateAsc(
                yearMonth.atDay(1), yearMonth.atEndOfMonth()
        );
    }
    // 반찬 갯수(sideCount)를 받아서 랜덤 식단을 짭니다.
    public DietResponse createRandomDiet(int sideCount) {
        DietResponse diet = new DietResponse();

        // 1. 각 카테고리별로 후보군을 불러옵니다.
        List<Menu> rices = menuRepository.findByCategory("RICE");
        List<Menu> soups = menuRepository.findByCategory("SOUP");
        List<Menu> mains = menuRepository.findByCategory("MAIN");
        List<Menu> sides = menuRepository.findByCategory("SIDE");
        List<Menu> kimchis = menuRepository.findByCategory("KIMCHI");

        // 2. 랜덤 섞기 (카드가 섞이듯 리스트 순서가 바뀜)
        // 데이터가 비어있을 경우를 대비해 !isEmpty 체크
        if (!rices.isEmpty()) {
            Collections.shuffle(rices);
            diet.setRice(rices.get(0)); // 섞고 나서 맨 위에 거 한 장 뽑기
        }

        if (!soups.isEmpty()) {
            Collections.shuffle(soups);
            diet.setSoup(soups.get(0));
        }

        if (!mains.isEmpty()) {
            Collections.shuffle(mains);
            diet.setMain(mains.get(0));
        }

        if (!kimchis.isEmpty()) {
            Collections.shuffle(kimchis);
            diet.setKimchi(kimchis.get(0));
        }

        // 3. 서브 반찬은 여러 개 뽑아야 함 (요청받은 개수만큼)
        if (!sides.isEmpty()) {
            Collections.shuffle(sides);
            // 0번부터 sideCount 개수만큼 잘라서 가져옴 (혹은 전체 개수보다 요청이 많으면 전체 가져옴)
            int limit = Math.min(sideCount, sides.size());
            diet.setSides(sides.subList(0, limit));
        } else {
            diet.setSides(Collections.emptyList());
        }

        return diet;
    }

        // 최근 사용된 ID 목록(recentIds)에 없는 메뉴만 후보로 추첨
    // 후보가 아예 없으면(메뉴 종류가 너무 적으면) 전체에서 랜덤
    private Menu pickWithoutRepeat(List<Menu> candidates, Deque<Long> recentIds) {
        List<Menu> available = candidates.stream()
                .filter(m -> !recentIds.contains(m.getId()))
                .collect(Collectors.toList());

        if (available.isEmpty()) {
            available = new ArrayList<>(candidates);
        }

        Collections.shuffle(available);
        return available.get(0);
    }
}