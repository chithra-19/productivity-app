package com.climbup.productivity_app;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.climbup.repository")
@EntityScan(basePackages = "com.climbup.model")
public class ClimbupApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClimbupApplication.class, args);
    }
}
