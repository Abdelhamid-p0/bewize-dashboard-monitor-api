package com.bewize.monitorbackend.domains.question;

import com.bewize.monitorbackend.enums.QuestionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Question {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    protected String id;

    private String question;

    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    private String image;

    private String feedback;


    private String feedbackAudio;

    private String questionAudio;

    private Integer orderNum;
    @ManyToOne
    private Objective objective;

    @OneToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "question_id")
    private List<SubQuestion> subQuestions;
}
