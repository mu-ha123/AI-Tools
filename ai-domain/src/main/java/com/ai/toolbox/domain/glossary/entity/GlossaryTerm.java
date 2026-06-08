package com.ai.toolbox.domain.glossary.entity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GlossaryTerm {

    private final Long id;
    private final Long systemId;
    private final String name;
    private final String description;
    private final String category;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public GlossaryTerm(Long id, Long systemId, String name, String description,
                        String category, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.systemId = systemId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static GlossaryTerm create(Long systemId, String name, String description, String category) {
        return new GlossaryTerm(null, systemId, name, description, category, null, null);
    }

    public static GlossaryTerm restore(Long id, Long systemId, String name, String description,
                                        String category, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new GlossaryTerm(id, systemId, name, description, category, createdAt, updatedAt);
    }
}
