package com.ai.toolbox.interfaces.dto;

import com.ai.toolbox.domain.overtime.valueobject.OvertimeCalculationMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class OvertimeSettingsRequest {

    @NotNull
    private LocalTime workStartTime;

    @NotNull
    private LocalTime workEndTime;

    private LocalTime lunchStartTime;
    private LocalTime lunchEndTime;

    @Min(1)
    private int standardWorkMinutes = 480;

    @NotNull
    private OvertimeCalculationMode calculationMode;
}
