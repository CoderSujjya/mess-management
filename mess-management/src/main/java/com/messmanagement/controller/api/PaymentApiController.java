package com.messmanagement.controller.api;

import com.messmanagement.dto.StudentStatusDTO;
import com.messmanagement.model.Payment;
import com.messmanagement.service.DashboardService;
import com.messmanagement.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * JSON REST API for payments and the derived dashboard status.
 */
@RestController
@RequestMapping("/api")
public class PaymentApiController {

    private final PaymentService paymentService;
    private final DashboardService dashboardService;

    public PaymentApiController(PaymentService paymentService, DashboardService dashboardService) {
        this.paymentService = paymentService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/payments/student/{studentId}")
    public List<Payment> history(@PathVariable Long studentId) {
        return paymentService.getPaymentHistory(studentId);
    }

    @PostMapping("/payments/student/{studentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Payment recordPayment(@PathVariable Long studentId, @Valid @RequestBody Payment payment) {
        return paymentService.recordPayment(studentId, payment);
    }

    @GetMapping("/dashboard")
    public List<StudentStatusDTO> dashboard(@RequestParam(required = false) String status,
                                             @RequestParam(required = false) String name) {
        return dashboardService.getDashboard(status, name);
    }
}
