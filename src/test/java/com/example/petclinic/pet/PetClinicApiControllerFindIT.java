package com.example.petclinic.pet;

import com.example.petclinic.TestcontainersConfiguration;
import com.example.petclinic.pet.internal.PetEntity;
import com.example.petclinic.pet.internal.PetRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openapitools.model.PetDto;
import org.openapitools.model.PetPageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
@SpringBootTest
class PetClinicApiControllerFindIT {
    private static UUID fidoId;
    private static UUID rexId;
    private static UUID miloId;

    @Autowired
    private RestTestClient restTestClient;
    @Autowired
    private PetRepository petRepository;

    @BeforeAll
    static void beforeAll(@Autowired PetRepository petRepository) {
        petRepository.deleteAll();

        PetEntity fido = PetEntity.builder()
                .name("Fido")
                .build();
        PetEntity rex = PetEntity.builder()
                .name("Rex")
                .build();
        PetEntity milo = PetEntity.builder()
                .name("Milo")
                .build();
        petRepository.saveAll(List.of(fido, rex, milo));

        fidoId = fido.getId();
        rexId = rex.getId();
        miloId = milo.getId();
    }

    @DisplayName("Pets should be found by names.")
    @Test
    void petShouldBeFoundByNames() {
        restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/petclinic/pets")
                        .queryParam("pageNumber", 0)
                        .queryParam("pageSize", 20)
                        .queryParam("names", "Fido", "Rex")
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(PetPageDto.class).value(page -> {
                    assertThat(page).isNotNull();

                    PetEntity fido = petRepository.findById(fidoId).orElseThrow();
                    PetEntity rex = petRepository.findById(rexId).orElseThrow();

                    assertThat(page.getItems()).containsExactlyInAnyOrder(
                            PetDto.builder()
                                    .id(fidoId)
                                    .createdAt(OffsetDateTime.ofInstant(fido.getCreatedAt(), ZoneOffset.UTC))
                                    .lastModifiedAt(OffsetDateTime.ofInstant(fido.getLastModifiedAt(), ZoneOffset.UTC))
                                    .name("Fido")
                                    .build(),
                            PetDto.builder()
                                    .id(rexId)
                                    .createdAt(OffsetDateTime.ofInstant(rex.getCreatedAt(), ZoneOffset.UTC))
                                    .lastModifiedAt(OffsetDateTime.ofInstant(rex.getLastModifiedAt(), ZoneOffset.UTC))
                                    .name("Rex")
                                    .build()
                    );
                    assertThat(page.getPageNumber()).isZero();
                    assertThat(page.getPageSize()).isEqualTo(20);
                    assertThat(page.getTotalElements()).isEqualTo(2L);
                    assertThat(page.getTotalPages()).isEqualTo(1);
                });
    }

    @DisplayName("Pets should be found by ids.")
    @Test
    void petsShouldBeFoundByIds() {
        restTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/petclinic/pets")
                                .queryParam("pageNumber", 0)
                                .queryParam("pageSize", 20)
                                .queryParam("ids", rexId, miloId)
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(PetPageDto.class).value(page -> {
                    assertThat(page).isNotNull();

                    PetEntity rex = petRepository.findById(rexId).orElseThrow();
                    PetEntity milo = petRepository.findById(fidoId).orElseThrow();

                    assertThat(page.getItems()).containsExactlyInAnyOrder(
                            PetDto.builder()
                                    .id(rexId)
                                    .createdAt(OffsetDateTime.ofInstant(rex.getCreatedAt(), ZoneOffset.UTC))
                                    .lastModifiedAt(OffsetDateTime.ofInstant(rex.getLastModifiedAt(), ZoneOffset.UTC))
                                    .name("Rex")
                                    .build(),
                            PetDto.builder()
                                    .id(miloId)
                                    .createdAt(OffsetDateTime.ofInstant(milo.getCreatedAt(), ZoneOffset.UTC))
                                    .lastModifiedAt(OffsetDateTime.ofInstant(milo.getLastModifiedAt(), ZoneOffset.UTC))
                                    .name("Milo")
                                    .build()
                    );
                    assertThat(page.getPageNumber()).isZero();
                    assertThat(page.getPageSize()).isEqualTo(20);
                    assertThat(page.getTotalElements()).isEqualTo(2L);
                    assertThat(page.getTotalPages()).isEqualTo(1);
                });
    }

    @DisplayName("Negative page number should be rejected")
    @Test
    void NegativePageNumberShouldBeRejected() {
        restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/petclinic/pets")
                        .queryParam("pageNumber", -1)
                        .queryParam("pageSize", 20)
                        .build()
                )
                .exchange()
                .expectStatus().isBadRequest();
    }

    @DisplayName("Page size out of range should be rejected.")
    @ParameterizedTest
    @ValueSource(ints = {0, 101})
    void pageSizeOutOfRangeShouldBeRejected(int pageSize) {
        restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/petclinic/pets")
                        .queryParam("pageNumber", 0)
                        .queryParam("pageSize", pageSize)
                        .build()
                )
                .exchange()
                .expectStatus().isBadRequest();
    }

    @DisplayName("Invalid pet ids should be rejected.")
    @Test
    void invalidPetIdsShouldBeRejected() {
        restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/petclinic/pets")
                        .queryParam("pageNumber", 0)
                        .queryParam("pageSize", 20)
                        .queryParam("ids", "not-a-uuid")
                        .build()
                )
                .exchange()
                .expectStatus().isBadRequest();
    }
}
