package com.bewize.monitorbackend.domains.quiz;

import com.bewize.monitorbackend.domains.question.Question;
import com.bewize.monitorbackend.domains.session.Session;
import com.bewize.monitorbackend.domains.subject.Course;
import com.bewize.monitorbackend.enums.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Quiz {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    protected String id;

    private String title;

    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne
    private Course course;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "quiz_id")
    private List<Question> questions;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "quiz_id")
    private List<Session> sessions;




}
