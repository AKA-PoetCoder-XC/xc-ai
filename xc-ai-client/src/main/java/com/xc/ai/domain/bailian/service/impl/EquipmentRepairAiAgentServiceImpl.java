package com.xc.ai.domain.bailian.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.xc.ai.domain.bailian.constant.BaiLianAiAgentCode;
import com.xc.ai.domain.bailian.dto.BaiLianAiAgentChatDTO;
import com.xc.ai.domain.bailian.service.BaiLianAiAgentService;
import com.xc.ai.domain.bailian.util.SseEmitterUtil;
import com.xc.ai.domain.bailian.vo.BaiLianAiAgentChatVO;
import com.xc.ai.properties.ApplicationProperties;
import com.xc.boot.exception.BaseBusinessException;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 设备维修应用接口实现类
 *
 * @author XieChen
 * @since 2025/06/09
 */
@Service(BaiLianAiAgentCode.EQUIPMENT_REPAIR)
public class EquipmentRepairAiAgentServiceImpl implements BaiLianAiAgentService {

    // 日志记录器
    private static final Logger logger = LoggerFactory.getLogger(EquipmentRepairAiAgentServiceImpl.class);
    // 百炼智能体应用编号
    private static final String APP_CODE = BaiLianAiAgentCode.EQUIPMENT_REPAIR;
    // 配置类对象
    private final ApplicationProperties applicationProperties;
    // spring容器环境对象
    private final Environment environment;
    // 百炼智能体应用调用对象
    private final Application application;
    // SSEEmitterUtil对象
    private final SseEmitterUtil sseEmitterUtil;
    // 构造器统一注入实例对象
    public EquipmentRepairAiAgentServiceImpl(
            ApplicationProperties applicationProperties,
            Environment environment,
            Application application,
            SseEmitterUtil sseEmitterUtil

    ) {
        this.applicationProperties = applicationProperties;
        this.environment = environment;
        this.application = application;
        this.sseEmitterUtil = sseEmitterUtil;

        logger.info("EquipmentRepairAiAgentServiceImpl bean init complete");
    }



    /**
     * 普通聊天接口
     *
     * @author XieChen
     * @since 2025/06/09
     */
    @Override
    public BaiLianAiAgentChatVO chat(BaiLianAiAgentChatDTO dto) {
        // TODO 待实现业务逻辑
        if (applicationProperties.getBaiLian() == null
                || applicationProperties.getBaiLian().getCodeMapAppId() == null
                || applicationProperties.getBaiLian().getCodeMapAppId().get(APP_CODE) == null
        ) {
            logger.error("百炼智能体应用appId获取失败!,appCode:{}", APP_CODE);
            throw new BaseBusinessException("系统异常!");
        }
        // 获取设备维修智能体appId
        String appId = applicationProperties.getBaiLian().getCodeMapAppId().get(APP_CODE);
        // 从环境中获取api-kay
        String apiKey = applicationProperties.getBaiLian().getApiKey();

        // 如果没有提供 sessionId，则生成一个新的
        String sessionId = dto.getSessionId();
        if (StrUtil.isBlank(sessionId)) {
            sessionId = UUID.randomUUID().toString().replace("-", "");
        }

        // 构建请求参数
        ApplicationParam param = ApplicationParam.builder()
                .apiKey(apiKey)
                .appId(appId)
                .prompt(dto.getPrompt())
                .sessionId(sessionId)
                .build();
        // 发送请求并获取请求结果
        ApplicationResult result;
        try {
            result = application.call(param);
        } catch (Exception e) {
            logger.error("百炼智能体调用异常,errorMessage:{}", e.getMessage(), e);
            throw new BaseBusinessException("系统异常!");
        }
        return BaiLianAiAgentChatVO.builder()
                .text(result.getOutput().getText())
                .sessionId(sessionId).build();
    }

    /**
     * 流式聊天接口
     *
     * @param dto
     * @author YangShuaiHua
     * @since 2025/06/12
     */
    @Override
    public void streamChat(BaiLianAiAgentChatDTO dto,  SseEmitter emitter) {
        // 使用AtomicBoolean跟踪状态
        AtomicBoolean isCompleted = new AtomicBoolean(false);

        if (applicationProperties.getBaiLian() == null
                || applicationProperties.getBaiLian().getCodeMapAppId() == null
                || applicationProperties.getBaiLian().getCodeMapAppId().get(APP_CODE) == null
        ) {
            logger.error("百炼智能体应用appId获取失败!,appCode:{}", APP_CODE);
            throw new BaseBusinessException("系统异常!");
        }
        // 获取设备维修智能体appId
        String appId = applicationProperties.getBaiLian().getCodeMapAppId().get(APP_CODE);
        // 从环境中获取spring.ai.dashscope.api-kay
        String apiKey = applicationProperties.getBaiLian().getApiKey();

        // 如果没有提供 sessionId，则生成一个新的
        String sessionId = dto.getSessionId();
        if (StrUtil.isBlank(sessionId)) {
            sessionId = UUID.randomUUID().toString().replace("-", "");
        }

        // 3. 创建文本跟踪器
        StringBuilder previousText = new StringBuilder();

        ApplicationParam param = ApplicationParam.builder()
                .apiKey(apiKey)
                .appId(appId)
                .prompt(dto.getPrompt())
                .sessionId(sessionId)
                .hasThoughts(true)
                .build();

        //响应式流处理
        Disposable disposable = sseEmitterUtil.processAiStream(emitter, param, sessionId, isCompleted,previousText);
        // 注册连接状态监听
        sseEmitterUtil.registerConnectionListeners(emitter, disposable, isCompleted);
    }

}
