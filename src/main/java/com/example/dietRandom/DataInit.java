package com.example.dietRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.dietRandom.domain.Menu;
import com.example.dietRandom.repository.MenuRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {

    private final MenuRepository menuRepository;
    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        System.out.println("========== 🏭 대규모 메뉴 데이터 공장 가동 ==========");

        List<Menu> menus = new ArrayList<>();

        // 1. 🍚 밥 생성 (약 15종)
        String[] riceTypes = {"흰쌀", "현미", "흑미", "잡곡", "보리", "귀리", "콩", "팥", "차조", "기장", "강황", "곤드레", "굴", "영양", "무"};
        for (String type : riceTypes) {
            menus.add(create(type + "밥", "RICE", 250 + random.nextInt(100)));
        }

        // 2. 🍲 국/찌개 생성 (약 50종)
        String[] soupIngredients = {"김치", "된장", "순두부", "청국장", "콩나물", "소고기무", "미역", "북어", "동태", "알", "오징어무", "감자", "계란", "어묵", "만두", "부대", "육개장", "갈비", "설렁", "곰"};
        String[] soupSuffixes = {"국", "찌개", "탕", "전골"};
        for (String ing : soupIngredients) {
            // 재료에 따라 어울리는 접미사 랜덤 부착 (단순화)
            String suffix = soupSuffixes[random.nextInt(soupSuffixes.length)];
            // "김치국", "김치찌개" 등이 생성됨
            if (isValidSoup(ing, suffix)) { 
                menus.add(create(ing + suffix, "SOUP", 100 + random.nextInt(200)));
            }
        }
        // 기본 국 추가
        menus.add(create("시래기국", "SOUP", 120));
        menus.add(create("아욱국", "SOUP", 110));
        menus.add(create("근대국", "SOUP", 110));

        // 3. 🍗 메인 요리 생성 (약 50종)
        String[] mainIngs = {"제육", "오징어", "낙지", "쭈꾸미", "닭갈비", "불고기", "갈비찜", "삼겹살", "목살", "훈제오리", "고등어", "삼치", "갈치", "임연수", "돈까스", "생선까스", "함박", "탕수육"};
        String[] mainMethods = {"볶음", "구이", "조림", "찜", "튀김"};
        for (String ing : mainIngs) {
            // 일부 고정 메뉴는 그대로, 나머지는 조리법 붙이기
            if(ing.contains("까스") || ing.contains("탕수육")) {
                 menus.add(create(ing, "MAIN", 500 + random.nextInt(300)));
            } else {
                menus.add(create(ing + mainMethods[random.nextInt(3)], "MAIN", 400 + random.nextInt(400)));
            }
        }
        // 추가 메인
        menus.add(create("마파두부", "MAIN", 450));
        menus.add(create("잡채밥", "MAIN", 550));
        menus.add(create("카레라이스", "MAIN", 600));
        menus.add(create("짜장밥", "MAIN", 600));

        // 4. 🥗 반찬 생성 (여기가 핵심! 약 100종)
        String[] sideIngs = {"계란", "두부", "감자", "어묵", "멸치", "진미채", "콩나물", "시금치", "무생채", "오이", "가지", "호박", "버섯", "고사리", "도라지", "취나물", "미역줄기", "연근", "우엉", "마늘쫑", "꽈리고추", "메추리알", "비엔나", "햄"};
        String[] sideMethods = {"말이", "조림", "볶음", "무침", "찜", "구이", "전", "부침"};
        
        for (String ing : sideIngs) {
            // 재료 하나당 2~3가지 조리법으로 뻥튀기
            for (int i = 0; i < 3; i++) {
                String method = sideMethods[random.nextInt(sideMethods.length)];
                menus.add(create(ing + method, "SIDE", 50 + random.nextInt(150)));
            }
        }
        // 김용 반찬 추가
        menus.add(create("조미김", "SIDE", 30));
        menus.add(create("김자반", "SIDE", 40));

        // 5. 🌶️ 김치 생성 (약 10종)
        String[] kimchis = {"배추김치", "깍두기", "열무김치", "총각김치", "파김치", "갓김치", "백김치", "동치미", "나박김치", "오이소박이"};
        for (String k : kimchis) {
            menus.add(create(k, "KIMCHI", 30 + random.nextInt(20)));
        }

        // DB 저장
        menuRepository.saveAll(menus);
        System.out.println("========== ✅ 총 " + menus.size() + "개의 메뉴 데이터 적재 완료! ==========");
    }

    // 헬퍼: 억지스러운 국 이름 제외 (예: 육개장찌개 X)
    private boolean isValidSoup(String ing, String suffix) {
        if ((ing.equals("육개장") || ing.equals("설렁") || ing.equals("곰")) && !suffix.equals("탕")) return false;
        if (ing.equals("미역") && suffix.equals("찌개")) return false;
        return true;
    }

    private Menu create(String name, String category, int calories) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setCategory(category);
        menu.setCalories(calories);
        return menu;
    }
}