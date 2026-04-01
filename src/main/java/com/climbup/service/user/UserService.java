package com.climbup.service.user;

import com.climbup.dto.request.UserRequestDTO;
import com.climbup.dto.response.UserResponseDTO;
import com.climbup.exception.ResourceNotFoundException;
import com.climbup.mapper.UserMapper;
import com.climbup.model.User;
import com.climbup.repository.UserRepository;
import com.climbup.service.productivity.AchievementService;

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
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AchievementService achievementService;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, AchievementService achievementService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.achievementService = achievementService;
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


    // ---------- Current Authenticated User ----------
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("No authenticated user found");
        }

        String login = authentication.getName(); // this is email now
        return userRepository.findByEmail(login)

                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    

    // ---------------- Registration ----------------
    public User registerUser(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User saved = userRepository.save(user);

        // 🔑 Initialize baseline achievements
        achievementService.initializeAchievements(saved);
       

        return saved;
    }


    // ---------- Update User ----------
    public UserResponseDTO updateUser(Long id, UserRequestDTO dto, String username) {
        User user = getUserById(id);
        if (!user.getEmail().equals(username)) {
            throw new SecurityException("You are not allowed to update this user");
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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),   // or user.getUsername(), depending on what you store
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
    
  
    public User getUserWithGoals(String email) {
    	User user = userRepository.findByEmail(email)
    		    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        user.getGoals().size(); // triggers lazy loading
        return user;
    }
    
    
    public User getUserWithTasks(String username) {
        return userRepository.findUserWithTasks(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    
    public User getUserWithAllData(String login) {
        return userRepository.findUserWithAllData(login)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + login));
    }

    
    public User findByUsernameOrEmail(String login) {
        return userRepository.findByEmail(login)
                
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username or email: " + login));
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }
    
    public int calculateLevel(int xp) {
        return (xp / 100) + 1;
    }
    public int getUserLevel(User user) {
        return calculateLevel(user.getProductivityScore());
    }


    
    
}
