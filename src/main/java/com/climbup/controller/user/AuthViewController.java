package com.climbup.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthViewController {

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";  // loads register.html
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";  // (we’ll create login.html next)
    }
}
