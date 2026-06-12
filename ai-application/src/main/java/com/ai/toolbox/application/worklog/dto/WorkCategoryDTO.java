package com.ai.toolbox.application.worklog.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkCategoryDTO {

    private final Long id;
    private final String name;
    private final String color;
    private final int sortOrder;
    private final boolean isDefault;
}
