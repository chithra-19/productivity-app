package com.climbup.service.user;

import com.climbup.dto.request.UserRequestDTO;
import com.climbup.dto.response.UserResponseDTO;
import com.climbup.exception.ResourceNotFoundException;
import com.climbup.mapper.UserMapper;
import com.climbup.model.User;
import com.climbup.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------- Save User ----------
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Ensure password is hashed
        return userRepository.save(user);
    }

    // ---------- Get All Users ----------
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ---------- Email Check ----------
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // ---------- Username Check ----------
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    // ---------- Find User by Email ----------
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    // ---------- Find User by ID ----------
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    // ---------- Find by Username ----------
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    // ---------- Current Authenticated User ----------
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();
        final String userEmail;

        if (principal instanceof UserDetails) {
            userEmail = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            userEmail = (String) principal;
        } else {
            throw new ResourceNotFoundException("Unable to get current user");
        }

        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));
    }

    // ---------- Register User (Using DTO) ----------
    public User registerUser(UserRequestDTO dto) {
        if (emailExists(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (usernameExists(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userRepository.save(user);
    }

    // ---------- Update User ----------
    public UserResponseDTO updateUser(Long id, UserRequestDTO dto, String username) {
        User user = getUserById(id);

        if (!user.getUsername().equals(username)) {
            throw new SecurityException("You are not allowed to update this user");
        }

        if (dto.getUsername() != null && !dto.getUsername().equals(user.getUsername())) {
            if (usernameExists(dto.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
            user.setUsername(dto.getUsername());
        }

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (emailExists(dto.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        User updated = userRepository.save(user);
        return UserMapper.toResponseDTO(updated);
    }

    // ---------- Delete User ----------
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    // ---------- UserDetailsService Implementation ----------
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    // ---------- Optional Authentication Helper ----------
    public boolean authenticate(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
