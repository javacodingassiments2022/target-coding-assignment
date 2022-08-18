package com.target.transit.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.target.transit.config.TransitConfig;
import com.target.transit.constants.TransitDirection;
import com.target.transit.dto.TransitInfo;
import com.target.transit.exception.DeparturesNotFoundException;
import com.target.transit.exception.DirectionNotFoundException;
import com.target.transit.exception.PlaceNotFoundException;
import com.target.transit.exception.RouteNotFoundException;
import com.target.transit.service.response.dto.Departures;
import com.target.transit.service.response.dto.Direction;
import com.target.transit.service.response.dto.Place;
import com.target.transit.service.response.dto.Route;
import com.target.transit.service.response.dto.TransitResponse;

@Component
public class TransitService {

	private final static Logger logger = LoggerFactory.getLogger(TransitService.class);

	@Autowired
	private TransitConfig transitConfig;

	@Autowired
	private RestTemplate restTemplate;

	public void showNextBusAvailibility(TransitInfo transitInfo) {

		// Step 1 - getRoute info based on Input.
		Route route = null;
		Direction direction = null;
		Place place = null;
		Departures departure = null;

		try {
			route = getRoute(transitInfo.getRouteInfo());
			direction = getDirection(transitInfo.getTransitDirection(), route.getRouteId());
			place = getPlaceDetails(transitInfo.getStopName(), route.getRouteId(), direction.getDirectionId());
			logger.debug("Invoking next trip final api with paramters route = " + route.getRouteId() + " direction = "
					+ direction.getDirectionId() + " place code = " + place.getPlaceCode());
			departure = getFinalTransitResponse(route.getRouteId(), direction.getDirectionId(), place.getPlaceCode());
			logger.info(departure.getDepartureText());

		} catch (RouteNotFoundException e) {
			logger.debug(e.getMessage());
		} catch (DirectionNotFoundException e) {
			logger.debug(e.getMessage());
		} catch (PlaceNotFoundException e) {
			logger.debug(e.getMessage());
		} catch (DeparturesNotFoundException e) {
			logger.debug(e.getMessage());
		}

	}

	Route getRoute(String inputRoute) throws RouteNotFoundException {

		ResponseEntity<List<Route>> response = restTemplate.exchange(transitConfig.getRouteApiUrl(), HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Route>>() {
				});
		List<Route> routeList = response.getBody();
		Route route = routeList.stream().filter(e -> e.getRouteLabel().toLowerCase().contains(inputRoute.toLowerCase()))
				.findFirst().orElse(null);
		if (route == null)
			throw new RouteNotFoundException("Route " + inputRoute + " not found.");
		return route;

	}

	Direction getDirection(String inputDirection, String routeId) throws DirectionNotFoundException {
		ResponseEntity<List<Direction>> response = restTemplate.exchange(transitConfig.getDirectionApiUrl() + routeId,
				HttpMethod.GET, null, new ParameterizedTypeReference<List<Direction>>() {
				});
		List<Direction> directionList = response.getBody();
		Direction direction = directionList.stream()
				.filter(e -> e.getDirectionName()
						.equalsIgnoreCase(TransitDirection.valueOf(inputDirection.toUpperCase()).getValue()))
				.findFirst().orElse(null);
		if (direction == null)
			throw new DirectionNotFoundException(
					" Direction " + inputDirection + " for route " + routeId + " not found");
		return direction;

	}

	Place getPlaceDetails(String inputStopName, String routeId, int directionId) throws PlaceNotFoundException {

		ResponseEntity<List<Place>> response = restTemplate.exchange(
				transitConfig.getPlaceApiUrl() + routeId + "/" + directionId, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Place>>() {
				});
		List<Place> placeList = response.getBody();

		Place place = placeList.stream()
				.filter(e -> e.getDescription().toLowerCase().contains(inputStopName.toLowerCase())).findFirst()
				.orElse(null);
		if (place == null)
			throw new PlaceNotFoundException(" Place with stop " + inputStopName + " route " + routeId + " directionId "
					+ directionId + " directionId");
		return place;

	}

	Departures getFinalTransitResponse(String routeId, int directionId, String placeCode)
			throws DeparturesNotFoundException {
		ResponseEntity<TransitResponse> response = restTemplate.exchange(
				transitConfig.getNextTripApiUrl() + routeId + "/" + directionId + "/" + placeCode, HttpMethod.GET, null,
				TransitResponse.class);
		List<Departures> departureList = response.getBody().getDepartures();
		Departures departure = departureList.stream().findFirst().orElse(null);

		if (departure == null)
			throw new DeparturesNotFoundException(" No Departures found for reouteId " + routeId + "  direction Id "
					+ directionId + " placeCode " + placeCode);

		return departure;

	}

}
