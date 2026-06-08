package com.ai.toolbox.application.overtime.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class AttendanceRecordDTO {

    private final Long id;
    private final LocalDate workDate;
    private final LocalTime clockIn;
    private final LocalTime clockOut;
    private final long overtimeMinutes;
    private final long earlyOvertimeMinutes;
    private final long lateOvertimeMinutes;
    private final long actualWorkMinutes;
    private final boolean isLeave;
}
