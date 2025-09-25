package com.climbup.productivity_app;

import com.climbup.controller.task.TaskController;
import com.climbup.dto.request.TaskRequestDTO;
import com.climbup.dto.response.TaskResponseDTO;
import com.climbup.service.task.TaskService;
import com.climbup.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService; // Mock the service layer

    @MockBean
    private UserService userService; // Mock the service layer

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

        // Mock the service calls
        when(userService.findByUsername("testuser")).thenReturn(new com.climbup.model.User());
        when(taskService.createTask(any(), any())).thenReturn(response);

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

        when(userService.findByUsername("testuser")).thenReturn(new com.climbup.model.User());
        when(taskService.getTasksForUser(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/tasks")
                        .principal(mockPrincipal()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Sample Task"));
    }
}
