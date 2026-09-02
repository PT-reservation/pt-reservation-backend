package com.ptreservation.backend.repository;

import com.ptreservation.backend.domain.FitnessClass;
import com.ptreservation.backend.domain.Member;
import com.ptreservation.backend.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByMemberOrderByReservedAtDesc(Member member);

    List<Reservation> findAllByFitnessClass(FitnessClass fitnessClass);

    boolean existsByFitnessClassAndMemberAndStatusIn(
            FitnessClass fitnessClass, Member member, List<Reservation.Status> statuses);

    List<Reservation> findAllByFitnessClassAndStatusOrderByReservedAtAsc(
            FitnessClass fitnessClass, Reservation.Status status);
}