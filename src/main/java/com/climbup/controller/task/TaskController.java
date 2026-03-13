package com.climbup.controller.task;

import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.request.TaskUpdateDTO;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.repository.TaskRepository;
import com.climbup.repository.UserRepository;
import com.climbup.service.productivity.StreakTrackerService;
import com.climbup.service.productivity.XPService;
import com.climbup.service.task.TaskService;
import com.climbup.service.user.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final StreakTrackerService streakTrackerService;
    private final XPService xpService;
    

    @Autowired
    public TaskController(TaskService taskService,
                          UserService userService,
                          TaskRepository taskRepository,
                          UserRepository userRepository,
                          StreakTrackerService streakTrackerService,
                          XPService xpService) {
        this.taskService = taskService;
        this.userService = userService;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.streakTrackerService = streakTrackerService ;
        this.xpService = xpService;
        }

    /** ✅ Create a new task */
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO dto, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        TaskResponseDTO created = taskService.createTask(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** ✅ Get all tasks for user */
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getTasks(Principal principal) {
        User user = userService.findByEmail(principal.getName());
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

         // 1️⃣ Update streak (category-based)
            streakTrackerService.evaluateToday(user, task.getCategory());

            // 2️⃣ Update XP
            xpService.handleTaskCompletion(user, task);

            // 3️⃣ Get fresh streak value
            int currentStreak = streakTrackerService.getCurrentStreak(user);

            // 4️⃣ Get XP
            int xp = user.getXp();


            Map<String, Object> response = new HashMap<>();
            response.put("xp", xp);
            response.put("streak", currentStreak);
            response.put("pendingTasks", taskRepository.countByUserAndCompletedFalse(user));
            response.put("xpIncrement", 10);


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
        User user = userService.findByEmail(principal.getName());
        TaskResponseDTO updated = taskService.updateTask(taskId, dto, user);
        return ResponseEntity.ok(updated);
    }


   

    /** ✅ Task stats */
    @GetMapping("/stats")
    public ResponseEntity<Map<LocalDate, Long>> getTaskStats(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Map<LocalDate, Long> stats = taskService.getTaskStats(user);
        return ResponseEntity.ok(stats);
    }
    
 // Fetch single task for editing
    @GetMapping("/single/{taskId}") 
    public ResponseEntity<TaskResponseDTO> getTask(@PathVariable Long taskId, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        TaskResponseDTO task = taskService.getTaskById(taskId, user);
        if(task == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(task);
    }
   
 // Delete task
    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<Map<String, Object>> deleteTaskForFrontend(
            @PathVariable Long taskId,
            Principal principal) {

        User user = userService.findByEmail(principal.getName());
        taskService.deleteTask(taskId, user);

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
    public ResponseEntity<Map<String, Object>> markTaskDone(@PathVariable Long id) {
        // Mark task as completed
        Task task = taskService.markDone(id); // sets task.completed = true

        User user = task.getUser(); // get the task owner

        streakTrackerService.evaluateToday(user, task.getCategory());
        int currentStreak = streakTrackerService.getCurrentStreak(user);


        // XP is the user's current productivity score
        int xp = user.getProductivityScore();

        // Pending tasks
        long pendingCount = taskRepository.countByUserAndCompletedFalse(user);

        Map<String, Object> resp = new HashMap<>();
        resp.put("streak", currentStreak);
        resp.put("xp", xp);
        resp.put("pendingCount", pendingCount);
        resp.put("xpIncrement", 10); // optional, for toast message

        return ResponseEntity.ok(resp);
    }



}
