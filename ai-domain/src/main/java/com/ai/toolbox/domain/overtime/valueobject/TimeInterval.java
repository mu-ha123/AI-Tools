package com.ai.toolbox.domain.overtime.valueobject;

import com.ai.toolbox.common.exception.BizException;
import com.ai.toolbox.common.result.ErrorCode;

import java.time.LocalTime;
import java.util.Objects;

public final class TimeInterval {

    private final LocalTime start;
    private final LocalTime end;

    private TimeInterval(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    public static TimeInterval of(LocalTime start, LocalTime end) {
        if (start == null || end == null) {
            throw new BizException(ErrorCode.OVERTIME_TIME_INVALID, "开始或结束时间不能为空");
        }
        if (!end.isAfter(start)) {
            throw new BizException(ErrorCode.OVERTIME_TIME_INVALID, "结束时间必须晚于开始时间");
        }
        return new TimeInterval(start, end);
    }

    public static TimeInterval tryOf(LocalTime start, LocalTime end) {
        if (start == null || end == null || !end.isAfter(start)) {
            return null;
        }
        return new TimeInterval(start, end);
    }

    public long minutes() {
        return java.time.Duration.between(start, end).toMinutes();
    }

    public TimeInterval intersect(TimeInterval other) {
        if (other == null) {
            return null;
        }
        LocalTime intersectStart = start.isAfter(other.start) ? start : other.start;
        LocalTime intersectEnd = end.isBefore(other.end) ? end : other.end;
        return tryOf(intersectStart, intersectEnd);
    }

    public long intersectMinutes(TimeInterval other) {
        TimeInterval intersect = intersect(other);
        return intersect == null ? 0L : intersect.minutes();
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        TimeInterval that = (TimeInterval) object;
        return Objects.equals(start, that.start) && Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
