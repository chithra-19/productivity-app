package com.climbup.productivity_app;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.climbup.controller.productivity.StreakTrackerController;
import com.climbup.dto.request.StreakTrackerRequestDTO;
import com.climbup.model.StreakTracker;
import com.climbup.model.User;
import com.climbup.service.productivity.StreakTrackerService;
import com.climbup.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class StreakTrackerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StreakTrackerService streakTrackerService;

    @Mock
    private UserService userService;

    @InjectMocks
    private StreakTrackerController streakTrackerController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private User testUser;
    private StreakTracker testStreak;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(streakTrackerController).build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testStreak = new StreakTracker();
        testStreak.setId(1L);
        testStreak.setUser(testUser);
        testStreak.setCategory("Task");
        testStreak.setCurrentStreak(5);
        testStreak.setLongestStreak(10);
        testStreak.setLastActiveDate(LocalDate.now());
    }

    @Test
    void updateStreak_ShouldReturnUpdatedStreak() throws Exception {
        StreakTrackerRequestDTO requestDTO = new StreakTrackerRequestDTO();
        requestDTO.setCategory("Task");

        // return User directly, not Optional
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(streakTrackerService.updateStreak(testUser, "Task")).thenReturn(testStreak);

        mockMvc.perform(post("/api/streaks/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStreak").value(5))
                .andExpect(jsonPath("$.longestStreak").value(10));
    }

    @Test
    void getStreak_ShouldReturnStreak() throws Exception {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(streakTrackerService.getStreakByUserAndCategory(testUser.getId(), "Task"))
                .thenReturn(testStreak);

        mockMvc.perform(get("/api/streaks/Task"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStreak").value(5))
                .andExpect(jsonPath("$.longestStreak").value(10));
    }

    @Test
    void getAllStreaks_ShouldReturnList() throws Exception {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(streakTrackerService.getAllStreaksForUser(testUser.getId()))
                .thenReturn(List.of(testStreak));

        mockMvc.perform(get("/api/streaks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Task"))
                .andExpect(jsonPath("$[0].currentStreak").value(5));
    }

}
