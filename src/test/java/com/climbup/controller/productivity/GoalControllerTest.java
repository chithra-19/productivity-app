package com.climbup.controller.productivity;

import com.climbup.model.Goal;
import com.climbup.model.User;
import com.climbup.repository.UserRepository;
import com.climbup.service.productivity.GoalService;
import com.climbup.controller.productivity.GoalController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GoalControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GoalService goalService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoalController goalController;

    private User testUser;
    private Goal testGoal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(goalController).build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testGoal = new Goal();
        testGoal.setId(1L);
        testGoal.setTitle("Test Goal");
        testGoal.setDescription("Goal Description");
        testGoal.setUser(testUser);
        testGoal.setProgress(50);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(testUser.getUsername());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
    }

    @Test
    @DisplayName("GET /goals should return goal list")
    void getGoals_ShouldReturnGoalList() throws Exception {
        when(goalService.filterGoals(testUser, "ALL", "ALL")).thenReturn(List.of(testGoal));

        mockMvc.perform(get("/goals")
                        .param("status", "ALL")
                        .param("priority", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Goal"))
                .andExpect(jsonPath("$[0].description").value("Goal Description"));
    }

    @Test
    @DisplayName("POST /goals/save should return saved goal")
    void saveGoal_ShouldReturnSavedGoal() throws Exception {
        when(goalService.saveGoal(any(Goal.class))).thenReturn(testGoal);

        mockMvc.perform(post("/goals/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test Goal\",\"description\":\"Goal Description\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Goal"))
                .andExpect(jsonPath("$.description").value("Goal Description"));
    }

    @Test
    @DisplayName("PUT /goals/{id} should update goal")
    void updateGoal_ShouldReturnUpdatedGoal() throws Exception {
        Goal updatedGoal = new Goal();
        updatedGoal.setTitle("Updated Goal");

        when(goalService.getGoalByIdAndUser(1L, testUser.getUsername())).thenReturn(testGoal);
        when(goalService.updateGoal(eq(1L), any(Goal.class))).thenReturn(updatedGoal);

        mockMvc.perform(put("/goals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Goal\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Goal"));
    }

    @Test
    @DisplayName("PUT /goals/{id}/complete should mark goal completed")
    void completeGoal_ShouldMarkGoalCompleted() throws Exception {
        testGoal.markCompleted();

        when(goalService.getGoalByIdAndUser(1L, testUser.getUsername())).thenReturn(testGoal);
        when(goalService.updateGoal(1L, testGoal)).thenReturn(testGoal);

        mockMvc.perform(put("/goals/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @DisplayName("PUT /goals/{id}/drop should mark goal dropped")
    void dropGoal_ShouldMarkGoalDropped() throws Exception {
        testGoal.dropGoal();

        when(goalService.getGoalByIdAndUser(1L, testUser.getUsername())).thenReturn(testGoal);
        when(goalService.updateGoal(1L, testGoal)).thenReturn(testGoal);

        mockMvc.perform(put("/goals/1/drop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dropped").value(true));
    }

    @Test
    @DisplayName("DELETE /goals/{id} should return 204 No Content")
    void deleteGoal_ShouldReturnNoContent() throws Exception {
        when(goalService.getGoalByIdAndUser(1L, testUser.getUsername())).thenReturn(testGoal);
        doNothing().when(goalService).deleteGoal(1L);

        mockMvc.perform(delete("/goals/1"))
                .andExpect(status().isNoContent());

        verify(goalService, times(1)).deleteGoal(1L);
    }

    @Test
    @DisplayName("PUT /goals/{id} should return 404 if goal not found")
    void updateGoal_NotFound_ShouldReturn404() throws Exception {
        when(goalService.getGoalByIdAndUser(999L, testUser.getUsername())).thenReturn(null);

        mockMvc.perform(put("/goals/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Goal\"}"))
                .andExpect(status().isNotFound());
    }
}