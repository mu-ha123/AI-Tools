package com.ai.toolbox.domain.glossary.entity;

import lombok.Getter;

@Getter
public class GlossarySystem {

    private final Long id;
    private final String name;
    private final int sortOrder;

    public GlossarySystem(Long id, String name, int sortOrder) {
        this.id = id;
        this.name = name;
        this.sortOrder = sortOrder;
    }

    public static GlossarySystem create(String name, int sortOrder) {
        return new GlossarySystem(null, name, sortOrder);
    }

    public static GlossarySystem restore(Long id, String name, int sortOrder) {
        return new GlossarySystem(id, name, sortOrder);
    }
}
