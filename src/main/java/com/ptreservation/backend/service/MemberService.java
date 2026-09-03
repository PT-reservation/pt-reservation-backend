package com.ptreservation.backend.service;

import com.ptreservation.backend.domain.Member;
import com.ptreservation.backend.dto.MemberResponse;
import com.ptreservation.backend.dto.ReservationResponse;
import com.ptreservation.backend.exception.BusinessException;
import com.ptreservation.backend.exception.ErrorCode;
import com.ptreservation.backend.repository.MemberRepository;
import com.ptreservation.backend.repository.ReservationRepository;
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

    public MemberResponse getMyProfile(String email) {
        return MemberResponse.from(getMember(email));
    }

    public List<ReservationResponse> getMyReservations(String email) {
        Member member = getMember(email);
        return reservationRepository.findAllByMemberOrderByReservedAtDesc(member).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    private Member getMember(String email) {
        return memberRepository.getByEmailOrThrow(email);
    }
}