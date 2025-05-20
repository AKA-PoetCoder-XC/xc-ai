package com.xc.ai.service.impl;

import com.alibaba.cloud.ai.tongyi.chat.TongYiChatClient;
import com.xc.ai.service.TongYiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.StreamingChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class TongYiServiceImpl implements TongYiService  {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private StreamingChatClient streamingChatClient;

    @Autowired
    private TongYiChatClient tongYiChatClient;

    @Override
    public String chat(String msg) {
        return chatClient.call(msg);
    }

    @Override
    public Flux<String> streamChat(String msg) {
        return streamingChatClient.stream(msg);
    }

}
