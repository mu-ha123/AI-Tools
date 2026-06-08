package com.ai.toolbox.domain.overtime.entity;

import com.ai.toolbox.common.exception.BizException;
import com.ai.toolbox.common.result.ErrorCode;
import com.ai.toolbox.domain.overtime.valueobject.DailyOvertimeResult;
import com.ai.toolbox.domain.overtime.valueobject.OvertimeCalculationMode;
import com.ai.toolbox.domain.overtime.valueobject.OvertimePolicy;
import com.ai.toolbox.domain.overtime.valueobject.TimeInterval;
import com.ai.toolbox.domain.overtime.valueobject.WorkSchedule;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Getter
public class DailyAttendance {

    private final Long id;
    private final LocalDate workDate;
    private final LocalTime clockIn;
    private final LocalTime clockOut;
    private final boolean isLeave;

    public DailyAttendance(Long id, LocalDate workDate, LocalTime clockIn, LocalTime clockOut, boolean isLeave) {
        this.id = id;
        this.workDate = workDate;
        this.clockIn = clockIn;
        this.clockOut = clockOut;
        this.isLeave = isLeave;
        validate();
    }

    public static DailyAttendance create(LocalDate workDate, LocalTime clockIn, LocalTime clockOut) {
        return new DailyAttendance(null, workDate, clockIn, clockOut, false);
    }

    public static DailyAttendance createLeave(LocalDate workDate) {
        return new DailyAttendance(null, workDate, LocalTime.of(0, 0), LocalTime.of(0, 1), true);
    }

    public static DailyAttendance restore(Long id, LocalDate workDate, LocalTime clockIn, LocalTime clockOut, boolean isLeave) {
        return new DailyAttendance(id, workDate, clockIn, clockOut, isLeave);
    }

    public DailyOvertimeResult calculateOvertime(WorkSchedule schedule, OvertimePolicy policy) {
        if (isLeave) {
            return DailyOvertimeResult.builder()
                    .workDate(workDate)
                    .overtimeMinutes(0L)
                    .earlyOvertimeMinutes(0L)
                    .lateOvertimeMinutes(0L)
                    .actualWorkMinutes(0L)
                    .build();
        }
        if (workDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            return calculateSaturdayOvertime(schedule);
        }

        TimeInterval attendance = TimeInterval.of(clockIn, clockOut);
        TimeInterval lunch = schedule.lunchInterval();
        long actualWorkMinutes = attendance.minutes() - attendance.intersectMinutes(lunch);

        if (policy.getCalculationMode() == OvertimeCalculationMode.INCLUDE_STANDARD) {
            long overtimeMinutes = Math.max(0L, actualWorkMinutes - policy.getStandardWorkMinutes());
            return DailyOvertimeResult.builder()
                    .workDate(workDate)
                    .overtimeMinutes(overtimeMinutes)
                    .earlyOvertimeMinutes(0L)
                    .lateOvertimeMinutes(0L)
                    .actualWorkMinutes(actualWorkMinutes)
                    .build();
        }

        long earlyMinutes = calculateEarlyOvertime(schedule, attendance, lunch);
        long lateMinutes = calculateLateOvertime(schedule, attendance, lunch);
        return DailyOvertimeResult.builder()
                .workDate(workDate)
                .overtimeMinutes(earlyMinutes + lateMinutes)
                .earlyOvertimeMinutes(earlyMinutes)
                .lateOvertimeMinutes(lateMinutes)
                .actualWorkMinutes(actualWorkMinutes)
                .build();
    }

    private DailyOvertimeResult calculateSaturdayOvertime(WorkSchedule schedule) {
        TimeInterval attendance = TimeInterval.of(clockIn, clockOut);
        TimeInterval lunch = schedule.lunchInterval();
        long actualWorkMinutes = attendance.minutes() - attendance.intersectMinutes(lunch);

        return DailyOvertimeResult.builder()
                .workDate(workDate)
                .overtimeMinutes(actualWorkMinutes)
                .earlyOvertimeMinutes(0L)
                .lateOvertimeMinutes(0L)
                .actualWorkMinutes(actualWorkMinutes)
                .build();
    }

    private long calculateEarlyOvertime(WorkSchedule schedule, TimeInterval attendance, TimeInterval lunch) {
        if (!clockIn.isBefore(schedule.getWorkStartTime())) {
            return 0L;
        }
        LocalTime earlyEnd = attendance.getEnd().isBefore(schedule.getWorkStartTime())
                ? attendance.getEnd() : schedule.getWorkStartTime();
        TimeInterval earlyInterval = TimeInterval.of(clockIn, earlyEnd);
        return earlyInterval.minutes() - earlyInterval.intersectMinutes(lunch);
    }

    private long calculateLateOvertime(WorkSchedule schedule, TimeInterval attendance, TimeInterval lunch) {
        if (!clockOut.isAfter(schedule.getWorkEndTime())) {
            return 0L;
        }
        LocalTime lateStart = attendance.getStart().isAfter(schedule.getWorkEndTime())
                ? attendance.getStart() : schedule.getWorkEndTime();
        TimeInterval lateInterval = TimeInterval.of(lateStart, clockOut);
        return lateInterval.minutes() - lateInterval.intersectMinutes(lunch);
    }

    private void validate() {
        if (workDate == null) {
            throw new BizException(ErrorCode.OVERTIME_TIME_INVALID, "日期不能为空");
        }
        if (!isLeave) {
            if (clockIn == null || clockOut == null) {
                throw new BizException(ErrorCode.OVERTIME_TIME_INVALID, "打卡时间不能为空");
            }
            if (!clockOut.isAfter(clockIn)) {
                throw new BizException(ErrorCode.OVERTIME_TIME_INVALID, "下班时间必须晚于上班时间");
            }
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        DailyAttendance that = (DailyAttendance) object;
        return Objects.equals(id, that.id)
                && Objects.equals(workDate, that.workDate)
                && Objects.equals(clockIn, that.clockIn)
                && Objects.equals(clockOut, that.clockOut)
                && isLeave == that.isLeave;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, workDate, clockIn, clockOut, isLeave);
    }
}
