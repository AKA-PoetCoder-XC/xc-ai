package com.xc.ai.controller;

import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.xc.ai.domain.bailian.dto.BaiLianAiAgentChatDTO;
import com.xc.ai.domain.bailian.service.BaiLianAiAgentService;
import com.xc.ai.properties.ApplicationProperties;
import com.xc.boot.utils.Assert;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

@Tag(name = "百炼相关接口")
@RestController
@RequestMapping("/bai-lian")
public class BaiLianController {

    // 日志记录器
    private static final Logger log = LoggerFactory.getLogger(BaiLianController.class);
    // Map<智能体应用code, 所有百炼平台智能体服务实例>
    private final Map<String, BaiLianAiAgentService> codeMapAiAgentService;

    private final ThreadPoolTaskExecutor taskExecutor;

    private final ApplicationProperties applicationProperties;
    // 构造器统一注入实例对象
    public BaiLianController(Map<String, BaiLianAiAgentService> codeMapAiAgentService,
                                    @Qualifier("sseTaskExecutor") ThreadPoolTaskExecutor taskExecutor,
                                    ApplicationProperties applicationProperties) {
        this.codeMapAiAgentService = codeMapAiAgentService;
        this.taskExecutor = taskExecutor;
        this.applicationProperties = applicationProperties;
    }

    @PostMapping("/stream-chat")
    @Operation(summary = "阿里百炼智能体应用")
    public SseEmitter streamChat(@Valid @RequestBody BaiLianAiAgentChatDTO dto) {
        BaiLianAiAgentService baiLianAiAgentService = codeMapAiAgentService.get(dto.getApp().getCode());
        Assert.isTrue(baiLianAiAgentService != null, "[阿里百炼智能体应用-聊天]应用不存在!");
        // 创建带超时设置的SSE发射器（建议从配置读取超时时间）
        long timeout = applicationProperties.getSseEmitter().getTimeout();
        SseEmitter emitter = new SseEmitter(timeout);

        // 使用CompletableFuture管理异步任务
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                baiLianAiAgentService.streamChat(dto, emitter);
            } catch (Exception e) {
                if (e instanceof CancellationException) {
                    log.debug("SSE连接被取消: {}", e.getMessage());
                } else {
                    log.error("SSE处理未预期异常", e);
                }
            }
        }, taskExecutor);

        // 注册超时处理
        emitter.onTimeout(() -> {
            if (!future.isDone()) {
                future.cancel(true);  // 中断正在执行的任务
                log.warn("[SSE连接超时] 应用ID: {}, 超时时间: {}ms", dto.getApp().getCode(), timeout);
            }
            emitter.complete();  // 确保资源释放
        });

        // 注册完成回调（客户端主动断开）
        emitter.onCompletion(() -> {
            if (!future.isDone()) {
                future.cancel(true);  // 取消未完成的任务
                log.info("[SSE连接关闭] 应用ID: {}", dto.getApp().getCode());
            }
        });

        return emitter;
    }

    @GetMapping("/stream-chat-v2")
    @Operation(summary = "阿里百炼智能体应用")
    public Flux<String> streamChatV2(HttpServletResponse response) throws NoApiKeyException, InputRequiredException {
        response.setContentType("text/event-stream;charset=UTF-8");
        ApplicationParam param = ApplicationParam.builder()
                .apiKey("sk-def3c5be5bac4d45ab310e4ef4af98ae")
                .appId("f9f8eda8a0b14bdda16cf64e1d29158e")
//                .appId("85f82f4ccbb04252b562bb1fb04155ec")
                .prompt("你好，请问你的知识库文档主要是关于什么内容的?")
                .incrementalOutput(true)
                .build();
        Flux<String> stringFlux = Flux.from(new Application().streamCall(param))
                .flatMap(data -> Flux.just(data.getOutput().getText()))
                .doOnNext(data -> log.info("收到数据: {}", data))
                .publishOn(Schedulers.parallel());

        return stringFlux;
    }

}
