package com.edumanageng.financial.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "payments")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String referenceCode; // internal reference

    private String paystackReference; // Paystack transaction reference

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Long schoolId;

    private Long feeStructureId;
    private String feeDescription;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount; // in NGN

    @Column(precision = 12, scale = 2)
    private BigDecimal amountPaid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private LocalDate paymentDate;
    private String payerName;
    private String payerPhone;
    private String receiptNumber;
    private String narration;

    @CreationTimestamp private LocalDateTime createdAt;
    @UpdateTimestamp private LocalDateTime updatedAt;

    public enum PaymentStatus { PENDING, SUCCESS, FAILED, CANCELLED, REFUNDED }
    public enum PaymentMethod { PAYSTACK, BANK_TRANSFER, CASH, POS }
}
