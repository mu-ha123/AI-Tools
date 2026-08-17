package com.ai.toolbox.infrastructure.ai;

import com.ai.toolbox.common.exception.BizException;
import com.ai.toolbox.common.result.ErrorCode;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorklogAiService {

    private final AiProviderFactory aiProviderFactory;

    public String generateSummary(String workContext) {
        try {
            WorklogAiAssistant assistant = buildAssistant();
            return assistant.generateSummary(workContext);
        } catch (BizException exception) {
            throw exception;
        } catch (Exception exception) {
            log.warn("AI generate work summary failed: {}", exception.getMessage());
            throw new BizException(ErrorCode.AI_UNAVAILABLE, "AI 生成总结失败：" + exception.getMessage());
        }
    }

    private WorklogAiAssistant buildAssistant() {
        ChatModel chatModel = aiProviderFactory.currentChatModel();
        return AiServices.builder(WorklogAiAssistant.class)
                .chatModel(chatModel)
                .build();
    }
}
