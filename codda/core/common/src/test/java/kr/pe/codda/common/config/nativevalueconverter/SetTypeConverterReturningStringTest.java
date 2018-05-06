package kr.pe.codda.common.config.nativevalueconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.config.NativeValueConverterTestIF;

public class SetTypeConverterReturningStringTest extends AbstractJunitTest implements
NativeValueConverterTestIF {	
	private SetTypeConverterReturningString nativeValueConverter = null;
	private String returnedValue = null;
	
	@Override
	@Before
	public void setup() {
		nativeValueConverter = new SetTypeConverterReturningString("AES", "DESede", "DES");
	}

	@Override
	public void testConstructor() throws Exception {
		try {
			testConstructor_NoParameter();
		} catch(IllegalArgumentException e) {
		}
		try {
			testConstructor_TrimString();
		} catch(IllegalArgumentException e) {
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_NoParameter() throws Exception {
		try {
			new SetTypeConverterReturningString("AES", "DESede", "DES ");
		} catch(IllegalArgumentException e) {
			log.info("no parameter test ok, errormessage={}", e.getMessage());
			throw e;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_TrimString() throws Exception {
		try {
			new SetTypeConverterReturningString();
		} catch(IllegalArgumentException e) {
			log.info("one more elements of varaiable parameter have a trim string  test ok, errormessage={}", e.getMessage());
			throw e;
		}
	}

	@Override
	@Test
	public void testToNativeValue_ExpectedValueComparison() {
		Iterator<String> iter = nativeValueConverter.getItemValueSet().iterator();
		while(iter.hasNext()) {
			String expectedValue = iter.next();
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
	public void testToNativeValue_NullParameter() throws Exception {
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
			returnedValue = nativeValueConverter.valueOf("aES");
			
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
