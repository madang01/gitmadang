package kr.pe.sinnori.common.config.nativevalueconverter;

import static org.junit.Assert.fail;

import java.nio.ByteOrder;

import kr.pe.sinnori.common.config.NativeValueConverterTestIF;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterReturningByteOrder;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetTypeConverterReturningByteOrderTest implements NativeValueConverterTestIF {
	Logger log = LoggerFactory.getLogger(SetTypeConverterReturningByteOrderTest.class);
	
	private SetTypeConverterReturningByteOrder nativeValueConverter = null;
	private ByteOrder returnedValue = null;	
	
	
	@Override
	@Before
	public void setup() {
		try {
			nativeValueConverter = new SetTypeConverterReturningByteOrder();
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
		ByteOrder expectedValue = null;
				
		/**
		 * info)  ByteOrder.LITTLE_ENDIAN.toString() equals to 'LITTLE_ENDIAN', 
		 *        ByteOrder.BIG_ENDIAN.toString() equals to 'BIG_ENDIAN',
		 */
		ByteOrder[] byteOrders = {ByteOrder.LITTLE_ENDIAN, ByteOrder.BIG_ENDIAN};
		for (int i=0; i < byteOrders.length; i++) {
			expectedValue = byteOrders[i];
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
			returnedValue = nativeValueConverter.valueOf("BIG_eNDIAN");
			
			fail("paramerter itemValue[king] is bad but no error, returnedValue="
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
			
			fail("paramerter itemValue[king] is bad but no error, returnedValue="
					+ returnedValue.toString());
		} catch (IllegalArgumentException e) {
			log.info("'the parameter itemvalue is valid but bad' test ok, errormessage={}", e.getMessage());
			throw e;
		}
	}
}
