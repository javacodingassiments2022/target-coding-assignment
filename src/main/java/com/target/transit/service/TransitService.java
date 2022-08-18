package com.target.transit.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.target.transit.config.TransitConfig;
import com.target.transit.constants.TransitApplicationConstants;
import com.target.transit.constants.TransitDirection;
import com.target.transit.dto.TransitInfo;

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
	TransitConfig transitConfig;

	@Autowired
	RestTemplate restTemplate;

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
			// logger.info("Invoking next trip final api with paramters route = " +
			// route.getRouteId() + " direction = "
			// + direction.getDirectionId() + " place code = " + place.getPlaceCode());
			departure = getFinalTransitResponse(route.getRouteId(), direction.getDirectionId(), place.getPlaceCode());

			if (departure != null) {
				Instant instant = Instant.now();
				Long longTime = Long.valueOf(departure.getDepartureTime()).longValue();
				long nextArrival = TimeUnit.MILLISECONDS.toMinutes(longTime * 1000 - instant.toEpochMilli());
				logger.info(nextArrival + " " + TransitApplicationConstants.MINUTES);
			}

		} catch (RouteNotFoundException e) {
			logger.info(e.getMessage());
		} catch (DirectionNotFoundException e) {
			logger.info(e.getMessage());
		} catch (PlaceNotFoundException e) {
			logger.info(e.getMessage());
		}

	}

	Route getRoute(String inputRoute) throws RouteNotFoundException {

		Route route = null;
		ResponseEntity<List<Route>> response = restTemplate.exchange(transitConfig.getRouteApiUrl(), HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Route>>() {
				});
		List<Route> routeList = response.getBody();
		if (routeList != null && !routeList.isEmpty())
			route = routeList.stream().filter(e -> e.getRouteLabel().trim().toLowerCase().contains(inputRoute.trim().toLowerCase()))
					.findFirst().orElseThrow(() -> new RouteNotFoundException("Route " + inputRoute + " not found."));

		return route;

	}

	Direction getDirection(String inputDirection, String routeId) throws DirectionNotFoundException {
		ResponseEntity<List<Direction>> response = restTemplate.exchange(transitConfig.getDirectionApiUrl() + routeId,
				HttpMethod.GET, null, new ParameterizedTypeReference<List<Direction>>() {
				});
		Direction direction = null;
		List<Direction> directionList = response.getBody();
		if (directionList != null && !directionList.isEmpty())
			direction = directionList.stream()
					.filter(e -> e.getDirectionName()
							.equalsIgnoreCase(TransitDirection.valueOf(inputDirection.trim().toUpperCase()).getValue()))
					.findFirst().orElseThrow(() -> new DirectionNotFoundException(
							" Direction " + inputDirection + " for route " + routeId + " not found"));
		return direction;

	}

	Place getPlaceDetails(String inputStopName, String routeId, int directionId) throws PlaceNotFoundException {
		Place place = null;
		ResponseEntity<List<Place>> response = restTemplate.exchange(
				transitConfig.getPlaceApiUrl() + routeId + "/" + directionId, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Place>>() {
				});
		List<Place> placeList = response.getBody();
		if (placeList != null && !placeList.isEmpty())
			place = placeList.stream()
					.filter(e -> e.getDescription().trim().toLowerCase().contains(inputStopName.trim().toLowerCase())).findFirst()
					.orElseThrow(() -> new PlaceNotFoundException("Place code for " + inputStopName + " route "
							+ routeId + " directionId " + directionId + " not found"));

		return place;

	}

	Departures getFinalTransitResponse(String routeId, int directionId, String placeCode) {
		ResponseEntity<TransitResponse> response = restTemplate.exchange(
				transitConfig.getNextTripApiUrl() + routeId + "/" + directionId + "/" + placeCode, HttpMethod.GET, null,
				TransitResponse.class);
		Departures departure = null;
		if (response != null) {
			List<Departures> departureList = response.getBody().getDepartures();
			departure = departureList.stream().findFirst().orElse(null);
		}
		return departure;

	}

}
