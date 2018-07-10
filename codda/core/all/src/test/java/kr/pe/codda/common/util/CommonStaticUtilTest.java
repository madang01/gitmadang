package kr.pe.codda.common.util;

import org.junit.Test;

public class CommonStaticUtilTest {

	@Test
	public void testSpeedWithCharArray() {
		
		int retryCount=1000000;
		
		String sourceString = "hello23";
		
		System.out.printf("%s", CommonStaticUtil.isAlphabetAndDigit(sourceString));
		
		for (int i=0; i < retryCount; i++) {
			CommonStaticUtil.isAlphabetAndDigit(sourceString);
		}
	}

	
	@Test
	public void testSpeedWithRegular() {
		
		int retryCount=1000000;
		
		String sourceString = "hello23";
		
		System.out.printf("%s", CommonStaticUtil.isAlphabetAndDigitWithRegular(sourceString));
		
		for (int i=0; i < retryCount; i++) {
			CommonStaticUtil.isAlphabetAndDigitWithRegular(sourceString);
		}
	}
}
