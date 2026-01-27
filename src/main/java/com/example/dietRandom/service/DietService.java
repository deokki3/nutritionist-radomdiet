package com.example.dietRandom.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    @Transactional // DB 변경(저장/삭제)이 일어나므로 트랜잭션 필수!
    public List<MealPlan> createMonthlyPlan(int year, int month, int sideCount) {
        
        // 1. 해당 월의 시작일과 종료일 구하기
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 2. 기존 식단이 있다면 싹 지우고 다시 짤까요? (일단 삭제하고 시작)
        mealPlanRepository.deleteByDateBetween(startDate, endDate);

        // 3. 1일부터 말일까지 반복문 (Loop)
        List<MealPlan> monthlyPlans = new ArrayList<>();
        
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate currentDate = yearMonth.atDay(day);

            // 주말(토,일)은 제외할까요? (일단 포함해서 짜고 나중에 빼는 걸로 하시죠)
            // if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || ...) continue;

            // 4. 기존에 만든 '1끼 랜덤 생성기' 호출! (재사용의 미학)
            DietResponse randomOneMeal = createRandomDiet(sideCount);

            // 5. DTO(결과물) -> Entity(DB저장용) 변환
            MealPlan plan = new MealPlan();
            plan.setDate(currentDate);
            plan.setRice(randomOneMeal.getRice());
            plan.setSoup(randomOneMeal.getSoup());
            plan.setMain(randomOneMeal.getMain());
            plan.setKimchi(randomOneMeal.getKimchi());
            plan.setSideDishes(randomOneMeal.getSides());

            monthlyPlans.add(plan);
        }

        // 6. 한방에 DB 저장 (Bulk Insert)
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
}