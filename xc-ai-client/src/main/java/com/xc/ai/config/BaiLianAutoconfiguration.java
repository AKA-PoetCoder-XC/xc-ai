
package com.xc.ai.config;

import com.alibaba.dashscope.app.Application;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
public class BaiLianAutoconfiguration {

//	/**
//	 * 百炼调用时需要配置 DashScope API，对 dashScopeApi 强依赖。
//	 *
//	 * @return
//	 */
//	@Bean
//	public DashScopeAgentApi dashScopeApi(Environment environment) {
//		return new DashScopeAgentApi(environment.getProperty("spring.ai.dashscope.api-key"));
//	}

	@Bean
	Application application() {
		return new Application();
	}


	@Bean(name = "sseTaskExecutor")
	public ThreadPoolTaskExecutor  taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(Runtime.getRuntime().availableProcessors() + 1);
		taskExecutor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2);
		taskExecutor.setQueueCapacity(1000);
		taskExecutor.setThreadNamePrefix("sse-exec-");
		taskExecutor.initialize();
		return taskExecutor;
	}

}