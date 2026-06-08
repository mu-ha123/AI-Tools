package com.ai.toolbox.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "glossary_change_history")
public class GlossaryChangeHistoryDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long termId;

    @Column(nullable = false, length = 50)
    private String fieldName;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String oldValue;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String newValue;

    @Column(nullable = false)
    private LocalDateTime changedAt;
}
