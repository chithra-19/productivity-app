package com.climbup.productivity_app;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.climbup.controller.task.DashboardController;
import com.climbup.model.ActivityLog;
import com.climbup.model.User;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.productivity.ActivityLogService;
import com.climbup.service.user.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.time.LocalDate;
import java.util.List;

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

        // Add a view resolver so "dashboard" view can be resolved
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/"); // match your templates folder
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController)
                .setViewResolvers(viewResolver)
                .build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
    }

    @Test
    void getDashboardView_ShouldReturnDashboardTemplate() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(achievementService.getUserAchievements(testUser)).thenReturn(List.of());
        when(activityLogService.getHeatmapData(testUser)).thenReturn(List.of());

        mockMvc.perform(get("/api/dashboard/view/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("achievements"))
                .andExpect(model().attributeExists("heatmapData"));
    }

    @Test
    void getActivityDates_ShouldReturnHeatmapResponse() throws Exception {
        LocalDate from = LocalDate.now().minusDays(5);
        LocalDate to = LocalDate.now();
        List<ActivityLog> logs = List.of();

        when(userService.getUserById(1L)).thenReturn(testUser);
        when(activityLogService.getLogs(testUser, "task", from, to)).thenReturn(logs);
        when(activityLogService.getCurrentStreak(testUser, "task")).thenReturn(3);

        mockMvc.perform(get("/api/dashboard/activity-log/1/task")
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDays").value(0))
                .andExpect(jsonPath("$.currentStreak").value(3))
                .andExpect(jsonPath("$.activeDates").isArray());
    }
}
