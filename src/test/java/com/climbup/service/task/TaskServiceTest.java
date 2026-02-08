package com.climbup.service.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.request.TaskUpdateDTO;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.model.Activity.ActivityType;
import com.climbup.repository.TaskRepository;

import com.climbup.service.productivity.AchievementService;
import com.climbup.service.productivity.StreakTrackerService;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ActivityService activityService;

    @Mock
    private StreakTrackerService streakTrackerService;

    @Mock
    private AchievementService achievementService;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setUser(user);
        task.setCompleted(false);
    }

    /* ---------------- CREATE TASK ---------------- */

    @Test
    void createTask_shouldSaveTaskAndLogActivity() {
        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("New Task");

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponseDTO response = taskService.createTask(dto, user);

        assertEquals("New Task", response.getTitle());
        assertFalse(response.isCompleted());

        verify(activityService)
                .log(contains("Created Task"), eq(ActivityType.TASK), eq(user));
    }

    @Test
    void createTask_emptyTitle_shouldThrowException() {
        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("");

        assertThrows(IllegalArgumentException.class,
                () -> taskService.createTask(dto, user));

        verifyNoInteractions(taskRepository, activityService);
    }

    /* ---------------- UPDATE TASK (COMPLETION INCLUDED) ---------------- */

    @Test
    void updateTask_markCompleted_shouldUpdateStreakAndAchievements() {
        TaskUpdateDTO dto = new TaskUpdateDTO();
        dto.setCompleted(true);

        when(taskRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponseDTO response = taskService.updateTask(1L, dto, user);

        assertTrue(response.isCompleted());
        assertNotNull(response.getCompletedDateTime());

        verify(streakTrackerService).updateStreak(user);
        verify(achievementService).checkForNewAchievements(user);
        verify(activityService)
                .log(contains("Updated Task"), eq(ActivityType.TASK), eq(user));
    }

    @Test
    void updateTask_alreadyCompleted_shouldNotUpdateStreakAgain() {
        task.setCompleted(true);
        task.setCompletedDateTime(LocalDateTime.now().minusDays(1));

        TaskUpdateDTO dto = new TaskUpdateDTO();
        dto.setCompleted(true);

        when(taskRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(task));

        TaskResponseDTO response = taskService.updateTask(1L, dto, user);

        assertTrue(response.isCompleted());
        assertEquals(task.getCompletedDateTime(), response.getCompletedDateTime());

        verify(streakTrackerService, never()).updateStreak(any());
        verify(achievementService, never()).checkForNewAchievements(any());
    }

    @Test
    void updateTask_taskNotFound_shouldThrowException() {
        TaskUpdateDTO dto = new TaskUpdateDTO();
        dto.setCompleted(true);

        when(taskRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> taskService.updateTask(1L, dto, user));
    }

    /* ---------------- DELETE TASK ---------------- */

    @Test
    void deleteTask_shouldDeleteAndLogActivity() {
        when(taskRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(task));

        taskService.deleteTask(1L, user);

        verify(taskRepository).delete(task);
        verify(activityService)
                .log(contains("Deleted Task"), eq(ActivityType.TASK), eq(user));
    }
}
