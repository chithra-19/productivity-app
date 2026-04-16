package com.climbup.controller.productivity;

import com.climbup.dto.request.GoalRequestDTO;
import com.climbup.model.Goal;
import com.climbup.model.User;
import com.climbup.service.productivity.GoalService;
import com.climbup.service.user.UserService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard/goals")
public class GoalViewController {

    private final GoalService goalService;
    private final UserService userService;

    public GoalViewController(GoalService goalService, UserService userService) {
        this.goalService = goalService;
        this.userService = userService;
    }

    private User currentUser(UserDetails springUser) {
        return userService.getUserWithAllData(springUser.getUsername());
    }

    @GetMapping
    public String showGoalsPage(@AuthenticationPrincipal UserDetails springUser,
                                Model model) {

        User user = currentUser(springUser);

        model.addAttribute("goals", goalService.getGoalsForUser(user));
        model.addAttribute("goal", new GoalRequestDTO());

        return "goals";
    }

   
}