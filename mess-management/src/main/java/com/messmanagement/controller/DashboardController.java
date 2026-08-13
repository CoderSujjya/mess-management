package com.messmanagement.controller;

import com.messmanagement.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Renders the main dashboard: every student with their current
 * payment status, filterable by Active/Expired and by name.
 */
@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String dashboard(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String name,
            Model model) {

        model.addAttribute("rows", dashboardService.getDashboard(status, name));
        model.addAttribute("statusFilter", status == null ? "ALL" : status);
        model.addAttribute("nameFilter", name == null ? "" : name);
        return "dashboard";
    }
}
