package com.ai.toolbox.infrastructure.persistence.entity;

import com.ai.toolbox.domain.overtime.valueobject.OvertimeCalculationMode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "overtime_settings")
public class OvertimeSettingsDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime workStartTime;

    @Column(nullable = false)
    private LocalTime workEndTime;

    private LocalTime lunchStartTime;
    private LocalTime lunchEndTime;

    @Column(nullable = false)
    private Integer standardWorkMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OvertimeCalculationMode calculationMode;
}