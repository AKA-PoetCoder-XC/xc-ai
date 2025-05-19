package com.xc.ai.service.impl;

import com.xc.ai.service.TongYiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TongYiServiceImpl implements TongYiService  {

    @Autowired
    private ChatClient chatClient;
//
//    @Autowired
//    private StreamingChatClient streamingChatClient;

    @Override
    public String chat(String prompt) {
        return chatClient.call(prompt);
    }

}
