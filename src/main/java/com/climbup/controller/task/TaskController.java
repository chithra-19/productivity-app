package com.climbup.controller.task;

import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.request.TaskUpdateDTO;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.model.User;
import com.climbup.service.task.TaskService;
import com.climbup.service.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    // ✅ Create task
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @RequestBody TaskRequestDTO dto, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        TaskResponseDTO created = taskService.createTask(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ✅ Get all tasks for current user
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getTasks(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<TaskResponseDTO> tasks = taskService.getTasksForUser(user);
        return ResponseEntity.ok(tasks);
    }

    // ✅ Complete task
    @PostMapping("/{taskId}/complete")
    public ResponseEntity<TaskResponseDTO> completeTask(@PathVariable Long taskId) {
        TaskResponseDTO updated = taskService.completeTask(taskId);
        return ResponseEntity.ok(updated);
    }

    // ✅ Update task
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long taskId,
            @RequestBody TaskUpdateDTO dto,
            Principal principal
    ) {
        User user = userService.findByUsername(principal.getName());
        TaskResponseDTO updated = taskService.updateTask(taskId, dto, user);
        return ResponseEntity.ok(updated);
    }

    // ✅ Delete task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        taskService.deleteTask(id, user);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
