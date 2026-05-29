package com.edumanageng.financial.repository;

import com.edumanageng.financial.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    List<FeeStructure> findBySchoolIdAndIsActive(Long schoolId, boolean isActive);
    List<FeeStructure> findBySchoolIdAndSessionId(Long schoolId, Long sessionId);
    List<FeeStructure> findBySchoolIdAndLevel(Long schoolId, FeeStructure.AcademicLevel level);
}
