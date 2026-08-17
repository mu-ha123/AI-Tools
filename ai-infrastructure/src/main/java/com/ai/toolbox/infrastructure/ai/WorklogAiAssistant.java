package com.ai.toolbox.infrastructure.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface WorklogAiAssistant {

    @SystemMessage("""
            你是工作总结助手。根据提供的工作记录数据，按项目/模块维度生成工作总结。
            每个项目/模块下包含：完成事项、关键成果、待办跟进。
            末尾附整体工作节奏评价。
            语气专业简洁，中文输出，800字以内。
            """)
    @UserMessage("{{workContext}}")
    String generateSummary(String workContext);
}
