package com.bewize.monitorbackend.domains.subject;

import com.bewize.monitorbackend.domains.quiz.Quiz;
import com.bewize.monitorbackend.domains.school.Level;
import com.bewize.monitorbackend.enums.Semester;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Course {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    protected String id;

    private String title;

    private String image;

    private Integer orderNum;

    @Enumerated(EnumType.STRING)
    private Semester semester;

    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;

    @ManyToOne
    @JsonBackReference
    private Domain domain;

    @ManyToOne
    @JsonBackReference
    private Subject subject;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "course_id")
    @JsonBackReference
    private List<Quiz> quizzes;

    protected Boolean active = true;

    public void updateStatus(final Boolean status) {
        this.active = status;
    }

}
