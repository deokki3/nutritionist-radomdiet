package com.example.dietRandom.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter @Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    // --- ⏰ 시간 기록 담당 (Auditing) ---

    @Column(updatable = false) // 생성일은 수정되면 안 됨
    private LocalDateTime createdAt;

    private LocalDateTime lastUpdatedAt; // 마지막 수정일

    // 1. 저장되기 직전에 실행 (Insert 전)
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.lastUpdatedAt = LocalDateTime.now(); // 처음엔 생성일 = 수정일
    }

    // 2. 수정되기 직전에 실행 (Update 전)
    @PreUpdate
    public void onUpdate() {
        this.lastUpdatedAt = LocalDateTime.now(); // 수정할 때마다 현재 시간으로 갱신
    }
}