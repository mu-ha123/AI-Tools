package com.ai.toolbox.common.tool;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ToolDescriptor {

    private final String id;
    private final String name;
    private final String description;
    private final String route;
    private final boolean aiEnabled;

    public static ToolDescriptor from(ToolType toolType) {
        return ToolDescriptor.builder()
                .id(toolType.getId())
                .name(toolType.getName())
                .description(toolType.getDescription())
                .route(toolType.getRoute())
                .aiEnabled(true)
                .build();
    }
}
