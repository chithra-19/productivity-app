package com.climbup.controller.task;

import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.request.TaskUpdateDTO;
import com.climbup.dto.response.HeatmapDTO;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.repository.TaskRepository;
import com.climbup.repository.UserRepository;
import com.climbup.service.task.TaskService;
import com.climbup.service.user.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskController(TaskService taskService,
                          UserService userService,
                          TaskRepository taskRepository,
                          UserRepository userRepository) {
        this.taskService = taskService;
        this.userService = userService;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /** ✅ Create a new task */
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO dto, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        TaskResponseDTO created = taskService.createTask(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** ✅ Get all tasks for user */
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getTasks(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<TaskResponseDTO> tasks = taskService.getTasksForUser(user);
        return ResponseEntity.ok(tasks);
    }

    /** ✅ Mark task completed & update score/streak */
    @PostMapping("/complete/{id}")
    public ResponseEntity<Map<String, Object>> completeTask(@PathVariable Long id, Principal principal) {
        try {
            // Logged-in email
            String email = principal.getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Task task = taskRepository.findById(id).orElse(null);
            if (task == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Task not found"));
            }

            if (task.isCompleted()) {
                return ResponseEntity.ok(Map.of("message", "Task already completed"));
            }

            task.setCompleted(true);
            taskRepository.save(task);

            int newScore = user.getProductivityScore() + 10;
            int newStreak = user.getCurrentStreak() + 1;

            user.setProductivityScore(newScore);
            user.setCurrentStreak(newStreak);
            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("score", newScore);
            response.put("streak", newStreak);
            response.put("pendingCount", taskRepository.countByUserAndCompletedFalse(user));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    /** ✅ Update task */
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long taskId,
                                                      @RequestBody TaskUpdateDTO dto,
                                                      Principal principal) {
        User user = userService.findByUsername(principal.getName());
        TaskResponseDTO updated = taskService.updateTask(taskId, dto, user);
        return ResponseEntity.ok(updated);
    }

    /** ✅ Delete task */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        taskService.deleteTask(taskId, user);
        return ResponseEntity.noContent().build();
    }

    /** ✅ Heatmap data */
    @GetMapping("/heatmap/all")
    public ResponseEntity<List<HeatmapDTO>> getHeatmapData(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<HeatmapDTO> heatmapData = taskService.getHeatmapData(user);
        return ResponseEntity.ok(heatmapData);
    }

    /** ✅ Task stats */
    @GetMapping("/stats")
    public ResponseEntity<Map<LocalDate, Long>> getTaskStats(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Map<LocalDate, Long> stats = taskService.getTaskStats(user);
        return ResponseEntity.ok(stats);
    }
    

}
