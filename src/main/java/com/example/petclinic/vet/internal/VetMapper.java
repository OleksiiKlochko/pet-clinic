package com.example.petclinic.vet.internal;

import org.jspecify.annotations.NonNull;
import org.openapitools.model.VetCreateDto;
import org.openapitools.model.VetDto;
import org.openapitools.model.VetPageDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Maps between vet API DTOs and persistence entities.
 */
@Component
public class VetMapper {

    /**
     * Maps a persisted vet entity to the API DTO.
     *
     * @param vetEntity persisted vet entity
     * @return mapped API DTO
     */
    public @NonNull VetDto map(@NonNull VetEntity vetEntity) {
        return VetDto.builder()
                .id(vetEntity.getId())
                .firstName(vetEntity.getFirstName())
                .lastName(vetEntity.getLastName())
                .createdAt(OffsetDateTime.ofInstant(vetEntity.getCreatedAt(), ZoneOffset.UTC))
                .lastModifiedAt(OffsetDateTime.ofInstant(vetEntity.getLastModifiedAt(), ZoneOffset.UTC))
                .build();
    }

    /**
     * Maps a create request to a persistence entity.
     *
     * @param vetCreateDto create request DTO
     * @return persisted vet entity
     */
    public @NonNull VetEntity map(@NonNull VetCreateDto vetCreateDto) {
        return VetEntity.builder()
                .firstName(vetCreateDto.getFirstName())
                .lastName(vetCreateDto.getLastName())
                .build();
    }

    /**
     * Maps a page of persisted vet entities to the API page DTO.
     *
     * @param page page of persisted vets
     * @return mapped API page DTO
     */
    public @NonNull VetPageDto mapPage(@NonNull Page<VetEntity> page) {
        return new VetPageDto()
                .items(page.stream().map(this::map).toList())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages());
    }
}
