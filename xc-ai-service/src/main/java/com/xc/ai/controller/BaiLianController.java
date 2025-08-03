package com.xc.ai.controller;

import com.xc.ai.domain.bailian.dto.BaiLianAiAgentChatDTO;
import com.xc.ai.domain.bailian.service.BaiLianAiAgentService;
import com.xc.ai.domain.bailian.vo.BaiLianAiAgentChatVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import java.util.Map;

@Tag(name = "百炼相关接口")
@RestController
@RequestMapping("/bai-lian")
public class BaiLianController {

    // 日志记录器
    private static final Logger log = LoggerFactory.getLogger(BaiLianController.class);
    // Map<智能体应用code, 所有百炼平台智能体服务实例>
    private final Map<String, BaiLianAiAgentService> codeMapAiAgentService;
    // 构造器统一注入实例对象
    public BaiLianController(Map<String, BaiLianAiAgentService> codeMapAiAgentService) {
        this.codeMapAiAgentService = codeMapAiAgentService;
    }

    @PostMapping("/stream-chat")
    @Operation(summary = "阿里百炼智能体应用")
    public Flux<BaiLianAiAgentChatVO> streamChat(@RequestBody BaiLianAiAgentChatDTO dto) {
        BaiLianAiAgentService baiLianAiAgentService = codeMapAiAgentService.get(dto.getApp().getCode());
        return baiLianAiAgentService.streamChat(dto);
    }

}
