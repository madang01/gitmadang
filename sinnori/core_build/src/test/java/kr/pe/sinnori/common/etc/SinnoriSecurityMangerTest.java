package kr.pe.sinnori.common.etc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import junitlib.JunitUtil;
import kr.pe.sinnori.common.AbstractJunitSupporter;

public class SinnoriSecurityMangerTest extends AbstractJunitSupporter {
	
	
	/**
	 * this method test for the private method 'LimitedLongBitSet#checkIndexOutOfBoundsException' using Java reflection.
	 * if this test can't run because of java reflection permission, then check java reflection permission
	 * (ex vm argument -Djava.security.manager, the Sinnori project set vm argument 'java.security.manager' to 'kr.pe.sinnori.common.etc.SinnoriSecurityManger' for preventing java reflection)  
	 */
	@Test
	public void testCheckIndexOutOfBoundsException_usnigJavaRelection_badParameter_bitIndexIsLessThanZero() {		
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
