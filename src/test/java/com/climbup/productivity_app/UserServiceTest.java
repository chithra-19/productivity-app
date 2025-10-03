package com.climbup.productivity_app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.climbup.dto.request.UserRequestDTO;
import com.climbup.dto.response.UserResponseDTO;
import com.climbup.model.User;
import com.climbup.repository.UserRepository;
import com.climbup.service.user.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
        testUser.setPassword("password123");

        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("Should return user when ID exists")
    void getUserById_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User user = userService.getUserById(1L);

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user ID not found")
    void getUserById_NotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
    }

    @Test
    @DisplayName("Should save user with encoded password")
    void saveUser_ShouldEncodePasswordAndSave() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.save(testUser);

        assertNotNull(saved);
        assertEquals("testuser", saved.getUsername());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should update username and email")
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
    @DisplayName("Should encode password during update")
    void updateUser_ShouldEncodePasswordIfPresent() {
        Long userId = 1L;
        String loggedInUsername = "testuser";

        UserRequestDTO updateInfo = new UserRequestDTO();
        updateInfo.setPassword("newpass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.encode("newpass")).thenReturn("encodedpass");

        UserResponseDTO updated = userService.updateUser(userId, updateInfo, loggedInUsername);

        assertNotNull(updated);
        verify(passwordEncoder).encode("newpass");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user when ID exists")
    void deleteUser_ShouldCallRepositoryDelete() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.deleteUser(1L);

        verify(userRepository).delete(testUser);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void deleteUser_NotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.deleteUser(1L));
    }
    
    @Test
    @DisplayName("Should throw exception if updated username already exists")
    void updateUser_UsernameExists_ShouldThrowException() {
        Long userId = 1L;
        String loggedInUsername = "testuser";

        UserRequestDTO updateInfo = new UserRequestDTO();
        updateInfo.setUsername("existingUser");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, updateInfo, loggedInUsername));
    }
    
    @Test
    @DisplayName("Should throw exception if user tries to update another user")
    void updateUser_Unauthorized_ShouldThrowSecurityException() {
        Long userId = 1L;
        String loggedInUsername = "otherUser"; // not the owner

        UserRequestDTO updateInfo = new UserRequestDTO();
        updateInfo.setUsername("newUser");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        assertThrows(SecurityException.class, () -> userService.updateUser(userId, updateInfo, loggedInUsername));
    }
}