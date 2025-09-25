package com.climbup.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserPageController {

    // Serve the register page
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; // resolves to src/main/resources/templates/register.html
    }

    // Serve the login page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // resolves to src/main/resources/templates/login.html
    }

    // Serve the dashboard page
    @GetMapping("/dashboard")
    public String showDashboardPage() {
        return "dashboard"; // resolves to src/main/resources/templates/dashboard.html
    }
}
