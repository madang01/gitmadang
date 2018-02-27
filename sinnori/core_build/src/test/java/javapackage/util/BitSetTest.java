package javapackage.util;

import static org.junit.Assert.assertEquals;

import java.util.BitSet;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitSupporter;

public class BitSetTest extends AbstractJunitSupporter {
	// Logger log = LoggerFactory.getLogger(BitSetTest.class);
	
	@Test
	public void test_javaAPI_BitSet_Constructor_parameter_nbitsIsZero() {
		BitSet zeroBitSet = new BitSet(0);
		
		log.info("zeroBitSet.length={}, zeroBitSet.car={}", zeroBitSet.length(), zeroBitSet.cardinality());
		
		
		int result = zeroBitSet.length();
		int expectedValue = 0;
		assertEquals("the expected value comparison", expectedValue, result);
	}
	
	@Test
	public void test_javaAPI_ZeroSizeBitSet_method_parameter_nbitsIsZero() {
		BitSet zeroBitSet = new BitSet(0);
		
		log.info("1. zeroBitSet.length={}, zeroBitSet.size={}", zeroBitSet.length(), zeroBitSet.size());
		try {
			boolean resultOfIndex = zeroBitSet.get(1);
			log.info("2. zeroBitSet.length={}, zeroBitSet.size={}", zeroBitSet.length(), zeroBitSet.size());
			// fail("BitSet get at zero index::"+resultOfIndex);
			
			zeroBitSet.set(0, true);
			log.info("3. zeroBitSet.length={}, zeroBitSet.size={}", zeroBitSet.length(), zeroBitSet.size());
			
			resultOfIndex = zeroBitSet.get(3);
			log.info("4. zeroBitSet.length={}, zeroBitSet.size={}", zeroBitSet.length(), zeroBitSet.size());
			
			log.warn("BitSet doesn't throw IndexOutOfBoundsException::"+resultOfIndex);
		} catch(IndexOutOfBoundsException e) {
			log.info(e.getMessage(), e);
		}
	}
}
