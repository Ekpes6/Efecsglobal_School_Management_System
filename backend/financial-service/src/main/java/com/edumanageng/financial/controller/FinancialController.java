package com.edumanageng.financial.controller;

import com.edumanageng.financial.entity.FeeStructure;
import com.edumanageng.financial.entity.Payment;
import com.edumanageng.financial.service.FinancialService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class FinancialController {

    private final FinancialService financialService;

    // Fee Structures
    @PostMapping("/api/v1/fees")
    public ResponseEntity<FeeStructure> createFee(@RequestBody FeeStructure feeStructure) {
        return ResponseEntity.status(HttpStatus.CREATED).body(financialService.createFeeStructure(feeStructure));
    }

    @GetMapping("/api/v1/fees/school/{schoolId}")
    public ResponseEntity<List<FeeStructure>> getActiveFees(@PathVariable Long schoolId) {
        return ResponseEntity.ok(financialService.getActiveFees(schoolId));
    }

    @GetMapping("/api/v1/fees/school/{schoolId}/session/{sessionId}")
    public ResponseEntity<List<FeeStructure>> getFeesBySession(@PathVariable Long schoolId,
                                                               @PathVariable Long sessionId) {
        return ResponseEntity.ok(financialService.getFeesBySession(schoolId, sessionId));
    }

    @PatchMapping("/api/v1/fees/{feeId}/deactivate")
    public ResponseEntity<FeeStructure> deactivateFee(@PathVariable Long feeId) {
        return ResponseEntity.ok(financialService.deactivateFee(feeId));
    }

    // Payments
    @PostMapping("/api/v1/payments/initiate")
    public ResponseEntity<Payment> initiatePayment(@RequestBody Map<String, String> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            financialService.initiatePayment(
                Long.parseLong(body.get("studentId")),
                Long.parseLong(body.get("schoolId")),
                body.get("feeStructureId") != null ? Long.parseLong(body.get("feeStructureId")) : null,
                new BigDecimal(body.get("amount")),
                body.get("payerName"),
                body.get("payerPhone"),
                Payment.PaymentMethod.PAYSTACK,
                body.getOrDefault("narration", "School fee payment")
            )
        );
    }

    @PostMapping("/api/v1/payments/cash")
    public ResponseEntity<Payment> recordCash(@RequestBody Map<String, String> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            financialService.recordCashPayment(
                Long.parseLong(body.get("studentId")),
                Long.parseLong(body.get("schoolId")),
                body.get("feeStructureId") != null ? Long.parseLong(body.get("feeStructureId")) : null,
                new BigDecimal(body.get("amount")),
                body.get("payerName"),
                body.getOrDefault("narration", "Cash payment")
            )
        );
    }

    @PostMapping("/api/v1/payments/confirm")
    public ResponseEntity<Payment> confirmPayment(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
            financialService.confirmPayment(
                body.get("referenceCode"),
                body.get("paystackReference"),
                new BigDecimal(body.get("amountPaid"))
            )
        );
    }

    @GetMapping("/api/v1/payments/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
        return ResponseEntity.ok(financialService.getPayment(id));
    }

    @GetMapping("/api/v1/payments/student/{studentId}")
    public ResponseEntity<List<Payment>> getStudentPayments(@PathVariable Long studentId) {
        return ResponseEntity.ok(financialService.getStudentPayments(studentId));
    }

    @GetMapping("/api/v1/payments/school/{schoolId}")
    public ResponseEntity<Page<Payment>> getSchoolPayments(@PathVariable Long schoolId,
                                                           @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(financialService.getSchoolPayments(schoolId, pageable));
    }
}
