package com.example.petclinic.vet;

import com.example.petclinic.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openapitools.model.VetDto;
import org.openapitools.model.VetUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
@SpringBootTest
class VetClinicApiControllerUpdateIT {

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private VetRepository vetRepository;

    @BeforeEach
    void setUp() {
        vetRepository.deleteAll();
    }

    @DisplayName("Updating an existing vet should update its name.")
    @Test
    void updatingExistingVetShouldUpdateName() {
        VetEntity vetEntity = vetRepository.save(
                VetEntity.builder()
                        .firstName("Anna")
                        .lastName("Smith")
                        .build()
        );

        restTestClient.put()
                .uri(builder -> builder.path("/petclinic/vets/{id}").build(vetEntity.getId()))
                .body(
                        VetUpdateDto.builder()
                                .firstName("Anna")
                                .lastName("Taylor")
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(VetDto.class).value(vetDto -> {
                    assertThat(vetDto).isNotNull();
                    assertThat(vetDto.getId()).isEqualTo(vetEntity.getId());
                    assertThat(vetDto.getFirstName()).isEqualTo("Anna");
                    assertThat(vetDto.getLastName()).isEqualTo("Taylor");
                    assertThat(vetDto.getCreatedAt()).isNotNull();
                    assertThat(vetDto.getLastModifiedAt()).isNotNull();

                    VetEntity updated = vetRepository.findById(vetDto.getId())
                            .orElseThrow(() -> new AssertionError("Vet with id " + vetDto.getId() + " not found."));

                    assertThat(updated.getFirstName()).isEqualTo("Anna");
                    assertThat(updated.getLastName()).isEqualTo("Taylor");
                    assertThat(updated.getCreatedAt()).isCloseTo(vetDto.getCreatedAt().toInstant(), within(1, ChronoUnit.MILLIS));
                    assertThat(updated.getLastModifiedAt()).isCloseTo(vetDto.getLastModifiedAt().toInstant(), within(1, ChronoUnit.MILLIS));
                });

        assertThat(vetRepository.count()).isEqualTo(1);
    }

    @DisplayName("Updating a missing vet should return 404.")
    @Test
    void updatingMissingVetShouldReturnNotFound() {
        UUID missingId = UUID.randomUUID();

        restTestClient.put()
                .uri(builder -> builder.path("/petclinic/vets/{id}").build(missingId))
                .body(
                        VetUpdateDto.builder()
                                .firstName("Anna")
                                .lastName("Taylor")
                                .build()
                )
                .exchange()
                .expectStatus().isNotFound();

        assertThat(vetRepository.count()).isZero();
    }

    @DisplayName("Updating a vet with invalid first name should be rejected.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", " a", " a ", "a "})
    void updatingVetWithBlankFirstNameShouldBeRejected(String firstName) {
        VetEntity vetEntity = vetRepository.save(
                VetEntity.builder()
                        .firstName("Anna")
                        .lastName("Smith")
                        .build()
        );

        restTestClient.put()
                .uri(builder -> builder.path("/petclinic/vets/{id}").build(vetEntity.getId()))
                .body(
                        VetUpdateDto.builder()
                                .firstName(firstName)
                                .lastName("Taylor")
                                .build()
                )
                .exchange()
                .expectStatus().isBadRequest();

        VetEntity persisted = vetRepository.findById(vetEntity.getId())
                .orElseThrow(() -> new AssertionError("Vet with id " + vetEntity.getId() + " not found."));
        assertThat(persisted.getFirstName()).isEqualTo("Anna");
        assertThat(persisted.getLastName()).isEqualTo("Smith");
        assertThat(vetRepository.count()).isEqualTo(1);
    }

    @DisplayName("Updating a vet with invalid last name should be rejected.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", " a", " a ", "a "})
    void updatingVetWithBlankLastNameShouldBeRejected(String lastName) {
        VetEntity vetEntity = vetRepository.save(
                VetEntity.builder()
                        .firstName("Anna")
                        .lastName("Smith")
                        .build()
        );

        restTestClient.put()
                .uri(builder -> builder.path("/petclinic/vets/{id}").build(vetEntity.getId()))
                .body(
                        VetUpdateDto.builder()
                                .firstName("Anna")
                                .lastName(lastName)
                                .build()
                )
                .exchange()
                .expectStatus().isBadRequest();

        VetEntity persisted = vetRepository.findById(vetEntity.getId())
                .orElseThrow(() -> new AssertionError("Vet with id " + vetEntity.getId() + " not found."));
        assertThat(persisted.getFirstName()).isEqualTo("Anna");
        assertThat(persisted.getLastName()).isEqualTo("Smith");
        assertThat(vetRepository.count()).isEqualTo(1);
    }
}
