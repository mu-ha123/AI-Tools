package com.ai.toolbox.domain.overtime.repository;

import com.ai.toolbox.domain.overtime.entity.DailyAttendance;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface AttendanceRecordRepository {

    DailyAttendance save(DailyAttendance attendance);

    Optional<DailyAttendance> findByDate(LocalDate workDate);

    List<DailyAttendance> findByYearMonth(YearMonth yearMonth);

    void deleteByDate(LocalDate workDate);
}
