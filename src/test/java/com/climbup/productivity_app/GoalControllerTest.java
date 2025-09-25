package com.climbup.productivity_app;

import com.climbup.controller.productivity.GoalController;
import com.climbup.dto.response.GoalResponseDTO;
import com.climbup.model.Goal;
import com.climbup.model.User;
import com.climbup.repository.UserRepository;
import com.climbup.service.productivity.GoalService;
import com.climbup.mapper.GoalMapper;

import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GoalControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GoalService goalService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoalController goalController;

    private User testUser;
    private Goal testGoal;
    private GoalResponseDTO testGoalDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(goalController).build();

        // Test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        // Test goal
        testGoal = new Goal();
        testGoal.setId(1L); // 🔥 IMPORTANT — set the ID here
        testGoal.setTitle("Test Goal");
        testGoal.setDescription("Goal Description");
        testGoal.setUser(testUser);
        testGoal.setProgress(50);

        testGoalDTO = GoalMapper.toDTO(testGoal);

        // Mock SecurityContextHolder to return test user
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(testUser.getUsername());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // Mock repository to return user by username
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(java.util.Optional.of(testUser));
    }


    @Test
    void getGoals_ShouldReturnGoalList() throws Exception {
        when(goalService.filterGoals(testUser, "ALL", "ALL"))
                .thenReturn(List.of(testGoal));

        mockMvc.perform(get("/goals")
                        .param("status", "ALL")
                        .param("priority", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Goal"))
                .andExpect(jsonPath("$[0].description").value("Goal Description"));
    }

    @Test
    void saveGoal_ShouldReturnSavedGoal() throws Exception {
        when(goalService.saveGoal(any(Goal.class))).thenReturn(testGoal);

        mockMvc.perform(post("/goals/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test Goal\",\"description\":\"Goal Description\"}"))
                .andExpect(status().isCreated()) // was isOk(), now matches 201
                .andExpect(jsonPath("$.title").value("Test Goal"))
                .andExpect(jsonPath("$.description").value("Goal Description"));
    }


    @Test
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
    void completeGoal_ShouldMarkGoalCompleted() throws Exception {
        testGoal.markCompleted();

        when(goalService.getGoalByIdAndUser(1L, testUser.getUsername())).thenReturn(testGoal);
        when(goalService.updateGoal(1L, testGoal)).thenReturn(testGoal);

        mockMvc.perform(put("/goals/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void dropGoal_ShouldMarkGoalDropped() throws Exception {
        testGoal.dropGoal();

        when(goalService.getGoalByIdAndUser(1L, testUser.getUsername())).thenReturn(testGoal);
        when(goalService.updateGoal(1L, testGoal)).thenReturn(testGoal);

        mockMvc.perform(put("/goals/1/drop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dropped").value(true));
    }

    @Test
    void deleteGoal_ShouldReturnNoContent() throws Exception {
        when(goalService.getGoalByIdAndUser(1L, testUser.getUsername())).thenReturn(testGoal);
        doNothing().when(goalService).deleteGoal(1L);

        mockMvc.perform(delete("/goals/1"))
                .andExpect(status().isNoContent());

        verify(goalService, times(1)).deleteGoal(1L);
    }
}
