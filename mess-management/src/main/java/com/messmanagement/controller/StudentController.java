package com.messmanagement.controller;

import com.messmanagement.model.Student;
import com.messmanagement.service.PaymentService;
import com.messmanagement.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Web (Thymeleaf) pages for managing students: list, add, edit, delete,
 * search, and viewing a single student's payment history.
 */
@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final PaymentService paymentService;

    public StudentController(StudentService studentService, PaymentService paymentService) {
        this.studentService = studentService;
        this.paymentService = paymentService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String name, Model model) {
        model.addAttribute("students", studentService.searchByName(name));
        model.addAttribute("nameFilter", name == null ? "" : name);
        return "students";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("isEdit", false);
        return "student-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("student") Student student, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "student-form";
        }
        studentService.addStudent(student);
        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        model.addAttribute("isEdit", true);
        return "student-form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("student") Student student,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "student-form";
        }
        studentService.updateStudent(id, student);
        return "redirect:/students";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/students";
    }

    @GetMapping("/{id}/history")
    public String history(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        model.addAttribute("payments", paymentService.getPaymentHistory(id));
        return "payment-history";
    }
}
