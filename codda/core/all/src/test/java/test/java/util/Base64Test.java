package test.java.util;

import kr.pe.codda.common.util.CommonStaticUtil;

import org.junit.Test;

import junitlib.AbstractJunitTest;

public class Base64Test extends AbstractJunitTest {
	@Test
	public void testGetEncoder_매번새로운인스턴스생성하지않음() {
		System.out.printf("111::Base64 encoder=%d", CommonStaticUtil.Base64Encoder.hashCode());
		System.out.println();
		System.out.printf("222::Base64 encoder=%d", CommonStaticUtil.Base64Encoder.hashCode());
		System.out.println();
	}
	
	@Test
	public void testGetEncoder_입력데이터없는경우() {
		
		try {
			byte[] encodedBytes = CommonStaticUtil.Base64Encoder.encode(new byte[0]);
			
			if (null == encodedBytes) {
				log.info("입력 데이터가 없는 경우 리턴값은 널");
			} else {
				log.info("입력 데이터가 없는 경우 리턴값은 널 아님, returned bytes length={}", encodedBytes.length);
			}
			
		} catch(Exception e) {
			log.info("에러", e);
		}
		
		try {
			String encodedString = CommonStaticUtil.Base64Encoder.encodeToString(new byte[0]);
			
			if (null == encodedString) {
				log.info("입력 데이터가 없는 경우 리턴값은 널");
			} else {
				log.info("입력 데이터가 없는 경우 리턴값은 널 아님, returned string=[{}]", encodedString);
			}
			
			byte[] decodedBytes = CommonStaticUtil.Base64Decoder.decode(encodedString);
			
			if (null == decodedBytes) {
				log.info("the var decodedBytes is null");
			} else {
				log.info("the var decodedBytes is not null, decodedBytes length=[{}]", decodedBytes.length);
			}
			
		} catch(Exception e) {
			log.info("에러", e);
		}
	}
}
