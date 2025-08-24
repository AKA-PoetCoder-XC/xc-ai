package com.xc.ai.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
public class SseServer {

    private static final Map<String, SseEmitter> sseClients = new ConcurrentHashMap<>();

    public static SseEmitter connect(String userId) {
        SseEmitter sseEmitter = new SseEmitter(0L);
        sseEmitter.onTimeout(timeoutCallback(userId));
        sseEmitter.onCompletion(completionCallback(userId));
        sseEmitter.onError(errorCallback(userId));
        sseClients.put(userId, sseEmitter);
        return sseEmitter;
    }

    public static void sendMsg(String userId,
                               String msg,
                               String msgType) {
        if (CollectionUtils.isEmpty(sseClients) || !sseClients.containsKey(userId)) {
            return;
        }

        try {
            sseClients.get(userId).send(
                    SseEmitter.event()
                            .id(userId)
                            .data(msg)
                            .name(msgType)
            );
        } catch (IOException e) {
            log.error("SSE发送消息异常...{}", e.getMessage());
            remove(userId);
        }

    }

    public static Runnable timeoutCallback(String userId) {
        return () -> {
            log.info("SSE连接超时...");
            remove(userId);
        };
    }

    public static Runnable completionCallback(String userId) {
        return () -> {
            log.info("SSE连接关闭...");
            remove(userId);
        };
    }

    public static Consumer<Throwable> errorCallback(String userId) {
        return throwable -> {
            log.error("SSE异常...", throwable);
            remove(userId);
        };
    }

    public static void remove(String userId) {
        log.info("SSE连接被移除,移除用户ID为:{}", userId);
        sseClients.remove(userId);
    }

}
