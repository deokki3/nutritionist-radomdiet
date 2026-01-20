package com.example.dietRandom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dietRandom.domain.User;

// <User, Long> : User 테이블을 관리하고, PK(ID)는 Long 타입이다.
public interface UserRepository extends JpaRepository<User, Long> {
    // 아무것도 안 적어도 됩니다! 
    // JpaRepository를 상속받는 순간, 저장/조회/삭제 기능이 자동으로 생깁니다.

    // 🔍 추가된 기능: 아이디(username)로 회원 정보를 찾아서 갖다줘!
    // Optional은 "찾았는데 없을 수도 있다"는 걸 처리하는 안전한 상자입니다.
    Optional<User> findByUsername(String username);
    
}