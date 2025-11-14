package com.climbup;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.climbup.model")
@EnableJpaRepositories(basePackages = "com.climbup.repository")
public class ClimbupApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClimbupApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner inspectBeans(ApplicationContext ctx) {
//        return args -> {
//            Arrays.stream(ctx.getBeanDefinitionNames())
//                  .filter(name -> name.contains("entityManager"))
//                  .forEach(System.out::println);
//        };
//    }
}