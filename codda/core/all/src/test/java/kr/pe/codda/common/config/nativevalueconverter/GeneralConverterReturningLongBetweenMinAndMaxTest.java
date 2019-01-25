package kr.pe.codda.common.config.nativevalueconverter;

import static org.junit.Assert.*;
import junitlib.AbstractJunitTest;

import org.junit.Test;

public class GeneralConverterReturningLongBetweenMinAndMaxTest extends AbstractJunitTest {

	@Test(expected=IllegalArgumentException.class)
	public void testValueOf_NullParamter_itemValue() throws Exception {
		GeneralConverterReturningLongBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningLongBetweenMinAndMax((long)10, (long)20);
		try {
			minMaxConverter.valueOf(null);
		} catch(IllegalArgumentException e) {
			log.info("null paramter", e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testValueOf_EmptyString() throws Exception {
		GeneralConverterReturningLongBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningLongBetweenMinAndMax((long)10, (long)20);
		try {
			minMaxConverter.valueOf("");
		} catch(IllegalArgumentException e) {
			log.info("empty string paramter", e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testValueOf_ValidButBadParameter_NotNumber() throws Exception {
		GeneralConverterReturningLongBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningLongBetweenMinAndMax((long)10, (long)20);
		try {
			minMaxConverter.valueOf("a");
		} catch(IllegalArgumentException e) {
			log.info("not number", e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testValueOf_ValidButBadParameter_TooBig() throws Exception {
		GeneralConverterReturningLongBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningLongBetweenMinAndMax((long)10, (long)20);
		
		
		try {
			minMaxConverter.valueOf("12345");
		} catch(IllegalArgumentException e) {
			log.info("bigger than max of byte", e);
			throw e;
		}
	}
	
	
	@Test
	public void testValueOf_ExpectedValueComparison() {
		GeneralConverterReturningLongBetweenMinAndMax minMaxConverter = 
				new GeneralConverterReturningLongBetweenMinAndMax((long)10, Long.MAX_VALUE);
		
		long expectedValue;
		long returnedValue;		
		
		expectedValue = 1234343434343333333L;
		returnedValue = minMaxConverter.valueOf(String.valueOf(expectedValue));
		
		assertEquals("the expected value comparison", returnedValue, expectedValue);		
	}

}
