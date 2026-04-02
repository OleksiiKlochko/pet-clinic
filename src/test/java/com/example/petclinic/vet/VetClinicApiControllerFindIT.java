package com.example.petclinic.vet;

import com.example.petclinic.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openapitools.model.VetDto;
import org.openapitools.model.VetPageDto;
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
class VetClinicApiControllerFindIT {
    private static UUID annaId;
    private static UUID bobId;
    private static UUID caraId;

    @Autowired
    private RestTestClient restTestClient;
    @Autowired
    private VetRepository vetRepository;

    @BeforeAll
    static void beforeAll(@Autowired VetRepository vetRepository) {
        vetRepository.deleteAll();

        VetEntity anna = VetEntity.builder()
                .firstName("Anna")
                .lastName("Smith")
                .build();
        VetEntity bob = VetEntity.builder()
                .firstName("Bob")
                .lastName("Smith")
                .build();
        VetEntity cara = VetEntity.builder()
                .firstName("Cara")
                .lastName("Jones")
                .build();
        vetRepository.saveAll(List.of(anna, bob, cara));

        annaId = anna.getId();
        bobId = bob.getId();
        caraId = cara.getId();
    }

    @DisplayName("Vets should be found by first names.")
    @Test
    void vetsShouldBeFoundByFirstNames() {
        restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/petclinic/vets")
                        .queryParam("pageNumber", 0)
                        .queryParam("pageSize", 20)
                        .queryParam("firstNames", "Anna", "Bob")
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(VetPageDto.class).value(page -> {
                    assertThat(page).isNotNull();

                    VetEntity anna = vetRepository.findById(annaId).orElseThrow();
                    VetEntity bob = vetRepository.findById(bobId).orElseThrow();

                    assertThat(page.getItems()).containsExactlyInAnyOrder(
                            VetDto.builder()
                                    .id(annaId)
                                    .createdAt(OffsetDateTime.ofInstant(anna.getCreatedAt(), ZoneOffset.UTC))
                                    .lastModifiedAt(OffsetDateTime.ofInstant(anna.getLastModifiedAt(), ZoneOffset.UTC))
                                    .firstName("Anna")
                                    .lastName("Smith")
                                    .build(),
                            VetDto.builder()
                                    .id(bobId)
                                    .createdAt(OffsetDateTime.ofInstant(bob.getCreatedAt(), ZoneOffset.UTC))
                                    .lastModifiedAt(OffsetDateTime.ofInstant(bob.getLastModifiedAt(), ZoneOffset.UTC))
                                    .firstName("Bob")
                                    .lastName("Smith")
                                    .build()
                    );
                    assertThat(page.getPageNumber()).isZero();
                    assertThat(page.getPageSize()).isEqualTo(20);
                    assertThat(page.getTotalElements()).isEqualTo(2L);
                    assertThat(page.getTotalPages()).isEqualTo(1);
                });
    }

    @DisplayName("Vets should be found by last names.")
    @Test
    void vetsShouldBeFoundByLastNames() {
        restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/petclinic/vets")
                        .queryParam("pageNumber", 0)
                        .queryParam("pageSize", 20)
                        .queryParam("lastNames", "Smith")
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(VetPageDto.class).value(page -> {
                    assertThat(page).isNotNull();

                    VetEntity anna = vetRepository.findById(annaId).orElseThrow();
                    VetEntity bob = vetRepository.findById(bobId).orElseThrow();

                    assertThat(page.getItems()).containsExactlyInAnyOrder(
                            VetDto.builder()
                                    .id(annaId)
                                    .createdAt(OffsetDateTime.ofInstant(anna.getCreatedAt(), ZoneOffset.UTC))
                                    .lastModifiedAt(OffsetDateTime.ofInstant(anna.getLastModifiedAt(), ZoneOffset.UTC))
                                    .firstName("Anna")
                                    .lastName("Smith")
                                    .build(),
                            VetDto.builder()
                                    .id(bobId)
                                    .createdAt(OffsetDateTime.ofInstant(bob.getCreatedAt(), ZoneOffset.UTC))
                                    .lastModifiedAt(OffsetDateTime.ofInstant(bob.getLastModifiedAt(), ZoneOffset.UTC))
                                    .firstName("Bob")
                                    .lastName("Smith")
                                    .build()
                    );
                    assertThat(page.getPageNumber()).isZero();
                    assertThat(page.getPageSize()).isEqualTo(20);
                    assertThat(page.getTotalElements()).isEqualTo(2L);
                    assertThat(page.getTotalPages()).isEqualTo(1);
                });
    }

    @DisplayName("Vets should be found by ids.")
    @Test
    void vetsShouldBeFoundByIds() {
        restTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/petclinic/vets")
                                .queryParam("pageNumber", 0)
                                .queryParam("pageSize", 20)
                                .queryParam("ids", bobId, caraId)
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(VetPageDto.class).value(page -> {
                    assertThat(page).isNotNull();

                    VetEntity bob = vetRepository.findById(bobId).orElseThrow();
                    VetEntity cara = vetRepository.findById(caraId).orElseThrow();

                    assertThat(page.getItems()).containsExactlyInAnyOrder(
                            VetDto.builder()
                                    .id(bobId)
                                    .createdAt(OffsetDateTime.ofInstant(bob.getCreatedAt(), ZoneOffset.UTC))
                                    .lastModifiedAt(OffsetDateTime.ofInstant(bob.getLastModifiedAt(), ZoneOffset.UTC))
                                    .firstName("Bob")
                                    .lastName("Smith")
                                    .build(),
                            VetDto.builder()
                                    .id(caraId)
                                    .createdAt(OffsetDateTime.ofInstant(cara.getCreatedAt(), ZoneOffset.UTC))
                                    .lastModifiedAt(OffsetDateTime.ofInstant(cara.getLastModifiedAt(), ZoneOffset.UTC))
                                    .firstName("Cara")
                                    .lastName("Jones")
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
    void negativePageNumberShouldBeRejected() {
        restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/petclinic/vets")
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
                        .path("/petclinic/vets")
                        .queryParam("pageNumber", 0)
                        .queryParam("pageSize", pageSize)
                        .build()
                )
                .exchange()
                .expectStatus().isBadRequest();
    }

    @DisplayName("Invalid vet ids should be rejected.")
    @Test
    void invalidVetIdsShouldBeRejected() {
        restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/petclinic/vets")
                        .queryParam("pageNumber", 0)
                        .queryParam("pageSize", 20)
                        .queryParam("ids", "not-a-uuid")
                        .build()
                )
                .exchange()
                .expectStatus().isBadRequest();
    }
}
