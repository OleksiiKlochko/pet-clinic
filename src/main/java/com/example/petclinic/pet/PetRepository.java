package com.example.petclinic.pet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * Repository for pet persistence and query operations.
 */
public interface PetRepository extends JpaRepository<PetEntity, UUID>, JpaSpecificationExecutor<PetEntity> {
}
