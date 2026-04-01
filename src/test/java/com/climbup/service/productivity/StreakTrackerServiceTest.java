package com.climbup.service.productivity;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.climbup.model.StreakTracker;
import com.climbup.model.User;
import com.climbup.repository.StreakTrackerRepository;
import com.climbup.service.productivity.StreakTrackerService;
import com.climbup.service.task.ActivityService;
import com.climbup.model.ActivityType;

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
       

        streak = new StreakTracker();
        streak.setId(1L);
        streak.setUser(testUser);
        streak.setCategory("Task");
        streak.setCurrentStreak(3);
        streak.setLongestStreak(5);
        streak.setLastActiveDate(LocalDate.now().minusDays(1));
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
