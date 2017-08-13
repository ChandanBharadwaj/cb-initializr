package com.cb.init;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author Chandan
 *
 */
@Configuration
@EnableCaching
public class InitConfig {

	@Bean
	RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
}
