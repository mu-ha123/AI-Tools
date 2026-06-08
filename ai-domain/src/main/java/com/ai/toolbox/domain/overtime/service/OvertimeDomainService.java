package com.ai.toolbox.domain.overtime.service;

import com.ai.toolbox.domain.overtime.entity.DailyAttendance;
import com.ai.toolbox.domain.overtime.entity.MonthlyOvertimeSummary;
import com.ai.toolbox.domain.overtime.valueobject.DailyOvertimeResult;
import com.ai.toolbox.domain.overtime.valueobject.OvertimePolicy;
import com.ai.toolbox.domain.overtime.valueobject.WorkSchedule;

import java.time.YearMonth;
import java.util.List;

public class OvertimeDomainService {

    public DailyOvertimeResult calculateDaily(DailyAttendance attendance, WorkSchedule schedule, OvertimePolicy policy) {
        return attendance.calculateOvertime(schedule, policy);
    }

    public MonthlyOvertimeSummary buildMonthlySummary(YearMonth yearMonth, List<DailyAttendance> records) {
        return MonthlyOvertimeSummary.of(yearMonth, records);
    }
}
