package com.bewize.monitorbackend.domains.school;

import com.bewize.monitorbackend.domains.user.BackUser;
import com.bewize.monitorbackend.domains.user.Student;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Setter
public class Classroom {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    private String title;

    @Builder.Default
    private Boolean deleted = false;


    @OneToOne
    @JoinColumn
    private School school;

    @OneToOne
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "classroom_id")
    private List<Student> students ;

    @ManyToMany
    @JoinTable(
            name = "classroom_teachers",
            joinColumns = @JoinColumn(name = "classroom_id"),
            inverseJoinColumns = @JoinColumn(name = "teachers_id"))
    private Set<BackUser> teachers;

    @Version
    private Integer version;

    public void addStudents(List<Student> students) {
        this.students.addAll(students);
    }

    public void deleteStudent(Student student) {
        this.students.remove(student);
    }

    public void updateTeachers(List<BackUser> teachers) {
        this.teachers = new HashSet<>(teachers);
    }
}
