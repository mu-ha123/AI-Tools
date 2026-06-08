package com.ai.toolbox.infrastructure.config;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnExpression("!'${langchain4j.community.dashscope.chat-model.api-key:}'.isBlank()")
public class DashScopeChatModelConfig {

    @Bean(name = "qwenChatModel")
    public ChatModel qwenChatModel(
            @Value("${langchain4j.community.dashscope.chat-model.api-key}") String apiKey,
            @Value("${langchain4j.community.dashscope.chat-model.model-name:qwen-plus}") String modelName,
            @Value("${langchain4j.community.dashscope.chat-model.temperature:0.3}") Double temperature) {
        return QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature.floatValue())
                .build();
    }
}