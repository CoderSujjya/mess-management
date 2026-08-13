package com.messmanagement.controller;

import com.messmanagement.model.Payment;
import com.messmanagement.service.PaymentService;
import com.messmanagement.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Web (Thymeleaf) pages for recording a new payment against a student.
 */
@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final StudentService studentService;

    public PaymentController(PaymentService paymentService, StudentService studentService) {
        this.paymentService = paymentService;
        this.studentService = studentService;
    }

    @GetMapping("/new/{studentId}")
    public String newForm(@PathVariable Long studentId, Model model) {
        Payment payment = new Payment();
        payment.setPaymentDate(LocalDate.now());
        model.addAttribute("payment", payment);
        model.addAttribute("student", studentService.getStudentById(studentId));
        return "payment-form";
    }

    @PostMapping("/{studentId}")
    public String create(@PathVariable Long studentId, @Valid @ModelAttribute("payment") Payment payment,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("student", studentService.getStudentById(studentId));
            return "payment-form";
        }
        try {
            paymentService.recordPayment(studentId, payment);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("student", studentService.getStudentById(studentId));
            model.addAttribute("errorMessage", ex.getMessage());
            return "payment-form";
        }
        return "redirect:/students/" + studentId + "/history";
    }
}
