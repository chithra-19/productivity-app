package com.climbup.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserPageController {

    @GetMapping("/register")
    public String showRegisterPage() {
        return "user/register";
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "user/dashboard";
    }
       
}
