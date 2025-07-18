package com.xc.ai.domain.bailian.service.impl;

import com.alibaba.cloud.ai.agent.Agent;
import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgent;
import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentFlowStreamMode;
import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentOptions;
import com.alibaba.cloud.ai.dashscope.api.DashScopeAgentApi;
import com.alibaba.fastjson2.JSONObject;
import com.xc.ai.domain.bailian.constant.BaiLianAiAgentCode;
import com.xc.ai.domain.bailian.convert.BaiLianAiAgentConvert;
import com.xc.ai.domain.bailian.dto.BaiLianAiAgentChatDTO;
import com.xc.ai.domain.bailian.service.BaiLianAiAgentService;
import com.xc.ai.domain.bailian.vo.BaiLianAiAgentChatVO;
import com.xc.ai.properties.ApplicationProperties;
import com.xc.boot.exception.BaseBusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

/**
 * 设备维修应用接口实现类
 *
 * @author XieChen
 * @since 2025/06/09
 */
@Service(BaiLianAiAgentCode.EQUIPMENT_REPAIR)
public class EquipmentRepairAiAgentServiceImpl implements BaiLianAiAgentService {

    private static final String SERVICE_NAME = "设备维修智能体";

    // 日志记录器
    private static final Logger logger = LoggerFactory.getLogger(EquipmentRepairAiAgentServiceImpl.class);
    // 百炼智能体应用编号
    private static final String APP_CODE = BaiLianAiAgentCode.EQUIPMENT_REPAIR;
    // 配置类对象
    private final ApplicationProperties applicationProperties;
    // spring容器环境对象
    private final Environment environment;
    // 百炼智能体应用调用对象
    private final Agent agent;
    // 百炼智能体应用调用对象
    // 构造器统一注入实例对象
    public EquipmentRepairAiAgentServiceImpl(
            ApplicationProperties applicationProperties,
            Environment environment,
            DashScopeAgentApi dashScopeAgentApi
    ) {
        this.applicationProperties = applicationProperties;
        this.environment = environment;
        this.agent = new DashScopeAgent(dashScopeAgentApi);

        logger.info("EquipmentRepairAiAgentServiceImpl bean init complete");
    }


    @Override
    public BaiLianAiAgentChatVO chat(BaiLianAiAgentChatDTO dto) {
        return null;
    }

    /**
     * 流式聊天接口
     *
     * @author XieChen
     * @since 2025/07/09
     */
    @Override
    public Flux<BaiLianAiAgentChatVO> streamChat(BaiLianAiAgentChatDTO dto) {
        if (applicationProperties.getBaiLian() == null
                || applicationProperties.getBaiLian().getCodeMapAppId() == null
                || applicationProperties.getBaiLian().getCodeMapAppId().get(APP_CODE) == null
        ) {
            logger.error("[{}]应用appId获取失败!,appCode:{}", SERVICE_NAME, APP_CODE);
            throw new BaseBusinessException("[{}]系统异常!", SERVICE_NAME);
        }
        // 获取设备维修智能体appId
        String appId = applicationProperties.getBaiLian().getCodeMapAppId().get(APP_CODE);
        Prompt prompt = new Prompt(
                dto.getPrompt(),
                DashScopeAgentOptions.builder()
                        .withAppId(appId) // 设置智能体应用id
                        .withSessionId(dto.getSessionId())
                        .withIncrementalOutput(true)
                        .withFlowStreamMode(DashScopeAgentFlowStreamMode.AGENT_FORMAT)
                        .build()
        );

        logger.info("[{}]请求参数:{}", SERVICE_NAME, prompt);

        return agent.stream(prompt)
                .map(response -> {
                    logger.info("[{}]响应结果:{}", SERVICE_NAME, JSONObject.toJSONString(response));
                    BaiLianAiAgentChatVO vo;
                    Object outputObj = response.getResult().getOutput().getMetadata().get("output");
                    if (outputObj instanceof DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput output) {
                        vo = BaiLianAiAgentConvert.INSTANCE.convert(output);
                    } else {
                        throw new BaseBusinessException("百炼智能体应用失败!");
                    }
                    return vo;
                });
    }

}
