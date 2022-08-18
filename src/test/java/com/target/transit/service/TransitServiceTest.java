package com.target.transit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import com.target.transit.exception.PlaceNotFoundException;
import com.target.transit.exception.RouteNotFoundException;
import com.target.transit.service.response.dto.Departures;
import com.target.transit.service.response.dto.Direction;
import com.target.transit.service.response.dto.Place;
import com.target.transit.service.response.dto.Route;
import com.target.transit.service.response.dto.TransitResponse;

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
	public void testGetRoute_ValidRouteLabel() throws Exception {
		when(transitConfig.getRouteApiUrl()).thenReturn("Mock_RouteApiUrl");
		when(restTemplate.exchange(transitConfig.getRouteApiUrl(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Route>>() {
				})).thenReturn(new ResponseEntity(getRouteList(), HttpStatus.OK));
		assertEquals(transitService.getRoute("METRO Blue Line").getRouteId(), "901");
	}

	@Test
	public void testGetRoute_InvalidRouteLabel() throws Exception {
		when(transitConfig.getRouteApiUrl()).thenReturn("Mock_RouteApiUrl");
		when(restTemplate.exchange(transitConfig.getRouteApiUrl(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Route>>() {
				})).thenReturn(new ResponseEntity(getEmptyRouteList(), HttpStatus.OK));
		assertThrows(RouteNotFoundException.class, () -> transitService.getRoute("METRO Black Line"));
	}

	@Test
	public void testGetDirection_ValidRouteIdAndDirectionLabel() throws Exception {
		when(transitConfig.getDirectionApiUrl()).thenReturn("Mock_DirectionApiUrl");
		when(restTemplate.exchange(transitConfig.getDirectionApiUrl() + getRouteList().get(0).getRouteId(),
				HttpMethod.GET, null, new ParameterizedTypeReference<List<Direction>>() {
				})).thenReturn(new ResponseEntity(getDirectionList(), HttpStatus.OK));
		assertEquals(transitService.getDirection("North", getRouteList().get(0).getRouteId()).getDirectionId(),
				getDirectionList().get(0).getDirectionId());
	}

	@Test
	public void testGetDirection_InValidRouteIdAndDirectionLabel() throws Exception {
		when(transitConfig.getDirectionApiUrl()).thenReturn("Mock_DirectionApiUrl");
		when(restTemplate.exchange(transitConfig.getDirectionApiUrl() + 123456789, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Direction>>() {
				})).thenReturn(new ResponseEntity(getEmptyDirectionList(), HttpStatus.OK));
		assertThrows(DirectionNotFoundException.class, () -> transitService.getDirection("West", "123456789"));
	}

	@Test
	public void testGetPlace_ValidRouteIdAndDirectionId() throws Exception {
		when(transitConfig.getPlaceApiUrl()).thenReturn("Mock_PlaceApiUrl");
		when(restTemplate.exchange(
				transitConfig.getPlaceApiUrl() + getRouteList().get(0).getRouteId() + "/"
						+ getDirectionList().get(0).getDirectionId(),
				HttpMethod.GET, null, new ParameterizedTypeReference<List<Place>>() {
				})).thenReturn(new ResponseEntity(getPlaceList(), HttpStatus.OK));
		assertEquals(
				transitService.getPlaceDetails("Target Field Station Platform 2", getRouteList().get(0).getRouteId(),
						getDirectionList().get(0).getDirectionId()).getPlaceCode(),
				getPlaceList().get(0).getPlaceCode());
	}

	@Test
	public void testGetPlace_InValidRouteIdAndDirectionId() throws Exception {
		when(transitConfig.getPlaceApiUrl()).thenReturn("Mock_PlaceApiUrl");
		when(restTemplate.exchange(
				transitConfig.getPlaceApiUrl() + getRouteList().get(0).getRouteId() + "/"
						+ getDirectionList().get(0).getDirectionId(),
				HttpMethod.GET, null, new ParameterizedTypeReference<List<Place>>() {
				})).thenReturn(new ResponseEntity(getEmptyPlaceList(), HttpStatus.OK));
		assertThrows(PlaceNotFoundException.class,
				() -> transitService.getPlaceDetails("Invalid Place Description", getRouteList().get(0).getRouteId(),
						getDirectionList().get(0).getDirectionId()),
				getPlaceList().get(0).getPlaceCode());
	}

	@Test
	public void testGetdepartures_ValidRouteId_DirectionId_PlaceCode() throws Exception {
		when(transitConfig.getNextTripApiUrl()).thenReturn("Mock_NextTrip");
		when(restTemplate.exchange(
				transitConfig.getNextTripApiUrl() + getRouteList().get(0).getRouteId() + "/"
						+ getDirectionList().get(0).getDirectionId() + "/" + getPlaceList().get(0).getPlaceCode(),
				HttpMethod.GET, null, TransitResponse.class))
						.thenReturn(new ResponseEntity(getTransitResponse(), HttpStatus.OK));
		assertNotNull(transitService.getFinalTransitResponse(getRouteList().get(0).getRouteId(),
				getDirectionList().get(0).getDirectionId(), "TF2"));
	}

	private TransitResponse getTransitResponse() {
		TransitResponse transitResponse = new TransitResponse();
		List<Departures> departures = new ArrayList<Departures>();
		departures.add(new Departures());
		transitResponse.setDepartures(departures);
		return transitResponse;
	}

	private List<Place> getPlaceList() {
		List<Place> placeList = new ArrayList<Place>();
		placeList.add(new Place("TF2", "Target Field Station Platform 2"));
		placeList.add(new Place("TF1", "Target Field Station Platform 1"));
		return placeList;
	}

	private List<Route> getRouteList() {
		List<Route> routeList = new ArrayList<Route>();
		routeList.add(new Route("901", "0", "METRO Blue Line"));
		routeList.add(new Route("903", "0", "METRO Red Line"));
		routeList.add(new Route("921", "0", "METRO A Line"));
		return routeList;

	}
	
	private List<Route> getEmptyRouteList() {
		List<Route> routeList = new ArrayList<Route>();
		return routeList;

	}
	
	private List<Route> getEmptyPlaceList() {
		List<Route> placeList = new ArrayList<Route>();
		return placeList;

	}

	private List<Direction> getDirectionList() {
		List<Direction> directionList = new ArrayList<Direction>();
		directionList.add(new Direction(0, "Northbound"));
		directionList.add(new Direction(1, "Southbound"));
		return directionList;
	}
	
	
	private List<Direction> getEmptyDirectionList() {
		List<Direction> directionList = new ArrayList<Direction>();
		return directionList;
	}

}
