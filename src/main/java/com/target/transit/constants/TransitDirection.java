package com.target.transit.constants;

public enum TransitDirection {
//TODO handle case sesnitivty and use dir id 

	NORTH("NorthBound"), SOUTH("SouthBound"), EAST("EastBound"), WEST("WestBound"),
	INVALID("Incorrect transit direction");

	private String dirBound;

	TransitDirection(String string) {
		this.dirBound = string;
	}

	public static boolean isValidTransitDirection(String name) {
		for (TransitDirection transitDirection : values()) {
			if (transitDirection.name().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public String getValue() {
		return this.dirBound;
	}

}
