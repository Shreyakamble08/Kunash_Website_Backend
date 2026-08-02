package com.kunash.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KunashBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(KunashBackendApplication.class, args);
        System.out.println("""
            \n
            ╔═══════════════════════════════════════════════╗
            ║   KUNASH BACKEND STARTED SUCCESSFULLY         ║
            ║                                               ║
            ║   Admin Email: admin@kunash.com               ║
            ║   Admin Password: Admin@123!                  ║
            ║                                               ║
            ║   API Base: http://localhost:8080/api         ║
            ║   Login: /auth/login                          ║
            ╚═══════════════════════════════════════════════╝
            """);
    }
}