package com.example.petclinic.pet;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.openapitools.api.PetApi;
import org.openapitools.model.PetCreateDto;
import org.openapitools.model.PetDto;
import org.openapitools.model.PetPageDto;
import org.openapitools.model.PetPatchDto;
import org.openapitools.model.PetUpdateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.example.petclinic.pet.internal.PetService;

import java.util.Set;
import java.util.UUID;

/**
 * REST controller implementing pet API operations.
 */
@RequiredArgsConstructor
@RestController
class PetApiController implements PetApi {
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

    @Override
    public ResponseEntity<PetDto> createPet(@NonNull PetCreateDto petCreateDto) {
        PetDto petDto = petService.createPet(petCreateDto);
        return ResponseEntity.status(201).body(petDto);
    }

    @Override
    public ResponseEntity<Void> deletePet(@NonNull UUID id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PetDto> updatePet(@NonNull UUID id, @NonNull PetUpdateDto petUpdateDto) {
        PetDto petDto = petService.updatePet(id, petUpdateDto);
        return ResponseEntity.ok(petDto);
    }

    @Override
    public ResponseEntity<PetDto> patchPet(@NonNull UUID id, @NonNull PetPatchDto petPatchDto) {
        PetDto petDto = petService.patchPet(id, petPatchDto);
        return ResponseEntity.ok(petDto);
    }
}
