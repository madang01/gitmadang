package kr.pe.codda.common.config.nativevalueconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.config.NativeValueConverterTestIF;
import kr.pe.codda.common.type.MessageProtocolType;

public class SetTypeConverterReturningMessageProtocolTest extends AbstractJunitTest implements
		NativeValueConverterTestIF {	
	private SetTypeConverterReturningMessageProtocolType nativeValueConverter = null;
	
	
	@Override
	@Before
	public void setup() {
		nativeValueConverter = new SetTypeConverterReturningMessageProtocolType();
	}
	
	@Override
	public void testConstructor() throws Exception {
		/** ignore */
	}

	
	@Override
	@Test
	public void testToNativeValue_ExpectedValueComparison() {
		MessageProtocolType expectedValue = null;
		MessageProtocolType returnedValue = null;
		
		MessageProtocolType[] messageProtocoles = MessageProtocolType.values();
		for (int i=0; i < messageProtocoles.length; i++) {
			expectedValue = messageProtocoles[i];
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
	public void testToNativeValue_NullParameter() {
		@SuppressWarnings("unused")
		MessageProtocolType returnedValue = null;
		try {
			returnedValue = nativeValueConverter.valueOf(null);
			
			fail("paramerter itemValue is null but no error");
		} catch (IllegalArgumentException e) {
			log.info("'the parameter itemvalue's null' test ok, errormessage={}", e.getMessage());
			throw e;
		}
	}

	
	@Override
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_EmptyStringParameter() {
		@SuppressWarnings("unused")
		MessageProtocolType returnedValue = null;
		try {
			returnedValue = nativeValueConverter.valueOf("");
			
			fail("paramerter itemValue is a empty string but no error");
			
		} catch (IllegalArgumentException e) {
			log.info("'the parameter itemvalue is a empty string' test ok, errormessage={}", e.getMessage());
			throw e;
		}
	}

	
	@Override
	public void testToNativeValue_ValidButBadParameter()  throws Exception {
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
		MessageProtocolType returnedValue = null;
		try {
			returnedValue = nativeValueConverter.valueOf("dHB");
			
			fail("paramerter itemValue[NoShareSync2] is bad but no error, returnedValue="
					+ returnedValue.toString());		
		} catch (IllegalArgumentException e) {
			log.info("'the parameter itemvalue is valid but bad' test ok, errormessage={}", e.getMessage());
			throw e;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_ValidButBadParameter_NotElementOfSet() throws Exception {
		MessageProtocolType returnedValue = null;
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
