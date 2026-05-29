package com.edumanageng.guardian.service;

import com.edumanageng.guardian.entity.Guardian;
import com.edumanageng.guardian.entity.GuardianStudentLink;
import com.edumanageng.guardian.exception.GuardianException;
import com.edumanageng.guardian.repository.GuardianRepository;
import com.edumanageng.guardian.repository.GuardianStudentLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class GuardianService {

    private final GuardianRepository guardianRepository;
    private final GuardianStudentLinkRepository linkRepository;

    public Guardian registerGuardian(Guardian guardian) {
        if (guardianRepository.existsByPhoneNumber(guardian.getPhoneNumber())) {
            throw new GuardianException("A guardian with this phone number already exists");
        }
        guardian.setStatus(Guardian.GuardianStatus.ACTIVE);
        return guardianRepository.save(guardian);
    }

    public Guardian getGuardian(Long id) {
        return guardianRepository.findById(Objects.requireNonNull(id))
            .orElseThrow(() -> new GuardianException("Guardian not found: " + id));
    }

    public Guardian getGuardianByUserId(Long userId) {
        return guardianRepository.findByUserId(userId)
            .orElseThrow(() -> new GuardianException("Guardian not found for user: " + userId));
    }

    public Guardian updateGuardian(Long id, Guardian update) {
        Guardian guardian = getGuardian(id);
        guardian.setFirstName(update.getFirstName());
        guardian.setLastName(update.getLastName());
        guardian.setOccupation(update.getOccupation());
        guardian.setAddress(update.getAddress());
        guardian.setState(update.getState());
        guardian.setLga(update.getLga());
        guardian.setAlternatePhone(update.getAlternatePhone());
        return guardianRepository.save(guardian);
    }

    public GuardianStudentLink linkStudentToGuardian(Long guardianId, Long studentId, Long schoolId,
                                                     String studentName, String admissionNumber,
                                                     String relationship, boolean isPrimary) {
        guardianRepository.findById(Objects.requireNonNull(guardianId))
            .orElseThrow(() -> new GuardianException("Guardian not found: " + guardianId));

        linkRepository.findByGuardianIdAndStudentId(guardianId, studentId).ifPresent(l -> {
            throw new GuardianException("This student is already linked to this guardian");
        });

        GuardianStudentLink link = GuardianStudentLink.builder()
            .guardianId(guardianId)
            .studentId(studentId)
            .schoolId(schoolId)
            .studentName(studentName)
            .admissionNumber(admissionNumber)
            .relationship(relationship)
            .isPrimary(isPrimary)
            .status(GuardianStudentLink.LinkStatus.ACTIVE)
            .build();
        return linkRepository.save(Objects.requireNonNull(link));
    }

    public List<GuardianStudentLink> getGuardianWards(Long guardianId) {
        return linkRepository.findByGuardianIdAndStatus(guardianId, GuardianStudentLink.LinkStatus.ACTIVE);
    }

    public void unlinkStudent(Long guardianId, Long studentId) {
        GuardianStudentLink link = linkRepository.findByGuardianIdAndStudentId(guardianId, studentId)
            .orElseThrow(() -> new GuardianException("Link not found"));
        link.setStatus(GuardianStudentLink.LinkStatus.INACTIVE);
        linkRepository.save(link);
    }

    public boolean isGuardianOfStudent(Long guardianId, Long studentId) {
        return linkRepository.findByGuardianIdAndStudentId(guardianId, studentId)
            .map(l -> l.getStatus() == GuardianStudentLink.LinkStatus.ACTIVE)
            .orElse(false);
    }
}
