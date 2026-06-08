package com.ai.toolbox.domain.overtime.valueobject;

import com.ai.toolbox.common.exception.BizException;
import com.ai.toolbox.common.result.ErrorCode;
import lombok.Getter;

import java.util.Objects;

@Getter
public final class OvertimePolicy {

    public static final int DEFAULT_STANDARD_WORK_MINUTES = 480;

    private final int standardWorkMinutes;
    private final OvertimeCalculationMode calculationMode;

    private OvertimePolicy(int standardWorkMinutes, OvertimeCalculationMode calculationMode) {
        this.standardWorkMinutes = standardWorkMinutes;
        this.calculationMode = calculationMode;
        validate();
    }

    public static OvertimePolicy of(int standardWorkMinutes, OvertimeCalculationMode calculationMode) {
        return new OvertimePolicy(standardWorkMinutes, calculationMode);
    }

    public static OvertimePolicy defaultPolicy() {
        return of(DEFAULT_STANDARD_WORK_MINUTES, OvertimeCalculationMode.EXCLUDE_STANDARD);
    }

    private void validate() {
        if (standardWorkMinutes <= 0) {
            throw new BizException(ErrorCode.OVERTIME_TIME_INVALID, "标准工时必须大于 0");
        }
        if (calculationMode == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "加班计算模式不能为空");
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
        OvertimePolicy that = (OvertimePolicy) object;
        return standardWorkMinutes == that.standardWorkMinutes && calculationMode == that.calculationMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(standardWorkMinutes, calculationMode);
    }
}
