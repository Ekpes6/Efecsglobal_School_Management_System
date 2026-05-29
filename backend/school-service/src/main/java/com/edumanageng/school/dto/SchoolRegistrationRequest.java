package com.edumanageng.school.dto;

import com.edumanageng.school.entity.School;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class SchoolRegistrationRequest {

    @NotBlank(message = "School name is required")
    @Size(max = 200)
    private String name;

    @Size(max = 50)
    private String registrationNumber;

    @Size(max = 50)
    private String rcNumber;

    @NotBlank(message = "Address is required")
    @Size(max = 200)
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 50)
    private String state;

    @Size(max = 100)
    private String lga;

    @Pattern(regexp = "^(\\+234|0)[789]\\d{9}$", message = "Invalid Nigerian phone number")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    private String email;

    private String website;

    @NotNull(message = "Admin user ID is required")
    private Long adminUserId;

    private List<School.AcademicLevel> levels;

    private School.SubscriptionPlan subscriptionPlan;
}
