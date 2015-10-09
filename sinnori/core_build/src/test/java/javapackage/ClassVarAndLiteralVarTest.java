package javapackage;

import org.junit.Test;

public class ClassVarAndLiteralVarTest {
	
	
	@Test(expected=NullPointerException.class)
	public void test_널_값을갖는_Integer클래스변수의값을_리터널변수에넣기() {
		Integer i = null;
		@SuppressWarnings({ "unused", "null" })
		int j = i;
	}
	
	@Test
	public void test_널_값을_갖는_Object클래스변수의_Integer클래스로_캐스팅하기() {
		@SuppressWarnings("unused")
		Integer i = null;		
		Object t = null;
		i = (Integer)t;
	}
}
