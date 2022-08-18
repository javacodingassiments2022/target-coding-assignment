package com.target.transit.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TransitValidatorTest {

	
	@Test
	public void testIsInputValid_InvalidNumberOfParams() {
		TransitValidator transitValidator = new TransitValidator();
		String[] inputParams = {"Route1", "STOP1"};
		assertFalse(transitValidator.isInputValid(inputParams));
		
	}
	
	@Test
	public void testIsValidInput_NullParams() {
		TransitValidator transitValidator = new TransitValidator();
		String[] inputParams = {"Route1", null, "EAST"};
		assertFalse(transitValidator.isInputValid(inputParams));
	}
	
	@Test
	public void testIsValidInput_EmptyParams() {
		TransitValidator transitValidator = new TransitValidator();
		String[] inputParams = {"Route1", "STOP1", ""};
		assertFalse(transitValidator.isInputValid(inputParams));
	}
	
	@Test
	public void testValidInput() {
		TransitValidator transitValidator = new TransitValidator();
		String[] inputParams = {"Route1", "STOP1", "EAST"};
		assertTrue(transitValidator.isInputValid(inputParams));
	}
}
