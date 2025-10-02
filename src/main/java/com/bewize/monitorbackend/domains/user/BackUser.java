package com.bewize.monitorbackend.domains.user;

import com.bewize.monitorbackend.domains.school.Classroom;
import com.bewize.monitorbackend.domains.school.Level;
import com.bewize.monitorbackend.domains.school.School;
import com.bewize.monitorbackend.domains.subject.Subject;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class BackUser extends User {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    protected String id;

    @OneToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "user_id")
    private List<UserRole> userRoles;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    @OneToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "parent_id", nullable = true)
    private List<Student> children;

    @ManyToMany
    @JoinTable(
            name = "teacher_level",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "level_id"))
    private List<Level> levels;

    @ManyToMany
    @JoinTable(
            name = "teacher_subject",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private List<Subject> subjects;

    @ManyToMany
    @JoinTable(
            name = "classroom_teachers",
            joinColumns = @JoinColumn(name = "teachers_id"),
            inverseJoinColumns = @JoinColumn(name = "classroom_id"))
    private List<Classroom> classrooms;

    private String phone;

    public void createPassword(String encode){
       this.setPassword(encode);
    }
}
