package com.xc.ai.properties;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 配置中心配置信息
 * 
 * @author XieChen
 * @date 2024/12/04
 */
@Slf4j
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "xc")
public class ApplicationProperties {

	private Nacos nacos;

	private AliYun aliYun;

	private BaiLian baiLian;

	private SseEmitter sseEmitter;

	@PostConstruct
	private void postConstruct() {
		log.debug("工程配置 ApplicationProperties {}", this.toString());
	}

	@Data
	public static class Nacos {
		private String serverAddr;
		private String namespace;
		private String username;
		private String password;
	}

	@Data
	public static class AliYun {
		private String apiKey;
	}

	/**
	 * 百炼配置信息
	 */
	@Data
	public static class BaiLian {
		private String apiKey; // 百炼apiKey
		// Map<智能体应用编号,百炼应用appId>
		private Map<String, String> codeMapAppId;
	}

	/**
	 * SseEmitter配置信息
	 */
	@Data
	public static class SseEmitter {
		private long timeout;
	}

}