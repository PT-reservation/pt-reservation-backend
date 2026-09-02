package com.ptreservation.backend.config;

import com.ptreservation.backend.domain.FitnessClass;
import com.ptreservation.backend.domain.Member;
import com.ptreservation.backend.domain.SessionTicket;
import com.ptreservation.backend.repository.FitnessClassRepository;
import com.ptreservation.backend.repository.MemberRepository;
import com.ptreservation.backend.repository.SessionTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Profile("local")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final FitnessClassRepository fitnessClassRepository;
    private final SessionTicketRepository sessionTicketRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (memberRepository.count() > 0) {
            return;
        }

        Member trainer = memberRepository.save(
                new Member("trainer@test.com", passwordEncoder.encode("password123"), "김트레이너", Member.Role.TRAINER));

        for (int i = 1; i <= 4; i++) {
            Member member = memberRepository.save(
                    new Member("member" + i + "@test.com", passwordEncoder.encode("password123"), "회원" + i, Member.Role.MEMBER));
            sessionTicketRepository.save(new SessionTicket(member, 10));
        }

        fitnessClassRepository.save(new FitnessClass(
                trainer, "정원 테스트용 클래스 (정원 2명)", LocalDateTime.now().plusDays(1).withHour(19).withMinute(0), 2));
        fitnessClassRepository.save(new FitnessClass(
                trainer, "상체 집중 그룹 PT", LocalDateTime.now().plusDays(2).withHour(20).withMinute(0), 4));
        fitnessClassRepository.save(new FitnessClass(
                trainer, "코어 강화 클래스", LocalDateTime.now().plusDays(3).withHour(18).withMinute(30), 8));
    }
}