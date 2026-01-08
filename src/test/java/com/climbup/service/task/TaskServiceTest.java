package com.climbup.service.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
public class TaskServiceTest {

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

    private User testUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setUser(testUser);
        testTask.setCompleted(false);
    }

    private Optional<Task> taskOptional() {
        return Optional.of(testTask);
    }

    @Test
    void completeTask_ShouldCompleteTaskAndUpdateStreakAndAchievements() {
        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(taskOptional());
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        TaskResponseDTO response = taskService.completeTask(1L, testUser);

        assertTrue(response.isCompleted());
        assertNotNull(response.getCompletedDateTime());

        verify(streakTrackerService).updateStreak(testUser);
        verify(achievementService).checkForNewAchievements(testUser);
        verify(activityService).log(contains("Completed Task: " + testTask.getTitle()), eq(ActivityType.TASK), eq(testUser));
    }

    @Test
    void completeTask_AlreadyCompleted_ShouldReturnSameTaskResponse() {
        testTask.setCompleted(true);
        testTask.setCompletedDateTime(LocalDateTime.now().minusDays(1));

        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(taskOptional());

        TaskResponseDTO response = taskService.completeTask(1L, testUser);

        assertTrue(response.isCompleted());
        assertEquals(testTask.getCompletedDateTime(), response.getCompletedDateTime());

        verify(streakTrackerService, never()).updateStreak(any());
        verify(achievementService, never()).checkForNewAchievements(any());
        verify(activityService, never()).log(anyString(), any(), any());
    }

    @Test
    void completeTask_TaskNotFound_ShouldThrowException() {
        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> taskService.completeTask(1L, testUser));

        verify(streakTrackerService, never()).updateStreak(any());
        verify(achievementService, never()).checkForNewAchievements(any());
        verify(activityService, never()).log(anyString(), any(), any());
    }

    @Test
    void createTask_ShouldSaveTaskAndLogActivity() {
        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("New Task");

        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        TaskResponseDTO response = taskService.createTask(dto, testUser);

        assertEquals("New Task", response.getTitle());
        assertFalse(response.isCompleted());

        verify(activityService).log(contains("Created Task: " + response.getTitle()), eq(ActivityType.TASK), eq(testUser));
    }

    @Test
    void createTask_EmptyTitle_ShouldThrowException() {
        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("");

        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(dto, testUser));
        verifyNoInteractions(taskRepository, activityService, streakTrackerService, achievementService);
    }

    @Test
    void updateTask_ShouldUpdateAndSaveTask() {
        TaskUpdateDTO dto = new TaskUpdateDTO();
        dto.setTitle("Updated Task");
        dto.setCompleted(true);

        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(taskOptional());
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponseDTO response = taskService.updateTask(1L, dto, testUser);

        assertEquals("Updated Task", response.getTitle());
        assertTrue(response.isCompleted());
        verify(taskRepository).save(any(Task.class));
        verify(streakTrackerService).updateStreak(testUser);
        verify(achievementService).checkForNewAchievements(testUser);
        verify(activityService).log(contains("Updated Task: " + testTask.getTitle()), eq(ActivityType.TASK), eq(testUser));
    }

    @Test
    void deleteTask_ShouldDeleteAndLog() {
        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(taskOptional());

        taskService.deleteTask(1L, testUser);

        verify(taskRepository).delete(testTask);
        verify(activityService).log(contains("Deleted Task: " + testTask.getTitle()), eq(ActivityType.TASK), eq(testUser));
    }
}
