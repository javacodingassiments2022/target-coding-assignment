package com.target.transit.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class RestTemplateCustomErrorHandler implements ResponseErrorHandler {

	private final static Logger logger = LoggerFactory.getLogger(RestTemplateCustomErrorHandler.class);

	@Override
	public boolean hasError(ClientHttpResponse httpResponse) throws IOException {

		if(!httpResponse.getStatusCode().is2xxSuccessful()) {
		
			String body = new BufferedReader(new InputStreamReader(httpResponse.getBody())).lines()
					.collect(Collectors.joining("\n"));
			logger.error("Response from API {} " , body);
			
			return true;
		}
		
		return false;

	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
	  //TODO for now, not doing any custom error handling 

	}

}
