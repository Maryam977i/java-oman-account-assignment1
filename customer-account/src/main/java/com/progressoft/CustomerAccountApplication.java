package com.progressoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CustomerAccountApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerAccountApplication.class, args);
        System.out.println("🚀 Customer Account Service Started Successfully!");
        System.out.println("📊 Database: PostgreSQL running on port 5432");
        System.out.println("🌐 API available at: http://localhost:8080");
    }
}
