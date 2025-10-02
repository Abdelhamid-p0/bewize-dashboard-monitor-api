package com.bewize.monitorbackend.domains.session;

import com.bewize.monitorbackend.domains.quiz.Quiz;
import com.bewize.monitorbackend.domains.user.Student;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Session {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    protected String id;

    private boolean isFinished;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    private Integer coins;

    private  Integer questionsNum;

    @Builder.Default
    private float progress = 0;

    private LocalDateTime finishDate;

    private LocalDateTime startDate;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "session_id")
    private List<SessionQuestion> responses;

    public float calculateScore(){
        return progress * 10;
    }
}
