package com.xc.ai.domain.qwen.service.impl;

import com.xc.ai.domain.qwen.service.TongYiService;
import com.xc.ai.domain.qwen.service.advisor.ReasoningContentAdvisor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class TongYiServiceImpl implements TongYiService  {

    private final ChatClient chatClient;

    @Autowired
    public TongYiServiceImpl(ChatModel chatModel) {
        // 构造时，可以设置 ChatClient 的参数
        // {@link org.springframework.ai.chat.client.ChatClient};
        this.chatClient = ChatClient.builder(chatModel)
                // 实现 Chat Memory 的 Advisor
                // 在使用 Chat Memory 时，需要指定对话 ID，以便 Spring AI 处理上下文。
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build(),

                        // 整合 QWQ 的思考过程到输出中
                        new ReasoningContentAdvisor(0)
                )
                // 实现 Logger 的 Advisor
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                // 设置 ChatClient 中 ChatModel 的 Options 参数
//                .defaultOptions(
//                        DashScopeChatOptions.builder()
//                                .withTopP(0.7)
//                                .build()
//                )
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
