package com.xc.ai.domain.bailian.util;

import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.xc.ai.domain.bailian.vo.BaiLianAiAgentChatVO;
import com.xc.boot.exception.BaseBusinessException;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SseEmitter工具类
 *
 * @author: YangShuaiHua
 * @since: 2025/06/19
 */
@Component
public class SseEmitterUtil {

    // 日志记录器
    private static final Logger logger = LoggerFactory.getLogger(SseEmitterUtil.class);
    /**
     * 处理AI流式响应
     */
    public Disposable processAiStream(SseEmitter emitter, ApplicationParam param,
                                      String sessionId, AtomicBoolean isCompleted,StringBuilder previousText) {
        Flowable<ApplicationResult> resultStream;
        try {
            resultStream = new Application().streamCall(param);
        } catch (NoApiKeyException | InputRequiredException e) {
            logger.error("[百炼智能体] 流调用初始化失败", e);
            sendErrorAndComplete(emitter, "服务初始化失败: " + e.getMessage(), logger, isCompleted);
            return Disposables.empty();
        }

        return resultStream
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(
                        data -> sendDataChunk(emitter, sessionId, data, isCompleted,previousText),
                        error -> handleStreamError(emitter, error, isCompleted),
                        () -> completeStream(emitter, isCompleted)
                );
    }

    /**
     * 发送数据块
     */
    private void sendDataChunk(SseEmitter emitter, String sessionId,
                               ApplicationResult data, AtomicBoolean isCompleted, StringBuilder previousText) {
        if (isCompleted.get()) {
            logger.debug("[SSE] 连接已关闭，忽略数据发送");
            return;
        }

        try {
            String currentFullText = data.getOutput().getText();
            logger.trace("[SSE] 当前完整文本: {}", currentFullText);

            // 计算增量内容
            String delta;
            if (previousText.isEmpty()) {
                // 第一条消息，发送全部内容
                delta = currentFullText;
                previousText.append(currentFullText);
            } else {
                // 提取新增内容
                if (currentFullText.startsWith(previousText.toString())) {
                    delta = currentFullText.substring(previousText.length());
                    previousText.setLength(0);
                    previousText.append(currentFullText);
                } else {
                    // 文本不连续，可能上下文重置，发送完整内容
                    logger.warn("[SSE] 文本不连续，发送完整内容");
                    delta = currentFullText;
                    previousText.setLength(0);
                    previousText.append(currentFullText);
                }
            }

            if (!delta.isEmpty()) {
                logger.trace("[SSE] 发送增量内容: {}", delta);

                // 发送增量内容
                emitter.send(BaiLianAiAgentChatVO.builder()
                        .sessionId(sessionId)
                        .text(delta)
                        .build());
            }
        } catch (IOException | IllegalStateException e) {
            logger.warn("[SSE] 数据发送失败", e);
            // 发送失败时标记为已完成
            isCompleted.set(true);
        }
    }

    /**
     * 注册连接监听器
     */
    public void registerConnectionListeners(SseEmitter emitter, Disposable disposable,
                                            AtomicBoolean isCompleted) {
        // 连接超时处理
        emitter.onTimeout(() -> {
            logger.info("[SSE] 连接超时");
            isCompleted.set(true);
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        });

        // 连接完成/断开处理
        emitter.onCompletion(() -> {
            logger.debug("[SSE] 连接正常完成");
            isCompleted.set(true);
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        });

        // 错误处理
        emitter.onError(error -> {
            logger.warn("[SSE] 连接发生错误", error);
            isCompleted.set(true);
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        });
    }

    /**
     * 错误处理
     */
    private void handleStreamError(SseEmitter emitter, Throwable error, AtomicBoolean isCompleted) {
        if (isCompleted.get()) {
            logger.debug("[SSE] 连接已关闭，忽略流错误");
            return;
        }

        logger.error("[百炼智能体] 流处理异常", error);
        isCompleted.set(true);

        try {
            // 尝试发送错误信息
            emitter.send(SseEmitter.event()
                    .name("error")
                    .data(error.getMessage()));
            emitter.completeWithError(error);
        } catch (IOException | IllegalStateException ex) {
            logger.debug("[SSE] 发送错误时连接已关闭");
        }
    }

    private void completeStream(SseEmitter emitter, AtomicBoolean isCompleted) {
        if (isCompleted.get()) {
            return;
        }

        try {
            emitter.complete();
            logger.debug("[SSE] 流处理正常结束");
            isCompleted.set(true);
        } catch (IllegalStateException e) {
            logger.debug("[SSE] 连接已关闭，忽略完成信号");
        }
    }

    // 公共错误处理工具
    private void sendErrorAndComplete(SseEmitter emitter, String errorMsg, Logger logger,
                                      AtomicBoolean isCompleted) {
        logger.error(errorMsg);
        try {
            emitter.send(SseEmitter.event()
                    .name("error")
                    .data(errorMsg));
            emitter.completeWithError(new BaseBusinessException(errorMsg));
            isCompleted.set(true);
        } catch (IOException | IllegalStateException e) {
            logger.debug("SSE发送错误时连接已关闭");
            isCompleted.set(true);
        }
    }
}
