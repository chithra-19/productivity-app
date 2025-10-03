package com.climbup.controller.user;

import com.climbup.dto.request.UserRequestDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthViewController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // maps to login.html in templates
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("userDTO", new UserRequestDTO());
        return "register"; // maps to register.html in templates
    }
}
