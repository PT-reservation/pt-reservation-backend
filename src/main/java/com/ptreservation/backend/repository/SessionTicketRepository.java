package com.ptreservation.backend.repository;

import com.ptreservation.backend.domain.SessionTicket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionTicketRepository extends JpaRepository<SessionTicket, Long> {
}