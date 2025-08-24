package com.xc.ai.domain.deepseek.service;

import reactor.core.publisher.Flux;

public interface DeepSeekService {

    /**
     * 普通聊天
     *
     * @param msg 消息
     * @return 模型回答
     * @author XieChen
     * @since 2025/05/20
     */
    String chat(String msg);

    /**
     * 流式响应聊天聊天
     *
     * @param msg 消息
     * @return 模型回答
     * @author XieChen
     * @since 2025/05/20
     */
    Flux<String> streamChat(String msg);

}
