package com.example.petclinic.vet;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.openapitools.api.VetApi;
import org.openapitools.model.VetCreateDto;
import org.openapitools.model.VetDto;
import org.openapitools.model.VetPageDto;
import org.openapitools.model.VetPatchDto;
import org.openapitools.model.VetUpdateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

/**
 * REST controller implementing vet API operations.
 */
@RequiredArgsConstructor
@RestController
class VetApiController implements VetApi {
    private final VetService vetService;

    @Override
    public ResponseEntity<VetPageDto> getVets(
            @NonNull Integer pageNumber,
            @NonNull Integer pageSize,
            @Nullable Set<UUID> ids,
            @Nullable Set<String> firstNames,
            @Nullable Set<String> lastNames
    ) {
        VetPageDto page = vetService.findVets(ids, firstNames, lastNames, pageNumber, pageSize);
        return ResponseEntity.ok(page);
    }

    @Override
    public ResponseEntity<VetDto> createVet(@NonNull VetCreateDto vetCreateDto) {
        VetDto vetDto = vetService.createVet(vetCreateDto);
        return ResponseEntity.status(201).body(vetDto);
    }

    @Override
    public ResponseEntity<Void> deleteVet(@NonNull UUID id) {
        vetService.deleteVet(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<VetDto> updateVet(@NonNull UUID id, @NonNull VetUpdateDto vetUpdateDto) {
        VetDto vetDto = vetService.updateVet(id, vetUpdateDto);
        return ResponseEntity.ok(vetDto);
    }

    @Override
    public ResponseEntity<VetDto> patchVet(@NonNull UUID id, @NonNull VetPatchDto vetPatchDto) {
        VetDto vetDto = vetService.patchVet(id, vetPatchDto);
        return ResponseEntity.ok(vetDto);
    }
}
