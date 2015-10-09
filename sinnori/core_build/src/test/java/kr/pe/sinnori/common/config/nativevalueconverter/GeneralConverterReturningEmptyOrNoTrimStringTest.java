package kr.pe.sinnori.common.config.nativevalueconverter;

import static org.junit.Assert.fail;
import kr.pe.sinnori.common.config.NativeValueConverterTestIF;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneralConverterReturningEmptyOrNoTrimStringTest implements
NativeValueConverterTestIF {
	
	Logger log = LoggerFactory.getLogger(GeneralConverterReturningEmptyOrNoTrimStringTest.class);

	private GeneralConverterReturningEmptyOrNoTrimString nativeValueConverter = null;
	private String returnedValue = null;

	@Override
	@Before
	public void setup() {
		nativeValueConverter = new GeneralConverterReturningEmptyOrNoTrimString();
	}
	
	@Override
	public void testConstructor() throws Exception {
		/** ignore */
	}
	
	@Test
	public void testRegularExprecssion_() {
		try {
			boolean returnValue = "\n\nabc d  ".matches("^\\s+[^.$]*|[^.$]*\\s+$");
			log.info("1.returnValue={}", returnValue);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void testToNativeValue_ExpectedValueComparison() {
		testToNativeValue_ExpectedValueComparison_EmptyString();
		testToNativeValue_ExpectedValueComparison_NotEmptyString();
	}
	
	@Test
	public void testToNativeValue_ExpectedValueComparison_EmptyString() {
		String expectedValue = null;
		
		expectedValue = "";		
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);

			
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testToNativeValue_ExpectedValueComparison_NotEmptyString() {
		String expectedValue = null;
		
		expectedValue = "aabc";		
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);

			
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}

	@Override
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_NullParameter() throws Exception {
		String expectedValue = null;
		
		// expectedValue = "aabc";		
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);

			
		} catch (IllegalArgumentException e) {
			log.info(e.getMessage());
			throw e;
		}
	}

	@Override
	public void testToNativeValue_EmptyStringParameter() throws Exception {
		/**
		 * Notice) empty string is valid. so this case drop.
		 */
	}

	@Override
	public void testToNativeValue_ValidButBadParameter() throws Exception {
		try {
			testToNativeValue_ValidButBadParameter_TrimString_OneSpaceChar();			
		} catch (IllegalArgumentException e) {			
		}
		try {
			testToNativeValue_ValidButBadParameter_TrimString_case1();			
		} catch (IllegalArgumentException e) {
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_ValidButBadParameter_TrimString_OneSpaceChar() throws Exception {
		String expectedValue = null;
		
		expectedValue = " ";		
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);

			
		} catch (IllegalArgumentException e) {
			log.info(e.getMessage());
			throw e;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_ValidButBadParameter_TrimString_case1() throws Exception {
		String expectedValue = null;
		
		expectedValue = " a \tb \t";		
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);

			
		} catch (IllegalArgumentException e) {
			log.info(e.getMessage());
			throw e;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_ValidButBadParameter_TrimString_MultiLine_HeadSpace() throws Exception {
		String expectedValue = null;
		
		expectedValue = " ab\nc ";
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);
			
			log.info("1.returnedValue=[{}]", returnedValue);
		} catch (IllegalArgumentException e) {
			log.info(e.getMessage());
			throw e;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_ValidButBadParameter_TrimString_MultiLine_TailSpace() throws Exception {
		String expectedValue = null;
		
		expectedValue = " ab\nc \t";
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue);
			
			log.info("2.returnedValue=[{}]", returnedValue);
		} catch (IllegalArgumentException e) {
			log.info(e.getMessage());
			throw e;
		}
	}
}