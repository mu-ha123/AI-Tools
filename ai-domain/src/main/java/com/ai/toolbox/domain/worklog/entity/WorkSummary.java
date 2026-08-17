package com.ai.toolbox.domain.worklog.entity;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class WorkSummary {

    private final Long id;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String content;
    private final LocalDateTime createdAt;

    public WorkSummary(Long id, LocalDate startDate, LocalDate endDate, String content, LocalDateTime createdAt) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static WorkSummary create(LocalDate startDate, LocalDate endDate, String content) {
        return new WorkSummary(null, startDate, endDate, content, null);
    }

    public static WorkSummary restore(Long id, LocalDate startDate, LocalDate endDate, String content, LocalDateTime createdAt) {
        return new WorkSummary(id, startDate, endDate, content, createdAt);
    }
}
