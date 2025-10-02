package com.bewize.monitorbackend.domains.user;

import com.bewize.monitorbackend.domains.school.Classroom;
import com.bewize.monitorbackend.domains.school.Level;
import com.bewize.monitorbackend.domains.school.School;
import com.bewize.monitorbackend.enums.AuthProvider;
import com.bewize.monitorbackend.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class Student extends User {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    protected String id;

    private String cne;

    @Column(columnDefinition = "DATE")
    private Date birthday;

    private String avatar;

    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;

    @ManyToOne
    @JoinColumn(name = "level_school_id")
    private Level levelSchool;

    @ManyToOne
    private BackUser parent;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phone;

    @Column(columnDefinition = "DATE")
    private Date lastLogin;

    @Builder.Default
    private Integer finishedCoursesCount = 0;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    private String sub;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;


    @Column(columnDefinition = "DATE")
    private Date singupDate;

    @Column(columnDefinition = "DATE")
    private Date lastVisit;

    private String storeCountry;

    private String locationCountry;

    private String locationCity;

    private String notificationId;

    public static Student create(String sub, String email, String firstName, String lastName, AuthProvider provider){
        Student student = new Student();
        student.sub = sub;
        student.email = Objects.isNull(email) ? student.sub : email;
        student.firstName = firstName;
        student.lastName = lastName;
        student.provider = provider;
        student.cne = student.email;
        student.active = true;
        student.singupDate = new Date();
        return student;
    }

    public static Student create(String sub, String email, String firstName, String lastName, AuthProvider provider ,String storeCountry){
        Student student = new Student();
        student.sub = sub;
        student.email = Objects.isNull(email) ? student.sub : email;
        student.firstName = firstName;
        student.lastName = lastName;
        student.provider = provider;
        student.cne = student.email;
        student.active = true;
        student.singupDate = new Date();
        student.storeCountry = storeCountry;
        return student;
    }

    public static Student create(String sub, String email, String firstName, String lastName, AuthProvider provider ,String storeCountry , String notificationId){
        Student student = new Student();
        student.sub = sub;
        student.email = Objects.isNull(email) ? student.sub : email;
        student.firstName = firstName;
        student.lastName = lastName;
        student.provider = provider;
        student.cne = student.email;
        student.active = true;
        student.singupDate = new Date();
        student.storeCountry = storeCountry;
        student.notificationId = notificationId;
        return student;
    }

}
