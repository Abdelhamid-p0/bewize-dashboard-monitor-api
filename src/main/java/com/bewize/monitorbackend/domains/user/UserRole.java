package com.bewize.monitorbackend.domains.user;

import com.bewize.monitorbackend.enums.RoleName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRole {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    protected String id;

    @Version
    private Integer version;

    @Enumerated(EnumType.STRING)
    private RoleName role;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private BackUser user;

}
