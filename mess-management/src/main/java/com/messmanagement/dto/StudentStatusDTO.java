package com.messmanagement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Read-only view model for a single row of the dashboard table.
 * Combines a student with their most recent payment period and
 * a derived status - nothing here is persisted directly.
 */
public class StudentStatusDTO {

    private Long studentId;
    private String studentName;
    private String phone;

    /** ACTIVE, EXPIRED, or NO_PAYMENT (student added but never paid) */
    private String status;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastPaymentDate;
    private BigDecimal lastAmountPaid;

    /** Negative when expired. Null when there is no payment on record. */
    private Long daysRemaining;

    /** True when status is ACTIVE and expiring within the next 5 days */
    private boolean expiringSoon;

    public StudentStatusDTO() {
    }

    public StudentStatusDTO(Long studentId, String studentName, String phone, String status,
                             LocalDate startDate, LocalDate endDate, LocalDate lastPaymentDate,
                             BigDecimal lastAmountPaid, Long daysRemaining, boolean expiringSoon) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.phone = phone;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lastPaymentDate = lastPaymentDate;
        this.lastAmountPaid = lastAmountPaid;
        this.daysRemaining = daysRemaining;
        this.expiringSoon = expiringSoon;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public LocalDate getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(LocalDate lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public BigDecimal getLastAmountPaid() {
        return lastAmountPaid;
    }

    public void setLastAmountPaid(BigDecimal lastAmountPaid) {
        this.lastAmountPaid = lastAmountPaid;
    }

    public Long getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(Long daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    public boolean isExpiringSoon() {
        return expiringSoon;
    }

    public void setExpiringSoon(boolean expiringSoon) {
        this.expiringSoon = expiringSoon;
    }
}
