package com.ai.toolbox.application.overtime.dto;

import com.ai.toolbox.domain.overtime.valueobject.OvertimeCalculationMode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class OvertimeSettingsDTO {

    private final LocalTime workStartTime;
    private final LocalTime workEndTime;
    private final LocalTime lunchStartTime;
    private final LocalTime lunchEndTime;
    private final int standardWorkMinutes;
    private final OvertimeCalculationMode calculationMode;
}
