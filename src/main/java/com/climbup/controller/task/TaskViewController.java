package com.climbup.controller.task;

import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.request.TaskUpdateDTO;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.mapper.TaskMapper;
import com.climbup.model.Task;
import com.climbup.model.User;
import com.climbup.repository.TaskRepository;
import com.climbup.service.task.TaskService;
import com.climbup.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tasks")
public class TaskViewController {

    private final TaskService taskService;
    private final UserService userService;
    private final TaskRepository taskRepository;

    @Autowired
    public TaskViewController(TaskService taskService, UserService userService,
    							TaskRepository taskRepository) {
        this.taskService = taskService;
        this.userService = userService;
        this.taskRepository = taskRepository;
    }

    // =========================
    // SHOW ALL TASKS (PAGINATED)
    // =========================
    @GetMapping("/all")
    public String showAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.findByEmail(userDetails.getUsername());

        Page<TaskResponseDTO> taskPage =
                taskService.getTasksForUserPaginated(user, page, size);

        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", taskPage.getTotalPages());

        return "tasks/task-all";
    }

    // =========================
    // SHOW TODAY'S TASKS
    // =========================
    @GetMapping("/today")
    public String showTodayTasks(Model model, Principal principal) {

        User user = userService.findByEmail(principal.getName());
        LocalDate today = LocalDate.now();

        List<TaskResponseDTO> todayTasks =
                taskService.getTasksForUserByDate(user, today);

        model.addAttribute("tasks", todayTasks);
        return "tasks/task-today";
    }

    // =========================
    // SHOW ADD TASK FORM
    // =========================
    @GetMapping("/add")
    public String showAddTaskForm(Model model) {
        model.addAttribute("task", new TaskRequestDTO());
        return "tasks/add-task";
    }

    // =========================
    // SAVE TASK (FROM FORM)
    // =========================
    @PostMapping("/save")
    public String saveTask(
            @ModelAttribute("task") TaskRequestDTO taskDTO,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User user = userService.findByEmail(principal.getName());
            taskService.createTask(taskDTO, user);
            redirectAttributes.addFlashAttribute("success", "Task added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add task.");
            e.printStackTrace();
        }
        return "redirect:/tasks/today";
    }
    @GetMapping("/edit/{id}")
    public String showEditTaskForm(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        TaskResponseDTO task = taskService.getTaskById(id, user);
        model.addAttribute("task", task);
        return "tasks/edit-task";
    }

    @PostMapping("/update")
    public String updateTask(@ModelAttribute("task") TaskUpdateDTO taskDTO, Principal principal,
                             RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(principal.getName());
        taskService.updateTask(taskDTO.getId(), taskDTO, user);
        redirectAttributes.addFlashAttribute("success", "Task updated successfully!");
        return "redirect:/tasks/all";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, Principal principal,
                             RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(principal.getName());
        taskService.deleteTask(id, user);
        redirectAttributes.addFlashAttribute("success", "Task deleted successfully!");
        return "redirect:/tasks/all";
    }
    @PostMapping("/update/json")
    @ResponseBody
    public ResponseEntity<?> updateTaskJson(@RequestBody TaskUpdateDTO taskDTO, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        taskService.updateTask(taskDTO.getId(), taskDTO, user);
        return ResponseEntity.ok("Task updated");
    }
    @GetMapping("/dashboard/top5")
    @ResponseBody
    public List<TaskResponseDTO> top5(Principal principal) {

        User user = userService.findByEmail(principal.getName());
        LocalDate today = LocalDate.now();
        return taskRepository.findByUserAndTaskDate(user, today)
                .stream()
                .sorted(Comparator.comparing(Task::getPriority))
                .limit(5)
                .map(task -> TaskMapper.toResponse(task, 
                		taskService.getPriorityPoints(task.getPriority())))
                .toList();
    }
}

