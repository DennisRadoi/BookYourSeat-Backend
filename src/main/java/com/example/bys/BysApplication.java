package com.example.bys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan("entities")
public class BysApplication {

    public static void main(String[] args) {
        SpringApplication.run(BysApplication.class, args);
    }

}
