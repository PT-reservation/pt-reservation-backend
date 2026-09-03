package com.ptreservation.backend.service;

import com.ptreservation.backend.domain.FitnessClass;
import com.ptreservation.backend.domain.Member;
import com.ptreservation.backend.domain.Reservation;
import com.ptreservation.backend.domain.SessionTicket;
import com.ptreservation.backend.dto.ReservationResponse;
import com.ptreservation.backend.exception.BusinessException;
import com.ptreservation.backend.exception.ErrorCode;
import com.ptreservation.backend.repository.FitnessClassRepository;
import com.ptreservation.backend.repository.MemberRepository;
import com.ptreservation.backend.repository.ReservationRepository;
import com.ptreservation.backend.repository.SessionTicketRepository;
import com.ptreservation.backend.sse.PromotionSkippedEvent;
import com.ptreservation.backend.sse.ReservationPromotedEvent;
import com.ptreservation.backend.sse.SeatUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationTransactionService {

    private final FitnessClassRepository fitnessClassRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final SessionTicketRepository sessionTicketRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ReservationResponse reserve(String memberEmail, Long classId) {
        Member member = getMember(memberEmail);
        FitnessClass fitnessClass = getFitnessClass(classId);

        if (reservationRepository.existsByFitnessClassAndMemberAndStatusIn(
                fitnessClass, member, List.of(Reservation.Status.CONFIRMED, Reservation.Status.WAITLISTED))) {
            throw new BusinessException(ErrorCode.ALREADY_RESERVED);
        }

        SessionTicket ticket = getTicket(member);
        if (ticket.getRemainingCount() <= 0) {
            throw new BusinessException(ErrorCode.NO_TICKET);
        }

        Reservation.Status status;
        try {
            fitnessClass.reserveSeat();
            status = Reservation.Status.CONFIRMED;
        } catch (IllegalStateException e) {
            status = Reservation.Status.WAITLISTED;
        }

        fitnessClassRepository.saveAndFlush(fitnessClass);

        Reservation reservation = new Reservation(fitnessClass, member, status);
        reservationRepository.save(reservation);

        if (status == Reservation.Status.CONFIRMED) {
            ticket.use();
            eventPublisher.publishEvent(new SeatUpdatedEvent(classId, fitnessClass.getCurrentCount(), fitnessClass.getCapacity()));
        }

        return ReservationResponse.from(reservation);
    }

    @Transactional
    public void cancel(String memberEmail, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getMember().getEmail().equals(memberEmail)) {
            throw new BusinessException(ErrorCode.NOT_RESERVATION_OWNER);
        }

        if (reservation.getStatus() == Reservation.Status.CANCELLED) {
            throw new BusinessException(ErrorCode.ALREADY_CANCELLED);
        }

        Reservation.Status previousStatus = reservation.getStatus();
        reservation.cancel();

        if (previousStatus == Reservation.Status.CONFIRMED) {
            FitnessClass fitnessClass = reservation.getFitnessClass();
            fitnessClass.releaseSeat();
            getTicket(reservation.getMember()).refund();

            promoteNextWaiting(fitnessClass);
            eventPublisher.publishEvent(new SeatUpdatedEvent(
                    fitnessClass.getId(), fitnessClass.getCurrentCount(), fitnessClass.getCapacity()));
        }
    }

    private void promoteNextWaiting(FitnessClass fitnessClass) {
        List<Reservation> waitlist = reservationRepository.findAllByFitnessClassAndStatusOrderByReservedAtAsc(
                fitnessClass, Reservation.Status.WAITLISTED);

        for (Reservation candidate : waitlist) {
            SessionTicket ticket = getTicket(candidate.getMember());

            if (ticket.getRemainingCount() <= 0) {
                eventPublisher.publishEvent(
                        new PromotionSkippedEvent(candidate.getMember().getEmail(), fitnessClass.getId()));
                continue;
            }

            fitnessClass.reserveSeat();
            candidate.confirm();
            ticket.use();
            eventPublisher.publishEvent(
                    new ReservationPromotedEvent(candidate.getMember().getEmail(), fitnessClass.getId()));
            return;
        }
    }

    private Member getMember(String email) {
        return memberRepository.getByEmailOrThrow(email);
    }

    private FitnessClass getFitnessClass(Long classId) {
        return fitnessClassRepository.findById(classId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_NOT_FOUND));
    }

    private SessionTicket getTicket(Member member) {
        return sessionTicketRepository.findByMember(member)
                .orElseThrow(() -> new BusinessException(ErrorCode.NO_TICKET));
    }
}