package com.ai.toolbox.application.glossary.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GlossaryTermDTO {

    private final Long id;
    private final Long systemId;
    private final String name;
    private final String description;
    private final String category;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
