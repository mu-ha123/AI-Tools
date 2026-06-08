package com.ai.toolbox.infrastructure.ai;

import com.ai.toolbox.common.exception.BizException;
import com.ai.toolbox.common.result.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OvertimeAiService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final AiProviderFactory aiProviderFactory;
    private final ObjectMapper objectMapper;

    public Map<String, String> parseAttendance(String userInput) {
        try {
            OvertimeAiAssistant assistant = buildAssistant();
            String json = assistant.parseAttendance(userInput);
            JsonNode node = objectMapper.readTree(cleanJson(json));
            return Map.of(
                    "workDate", node.get("workDate").asText(),
                    "clockIn", node.get("clockIn").asText(),
                    "clockOut", node.get("clockOut").asText());
        } catch (BizException exception) {
            throw exception;
        } catch (Exception exception) {
            log.warn("AI parse attendance failed: {}", exception.getMessage());
            throw new BizException(ErrorCode.AI_UNAVAILABLE, "AI 解析失败：" + exception.getMessage());
        }
    }

    public String analyzeSummary(String summaryContext) {
        try {
            OvertimeAiAssistant assistant = buildAssistant();
            return assistant.analyzeSummary(summaryContext);
        } catch (BizException exception) {
            throw exception;
        } catch (Exception exception) {
            log.warn("AI analyze summary failed: {}", exception.getMessage());
            throw new BizException(ErrorCode.AI_UNAVAILABLE, "AI 分析失败：" + exception.getMessage());
        }
    }

    public ParsedAttendance toParsedAttendance(Map<String, String> parsed) {
        try {
            LocalDate workDate = LocalDate.parse(parsed.get("workDate"), DATE_FORMATTER);
            LocalTime clockIn = parseTime(parsed.get("clockIn"));
            LocalTime clockOut = parseTime(parsed.get("clockOut"));
            return new ParsedAttendance(workDate, clockIn, clockOut);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new BizException(ErrorCode.AI_UNAVAILABLE, "AI 返回的考勤数据格式异常");
        }
    }

    private LocalTime parseTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalTime.parse(value, TIME_FORMATTER);
    }

    private OvertimeAiAssistant buildAssistant() {
        ChatModel chatModel = aiProviderFactory.currentChatModel();
        return AiServices.builder(OvertimeAiAssistant.class)
                .chatModel(chatModel)
                .build();
    }

    private String cleanJson(String raw) {
        String trimmed = raw == null ? "" : raw.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceAll("(?s)^```json\\s*", "").replaceAll("```\\s*$", "").trim();
        }
        return trimmed;
    }

    public record ParsedAttendance(LocalDate workDate, LocalTime clockIn, LocalTime clockOut) {
    }
}
