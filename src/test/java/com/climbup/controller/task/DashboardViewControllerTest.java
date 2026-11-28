package com.climbup.controller.task;

import com.climbup.dto.response.DashboardResponseDTO;
import com.climbup.model.Profile;
import com.climbup.model.User;
import com.climbup.service.productivity.MotivationService;
import com.climbup.service.task.ActivityService;
import com.climbup.service.task.DashboardService;
import com.climbup.service.task.TaskQueryService;
import com.climbup.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardViewControllerTest {

    @Mock private UserService userService;
    @Mock private TaskQueryService taskQueryService;
    @Mock private MotivationService motivationService;
    @Mock private DashboardService dashboardService;
    @Mock private ActivityService activityService;
    @Mock private DashboardResponseDTO dashboardDto;
    @Mock private UserDetails springUser;

    @InjectMocks
    private DashboardController controller;

    private void stubCommonCalls(User user) {
        when(springUser.getUsername()).thenReturn("test@example.com");
        when(userService.getUserWithAllData("test@example.com")).thenReturn(user);
        when(dashboardService.buildDashboard(user)).thenReturn(dashboardDto);
        when(motivationService.getRandomQuote()).thenReturn("Keep going");
        when(taskQueryService.getTasksDueOn(eq(user), any(LocalDate.class))).thenReturn(List.of());
    }

    @Test
    void showDashboard_ShouldUseFirstNameAndFillTheModel() {
        User user = new User("test@example.com", "pass");
        Profile profile = new Profile("Chithra", "K", "bio");
        profile.setProfilePictureUrl("/profile/picture/1");
        user.setProfile(profile);
        stubCommonCalls(user);

        Model model = new ExtendedModelMap();
        String view = controller.showDashboard(springUser, model);

        assertEquals("dashboard", view);
        assertEquals("Chithra", model.getAttribute("displayName"));
        assertEquals("/profile/picture/1", model.getAttribute("profilePictureUrl"));
        assertEquals("Keep going", model.getAttribute("quote"));
        assertEquals(0, model.getAttribute("todaysCount"));
        assertSame(dashboardDto, model.getAttribute("dashboard"));
    }

    @Test
    void showDashboard_WithoutProfile_ShouldFallBackToUser() {
        User user = new User("test@example.com", "pass"); // no profile
        stubCommonCalls(user);

        Model model = new ExtendedModelMap();
        String view = controller.showDashboard(springUser, model);

        assertEquals("dashboard", view);
        assertEquals("User", model.getAttribute("displayName"));
        assertNull(model.getAttribute("profilePictureUrl"));
    }
}