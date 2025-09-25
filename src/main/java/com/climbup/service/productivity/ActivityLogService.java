package com.climbup.service.productivity;

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

    public void logActivity(User user, String category, LocalDate date) {
        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setCategory(category);
        log.setActivityDate(date);
        repository.save(log);
    }
    
    public int getCurrentStreak(User user, String category) {
        List<LocalDate> dates = getLogs(user, category, LocalDate.now().minusDays(30), LocalDate.now())
            .stream()
            .map(ActivityLog::getActivityDate)
            .distinct()
            .sorted(Comparator.reverseOrder()) // latest first
            .collect(Collectors.toList());

        int streak = 0;
        LocalDate today = LocalDate.now();

        for (LocalDate date : dates) {
            if (date.equals(today.minusDays(streak))) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }

    public List<ActivityLog> getLogs(User user, String category, LocalDate from, LocalDate to) {
        return repository.findByUserAndCategoryAndActivityDateBetween(user, category, from, to);
    }

	public Object getHeatmapData(User user) {
		// TODO Auto-generated method stub
		return null;
	}
}