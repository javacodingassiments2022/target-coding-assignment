package com.target.transit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.target.transit.config.TransitConfig;
import com.target.transit.exception.DirectionNotFoundException;
import com.target.transit.exception.RouteNotFoundException;
import com.target.transit.service.response.dto.Direction;
import com.target.transit.service.response.dto.Route;


@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransitServiceTest {
	
	@InjectMocks
	private TransitService transitService;
	
	
	@Mock
	TransitConfig transitConfig;

	@Mock
	RestTemplate restTemplate;
	
	
	@BeforeEach
	public void intitTransitService() {
		transitService.restTemplate = restTemplate;
		transitService.transitConfig = transitConfig;
	}
	
	@Test
	public void testGetRoute_ValidRouteLabel() throws Exception{
		when(transitConfig.getRouteApiUrl()).thenReturn("Mock_RouteApiUrl");
		when(restTemplate.exchange(transitConfig.getRouteApiUrl(), HttpMethod.GET, null, new ParameterizedTypeReference<List<Route>>(){})).
		thenReturn(new ResponseEntity(getRouteList(), HttpStatus.OK));
		assertEquals(transitService.getRoute("METRO Blue Line").getRouteId(),"901");
	}


	@Test
	public void testGetRoute_InvalidRouteLabel() throws Exception{
		when(transitConfig.getRouteApiUrl()).thenReturn("Mock_RouteApiUrl");
		when(restTemplate.exchange(transitConfig.getRouteApiUrl(), HttpMethod.GET, null, new ParameterizedTypeReference<List<Route>>(){})).
		thenReturn(new ResponseEntity(getRouteList(), HttpStatus.OK));
		assertThrows(RouteNotFoundException.class, ()->transitService.getRoute("METRO Black Line"));
	}

	@Test
	public void testGetDirection_ValidRouteAndDirection() throws Exception{
		when(transitConfig.getDirectionApiUrl()).thenReturn("Mock_DirectionApiUrl");
		when(restTemplate.exchange(transitConfig.getDirectionApiUrl()+ getRouteList().get(0).getRouteId(), HttpMethod.GET, null, new ParameterizedTypeReference<List<Direction>>(){})).
		thenReturn(new ResponseEntity(getDirectionList(), HttpStatus.OK));
		assertEquals(transitService.getDirection("North" ,getRouteList().get(0).getRouteId()).getDirectionId(),getDirectionList().get(0).getDirectionId());
	}
	
	@Test
	public void testGetDirection_InValidRouteAndDirection() throws Exception{
		when(transitConfig.getDirectionApiUrl()).thenReturn("Mock_DirectionApiUrl");
		when(restTemplate.exchange(transitConfig.getDirectionApiUrl() + 123456789, HttpMethod.GET, null, new ParameterizedTypeReference<List<Direction>>(){})).
		thenReturn(new ResponseEntity(getDirectionList(), HttpStatus.OK));
		assertThrows(DirectionNotFoundException.class,()->transitService.getDirection("West" , "123456789"));
	}
	
	
	
	private List<Route> getRouteList() {
		List<Route> routeList = new ArrayList<Route>();
		routeList.add(new Route("901","0","METRO Blue Line"));
		routeList.add(new Route("903","0","METRO Red Line"));
		routeList.add(new Route("921","0","METRO A Line"));
		return routeList;
		
	}
	
	private List<Direction> getDirectionList() {
		List<Direction> directionList = new ArrayList<Direction>();
		directionList.add(new Direction(0, "Northbound"));
		directionList.add(new Direction(1, "Southbound"));
		return directionList;
	}
	
	private List<Direction> getPlaceList() {
		List<Direction> directionList = new ArrayList<Direction>();
		directionList.add(new Direction(0, "Northbound"));
		directionList.add(new Direction(1, "Southbound"));
		return directionList;
	}
	
}
