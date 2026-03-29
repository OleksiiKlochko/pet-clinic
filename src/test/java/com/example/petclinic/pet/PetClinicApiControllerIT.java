package com.example.petclinic.pet;

import com.example.petclinic.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.Pet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
@SpringBootTest
class PetClinicApiControllerIT {

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private PetRepository petRepository;

    @BeforeEach
    void setUp() {
        petRepository.deleteAll();
    }

    @Test
    void getPets_filtersByNames() {
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

        restTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/petclinic/pets")
                                .queryParam("names", "Fido", "Rex")
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Pet>>() {
                }).value(pets ->
                        assertThat(pets)
                                .extracting(Pet::getName)
                                .containsExactlyInAnyOrder("Fido", "Rex")
                );
    }
}
