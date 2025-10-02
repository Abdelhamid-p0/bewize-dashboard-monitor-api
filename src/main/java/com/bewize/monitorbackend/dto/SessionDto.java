package com.bewize.monitorbackend.dto;

import com.bewize.monitorbackend.domains.session.Session;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SessionDto {

    private String id;

    private Float progress;

    private boolean isFinished;

    private LocalDateTime startDate;

    private LocalDateTime finishDate;

    static public SessionDto fromEntity(Session session) {
        if (session == null) return null;
        return SessionDto.builder().isFinished(session.isFinished())
                .startDate(session.getStartDate())
                .finishDate(session.getFinishDate())
                .progress(session.getProgress())
                .id(session.getId()).build();
    }

}
