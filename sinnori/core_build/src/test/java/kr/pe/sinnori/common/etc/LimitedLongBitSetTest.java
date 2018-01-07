package kr.pe.sinnori.common.etc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junitlib.JunitUtil;
import kr.pe.sinnori.common.etc.LimitedLongBitSet.BadBitSetIndexException;
import kr.pe.sinnori.common.etc.LimitedLongBitSet.FailedListFullException;

public class LimitedLongBitSetTest {
	Logger log = LoggerFactory.getLogger(LimitedLongBitSetTest.class);
	
	
	@Test
	public void test_normal() {
		LimitedLongBitSet limitedLongBitSet = new LimitedLongBitSet(5);
	
		try {
			limitedLongBitSet.clear(0);
			limitedLongBitSet.set(1);
			limitedLongBitSet.set(2);
			limitedLongBitSet.set(0);
			limitedLongBitSet.set(3);
			limitedLongBitSet.set(4);
			
			long workingLastCheckedIndex = limitedLongBitSet.getWorkingLastCheckedIndex();
			assertEquals(4L, workingLastCheckedIndex);		
		} catch (IndexOutOfBoundsException e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		} catch (FailedListFullException e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		} catch(Exception e) {
			fail("unknown error::"+e.getMessage());
		}
	}
	
	@Test
	public void testConstructor_badParameter_maxBitNumberIsZero() {
		long maxBitNumber = 0;
		try {			
			@SuppressWarnings("unused")
			LimitedLongBitSet limitedLongBitSet = new LimitedLongBitSet(maxBitNumber);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			log.info("errorMessage=[{}]", errorMessage);
			
			final String exepectedErrorMessage = String.format("the parameter maxBitNumber[%d] is less than or equal to zero", maxBitNumber);
			
			assertEquals(errorMessage, exepectedErrorMessage);
		} catch(Exception e) {
			fail("unknown error::"+e.getMessage());
		}
	}
	
	@Test
	public void testConstructor_badParameter_maxBitNumberIsLessThanZero() {
		long maxBitNumber = -10;
		try {			
			@SuppressWarnings("unused")
			LimitedLongBitSet limitedLongBitSet = new LimitedLongBitSet(maxBitNumber);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			log.info("errorMessage=[{}]", errorMessage);
			
			final String exepectedErrorMessage = String.format("the parameter maxBitNumber[%d] is less than or equal to zero", maxBitNumber);
			
			assertEquals(errorMessage, exepectedErrorMessage);
		} catch(Exception e) {
			fail("unknown error::"+e.getMessage());
		}
	}
	
	@Test
	public void testConstructor_badParameter_lastCheckedIndexIsNegative() {
		long maxBitNumber = 10;
		long lastCheckedIndex = -5;
		try {			
			@SuppressWarnings("unused")
			LimitedLongBitSet limitedLongBitSet = new LimitedLongBitSet(maxBitNumber, lastCheckedIndex);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			log.info("errorMessage=[{}]", errorMessage);
			
			final String exepectedErrorMessage = String.format("the parameter lastCheckedIndex[%d] is less than zero", lastCheckedIndex);
			
			assertEquals(errorMessage, exepectedErrorMessage);
		} catch(Exception e) {
			fail("unknown error::"+e.getMessage());
		}
		
	}
	
	@Test
	public void testConstructor_badParameter_lastCheckedIndexIsGreaterThanMaxBitNumber() {
		long maxBitNumber = 10;
		long lastCheckedIndex = 11;
		try {			
			@SuppressWarnings("unused")
			LimitedLongBitSet limitedLongBitSet = new LimitedLongBitSet(maxBitNumber, lastCheckedIndex);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			log.info("errorMessage=[{}]", errorMessage);
			
			final String exepectedErrorMessage = String.format("the parameter lastCheckedIndex[%d] is greater than or equal to the maxBitNumber[%d]", lastCheckedIndex, maxBitNumber);
			
			assertEquals(errorMessage, exepectedErrorMessage);
		} catch(Exception e) {
			fail("unknown error::"+e.getMessage());
		}
		
	}
	
	
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
			returnedValueObject = JunitUtil.genericInvokMethod(limitedLongBitSet, "checkIndexOutOfBoundsException", 1, bitIndex);
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
	
	@Test
	public void testCheckIndexOutOfBoundsException_badParameter_bitIndexIsLessThanZero() {
		long maxBitNumber = 10;
		LimitedLongBitSet limitedLongBitSet = null;
		try {			
			limitedLongBitSet = new LimitedLongBitSet(maxBitNumber);			
			
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		long bitIndex = -1;
		try {
			limitedLongBitSet.get(bitIndex);
			fail("no IndexOutOfBoundsException");
		} catch(IndexOutOfBoundsException e) {
			String errorMessage = e.getMessage();
			log.info("errorMessage=[{}]", errorMessage);
			
			final String exepectedErrorMessage = String.format("the parameter bitIndex[%d] is less than zero", bitIndex);
			
			
			assertEquals(errorMessage, exepectedErrorMessage);
		} catch(Exception e) {
			fail("unknown error::"+e.getMessage());
		}
	}
	
	@Test
	public void testCheckIndexOutOfBoundsException_badParameter_bitIndexIsGreaterThanOrEqualToMaxBitNumber() {		
		long maxBitNumber = 10;
		LimitedLongBitSet limitedLongBitSet = null;
		try {			
			limitedLongBitSet = new LimitedLongBitSet(maxBitNumber);			
			
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		long bitIndex = maxBitNumber;
		try {
			limitedLongBitSet.get(bitIndex);
			fail("no IndexOutOfBoundsException");
		} catch(IndexOutOfBoundsException e) {
			String errorMessage = e.getMessage();
			log.info("errorMessage=[{}]", errorMessage);
			
			final String exepectedErrorMessage = String.format("the parameter bitIndex[%d] is greater than or equal to the maxBitNumber[%d]", bitIndex, maxBitNumber);
			
			
			assertEquals(errorMessage, exepectedErrorMessage);
		} catch(Exception e) {
			fail("unknown error::"+e.getMessage());
		}
	}
	
	@Test
	public void testCheckIndexOutOfBoundsException_badParameter_bitIndexIsLessThanOrEqualToLastCheckedIndex() {
		long maxBitNumber = 10;
		long lastCheckedIndex = 5;
		LimitedLongBitSet limitedLongBitSet = null;
		try {			
			limitedLongBitSet = new LimitedLongBitSet(maxBitNumber, lastCheckedIndex);			
			
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		long bitIndex = lastCheckedIndex;
		try {
			limitedLongBitSet.get(bitIndex);
			fail("no IndexOutOfBoundsException");
		} catch(IndexOutOfBoundsException e) {
			String errorMessage = e.getMessage();
			log.info("errorMessage=[{}]", errorMessage);
			
			final String exepectedErrorMessage = String.format("the parameter bitIndex[%d] is less than or equal to the lastCheckedIndex[%d]", bitIndex, lastCheckedIndex);
			
			
			assertEquals(errorMessage, exepectedErrorMessage);
		} catch(Exception e) {
			fail("unknown error::"+e.getMessage());
		}
	}
	
	@Test
	public void testClear_badParameter_NotNextBitSetIndex() {
		long maxBitNumber = 10;
		LimitedLongBitSet limitedLongBitSet = null;
		try {			
			limitedLongBitSet = new LimitedLongBitSet(maxBitNumber);			
			
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		long bitIndex = 0;
		try {
			limitedLongBitSet.clear(bitIndex);
			limitedLongBitSet.set(bitIndex++);
			limitedLongBitSet.clear(bitIndex);
		} catch(Exception e) {
			fail("1.unknown error::"+e.getMessage());
		}
		
		try {
			limitedLongBitSet.clear(bitIndex);
		} catch(BadBitSetIndexException e) {
			String errorMessage = e.getMessage();
			log.info("errorMessage=[{}]", errorMessage);
			
			final String exepectedErrorMessage = String.format("the parameter bitIndex[%d] is not a next bit index[%d]", bitIndex, bitIndex+1);
			
			
			assertEquals(errorMessage, exepectedErrorMessage);
		} catch(Exception e) {
			fail("2.unknown error::"+e.getMessage());
		}
	}
	
	
	@Test
	public void testClear_FullListException() {
		long maxBitNumber = 10;
		LimitedLongBitSet limitedLongBitSet = null;
		try {			
			limitedLongBitSet = new LimitedLongBitSet(maxBitNumber);			
			
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		long bitIndex = 0;
		try {
			limitedLongBitSet.clear(bitIndex++);
			limitedLongBitSet.clear(bitIndex++);
			limitedLongBitSet.clear(bitIndex++);
		} catch(Exception e) {
			fail("1.unknown error::"+e.getMessage());
		}
		
		try {
			limitedLongBitSet.clear(bitIndex);
			
			fail("no FullListException");
		} catch(FailedListFullException e) {
			log.info("success, errormessage={}", e.getMessage());
		} catch(Exception e) {
			fail("2.unknown error::"+e.getMessage());
		}
	}
	
	@Test
	public void testSet_duplicatedIndexButNotFailedIndex() {
		long maxBitNumber = 10;
		LimitedLongBitSet limitedLongBitSet = null;
		try {			
			limitedLongBitSet = new LimitedLongBitSet(maxBitNumber);			
			
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		long bitIndex = 0;
		try {
			limitedLongBitSet.set(bitIndex++);
			limitedLongBitSet.set(bitIndex++);
			limitedLongBitSet.set(bitIndex);
		} catch(Exception e) {
			fail("1.unknown error::"+e.getMessage());
		}
		
		long workingLastCheckedIndex = bitIndex;
		bitIndex = 1;
		
		try {
			limitedLongBitSet.set(bitIndex);			
			fail("no BadBitIndexException");
		} catch(BadBitSetIndexException e) {
			String errorMessage = e.getMessage();
			log.info("errorMessage=[{}]", errorMessage);
			
			final String exepectedErrorMessage = String.format("the parameter bitIndex[%d] is an already processed bit index, it is less than or equalst to workingLastCheckedIndex[%d]", bitIndex, workingLastCheckedIndex);
			
			
			assertEquals(errorMessage, exepectedErrorMessage);
			log.info("success, errormessage={}", e.getMessage());
		} catch(Exception e) {
			fail("2.unknown error::"+e.getMessage());
		}
	}
	
	
	@Test
	public void testSet_NoDuplicatedIndexAndNotNextBitSetIndex() {
		long maxBitNumber = 10;
		LimitedLongBitSet limitedLongBitSet = null;
		try {			
			limitedLongBitSet = new LimitedLongBitSet(maxBitNumber);			
			
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		long bitIndex = 0;
		try {
			limitedLongBitSet.set(bitIndex++);
			limitedLongBitSet.set(bitIndex++);
			limitedLongBitSet.set(bitIndex);
		} catch(Exception e) {
			fail("1.unknown error::"+e.getMessage());
		}
		
		long workingLastCheckedIndex = bitIndex;
		bitIndex = workingLastCheckedIndex+2;
		
		try {
			limitedLongBitSet.set(bitIndex);			
			fail("no BadBitIndexException");
		} catch(BadBitSetIndexException e) {
			String errorMessage = e.getMessage();
			log.info("errorMessage=[{}]", errorMessage);
			
			final String exepectedErrorMessage = String.format("the parameter bitIndex[%d] is not a next bit index[%d]", bitIndex, workingLastCheckedIndex+1);
			
			
			assertEquals(errorMessage, exepectedErrorMessage);
			log.info("success, errormessage={}", e.getMessage());
		} catch(Exception e) {
			fail("2.unknown error::"+e.getMessage());
		}
	}
	
	@Test
	public void testGet_returnValueIsTrue() {
		long maxBitNumber = 10;
		LimitedLongBitSet limitedLongBitSet = null;
		try {			
			limitedLongBitSet = new LimitedLongBitSet(maxBitNumber);			
			
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		long bitIndex = 0;
		try {
			limitedLongBitSet.clear(bitIndex++);
			limitedLongBitSet.clear(bitIndex++);
			limitedLongBitSet.set(bitIndex);
			
			
			
			boolean expectedResult = true;
			boolean actualResult = limitedLongBitSet.get(bitIndex);
			
			assertEquals(expectedResult, actualResult);
			
		} catch(Exception e) {
			fail("1.unknown error::"+e.getMessage());
		}
	}
	
	@Test
	public void testGet_returnValueIsFalse_greaterThanWorkingLastCheckedIndex() {
		long maxBitNumber = 10;
		LimitedLongBitSet limitedLongBitSet = null;
		try {			
			limitedLongBitSet = new LimitedLongBitSet(maxBitNumber);			
			
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		long bitIndex = 0;
		try {
			limitedLongBitSet.set(bitIndex++);
			limitedLongBitSet.set(bitIndex++);
			limitedLongBitSet.set(bitIndex);
			
			long workingLastCheckedIndex = bitIndex;
			
			boolean expectedResult = false;
			boolean actualResult = limitedLongBitSet.get(workingLastCheckedIndex+2);
			
			assertEquals(expectedResult, actualResult);
			
		} catch(Exception e) {
			fail("1.unknown error::"+e.getMessage());
		}
	}
	
	@Test
	public void testGet_returnValueIsFalse_workdedIndexButFailedIndex() {
		long maxBitNumber = 10;
		LimitedLongBitSet limitedLongBitSet = null;
		try {			
			limitedLongBitSet = new LimitedLongBitSet(maxBitNumber);			
			
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		long bitIndex = 0;
		boolean expectedResult = false;
		boolean actualResult = false;
		try {
			limitedLongBitSet.set(bitIndex);
			
			expectedResult = true;
			actualResult = limitedLongBitSet.get(bitIndex);			
			assertEquals(expectedResult, actualResult);
			bitIndex++;
			
			limitedLongBitSet.clear(bitIndex);
			
			expectedResult = false;
			actualResult = limitedLongBitSet.get(bitIndex);			
			assertEquals(expectedResult, actualResult);
			bitIndex++;
			
			limitedLongBitSet.clear(bitIndex);
			limitedLongBitSet.set(bitIndex);
			
			expectedResult = true;
			actualResult = limitedLongBitSet.get(bitIndex);			
			assertEquals(expectedResult, actualResult);
			bitIndex++;
			
			expectedResult = false;
			actualResult = limitedLongBitSet.get(1);			
			assertEquals(expectedResult, actualResult);			
			
		} catch(Exception e) {
			fail("1.unknown error::"+e.getMessage());
		}
	}
}
