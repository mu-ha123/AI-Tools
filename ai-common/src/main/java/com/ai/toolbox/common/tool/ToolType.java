package com.ai.toolbox.common.tool;

import lombok.Getter;

@Getter
public enum ToolType {

    OVERTIME("overtime", "加班计算器", "根据上下班时间与计算模式统计每日及每月加班时长", "/tools/overtime.html"),
    GLOSSARY("glossary", "业务名词库", "管理系统中的业务名词，支持多系统切换、模糊搜索与变更追溯", "/tools/glossary.html");

    private final String id;
    private final String name;
    private final String description;
    private final String route;

    ToolType(String id, String name, String description, String route) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.route = route;
    }
}
