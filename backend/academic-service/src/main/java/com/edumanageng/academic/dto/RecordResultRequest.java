package com.edumanageng.academic.dto;

import com.edumanageng.academic.entity.Result;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecordResultRequest {

    @NotNull private Long schoolId;
    @NotNull private Long studentId;
    @NotNull private Long subjectId;
    private String subjectName;
    private Long classId;
    private String className;
    @NotNull private Long sessionId;
    private String sessionName;
    @NotNull private Result.Term term;
    private Long teacherId;

    @DecimalMin("0") @DecimalMax("10") private BigDecimal ca1Score;
    @DecimalMin("0") @DecimalMax("10") private BigDecimal ca2Score;
    @DecimalMin("0") @DecimalMax("10") private BigDecimal ca3Score;
    @DecimalMin("0") @DecimalMax("70") private BigDecimal examScore;
    private String teacherRemark;
}
