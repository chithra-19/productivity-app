package com.climbup.admin.service;

import com.climbup.admin.dto.AdminDashboardDTO;
import com.climbup.admin.dto.RecentActivityDTO;
import com.climbup.model.Activity;
import com.climbup.repository.ActivityRepository;
import com.climbup.repository.TaskRepository;
import com.climbup.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ActivityRepository activityRepository;

    public AdminDashboardService(UserRepository userRepository,
                                 TaskRepository taskRepository,
                                 ActivityRepository activityRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.activityRepository = activityRepository;
    }

    public AdminDashboardDTO getDashboardStats() {

        AdminDashboardDTO dto = new AdminDashboardDTO();

        // 👥 Users
        long totalUsers = userRepository.count();
        long activeUsersLast7Days =
                userRepository.countByLastLoginAtAfter(LocalDateTime.now().minusDays(7));

        // 📝 Tasks
        long totalTasks = taskRepository.count();
        long completedTasks = taskRepository.countByCompletedTrue();
        long pendingTasks = taskRepository.countByCompletedFalse();

        // 📊 Recent Activities (latest 10)
        List<RecentActivityDTO> recentActivities =
                activityRepository.findTop10ByOrderByCreatedAtDesc()
                        .stream()
                        .map(this::mapToRecentActivityDTO)
                        .collect(Collectors.toList());

        dto.setTotalUsers(totalUsers);
        dto.setActiveUsersLast7Days(activeUsersLast7Days);
        dto.setTotalTasks(totalTasks);
        dto.setCompletedTasks(completedTasks);
        dto.setPendingTasks(pendingTasks);
        dto.setRecentActivities(recentActivities);

        return dto;
    }

    private RecentActivityDTO mapToRecentActivityDTO(Activity activity) {
        RecentActivityDTO dto = new RecentActivityDTO();
        dto.setUserEmail(activity.getUser().getEmail());
        dto.setAction(activity.getDescription());
        dto.setType(activity.getType().name());
        dto.setTimestamp(activity.getTimestamp());
        return dto;
    }
}
