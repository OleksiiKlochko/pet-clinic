package com.example.petclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Application entry point.
 */
@EnableJpaAuditing
@SpringBootApplication
public class PetClinicApplication {

    static void main(String[] args) {
        SpringApplication.run(PetClinicApplication.class, args);
    }
}
