package com.target.transit.service.response.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class Departures {
	
	private boolean actual;
	@JsonProperty("departure_text")
	private String departureText;
}
