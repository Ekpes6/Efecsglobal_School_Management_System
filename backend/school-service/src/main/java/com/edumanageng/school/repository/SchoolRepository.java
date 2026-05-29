package com.edumanageng.school.repository;

import com.edumanageng.school.entity.School;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findByRegistrationNumber(String registrationNumber);
    boolean existsByEmail(String email);
    boolean existsByRegistrationNumber(String registrationNumber);
    Page<School> findByStatus(School.SchoolStatus status, Pageable pageable);

    @Query("SELECT s FROM School s WHERE s.state = :state AND s.status = 'ACTIVE'")
    Page<School> findActiveSchoolsByState(String state, Pageable pageable);
}
