package com.target.transit.validator;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.target.transit.constants.TransitApplicationConstants;
import com.target.transit.constants.TransitDirection;


@Component
public class TransitValidator {
	
	private final static Logger logger = LoggerFactory.getLogger(TransitValidator.class);

	public boolean isInputValid(String[] args){
		
		if (args.length != 3) {
			logger.error(TransitApplicationConstants.INVALID_ARGS_LENGTH);
			return false;
		}
		// Read the args
		Optional<String> routeOptional = Optional.ofNullable(args[0]);
		if (routeOptional.isEmpty()) {
			logger.error(TransitApplicationConstants.INVALID_ROUTE);
			return false;
		}

		Optional<String> stopOptional = Optional.ofNullable(args[1]);
		if (stopOptional.isEmpty()) {
			logger.error(TransitApplicationConstants.INVALID_STOP);
			return false;
		}
		Optional<String> directionOptional = Optional.ofNullable(args[2]);
		if (stopOptional.isEmpty() || !TransitDirection.isValidTransitDirection(directionOptional.get())) {
			logger.error(TransitApplicationConstants.INVALID_DIRECTION);
			return false;
		}
		return true;
		
	}
	
}
