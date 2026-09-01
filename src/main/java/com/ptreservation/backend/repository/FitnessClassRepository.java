package com.ptreservation.backend.repository;

import com.ptreservation.backend.domain.FitnessClass;
import com.ptreservation.backend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FitnessClassRepository extends JpaRepository<FitnessClass, Long> {
    List<FitnessClass> findAllByTrainer(Member trainer);
}