package javapackage.java.util;

import static org.junit.Assert.fail;

import java.nio.ByteOrder;
import java.util.BitSet;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;

public class BitSetTest {
	Logger log = LoggerFactory.getLogger(BitSetTest.class);
	
	@Test
	public void testConstructor__parameter_nbitsIsZero() {
		
		
		BitSet zeroBitSet = new BitSet(0);
		
		log.info("zeroBitSet.length={}, zeroBitSet.car={}", zeroBitSet.length(), zeroBitSet.cardinality());
		
		
		int result = zeroBitSet.length();
		int expectedValue = 0;
		org.junit.Assert.assertThat("the expected value comparison",
				result, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	@Test
	public void testGetOfZeroSizeBitSet() {
		BitSet zeroBitSet = new BitSet(0);
		
		log.info("zeroBitSet.length={}, zeroBitSet.car={}", zeroBitSet.length(), zeroBitSet.cardinality());
		try {
			// zeroBitSet.set(0);
			boolean result = zeroBitSet.get(1);
			// fail("result="+result);
			
			log.info("zeroBitSet.length={}, zeroBitSet.car={}, result={}", zeroBitSet.length(), zeroBitSet.cardinality(), result);
			
			zeroBitSet.set(1);
			result = zeroBitSet.get(1);
			
			log.info("zeroBitSet.length={}, zeroBitSet.car={}, result={}", zeroBitSet.length(), zeroBitSet.cardinality(), result);
			
			fail("result="+result);
			
		} catch(IndexOutOfBoundsException e) {
			log.info(e.getMessage(), e);
		}
	}
	
	@Test
	public void testHighBitSetLength() {
		long numberOfBits = (long)Math.pow(2, 32) + 1L;
		int highLength = (int) (numberOfBits >> 32);
		
		log.info("numberOfBits={}, highLength={}", numberOfBits, highLength);
		
		java.nio.ByteBuffer t = java.nio.ByteBuffer.allocate(8);
		t.order(ByteOrder.LITTLE_ENDIAN);
		log.info("byteorder={}", t.order());
		
		long bitIndex = CommonStaticFinalVars.UNSIGNED_INTEGER_MAX;
		t.clear();
		t.putLong(bitIndex);
		t.flip();		
		long highIndex = t.getInt();
		long lowIndex = t.getInt();
		log.info("1.bitIndex={}, highIndex={}, lowIndex={}", bitIndex, highIndex, lowIndex);
		
		bitIndex = CommonStaticFinalVars.UNSIGNED_INTEGER_MAX+1L;
		t.clear();
		t.putLong(bitIndex);
		t.flip();
		highIndex = t.getInt();
		lowIndex = t.getInt();
		
		log.info("2.bitIndex={}, highIndex={}, lowIndex={}", bitIndex, highIndex, lowIndex);
		
		bitIndex = CommonStaticFinalVars.UNSIGNED_INTEGER_MAX+2L;
		t.clear();
		t.putLong(bitIndex);
		t.flip();
		highIndex = t.getInt();
		lowIndex = t.getInt();
		log.info("3.bitIndex={}, highIndex={}, lowIndex={}", bitIndex, highIndex, lowIndex);
	}
}
