package com.bewize.monitorbackend.domains.school;

import com.bewize.monitorbackend.domains.user.BackUser;
import com.bewize.monitorbackend.enums.Cycle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class Level {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    protected String id;

    private String levelName;

    @Enumerated(EnumType.STRING)
    private Cycle cycle;

    @JsonIgnore
    @ManyToMany(mappedBy = "levels")
    private List<BackUser> teachers;
}
