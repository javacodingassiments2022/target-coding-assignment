package com.target.transit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties
@Data
public class TransitConfig {
	
	private String routeApiUrl;
	private String directionApiUrl;
	private String placeApiUrl;
	private String nextTripApiUrl;

}
