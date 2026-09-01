package com.ptreservation.backend.service;

import com.ptreservation.backend.dto.ReservationResponse;
import com.ptreservation.backend.exception.BusinessException;
import com.ptreservation.backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final int MAX_RETRY = 30;

    private final ReservationTransactionService reservationTransactionService;

    public ReservationResponse reserve(String memberEmail, Long classId) {
        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {
                return reservationTransactionService.reserve(memberEmail, classId);
            } catch (ConcurrencyFailureException e) {
                // 낙관적 락 충돌 - 재시도
            }
        }
        throw new BusinessException(ErrorCode.RESERVATION_CONFLICT);
    }

    public void cancel(String memberEmail, Long reservationId) {
        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {
                reservationTransactionService.cancel(memberEmail, reservationId);
                return;
            } catch (ConcurrencyFailureException e) {
                // 낙관적 락 충돌 - 재시도
            }
        }
        throw new BusinessException(ErrorCode.RESERVATION_CONFLICT);
    }
}