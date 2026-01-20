package com.example.dietRandom.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dietRandom.domain.User;
import com.example.dietRandom.dto.LoginRequest;
import com.example.dietRandom.dto.SignupRequest;
import com.example.dietRandom.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) {
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setCreatedAt(LocalDateTime.now());

        // 근무지 관련 설정(setWorkplace...) 삭제 완료!

        userRepository.save(user);

        return "회원가입 성공! 환영합니다, " + user.getName() + "님!";
    }



    // 주소: POST http://localhost:8080/api/users/login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        
        // 1. 아이디로 회원을 찾아봅니다.
        // findByUsername이 Optional 상자를 주니까, .orElse(null)로 꺼냅니다. (없으면 null)
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);

        // ⚠️ user가 없거나(null) 비번이 틀리면 -> 401 에러 발사
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 틀렸습니다.");
        }

        // 성공 시 -> 200 OK
        return ResponseEntity.ok("로그인 성공!");
    }
}
