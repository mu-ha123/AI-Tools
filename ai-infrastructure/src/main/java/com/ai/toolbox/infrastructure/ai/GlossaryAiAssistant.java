package com.ai.toolbox.infrastructure.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface GlossaryAiAssistant {

    @SystemMessage("""
            你是业务名词解析助手。请将用户对名词的描述解析为 JSON，仅返回 JSON，不要附加说明。
            字段：name（名词名称）、description（名词解释/定义）、category（所属分类，可选）。
            若用户输入包含明确的名词定义或解释，提取为 description。
            示例：
            用户: "ERP是企业资源计划系统"
            {"name": "ERP", "description": "企业资源计划系统", "category": null}
            用户: "什么是微服务架构？微服务是一种将应用拆分为独立服务的架构风格"
            {"name": "微服务架构", "description": "一种将应用拆分为独立服务的架构风格", "category": "架构设计"}
            """)
    @UserMessage("{{userInput}}")
    String parseTerm(String userInput);
}
