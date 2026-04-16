package com.climbup.service.productivity;

import com.climbup.model.User;
import com.climbup.model.UserAchievement;
import com.climbup.repository.UserAchievementRepository;
import com.climbup.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementServiceTest {

    @Mock
    private UserAchievementRepository achievementRepository;

    @Mock
    private UserAchievementRepository userAchievementRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AchievementService achievementService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "password");
    }

    @Test
    void hasNewAchievements_shouldReturnTrue_whenNewExists() {

        when(userAchievementRepository.findByUserAndNewlyUnlockedTrue(testUser))
                .thenReturn(List.of(new UserAchievement()));

        boolean result = achievementService.hasNew(testUser);

        assertTrue(result);
    }

    @Test
    void hasNewAchievements_shouldReturnFalse_whenNoneExists() {

        when(userAchievementRepository.findByUserAndNewlyUnlockedTrue(testUser))
                .thenReturn(List.of());

        boolean result = achievementService.hasNew(testUser);

        assertFalse(result);
    }

    @Test
    void markSeen_shouldCallRepository() {

        UserAchievement ua = new UserAchievement();

        when(userAchievementRepository.findByUserAndNewlyUnlockedTrue(testUser))
                .thenReturn(List.of(ua));

        achievementService.markSeen(testUser);

        verify(userAchievementRepository, times(1))
                .findByUserAndNewlyUnlockedTrue(testUser);
    }
}