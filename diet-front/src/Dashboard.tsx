import axios from 'axios';
import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

// 1. [DTO 정의] 백엔드의 Menu.java와 똑같이 맞춥니다.
interface Menu {
  id: number;
  name: string;
  category: string;
  calories: number;
}

// [추가] 생성된 식단 응답 타입 (DTO와 일치)
interface DietResponse {
  rice: Menu;
  soup: Menu;
  main: Menu;
  sides: Menu[];
  kimchi: Menu;
  totalCalories: number;
}

function Dashboard() {
  const navigate = useNavigate();
  const location = useLocation();
  const userInfo = location.state?.username || "Guest";

  // 2. [State] 메뉴 목록을 담을 그릇 (초기값은 빈 배열 [])
  const [menus, setMenus] = useState<Menu[]>([]);

  // [추가] 생성된 식단을 담을 State
  const [generatedDiet, setGeneratedDiet] = useState<DietResponse | null>(null);
  const [sideCount, setSideCount] = useState<number>(2); // 기본 반찬 2개

  // 3. [초기 로딩] JSP의 $(document).ready() 와 같습니다.
  // 페이지가 처음 뜰 때 딱 한 번 실행됩니다.
  useEffect(() => {
    fetchMenus();
  }, []);

  const fetchMenus = () => {
    axios.get("http://localhost:8080/api/menus")
      .then((res) => {
        console.log("메뉴 가져오기 성공:", res.data);
        setMenus(res.data); // 받아온 데이터를 State에 넣음 -> 화면 자동 갱신
      })
      .catch((err) => {
        console.error("메뉴 로딩 실패:", err);
      });
  };

    const handleGenerate = () => {
    // 쿼리 스트링으로 반찬 갯수 전달
    axios.post(`http://localhost:8080/api/diets/random?sideCount=${sideCount}`)
      .then(res => {
        setGeneratedDiet(res.data); // 결과 받아서 모달 띄우기
      })
      .catch(err => alert("식단 생성 실패!"));
  }

  // 4. [Helper] 카테고리별로 필터링하는 함수
  const getByCategory = (cat: string) => menus.filter(m => m.category === cat);



 return (
    <div style={{ padding: "40px", maxWidth: "1200px", margin: "0 auto" }}>
      {/* 상단 헤더 & 컨트롤 */}
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "30px" }}>
        <h1>🍱 {userInfo}님의 재료 창고</h1>
        
        {/* 생성 컨트롤 박스 */}
        <div style={{ display: "flex", gap: "10px", alignItems: "center", background: "#f8f9fa", padding: "10px", borderRadius: "8px" }}>
          <label>반찬 갯수:</label>
          <input 
            type="number" min="1" max="5" 
            value={sideCount} onChange={(e) => setSideCount(Number(e.target.value))}
            style={{ width: "50px", padding: "5px" }}
          />
          <button 
            onClick={handleGenerate}
            style={{ background: "#6f42c1", color: "white", border: "none", padding: "10px 20px", borderRadius: "5px", cursor: "pointer", fontWeight: "bold" }}
          >
            ✨ 랜덤 식단 생성!
          </button>
        </div>

        <button onClick={() => navigate("/")} style={{ background: "#ff6b6b", color: "white", border: "none", padding: "10px", borderRadius: "5px" }}>
          로그아웃
        </button>
      </div>

      {/* [결과 모달 영역] 생성된 식단이 있을 때만 표시 */}
      {generatedDiet && (
        <div style={{ 
          marginBottom: "30px", border: "3px solid #6f42c1", borderRadius: "15px", padding: "20px", 
          backgroundColor: "#f3e5f5", textAlign: "center", animation: "fadeIn 0.5s" 
        }}>
          <h2 style={{ color: "#6f42c1", margin: "0 0 20px 0" }}>🎉 오늘의 추천 식단</h2>
          <div style={{ display: "flex", justifyContent: "center", gap: "15px", flexWrap: "wrap", alignItems: "center" }}>
            <DietCard menu={generatedDiet.rice} type="밥" />
            <DietCard menu={generatedDiet.soup} type="국" />
            <DietCard menu={generatedDiet.main} type="메인" />
            {generatedDiet.sides.map((side, idx) => (
              <DietCard key={idx} menu={side} type={`반찬${idx+1}`} />
            ))}
            <DietCard menu={generatedDiet.kimchi} type="김치" />
          </div>
          <h3 style={{ marginTop: "20px" }}>총 칼로리: {generatedDiet.totalCalories} kcal</h3>
          
          <button 
            onClick={() => setGeneratedDiet(null)} // 닫기
            style={{ marginTop: "10px", background: "#888", color: "white", border: "none", padding: "5px 15px", borderRadius: "5px", cursor: "pointer" }}
          >
            닫기
          </button>
        </div>
      )}

      {/* 하단 재료 목록 (기존 코드) */}
      <div style={{ display: "grid", gridTemplateColumns: "repeat(5, 1fr)", gap: "20px", opacity: generatedDiet ? 0.5 : 1 }}>
        <MenuColumn title="🍚 밥" items={getByCategory("RICE")} color="#fff3cd" />
        <MenuColumn title="🍲 국/찌개" items={getByCategory("SOUP")} color="#d1ecf1" />
        <MenuColumn title="🍗 메인" items={getByCategory("MAIN")} color="#f8d7da" />
        <MenuColumn title="🥗 반찬" items={getByCategory("SIDE")} color="#d4edda" />
        <MenuColumn title="🌶️ 김치" items={getByCategory("KIMCHI")} color="#fce5cd" />
      </div>
    </div>
  );
}


// 식단 카드 컴포넌트
function DietCard({ menu, type }: { menu: Menu, type: string }) {
  if (!menu) return <div style={{ width: "100px", height: "100px", background: "#ddd", display: "flex", alignItems: "center", justifyContent: "center", borderRadius: "50%" }}>없음</div>;
  
  return (
    <div style={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
      <div style={{ 
        width: "120px", height: "120px", borderRadius: "50%", background: "white", 
        display: "flex", alignItems: "center", justifyContent: "center", 
        boxShadow: "0 4px 6px rgba(0,0,0,0.1)", border: "2px solid #eee",
        fontWeight: "bold", fontSize: "16px", padding: "10px", wordBreak: "keep-all"
      }}>
        {menu.name}
      </div>
      <span style={{ marginTop: "5px", fontSize: "14px", fontWeight: "bold", color: "#555" }}>{type}</span>
    </div>
  )
}

// MenuColumn은 기존과 동일... (아래에 그대로 두시면 됩니다)
function MenuColumn({ title, items, color }: { title: string, items: Menu[], color: string }) {
    return (
      <div style={{ background: color, padding: "15px", borderRadius: "10px", minHeight: "300px" }}>
        <h3 style={{ borderBottom: "2px solid rgba(0,0,0,0.1)", paddingBottom: "10px", textAlign: "center" }}>{title}</h3>
        <ul style={{ listStyle: "none", padding: 0 }}>
          {items.map((menu) => (
            <li key={menu.id} style={{ background: "white", margin: "5px 0", padding: "8px", borderRadius: "5px", boxShadow: "0 2px 2px rgba(0,0,0,0.1)" }}>
              <strong>{menu.name}</strong> <span style={{ fontSize: "12px", color: "#888" }}>({menu.calories}kcal)</span>
            </li>
          ))}
          {items.length === 0 && <li style={{ color: "#888", textAlign: "center", fontSize: "12px" }}>등록된 메뉴 없음</li>}
        </ul>
      </div>
    );
  }

export default Dashboard;