package com.example.dietRandom.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.dietRandom.domain.Menu;
import com.example.dietRandom.dto.DietResponse;
import com.example.dietRandom.repository.MenuRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DietService {

    private final MenuRepository menuRepository;

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