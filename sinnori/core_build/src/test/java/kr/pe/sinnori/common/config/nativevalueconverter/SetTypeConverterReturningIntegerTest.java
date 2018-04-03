package kr.pe.sinnori.common.config.nativevalueconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.config.NativeValueConverterTestIF;

public class SetTypeConverterReturningIntegerTest extends AbstractJunitTest implements NativeValueConverterTestIF {
	private SetTypeConverterReturningInteger nativeValueConverter = null;
	private final Integer testIntegerSet[] = {123, 43};
	private Integer returnedValue = null;
	
	
	@Override
	@Before
	public void setup() {
		try {
			nativeValueConverter = new SetTypeConverterReturningInteger(testIntegerSet[0].toString(), 
					testIntegerSet[1].toString());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		returnedValue = null;
	}
	
	@Override
	public void testConstructor() throws Exception {
		/** ignore */
	}

	
	@Override
	@Test
	public void testToNativeValue_ExpectedValueComparison() {
		
		Integer expectedValue = testIntegerSet[0];
		
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue.toString());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		/*if (wantedValue != returnedValue) {
			fail(String.format("원하는 결과[%d]와 얻은 결과[%d]가 다름", wantedValue, returnedValue));
		}*/
		assertEquals("the expected value comparison", returnedValue, expectedValue);
	}	
	
	@Override
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_NullParameter() throws Exception {		
		try {
			returnedValue = nativeValueConverter.valueOf(null);
			
			fail("paramerter itemValue is null but no error");
		} catch (IllegalArgumentException e) {
			log.info("'the parameter itemvalue is null' test ok, errormessage={}", e.getMessage());
			throw e;
		}
	}
		
	@Override
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_EmptyStringParameter() throws Exception {		
		try {
			returnedValue = nativeValueConverter.valueOf("");
			
			fail("paramerter itemValue is a empty string but no error");
		} catch (IllegalArgumentException e) {
			log.info("'the parameter itemvalue is a empty string' test ok, errormessage={}", e.getMessage());
			throw e;
		}
	}
	
	@Override
	public void testToNativeValue_ValidButBadParameter() throws Exception {		
		try {
			testToNativeValue_ValidButBadParameter_NotInteger_case1();
		} catch(IllegalArgumentException e) {
		}
		try {
			testToNativeValue_ValidButBadParameter_NotInteger_case2();
		} catch(IllegalArgumentException e) {
		}		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_ValidButBadParameter_NotInteger_case1() throws Exception {
		try {
			returnedValue = nativeValueConverter.valueOf("aabc");
			
			fail("paramerter itemValue[aabc] is bad but no error, returnedValue="
					+ returnedValue.toString());
		} catch (IllegalArgumentException e) {
			log.info("'the parameter itemvalue is valid but bad' test ok, errormessage={}", e.getMessage());
			throw e;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_ValidButBadParameter_NotInteger_case2() throws Exception {
		try {
			returnedValue = nativeValueConverter.valueOf("123 ");
			
			fail("paramerter itemValue[123 ] is bad but no error, returnedValue="
			+ returnedValue.toString());
		} catch (IllegalArgumentException e) {
			log.info("'the parameter itemvalue is valid but bad' test ok, errormessage={}", e.getMessage());
			throw e;
		}
	}
	
	/*
	@After
	public void end123() {
		if (null == returnedValue) {
			log.info("returnedValue is null");
		} else {
			log.info("returnedValue=[{}]", returnedValue.toString());
			itemGetter = null;
		}
		
	}*/
}
