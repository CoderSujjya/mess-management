package com.messmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a single payment record for a student.
 * Each payment covers one billing period (startDate to endDate),
 * e.g. 1-Sep-2026 to 30-Sep-2026. Status (Active/Expired) is derived
 * at read time by comparing endDate to today - it is never stored.
 */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"payments"})
    private Student student;

    @NotNull(message = "Amount paid is required")
    @PositiveOrZero(message = "Amount must be zero or positive")
    @Column(name = "amount_paid", nullable = false)
    private BigDecimal amountPaid;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    public Payment() {
    }

    public Payment(Long id, Student student, BigDecimal amountPaid, LocalDate startDate,
                    LocalDate endDate, LocalDate paymentDate) {
        this.id = id;
        this.student = student;
        this.amountPaid = amountPaid;
        this.startDate = startDate;
        this.endDate = endDate;
        this.paymentDate = paymentDate;
    }

    @PrePersist
    protected void onCreate() {
        if (this.paymentDate == null) {
            this.paymentDate = LocalDate.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
}
