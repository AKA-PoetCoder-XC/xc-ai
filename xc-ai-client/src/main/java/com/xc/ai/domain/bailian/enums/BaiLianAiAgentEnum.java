package com.xc.ai.domain.bailian.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.xc.ai.domain.bailian.constant.BaiLianAiAgentCode;
import lombok.Getter;

/**
 * 百炼智能体枚举
 *
 * @author XieChen
 * @since 2025/06/09
 */
@Getter
public enum BaiLianAiAgentEnum {

    EQUIPMENT_REPAIR_CHAT(BaiLianAiAgentCode.EQUIPMENT_REPAIR, "设备维修智能体"),
    ;

    @JsonValue
    private final String code;

    private final String name;

    BaiLianAiAgentEnum(String code, String name) {
        this.code = code;
        this.name = name;

    }
}
