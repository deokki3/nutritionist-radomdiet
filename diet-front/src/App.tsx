import axios from 'axios'; // 1. 아까 설치한 통신 도구 import
import { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // 1. 이동 도구 import
import './App.css';

// [TS 전용] Java의 DTO 클래스 선언과 같습니다.
// JS에서는 이런 게 없고 그냥 나중에 { username: ... } 이렇게 썼었죠.
interface LoginFormData {
  username: string;
  password: string;
}

function App() {
  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");

  const navigate = useNavigate(); // 2. 네비게이션 객체 생성

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();

    const loginData: LoginFormData = {
      username: username,
      password: password
    };


    // ⛔ [추가된 부분] 유효성 검사 (Validation)
    if (!username.trim() || !password.trim()) {
      alert("아이디와 비밀번호를 모두 입력해주세요!");
      return; // 함수 강제 종료 (서버 요청 안 보냄)
    }

    // 2. [AJAX 요청] 백엔드(8080)로 데이터 발사!
    // axios.post("주소", 보낼데이터)
    axios.post("http://localhost:8080/api/users/login", loginData)
      .then((response) => {
        // [방어 코드 추가] 
        // 상태코드는 200인데, 혹시나 메시지 내용에 "실패"나 "틀렸" 같은 말이 있으면 막아버리기
        // (원래는 상태코드로 하는 게 정석이지만, 확실하게 하기 위해 추가합니다)
        if (typeof response.data === 'string' && response.data.includes("틀렸")) {
           alert("로그인 실패: " + response.data);
           return; // 대시보드로 이동 안 함!
        }

        console.log("로그인 성공:", response.data);
        navigate("/dashboard", { state: { username: username } });
      }) 
      
      .catch((error) => {
        // 실패했을 때 (에러 발생)
        console.error("에러 발생:", error);
        alert("로그인 실패! 아이디/비번을 확인하세요.");
      });
  }

  return (
    <div style={{ padding: "50px", textAlign: "center" }}>
      <h1>🥗 로그인</h1>
      <form onSubmit={handleLogin} style={{ display: "inline-block", textAlign: "left" }}>
        
        <div style={{ marginBottom: "10px" }}>
          <label>아이디: </label>
          <input 
            type="text" 
            value={username}
            // [이벤트 핸들러: 인풋 변경]
            // JS: onChange={(e) => setUsername(e.target.value)}
            // TS: e가 'HTML Input 요소의 변경 이벤트'임을 명시합니다.
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => setUsername(e.target.value)} 
            placeholder="아이디 입력"
          />
        </div>

        <div style={{ marginBottom: "10px" }}>
          <label>비밀번호: </label>
          <input 
            type="password" 
            value={password}
            // 여기도 똑같이 ChangeEvent 입니다.
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => setPassword(e.target.value)}
            placeholder="비밀번호 입력"
          />
        </div>

        <button type="submit" style={{ width: "100%", padding: "10px", backgroundColor: "#007BFF", color: "white", border: "none" }}>
          로그인 하기
        </button>
      </form>
    </div>
  )
}

export default App