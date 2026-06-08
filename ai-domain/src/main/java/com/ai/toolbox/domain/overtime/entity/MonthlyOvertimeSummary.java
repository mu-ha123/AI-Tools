package com.ai.toolbox.domain.overtime.entity;

import com.ai.toolbox.domain.overtime.valueobject.DailyOvertimeResult;
import com.ai.toolbox.domain.overtime.valueobject.OvertimePolicy;
import com.ai.toolbox.domain.overtime.valueobject.WorkSchedule;
import lombok.Getter;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Getter
public class MonthlyOvertimeSummary {

    private final YearMonth yearMonth;
    private final List<DailyAttendance> records;

    public MonthlyOvertimeSummary(YearMonth yearMonth, List<DailyAttendance> records) {
        this.yearMonth = yearMonth;
        this.records = new ArrayList<>(records == null ? Collections.emptyList() : records);
        this.records.sort(Comparator.comparing(DailyAttendance::getWorkDate));
    }

    public static MonthlyOvertimeSummary of(YearMonth yearMonth, List<DailyAttendance> records) {
        return new MonthlyOvertimeSummary(yearMonth, records);
    }

    public void addRecord(DailyAttendance record) {
        if (record == null) {
            return;
        }
        records.removeIf(item -> item.getWorkDate().equals(record.getWorkDate()));
        records.add(record);
        records.sort(Comparator.comparing(DailyAttendance::getWorkDate));
    }

    public List<DailyOvertimeResult> dailyBreakdown(WorkSchedule schedule, OvertimePolicy policy) {
        List<DailyOvertimeResult> results = new ArrayList<>();
        for (DailyAttendance record : records) {
            results.add(record.calculateOvertime(schedule, policy));
        }
        return results;
    }

    public long totalMinutes(WorkSchedule schedule, OvertimePolicy policy) {
        return dailyBreakdown(schedule, policy).stream()
                .mapToLong(DailyOvertimeResult::getOvertimeMinutes)
                .sum();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        MonthlyOvertimeSummary that = (MonthlyOvertimeSummary) object;
        return Objects.equals(yearMonth, that.yearMonth) && Objects.equals(records, that.records);
    }

    @Override
    public int hashCode() {
        return Objects.hash(yearMonth, records);
    }
}
