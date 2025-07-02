package com.xc.ai.domain.bailian.service;

import com.xc.ai.domain.bailian.dto.BaiLianAiAgentChatDTO;
import com.xc.ai.domain.bailian.vo.BaiLianAiAgentChatVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 百炼智能体服务接口,每个智能体服务类都需要实现该接口
 *
 * @author XieChen
 * @since 2025/06/09
 */
public interface BaiLianAiAgentService {

    /**
     * 普通聊天接口
     *
     * @author XieChen
     * @since 2025/06/09
     */
    BaiLianAiAgentChatVO chat(BaiLianAiAgentChatDTO dto);

    /**
     * 流式聊天接口
     *
     * @author YangShuaiHua
     * @since 2025/06/12
     */
    void streamChat(BaiLianAiAgentChatDTO dto,  SseEmitter emitter);

}
