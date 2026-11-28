package com.climbup.controller.task;

import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.model.User;
import com.climbup.model.Task;
import com.climbup.repository.TaskRepository;
import com.climbup.service.productivity.StreakTrackerService;
import com.climbup.service.task.TaskCommandService;
import com.climbup.service.task.TaskQueryService;
import com.climbup.service.task.TaskStatsService;
import com.climbup.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TaskController.class)
@ContextConfiguration(classes = { TaskController.class }) // isolate only controller
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Everything TaskController needs, mocked
    @MockBean private TaskQueryService taskQueryService;
    @MockBean private TaskCommandService taskCommandService;
    @MockBean private UserService userService;
    @MockBean private TaskRepository taskRepository;
    @MockBean private StreakTrackerService streakTrackerService;
    @MockBean private TaskStatsService taskStatsService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("POST /api/tasks should return 201 Created with task details")
    @Test
    @WithMockUser(username = "testuser")
    void createTask_ShouldReturn201() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("New Task");
        request.setPriority(Task.Priority.HIGH);

        TaskResponseDTO response = new TaskResponseDTO();
        response.setId(1L);
        response.setTitle("New Task");
        response.setCompleted(false);

        when(userService.findByEmail("testuser")).thenReturn(new User());
        when(taskCommandService.createTask(any(TaskRequestDTO.class), any(User.class))).thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getTasks_ShouldReturn200() throws Exception {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(1L);
        dto.setTitle("Sample Task");
        dto.setCompleted(false);

        when(userService.findByEmail("testuser")).thenReturn(new User());
        when(taskQueryService.getTasksForUser(any(User.class))).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/tasks"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].title").value("Sample Task"));
    }

    @DisplayName("POST /api/tasks with empty title should return 400")
    @Test
    @WithMockUser(username = "testuser")
    void createTask_EmptyTitle_ShouldReturn400() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle(""); // Empty title
        request.setPriority(Task.Priority.HIGH);

        when(userService.findByEmail("testuser")).thenReturn(new User());

        mockMvc.perform(post("/api/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest());
    }

    @DisplayName("GET /api/tasks should return empty list if no tasks")
    @Test
    @WithMockUser(username = "testuser")
    void getTasks_Empty_ShouldReturnEmptyArray() throws Exception {
        when(userService.findByEmail("testuser")).thenReturn(new User());
        when(taskQueryService.getTasksForUser(any(User.class))).thenReturn(List.of());

        mockMvc.perform(get("/api/tasks"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isEmpty());
    }

    @DisplayName("POST /api/tasks without login should be rejected")
    @Test
    void createTask_WithoutLogin_ShouldBeRejected() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("New Task");

        mockMvc.perform(post("/api/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
               .andExpect(result -> {
                   int s = result.getResponse().getStatus();
                   // 401 (API style) or 302 (redirect to the login page)
                   assertTrue(s == 401 || s == 302, "Expected the request to be rejected, but status was " + s);
               });
    }

    @DisplayName("DELETE /api/tasks/delete/{id} returns the updated counters")
    @Test
    @WithMockUser(username = "testuser")
    void deleteTask_ShouldReturnCounters() throws Exception {
        when(userService.findByEmail("testuser")).thenReturn(new User());

        mockMvc.perform(delete("/api/tasks/delete/1").with(csrf()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.pendingTasks").value(0))
               .andExpect(jsonPath("$.streak").value(0));
    }

    @DisplayName("PUT /api/tasks/mark-done/{id} returns the task and the counters")
    @Test
    @WithMockUser(username = "testuser")
    void markDone_ShouldReturnTaskAndCounters() throws Exception {
        TaskResponseDTO done = new TaskResponseDTO();
        done.setId(1L);
        done.setTitle("Finish report");
        done.setCompleted(true);

        when(userService.findByEmail("testuser")).thenReturn(new User());
        when(taskCommandService.markDone(eq(1L), any(User.class))).thenReturn(done);

        mockMvc.perform(put("/api/tasks/mark-done/1").with(csrf()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.task.title").value("Finish report"))
               .andExpect(jsonPath("$.pendingTasks").value(0));
    }
}