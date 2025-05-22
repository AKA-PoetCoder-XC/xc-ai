package com.xc.ai.service.impl;

import com.xc.ai.service.TongYiService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class TongYiServiceImpl implements TongYiService  {

    @Autowired
    private ChatClient chatClient;

    @Override
    public String chat(String msg) {
        return null;
    }

//    @Override
//    public Flux<String> streamChat(String msg) {
//        return streamingChatClient.stream(msg);
//    }

}
