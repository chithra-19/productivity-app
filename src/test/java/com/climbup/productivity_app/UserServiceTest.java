package com.climbup.productivity_app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.climbup.dto.request.UserRequestDTO;
import com.climbup.dto.response.UserResponseDTO;
import com.climbup.model.User;
import com.climbup.repository.UserRepository;
import com.climbup.service.user.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    
    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123"); // ✅ set password

        // Mock passwordEncoder to return the same password (simple for test)
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void getUserById_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User user = userService.getUserById(1L);

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_NotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
    }

    @Test
    void saveUser_ShouldSaveAndReturnUser() {
        // When encode is called, return predictable hash (raw password here for simplicity)
        when(passwordEncoder.encode("password123")).thenReturn("password123");

        // When repository saves, return the same user
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.save(testUser);

        assertNotNull(created);
        assertEquals("testuser", created.getUsername());
        assertEquals("password123", created.getPassword()); // now it matches
        verify(userRepository, times(1)).save(testUser);
        verify(passwordEncoder, times(1)).encode("password123"); // ensure encoder was used
    }


    @Test
    void updateUser_ShouldModifyUser() {
        Long userId = 1L;
        String loggedInUsername = "testuser";

        UserRequestDTO updateInfo = new UserRequestDTO();
        updateInfo.setUsername("updatedUser");
        updateInfo.setEmail("updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO updated = userService.updateUser(userId, updateInfo, loggedInUsername);

        assertNotNull(updated);
        assertEquals("updatedUser", updated.getUsername());
        assertEquals("updated@example.com", updated.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldCallRepositoryDelete() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void deleteUser_NotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.deleteUser(1L));
    }
}
