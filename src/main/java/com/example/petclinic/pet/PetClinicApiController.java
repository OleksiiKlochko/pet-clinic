package com.example.petclinic.pet;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.openapitools.api.PetclinicApi;
import org.openapitools.model.Pet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class PetClinicApiController implements PetclinicApi {
    private final PetService petService;

    @Override
    public ResponseEntity<List<Pet>> getPets(@Nullable Set<UUID> ids, @Nullable Set<String> names) {
        List<Pet> body = petService.findPets(ids, names);
        return ResponseEntity.ok(body);
    }
}
