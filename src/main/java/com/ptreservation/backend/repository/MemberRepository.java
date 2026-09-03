package com.ptreservation.backend.repository;

import com.ptreservation.backend.domain.Member;
import com.ptreservation.backend.exception.BusinessException;
import com.ptreservation.backend.exception.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);

    default Member getByEmailOrThrow(String email) {
        return findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }
}