package com.climbup.admin.controller;

import com.climbup.admin.dto.AdminDashboardDTO;
import com.climbup.admin.service.AdminDashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/dashboard")
    public String getAdminDashboard(Model model) {

        AdminDashboardDTO dashboardData =
                adminDashboardService.getDashboardStats();

        model.addAttribute("dashboard", dashboardData);
        return "admin/dashboard";
    }
}
