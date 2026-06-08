package com.ai.toolbox.domain.glossary.entity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GlossaryChangeHistory {

    private final Long id;
    private final Long termId;
    private final String fieldName;
    private final String oldValue;
    private final String newValue;
    private final LocalDateTime changedAt;

    public GlossaryChangeHistory(Long id, Long termId, String fieldName,
                                  String oldValue, String newValue, LocalDateTime changedAt) {
        this.id = id;
        this.termId = termId;
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedAt = changedAt;
    }

    public static GlossaryChangeHistory create(Long termId, String fieldName, String oldValue, String newValue) {
        return new GlossaryChangeHistory(null, termId, fieldName, oldValue, newValue, null);
    }

    public static GlossaryChangeHistory restore(Long id, Long termId, String fieldName,
                                                 String oldValue, String newValue, LocalDateTime changedAt) {
        return new GlossaryChangeHistory(id, termId, fieldName, oldValue, newValue, changedAt);
    }
}
