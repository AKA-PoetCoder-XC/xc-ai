package com.xc.ai.controller;

import com.xc.ai.domain.ollama.service.OllamaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Tag(name = "ollama相关接口")
@RestController
@RequestMapping(("/ollama"))
public class OllamaController {

    @Autowired
    private OllamaService ollamaService;

    @GetMapping("/chat")
    @Operation(summary = "普通聊天")
    public String chat(@RequestParam("msg") String msg) {
        return ollamaService.chat(msg);
    }

    @GetMapping("/stream-chat")
    @Operation(summary = "流式响应聊天聊天")
    public Flux<String> streamChat(@RequestParam("msg") String msg) {
        return ollamaService.streamChat(msg);
    }
}
