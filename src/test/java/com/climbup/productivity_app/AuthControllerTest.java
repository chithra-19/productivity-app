package com.climbup.productivity_app;

import com.climbup.controller.user.AuthController;
import com.climbup.dto.request.UserRequestDTO;
import com.climbup.model.User;
import com.climbup.repository.UserRepository;
import com.climbup.security.SecurityConfig;
import com.climbup.service.user.CustomUserDetailsService;
import com.climbup.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

   
    
    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;
   

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

   

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
    void testProcessLogin() throws Exception {
        when(userService.authenticate(any(String.class), any(String.class))).thenReturn(true);

        mockMvc.perform(post("/auth/login")
                        .param("username", "testuser")
                        .param("password", "testpass")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
               .andExpect(status().is3xxRedirection());
    }

    @Test
    void testProcessRegister() throws Exception {
        User mockUser = new User();
        when(userService.registerUser(any(UserRequestDTO.class))).thenReturn(mockUser);

        mockMvc.perform(post("/auth/register")
                        .param("username", "newuser")
                        .param("password", "newpass")
                        .param("email", "newuser@test.com")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
               .andExpect(status().is3xxRedirection());
    }

    @Test
    void testProcessLoginFailure() throws Exception {
        when(userService.authenticate(any(), any())).thenReturn(false);

        mockMvc.perform(post("/auth/login")
                        .param("username", "wronguser")
                        .param("password", "wrongpass")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
               .andExpect(status().is3xxRedirection()); // or .isOk() if you show error on same page
    }
}