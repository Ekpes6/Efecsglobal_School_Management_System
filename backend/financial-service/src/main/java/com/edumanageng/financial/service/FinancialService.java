package com.edumanageng.financial.service;

import com.edumanageng.financial.entity.FeeStructure;
import com.edumanageng.financial.entity.Payment;
import com.edumanageng.financial.repository.FeeStructureRepository;
import com.edumanageng.financial.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FinancialService {

    private final FeeStructureRepository feeStructureRepository;
    private final PaymentRepository paymentRepository;
    private final AtomicInteger receiptCounter = new AtomicInteger(1);

    // FEE MANAGEMENT

    public FeeStructure createFeeStructure(FeeStructure feeStructure) {
        feeStructure.setActive(true);
        return feeStructureRepository.save(feeStructure);
    }

    public List<FeeStructure> getActiveFees(Long schoolId) {
        return feeStructureRepository.findBySchoolIdAndIsActive(schoolId, true);
    }

    public List<FeeStructure> getFeesBySession(Long schoolId, Long sessionId) {
        return feeStructureRepository.findBySchoolIdAndSessionId(schoolId, sessionId);
    }

    public FeeStructure deactivateFee(Long feeId) {
        FeeStructure fee = feeStructureRepository.findById(Objects.requireNonNull(feeId))
            .orElseThrow(() -> new IllegalArgumentException("Fee structure not found: " + feeId));
        fee.setActive(false);
        return feeStructureRepository.save(fee);
    }

    // PAYMENT MANAGEMENT

    public Payment initiatePayment(Long studentId, Long schoolId, Long feeStructureId,
                                   BigDecimal amount, String payerName, String payerPhone,
                                   Payment.PaymentMethod method, String narration) {
        String reference = generateReference();
        Payment payment = Payment.builder()
            .referenceCode(reference)
            .studentId(studentId)
            .schoolId(schoolId)
            .feeStructureId(feeStructureId)
            .amount(amount)
            .amountPaid(BigDecimal.ZERO)
            .status(Payment.PaymentStatus.PENDING)
            .paymentMethod(method)
            .payerName(payerName)
            .payerPhone(payerPhone)
            .narration(narration)
            .build();
        return paymentRepository.save(Objects.requireNonNull(payment));
    }

    /**
     * Called after Paystack webhook confirms payment success.
     * In production, always verify via Paystack API before marking success.
     */
    public Payment confirmPayment(String referenceCode, String paystackReference, BigDecimal amountPaid) {
        Payment payment = paymentRepository.findByReferenceCode(referenceCode)
            .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + referenceCode));

        payment.setPaystackReference(paystackReference);
        payment.setAmountPaid(amountPaid);
        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDate.now());
        payment.setReceiptNumber(generateReceiptNumber());
        return paymentRepository.save(Objects.requireNonNull(payment));
    }

    public Payment recordCashPayment(Long studentId, Long schoolId, Long feeStructureId,
                                     BigDecimal amount, String payerName, String narration) {
        String reference = generateReference();
        Payment payment = Payment.builder()
            .referenceCode(reference)
            .studentId(studentId)
            .schoolId(schoolId)
            .feeStructureId(feeStructureId)
            .amount(amount)
            .amountPaid(amount)
            .status(Payment.PaymentStatus.SUCCESS)
            .paymentMethod(Payment.PaymentMethod.CASH)
            .payerName(payerName)
            .paymentDate(LocalDate.now())
            .receiptNumber(generateReceiptNumber())
            .narration(narration)
            .build();
        return paymentRepository.save(Objects.requireNonNull(payment));
    }

    public List<Payment> getStudentPayments(Long studentId) {
        return paymentRepository.findByStudentId(studentId);
    }

    public Page<Payment> getSchoolPayments(Long schoolId, Pageable pageable) {
        return paymentRepository.findBySchoolId(schoolId, pageable);
    }

    public Payment getPayment(Long id) {
        return paymentRepository.findById(Objects.requireNonNull(id))
            .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + id));
    }

    private String generateReference() {
        return "EDU-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-" +
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateReceiptNumber() {
        return "RCP-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")) +
               "-" + String.format("%06d", receiptCounter.getAndIncrement());
    }
}
