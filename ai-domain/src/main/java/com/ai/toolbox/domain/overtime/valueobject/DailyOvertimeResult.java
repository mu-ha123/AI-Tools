package com.ai.toolbox.domain.overtime.valueobject;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DailyOvertimeResult {

    private final LocalDate workDate;
    private final long overtimeMinutes;
    private final long earlyOvertimeMinutes;
    private final long lateOvertimeMinutes;
    private final long actualWorkMinutes;
}
