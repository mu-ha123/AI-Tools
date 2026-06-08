package com.ai.toolbox.domain.overtime.valueobject;

import com.ai.toolbox.common.exception.BizException;
import com.ai.toolbox.common.result.ErrorCode;
import lombok.Getter;

import java.time.LocalTime;
import java.util.Objects;

@Getter
public final class WorkSchedule {

    private final LocalTime workStartTime;
    private final LocalTime workEndTime;
    private final LocalTime lunchStartTime;
    private final LocalTime lunchEndTime;

    private WorkSchedule(LocalTime workStartTime, LocalTime workEndTime,
                         LocalTime lunchStartTime, LocalTime lunchEndTime) {
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
        this.lunchStartTime = lunchStartTime;
        this.lunchEndTime = lunchEndTime;
        validate();
    }

    public static WorkSchedule of(LocalTime workStartTime, LocalTime workEndTime,
                                  LocalTime lunchStartTime, LocalTime lunchEndTime) {
        return new WorkSchedule(workStartTime, workEndTime, lunchStartTime, lunchEndTime);
    }

    public TimeInterval lunchInterval() {
        if (lunchStartTime == null || lunchEndTime == null) {
            return null;
        }
        return TimeInterval.tryOf(lunchStartTime, lunchEndTime);
    }

    public TimeInterval standardWorkInterval() {
        return TimeInterval.of(workStartTime, workEndTime);
    }

    private void validate() {
        if (workStartTime == null || workEndTime == null) {
            throw new BizException(ErrorCode.OVERTIME_TIME_INVALID, "标准上下班时间不能为空");
        }
        if (!workEndTime.isAfter(workStartTime)) {
            throw new BizException(ErrorCode.OVERTIME_TIME_INVALID, "标准下班时间必须晚于上班时间");
        }
        if ((lunchStartTime == null) != (lunchEndTime == null)) {
            throw new BizException(ErrorCode.OVERTIME_TIME_INVALID, "午休开始和结束时间需同时配置");
        }
        if (lunchStartTime != null && !lunchEndTime.isAfter(lunchStartTime)) {
            throw new BizException(ErrorCode.OVERTIME_TIME_INVALID, "午休结束时间必须晚于开始时间");
        }
        TimeInterval lunch = lunchInterval();
        if (lunch != null) {
            TimeInterval work = standardWorkInterval();
            if (work.intersect(lunch) == null) {
                throw new BizException(ErrorCode.OVERTIME_TIME_INVALID, "午休时段需落在标准工作时间内");
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
        WorkSchedule that = (WorkSchedule) object;
        return Objects.equals(workStartTime, that.workStartTime)
                && Objects.equals(workEndTime, that.workEndTime)
                && Objects.equals(lunchStartTime, that.lunchStartTime)
                && Objects.equals(lunchEndTime, that.lunchEndTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workStartTime, workEndTime, lunchStartTime, lunchEndTime);
    }
}
