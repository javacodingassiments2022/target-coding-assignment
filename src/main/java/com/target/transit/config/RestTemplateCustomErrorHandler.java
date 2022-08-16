package com.target.transit.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.target.transit.exception.TransitException;






public class RestTemplateCustomErrorHandler implements ResponseErrorHandler  {
	
	private final static Logger logger = LoggerFactory.getLogger(RestTemplateCustomErrorHandler.class);
	
	@Override
    public boolean hasError(ClientHttpResponse httpResponse) 
      throws IOException {

		  if (!httpResponse.getStatusCode().is2xxSuccessful()) {
			String  responseAsString = toString(httpResponse.getBody());
			    logger.error("ResponseBody : {}", httpResponse.getStatusCode());
			    throw new TransitException(responseAsString);
		  }
		return false;
	   
	    
	   
    }

	String toString(InputStream inputStream) {
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
	
    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) 
      throws IOException {
    	String responseAsString = toString(response.getBody());
        logger.error("URL: {}, HttpMethod: {}, ResponseBody: {}", url, method, responseAsString);

        throw new TransitException(responseAsString);
    
    }
    
    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        String responseAsString = toString(clientHttpResponse.getBody());
        logger.error("ResponseBody: 456 {}", responseAsString);

        throw new TransitException(responseAsString);
    }

}
