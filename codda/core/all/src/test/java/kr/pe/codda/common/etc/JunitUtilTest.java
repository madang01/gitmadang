package kr.pe.codda.common.etc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import junitlib.JunitUtil;


public class JunitUtilTest extends AbstractJunitTest {
	
	/**
	 * if you want to prevent 'Java reflection', please refer to the {@link CoddaSecurityManger} class.
	 */
	@Test
	public void testGenericInvokMethod_CheckIndexOutOfBoundsException_usnigJavaRelection_badParameter_bitIndexIsLessThanZero() {		
		long maxBitNumber = 10;
		LimitedLongBitSet limitedLongBitSet = null;
		try {			
			limitedLongBitSet = new LimitedLongBitSet(maxBitNumber);			
			
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		long bitIndex = -1;
		@SuppressWarnings("unused")
		Object returnedValueObject = null;
		try {
			returnedValueObject = JunitUtil.genericInvokMethod(limitedLongBitSet, "throwExceptionIfIndexOutOfBound", 1, bitIndex);			
			
			fail("no InvocationTargetException");
		} catch(InvocationTargetException e) {
			Throwable causedException = e.getCause();
			if (null != causedException && causedException instanceof IndexOutOfBoundsException) {
				String errorMessage = causedException.getMessage();
				log.info("errorMessage=[{}]", errorMessage);
				
				final String exepectedErrorMessage = String.format("the parameter bitIndex[%d] is less than zero", bitIndex);
				
				assertEquals(errorMessage, exepectedErrorMessage);
			} else {
				log.warn(e.getMessage(), e);
				fail("InvocationTargetException error::"+e.getMessage());
			}
		
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			fail("unknown error::"+e.getMessage());
		}
		
		/*if (null == returnedValueObject) {
			fail("the returned value object of LimitedLongBitSet class::checkIndexOutOfBoundsException method is null");
		}*/		
	}
}
