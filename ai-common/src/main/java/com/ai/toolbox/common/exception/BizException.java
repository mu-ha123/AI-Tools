package com.ai.toolbox.common.exception;

import com.ai.toolbox.common.result.ErrorCode;
import lombok.Getter;

@Getter
public class BizException extends RuntimeException {

    private final String code;

    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}
