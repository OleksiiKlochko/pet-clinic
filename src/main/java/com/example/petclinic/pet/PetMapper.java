package com.example.petclinic.pet;

import org.jspecify.annotations.NonNull;
import org.openapitools.model.PetCreateDto;
import org.openapitools.model.PetDto;
import org.openapitools.model.PetPageDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Maps between pet API DTOs and persistence entities.
 */
@Component
public class PetMapper {

    /**
     * Maps a persisted pet entity to the API DTO.
     *
     * @param petEntity persisted pet entity
     * @return mapped API DTO
     */
    public @NonNull PetDto map(@NonNull PetEntity petEntity) {
        return PetDto.builder()
                .id(petEntity.getId())
                .name(petEntity.getName())
                .createdAt(OffsetDateTime.ofInstant(petEntity.getCreatedAt(), ZoneOffset.UTC))
                .lastModifiedAt(OffsetDateTime.ofInstant(petEntity.getLastModifiedAt(), ZoneOffset.UTC))
                .build();
    }

    /**
     * Maps a create request to a persistence entity.
     *
     * @param petCreateDto create request DTO
     * @return persisted pet entity
     */
    public @NonNull PetEntity map(@NonNull PetCreateDto petCreateDto) {
        return PetEntity.builder()
                .name(petCreateDto.getName())
                .build();
    }

    /**
     * Maps a page of persisted pet entities to the API page DTO.
     *
     * @param page page of persisted pets
     * @return mapped API page DTO
     */
    public @NonNull PetPageDto mapPage(@NonNull Page<PetEntity> page) {
        return new PetPageDto()
                .items(page.stream().map(this::map).toList())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages());
    }
}
