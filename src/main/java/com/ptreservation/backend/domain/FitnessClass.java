package com.ptreservation.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FitnessClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Member trainer;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private LocalDateTime classDateTime;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private int currentCount;

    @Version
    private Long version;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public FitnessClass(Member trainer, String title, LocalDateTime classDateTime, int capacity) {
        this(trainer, title, classDateTime, capacity, null, null);
    }

    public FitnessClass(Member trainer, String title, LocalDateTime classDateTime, int capacity, String description, String imageUrl) {
        this.trainer = trainer;
        this.title = title;
        this.classDateTime = classDateTime;
        this.capacity = capacity;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void reserveSeat() {
        if (this.currentCount >= this.capacity) {
            throw new IllegalStateException("정원이 마감되었습니다.");
        }
        this.currentCount++;
    }

    public void releaseSeat() {
        this.currentCount--;
    }

    public void update(String title, LocalDateTime classDateTime, int capacity, String description, String imageUrl) {
        if (capacity < this.currentCount) {
            throw new IllegalStateException("이미 예약된 인원보다 정원을 적게 설정할 수 없습니다.");
        }
        this.title = title;
        this.classDateTime = classDateTime;
        this.capacity = capacity;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}