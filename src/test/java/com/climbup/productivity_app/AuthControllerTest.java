package com.climbup.productivity_app;

import com.climbup.controller.user.AuthController;
import com.climbup.dto.request.UserRequestDTO;
import com.climbup.model.User;
import com.climbup.repository.UserRepository;
import com.climbup.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AuthController.class) // Only loads controller layer
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService; // Mock the service layer to avoid JPA

    @MockBean
    private UserRepository userRepository; // Needed because AuthController constructor uses it

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;
    // -------- GET Endpoints --------
    @Test
    void testShowLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
               .andExpect(status().isOk())
               .andExpect(view().name("login"));
    }

    @Test
    void testShowRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
               .andExpect(status().isOk())
               .andExpect(view().name("register"));
    }
    
    

    // -------- POST Endpoints --------
    @Test
    void testProcessLogin() throws Exception {
        // Mock your authentication logic if needed
        when(userService.authenticate(any(String.class), any(String.class))).thenReturn(true);

        mockMvc.perform(post("/login")
                        .param("username", "testuser")
                        .param("password", "testpass")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
               .andExpect(status().is3xxRedirection()); // Redirect after login
    }

    @Test
    void testProcessRegister() throws Exception {
        // Mock registration logic with DTO
        doNothing().when(userService).registerUser(any(UserRequestDTO.class));

        mockMvc.perform(post("/register")
                        .param("username", "newuser")
                        .param("password", "newpass")
                        .param("email", "newuser@test.com")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
               .andExpect(status().is3xxRedirection());
    }

}
