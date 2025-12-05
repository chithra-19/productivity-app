package com.climbup.controller.productivity;

import com.climbup.dto.response.HeatmapResponse;
import com.climbup.model.Activity;
import com.climbup.model.Activity.ActivityType;
import com.climbup.model.User;
import com.climbup.service.task.HeatmapService;
import com.climbup.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    // Primary, unambiguous endpoint for the frontend
    // GET /api/heatmap/full-data?days=365
    @GetMapping("/full-data")
    public HeatmapResponse getFullHeatmapData(
            @RequestParam(defaultValue = "365") int days,
            Principal principal,
            @RequestParam(required = false) ActivityType type
    ) {
        User user = userService.findByEmail(principal.getName());
        return heatmapService.buildHeatmapResponse(user.getId(), type, days);
    }

    // Category-specific heatmap (returns same shape, but filtered)
    // GET /api/heatmap/{category}?days=30
    @GetMapping("/{category}")
    public ResponseEntity<?> getHeatmapByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "30") int days,
            Principal principal
    ) {
        User user = userService.findByEmail(principal.getName());

        ActivityType type = parseCategory(category);
        if (type == null && !"all".equalsIgnoreCase(category)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid category: " + category));
        }

        HeatmapResponse resp = heatmapService.buildHeatmapResponse(user.getId(), type, days);
        return ResponseEntity.ok(resp);
    }

    // Flat representation: list of day DTOs (if you still need it)
    // GET /api/heatmap/{category}/flat?days=30
    @GetMapping("/{category}/flat")
    public ResponseEntity<?> getFlat(
            @PathVariable String category,
            @RequestParam(defaultValue = "30") int days,
            Principal principal
    ) {
        User user = userService.findByEmail(principal.getName());
        ActivityType type = parseCategory(category);
        if (type == null && !"all".equalsIgnoreCase(category)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid category: " + category));
        }

        List<?> dtos = heatmapService.getHeatmapDTOs(user.getId(), type, days);
        return ResponseEntity.ok(dtos);
    }

    private ActivityType parseCategory(String category) {
        try {
            return ActivityType.valueOf(category.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
