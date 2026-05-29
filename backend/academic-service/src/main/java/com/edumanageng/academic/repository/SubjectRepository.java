package com.edumanageng.academic.repository;

import com.edumanageng.academic.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findBySchoolIdAndClassId(Long schoolId, Long classId);
    List<Subject> findBySchoolId(Long schoolId);
    List<Subject> findBySchoolIdAndAcademicLevel(Long schoolId, Subject.AcademicLevel level);
}
