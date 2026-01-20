// src/main.tsx
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Route, Routes } from 'react-router-dom'; // 1. 라우터 도구들 import
import App from './App.tsx'
import Dashboard from './Dashboard.tsx'
import './index.css'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    {/* 2. BrowserRouter가 전체 앱을 감싸야 페이지 이동이 가능합니다. */}
    <BrowserRouter>
      <Routes>
        {/* 3. URL 매핑 정의 */}
        {/* http://localhost:5173/ -> 로그인 화면(App) */}
        <Route path="/" element={<App />} />
        
        {/* http://localhost:5173/dashboard -> 대시보드 화면 */}
        <Route path="/dashboard" element={<Dashboard />} />
      </Routes>
    </BrowserRouter>
  </StrictMode>,
)