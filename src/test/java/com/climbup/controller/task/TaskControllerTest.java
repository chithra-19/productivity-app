package com.climbup.controller.task;

import com.climbup.controller.task.TaskController;
import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.model.User;
import com.climbup.security.TestSecurityConfig;
import com.climbup.service.task.TaskService;
import com.climbup.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TaskController.class)
@ContextConfiguration(classes = { TaskController.class }) // isolate only controller
@Import(TestSecurityConfig.class) 
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    
    private Principal mockPrincipal() {
        return () -> "testuser";
    }

    @DisplayName("POST /tasks should return 201 Created with task details")
    @Test
    @WithMockUser(username = "testuser")
    void createTask_ShouldReturn201() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("New Task");

        TaskResponseDTO response = new TaskResponseDTO();
        response.setId(1L);
        response.setTitle("New Task");
        response.setCompleted(false);

        User mockUser = new User();
        mockUser.setUsername("testuser");

        when(userService.findByUsername("testuser")).thenReturn(mockUser);
        when(taskService.createTask(any(TaskRequestDTO.class), any(User.class))).thenReturn(response);

        mockMvc.perform(post("/tasks")
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

        User mockUser = new User();
        mockUser.setUsername("testuser");

        when(userService.findByUsername("testuser")).thenReturn(mockUser);
        when(taskService.getTasksForUser(any(User.class))).thenReturn(List.of(dto));

        mockMvc.perform(get("/tasks"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].title").value("Sample Task"));
    }


    @DisplayName("POST /tasks with empty title should return 400")
    @Test
    @WithMockUser(username = "testuser")
    void createTask_EmptyTitle_ShouldReturn400() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle(""); // Empty title

        User mockUser = new User();
        mockUser.setUsername("testuser");

        when(userService.findByUsername("testuser")).thenReturn(mockUser);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
               .andExpect(status().isBadRequest());
    }


    @DisplayName("GET /tasks should return empty list if no tasks")
    @Test
    @WithMockUser(username = "testuser")
    void getTasks_Empty_ShouldReturnEmptyArray() throws Exception {
        User mockUser = new User();
        mockUser.setUsername("testuser");

        when(userService.findByUsername("testuser")).thenReturn(mockUser);
        when(taskService.getTasksForUser(any(User.class))).thenReturn(List.of()); // empty list

        mockMvc.perform(get("/tasks"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isEmpty());
    }


    @DisplayName("POST /tasks without principal should return 401")
    @Test
    void createTask_WithoutPrincipal_ShouldReturn401() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("New Task");

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void deleteTask_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/tasks/1"))
               .andExpect(status().isNoContent()); // 204
    }
}
