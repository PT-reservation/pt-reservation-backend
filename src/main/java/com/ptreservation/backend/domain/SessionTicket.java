package com.ptreservation.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private int totalCount;

    @Column(nullable = false)
    private int remainingCount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    public SessionTicket(Member member, int totalCount) {
        this.member = member;
        this.totalCount = totalCount;
        this.remainingCount = totalCount;
    }

    @PrePersist
    protected void onCreate() {
        this.issuedAt = LocalDateTime.now();
    }

    public void use() {
        if (this.remainingCount <= 0) {
            throw new IllegalStateException("잔여 세션권이 없습니다.");
        }
        this.remainingCount--;
    }

    public void refund() {
        this.remainingCount++;
    }

    public void charge(int count) {
        this.totalCount += count;
        this.remainingCount += count;
    }
}