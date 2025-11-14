package com.climbup;

import com.climbup.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Centralized exception handling for all controllers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles invalid input scenarios (e.g., form validation errors).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        logger.warn("Illegal argument: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return "register"; // fallback view
    }

    /**
     * Handles missing resources (e.g., user not found).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException ex, RedirectAttributes redirectAttributes) {
        logger.warn("Resource not found: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/auth/login";
    }

    /**
     * Handles unauthorized access attempts.
     */
    @ExceptionHandler(SecurityException.class)
    public String handleSecurity(SecurityException ex, RedirectAttributes redirectAttributes) {
        logger.error("Security violation: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", "Access denied: " + ex.getMessage());
        return "redirect:/auth/login";
    }

    /**
     * Handles all other unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex, Model model) {
        logger.error("Unexpected error", ex);
        model.addAttribute("error", "Unexpected error: " + ex.getMessage());
        return "error"; // generic error page
    }
}