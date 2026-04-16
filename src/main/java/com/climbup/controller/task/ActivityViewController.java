package com.climbup.controller.task;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.climbup.dto.response.ActivityDTO;
import com.climbup.model.User;
import com.climbup.service.task.ActivityService;
import com.climbup.service.user.UserService;

@Controller
@RequestMapping("/dashboard")
public class ActivityViewController {
	
	 private final ActivityService activityService;
	   private final UserService userService;

	    @Autowired
	    public ActivityViewController(ActivityService activityService, UserService userService) {
	        this.activityService = activityService;
	        this.userService = userService;
	    }
	
    @GetMapping("/activities")
    public ResponseEntity<List<ActivityDTO>> getActivities(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<ActivityDTO> activities = activityService.getRecentActivities(user); // Already DTOs
        return ResponseEntity.ok(activities);
    }


}
