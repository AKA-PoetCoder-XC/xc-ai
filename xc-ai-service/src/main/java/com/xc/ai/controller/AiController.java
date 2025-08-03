package com.xc.ai.controller;

import com.xc.ai.properties.ApplicationProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI控制器")
@RestController
@RequestMapping(("/ai"))
public class AiController {

    private final ApplicationProperties applicationProperties;

    public AiController(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @GetMapping("/service-info")
    @Operation(summary = "服务基本信息")
    public String serviceInfo() {
        return String.format("Hello,this is xc-ai-service controller, remote-config-name:%s", applicationProperties.getRemoteConfigName());
    }


}
