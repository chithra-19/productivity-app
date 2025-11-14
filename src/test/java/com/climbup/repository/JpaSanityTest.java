package com.climbup.repository;

import static org.assertj.core.api.Assertions.assertThat;



import com.climbup.model.User;
import com.climbup.repository.UserRepository;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ✅ Sanity check to verify JPA infrastructure is wired correctly.
 * - Confirms EntityManager is available.
 * - Verifies basic repository functionality.
 */
@DataJpaTest
class JpaSanityTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void entityManagerShouldBeAvailable() {
        assertThat(entityManager).isNotNull();
    }

    @Test
    void userRepositoryShouldSaveAndRetrieveUser() {
        // Arrange
        User user = new User("testuser", "test@example.com", "password123");

        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
    }
}