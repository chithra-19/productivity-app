package com.climbup.service.productivity;

import com.climbup.dto.response.HeatmapDTO;
import com.climbup.model.ActivityLog;
import com.climbup.model.User;
import com.climbup.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository repository;
    

    // 🔹 Log a simple event (type + description)
    public void log(User user, String type, String description) {
        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setType(type);
        log.setDescription(description);
        log.setActivityDate(LocalDate.now());
        repository.save(log);
    }

    // 🔹 Fetch latest 10 activities
    public List<ActivityLog> getRecentActivities(User user) {
        return repository.findTop10ByUserOrderByLoggedAtDesc(user);
    }

    // 🔹 Overloaded: Fetch latest N activities
    public List<ActivityLog> getRecentActivities(User user, int limit) {
        return repository.findByUserOrderByLoggedAtDesc(user)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    // 🔹 Fetch logs for heatmap
    public List<ActivityLog> getLogs(User user, LocalDate from, LocalDate to) {
        return repository.findByUserAndActivityDateBetweenOrderByActivityDateDesc(user, from, to);
    }

    // 🔹 Generate heatmap data
    public List<HeatmapDTO> getHeatmapData(User user, String category) {
        return repository.findByUserAndActivityDateBetweenOrderByActivityDateDesc(
                        user,
                        LocalDate.now().minusYears(1),
                        LocalDate.now()
                )
                .stream()
                .map(log -> new HeatmapDTO(
                        log.getActivityDate().toString(),
                        log.getTaskCount(),
                        log.getFocusMinutes(),
                        false // streak logic if needed
                ))
                .collect(Collectors.toList());
    }

    public int getCurrentStreak(User user, String category) {
        List<LocalDate> dates = repository
                .findByUserOrderByActivityDateDesc(user)
                .stream()
                .map(ActivityLog::getActivityDate)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        int streak = 0;
        LocalDate today = LocalDate.now();

        for (LocalDate date : dates) {
            if (date.equals(today.minusDays(streak))) {
                streak++;
            } else break;
        }
        return streak;
    }

	public Object getRecentActivities(Long userId, int limit) {
		// TODO Auto-generated method stub
		return null;
	}
}
