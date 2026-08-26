package com.example.bys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.example.bys",
        "controllers",
        "services",
        "repositories",
        "entities",
        "exceptions",
        "security",
        "dto",
        "utils",
        "config"
})
@EntityScan("entities")
@EnableJpaRepositories("repositories")
public class BysApplication {
    public static void main(String[] args) {
        SpringApplication.run(BysApplication.class, args);
    }
}
