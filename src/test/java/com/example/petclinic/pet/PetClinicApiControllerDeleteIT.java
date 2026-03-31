package com.example.petclinic.pet;

import com.example.petclinic.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
@SpringBootTest
class PetClinicApiControllerDeleteIT {

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private PetRepository petRepository;

    @BeforeEach
    void setUp() {
        petRepository.deleteAll();
    }

    @DisplayName("Deleting an existing pet should remove it.")
    @Test
    void deletingExistingPetShouldRemoveIt() {
        PetEntity petEntity = petRepository.save(
                PetEntity.builder()
                        .name("Bella")
                        .build()
        );

        restTestClient.delete()
                .uri(builder -> builder.path("/petclinic/pets/{id}").build(petEntity.getId()))
                .exchange()
                .expectStatus().isNoContent();

        assertThat(petRepository.existsById(petEntity.getId())).isFalse();
        assertThat(petRepository.count()).isZero();
    }

    @DisplayName("Deleting a missing pet should return 404.")
    @Test
    void deletingMissingPetShouldReturnNotFound() {
        UUID missingId = UUID.randomUUID();

        restTestClient.delete()
                .uri(builder -> builder.path("/petclinic/pets/{id}").build(missingId))
                .exchange()
                .expectStatus().isNotFound();

        assertThat(petRepository.count()).isZero();
    }
}
