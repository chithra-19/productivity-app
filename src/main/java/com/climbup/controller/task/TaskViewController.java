package com.climbup.controller.task;

import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.request.TaskUpdateDTO;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.model.User;
import com.climbup.service.task.TaskCommandService;
import com.climbup.service.task.TaskQueryService;
import com.climbup.service.task.TaskStatsService;
import com.climbup.service.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/tasks") // ✅ FIXED
public class TaskViewController {

    private final TaskCommandService taskCommandService;
    private final UserService userService;
    private final TaskQueryService taskQueryService;
    private final TaskStatsService taskStatsService;

    @Autowired
    public TaskViewController(TaskCommandService taskCommandService,
    							UserService userService,
    							TaskQueryService taskQueryService,
    							TaskStatsService taskStatsService) {
        this.taskCommandService = taskCommandService;
        this.userService = userService;
        this.taskQueryService = taskQueryService;
        this.taskStatsService = taskStatsService;
    }

    private User getUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername());
    }

    // =========================
    // SHOW ALL TASKS
    // =========================
    @GetMapping("/all")
    public String showAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = getUser(userDetails);

        Page<TaskResponseDTO> taskPage =
                taskQueryService.getTasksForUserPaginated(user, page, size);

        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", taskPage.getTotalPages());

        return "tasks/task-all";
    }

    // =========================
    // TODAY TASKS
    // =========================
    @GetMapping("/today")
    public String showTodayTasks(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = getUser(userDetails);

        List<TaskResponseDTO> todayTasks =
                taskQueryService.getTasksForUserByDate(user, LocalDate.now());

        model.addAttribute("tasks", todayTasks);

        return "tasks/task-today";
    }

    // =========================
    // ADD TASK PAGE
    // =========================
    @GetMapping("/add")
    public String showAddTaskForm(Model model) {
        model.addAttribute("task", new TaskRequestDTO());
        return "tasks/add-task";
    }

    // =========================
    // SAVE TASK
    // =========================
    @PostMapping("/save")
    public String saveTask(
            @ModelAttribute("task") TaskRequestDTO taskDTO,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User user = getUser(userDetails);
            taskCommandService.createTask(taskDTO, user);
            redirectAttributes.addFlashAttribute("success", "Task added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add task.");
        }

        return "redirect:/tasks/today";
    }

    // =========================
    // EDIT TASK PAGE
    // =========================
    @GetMapping("/edit/{id}")
    public String showEditTaskForm(
            @PathVariable Long id,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = getUser(userDetails);

        TaskResponseDTO task = taskQueryService.getTaskById(id, user);
        model.addAttribute("task", task);

        return "tasks/edit-task";
    }

    // =========================
    // UPDATE TASK
    // =========================
    @PostMapping("/update")
    public String updateTask(
            @ModelAttribute("task") TaskUpdateDTO taskDTO,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        User user = getUser(userDetails);

        taskCommandService.updateTask(taskDTO.getId(), taskDTO, user);
        redirectAttributes.addFlashAttribute("success", "Task updated successfully!");

        return "redirect:/tasks/all";
    }

    // =========================
    // DELETE TASK
    // =========================
    @GetMapping("/delete/{id}")
    public String deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        User user = getUser(userDetails);

        taskCommandService.deleteTask(id, user);
        redirectAttributes.addFlashAttribute("success", "Task deleted successfully!");

        return "redirect:/tasks/all";
    }
}