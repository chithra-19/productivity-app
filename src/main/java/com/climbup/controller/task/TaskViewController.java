package com.climbup.controller.task;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tasks")
public class TaskViewController {

    // ✅ Loads the page where all tasks will be displayed
    @GetMapping("/all")
    public String showAllTasksPage() {
        // This returns the Thymeleaf template: task-all.html
        return "task-all";
    }

    // (Optional) You can add more views like:
    // @GetMapping("/create")
    // public String showCreateTaskPage() {
    //     return "task-create"; 
    // }
}
