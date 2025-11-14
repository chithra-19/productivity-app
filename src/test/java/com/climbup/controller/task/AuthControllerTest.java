package com.climbup.controller.task;


import com.climbup.controller.user.AuthController;
import com.climbup.dto.request.UserRequestDTO;
import com.climbup.model.User;
import com.climbup.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
  
    @Test
    void testShowLoginPage() throws Exception {
        mockMvc.perform(get("/auth/login"))
               .andExpect(status().isOk())
               .andExpect(view().name("login"));
    }

    @Test
    void testShowRegisterPage() throws Exception {
        mockMvc.perform(get("/auth/register"))
               .andExpect(status().isOk())
               .andExpect(view().name("register"));
    }

    @Test
    void testProcessRegisterSuccess() throws Exception {
        when(userService.registerUser(any(UserRequestDTO.class)))
            .thenReturn(new User("newuser", "newuser@test.com", "newpass"));

        mockMvc.perform(post("/auth/register")
                .param("userDTO.username", "newuser")
                .param("userDTO.email", "newuser@test.com")
                .param("userDTO.password", "newpass")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));
        
        verify(userService).registerUser(any(UserRequestDTO.class));
    }

    @Test
    void testProcessRegisterDuplicateEmail() throws Exception {
        when(userService.registerUser(any(UserRequestDTO.class)))
            .thenThrow(new IllegalArgumentException("Email already exists"));

        mockMvc.perform(post("/auth/register")
                .param("userDTO.username", "newuser")
                .param("userDTO.email", "duplicate@test.com")
                .param("userDTO.password", "newpass")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isOk())
            .andExpect(view().name("register"))
            .andExpect(model().attributeExists("error"));
        
        verify(userService).registerUser(any(UserRequestDTO.class));
    }

    @Test
    void testProcessRegisterGenericException() throws Exception {
        when(userService.registerUser(any(UserRequestDTO.class)))
            .thenThrow(new RuntimeException("Unexpected failure"));

        mockMvc.perform(post("/auth/register")
                .param("userDTO.username", "newuser")
                .param("userDTO.email", "newuser@test.com")
                .param("userDTO.password", "newpass")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isOk())
            .andExpect(view().name("error"))
            .andExpect(model().attributeExists("error"));
        
        verify(userService).registerUser(any(UserRequestDTO.class));
    }
    
    @Test
    void testProcessRegister_WithValidationErrors_ShouldReturnToForm() throws Exception {
        mockMvc.perform(post("/auth/register")
                .param("userDTO.username", "") // invalid
                .param("userDTO.email", "")    // invalid
                .param("userDTO.password", "") // invalid
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isOk())
            .andExpect(view().name("register"))
            .andExpect(model().attributeHasFieldErrors("userDTO", "username", "email", "password"));
    }
}