package com.climbup.controller.productivity;

import com.climbup.dto.request.AchievementRequestDTO;
import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.model.Achievement.Type;
import com.climbup.model.User;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AchievementController.class)
class AchievementControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AchievementService achievementService;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("POST /api/achievements/create returns created achievement")
    @WithMockUser(username = "testuser")
    void createAchievement_ShouldReturnCreatedAchievement() throws Exception {
        AchievementRequestDTO requestDTO = new AchievementRequestDTO();
        requestDTO.setTitle("Test Achievement");
        requestDTO.setDescription("Test Description");
        requestDTO.setCategory("GENERAL");
        requestDTO.setType(Type.GOAL);
        requestDTO.setUnlockedDate(LocalDate.now());

        User mockUser = new User("testuser", "test@example.com", "password");
        mockUser.setId(1L);

        AchievementResponseDTO responseDTO = new AchievementResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setTitle("Test Achievement");
        responseDTO.setDescription("Test Description");
        responseDTO.setCategory("GENERAL");
        responseDTO.setUnlocked(false);
        responseDTO.setNewlyUnlocked(true);

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(achievementService.createAchievement(any(AchievementRequestDTO.class), any(User.class)))
                .thenReturn(responseDTO);

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
