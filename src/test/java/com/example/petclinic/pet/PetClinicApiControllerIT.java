package com.example.petclinic.pet;

import com.example.petclinic.TestcontainersConfiguration;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
@SpringBootTest
class PetClinicApiControllerIT {
    private static UUID fidoId;
    private static UUID rexId;
    private static UUID miloId;

    @Autowired
    private RestTestClient restTestClient;

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
                    assertThat(page.getItems()).containsExactlyInAnyOrder(
                            PetDto.builder()
                                    .id(fidoId)
                                    .name("Fido")
                                    .build(),
                            PetDto.builder()
                                    .id(rexId)
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
                    assertThat(page.getItems()).containsExactlyInAnyOrder(
                            PetDto.builder()
                                    .id(rexId)
                                    .name("Rex")
                                    .build(),
                            PetDto.builder()
                                    .id(miloId)
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
