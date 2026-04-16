package com.climbup.controller.productivity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/dashboard")
public class StreakTrackerViewController {

	 
    @GetMapping("/streaks")
    public String showStreaksPage(HttpServletRequest request, Model model) {
        model.addAttribute("currentPath", request.getRequestURI());
        return "streak-tracker";
    }


}

