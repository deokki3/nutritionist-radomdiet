import axios from 'axios';
import { useEffect, useState } from 'react';
import './DietCalendar.css'; // 스타일 import

// DB 데이터 타입 정의
interface Menu {
  id: number;
  name: string;
  category: string;
}

interface MealPlan {
  id: number;
  date: string; // "2026-05-01"
  rice: Menu;
  soup: Menu;
  main: Menu;
  kimchi: Menu;
  sideDishes: Menu[];
}

function MonthlyDiet() {
  const [currentDate, setCurrentDate] = useState(new Date(2026, 4, 1)); // 2026년 5월 (월은 0부터 시작, 4=5월)
  const [mealPlans, setMealPlans] = useState<MealPlan[]>([]);
  const [loading, setLoading] = useState(false);

  // 년, 월 추출 편의 변수
  const year = currentDate.getFullYear();
  const month = currentDate.getMonth() + 1; // 표시는 1 더해서

  useEffect(() => {
    fetchMonthlyData();
  }, [currentDate]); // 날짜(달)가 바뀌면 다시 조회

  // 1. 데이터 가져오기
  const fetchMonthlyData = () => {
    setLoading(true);
    axios.get(`http://localhost:8080/api/diets/month?year=${year}&month=${month}`)
      .then(res => {
        setMealPlans(res.data);
        setLoading(false);
      })
      .catch(err => {
        console.error(err);
        setLoading(false);
      });
  };

  // 2. 식단 생성 요청 (재생성)
  const handleGenerate = () => {
    if (!window.confirm(`${month}월 식단을 새로 생성하시겠습니까?\n기존 데이터는 삭제됩니다.`)) return;

    setLoading(true);
    axios.post(`http://localhost:8080/api/diets/month?year=${year}&month=${month}&sideCount=3`)
      .then(res => {
        setMealPlans(res.data); // 결과 바로 반영
        setLoading(false);
        alert("식단 생성이 완료되었습니다!");
      });
  };

  // 3. 달력 그리기 로직 (핵심!)
  const renderCalendarDays = () => {
    const daysInMonth = new Date(year, month, 0).getDate(); // 이번 달 며칠까지 있는지 (30? 31?)
    const firstDayOfWeek = new Date(year, month - 1, 1).getDay(); // 1일이 무슨 요일인지 (0:일, 1:월 ...)
    
    const days = [];

    // [빈 칸 채우기] 1일이 수요일이면, 일/월/화 3칸은 비워야 함
    for (let i = 0; i < firstDayOfWeek; i++) {
      days.push(<div key={`empty-${i}`} className="day-cell empty" style={{ background: "#f9f9f9" }}></div>);
    }

    // [날짜 채우기] 1일 ~ 31일
    for (let day = 1; day <= daysInMonth; day++) {
      // 날짜 포맷 맞추기 (2026-05-01 처럼)
      const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
      
      // 이 날짜에 해당하는 식단 찾기 (Array.find)
      const plan = mealPlans.find(p => p.date === dateStr);

      days.push(
        <div key={day} className="day-cell">
          <span className="day-number">{day}</span>
          {/* 식단이 있으면 뿌려주기 */}
          {plan ? (
            <div className="menu-list">
              <div className="menu-item type-MAIN">{plan.main?.name}</div>
              <div className="menu-item type-SOUP">{plan.soup?.name}</div>
              {plan.sideDishes.map((side, idx) => (
                 <div key={idx} className="menu-item type-SIDE">{side.name}</div>
              ))}
              <div className="menu-item type-RICE">{plan.rice?.name}</div>
              <div className="menu-item type-KIMCHI">{plan.kimchi?.name}</div>
            </div>
          ) : (
            <div style={{ fontSize: "12px", color: "#ccc", marginTop: "10px" }}>식단 없음</div>
          )}
        </div>
      );
    }
    return days;
  };

  return (
    <div className="calendar-container">
      {/* 상단 컨트롤러 */}
      <div className="calendar-header">
        <div>
          <button onClick={() => setCurrentDate(new Date(year, month - 2, 1))}>◀ 이전달</button>
          <span style={{ fontSize: "24px", fontWeight: "bold", margin: "0 20px" }}>
            {year}년 {month}월 식단표
          </span>
          <button onClick={() => setCurrentDate(new Date(year, month, 1))}>다음달 ▶</button>
        </div>
        
        <div>
           <button 
             onClick={handleGenerate}
             style={{ background: "#6f42c1", color: "white", border: "none", padding: "10px 20px", borderRadius: "5px", cursor: "pointer" }}
           >
             ✨ {month}월 전체 자동 생성
           </button>
        </div>
      </div>

      {/* 요일 헤더 */}
      <div className="day-names">
        <div style={{color: "red"}}>일</div>
        <div>월</div>
        <div>화</div>
        <div>수</div>
        <div>목</div>
        <div>금</div>
        <div style={{color: "blue"}}>토</div>
      </div>

      {/* 날짜 그리드 */}
      <div className="calendar-grid">
        {loading ? <div style={{ padding: "50px", gridColumn: "1 / -1", textAlign: "center" }}>로딩중...</div> : renderCalendarDays()}
      </div>
    </div>
  );
}

export default MonthlyDiet;