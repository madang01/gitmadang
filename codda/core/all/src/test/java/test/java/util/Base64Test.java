package test.java.util;

import kr.pe.codda.common.util.CommonStaticUtil;

import org.junit.Test;

public class Base64Test {
	@Test
	public void testGetEncoder_매번새로운인스턴스생성하지않음() {
		System.out.printf("111::Base64 encoder=%d", CommonStaticUtil.Base64Encoder.hashCode());
		System.out.println();
		System.out.printf("222::Base64 encoder=%d", CommonStaticUtil.Base64Encoder.hashCode());
		System.out.println();
	}
}
