package com.xc.ai.service;

public interface TongYiService {

    /**
     * 普通聊天
     *
     * @param prompt 消息
     * @return 模型回答
     * @author XieChen
     * @since 2025/05/20
     */
    String chat(String prompt);
}
