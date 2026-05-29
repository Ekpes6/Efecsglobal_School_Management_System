package com.edumanageng.guardian.repository;

import com.edumanageng.guardian.entity.GuardianStudentLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GuardianStudentLinkRepository extends JpaRepository<GuardianStudentLink, Long> {
    List<GuardianStudentLink> findByGuardianIdAndStatus(Long guardianId, GuardianStudentLink.LinkStatus status);
    List<GuardianStudentLink> findByStudentId(Long studentId);
    Optional<GuardianStudentLink> findByGuardianIdAndStudentId(Long guardianId, Long studentId);

    @Query("SELECT l FROM GuardianStudentLink l WHERE l.guardianId = :guardianId AND l.schoolId = :schoolId")
    List<GuardianStudentLink> findByGuardianIdAndSchoolId(@Param("guardianId") Long guardianId,
                                                          @Param("schoolId") Long schoolId);
}
