package test.java.util;

import java.util.Base64;

import org.junit.Test;

public class Base64Test {
	@Test
	public void testGetEncoder_매번새로운인스턴스생성하지않음() {
		System.out.printf("111::Base64 encoder=%d", Base64.getEncoder().hashCode());
		System.out.println();
		System.out.printf("222::Base64 encoder=%d", Base64.getEncoder().hashCode());
		System.out.println();
	}
}
