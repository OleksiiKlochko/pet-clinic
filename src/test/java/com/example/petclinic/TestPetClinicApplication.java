package com.example.petclinic;

import org.springframework.boot.SpringApplication;

public class TestPetClinicApplication {

    static void main(String[] args) {
        SpringApplication.from(PetClinicApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
