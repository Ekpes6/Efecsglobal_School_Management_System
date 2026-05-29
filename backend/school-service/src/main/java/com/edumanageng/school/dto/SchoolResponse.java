package com.edumanageng.school.dto;

import com.edumanageng.school.entity.School;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchoolResponse {
    private Long id;
    private String name;
    private String registrationNumber;
    private String address;
    private String city;
    private String state;
    private String lga;
    private String phoneNumber;
    private String email;
    private String website;
    private String logoUrl;
    private School.SchoolStatus status;
    private School.SubscriptionPlan subscriptionPlan;
    private List<School.AcademicLevel> levels;
    private Integer maxStudents;
    private LocalDateTime createdAt;
}
