package com.example.petclinic.vet.internal;

import com.example.petclinic.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

/**
 * Persistence entity representing a vet record.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "vet")
public class VetEntity extends BaseEntity {

    @NotNull
    @Pattern(regexp = "^\\S(.*\\S)?$")
    @Size(min = 1, max = 255)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull
    @Pattern(regexp = "^\\S(.*\\S)?$")
    @Size(min = 1, max = 255)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy hibernateProxy
                ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hibernateProxy
                ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass()
                : getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        VetEntity vetEntity = (VetEntity) o;
        return getId() != null && Objects.equals(getId(), vetEntity.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy
                ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
