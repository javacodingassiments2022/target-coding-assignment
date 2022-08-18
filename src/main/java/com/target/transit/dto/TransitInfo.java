package com.target.transit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransitInfo {

	private String routeInfo;
	private String stopName;
	private String transitDirection;

}
