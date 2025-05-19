package com.xc.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI控制器")
@RestController
@RequestMapping(("/ai"))
public class AiController {

    @GetMapping("/service-info")
    @Operation(summary = "服务基本信息")
    public String serviceInfo() {
        return "Hello,this is xc-ai-service controller";
    }


}
