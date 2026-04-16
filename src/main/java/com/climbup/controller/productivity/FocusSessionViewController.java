package com.climbup.controller.productivity;


import com.climbup.model.FocusSession;
import com.climbup.model.User;
import com.climbup.service.productivity.FocusSessionService;
import com.climbup.service.user.UserService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard/focus")
public class FocusSessionViewController {

    private final FocusSessionService focusSessionService;
    private final UserService userService;

    public FocusSessionViewController(FocusSessionService focusSessionService,
                           UserService userService) {
        this.focusSessionService = focusSessionService;
        this.userService = userService;
    }

    
    @GetMapping("/focus-mode")
    public String showFocusMode(@AuthenticationPrincipal UserDetails springUser,
                                Model model) {

        User user = userService.getUserWithAllData(springUser.getUsername());
        FocusSession currentSession = focusSessionService.getCurrentSession(user);

        model.addAttribute("user", user); // 🔥 REQUIRED
        model.addAttribute("currentSession", currentSession);
        model.addAttribute("remainingMinutes",
                currentSession != null ? focusSessionService.getRemainingMinutes(currentSession) : 0);

        return "focus-mode";
    }

    @GetMapping("/focus-sessions")
    public String focusSessionsPage() {
        return "focus-sessions";
    }
  
}