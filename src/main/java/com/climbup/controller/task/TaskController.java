package com.climbup.controller.task;

import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.request.TaskUpdateDTO;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.model.User;
import com.climbup.repository.TaskRepository;
import com.climbup.service.productivity.StreakTrackerService;
import com.climbup.service.task.TaskCommandService;
import com.climbup.service.task.TaskQueryService;
import com.climbup.service.task.TaskStatsService;
import com.climbup.service.user.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskCommandService taskCommandService;
    private final UserService userService;
    private final TaskRepository taskRepository;
    private final TaskQueryService taskQueryService;
    private final StreakTrackerService streakTrackerService;
    private final TaskStatsService taskStatsService;
    

    @Autowired
    public TaskController(TaskCommandService taskCommandService,
                          UserService userService,
                          TaskRepository taskRepository,
                          TaskQueryService taskQueryService,
                          StreakTrackerService streakTrackerService,
                          TaskStatsService taskStatsService) {
        this.taskCommandService = taskCommandService;
        this.userService = userService;
        this.taskRepository = taskRepository;
        this.taskQueryService = taskQueryService;
        this.streakTrackerService = streakTrackerService ;
        this.taskStatsService = taskStatsService;
        }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllTasksPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal
    ) {
        User user = userService.findByEmail(principal.getName());

        Page<TaskResponseDTO> taskPage =
                taskQueryService.getTasksForUserPaginated(user, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("tasks", taskPage.getContent());
        response.put("currentPage", page);
        response.put("totalPages", taskPage.getTotalPages());

        return ResponseEntity.ok(response);
    }
    
    /** ✅ Create a new task */
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO dto, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        TaskResponseDTO created = taskCommandService.createTask(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** ✅ Get all tasks for user */
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getTasks(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<TaskResponseDTO> tasks = taskQueryService.getTasksForUser(user);
        return ResponseEntity.ok(tasks);
    }

    /** ✅ Update task */
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long taskId,
                                                      @RequestBody TaskUpdateDTO dto,
                                                      Principal principal) {
        User user = userService.findByEmail(principal.getName());
        TaskResponseDTO updated = taskCommandService.updateTask(taskId, dto, user);
        return ResponseEntity.ok(updated);
    }


   

    /** ✅ Task stats */
    @GetMapping("/stats")
    public ResponseEntity<Map<LocalDate, Long>> getTaskStats(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Map<LocalDate, Long> stats = taskStatsService.getTaskStats(user);
        return ResponseEntity.ok(stats);
    }
    
 // Fetch single task for editing
    @GetMapping("/single/{taskId}") 
    public ResponseEntity<TaskResponseDTO> getTask(@PathVariable Long taskId, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        TaskResponseDTO task = taskQueryService.getTaskById(taskId, user);
        if(task == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(task);
    }
   
 // Delete task
    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<Map<String, Object>> deleteTaskForFrontend(
            @PathVariable Long taskId,
            Principal principal) {

        User user = userService.findByEmail(principal.getName());
        taskCommandService.deleteTask(taskId, user);

        int currentStreak = streakTrackerService.getCurrentStreak(user);
        int xp = user.getXp();
        long pendingTasks = taskRepository.countByUserAndCompletedFalse(user);

        Map<String, Object> response = new HashMap<>();
        response.put("pendingTasks", pendingTasks);
        response.put("streak", currentStreak);
        response.put("xp", xp);

        return ResponseEntity.ok(response);
   
    }
    
    @PutMapping("/mark-done/{id}")
    public ResponseEntity<Map<String, Object>> markTaskDone(
            @PathVariable Long id,
            Principal principal
    ) {

        User user = userService.findByEmail(principal.getName());

        TaskResponseDTO task = taskCommandService.markDone(id, user);

        int currentStreak = streakTrackerService.getCurrentStreak(user);
        long pendingCount = taskRepository.countByUserAndCompletedFalse(user);

        Map<String, Object> resp = new HashMap<>();
        resp.put("task", task);
        resp.put("streak", currentStreak);
        resp.put("pendingTasks", pendingCount);
        resp.put("xp", user.getXp());

        return ResponseEntity.ok(resp);
    }


}
