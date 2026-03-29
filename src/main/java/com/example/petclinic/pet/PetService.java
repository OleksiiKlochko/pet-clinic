package com.example.petclinic.pet;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.openapitools.model.Pet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PetService {
    private final PetRepository petRepository;
    private final PetMapper petMapper;

    /**
     * Finds a list of pets based on the provided sets of IDs and names.
     * This method retrieves pet entities from the database, maps them to domain models, and returns them as a list.
     *
     * @param ids a set of UUIDs representing the IDs of the pets to be fetched; can be null or empty
     * @param names a set of names representing the names of the pets to be fetched; can be null or empty
     * @return a list of {@code Pet} objects that match the given criteria; if no criteria is provided, all pets are returned
     */
    @Transactional(readOnly = true)
    public List<Pet> findPets(Set<UUID> ids, Set<String> names) {
        return findPetEntitiesByCriteria(ids, names).stream()
                .map(petMapper::toPet)
                .toList();
    }

    private List<PetEntity> findPetEntitiesByCriteria(Set<UUID> ids, Set<String> names) {
        boolean hasIdFilter = hasFilter(ids);
        boolean hasNameFilter = hasFilter(names);

        if (hasIdFilter && hasNameFilter) {
            return petRepository.findByIdInAndNameInIgnoreCase(ids, names);
        }

        if (hasIdFilter) {
            return petRepository.findByIdIn(ids);
        }

        if (hasNameFilter) {
            return petRepository.findByNameInIgnoreCase(names);
        }

        return petRepository.findAll();
    }

    private boolean hasFilter(Set<?> values) {
        return CollectionUtils.isNotEmpty(values);
    }
}
