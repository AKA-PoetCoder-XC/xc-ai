package com.xc.ai.domain.bailian.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 百炼智能体BO对象
 *
 * @author XieChen
 * @since 2025/06/09
 */

@Data
@Builder
public class BaiLianAiAgentChatVO {
    @Schema(description = "回复内容")
    private String text;
    @Schema(description = "对话Id")
    private String sessionId;

}
