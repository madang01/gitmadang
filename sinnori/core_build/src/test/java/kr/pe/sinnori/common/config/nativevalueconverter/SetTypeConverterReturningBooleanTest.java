package kr.pe.sinnori.common.config.nativevalueconverter;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.config.NativeValueConverterTestIF;

public class SetTypeConverterReturningBooleanTest extends AbstractJunitTest implements NativeValueConverterTestIF {
	
	private SetTypeConverterReturningBoolean nativeValueConverter = null;
	private Boolean returnedValue = null;
	
	
	@Override
	@Before
	public void setup() {
		super.setup();
		
		try {
			nativeValueConverter = new SetTypeConverterReturningBoolean();
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}
	}
	
	@Override
	public void testConstructor() throws Exception {
		/** ignore */
	}
	
	@Override
	@Test
	public void testToNativeValue_ExpectedValueComparison() {
		Boolean expectedValue = null;
		
		/**
		 * info)  Boolean.TRUE.toString() equals to 'true', 
		 *        Boolean.FALSE.toString() equals to 'false',
		 */
		Boolean[] booleans = {Boolean.FALSE, Boolean.TRUE};
		for (int i=0; i < booleans.length; i++) {
			expectedValue = booleans[i];
			try {
				returnedValue = nativeValueConverter.valueOf(expectedValue.toString());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
			
			org.junit.Assert.assertThat("the expected value comparison",
					returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
		}
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
			log.info("'the parameter itemvalue is a empty string' test ok, errormessage={}", 
					e.getMessage());
			throw e;
		}
	}
	
	
	@Override
	public void testToNativeValue_ValidButBadParameter() throws Exception {
		try {
			testToNativeValue_ValidButBadParameter_NotCaseSensitive();
		} catch(IllegalArgumentException e) {
		}
		try {
			testToNativeValue_ValidButBadParameter_NotElementOfSet();
		} catch(IllegalArgumentException e) {
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_ValidButBadParameter_NotCaseSensitive() throws Exception {		
		try {
			returnedValue = nativeValueConverter.valueOf("True");
			
			fail("paramerter itemValue[True] is bad but no error, returnedValue="
					+ returnedValue.toString());
		} catch (IllegalArgumentException e) {
			log.info("'the parameter itemvalue is valid but bad' test ok, errormessage={}", e.getMessage());
			throw e;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_ValidButBadParameter_NotElementOfSet() throws Exception {
		try {
			returnedValue = nativeValueConverter.valueOf("king");
			
			fail("paramerter itemValue[king] is bad but no error, returnedValue="
					+ returnedValue.toString());
		} catch (IllegalArgumentException e) {
			log.info("'the parameter itemvalue is valid but bad' test ok, errormessage={}", e.getMessage());
			throw e;
		}
	}
	
}
