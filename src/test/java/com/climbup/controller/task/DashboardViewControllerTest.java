package com.climbup.controller.task;

import com.climbup.model.User;
import com.climbup.service.productivity.AchievementService;

import com.climbup.service.productivity.StreakTrackerService;
import com.climbup.service.task.TaskService;
import com.climbup.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc

public class DashboardViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private UserService userService;
    @MockBean private TaskService taskService;
    @MockBean private StreakTrackerService streakTrackerService;
    @MockBean private AchievementService achievementService;
   
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
    }

    @Test
    @WithMockUser(username = "testuser")
    void getDashboardView_ShouldReturnDashboardTemplate() throws Exception {
        when(userService.getUserWithAllData("testuser")).thenReturn(testUser);
        when(streakTrackerService.getCurrentStreak(testUser)).thenReturn(5);
        when(taskService.getTasksForUser(testUser)).thenReturn(List.of());

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("username"))
                .andExpect(model().attributeExists("streak"))
                .andExpect(model().attributeExists("score"))
                .andExpect(model().attributeExists("pendingCount"))
                .andExpect(model().attributeExists("quote"))
                .andExpect(model().attributeExists("task"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getActivityDates_ShouldReturnHeatmapResponse() throws Exception {
        LocalDate from = LocalDate.now().minusDays(5);
        LocalDate to = LocalDate.now();

        when(userService.getUserWithAllData("testuser")).thenReturn(testUser);
     
        mockMvc.perform(get("/dashboard/api/activity-log/TASK")
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDays").value(0))
                .andExpect(jsonPath("$.currentStreak").value(3))
                .andExpect(jsonPath("$.activeDates").isArray());
    }

//    @Test
//    void unauthenticatedUser_ShouldRedirectToLogin() throws Exception {
//        mockMvc.perform(get("/dashboard"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrlPattern("**/auth/login"));
//    }
}