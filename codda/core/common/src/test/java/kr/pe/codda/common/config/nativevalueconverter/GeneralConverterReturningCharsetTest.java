package kr.pe.codda.common.config.nativevalueconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.config.NativeValueConverterTestIF;

public class GeneralConverterReturningCharsetTest extends AbstractJunitTest implements
		NativeValueConverterTestIF {

	private GeneralConverterReturningCharset nativeValueConverter = null;
	private Charset returnedValue = null;
	private Charset defaultCharset = Charset.defaultCharset();
	private Charset utf8Charset = Charset.forName("UTF8");
	
	@Before
	public void setup() {
		nativeValueConverter = new GeneralConverterReturningCharset();
	}
	
	@Override
	public void testConstructor() throws Exception {
		/** ignore */
	}

	@Override
	public void testToNativeValue_ExpectedValueComparison() {
		testToNativeValue_ExpectedValueComparison_defalutCharset();
		testToNativeValue_ExpectedValueComparison_UTF8CharsetAliasName();
		testToNativeValue_ExpectedValueComparison_NotCaseSensitive();
	}

	@Test
	public void testToNativeValue_ExpectedValueComparison_defalutCharset() {
		Charset expectedValue = null;
		
		expectedValue = defaultCharset;
		
		try {
			returnedValue = nativeValueConverter.valueOf(defaultCharset.name());

			log.info("default::charset name=[{}], display name=[{}], alias={}",
					returnedValue.name(), returnedValue.displayName(),
					returnedValue.aliases().toString());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		assertEquals("the expected value comparison", returnedValue, expectedValue);
	}

	@Test
	public void testToNativeValue_ExpectedValueComparison_UTF8CharsetAliasName() {
		Charset expectedValue = null;
		
		expectedValue = utf8Charset;
		/** alias test start */
		try {
			returnedValue = nativeValueConverter.valueOf("UTF8");

			log.info("utf8::charset name=[{}], display name=[{}], alias={}",
					returnedValue.name(), returnedValue.displayName(),
					returnedValue.aliases().toString());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}

		
		assertEquals("the expected value comparison", returnedValue, expectedValue);

		try {
			returnedValue = nativeValueConverter.valueOf("unicode-1-1-utf-8");

			log.info("utf8::charset name=[{}], display name=[{}], alias={}",
					returnedValue.name(), returnedValue.displayName(),
					returnedValue.aliases().toString());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}

		
		assertEquals("the expected value comparison", returnedValue, expectedValue);

		/** alias test end */
	}

	@Test
	public void testToNativeValue_ExpectedValueComparison_NotCaseSensitive() {
		Charset expectedValue = null;
		
		expectedValue = utf8Charset;
		
		/** 대소문자 구분 여부 테스트 시작 */
		try {
			returnedValue = nativeValueConverter.valueOf("Unicode-1-1-utf-8");

			log.info("utf8::charset name=[{}], display name=[{}], alias={}",
					returnedValue.name(), returnedValue.displayName(),
					returnedValue.aliases().toString());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		assertEquals("the expected value comparison", returnedValue, expectedValue);

		try {
			returnedValue = nativeValueConverter.valueOf("uTF-8");

			log.info("utf8::charset name=[{}], display name=[{}], alias={}",
					returnedValue.name(), returnedValue.displayName(),
					returnedValue.aliases().toString());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		}

		
		assertEquals("the expected value comparison", returnedValue, expectedValue);
		/** 대소문자 구분 여부 테스트 종료 */
	}

	
	@Override
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_NullParameter() {
		try {
			returnedValue = nativeValueConverter.valueOf(null);

			log.warn("error::charset name=[{}], display name=[{}], alias={}",
					returnedValue.name(), returnedValue.displayName(),
					returnedValue.aliases().toString());

			// fail("paramerter itemValue is null but no error");
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter itemvalue is null' test ok, errormessage={}",
					e.getMessage());
			throw e;
		}

	}

	
	@Override
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_EmptyStringParameter() {
		try {
			returnedValue = nativeValueConverter.valueOf("");

			log.warn("error::charset name=[{}], display name=[{}], alias={}",
					returnedValue.name(), returnedValue.displayName(),
					returnedValue.aliases().toString());

			// fail("paramerter itemValue is a empty string but no error");
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter itemvalue is a empty string' test ok, errormessage={}",
					e.getMessage());
			throw e;
		}
	}

	
	@Override
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_ValidButBadParameter() {
		try {
			returnedValue = nativeValueConverter.valueOf("king");

			log.warn("error::charset name=[{}], display name=[{}], alias={}",
					returnedValue.name(), returnedValue.displayName(),
					returnedValue.aliases().toString());

			/*
			 * fail("paramerter itemValue[king] is bad but no error, nativeValue="
			 * + returnedValue.toString());
			 */
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter itemvalue is valid but bad' test ok, errormessage={}",
					e.getMessage());
			throw e;
		}
	}
}
