package com.climbup.productivity_app;

import com.climbup.controller.task.TaskController;
import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.model.User;
import com.climbup.service.task.TaskService;
import com.climbup.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
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

    @Test
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
                        .principal(mockPrincipal())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void getTasks_ShouldReturn200() throws Exception {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(1L);
        dto.setTitle("Sample Task");
        dto.setCompleted(false);

        User mockUser = new User();
        mockUser.setUsername("testuser");

        when(userService.findByUsername("testuser")).thenReturn(mockUser);
        when(taskService.getTasksForUser(any(User.class))).thenReturn(List.of(dto));

        mockMvc.perform(get("/tasks")
                        .principal(mockPrincipal()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Sample Task"));
    }
}