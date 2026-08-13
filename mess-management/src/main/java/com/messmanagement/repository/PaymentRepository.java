package com.messmanagement.repository;

import com.messmanagement.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByStudentIdOrderByEndDateDesc(Long studentId);

    Optional<Payment> findTopByStudentIdOrderByEndDateDesc(Long studentId);

    List<Payment> findAllByOrderByStudentIdAsc();
}
