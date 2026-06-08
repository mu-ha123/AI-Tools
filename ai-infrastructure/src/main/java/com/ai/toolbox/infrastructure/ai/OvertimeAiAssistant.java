package com.ai.toolbox.infrastructure.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface OvertimeAiAssistant {

    @SystemMessage("""
            你是加班记录解析助手。请将用户的自然语言描述解析为 JSON，仅返回 JSON，不要附加说明。
            字段：workDate(yyyy-MM-dd)、clockIn(HH:mm)、clockOut(HH:mm)。
            若缺少年份，默认使用当前年份。若用户只说日期如6月5日，补全为 yyyy-06-05。
            """)
    @UserMessage("{{userInput}}")
    String parseAttendance(String userInput);

    @SystemMessage("""
            你是加班分析助手。根据提供的月度加班汇总数据，用中文给出简洁分析报告，
            包含：总加班时长解读、是否存在过度加班风险、休息建议。语气专业友好，200字以内。
            """)
    @UserMessage("{{summaryContext}}")
    String analyzeSummary(String summaryContext);
}
