package com.example.petclinic.pet;

import com.example.petclinic.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openapitools.model.PetDto;
import org.openapitools.model.PetPatchDto;
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
class PetClinicApiControllerPatchIT {

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private PetRepository petRepository;

    @BeforeEach
    void setUp() {
        petRepository.deleteAll();
    }

    @DisplayName("Patching an existing pet should update its name.")
    @Test
    void patchingExistingPetShouldUpdateName() {
        PetEntity petEntity = petRepository.save(
                PetEntity.builder()
                        .name("Bella")
                        .build()
        );

        restTestClient.patch()
                .uri(builder -> builder.path("/petclinic/pets/{id}").build(petEntity.getId()))
                .body(
                        PetPatchDto.builder()
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

    @DisplayName("Patching a pet with null name should keep the existing name.")
    @Test
    void patchingPetWithNullNameShouldKeepExistingName() {
        PetEntity petEntity = petRepository.save(
                PetEntity.builder()
                        .name("Bella")
                        .build()
        );

        restTestClient.patch()
                .uri(builder -> builder.path("/petclinic/pets/{id}").build(petEntity.getId()))
                .body(new PetPatchDto())
                .exchange()
                .expectStatus().isOk()
                .expectBody(PetDto.class).value(petDto -> {
                    assertThat(petDto).isNotNull();
                    assertThat(petDto.getId()).isEqualTo(petEntity.getId());
                    assertThat(petDto.getName()).isEqualTo("Bella");
                });

        PetEntity updated = petRepository.findById(petEntity.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Bella");
    }

    @DisplayName("Patching a missing pet should return 404.")
    @Test
    void patchingMissingPetShouldReturnNotFound() {
        UUID missingId = UUID.randomUUID();

        restTestClient.patch()
                .uri(builder -> builder.path("/petclinic/pets/{id}").build(missingId))
                .body(
                        PetPatchDto.builder()
                                .name("Luna")
                                .build()
                )
                .exchange()
                .expectStatus().isNotFound();

        assertThat(petRepository.count()).isZero();
    }

    @DisplayName("Patching a pet with blank name should be rejected.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", " a", " a ", "a "})
    void patchingPetWithBlankNameShouldBeRejected(String name) {
        PetEntity petEntity = petRepository.save(
                PetEntity.builder()
                        .name("Bella")
                        .build()
        );

        restTestClient.patch()
                .uri(builder -> builder.path("/petclinic/pets/{id}").build(petEntity.getId()))
                .body(
                        PetPatchDto.builder()
                                .name(name)
                                .build()
                )
                .exchange()
                .expectStatus().isBadRequest();

        PetEntity persisted = petRepository.findById(petEntity.getId()).orElseThrow();
        assertThat(persisted.getName()).isEqualTo("Bella");
    }
}
