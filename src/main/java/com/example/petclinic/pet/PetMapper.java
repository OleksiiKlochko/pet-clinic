package com.example.petclinic.pet;

import org.openapitools.model.Pet;
import org.springframework.stereotype.Component;

@Component
public class PetMapper {

    public Pet toPet(PetEntity petEntity) {
        return Pet.builder()
                .id(petEntity.getId())
                .name(petEntity.getName())
                .build();
    }
}
