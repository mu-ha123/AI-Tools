package com.ai.toolbox.application.worklog.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class WorkRecordDTO {

    private final Long id;
    private final Long categoryId;
    private final String categoryName;
    private final String categoryColor;
    private final String title;
    private final String description;
    private final LocalDate recordDate;
    private final LocalDate endDate;
    private final String dateType;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
