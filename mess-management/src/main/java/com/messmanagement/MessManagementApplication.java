package com.messmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Mess Management Application.
 *
 * Run with: mvn spring-boot:run
 * Then open: http://localhost:8080
 */
@SpringBootApplication
public class MessManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessManagementApplication.class, args);
    }
}
