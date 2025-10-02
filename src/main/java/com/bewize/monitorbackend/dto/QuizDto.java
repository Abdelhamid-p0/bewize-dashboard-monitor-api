package com.bewize.monitorbackend.dto;

import com.bewize.monitorbackend.domains.quiz.Quiz;
import com.bewize.monitorbackend.domains.session.Session;
import com.bewize.monitorbackend.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizDto {

    private String id;

    private String title;

    private Type type;

    private SessionDto session;

    private Integer coins;

    private String courseId;

    private String flashcardRedirectQuizId;



    public QuizDto(String id, String title, Type type, Session session) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.session = SessionDto.fromEntity(session);
        
    }

    public Quiz toEntity() {
        return Quiz.builder()
                .id(id)
                .type(type)
                .title(title)
                .build();
    }

    static public QuizDto fromEntity(Quiz quiz) {
        return QuizDto.builder()
                .id(quiz.getId())
                .type(quiz.getType())
                .title(quiz.getTitle())
                
                .build();
    }

}
