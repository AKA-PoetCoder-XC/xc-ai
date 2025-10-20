package com.xc.ai.controller;

import com.xc.ai.domain.deepseek.service.DeepSeekService;
import com.xc.ai.domain.qwen.service.QwenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Tag(name = "DeepSeek相关接口")
@RestController
@RequestMapping(("/deep-seek"))
public class DeepSeekController {

    @Autowired
    private DeepSeekService deepSeekService;

    @GetMapping("/chat")
    @Operation(summary = "普通聊天")
    public String chat(@RequestParam("msg") String msg) {
        return deepSeekService.chat(msg);
    }

    @GetMapping("/stream-chat")
    @Operation(summary = "流式响应聊天聊天")
    public Flux<String> streamChat(@RequestParam("msg") String msg) {
        return deepSeekService.streamChat(msg);
    }
}
