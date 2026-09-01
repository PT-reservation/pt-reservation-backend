package com.ptreservation.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private FitnessClass fitnessClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime reservedAt;

    private LocalDateTime cancelledAt;

    public Reservation(FitnessClass fitnessClass, Member member, Status status) {
        this.fitnessClass = fitnessClass;
        this.member = member;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        this.reservedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = Status.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    public void confirm() {
        this.status = Status.CONFIRMED;
    }

    public enum Status {
        CONFIRMED, WAITLISTED, CANCELLED
    }
}