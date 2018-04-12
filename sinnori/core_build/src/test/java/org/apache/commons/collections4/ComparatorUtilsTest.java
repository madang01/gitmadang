package org.apache.commons.collections4;

import java.util.Comparator;

import org.junit.Test;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class ComparatorUtilsTest {
	private final InternalLogger log = InternalLoggerFactory.getInstance(ComparatorUtilsTest.class);
	
	@Test
	public void testNaturalComparator() {
		Comparator<Long> longTypeComparator = ComparatorUtils.<Long>naturalComparator();
		// int expectedResult;
		int result;
		
		// expectedResult = -1;
		
		result = longTypeComparator.compare(10L, 20L);
		log.info("longTypeComparator min[10L], max[20L], result=[{}]", result);
		if (result >= 0) {
			org.junit.Assert.fail("fail");
		}
		
		
		result = longTypeComparator.compare(-10L, -20L);
		log.info("longTypeComparator min[-10L], max[-20L], result=[{}]", result);
		if (result <= 0) {
			org.junit.Assert.fail("fail");
		}
		
		
		Comparator<Integer> integerTypeComparator = ComparatorUtils.<Integer>naturalComparator();
		result = integerTypeComparator.compare(10, 20);
		log.info("integerTypeComparator min[10L], max[20L], result=[{}]", result);
		if (result >= 0) {
			org.junit.Assert.fail("fail");
		}
		
		result = integerTypeComparator.compare(-10, -20);
		log.info("integerTypeComparator min[-10L], max[-20L], result=[{}]", result);
		if (result <= 0) {
			org.junit.Assert.fail("fail");
		}
		
	}
}
