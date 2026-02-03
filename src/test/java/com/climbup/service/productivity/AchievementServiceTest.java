package com.climbup.service.productivity;

import com.climbup.dto.request.AchievementRequestDTO;
import com.climbup.dto.response.AchievementResponseDTO;
import com.climbup.model.Achievement;
import com.climbup.model.Achievement.Type;
import com.climbup.model.User;
import com.climbup.repository.AchievementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AchievementServiceTest {

    private AchievementRepository achievementRepository;
    private AchievementService achievementService;

    private User testUser;

    @BeforeEach
    void setUp() {
        achievementRepository = mock(AchievementRepository.class);
        achievementService = new AchievementService(achievementRepository, null, null);

        testUser = new User("testuser", "test@example.com", "password");
        testUser.setId(1L);
    }

    @Test
    void createAchievement_shouldReturnResponseDTO() {
        AchievementRequestDTO dto = new AchievementRequestDTO();
        dto.setTitle("Test Achievement");
        dto.setDescription("Test Description");
        dto.setType(Type.GOAL);
        dto.setCategory("GENERAL");
        dto.setUnlockedDate(LocalDate.now());

        // Mock save to return the achievement
        when(achievementRepository.save(any(Achievement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AchievementResponseDTO responseDTO = achievementService.createAchievement(dto, testUser);

        assertNotNull(responseDTO);
        assertEquals("Test Achievement", responseDTO.getTitle());
        assertEquals("Test Description", responseDTO.getDescription());
        assertEquals("GENERAL", responseDTO.getCategory());

        verify(achievementRepository, times(1)).save(any(Achievement.class));
    }

    @Test
    void unlockAchievement_shouldUnlockAndReturnDTO() {
        Achievement achievement = new Achievement();
        achievement.setId(100L);
        achievement.setUser(testUser);
        achievement.setUnlocked(false);

        when(achievementRepository.findById(100L)).thenReturn(Optional.of(achievement));
        when(achievementRepository.save(any(Achievement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AchievementResponseDTO dto = achievementService.unlockAchievement(100L, testUser);

        assertTrue(dto.isUnlocked());
        verify(achievementRepository, times(1)).save(achievement);
    }

    @Test
    void checkForNewAchievements_shouldReturnTrueIfAny() {
        Achievement a = new Achievement();
        a.setNewlyUnlocked(true);

        when(achievementRepository.findByUserAndNewlyUnlockedTrue(testUser))
                .thenReturn(java.util.List.of(a));

        boolean result = achievementService.checkForNewAchievements(testUser);

        assertTrue(result);
    }

    @Test
    void checkForNewAchievements_shouldReturnFalseIfNone() {
        when(achievementRepository.findByUserAndNewlyUnlockedTrue(testUser))
                .thenReturn(java.util.List.of());

        boolean result = achievementService.checkForNewAchievements(testUser);

        assertFalse(result);
    }
}
