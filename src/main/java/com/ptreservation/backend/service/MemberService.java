package com.ptreservation.backend.service;

import com.ptreservation.backend.domain.Member;
import com.ptreservation.backend.domain.Reservation;
import com.ptreservation.backend.dto.MemberResponse;
import com.ptreservation.backend.dto.ReservationResponse;
import com.ptreservation.backend.exception.BusinessException;
import com.ptreservation.backend.exception.ErrorCode;
import com.ptreservation.backend.repository.FitnessClassRepository;
import com.ptreservation.backend.repository.MemberRepository;
import com.ptreservation.backend.repository.ReservationRepository;
import com.ptreservation.backend.repository.SessionTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final SessionTicketRepository sessionTicketRepository;
    private final FitnessClassRepository fitnessClassRepository;
    private final ReservationTransactionService reservationTransactionService;

    public MemberResponse getMyProfile(String email) {
        return MemberResponse.from(getMember(email));
    }

    public List<ReservationResponse> getMyReservations(String email) {
        Member member = getMember(email);
        return reservationRepository.findAllByMemberOrderByReservedAtDesc(member).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public void deleteAccount(String email) {
        Member member = getMember(email);

        if (member.getRole() == Member.Role.TRAINER) {
            if (fitnessClassRepository.existsByTrainer(member)) {
                throw new BusinessException(ErrorCode.TRAINER_HAS_CLASSES);
            }
            memberRepository.delete(member);
            return;
        }

        for (Reservation reservation : reservationRepository.findAllByMemberOrderByReservedAtDesc(member)) {
            if (reservation.getStatus() != Reservation.Status.CANCELLED) {
                reservationTransactionService.cancel(email, reservation.getId());
            }
        }
        reservationRepository.deleteAll(reservationRepository.findAllByMemberOrderByReservedAtDesc(member));
        sessionTicketRepository.findByMember(member).ifPresent(sessionTicketRepository::delete);
        memberRepository.delete(member);
    }

    private Member getMember(String email) {
        return memberRepository.getByEmailOrThrow(email);
    }
}