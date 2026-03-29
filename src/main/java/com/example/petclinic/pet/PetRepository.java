package com.example.petclinic.pet;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PetRepository extends JpaRepository<PetEntity, UUID> {

    List<PetEntity> findByIdIn(Set<UUID> ids);

    List<PetEntity> findByNameInIgnoreCase(Set<String> names);

    List<PetEntity> findByIdInAndNameInIgnoreCase(Set<UUID> ids, Set<String> names);
}
