package com.ai.toolbox.interfaces.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AttendanceRecordRequest {

    @NotNull
    private LocalDate workDate;

    @NotNull
    private LocalTime clockIn;

    @NotNull
    private LocalTime clockOut;
}
