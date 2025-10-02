package com.bewize.monitorbackend.dto;

import com.bewize.monitorbackend.domains.subject.Course;
import com.bewize.monitorbackend.enums.Semester;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {
    private String id;
    private String title;
    private String image;
    private Integer orderNum;

    private String levelId;
    private Semester semester;

    private String domainTitle;
    private String subjectTitle;

    private List<QuizDto> quizzes;
    private Boolean status;

    public CourseDto(String id, String title, String image, Integer orderNum, Semester semester, String subjectTitle, Boolean status){
        this.id = id;
        this.title = title;
        this.image = image;
        this.orderNum = orderNum;
        this.semester = semester;
        this.subjectTitle = subjectTitle;
        this.status = status;
    }
    public Course toEntity() {
        return Course.builder()
                .id(id)
                .title(title)
                .image(image)
                .orderNum(orderNum)
                .semester(semester)
                .active(status)
                .build();
    }

    static public CourseDto fromEntity(Course course) {
        List<QuizDto> quizzes = null;
        if (course.getQuizzes() != null)
            quizzes = course.getQuizzes().stream().map(QuizDto::fromEntity).collect(Collectors.toList());
        return CourseDto.builder()
                .id(course.getId())
                .title(course.getTitle())
                .image(course.getImage())
                .orderNum(course.getOrderNum())
                .semester(course.getSemester())
                .quizzes(quizzes)
                .levelId(Objects.isNull(course.getLevel()) ? null : course.getLevel().getId())
                .domainTitle(Objects.isNull(course.getDomain()) ? null : course.getDomain().getTitle())
                .subjectTitle(Objects.isNull(course.getSubject()) ? null : course.getSubject().getTitle())
                .status(course.getActive())
                .build();
    }
}
