package com.ai.toolbox.application.worklog.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class WorkSummaryDTO {

    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String content;
    private LocalDateTime createdAt;
}
