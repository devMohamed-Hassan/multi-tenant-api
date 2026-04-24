package com.saasauth.multitenant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MultiTenantApiApplication implements CommandLineRunner {

    @Value("${app.message}")
    private String message;

    public static void main(String[] args) {
        SpringApplication.run(MultiTenantApiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("ENV Variable: " + message);
    }
}