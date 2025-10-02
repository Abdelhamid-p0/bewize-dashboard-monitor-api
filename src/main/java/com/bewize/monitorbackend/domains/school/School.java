package com.bewize.monitorbackend.domains.school;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class School {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    protected String id;

    private String schoolName;
	private String address;
	private String phone;
	private Integer studentsCount;
	private Boolean active;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

}
