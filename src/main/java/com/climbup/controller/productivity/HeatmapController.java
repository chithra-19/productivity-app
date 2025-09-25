package com.climbup.controller.productivity;

import com.climbup.model.Activity;
import com.climbup.model.User;
import com.climbup.service.task.HeatmapService;
import com.climbup.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/heatmap")
public class HeatmapController {

    private final HeatmapService heatmapService;
    private final UserService userService;

    @Autowired
    public HeatmapController(HeatmapService heatmapService, UserService userService) {
        this.heatmapService = heatmapService;
        this.userService = userService;
    }

    /**
     * Get heatmap data for the currently logged-in user.
     *
     * Example: GET /api/heatmap/workout?days=30
     *
     * @param category activity type (or "all" for all types)
     * @param days     number of days back (default 30)
     * @return list of {date, count} maps
     */
    @GetMapping("/{category}")
    public ResponseEntity<List<Map<String, Object>>> getHeatmap(
            @PathVariable String category,
            @RequestParam(defaultValue = "30") int days
    ) {
        User user = userService.getCurrentUser(); // ✅ get current user from JWT
        Activity.ActivityType type = null;

        if (!"all".equalsIgnoreCase(category)) {
            try {
                type = Activity.ActivityType.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(List.of(Map.of("error", "Invalid category: " + category)));
            }
        }

        List<Map<String, Object>> data = heatmapService.getHeatmapData(user.getId(), type, days);
        return ResponseEntity.ok(data);
    }
}
