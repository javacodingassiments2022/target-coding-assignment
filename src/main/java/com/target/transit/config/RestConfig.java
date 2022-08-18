package com.target.transit.config;
import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.target.transit.handler.RestTemplateCustomErrorHandler;

@Configuration
public class RestConfig {
	
	@Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
		 RestTemplate restTemplate = builder.setConnectTimeout(Duration.ofSeconds(3))
	                .setReadTimeout(Duration.ofSeconds(3)).errorHandler(new RestTemplateCustomErrorHandler())
		          .build();
		return restTemplate;
    }


}
