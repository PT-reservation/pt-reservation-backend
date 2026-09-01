package com.ptreservation.backend.service;

import com.ptreservation.backend.domain.Member;
import com.ptreservation.backend.domain.SessionTicket;
import com.ptreservation.backend.dto.TicketResponse;
import com.ptreservation.backend.exception.BusinessException;
import com.ptreservation.backend.exception.ErrorCode;
import com.ptreservation.backend.repository.MemberRepository;
import com.ptreservation.backend.repository.SessionTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionTicketService {

    private final SessionTicketRepository sessionTicketRepository;
    private final MemberRepository memberRepository;

    public TicketResponse getMyTicket(String email) {
        Member member = getMember(email);
        SessionTicket ticket = sessionTicketRepository.findByMember(member)
                .orElseThrow(() -> new BusinessException(ErrorCode.NO_TICKET));
        return TicketResponse.from(ticket);
    }

    @Transactional
    public TicketResponse charge(String email, int count) {
        Member member = getMember(email);
        SessionTicket ticket = sessionTicketRepository.findByMember(member)
                .orElseGet(() -> sessionTicketRepository.save(new SessionTicket(member, 0)));

        ticket.charge(count);
        return TicketResponse.from(ticket);
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }
}