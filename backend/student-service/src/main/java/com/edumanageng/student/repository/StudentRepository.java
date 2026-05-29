package com.edumanageng.student.repository;

import com.edumanageng.student.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByAdmissionNumberAndSchoolId(String admissionNumber, Long schoolId);
    boolean existsByAdmissionNumberAndSchoolId(String admissionNumber, Long schoolId);
    Page<Student> findBySchoolId(Long schoolId, Pageable pageable);
    Page<Student> findBySchoolIdAndStatus(Long schoolId, Student.StudentStatus status, Pageable pageable);
    List<Student> findByClassIdAndSchoolId(Long classId, Long schoolId);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.schoolId = :schoolId AND s.status = 'ACTIVE'")
    long countActiveStudentsBySchool(Long schoolId);

    @Query("SELECT s FROM Student s WHERE s.schoolId = :schoolId AND " +
           "(LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "s.admissionNumber LIKE CONCAT('%', :query, '%'))")
    Page<Student> searchStudents(Long schoolId, String query, Pageable pageable);
}
