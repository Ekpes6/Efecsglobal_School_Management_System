package com.edumanageng.school.repository;

import com.edumanageng.school.entity.AcademicSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicSessionRepository extends JpaRepository<AcademicSession, Long> {
    List<AcademicSession> findBySchoolIdOrderByStartDateDesc(Long schoolId);
    Optional<AcademicSession> findBySchoolIdAndIsCurrentTrue(Long schoolId);
}
