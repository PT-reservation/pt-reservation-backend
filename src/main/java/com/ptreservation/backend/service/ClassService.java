package com.ptreservation.backend.service;

import com.ptreservation.backend.domain.FitnessClass;
import com.ptreservation.backend.domain.Member;
import com.ptreservation.backend.dto.ClassRequest;
import com.ptreservation.backend.dto.ClassResponse;
import com.ptreservation.backend.exception.BusinessException;
import com.ptreservation.backend.exception.ErrorCode;
import com.ptreservation.backend.repository.FitnessClassRepository;
import com.ptreservation.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassService {

    private final FitnessClassRepository fitnessClassRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ClassResponse createClass(String trainerEmail, ClassRequest request) {
        Member trainer = getTrainer(trainerEmail);

        FitnessClass fitnessClass = new FitnessClass(
                trainer,
                request.title(),
                request.classDateTime(),
                request.capacity(),
                request.description(),
                request.imageUrl()
        );

        fitnessClassRepository.save(fitnessClass);
        return ClassResponse.from(fitnessClass);
    }

    @Transactional
    public ClassResponse updateClass(String trainerEmail, Long classId, ClassRequest request) {
        FitnessClass fitnessClass = getOwnedClass(trainerEmail, classId);
        fitnessClass.update(
                request.title(),
                request.classDateTime(),
                request.capacity(),
                request.description(),
                request.imageUrl()
        );
        return ClassResponse.from(fitnessClass);
    }

    @Transactional
    public void deleteClass(String trainerEmail, Long classId) {
        FitnessClass fitnessClass = getOwnedClass(trainerEmail, classId);
        fitnessClassRepository.delete(fitnessClass);
    }

    public List<ClassResponse> getClasses() {
        return fitnessClassRepository.findAll().stream()
                .map(ClassResponse::from)
                .toList();
    }

    public ClassResponse getClass(Long classId) {
        FitnessClass fitnessClass = fitnessClassRepository.findById(classId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_NOT_FOUND));
        return ClassResponse.from(fitnessClass);
    }

    public List<ClassResponse> getMyClasses(String trainerEmail) {
        Member trainer = getTrainer(trainerEmail);
        return fitnessClassRepository.findAllByTrainer(trainer).stream()
                .map(ClassResponse::from)
                .toList();
    }

    private Member getTrainer(String email) {
        return memberRepository.getByEmailOrThrow(email);
    }

    private FitnessClass getOwnedClass(String trainerEmail, Long classId) {
        FitnessClass fitnessClass = fitnessClassRepository.findById(classId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_NOT_FOUND));

        if (!fitnessClass.getTrainer().getEmail().equals(trainerEmail)) {
            throw new BusinessException(ErrorCode.NOT_CLASS_OWNER);
        }

        return fitnessClass;
    }
}