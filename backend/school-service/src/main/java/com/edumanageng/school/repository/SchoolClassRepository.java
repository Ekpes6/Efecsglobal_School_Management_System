package com.edumanageng.school.repository;

import com.edumanageng.school.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    List<SchoolClass> findBySchoolIdOrderByLevelAscNameAsc(Long schoolId);
    List<SchoolClass> findBySchoolIdAndLevel(Long schoolId, com.edumanageng.school.entity.School.AcademicLevel level);
}
