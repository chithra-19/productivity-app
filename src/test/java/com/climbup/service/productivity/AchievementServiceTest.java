package com.climbup.service.productivity;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.climbup.exception.NotFoundException;
import com.climbup.model.Achievement;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.repository.AchievementRepository;
import com.climbup.repository.TaskRepository;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.task.TaskService;
import com.climbup.dto.response.AchievementResponseDTO;

@ExtendWith(MockitoExtension.class)
public class AchievementServiceTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private AchievementService achievementService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = createTestUser(1L, "testuser");
    }

    // ------------------ Helper Methods ------------------
    private User createTestUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    private Achievement createAchievement(String title, User user, boolean unlocked) {
        Achievement achievement = new Achievement();
        achievement.setId((long) (Math.random() * 1000)); // random ID
        achievement.setTitle(title);
        achievement.setDescription(title + " description");
        achievement.setUnlocked(unlocked);
        achievement.setUser(user);
        return achievement;
    }

    private Task createTask(LocalDate completionDate, boolean completed) {
        Task task = new Task();
        task.setCompletionDate(completionDate);
        task.setCompleted(completed);
        return task;
    }

    // ------------------ Tests ------------------

    @Test
    void checkForNewAchievements_ShouldUnlockStreakAchievement() {
        LocalDate today = LocalDate.now();
        List<Task> tasks = List.of(
            createTask(today, true),
            createTask(today.minusDays(1), true),
            createTask(today.minusDays(2), true)
        );

        Achievement achievement = createAchievement("Streak Starter", testUser, false);

        when(taskRepository.findByUserAndCompletedTrueOrderByCompletedDateTimeDesc(testUser)).thenReturn(tasks);
        when(achievementRepository.findByUserAndUnlocked(testUser, false)).thenReturn(List.of(achievement));

        achievementService.checkForNewAchievements(testUser);

        ArgumentCaptor<List<Achievement>> captor = ArgumentCaptor.forClass(List.class);
        verify(achievementRepository).saveAll(captor.capture());

        List<Achievement> saved = captor.getValue();
        Achievement updated = saved.get(0);
        assertTrue(updated.isUnlocked());
        assertTrue(updated.isNewlyUnlocked());
        assertNotNull(updated.getUnlockedDate());
    }

    @Test
    void checkForNewAchievements_ShouldDoNothing_WhenNoLockedAchievements() {
        when(achievementRepository.findByUserAndUnlocked(testUser, false)).thenReturn(Collections.emptyList());

        achievementService.checkForNewAchievements(testUser);

        verify(achievementRepository, never()).saveAll(any());
    }

    @Test
    void getUserAchievements_ShouldReturnDTOList() {
        Achievement achievement = createAchievement("First Step", testUser, false);
        when(achievementRepository.findByUser(testUser)).thenReturn(List.of(achievement));

        List<AchievementResponseDTO> result = achievementService.getUserAchievements(testUser);

        assertEquals(1, result.size());
        assertEquals("First Step", result.get(0).getTitle());
    }

    @Test
    void initializeAchievements_ShouldSaveDefaults_WhenNoneExist() {
        when(achievementRepository.countByUser(testUser)).thenReturn(0L);

        achievementService.initializeAchievements(testUser);

        ArgumentCaptor<List<Achievement>> captor = ArgumentCaptor.forClass(List.class);
        verify(achievementRepository).saveAll(captor.capture());

        List<Achievement> saved = captor.getValue();
        assertTrue(saved.stream().anyMatch(a -> a.getTitle().equals("First Step")));
        assertTrue(saved.stream().anyMatch(a -> a.getTitle().equals("Streak Starter")));
        assertTrue(saved.stream().anyMatch(a -> a.getTitle().equals("Task Master")));
        assertTrue(saved.stream().anyMatch(a -> a.getTitle().equals("Early Bird")));
        assertTrue(saved.stream().anyMatch(a -> a.getTitle().equals("Productivity Pro")));
    }

    @Test
    void unlockAchievement_ShouldUnlockAchievement_WhenValid() {
        Achievement locked = createAchievement("First Step", testUser, false);
        when(achievementRepository.findById(locked.getId())).thenReturn(Optional.of(locked));

        AchievementResponseDTO result = achievementService.unlockAchievement(locked.getId(), testUser);

        assertTrue(result.isUnlocked());
        assertEquals("First Step", result.getTitle());
        assertNotNull(result.getUnlockedDate());
    }

    @Test
    void unlockAchievement_ShouldThrowNotFound_WhenInvalidId() {
        when(achievementRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            achievementService.unlockAchievement(999L, testUser);
        });
    }
}
