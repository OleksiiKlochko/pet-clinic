package com.example.petclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.modulith.Modulithic;

/**
 * Application entry point.
 */
@EnableJpaAuditing
@Modulithic
@SpringBootApplication
public class PetClinicApplication {

    static void main(String[] args) {
        SpringApplication.run(PetClinicApplication.class, args);
    }
}
