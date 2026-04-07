package com.climbup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan(basePackages = "com.climbup.model")
@EnableJpaRepositories(basePackages = "com.climbup.repository")
@EnableScheduling // 🔥 REQUIRED for @Scheduled to work
public class ClimbupApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClimbupApplication.class, args);
    }
}