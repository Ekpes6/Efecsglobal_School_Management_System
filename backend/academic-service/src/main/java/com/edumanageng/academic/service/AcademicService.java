package com.edumanageng.academic.service;

import com.edumanageng.academic.dto.*;
import com.edumanageng.academic.entity.Result;
import com.edumanageng.academic.entity.Subject;
import com.edumanageng.academic.exception.AcademicException;
import com.edumanageng.academic.repository.ResultRepository;
import com.edumanageng.academic.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AcademicService {

    private final ResultRepository resultRepository;
    private final SubjectRepository subjectRepository;

    // ============ SUBJECT MANAGEMENT ============

    public Subject createSubject(Long schoolId, String name, String code, String description,
                                 Subject.AcademicLevel level, Long classId, boolean isCompulsory) {
        Subject subject = Subject.builder()
            .schoolId(schoolId)
            .name(name)
            .subjectCode(code)
            .description(description)
            .academicLevel(level)
            .classId(classId)
            .isCompulsory(isCompulsory)
            .build();
        return subjectRepository.save(Objects.requireNonNull(subject));
    }

    public List<Subject> getSubjectsByClass(Long schoolId, Long classId) {
        return subjectRepository.findBySchoolIdAndClassId(schoolId, classId);
    }

    public List<Subject> getSubjectsBySchool(Long schoolId) {
        return subjectRepository.findBySchoolId(schoolId);
    }

    // ============ RESULT MANAGEMENT ============

    @SuppressWarnings("null")
    public ResultResponse recordResult(RecordResultRequest request) {
        // Check if result already exists
        resultRepository.findByStudentIdAndSubjectIdAndSessionIdAndTerm(
            request.getStudentId(), request.getSubjectId(), request.getSessionId(), request.getTerm()
        ).ifPresent(r -> {
            throw new AcademicException("Result already exists for this student, subject, session and term");
        });

        BigDecimal total = calculateTotal(request.getCa1Score(), request.getCa2Score(),
                                          request.getCa3Score(), request.getExamScore());
        String grade = computeGrade(total);
        String remark = computeRemark(grade);

        Result result = Result.builder()
            .schoolId(request.getSchoolId())
            .studentId(request.getStudentId())
            .subjectId(request.getSubjectId())
            .subjectName(request.getSubjectName())
            .classId(request.getClassId())
            .className(request.getClassName())
            .sessionId(request.getSessionId())
            .sessionName(request.getSessionName())
            .term(request.getTerm())
            .ca1Score(request.getCa1Score())
            .ca2Score(request.getCa2Score())
            .ca3Score(request.getCa3Score())
            .examScore(request.getExamScore())
            .totalScore(total)
            .grade(grade)
            .gradeRemark(remark)
            .teacherRemark(request.getTeacherRemark())
            .teacherId(request.getTeacherId())
            .isPublished(false)
            .build();

        result = resultRepository.save(result);
        return mapToResultResponse(result);
    }

    public ResultResponse updateResult(Long id, RecordResultRequest request) {
        Result result = resultRepository.findById(Objects.requireNonNull(id))
            .orElseThrow(() -> new AcademicException("Result not found with ID: " + id));

        BigDecimal total = calculateTotal(request.getCa1Score(), request.getCa2Score(),
                                          request.getCa3Score(), request.getExamScore());
        result.setCa1Score(request.getCa1Score());
        result.setCa2Score(request.getCa2Score());
        result.setCa3Score(request.getCa3Score());
        result.setExamScore(request.getExamScore());
        result.setTotalScore(total);
        result.setGrade(computeGrade(total));
        result.setGradeRemark(computeRemark(result.getGrade()));
        result.setTeacherRemark(request.getTeacherRemark());

        return mapToResultResponse(resultRepository.save(result));
    }

    public void publishResults(Long schoolId, Long sessionId, Result.Term term) {
        List<Result> results = resultRepository.findByStudentIdAndSessionId(schoolId, sessionId)
            .stream()
            .filter(r -> r.getTerm() == term)
            .collect(Collectors.toList());
        results.forEach(r -> r.setPublished(true));
        resultRepository.saveAll(results);
        log.info("Published {} results for session {} term {}", results.size(), sessionId, term);
    }

    public List<ResultResponse> getStudentResults(Long studentId) {
        return resultRepository.findByStudentId(studentId)
            .stream().map(this::mapToResultResponse).collect(Collectors.toList());
    }

    public List<ResultResponse> getStudentResultsBySession(Long studentId, Long sessionId) {
        return resultRepository.findByStudentIdAndSessionId(studentId, sessionId)
            .stream().map(this::mapToResultResponse).collect(Collectors.toList());
    }

    public List<ResultResponse> getStudentTermResults(Long studentId, Long sessionId, Result.Term term) {
        return resultRepository.findByStudentIdAndSessionIdAndTerm(studentId, sessionId, term)
            .stream().map(this::mapToResultResponse).collect(Collectors.toList());
    }

    public List<ResultResponse> getPublishedStudentResults(Long studentId) {
        return resultRepository.findPublishedResultsByStudent(studentId)
            .stream().map(this::mapToResultResponse).collect(Collectors.toList());
    }

    // ============ GRADING (Nigerian System) ============

    private BigDecimal calculateTotal(BigDecimal ca1, BigDecimal ca2, BigDecimal ca3, BigDecimal exam) {
        BigDecimal total = BigDecimal.ZERO;
        if (ca1 != null) total = total.add(ca1);
        if (ca2 != null) total = total.add(ca2);
        if (ca3 != null) total = total.add(ca3);
        if (exam != null) total = total.add(exam);
        return total;
    }

    private String computeGrade(BigDecimal total) {
        int score = total.intValue();
        if (score >= 75) return "A1";
        if (score >= 70) return "B2";
        if (score >= 65) return "B3";
        if (score >= 60) return "C4";
        if (score >= 55) return "C5";
        if (score >= 50) return "C6";
        if (score >= 45) return "D7";
        if (score >= 40) return "E8";
        return "F9";
    }

    private String computeRemark(String grade) {
        return switch (grade) {
            case "A1" -> "Excellent";
            case "B2" -> "Very Good";
            case "B3" -> "Good";
            case "C4" -> "Credit";
            case "C5" -> "Credit";
            case "C6" -> "Credit";
            case "D7" -> "Pass";
            case "E8" -> "Pass";
            default -> "Fail";
        };
    }

    private ResultResponse mapToResultResponse(Result result) {
        return ResultResponse.builder()
            .id(result.getId())
            .studentId(result.getStudentId())
            .subjectId(result.getSubjectId())
            .subjectName(result.getSubjectName())
            .classId(result.getClassId())
            .className(result.getClassName())
            .sessionId(result.getSessionId())
            .sessionName(result.getSessionName())
            .term(result.getTerm())
            .ca1Score(result.getCa1Score())
            .ca2Score(result.getCa2Score())
            .ca3Score(result.getCa3Score())
            .examScore(result.getExamScore())
            .totalScore(result.getTotalScore())
            .grade(result.getGrade())
            .gradeRemark(result.getGradeRemark())
            .positionInClass(result.getPositionInClass())
            .teacherRemark(result.getTeacherRemark())
            .isPublished(result.isPublished())
            .createdAt(result.getCreatedAt())
            .build();
    }
}
