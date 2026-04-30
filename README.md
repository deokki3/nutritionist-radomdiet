# 🍱 Nutritionist Random Diet Planner

> 영양사를 위한 **월간 식단 자동 생성 시스템**  
> 현장의 실제 니즈에서 출발한 개인 사이드 프로젝트 (진행 중)

---

## 📌 프로젝트 배경

급식 영양사는 매달 한 달치 식단을 수작업으로 작성합니다.  
이 과정에서 반복적으로 발생하는 문제들이 있습니다.

- 같은 메뉴가 연속으로 겹치는 문제 (예: 3일 연속 제육볶음)
- 발주 비용 절감을 위한 **주 3회 이상 식재료 중복** 조건 충족의 어려움
- 전체 칼로리 균형 유지
- 수십 일치 식단을 일일이 손으로 채우는 비효율

이 문제를 해결하기 위해, **제약 조건을 반영한 식단 자동 생성 시스템**을 직접 설계하고 개발하기 시작했습니다.

---

## 🛠️ 기술 스택

| 구분 | 기술 |
|------|------|
| **Backend** | Java 17, Spring Boot 3.5, Spring Data JPA |
| **Database** | H2 (In-Memory, 개발용) → MariaDB/MySQL 전환 예정 |
| **Frontend** | React 18, TypeScript, Vite, Axios |
| **빌드 도구** | Gradle |
| **기타** | Lombok, REST API |

---

## ✅ 현재 구현된 기능

### Backend
- **랜덤 식단 생성 API** (`POST /api/diets/random`)  
  카테고리별(밥/국/메인/반찬/김치) 메뉴 풀에서 랜덤으로 1끼 식단 구성  
  반찬 개수를 파라미터로 조절 가능 (`sideCount`)

- **월간 식단 일괄 생성 API** (`POST /api/diets/month`)  
  해당 월의 1일~말일까지 날짜별 식단을 한 번에 생성 후 DB 저장  
  기존 데이터 삭제 후 재생성 지원 (`@Transactional`)

- **월간 식단 조회 API** (`GET /api/diets/month`)  
  년/월 기준 전체 식단 데이터 날짜 오름차순 반환

- **메뉴 데이터 초기화** (`DataInit`)  
  애플리케이션 실행 시 밥(15종) / 국(50종) / 메인(50종) / 반찬(100종+) / 김치(10종) 자동 적재

- **JPA 연관관계 매핑**  
  MealPlan ↔ Menu : 밥/국/메인/김치는 `@ManyToOne`, 반찬은 `@ManyToMany` (중간 테이블 자동 생성)

### Frontend
- **월간 식단 달력 UI** (`MonthlyDiet.tsx`)  
  달력 형태로 날짜별 식단 시각화, 이전달/다음달 네비게이션

- **식단 자동 생성 버튼**  
  버튼 클릭 시 해당 월 전체 식단 생성 요청 및 결과 즉시 반영

- **재료 창고 대시보드** (`Dashboard.tsx`)  
  카테고리별 메뉴 목록 시각화, 단건 랜덤 식단 생성 및 결과 카드 표시, 총 칼로리 합산

---

## 🚧 개발 예정 기능 (To-Do)

### 핵심 비즈니스 로직 (제약 조건 알고리즘)
현재 순수 랜덤 방식에서 아래 제약 조건을 반영한 알고리즘으로 개선 예정입니다.

```
1. 연속 중복 메뉴 방지
   - 동일 메인 메뉴가 n일 연속 등장하지 않도록 제한

2. 주간 식재료 중복 조건
   - 발주 비용 절감을 위해 동일 주 내 주요 식재료가 3회 이상 겹치도록 유도
   - 메뉴에 식재료 태그 추가 후 주간 단위로 빈도 검증

3. 칼로리 균형
   - 월 전체 평균 칼로리가 설정 범위 내에 들도록 재추첨 로직 적용

4. 부분 수정 기능
   - 자동 생성된 식단에서 특정 날짜의 특정 메뉴만 교체 가능하도록 수정 API 추가
```

### 그 외
- [ ] H2 → MariaDB 전환 및 Docker Compose 환경 구성
- [ ] 메뉴 등록 / 수정 / 삭제 관리자 기능
- [ ] 식단표 PDF/엑셀 출력 기능
- [ ] 사용자별 식단 관리 (다중 사용자 지원)
- [ ] 메뉴에 알레르기 정보, 식재료 태그 추가

---

## 📁 프로젝트 구조

```
nutritionist-radomdiet/
├── src/main/java/com/example/dietRandom/
│   ├── controller/
│   │   ├── DietController.java     # 식단 생성/조회 API
│   │   ├── MenuController.java     # 메뉴 목록 API
│   │   └── UserController.java     # 사용자 API
│   ├── service/
│   │   └── DietService.java        # 핵심 식단 생성 로직
│   ├── domain/
│   │   ├── MealPlan.java           # 날짜별 식단 엔티티
│   │   ├── Menu.java               # 메뉴 엔티티 (카테고리/칼로리)
│   │   └── User.java               # 사용자 엔티티
│   ├── dto/
│   │   └── DietResponse.java       # 식단 응답 DTO
│   ├── repository/                 # Spring Data JPA Repository
│   ├── config/
│   │   └── WebConfig.java          # CORS 설정
│   └── DataInit.java               # 초기 메뉴 데이터 적재
└── diet-front/                     # React + TypeScript 프론트엔드
    └── src/
        ├── MonthlyDiet.tsx         # 월간 달력 식단 뷰
        └── Dashboard.tsx           # 메뉴 현황 + 단건 식단 생성
```

---

## 🔌 API 명세

| Method | URL | 설명 |
|--------|-----|------|
| `POST` | `/api/diets/random?sideCount={n}` | 단건 랜덤 식단 생성 |
| `POST` | `/api/diets/month?year={y}&month={m}&sideCount={n}` | 월간 식단 일괄 생성 |
| `GET`  | `/api/diets/month?year={y}&month={m}` | 월간 식단 조회 |
| `GET`  | `/api/menus` | 전체 메뉴 목록 조회 |

---

## 🚀 로컬 실행 방법

### Backend
```bash
# 프로젝트 루트에서
./gradlew bootRun
# H2 인메모리 DB 자동 실행 및 샘플 데이터 적재
# http://localhost:8080
```

### Frontend
```bash
cd diet-front
npm install
npm run dev
# http://localhost:5173
```

> H2 콘솔: `http://localhost:8080/h2-console`  
> JDBC URL: `jdbc:h2:mem:testdb`

---

## 💡 설계 의도 및 고민

이 프로젝트는 단순 CRUD를 넘어서 **현실의 제약 조건을 코드로 풀어내는 것**에 초점을 맞추고 있습니다.

- 식단 생성은 단순 랜덤이 아닌, 여러 조건을 동시에 만족해야 하는 **제약 최적화 문제**입니다.
- 백엔드에서 제약 조건 검증 로직을 서비스 레이어에 집중시켜, 컨트롤러와 역할을 명확히 분리했습니다.
- 프론트엔드는 기존에 익숙한 jQuery 방식에서 벗어나 **React + TypeScript**로 직접 학습하며 구현했습니다.

---

> 개발자: deokki3  
> 상태: 🚧 진행 중 (In Progress)
