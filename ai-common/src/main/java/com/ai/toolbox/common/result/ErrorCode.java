package com.ai.toolbox.common.result;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS("A00-000-000", "操作成功"),
    SYSTEM_ERROR("A00-000-001", "系统异常"),
    PARAM_INVALID("A00-000-002", "参数校验失败"),
    AI_UNAVAILABLE("A00-AI-001", "AI 服务暂不可用，请检查配置或稍后重试"),

    OVERTIME_TIME_INVALID("A01-OVERTIME-001", "时间区间非法"),
    OVERTIME_RECORD_NOT_FOUND("A01-OVERTIME-002", "打卡记录不存在"),
    OVERTIME_SETTINGS_NOT_FOUND("A01-OVERTIME-003", "加班配置不存在"),
    OVERTIME_IMPORT_ERROR("A01-OVERTIME-004", "导入失败"),
    GLOSSARY_DUPLICATE("A02-GLOSSARY-001", "名词已存在"),

    WORK_CATEGORY_NOT_FOUND("A03-WORK-001", "分类不存在"),
    WORK_CATEGORY_DEFAULT_DELETE("A03-WORK-002", "预置分类不可删除"),
    WORK_CATEGORY_DUPLICATE("A03-WORK-003", "分类名称已存在"),
    WORK_RECORD_NOT_FOUND("A03-WORK-004", "记录不存在");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
