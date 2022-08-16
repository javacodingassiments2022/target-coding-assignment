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
import com.target.transit.constants.TransitApplicationConstants;
import com.target.transit.constants.TransitDirection;
import com.target.transit.dto.TransitInfo;
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
		Route route = getRoute(transitInfo.getRouteInfo());
		if (route != null) {
			// Step 2 - get direction based on the route.
			Direction direction = getDirection(transitInfo.getTransitDirection(), route.getRouteId());
			if (direction != null) {
				// step 3 - get place code based on routeId and directionId.
				Place place = getPlaceDetails(transitInfo.getStopName(), route.getRouteId(),
						direction.getDirectionId());

				if (place != null) {
					logger.debug("Invoking next trip final api with paramters route = " + route.getRouteId()
							+ " direction = " + direction.getDirectionId() + " place code = " + place.getPlaceCode());
					Departures departure = getFinalTransitResponse(route.getRouteId(), direction.getDirectionId(),
							place.getPlaceCode());

					if (departure != null) {
						// If not null , display the minutes otherwise dont do anything
						logger.info(departure.getDepartureText()+" " + TransitApplicationConstants.MINUTES);
					}

				} else {
					logger.debug("No place code found for " + route.getRouteId() + " and direction "
							+ direction.getDirectionId());
				}

			} else {
				logger.debug("No transit directions found for route id " + route.getRouteId());
			}

		} else {
			logger.debug("Route not present, please enter valid route name");
		}

	}

	Route getRoute(String inputRoute) {

		ResponseEntity<List<Route>> response = restTemplate.exchange(transitConfig.getRouteApiUrl(), HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Route>>() {
				});
		List<Route> routeList = response.getBody();
		Route route = routeList.stream().filter(e -> e.getRouteLabel().toLowerCase().contains(inputRoute.toLowerCase()))
				.findFirst().orElse(null);

		return route;

	}

	Direction getDirection(String inputDirection, String routeId) {
		ResponseEntity<List<Direction>> response = restTemplate.exchange(transitConfig.getDirectionApiUrl() + routeId,
				HttpMethod.GET, null, new ParameterizedTypeReference<List<Direction>>() {
				});
		List<Direction> directionList = response.getBody();
		Direction direction = directionList.stream()
				.filter(e -> e.getDirectionName()
						.equalsIgnoreCase(TransitDirection.valueOf(inputDirection.toUpperCase()).getValue()))
				.findFirst().orElse(null);
		return direction;

	}

	Place getPlaceDetails(String inputStopName, String routeId, int directionId) {

		ResponseEntity<List<Place>> response = restTemplate.exchange(
				transitConfig.getPlaceApiUrl() + routeId + "/" + directionId, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Place>>() {
				});
		List<Place> placeList = response.getBody();

		Place place = placeList.stream()
				.filter(e -> e.getDescription().toLowerCase().contains(inputStopName.toLowerCase())).findFirst()
				.orElse(null);
		return place;

	}

	Departures getFinalTransitResponse(String routeId, int directionId, String placeCode) {
		ResponseEntity<TransitResponse> response = restTemplate.exchange(
				transitConfig.getNextTripApiUrl() + routeId + "/" + directionId + "/" + placeCode, HttpMethod.GET, null,
				TransitResponse.class);
		List<Departures> departureList = response.getBody().getDepartures();
		Departures depature = departureList.stream().findFirst().orElse(null);
		return depature;

	}

}
