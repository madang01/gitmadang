package kr.pe.codda.common.config.nativevalueconverter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import junitlib.AbstractJunitTest;

public class GeneralConverterReturningIntegerBetweenMinAndMaxTest extends AbstractJunitTest {
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testValueOf_NullParamter_itemValue() throws Exception {
		GeneralConverterReturningIntegerBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningIntegerBetweenMinAndMax((int)10, (int)20);
		try {
			minMaxConverter.valueOf(null);
		} catch(IllegalArgumentException e) {
			log.info("null paramter", e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testValueOf_EmptyString() throws Exception {
		GeneralConverterReturningIntegerBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningIntegerBetweenMinAndMax((int)10, (int)20);
		try {
			minMaxConverter.valueOf("");
		} catch(IllegalArgumentException e) {
			log.info("empty string paramter", e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testValueOf_ValidButBadParameter_NotNumber() throws Exception {
		GeneralConverterReturningIntegerBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningIntegerBetweenMinAndMax((int)10, (int)20);
		try {
			minMaxConverter.valueOf("a");
		} catch(IllegalArgumentException e) {
			log.info("not number", e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testValueOf_ValidButBadParameter_TooBig() throws Exception {
		GeneralConverterReturningIntegerBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningIntegerBetweenMinAndMax((int)10, (int)20);
		
		
		try {
			minMaxConverter.valueOf("12345");
		} catch(IllegalArgumentException e) {
			log.info("bigger than max of byte", e);
			throw e;
		}
	}
	
	
	@Test
	public void testValueOf_ExpectedValueComparison() {
		GeneralConverterReturningIntegerBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningIntegerBetweenMinAndMax((int)10, (int)20);
		
		int expectedValue;
		int returnedValue;
		
		expectedValue = 12;
		returnedValue = minMaxConverter.valueOf(String.valueOf(expectedValue));
		
		assertEquals("the expected value comparison", returnedValue, expectedValue);		
	}
}
