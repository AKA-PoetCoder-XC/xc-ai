package com.xc.ai.domain.bailian.dto;

import com.xc.ai.domain.bailian.enums.BaiLianAiAgentEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 百炼智能体统一抽象出来的入参
 *
 * @author XieChen
 * @since 2025/06/09
 */

@Data
public class BaiLianAiAgentChatDTO {

    @NotNull(message = "应用不能为空")
    @Schema(description = "应用")
    private BaiLianAiAgentEnum app;

    @Schema(description = "用户id")
    private Long userId;

    @NotBlank(message = "用户输入不能为空")
    @Schema(description = "用户输入")
    private String prompt;

    @Schema(description = "会话id,用于关联历史聊天记录")
    private String sessionId;

}
