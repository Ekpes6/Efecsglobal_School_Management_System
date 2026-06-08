package com.edumanageng.academic.repository;

import com.edumanageng.academic.entity.Attendance;
import com.edumanageng.academic.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByClassIdAndAttendanceDateOrderByStudentName(Long classId, LocalDate date);

    List<Attendance> findByStudentIdAndTermAndSessionId(Long studentId, Result.Term term, Long sessionId);

    List<Attendance> findBySchoolIdAndAttendanceDateBetween(Long schoolId, LocalDate start, LocalDate end);

    Optional<Attendance> findByStudentIdAndClassIdAndAttendanceDateAndTermAndSessionId(
        Long studentId, Long classId, LocalDate attendanceDate, Result.Term term, Long sessionId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.studentId = :studentId AND a.sessionId = :sessionId " +
           "AND a.term = :term AND a.status = 'PRESENT'")
    long countPresent(@Param("studentId") Long studentId, @Param("sessionId") Long sessionId,
                      @Param("term") Result.Term term);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.studentId = :studentId AND a.sessionId = :sessionId " +
           "AND a.term = :term")
    long countTotal(@Param("studentId") Long studentId, @Param("sessionId") Long sessionId,
                    @Param("term") Result.Term term);
}
