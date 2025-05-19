package com.xc.ai.controller;

import com.xc.ai.service.TongYiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "通义相关接口")
@RestController
@RequestMapping(("/tong-yi"))
public class TongYiAiController {

    @Autowired
    private TongYiService tongYiService;

    @GetMapping("/chat")
    @Operation(summary = "普通聊天")
    public String chat(@RequestParam("msg") String msg) {
        return tongYiService.chat(msg);
    }
}
