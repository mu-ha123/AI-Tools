package com.ai.toolbox.domain.overtime;

import com.ai.toolbox.domain.overtime.entity.DailyAttendance;
import com.ai.toolbox.domain.overtime.entity.MonthlyOvertimeSummary;
import com.ai.toolbox.domain.overtime.valueobject.DailyOvertimeResult;
import com.ai.toolbox.domain.overtime.valueobject.OvertimeCalculationMode;
import com.ai.toolbox.domain.overtime.valueobject.OvertimePolicy;
import com.ai.toolbox.domain.overtime.valueobject.WorkSchedule;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DailyAttendanceTest {

    private final WorkSchedule schedule = WorkSchedule.of(
            LocalTime.of(9, 0), LocalTime.of(18, 0),
            LocalTime.of(12, 0), LocalTime.of(13, 0));

    @Test
    void shouldCalculateLateOvertimeInExcludeMode() {
        DailyAttendance attendance = DailyAttendance.create(
                LocalDate.of(2026, 6, 5), LocalTime.of(9, 0), LocalTime.of(21, 0));
        OvertimePolicy policy = OvertimePolicy.of(480, OvertimeCalculationMode.EXCLUDE_STANDARD);

        DailyOvertimeResult result = attendance.calculateOvertime(schedule, policy);

        assertEquals(180L, result.getLateOvertimeMinutes());
        assertEquals(0L, result.getEarlyOvertimeMinutes());
        assertEquals(180L, result.getOvertimeMinutes());
        assertEquals(660L, result.getActualWorkMinutes());
    }

    @Test
    void shouldCalculateEarlyAndLateOvertimeInExcludeMode() {
        DailyAttendance attendance = DailyAttendance.create(
                LocalDate.of(2026, 6, 6), LocalTime.of(8, 0), LocalTime.of(20, 0));
        OvertimePolicy policy = OvertimePolicy.of(480, OvertimeCalculationMode.EXCLUDE_STANDARD);

        DailyOvertimeResult result = attendance.calculateOvertime(schedule, policy);

        assertEquals(60L, result.getEarlyOvertimeMinutes());
        assertEquals(120L, result.getLateOvertimeMinutes());
        assertEquals(180L, result.getOvertimeMinutes());
    }

    @Test
    void shouldCalculateOvertimeInIncludeMode() {
        DailyAttendance attendance = DailyAttendance.create(
                LocalDate.of(2026, 6, 7), LocalTime.of(9, 0), LocalTime.of(20, 0));
        OvertimePolicy policy = OvertimePolicy.of(480, OvertimeCalculationMode.INCLUDE_STANDARD);

        DailyOvertimeResult result = attendance.calculateOvertime(schedule, policy);

        assertEquals(600L, result.getActualWorkMinutes());
        assertEquals(120L, result.getOvertimeMinutes());
    }

    @Test
    void shouldAggregateMonthlySummary() {
        DailyAttendance day1 = DailyAttendance.create(
                LocalDate.of(2026, 6, 5), LocalTime.of(9, 0), LocalTime.of(21, 0));
        DailyAttendance day2 = DailyAttendance.create(
                LocalDate.of(2026, 6, 6), LocalTime.of(8, 0), LocalTime.of(20, 0));
        OvertimePolicy policy = OvertimePolicy.of(480, OvertimeCalculationMode.EXCLUDE_STANDARD);

        MonthlyOvertimeSummary summary = MonthlyOvertimeSummary.of(
                YearMonth.of(2026, 6), List.of(day1, day2));

        assertEquals(360L, summary.totalMinutes(schedule, policy));
        assertEquals(2, summary.dailyBreakdown(schedule, policy).size());
    }
}
