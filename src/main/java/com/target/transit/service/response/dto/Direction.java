package com.target.transit.service.response.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class Direction {
	
	@JsonProperty("direction_id")
	private int directionId;
	@JsonProperty("direction_name")
	private String directionName;

}
