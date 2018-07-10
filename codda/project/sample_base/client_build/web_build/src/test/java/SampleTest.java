import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;

import org.junit.Test;

public class SampleTest {

	@Test
	public void test() {
		
		System.out.printf("(float)0.33333333+(float)0.33333333 의 결과  = %f", (float)0.33333333+(float)0.33333333);
		System.out.println("");
		
		Float v1 = (float)1/3;
		if (v1.equals((float)0.33333333)) {
			System.out.println("1/3 is same to 0.33333333");
		} else {
			System.out.println("1/3 is not same to 0.33333333");
		}
		
		Float v2 = Float.sum((float)1/(float)3, (float)1/(float)3);
		
		System.out.printf("v1 hex=%s", Float.toHexString(v1));
		System.out.println("");
		
		System.out.printf("v2 hex=%s", Float.toHexString(v2));
		System.out.println("");
		
		ByteBuffer t1 = ByteBuffer.allocate(8);
		t1.putFloat((float)2/(float)3);
		t1.flip();
		
		System.out.println("");
		while(t1.hasRemaining()) {
			byte tvalue = t1.get();
			System.out.printf("\nt1 hex=%x", tvalue);
			
		}
		System.out.println("");
		
		
		BigDecimal big1 = BigDecimal.valueOf((float)1);
		BigDecimal big2 = BigDecimal.valueOf((float)3);
		BigDecimal big3 = big1.divide(big2, 7, RoundingMode.CEILING);
		BigDecimal big4 = big3.add(big3);
		BigDecimal big5 = big4.add(big3);
		System.out.printf("\nbig3 hex=%s", big3.toString());
		System.out.printf("\nbig5 hex=%s", big5.toString());
		
		
		/*
		
		float var1=1;
		float var2=3;
		float var3=var1/var2;
		 
		
		float floatResult = var3 + (float)1/3 + (float)1/3;
		
		System.out.printf("수식 1/3 + 1/3 + 1/3 의 정수 결과  = %f", floatResult);
		System.out.println("");
		
		ByteBuffer t1 = ByteBuffer.allocate(8);
		t1.putFloat((float)1/3);
		
		System.out.printf("t1.postion=%d", t1.position());
		System.out.println("");
		
		t1.flip();
		float v4 = t1.getFloat();
		if (v4 == (float)0.33333333) {
			System.out.println("1/3 is same to 0.33333333");
		} else {
			System.out.println("1/3 is not same to 0.33333333");
		}
		
		ByteBuffer t2 = ByteBuffer.allocate(8);
		t2.putFloat((float)0.33333333+(float)0.33333333);
			
		
		t1.flip();
		t2.flip();
		
		while(t1.hasRemaining()) {
			byte tvalue = t1.get();
			
			System.out.printf("t1[%d]=%x", t1.position()-1, tvalue);
			System.out.println("");
			
		}
		
		System.out.printf("t2=%f", t2.getFloat());
		System.out.println("");*/
		
	}
}
