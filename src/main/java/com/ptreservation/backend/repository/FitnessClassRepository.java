package com.ptreservation.backend.repository;

import com.ptreservation.backend.domain.FitnessClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FitnessClassRepository extends JpaRepository<FitnessClass, Long> {
}