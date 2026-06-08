package com.ai.toolbox.application.overtime.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MonthlySummaryDTO {

    private final int year;
    private final int month;
    private final long totalOvertimeMinutes;
    private final String totalOvertimeText;
    private final List<AttendanceRecordDTO> dailyRecords;
}
