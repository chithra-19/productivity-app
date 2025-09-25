package com.climbup.productivity_app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.request.TaskUpdateDTO;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.model.Activity.ActivityType;
import com.climbup.repository.TaskRepository;
import com.climbup.service.productivity.AchievementService;
import com.climbup.service.productivity.StreakTrackerService;
import com.climbup.service.task.ActivityService;
import com.climbup.service.task.TaskService;

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

    @Test
    void completeTask_ShouldCompleteTaskAndUpdateStreakAndAchievements() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        TaskResponseDTO response = taskService.completeTask(1L);

        // Verify task is marked completed
        assertTrue(response.isCompleted());
        assertNotNull(response.getCompletionDate());
        assertNotNull(response.getCompletedDateTime());

        // Verify streak update and achievement check
        verify(streakTrackerService, times(1)).updateStreak(testUser);
        verify(achievementService, times(1)).checkForNewAchievements(testUser);

        // Verify activity logging
        verify(activityService, times(1)).log(
                contains("Completed Task: " + testTask.getTitle()),
                eq(ActivityType.TASK),
                eq(testUser)
        );
    }

    @Test
    void completeTask_AlreadyCompleted_ShouldReturnSameTaskResponse() {
        testTask.setCompleted(true);
        testTask.setCompletionDate(LocalDate.now().minusDays(1));
        testTask.setCompletedDateTime(LocalDateTime.now().minusDays(1));

        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        TaskResponseDTO response = taskService.completeTask(1L);

        // Should remain completed
        assertTrue(response.isCompleted());
        assertEquals(testTask.getCompletionDate(), response.getCompletionDate());
        assertEquals(testTask.getCompletedDateTime(), response.getCompletedDateTime());

        // Streak and achievements should NOT be called
        verify(streakTrackerService, never()).updateStreak(any());
        verify(achievementService, never()).checkForNewAchievements(any());
        verify(activityService, never()).log(anyString(), any(), any());
    }

    @Test
    void completeTask_TaskNotFound_ShouldThrowException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> taskService.completeTask(1L));

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

        verify(activityService, times(1))
                .log(contains("Created Task: " + response.getTitle()), eq(ActivityType.TASK), eq(testUser));
    }

    @Test
    void getTasksForUser_ShouldReturnMappedTasks() {
        when(taskRepository.findByUser(testUser)).thenReturn(List.of(testTask));

        List<TaskResponseDTO> result = taskService.getTasksForUser(testUser);

        assertEquals(1, result.size());
        assertEquals(testTask.getTitle(), result.get(0).getTitle());
    }

    @Test
    void updateTask_ShouldUpdateAndSaveTask() {
        TaskUpdateDTO dto = new TaskUpdateDTO();
        dto.setTitle("Updated Title");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        TaskResponseDTO response = taskService.updateTask(1L, dto, testUser);

        assertEquals("Updated Title", response.getTitle());
        verify(activityService, times(1))
                .log(contains("Updated Task: " + response.getTitle()), eq(ActivityType.TASK), eq(testUser));
    }

    @Test
    void deleteTask_ShouldDeleteAndLog() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        taskService.deleteTask(1L, testUser);

        verify(taskRepository, times(1)).delete(testTask);
        verify(activityService, times(1))
                .log(contains("Deleted Task: " + testTask.getTitle()), eq(ActivityType.TASK), eq(testUser));
    }
}
