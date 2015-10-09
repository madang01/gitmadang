package kr.pe.sinnori.common.config.nativevalueconverter;

import static org.junit.Assert.fail;
import kr.pe.sinnori.common.config.NativeValueConverterTestIF;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// GeneralConverterReturningUpDownFileBlockMaxSize
public class GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMaxTest
		implements NativeValueConverterTestIF {

	Logger log = LoggerFactory
			.getLogger(GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMaxTest.class);
	private GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax nativeValueConverter = null;
	private Integer returnedValue = null;
	private Integer expectedValue = null;

	@Override
	@Before
	public void setup() {
		nativeValueConverter = new GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax(
				600, 2048);
	}

	@Override
	public void testConstructor() throws Exception {
		try {
			testConstructor_MinIsLessThanZero();
		} catch (IllegalArgumentException e) {
		}

		try {
			testConstructor_MaxIsLessThan1024();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_MinIsLessThanZero() throws Exception {
		@SuppressWarnings("unused")
		GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax nativeValueConverter = null;
		try {
			nativeValueConverter = new GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax(
					-1, 1);
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter min is less than zero' test ok, errormessage={}",
					e.getMessage());
			throw e;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_MaxIsLessThan1024() throws Exception {
		@SuppressWarnings("unused")
		GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax nativeValueConverter = null;
		try {
			nativeValueConverter = new GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax(
					2, 1023);
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter max is less than 1024' test ok, errormessage={}",
					e.getMessage());
			throw e;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_MinParameterIsOverMax() throws Exception {
		@SuppressWarnings("unused")
		GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax nativeValueConverter = null;
		try {
			nativeValueConverter = new GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax(
					2, 1);
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter min is over than max' test ok, errormessage={}",
					e.getMessage());
			throw e;
		}
	}

	@Override
	@Test
	public void testToNativeValue_ExpectedValueComparison() {
		expectedValue = new Integer(1024);
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue
					.toString());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}

	@Override
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_NullParameter() throws Exception {
		try {
			returnedValue = nativeValueConverter.valueOf(null);
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter itemvalue is null' test ok, errormessage={}",
					e.getMessage());
			throw e;
		}
	}

	@Override
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_EmptyStringParameter() throws Exception {
		try {
			returnedValue = nativeValueConverter.valueOf("");
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter itemvalue is a empty string' test ok, errormessage={}",
					e.getMessage());
			throw e;
		}
	}

	@Override
	public void testToNativeValue_ValidButBadParameter() throws Exception {
		try {
			testToNativeValue_ValidButBadParameter_NotIntegerString();
		} catch (IllegalArgumentException e) {
		}
		try {
			testToNativeValue_ValidButBadParameter_TooBigNumberStringToFitInInteger();
		} catch (IllegalArgumentException e) {
		}
		try {
			testToNativeValue_ValidButBadParameter_LessThanMin();
		} catch (IllegalArgumentException e) {
		}
		try {
			testToNativeValue_ValidButBadParameter_OverThanMax();
		} catch (IllegalArgumentException e) {
		}
		try {
			testToNativeValue_ValidButBadParameter_1024Multiple();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_ValidButBadParameter_NotIntegerString()
			throws Exception {
		try {
			returnedValue = nativeValueConverter.valueOf("abc");
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter itemvalue is valid but bad' test ok, errormessage={}",
					e.getMessage());
			throw e;
		}

	}

	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_ValidButBadParameter_TooBigNumberStringToFitInInteger()
			throws Exception {
		try {
			returnedValue = nativeValueConverter.valueOf(""
					+ Long.MAX_VALUE);
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter itemvalue is valid but bad' test ok, errormessage={}",
					e.getMessage());
			throw e;
		}

	}

	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_ValidButBadParameter_LessThanMin()
			throws Exception {
		try {
			returnedValue = nativeValueConverter.valueOf(""
					+ (nativeValueConverter.getMin() - 1));
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter itemvalue is valid but bad' test ok, errormessage={}",
					e.getMessage());
			throw e;
		}

	}

	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_ValidButBadParameter_OverThanMax()
			throws Exception {
		try {
			returnedValue = nativeValueConverter.valueOf(""
					+ (nativeValueConverter.getMax() + 1));
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter itemvalue is valid but bad' test ok, errormessage={}",
					e.getMessage());
			throw e;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_ValidButBadParameter_1024Multiple()
			throws Exception {
		try {
			returnedValue = nativeValueConverter.valueOf("1023");
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter itemvalue is valid but bad' test ok, errormessage={}",
					e.getMessage());
			throw e;
		}
	}

}
