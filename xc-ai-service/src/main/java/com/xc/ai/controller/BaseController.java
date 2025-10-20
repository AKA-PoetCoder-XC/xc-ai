package com.xc.ai.controller;

import com.alibaba.fastjson2.JSON;
import com.xc.ai.properties.ApplicationProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "基本控制器")
@RestController
@RequestMapping(("/base"))
public class BaseController {

    private final ApplicationProperties applicationProperties;

    public BaseController(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @GetMapping("/info")
    @Operation(summary = "服务基本信息")
    public String serviceInfo(@RequestParam(value = "key", required = false) String key) {
        return "xiechen".equals(key) ? String.format("Hello,this is xc-ai-service controller, remote-config:%s", JSON.toJSONString(applicationProperties)) : "无权限!";
    }

}
