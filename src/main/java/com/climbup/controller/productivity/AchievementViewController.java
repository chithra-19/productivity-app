package com.climbup.controller.productivity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.model.CustomUserDetails;
import com.climbup.model.User;
import com.climbup.service.productivity.AchievementEvaluationService;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.user.UserService;

@Controller
@RequestMapping("/dashboard")
public class AchievementViewController {

    private final AchievementService achievementService;
    private final UserService userService;
    
    @Autowired
    public AchievementViewController(AchievementService achievementService,
            						UserService userService) {
    	this.achievementService = achievementService;
    	this.userService = userService;
    }

    @GetMapping("/achievements")
    public String showAchievements(
            @AuthenticationPrincipal CustomUserDetails principal,
            Model model) {

        if (principal == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        User user = userService.findByEmail(principal.getUsername());
        List<AchievementResponseDTO> achievements = achievementService.getUserAchievements(user.getId());

        model.addAttribute("achievements", achievements);
        return "achievements"; // points to achievements.html
    }



}
