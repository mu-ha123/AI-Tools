package com.ai.toolbox.application.glossary.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GlossarySystemDTO {

    private final Long id;
    private final String name;
    private final int sortOrder;
}
