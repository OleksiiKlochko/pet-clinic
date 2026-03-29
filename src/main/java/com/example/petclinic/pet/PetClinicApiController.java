package com.example.petclinic.pet;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.openapitools.api.PetclinicApi;
import org.openapitools.model.PetPageDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
class PetClinicApiController implements PetclinicApi {
    private final PetService petService;

    @Override
    public ResponseEntity<PetPageDto> getPets(
            @NonNull Integer pageNumber,
            @NonNull Integer pageSize,
            @Nullable Set<UUID> ids,
            @Nullable Set<String> names
    ) {
            PetPageDto page = petService.findPets(ids, names, pageNumber, pageSize);
            return ResponseEntity.ok(page);
    }
}
