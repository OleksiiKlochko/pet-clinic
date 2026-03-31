package com.example.petclinic.pet;

import com.example.petclinic.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openapitools.model.PetDto;
import org.openapitools.model.PetUpdateDto;
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
class PetClinicApiControllerUpdateIT {

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private PetRepository petRepository;

    @BeforeEach
    void setUp() {
        petRepository.deleteAll();
    }

    @DisplayName("Updating an existing pet should update its name.")
    @Test
    void updatingExistingPetShouldUpdateName() {
        PetEntity petEntity = petRepository.save(
                PetEntity.builder()
                        .name("Bella")
                        .build()
        );

        restTestClient.put()
                .uri(builder -> builder.path("/petclinic/pets/{id}").build(petEntity.getId()))
                .body(
                        PetUpdateDto.builder()
                                .name("Luna")
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(PetDto.class).value(petDto -> {
                    assertThat(petDto).isNotNull();
                    assertThat(petDto.getId()).isEqualTo(petEntity.getId());
                    assertThat(petDto.getName()).isEqualTo("Luna");
                });

        PetEntity updated = petRepository.findById(petEntity.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Luna");
    }

    @DisplayName("Updating a missing pet should return 404.")
    @Test
    void updatingMissingPetShouldReturnNotFound() {
        UUID missingId = UUID.randomUUID();

        restTestClient.put()
                .uri(builder -> builder.path("/petclinic/pets/{id}").build(missingId))
                .body(
                        PetUpdateDto.builder()
                                .name("Luna")
                                .build()
                )
                .exchange()
                .expectStatus().isNotFound();

        assertThat(petRepository.count()).isZero();
    }

    @DisplayName("Updating a pet with invalid name should be rejected.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", " a", " a ", "a "})
    void updatingPetWithBlankNameShouldBeRejected(String name) {
        PetEntity petEntity = petRepository.save(
                PetEntity.builder()
                        .name("Bella")
                        .build()
        );

        restTestClient.put()
                .uri(builder -> builder.path("/petclinic/pets/{id}").build(petEntity.getId()))
                .body(
                        PetUpdateDto.builder()
                                .name(name)
                                .build()
                )
                .exchange()
                .expectStatus().isBadRequest();

        PetEntity persisted = petRepository.findById(petEntity.getId()).orElseThrow();
        assertThat(persisted.getName()).isEqualTo("Bella");
    }
}
