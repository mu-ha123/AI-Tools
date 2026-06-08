package com.ai.toolbox.infrastructure.ai;

import com.ai.toolbox.common.exception.BizException;
import com.ai.toolbox.common.result.ErrorCode;
import com.ai.toolbox.infrastructure.config.AiProviderProperties;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class AiProviderFactory {

    private final AiProviderProperties aiProviderProperties;
    private final Map<String, ChatModel> chatModels;

    public AiProviderFactory(
            AiProviderProperties aiProviderProperties,
            ObjectProvider<ChatModel> ollamaChatModelProvider,
            @Qualifier("qwenChatModel") ObjectProvider<ChatModel> dashscopeChatModelProvider) {
        this.aiProviderProperties = aiProviderProperties;
        this.chatModels = new HashMap<>();
        ollamaChatModelProvider.ifAvailable(model -> chatModels.put("ollama", model));
        dashscopeChatModelProvider.ifAvailable(model -> chatModels.put("dashscope", model));
    }

    public ChatModel currentChatModel() {
        String provider = aiProviderProperties.getProvider();
        ChatModel chatModel = chatModels.get(provider);
        if (chatModel == null) {
            throw new BizException(ErrorCode.AI_UNAVAILABLE,
                    "未找到 AI 提供商 [" + provider + "]，请检查 application.yml 配置及相关服务是否可用");
        }
        return chatModel;
    }

    public String currentProvider() {
        return aiProviderProperties.getProvider();
    }

    public boolean available() {
        return Optional.ofNullable(chatModels.get(aiProviderProperties.getProvider())).isPresent();
    }
}