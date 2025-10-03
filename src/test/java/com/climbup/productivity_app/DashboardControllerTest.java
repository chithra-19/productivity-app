package com.climbup.productivity_app;

import com.climbup.controller.task.DashboardController;
import com.climbup.model.ActivityLog;
import com.climbup.model.User;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.productivity.ActivityLogService;
import com.climbup.service.user.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class DashboardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private UserService userService;

    @Mock
    private AchievementService achievementService;

    @InjectMocks
    private DashboardController dashboardController;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController)
                .setViewResolvers(viewResolver)
                .build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
    }

    @Test
    @DisplayName("Should return dashboard view with user, achievements, and heatmap")
    void getDashboardView_ShouldReturnDashboardTemplate() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(achievementService.getUserAchievements(testUser)).thenReturn(List.of());
        when(activityLogService.getHeatmapData(testUser)).thenReturn(List.of());

        // ✅ Corrected path based on @RequestMapping("/dashboard") + @GetMapping("/api/dashboard/view/{userId}")
        mockMvc.perform(get("/dashboard/api/dashboard/view/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("achievements"))
                .andExpect(model().attributeExists("heatmapData"));

        verify(userService).getUserById(1L);
        verify(achievementService).getUserAchievements(testUser);
        verify(activityLogService).getHeatmapData(testUser);
    }

    @Test
    @DisplayName("Should return heatmap JSON with streak and active dates")
    void getActivityDates_ShouldReturnHeatmapResponse() throws Exception {
        LocalDate from = LocalDate.now().minusDays(5);
        LocalDate to = LocalDate.now();
        List<ActivityLog> logs = List.of();

        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(activityLogService.getLogs(testUser, "task", from, to)).thenReturn(logs);
        when(activityLogService.getCurrentStreak(testUser, "task")).thenReturn(3);

        // ✅ Corrected path based on @RequestMapping("/dashboard") + @GetMapping("/api/activity-log/{category}")
        mockMvc.perform(get("/dashboard/api/activity-log/task")
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .principal(() -> "testuser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDays").value(0))
                .andExpect(jsonPath("$.currentStreak").value(3))
                .andExpect(jsonPath("$.activeDates").isArray());

        verify(userService).findByUsername("testuser");
        verify(activityLogService).getLogs(testUser, "task", from, to);
        verify(activityLogService).getCurrentStreak(testUser, "task");
    }
}