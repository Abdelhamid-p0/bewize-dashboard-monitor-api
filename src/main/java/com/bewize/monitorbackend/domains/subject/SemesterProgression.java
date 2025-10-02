package com.bewize.monitorbackend.domains.subject;

import com.bewize.monitorbackend.domains.quiz.Quiz;
import com.bewize.monitorbackend.dto.CourseDto;
import com.bewize.monitorbackend.dto.QuizDto;
import com.bewize.monitorbackend.enums.Semester;
import com.bewize.monitorbackend.enums.Type;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SemesterProgression {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    protected String id;

    private Integer orderNum;

    @Enumerated(EnumType.STRING)
    private Semester semester;

    @OneToOne
    private Course course;

    private String schoolId;

    protected Boolean active = true;

    public static SemesterProgression create(Course course, CourseDto courseUpdates, String schoolId) {
        if (Objects.isNull(course))
            return null;
        return SemesterProgression
                .builder()
                .course(course)
                .orderNum(courseUpdates.getOrderNum())
                .semester(courseUpdates.getSemester())
                .active(courseUpdates.getStatus())
                .schoolId(schoolId)
                .build();
    }

    public SemesterProgression update(CourseDto courseUpdates) {
        this.active = courseUpdates.getStatus();
        this.semester = courseUpdates.getSemester();
        this.orderNum = courseUpdates.getOrderNum();
        return this;
    }

    public static List<CourseDto> getUpdatedCourses(List<SemesterProgression> semesterProgression) {
        if (Objects.isNull(semesterProgression))
            return null;

        return semesterProgression.stream().map(sp -> {
            List<QuizDto> quizzes = setSecondQuizFlashcardRedirectInQuiz(sp);

            CourseDto courseDto = CourseDto.fromEntity(sp.course);
            courseDto.setSemester(sp.semester);
            courseDto.setQuizzes(quizzes);
            // courseDto.setStatus(sp.active);
            courseDto.setOrderNum(sp.orderNum);
            return courseDto;
        }).collect(Collectors.toList());

    }

    public static List<QuizDto> setSecondQuizFlashcardRedirectInQuiz(SemesterProgression semesterProgression) {

        if (semesterProgression == null)
            return null;

        List<QuizDto> quizDtos = new java.util.ArrayList<>();

        List<Quiz> quizzes = semesterProgression.getCourse() != null ? semesterProgression.getCourse().getQuizzes()
                : null;

        if (quizzes != null) {
            for (Quiz quiz : quizzes) {
                QuizDto quizdto = QuizDto.fromEntity(quiz);
                quizDtos.add(quizdto);
            }
        }

        if (quizDtos.size() > 1) {
            QuizDto quizWithTypeQuiz = quizDtos.stream()
                .filter(q -> q.getType() == Type.FLASHCARD)
                .findFirst()
                .orElse(null);
            quizDtos.stream()
                    .filter(q -> q.getType() == Type.QUIZ)
                    .findFirst()
                    .ifPresent(quiz -> quizWithTypeQuiz.setFlashcardRedirectQuizId(quiz.getId()));

        }

        return quizDtos;

    }
}
