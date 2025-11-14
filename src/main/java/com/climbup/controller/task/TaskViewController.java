package com.climbup.controller.task;

import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.model.User;
import com.climbup.service.task.TaskService;
import com.climbup.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tasks")
public class TaskViewController {

    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public TaskViewController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    // Show all tasks for logged-in user
    @GetMapping("/all")
    public String showAllTasks(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<TaskResponseDTO> tasks = taskService.getTasksForUser(user);
        model.addAttribute("tasks", tasks);
        return "tasks/task-all"; // Thymeleaf view
    }

    // Show today's tasks for logged-in user
    @GetMapping("/today")
    public String showTodayTasks(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        LocalDate today = LocalDate.now();

        List<TaskResponseDTO> todayTasks = taskService.getTasksForUser(user)
                .stream()
                .filter(t -> today.equals(t.getDueDate()))
                .collect(Collectors.toList());

        model.addAttribute("tasks", todayTasks);
        return "tasks/task-today"; // Thymeleaf view
    }

    // Show Add Task form
    @GetMapping("/add")
    public String showAddTaskForm(Model model) {
        model.addAttribute("task", new com.climbup.dto.request.TaskRequestDTO());
        return "tasks/add-task"; // Thymeleaf view
    }
    
    @PostMapping("/save")
    public String saveTask(@ModelAttribute("task") TaskRequestDTO taskDTO,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(principal.getName());
            taskService.createTask(taskDTO, user); // ✅ Your service method
            redirectAttributes.addFlashAttribute("success", "Task added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add task.");
            e.printStackTrace(); // Optional: log for debugging
        }
        return "redirect:/dashboard";
    }
}
