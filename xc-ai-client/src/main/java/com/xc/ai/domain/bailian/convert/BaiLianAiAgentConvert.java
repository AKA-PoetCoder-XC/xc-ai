package com.xc.ai.domain.bailian.convert;

import com.alibaba.cloud.ai.dashscope.api.DashScopeAgentApi;
import com.xc.ai.domain.bailian.vo.BaiLianAiAgentChatVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BaiLianAiAgentConvert {

    BaiLianAiAgentConvert INSTANCE = Mappers.getMapper(BaiLianAiAgentConvert.class);

    BaiLianAiAgentChatVO convert(DashScopeAgentApi.DashScopeAgentResponse.DashScopeAgentResponseOutput output);

}
