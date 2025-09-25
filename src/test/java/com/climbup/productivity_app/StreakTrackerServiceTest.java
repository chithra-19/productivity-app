package com.climbup.productivity_app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.climbup.model.StreakTracker;
import com.climbup.model.User;
import com.climbup.repository.StreakTrackerRepository;
import com.climbup.service.productivity.StreakTrackerService;
import com.climbup.service.task.ActivityService;
import com.climbup.model.Activity.ActivityType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Optional;

public class StreakTrackerServiceTest {

    @Mock
    private StreakTrackerRepository repository;

    @Mock
    private ActivityService activityService;

    @InjectMocks
    private StreakTrackerService streakTrackerService;

    private User testUser;
    private StreakTracker streak;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        streak = new StreakTracker();
        streak.setId(1L);
        streak.setUser(testUser);
        streak.setCategory("Task");
        streak.setCurrentStreak(3);
        streak.setLongestStreak(5);
        streak.setLastActiveDate(LocalDate.now().minusDays(1));
    }

    @Test
    void updateStreak_ShouldContinueStreak() {
        when(repository.findByUserIdAndCategory(testUser.getId(), "Task"))
                .thenReturn(Optional.of(streak));
        when(repository.save(any(StreakTracker.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StreakTracker updated = streakTrackerService.updateStreak(testUser, "Task");

        assertEquals(4, updated.getCurrentStreak());
        verify(activityService).log(anyString(), eq(ActivityType.STREAK), eq(testUser));
        verify(repository).save(updated);
    }

    @Test
    void updateStreak_ShouldResetStreak() {
        streak.setLastActiveDate(LocalDate.now().minusDays(3));
        when(repository.findByUserIdAndCategory(testUser.getId(), "Task"))
                .thenReturn(Optional.of(streak));
        when(repository.save(any(StreakTracker.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StreakTracker updated = streakTrackerService.updateStreak(testUser, "Task");

        assertEquals(1, updated.getCurrentStreak());
        verify(activityService).log(anyString(), eq(ActivityType.STREAK), eq(testUser));
    }

    @Test
    void updateStreak_ShouldCreateNewStreakIfNotExist() {
        when(repository.findByUserIdAndCategory(testUser.getId(), "Task"))
                .thenReturn(Optional.empty());
        when(repository.save(any(StreakTracker.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StreakTracker newStreak = streakTrackerService.updateStreak(testUser, "Task");

        assertEquals(1, newStreak.getCurrentStreak());
        assertEquals(1, newStreak.getLongestStreak());
        assertEquals(LocalDate.now(), newStreak.getLastActiveDate());
        verify(activityService).log(contains("Started first streak"), eq(ActivityType.STREAK), eq(testUser));
    }

    @Test
    void getStreakByUserAndCategory_ShouldReturnStreak() {
        when(repository.findByUserIdAndCategory(testUser.getId(), "Task"))
                .thenReturn(Optional.of(streak));

        StreakTracker result = streakTrackerService.getStreakByUserAndCategory(testUser.getId(), "Task");

        assertNotNull(result);
        assertEquals(3, result.getCurrentStreak());
    }

    @Test
    void getAllStreaksForUser_ShouldReturnList() {
        when(repository.findAllByUserId(testUser.getId()))
                .thenReturn(java.util.List.of(streak));

        var result = streakTrackerService.getAllStreaksForUser(testUser.getId());

        assertEquals(1, result.size());
        assertEquals(streak, result.get(0));
    }
}
