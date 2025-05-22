package com.xc.ai.service.impl;

import com.xc.ai.service.TongYiService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class TongYiServiceImpl implements TongYiService  {

    private final ChatClient chatClient;

    @Autowired
    public TongYiServiceImpl(OllamaChatModel model) {
        chatClient = ChatClient.builder(model)
                .build();
    }

    @Override
    public String chat(String msg) {
        return chatClient.prompt()
                .call()
                .content();
    }

    @Override
    public Flux<String> streamChat(String msg) {
        return null;
    }

}
