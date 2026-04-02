package com.example.petclinic.vet;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.openapitools.model.VetCreateDto;
import org.openapitools.model.VetDto;
import org.openapitools.model.VetPageDto;
import org.openapitools.model.VetPatchDto;
import org.openapitools.model.VetUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service for vet operations.
 */
@RequiredArgsConstructor
@Service
public class VetService {
    private final VetRepository vetRepository;
    private final VetMapper vetMapper;

    /**
     * Finds a page of vets based on the provided sets of IDs and names.
     * This method retrieves vet entities from the database, maps them to DTOs, and returns them as a page.
     *
     * @param ids a set of UUIDs representing the IDs of the vets to be fetched; can be null or empty
     * @param firstNames a set of first names representing the vets to be fetched; can be null or empty
     * @param lastNames a set of last names representing the vets to be fetched; can be null or empty
     * @param pageNumber zero-based page index
     * @param pageSize page size
     * @return a page of {@code VetDto} objects that match the given criteria; if no criteria is provided, all vets are returned
     */
    @Transactional(readOnly = true)
    public @NonNull VetPageDto findVets(
            @Nullable Set<UUID> ids,
            @Nullable Set<String> firstNames,
            @Nullable Set<String> lastNames,
            int pageNumber,
            int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<VetEntity> page = vetRepository.findAll(buildSpecification(ids, firstNames, lastNames), pageable);
        return vetMapper.mapPage(page);
    }

    /**
     * Creates a new vet record.
     *
     * @param vetCreateDto create request DTO
     * @return created vet DTO
     */
    @Transactional
    public @NonNull VetDto createVet(@NonNull VetCreateDto vetCreateDto) {
        VetEntity saved = vetRepository.save(vetMapper.map(vetCreateDto));
        return vetMapper.map(saved);
    }

    /**
     * Deletes a vet by id.
     *
     * @param id vet id
     */
    @Transactional
    public void deleteVet(@NonNull UUID id) {
        if (!vetRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vet not found");
        }
        vetRepository.deleteById(id);
    }

    /**
     * Updates a vet by id.
     *
     * @param id vet id
     * @param vetUpdateDto update request DTO
     * @return updated vet DTO
     */
    @Transactional
    public @NonNull VetDto updateVet(@NonNull UUID id, @NonNull VetUpdateDto vetUpdateDto) {
        VetEntity vetEntity = vetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vet not found"));
        vetEntity.setFirstName(vetUpdateDto.getFirstName());
        vetEntity.setLastName(vetUpdateDto.getLastName());
        VetEntity saved = vetRepository.save(vetEntity);
        return vetMapper.map(saved);
    }

    /**
     * Partially updates a vet by id.
     *
     * @param id vet id
     * @param vetPatchDto patch request DTO
     * @return patched vet DTO
     */
    @Transactional
    public @NonNull VetDto patchVet(@NonNull UUID id, @NonNull VetPatchDto vetPatchDto) {
        VetEntity vetEntity = vetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vet not found"));
        if (vetPatchDto.getFirstName() != null) {
            vetEntity.setFirstName(vetPatchDto.getFirstName());
        }
        if (vetPatchDto.getLastName() != null) {
            vetEntity.setLastName(vetPatchDto.getLastName());
        }
        VetEntity saved = vetRepository.save(vetEntity);
        return vetMapper.map(saved);
    }

    /**
     * Builds a specification with optional filters for vet IDs and names.
     */
    private Specification<VetEntity> buildSpecification(Set<UUID> ids, Set<String> firstNames, Set<String> lastNames) {
        Specification<VetEntity> spec = Specification.where((_, _, builder) -> builder.conjunction());
        if (CollectionUtils.isNotEmpty(ids)) {
            spec = spec.and((root, _, _) -> root.get("id").in(ids));
        }
        if (CollectionUtils.isNotEmpty(firstNames)) {
            spec = spec.and((root, _, builder) -> builder.lower(root.get("firstName")).in(lowerCase(firstNames)));
        }
        if (CollectionUtils.isNotEmpty(lastNames)) {
            spec = spec.and((root, _, builder) -> builder.lower(root.get("lastName")).in(lowerCase(lastNames)));
        }
        return spec;
    }

    /**
     * Normalizes a set of strings to lower case for case-insensitive matching.
     */
    private Set<String> lowerCase(Set<String> strings) {
        return strings.stream()
                .map(name -> name == null ? null : name.toLowerCase())
                .collect(Collectors.toSet());
    }
}
