package com.messmanagement.service;

import com.messmanagement.dto.StudentStatusDTO;
import com.messmanagement.model.Payment;
import com.messmanagement.model.Student;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Builds the dashboard view: one row per student, combining their
 * latest payment period with a status that is computed fresh on
 * every request (Active / Expired / No payment yet). This means
 * status is always correct with zero manual updates, even if the
 * app has been sitting idle for weeks.
 */
@Service
public class DashboardService {

    private static final long EXPIRING_SOON_THRESHOLD_DAYS = 5;

    private final StudentService studentService;
    private final PaymentService paymentService;

    public DashboardService(StudentService studentService, PaymentService paymentService) {
        this.studentService = studentService;
        this.paymentService = paymentService;
    }

    public List<StudentStatusDTO> getDashboard() {
        List<Student> students = studentService.getAllStudents();
        return students.stream()
                .map(this::toStatusDTO)
                .sorted(Comparator.comparingInt(this::statusSortPriority))
                .collect(Collectors.toList());
    }

    /**
     * Keeps Expired students at the bottom of the list. Active/expiring-soon
     * students come first, then upcoming, then no-payment, then expired last.
     */
    private int statusSortPriority(StudentStatusDTO dto) {
        return switch (dto.getStatus()) {
            case "ACTIVE" -> 0;
            case "UPCOMING" -> 1;
            case "NO_PAYMENT" -> 2;
            case "EXPIRED" -> 3;
            default -> 4;
        };
    }

    public List<StudentStatusDTO> getDashboard(String statusFilter, String nameFilter) {
        List<StudentStatusDTO> all = getDashboard();

        if (nameFilter != null && !nameFilter.isBlank()) {
            String needle = nameFilter.trim().toLowerCase();
            all = all.stream()
                    .filter(dto -> dto.getStudentName().toLowerCase().contains(needle))
                    .collect(Collectors.toList());
        }

        if (statusFilter != null && !statusFilter.isBlank() && !statusFilter.equalsIgnoreCase("ALL")) {
            all = all.stream()
                    .filter(dto -> dto.getStatus().equalsIgnoreCase(statusFilter))
                    .collect(Collectors.toList());
        }

        return all;
    }

    private StudentStatusDTO toStatusDTO(Student student) {
        Optional<Payment> latest = paymentService.getLatestPayment(student.getId());
        LocalDate today = LocalDate.now();

        StudentStatusDTO dto = new StudentStatusDTO();
        dto.setStudentId(student.getId());
        dto.setStudentName(student.getName());
        dto.setPhone(student.getPhone());

        if (latest.isEmpty()) {
            dto.setStatus("NO_PAYMENT");
            dto.setDaysRemaining(null);
            dto.setExpiringSoon(false);
            return dto;
        }

        Payment payment = latest.get();
        dto.setStartDate(payment.getStartDate());
        dto.setEndDate(payment.getEndDate());
        dto.setLastPaymentDate(payment.getPaymentDate());
        dto.setLastAmountPaid(payment.getAmountPaid());

        long daysRemaining = ChronoUnit.DAYS.between(today, payment.getEndDate());
        dto.setDaysRemaining(daysRemaining);

        if (today.isBefore(payment.getStartDate())) {
            // The period hasn't started yet - this is a future/upcoming payment, not an expired one.
            dto.setStatus("UPCOMING");
            dto.setExpiringSoon(false);
        } else if (today.isAfter(payment.getEndDate())) {
            dto.setStatus("EXPIRED");
            dto.setExpiringSoon(false);
        } else {
            dto.setStatus("ACTIVE");
            dto.setExpiringSoon(daysRemaining <= EXPIRING_SOON_THRESHOLD_DAYS);
        }

        return dto;
    }
}
