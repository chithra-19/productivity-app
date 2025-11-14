package com.climbup.controller.user;

import com.climbup.dto.request.UserRequestDTO;
import com.climbup.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth") // 🔧 All routes here will be prefixed with /auth
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register") // 📥 Show registration form
    public String showRegisterForm(Model model) {
        model.addAttribute("userDTO", new UserRequestDTO()); // 🧠 Bind empty DTO for form
        return "register"; // 🖼️ Return register.html view
    }

    @PostMapping("/register") // 📝 Handle form submission
    public String registerUser(@Validated @ModelAttribute("userDTO") UserRequestDTO userDTO,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "register"; // ❌ Validation failed — redisplay form
        }
        try {
            userService.registerUser(userDTO); // ✅ Register user via service
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage()); // ⚠️ Show error message
            return "register";
        }catch (RuntimeException e) {
            model.addAttribute("error", "Something went wrong");
            return "error";
        }
        
        return "redirect:/auth/login"; // 🎯 Redirect to login after success
    }

    @GetMapping("/login") // 🔐 Show login page
    public String showLoginPage() {
        return "login"; // 🖼️ Return login.html view
    }

    @GetMapping("/dashboard") // 📊 Protected dashboard view
    public String dashboard() {
        return "dashboard"; // 🖼️ Return dashboard.html view
    }
}