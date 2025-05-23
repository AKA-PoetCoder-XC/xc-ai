package com.xc.ai.domain.ollama.service.impl;

import com.xc.ai.domain.ollama.service.OllamaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class OllamaServiceImpl implements OllamaService {

    private final ChatModel chatModel;

    @Autowired
    public OllamaServiceImpl(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String chat(String msg) {
        String response = chatModel.call(msg);
        return response;
    }

    @Override
    public Flux<String> streamChat(String msg) {
        return chatModel.stream(msg);
    }

}
