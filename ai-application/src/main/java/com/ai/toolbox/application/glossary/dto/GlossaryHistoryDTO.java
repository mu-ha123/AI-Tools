package com.ai.toolbox.application.glossary.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GlossaryHistoryDTO {

    private final Long id;
    private final Long termId;
    private final String fieldName;
    private final String oldValue;
    private final String newValue;
    private final LocalDateTime changedAt;
}
