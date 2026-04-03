package com.example.petclinic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModulithStructureTest {

    @DisplayName("Module structure should be verified.")
    @Test
    void moduleStructureShouldBeVerified() {
        ApplicationModules.of(PetClinicApplication.class).verify();
    }
}
