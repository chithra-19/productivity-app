package com.climbup.controller.productivity;

import com.climbup.dto.request.AchievementRequestDTO;
import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.model.AchievementType;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @DisplayName("GET /api/achievements should return user achievements")
    @WithMockUser(username = "testuser")
    void getUserAchievements_ShouldReturnList() throws Exception {

        User mockUser = new User("test@example.com", "password");

        AchievementResponseDTO dto = new AchievementResponseDTO();
        dto.setId(1L);
        dto.setTitle("Test Achievement");
        dto.setDescription("Test Description");
        dto.setCategory("GENERAL");
        dto.setUnlocked(false);
        dto.setNewlyUnlocked(true);

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(achievementService.getUserAchievements(mockUser.getId()))
                .thenReturn(java.util.List.of(dto));

        mockMvc.perform(get("/api/achievements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Achievement"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].category").value("GENERAL"))
                .andExpect(jsonPath("$[0].unlocked").value(false))
                .andExpect(jsonPath("$[0].newlyUnlocked").value(true));
    }
}