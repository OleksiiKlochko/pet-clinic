package com.example.petclinic.pet;

import com.example.petclinic.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openapitools.model.PetCreateDto;
import org.openapitools.model.PetDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
@SpringBootTest
class PetClinicApiControllerCreateIT {

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private PetRepository petRepository;

    @BeforeEach
    void setUp() {
        petRepository.deleteAll();
    }

    @DisplayName("A new pet should be created and saved.")
    @Test
    void newPetShouldBeCreatedAndSaved() {
        restTestClient.post()
                .uri("/petclinic/pets")
                .body(
                        PetCreateDto.builder()
                                .name("A")
                                .build()
                )
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PetDto.class).value(petDto -> {
                    assertThat(petDto).isNotNull();
                    assertThat(petDto.getId()).isNotNull();
                    assertThat(petDto.getName()).isEqualTo("A");
                    assertThat(petDto.getCreatedAt()).isNotNull();
                    assertThat(petDto.getLastModifiedAt()).isNotNull();

                    PetEntity a = petRepository.findById(petDto.getId())
                            .orElseThrow(() -> new AssertionError("Pet with id " + petDto.getId() + " not found."));

                    assertThat(a.getName()).isEqualTo("A");
                    assertThat(a.getCreatedAt()).isCloseTo(petDto.getCreatedAt().toInstant(), within(1, ChronoUnit.MILLIS));
                    assertThat(a.getLastModifiedAt()).isCloseTo(petDto.getLastModifiedAt().toInstant(), within(1, ChronoUnit.MILLIS));

                });

        assertThat(petRepository.count()).isEqualTo(1);
    }

    @DisplayName("Creating a new pet with invalid name should be rejected.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", " a", " a ", "a "})
    void creatingNewPetWithBlankNameShouldBeRejected(String name) {
        restTestClient.post()
                .uri("/petclinic/pets")
                .body(
                        PetCreateDto.builder()
                                .name(name)
                                .build()
                )
                .exchange()
                .expectStatus().isBadRequest();

        assertThat(petRepository.count()).isZero();
    }

    @Test
    @DisplayName("Creating a new pet with too long name should be rejected.")
    void creatingNewPetWithTooLongNameShouldBeRejected() {
        restTestClient.post()
                .uri("/petclinic/pets")
                .body(
                        PetCreateDto.builder()
                                .name("a".repeat(256))
                                .build()
                )
                .exchange()
                .expectStatus().isBadRequest();

        assertThat(petRepository.count()).isZero();
    }
}
