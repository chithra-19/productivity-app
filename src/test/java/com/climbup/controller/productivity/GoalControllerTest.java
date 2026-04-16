package com.climbup.controller.productivity;

import com.climbup.model.Goal;
import com.climbup.model.User;
import com.climbup.repository.UserRepository;
import com.climbup.service.productivity.GoalService;
import com.climbup.dto.request.GoalRequestDTO;

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
        testUser.setEmail("email@test.com");
        

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

        when(userRepository.findByEmail(testUser.getUsername()))
                .thenReturn(Optional.of(testUser));
    }
    @Test
    void getGoals_ShouldReturnView() throws Exception {

        when(goalService.filterGoals(testUser, "ALL", "ALL"))
                .thenReturn(List.of(testGoal));

        mockMvc.perform(get("/dashboard/goals"))
                .andExpect(status().isOk());
    }
    
    @Test
    void saveGoal_ShouldRedirect() throws Exception {

    	when(goalService.createGoal(any(GoalRequestDTO.class), any(User.class)))
        .thenReturn(testGoal);

        mockMvc.perform(post("/dashboard/goals")
                        .param("title", "Test Goal")
                        .param("description", "Goal Description"))
                .andExpect(status().is3xxRedirection());
    }
    
    @Test
    void updateGoal_ShouldReturnRedirect() throws Exception {

    	when(goalService.updateGoal(eq(1L), any(GoalRequestDTO.class), any(User.class)))
        .thenReturn(testGoal);

        mockMvc.perform(put("/dashboard/goals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Goal\"}"))
                .andExpect(status().isOk());
    }
    
    @Test
    void deleteGoal_ShouldReturnNoContent() throws Exception {

    	doNothing().when(goalService).deleteGoal(1L, testUser);

    	mockMvc.perform(delete("/goals/1"))
    	        .andExpect(status().isNoContent());

    	verify(goalService, times(1)).deleteGoal(1L, testUser);

        mockMvc.perform(delete("/dashboard/goals/1"))
                .andExpect(status().is3xxRedirection());

        verify(goalService, times(1)).deleteGoal(1L, testUser);
    }
    
}