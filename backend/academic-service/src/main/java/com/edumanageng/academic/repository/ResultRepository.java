package com.edumanageng.academic.repository;

import com.edumanageng.academic.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByStudentIdAndSessionIdAndTerm(Long studentId, Long sessionId, Result.Term term);
    List<Result> findByStudentIdAndSessionId(Long studentId, Long sessionId);
    List<Result> findByStudentId(Long studentId);
    List<Result> findByClassIdAndSubjectIdAndSessionIdAndTerm(Long classId, Long subjectId, Long sessionId, Result.Term term);

    @Query("SELECT r FROM Result r WHERE r.studentId = :studentId AND r.isPublished = true ORDER BY r.sessionName DESC, r.term DESC")
    List<Result> findPublishedResultsByStudent(Long studentId);

    Optional<Result> findByStudentIdAndSubjectIdAndSessionIdAndTerm(
        Long studentId, Long subjectId, Long sessionId, Result.Term term);
}
