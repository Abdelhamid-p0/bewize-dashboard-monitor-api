package com.bewize.monitorbackend.domains.user;

import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@Data
@NoArgsConstructor
@MappedSuperclass
public class User {
    @Version
    protected Integer version;

    protected String firstName;

    protected String lastName;

    protected String password;

    protected String email;

    protected Boolean active;

    @Builder.Default
    protected Boolean deleted = false;

    public void delete() {
        this.deleted = true;
    }
}
