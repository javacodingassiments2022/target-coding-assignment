package com.target.transit.service.response.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class Route {
	@JsonProperty("route_id")
	private String routeId;
	@JsonProperty("agency_id")
	private String agencyId;
	@JsonProperty("route_label")
	private String routeLabel;

}
