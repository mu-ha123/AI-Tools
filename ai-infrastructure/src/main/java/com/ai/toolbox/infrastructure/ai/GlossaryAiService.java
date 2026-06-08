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

@Slf4j
@Service
@RequiredArgsConstructor
public class GlossaryAiService {

    private final AiProviderFactory aiProviderFactory;
    private final ObjectMapper objectMapper;

    public ParsedTerm parseTerm(String userInput) {
        try {
            GlossaryAiAssistant assistant = buildAssistant();
            String json = assistant.parseTerm(userInput);
            JsonNode node = objectMapper.readTree(cleanJson(json));
            String name = node.get("name").asText();
            String description = node.get("description").asText();
            String category = node.has("category") && !node.get("category").isNull()
                    ? node.get("category").asText() : null;
            return new ParsedTerm(name, description, category);
        } catch (BizException exception) {
            throw exception;
        } catch (Exception exception) {
            log.warn("AI parse glossary term failed: {}", exception.getMessage());
            throw new BizException(ErrorCode.AI_UNAVAILABLE, "AI 解析失败：" + exception.getMessage());
        }
    }

    private GlossaryAiAssistant buildAssistant() {
        ChatModel chatModel = aiProviderFactory.currentChatModel();
        return AiServices.builder(GlossaryAiAssistant.class)
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

    public record ParsedTerm(String name, String description, String category) {
    }
}
