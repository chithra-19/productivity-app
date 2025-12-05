package com.climbup.controller.user;

import com.climbup.dto.request.UserRequestDTO;
import com.climbup.model.User;
import com.climbup.model.PasswordResetToken;
import com.climbup.repository.UserRepository;
import com.climbup.repository.TokenRepository;
import com.climbup.service.user.UserService;
import com.climbup.service.task.EmailService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth") // All routes prefixed with /auth
public class AuthController {

	
    private final UserService userService;
    
    @Autowired
    private final UserRepository userRepository;
    
    @Autowired
    private final TokenRepository tokenRepository;
    
    @Autowired
    private final EmailService emailService;

    public AuthController(UserService userService,
                          UserRepository userRepository,
                          TokenRepository tokenRepository,
                          EmailService emailService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    // ---------------- Registration ----------------
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userDTO", new UserRequestDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Validated @ModelAttribute("userDTO") UserRequestDTO userDTO,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.registerUser(userDTO);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Something went wrong");
            return "error";
        }
        return "redirect:/auth/login";
    }

    // ---------------- Login ----------------
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

 

    // ---------------- Forgot Password ----------------
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email, Model model) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // Remove old token if exists
                PasswordResetToken existing = tokenRepository.findByUser(user);
                if (existing != null) {
                    tokenRepository.delete(existing);
                }

                // Create new token
                String token = UUID.randomUUID().toString();
                PasswordResetToken resetToken = new PasswordResetToken(token, user, LocalDateTime.now().plusHours(1));
                tokenRepository.save(resetToken);

                String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;
                emailService.sendEmail(email, "ClimbUp Password Reset", "Click here to reset your password: " + resetLink);
            }

            model.addAttribute("message", "If this email exists, a reset link has been sent.");
            return "forgot-password";

        } catch (Exception e) {
            model.addAttribute("error", "Something went wrong. Please try again.");
            return "forgot-password";
        }
    }
    // ---------------- Reset Password Form ----------------
    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam("token") String token, Model model) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if(resetToken == null || resetToken.isExpired()) {
            model.addAttribute("error", "Invalid or expired token");
            return "reset-password-error";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    // ---------------- Reset Password Submission ----------------
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            model.addAttribute("token", token);
            return "reset-password";
        }
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null || resetToken.isExpired()) {
            model.addAttribute("error", "Invalid or expired token");
            return "reset-password-error";
        }
        User user = resetToken.getUser();
        userService.updatePassword(user, newPassword); // hash & save
        tokenRepository.delete(resetToken);
        model.addAttribute("message", "Password successfully reset!");
        return "redirect:/auth/login?resetSuccess";
    }
}
