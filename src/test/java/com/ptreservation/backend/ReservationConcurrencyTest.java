package com.ptreservation.backend;

import com.ptreservation.backend.domain.FitnessClass;
import com.ptreservation.backend.domain.Member;
import com.ptreservation.backend.domain.Reservation;
import com.ptreservation.backend.domain.SessionTicket;
import com.ptreservation.backend.repository.FitnessClassRepository;
import com.ptreservation.backend.repository.MemberRepository;
import com.ptreservation.backend.repository.ReservationRepository;
import com.ptreservation.backend.repository.SessionTicketRepository;
import com.ptreservation.backend.service.ReservationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReservationConcurrencyTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private FitnessClassRepository fitnessClassRepository;
    @Autowired
    private SessionTicketRepository sessionTicketRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    private static final int CAPACITY = 5;
    private static final int MEMBER_COUNT = 20;

    private FitnessClass fitnessClass;
    private List<Member> members;

    @BeforeEach
    void setUp() {
        Member trainer = memberRepository.save(
                new Member("trainer@test.com", "encoded", "트레이너", Member.Role.TRAINER));

        fitnessClass = fitnessClassRepository.save(
                new FitnessClass(trainer, "동시성 테스트 클래스", LocalDateTime.now().plusDays(1), CAPACITY));

        members = new ArrayList<>();
        for (int i = 0; i < MEMBER_COUNT; i++) {
            Member member = memberRepository.save(
                    new Member("member" + i + "@test.com", "encoded", "회원" + i, Member.Role.MEMBER));
            sessionTicketRepository.save(new SessionTicket(member, 10));
            members.add(member);
        }
    }

    @Test
    void 동시에_정원보다_많은_인원이_예약해도_정원만큼만_확정된다() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(MEMBER_COUNT);
        CountDownLatch readyLatch = new CountDownLatch(MEMBER_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(MEMBER_COUNT);
        AtomicInteger failureCount = new AtomicInteger();

        for (Member member : members) {
            executorService.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    reservationService.reserve(member.getEmail(), fitnessClass.getId());
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executorService.shutdown();

        List<Reservation> reservations = reservationRepository.findAllByFitnessClass(fitnessClass);
        long confirmedCount = reservations.stream()
                .filter(r -> r.getStatus() == Reservation.Status.CONFIRMED)
                .count();
        long waitlistedCount = reservations.stream()
                .filter(r -> r.getStatus() == Reservation.Status.WAITLISTED)
                .count();

        System.out.println("실패한 요청 수: " + failureCount.get());
        System.out.println("확정: " + confirmedCount + ", 대기: " + waitlistedCount + ", 총 예약 수: " + reservations.size());
        System.out.println("최종 currentCount: " + fitnessClassRepository.findById(fitnessClass.getId()).orElseThrow().getCurrentCount());

        assertThat(confirmedCount).isEqualTo(CAPACITY);
        assertThat(waitlistedCount).isEqualTo(MEMBER_COUNT - CAPACITY);

        FitnessClass result = fitnessClassRepository.findById(fitnessClass.getId()).orElseThrow();
        assertThat(result.getCurrentCount()).isEqualTo(CAPACITY);
    }

    @AfterEach
    void tearDown() {
        reservationRepository.deleteAll(reservationRepository.findAllByFitnessClass(fitnessClass));
        sessionTicketRepository.deleteAll(members.stream()
                .map(m -> sessionTicketRepository.findByMember(m).orElseThrow())
                .toList());
        fitnessClassRepository.deleteById(fitnessClass.getId());
        memberRepository.deleteAll(members);
        memberRepository.findByEmail("trainer@test.com").ifPresent(memberRepository::delete);
    }
}