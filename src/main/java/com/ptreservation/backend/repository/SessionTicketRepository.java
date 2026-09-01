package com.ptreservation.backend.repository;

import com.ptreservation.backend.domain.Member;
import com.ptreservation.backend.domain.SessionTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionTicketRepository extends JpaRepository<SessionTicket, Long> {
    Optional<SessionTicket> findByMember(Member member);
}