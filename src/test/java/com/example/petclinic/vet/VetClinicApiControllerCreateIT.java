package com.example.petclinic.vet;

import com.example.petclinic.TestcontainersConfiguration;
import com.example.petclinic.vet.internal.VetEntity;
import com.example.petclinic.vet.internal.VetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openapitools.model.VetCreateDto;
import org.openapitools.model.VetDto;
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
class VetClinicApiControllerCreateIT {

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private VetRepository vetRepository;

    @BeforeEach
    void setUp() {
        vetRepository.deleteAll();
    }

    @DisplayName("A new vet should be created and saved.")
    @Test
    void newVetShouldBeCreatedAndSaved() {
        restTestClient.post()
                .uri("/petclinic/vets")
                .body(
                        VetCreateDto.builder()
                                .firstName("Anna")
                                .lastName("Smith")
                                .build()
                )
                .exchange()
                .expectStatus().isCreated()
                .expectBody(VetDto.class).value(vetDto -> {
                    assertThat(vetDto).isNotNull();
                    assertThat(vetDto.getId()).isNotNull();
                    assertThat(vetDto.getFirstName()).isEqualTo("Anna");
                    assertThat(vetDto.getLastName()).isEqualTo("Smith");
                    assertThat(vetDto.getCreatedAt()).isNotNull();
                    assertThat(vetDto.getLastModifiedAt()).isNotNull();

                    VetEntity anna = vetRepository.findById(vetDto.getId())
                            .orElseThrow(() -> new AssertionError("Vet with id " + vetDto.getId() + " not found."));

                    assertThat(anna.getFirstName()).isEqualTo("Anna");
                    assertThat(anna.getLastName()).isEqualTo("Smith");
                    assertThat(anna.getCreatedAt()).isCloseTo(vetDto.getCreatedAt().toInstant(), within(1, ChronoUnit.MILLIS));
                    assertThat(anna.getLastModifiedAt()).isCloseTo(vetDto.getLastModifiedAt().toInstant(), within(1, ChronoUnit.MILLIS));
                });

        assertThat(vetRepository.count()).isEqualTo(1);
    }

    @DisplayName("Creating a new vet with invalid first name should be rejected.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", " a", " a ", "a "})
    void creatingNewVetWithBlankFirstNameShouldBeRejected(String firstName) {
        restTestClient.post()
                .uri("/petclinic/vets")
                .body(
                        VetCreateDto.builder()
                                .firstName(firstName)
                                .lastName("Smith")
                                .build()
                )
                .exchange()
                .expectStatus().isBadRequest();

        assertThat(vetRepository.count()).isZero();
    }

    @DisplayName("Creating a new vet with invalid last name should be rejected.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", " a", " a ", "a "})
    void creatingNewVetWithBlankLastNameShouldBeRejected(String lastName) {
        restTestClient.post()
                .uri("/petclinic/vets")
                .body(
                        VetCreateDto.builder()
                                .firstName("Anna")
                                .lastName(lastName)
                                .build()
                )
                .exchange()
                .expectStatus().isBadRequest();

        assertThat(vetRepository.count()).isZero();
    }

    @Test
    @DisplayName("Creating a new vet with too long names should be rejected.")
    void creatingNewVetWithTooLongNameShouldBeRejected() {
        restTestClient.post()
                .uri("/petclinic/vets")
                .body(
                        VetCreateDto.builder()
                                .firstName("a".repeat(256))
                                .lastName("b".repeat(256))
                                .build()
                )
                .exchange()
                .expectStatus().isBadRequest();

        assertThat(vetRepository.count()).isZero();
    }
}
