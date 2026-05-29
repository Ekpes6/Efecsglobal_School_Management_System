package com.edumanageng.financial.repository;

import com.edumanageng.financial.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReferenceCode(String referenceCode);
    Optional<Payment> findByPaystackReference(String paystackReference);
    List<Payment> findByStudentId(Long studentId);
    Page<Payment> findBySchoolId(Long schoolId, Pageable pageable);
    List<Payment> findByStudentIdAndStatus(Long studentId, Payment.PaymentStatus status);
}
