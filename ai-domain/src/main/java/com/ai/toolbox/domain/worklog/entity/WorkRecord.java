package com.ai.toolbox.domain.worklog.entity;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class WorkRecord {

    private final Long id;
    private final Long categoryId;
    private final String title;
    private final String description;
    private final LocalDate recordDate;
    private final LocalDate endDate;
    private final String dateType;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public WorkRecord(Long id, Long categoryId, String title, String description,
                      LocalDate recordDate, LocalDate endDate, String dateType, String status,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.recordDate = recordDate;
        this.endDate = endDate;
        this.dateType = dateType;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static WorkRecord create(Long categoryId, String title, String description,
                                    LocalDate recordDate, LocalDate endDate, String dateType) {
        return new WorkRecord(null, categoryId, title, description, recordDate, endDate, dateType, "DONE", null, null);
    }

    public static WorkRecord createTodo(Long categoryId, String title, String description,
                                        LocalDate recordDate, LocalDate endDate, String dateType) {
        return new WorkRecord(null, categoryId, title, description, recordDate, endDate, dateType, "TODO", null, null);
    }

    public static WorkRecord restore(Long id, Long categoryId, String title, String description,
                                     LocalDate recordDate, LocalDate endDate, String dateType, String status,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new WorkRecord(id, categoryId, title, description, recordDate, endDate, dateType, status, createdAt, updatedAt);
    }
}
