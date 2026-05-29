package com.edumanageng.academic.dto;

import com.edumanageng.academic.entity.Result;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultResponse {
    private Long id;
    private Long studentId;
    private Long subjectId;
    private String subjectName;
    private Long classId;
    private String className;
    private Long sessionId;
    private String sessionName;
    private Result.Term term;
    private BigDecimal ca1Score;
    private BigDecimal ca2Score;
    private BigDecimal ca3Score;
    private BigDecimal examScore;
    private BigDecimal totalScore;
    private String grade;
    private String gradeRemark;
    private Integer positionInClass;
    private String teacherRemark;
    private boolean isPublished;
    private LocalDateTime createdAt;
}
