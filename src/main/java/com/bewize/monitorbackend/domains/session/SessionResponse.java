package com.bewize.monitorbackend.domains.session;

import com.bewize.monitorbackend.domains.question.Answer;
import com.bewize.monitorbackend.domains.question.SubQuestion;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SessionResponse {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    protected String id;

    @ManyToOne
    private Answer answer;

    @ManyToOne
    private SubQuestion subQuestion;

    private Boolean correct;
}
