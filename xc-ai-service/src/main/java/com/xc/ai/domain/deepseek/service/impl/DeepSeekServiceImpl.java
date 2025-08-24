package com.xc.ai.domain.deepseek.service.impl;

import com.xc.ai.domain.deepseek.service.DeepSeekService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class DeepSeekServiceImpl implements DeepSeekService {

    private final ChatClient chatClient;

    public DeepSeekServiceImpl(OpenAiChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel)
                .build();
    }


    @Override
    public String chat(String msg) {
        return chatClient.prompt(msg)
                .call()
                .content();
    }

    @Override
    public Flux<String> streamChat(String msg) {
        return chatClient.prompt(msg)
                .stream()
                .content();
    }
}
