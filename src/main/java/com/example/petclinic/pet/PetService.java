package com.example.petclinic.pet;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.openapitools.model.PetCreateDto;
import org.openapitools.model.PetDto;
import org.openapitools.model.PetPageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PetService {
    private final PetRepository petRepository;
    private final PetMapper petMapper;

    /**
     * Finds a page of pets based on the provided sets of IDs and names.
     * This method retrieves pet entities from the database, maps them to DTOs, and returns them as a page.
     *
     * @param ids a set of UUIDs representing the IDs of the pets to be fetched; can be null or empty
     * @param names a set of names representing the names of the pets to be fetched; can be null or empty
     * @param pageNumber zero-based page index
     * @param pageSize page size
     * @return a page of {@code PetDto} objects that match the given criteria; if no criteria is provided, all pets are returned
     */
    @Transactional(readOnly = true)
    public @NonNull PetPageDto findPets(
            @Nullable Set<UUID> ids,
            @Nullable Set<String> names,
            int pageNumber,
            int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<PetEntity> page = petRepository.findAll(buildSpecification(ids, names), pageable);
        return petMapper.mapPage(page);
    }

    /**
     * Creates a new pet record.
     *
     * @param petCreateDto create request DTO
     * @return created pet DTO
     */
    @Transactional
    public @NonNull PetDto createPet(@NonNull PetCreateDto petCreateDto) {
        PetEntity saved = petRepository.save(petMapper.map(petCreateDto));
        return petMapper.map(saved);
    }

    /**
     * Builds a specification with optional filters for pet IDs and names.
     */
    private Specification<PetEntity> buildSpecification(Set<UUID> ids, Set<String> names) {
        Specification<PetEntity> spec = Specification.where((_, _, builder) -> builder.conjunction());
        if (CollectionUtils.isNotEmpty(ids)) {
            spec = spec.and((root, _, _) -> root.get("id").in(ids));
        }
        if (CollectionUtils.isNotEmpty(names)) {
            spec = spec.and((root, _, builder) -> builder.lower(root.get("name")).in(lowerCase(names)));
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
