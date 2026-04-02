package com.example.petclinic.vet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * Repository for vet persistence and query operations.
 */
public interface VetRepository extends JpaRepository<VetEntity, UUID>, JpaSpecificationExecutor<VetEntity> {
}
