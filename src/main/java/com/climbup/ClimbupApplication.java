package com.climbup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // REQUIRED for @Scheduled to work
public class ClimbupApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClimbupApplication.class, args);
    }
}