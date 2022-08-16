//package com.target.transit.service;
//
//import static org.mockito.Mockito.*;
//
//import java.util.List;
//
//import org.hamcrest.Matchers;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentMatchers;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//
//import com.target.transit.config.TransitConfig;
//import com.target.transit.service.response.dto.Route;
//
//
//@ExtendWith(MockitoExtension.class)
//public class TransitServiceTest {
//	
//	@Mock
//    private RestTemplate restTemplate;
//	
//	@Mock
//	private TransitConfig transitConfig;
//		
//	 @InjectMocks
//	 private TransitService transitService = new TransitService();
//
//	 @Test
//	    public void testGetObjectAList() {
//	        Route route = new Route("1","1","1");
//	        //define the entity you want the exchange to return
//	        
//	      //  ResponseEntity<String> responseEntity = new ResponseEntity<String>("sampleBodyString", HttpStatus.ACCEPTED);
//
//
//	        
//	      ResponseEntity<List<Route>> myEntity = new ResponseEntity<List<Route>>(HttpStatus.ACCEPTED);
//	//      myEntity.getBody().add(route);
////	        Mockito.when(restTemplate.exchange(	
////	        		ArgumentMatchers.anyString(),
////	                ArgumentMatchers.any(HttpMethod.class),
////	                ArgumentMatchers.any(),
////	                ArgumentMatchers.<Class<String>>any()))
////	            .thenReturn(responseEntity);
//	        
//	      ParameterizedTypeReference<List<Route>>  parameterizedTypeReference = 
//	    		  new ParameterizedTypeReference<List<Route>>(){};
//
//	        
//	       // ResponseEntity<Route> routeResponseEntity = new ResponseEntity<>(route, HttpStatus.OK);
//	        
////	        ResponseEntity<List<Route>> response = restTemplate.exchange(transitConfig.getRouteApiUrl(), HttpMethod.GET,
////					null, new ParameterizedTypeReference<List<Route>>() {
////					});
////	        
//	        Mockito.when(restTemplate.exchange(Mockito.anyString()
//	                      , Mockito.eq(HttpMethod.GET)
//	                      , null
//	                      , any(parameterizedTypeReference)
//	         )).thenReturn(myEntity);
//	        
//	       
//
//	       Route res = transitService.getRoute("1");
//	       // Assert.assertEquals(myobjectA, res.get(0));
//	    }
//	 
//	
//	
//}
