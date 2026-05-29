package com.edumanageng.school.service;

import com.edumanageng.school.dto.SchoolRegistrationRequest;
import com.edumanageng.school.dto.SchoolResponse;
import com.edumanageng.school.entity.AcademicSession;
import com.edumanageng.school.entity.School;
import com.edumanageng.school.entity.SchoolClass;
import com.edumanageng.school.exception.SchoolException;
import com.edumanageng.school.repository.AcademicSessionRepository;
import com.edumanageng.school.repository.SchoolClassRepository;
import com.edumanageng.school.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final AcademicSessionRepository sessionRepository;
    private final SchoolClassRepository classRepository;

    public SchoolResponse registerSchool(SchoolRegistrationRequest request) {
        if (request.getEmail() != null && schoolRepository.existsByEmail(request.getEmail())) {
            throw new SchoolException("A school with this email already exists");
        }

        School school = School.builder()
            .name(request.getName())
            .registrationNumber(request.getRegistrationNumber())
            .rcNumber(request.getRcNumber())
            .address(request.getAddress())
            .city(request.getCity())
            .state(request.getState())
            .lga(request.getLga())
            .phoneNumber(request.getPhoneNumber())
            .email(request.getEmail())
            .website(request.getWebsite())
            .adminUserId(request.getAdminUserId())
            .levels(request.getLevels())
            .subscriptionPlan(request.getSubscriptionPlan() != null ? request.getSubscriptionPlan() : School.SubscriptionPlan.FREE)
            .status(School.SchoolStatus.ACTIVE)
            .maxStudents(getMaxStudentsByPlan(request.getSubscriptionPlan()))
            .build();

        school = schoolRepository.save(Objects.requireNonNull(school));
        log.info("School registered: {} (ID: {})", school.getName(), school.getId());
        return mapToResponse(school);
    }

    public SchoolResponse getSchool(Long id) {
        School school = schoolRepository.findById(Objects.requireNonNull(id))
            .orElseThrow(() -> new SchoolException("School not found with ID: " + id));
        return mapToResponse(school);
    }

    public Page<SchoolResponse> getAllSchools(Pageable pageable) {
        return schoolRepository.findAll(Objects.requireNonNull(pageable)).map(this::mapToResponse);
    }

    public SchoolResponse updateSchool(Long id, SchoolRegistrationRequest request) {
        School school = schoolRepository.findById(Objects.requireNonNull(id))
            .orElseThrow(() -> new SchoolException("School not found with ID: " + id));

        school.setName(request.getName());
        school.setAddress(request.getAddress());
        school.setCity(request.getCity());
        school.setState(request.getState());
        school.setLga(request.getLga());
        school.setPhoneNumber(request.getPhoneNumber());
        school.setEmail(request.getEmail());
        school.setWebsite(request.getWebsite());
        school.setLevels(request.getLevels());

        return mapToResponse(schoolRepository.save(school));
    }

    @SuppressWarnings("null")
    public AcademicSession createAcademicSession(Long schoolId, String sessionName, LocalDate startDate, LocalDate endDate) {
        // Mark all current sessions as not current
        sessionRepository.findBySchoolIdAndIsCurrentTrue(schoolId)
            .ifPresent(s -> {
                s.setCurrent(false);
                sessionRepository.save(s);
            });

        AcademicSession session = AcademicSession.builder()
            .schoolId(schoolId)
            .name(sessionName)
            .startDate(startDate)
            .endDate(endDate)
            .isCurrent(true)
            .currentTerm(AcademicSession.Term.FIRST_TERM)
            .build();

        return sessionRepository.save(session);
    }

    public List<AcademicSession> getSchoolSessions(Long schoolId) {
        return sessionRepository.findBySchoolIdOrderByStartDateDesc(schoolId);
    }

    public AcademicSession getCurrentSession(Long schoolId) {
        return sessionRepository.findBySchoolIdAndIsCurrentTrue(schoolId)
            .orElseThrow(() -> new SchoolException("No active academic session found"));
    }

    public SchoolClass createClass(Long schoolId, String name, String arm, School.AcademicLevel level, Integer capacity) {
        SchoolClass schoolClass = SchoolClass.builder()
            .schoolId(schoolId)
            .name(name)
            .classArm(arm)
            .level(level)
            .capacity(capacity)
            .build();
        return classRepository.save(Objects.requireNonNull(schoolClass));
    }

    public List<SchoolClass> getSchoolClasses(Long schoolId) {
        return classRepository.findBySchoolIdOrderByLevelAscNameAsc(schoolId);
    }

    private SchoolResponse mapToResponse(School school) {
        return SchoolResponse.builder()
            .id(school.getId())
            .name(school.getName())
            .registrationNumber(school.getRegistrationNumber())
            .address(school.getAddress())
            .city(school.getCity())
            .state(school.getState())
            .lga(school.getLga())
            .phoneNumber(school.getPhoneNumber())
            .email(school.getEmail())
            .website(school.getWebsite())
            .logoUrl(school.getLogoUrl())
            .status(school.getStatus())
            .subscriptionPlan(school.getSubscriptionPlan())
            .levels(school.getLevels())
            .maxStudents(school.getMaxStudents())
            .createdAt(school.getCreatedAt())
            .build();
    }

    private Integer getMaxStudentsByPlan(School.SubscriptionPlan plan) {
        if (plan == null) return 50;
        return switch (plan) {
            case FREE -> 50;
            case BASIC -> 200;
            case STANDARD -> 500;
            case PREMIUM -> 2000;
            case ENTERPRISE -> Integer.MAX_VALUE;
        };
    }
}
