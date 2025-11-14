package com.climbup;

import static org.assertj.core.api.Assertions.assertThat;



import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

/**
 * Basic smoke test to verify that the Spring Boot application context loads
 * and critical beans like EntityManager are present.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
																																																																																																																																																																																																																																																																																																																																																																																																																						
class ClimbupApplicationTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private EntityManager entityManager;

    @Test
    void contextLoads() {
        // Verifies that the application context is initialized
        assertThat(context).isNotNull();
    }

    @Transactional
    @Test
    void entityManagerIsAvailable() {
        // Verifies that the EntityManager bean is present and wired
        assertThat(entityManager).isNotNull();
    }

    @Test
    void climbupApplicationBeanExists() {
        // Optional: Check if the main application class is registered as a bean
        assertThat(context.getBean(ClimbupApplication.class)).isNotNull();
    }
}