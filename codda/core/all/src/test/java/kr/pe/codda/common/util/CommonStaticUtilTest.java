package kr.pe.codda.common.util;

import org.junit.Test;

public class CommonStaticUtilTest {

	@Test
	public void testSpeedWithCharArray() {
		
		int retryCount=1000000;
		
		String sourceString = "helloAZzA23";
		
		System.out.printf("%s", CommonStaticUtil.isEnglishAndDigit(sourceString));
		
		for (int i=0; i < retryCount; i++) {
			CommonStaticUtil.isEnglishAndDigit(sourceString);
		}
	}

	
	@Test
	public void testSpeedWithRegular() {
		
		int retryCount=1000000;
		
		String sourceString = "helloAZzA23";
		
		System.out.printf("%s", CommonStaticUtil.isEnglishAndDigitWithRegular(sourceString));
		
		for (int i=0; i < retryCount; i++) {
			CommonStaticUtil.isEnglishAndDigitWithRegular(sourceString);
		}
	}
}
