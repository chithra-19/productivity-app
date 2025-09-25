package com.climbup.productivity_app;

import com.climbup.controller.productivity.AchievementController;
import com.climbup.dto.request.AchievementRequestDTO;
import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.model.User;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AchievementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AchievementService achievementService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AchievementController achievementController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(achievementController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @WithMockUser(username = "testuser") // simulates a logged-in session
    void createAchievement_ShouldReturnCreatedAchievement() throws Exception {
        // Prepare request DTO
        AchievementRequestDTO requestDTO = new AchievementRequestDTO();
        requestDTO.setTitle("Test Achievement");
        requestDTO.setDescription("Test Description");
        requestDTO.setCategory("GENERAL");
        requestDTO.setType(com.climbup.model.Achievement.Type.GOAL);
        requestDTO.setUnlockedDate(LocalDate.now());

        // Mock the currently logged-in user
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        // Mock the response DTO using setters (no-args constructor)
        AchievementResponseDTO responseDTO = new AchievementResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setTitle("Test Achievement");
        responseDTO.setDescription("Test Description");
        responseDTO.setCategory("GENERAL");
        responseDTO.setUnlocked(false);
        responseDTO.setNewlyUnlocked(true);

        // Mock service behavior
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(achievementService.createAchievement(any(AchievementRequestDTO.class), any(User.class)))
                .thenReturn(responseDTO);

        // Perform POST request and assert
        mockMvc.perform(post("/api/achievements/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Achievement"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.category").value("GENERAL"))
                .andExpect(jsonPath("$.unlocked").value(false))
                .andExpect(jsonPath("$.newlyUnlocked").value(true));
    }
}
