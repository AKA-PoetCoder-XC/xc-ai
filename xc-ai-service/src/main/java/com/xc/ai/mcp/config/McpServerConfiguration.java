package com.xc.ai.mcp.config;

import com.xc.ai.mcp.service.TimeService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfiguration {

    @Bean
    public ToolCallbackProvider timeTools(TimeService timeService) {
        return MethodToolCallbackProvider.builder().toolObjects(timeService).build();
    }
}
