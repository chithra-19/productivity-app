package com.climbup.controller.task;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardViewController {

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "dashboard";  // loads dashboard.html
    }
}
