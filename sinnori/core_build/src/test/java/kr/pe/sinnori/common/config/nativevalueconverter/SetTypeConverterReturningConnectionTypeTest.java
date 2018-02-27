package kr.pe.sinnori.common.config.nativevalueconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitSupporter;
import kr.pe.sinnori.common.config.NativeValueConverterTestIF;
import kr.pe.sinnori.common.type.ConnectionType;

public class SetTypeConverterReturningConnectionTypeTest extends AbstractJunitSupporter implements NativeValueConverterTestIF {
	
	
	private SetTypeConverterReturningConnectionType nativeValueConverter = null;
	private ConnectionType returnedValue = null;
	
	@Override
	@Before
	public void setup() {
		super.setup();
		nativeValueConverter = new SetTypeConverterReturningConnectionType();
	}
	
	@Override
	public void testConstructor() throws Exception {
		/** ignore */
	}
	
	@Override
	@Test
	public void testToNativeValue_ExpectedValueComparison() {
		ConnectionType expectedValue = null;
		
		ConnectionType[] conectionTypes = ConnectionType.values();
		for (int i=0; i < conectionTypes.length; i++) {
			expectedValue = conectionTypes[i];
			try {
				returnedValue = nativeValueConverter.valueOf(expectedValue.toString());
			} catch (IllegalArgumentException e) {
				fail(e.getMessage());
			}
			
			assertEquals("the expected value comparison", returnedValue, expectedValue);
		}
	}

	@Override
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_NullParameter()  throws Exception {
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
			returnedValue = nativeValueConverter.valueOf("noShareSync");
			
			fail("paramerter itemValue[NoShareSync2] is bad but no error, returnedValue="
					+ returnedValue.toString());		
		} catch (IllegalArgumentException e) {
			log.info("'the parameter itemvalue is valid but bad' test ok, errormessage={}", e.getMessage());
			throw e;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_ValidButBadParameter_NotElementOfSet() throws Exception {
		try {
			returnedValue = nativeValueConverter.valueOf("aabbcc");
			
			fail("paramerter itemValue[NoShareSync2] is bad but no error, returnedValue="
					+ returnedValue.toString());		
		} catch (IllegalArgumentException e) {
			log.info("'the parameter itemvalue is valid but bad' test ok, errormessage={}", e.getMessage());
			throw e;
		}
	}
}
