package kr.pe.sinnori.common.config.nativevalueconverter;

import static org.junit.Assert.fail;
import kr.pe.sinnori.common.config.NativeValueConverterTestIF;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterReturningConnectionType;
import kr.pe.sinnori.common.etc.CommonType;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetTypeConverterReturningConnectionTypeTest implements NativeValueConverterTestIF {
	Logger log = LoggerFactory.getLogger(SetTypeConverterReturningConnectionTypeTest.class);
	
	private SetTypeConverterReturningConnectionType nativeValueConverter = null;
	private CommonType.CONNECTION_TYPE returnedValue = null;
	
	@Override
	@Before
	public void setup() {
		nativeValueConverter = new SetTypeConverterReturningConnectionType();
	}
	
	@Override
	public void testConstructor() throws Exception {
		/** ignore */
	}
	
	@Override
	@Test
	public void testToNativeValue_ExpectedValueComparison() {
		CommonType.CONNECTION_TYPE expectedValue = null;
		
		CommonType.CONNECTION_TYPE[] conectionTypes = CommonType.CONNECTION_TYPE.values();
		for (int i=0; i < conectionTypes.length; i++) {
			expectedValue = conectionTypes[i];
			try {
				returnedValue = nativeValueConverter.valueOf(expectedValue.toString());
			} catch (IllegalArgumentException e) {
				fail(e.getMessage());
			}
			
			org.junit.Assert.assertThat("the expected value comparison",
					returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
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
