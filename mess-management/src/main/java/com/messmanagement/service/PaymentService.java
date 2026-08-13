package com.messmanagement.service;

import com.messmanagement.exception.ResourceNotFoundException;
import com.messmanagement.model.Payment;
import com.messmanagement.model.Student;
import com.messmanagement.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Handles payment recording and payment-history lookups.
 * Status (Active / Expired) is never stored - it is always derived
 * from today's date vs. the payment's end date, so it can never
 * go stale. See StudentStatusDTO / DashboardService for that logic.
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final StudentService studentService;

    public PaymentService(PaymentRepository paymentRepository, StudentService studentService) {
        this.paymentRepository = paymentRepository;
        this.studentService = studentService;
    }

    @Transactional
    public Payment recordPayment(Long studentId, Payment payment) {
        Student student = studentService.getStudentById(studentId);
        if (payment.getEndDate().isBefore(payment.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        payment.setStudent(student);
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentHistory(Long studentId) {
        // ensure the student exists, otherwise 404 instead of an empty list
        studentService.getStudentById(studentId);
        return paymentRepository.findByStudentIdOrderByEndDateDesc(studentId);
    }

    public Optional<Payment> getLatestPayment(Long studentId) {
        return paymentRepository.findTopByStudentIdOrderByEndDateDesc(studentId);
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAllByOrderByStudentIdAsc();
    }
}
