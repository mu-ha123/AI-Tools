package com.ai.toolbox.domain.worklog.entity;

import lombok.Getter;

@Getter
public class WorkCategory {

    private final Long id;
    private final String name;
    private final String color;
    private final int sortOrder;
    private final boolean isDefault;

    public WorkCategory(Long id, String name, String color, int sortOrder, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sortOrder = sortOrder;
        this.isDefault = isDefault;
    }

    public static WorkCategory create(String name, String color, int sortOrder) {
        return new WorkCategory(null, name, color, sortOrder, false);
    }

    public static WorkCategory restore(Long id, String name, String color, int sortOrder, boolean isDefault) {
        return new WorkCategory(id, name, color, sortOrder, isDefault);
    }
}
