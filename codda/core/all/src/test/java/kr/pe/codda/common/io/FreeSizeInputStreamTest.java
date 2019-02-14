package kr.pe.codda.common.io;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import junitlib.AbstractJunitTest;

public class FreeSizeInputStreamTest extends AbstractJunitTest {

	@Test
	public void test_스트림에서숫자를읽을때_비트와_바이트버퍼_속도비교() {
		
		ByteBuffer intByteBuffer = ByteBuffer.allocate(4);
		
		intByteBuffer.clear();
		intByteBuffer.order(ByteOrder.BIG_ENDIAN);
		
		int tValue = 2147483647;
		intByteBuffer.putInt(tValue);
		intByteBuffer.flip();
		
		System.out.printf("%s ==> 첫번째 : 0x%02x, 두번째 0x%02x, 세번째 0x%02x, 네번째 0x%02x", 
				intByteBuffer.order().toString(), intByteBuffer.get(), intByteBuffer.get(), intByteBuffer.get(), intByteBuffer.get());		
		System.out.println();
		// BIG_ENDIAN ==> 첫번째 : 0x01, 두번째 0x2d
		
		intByteBuffer.clear();
		intByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		intByteBuffer.putInt(tValue);
		intByteBuffer.flip();
		
		System.out.printf("%s ==> 첫번째 : 0x%02x, 두번째 0x%02x, 세번째 0x%02x, 네번째 0x%02x", 
				intByteBuffer.order().toString(), intByteBuffer.get(), intByteBuffer.get(), intByteBuffer.get(), intByteBuffer.get());		
		System.out.println();
		
		int retValue = 0;
		
		byte t1 = 0x7f;
		byte t2 = (byte)0xff;
		byte t3 = (byte)0xff;
		byte t4 = (byte)0xff;
		
		long startTime = System.nanoTime();
		for (int i=0; i < 1000000; i++) {
			retValue = (((t1 & 0xff) << 24) | ((t2 & 0xff) << 16) | ((t3 & 0xff) << 8) | (t4 & 0xff));
		}
		
		System.out.printf("1.retValue=%d", retValue);
		System.out.println();
		
		long endTime = System.nanoTime();
		
		System.out.printf("1.백만번 경과시간=[%d] ms", TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));
		System.out.println();
		
		intByteBuffer.order(ByteOrder.BIG_ENDIAN);
		startTime = System.nanoTime();
		for (int i=0; i < 1000000; i++) {
			intByteBuffer.clear();
			intByteBuffer.put(t1);
			intByteBuffer.put(t2);
			intByteBuffer.put(t3);
			intByteBuffer.put(t4);
			intByteBuffer.flip();
			retValue = intByteBuffer.getInt();
		}
		endTime = System.nanoTime();
		
		System.out.printf("2.백만번 경과시간=[%d] ms", TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));
		System.out.println();
		
		System.out.printf("2.retValue=%d", retValue);
		System.out.println();
		
		
		/*System.out.printf("retValue=%d", retValue);
		System.out.println();*/
		
		
	}
	
	@Test
	public void test_스트림에서long형데이터를비트를이용하여복원() {
		byte t1 = 0x7f;
		byte t2 = (byte)0xff;
		byte t3 = (byte)0xff;
		byte t4 = (byte)0xff;
		byte t5 = (byte)0xff;
		byte t6 = (byte)0xff;
		byte t7 = (byte)0xff;
		byte t8 = (byte)0xff;
		
		
		long retValue = (((t1 & 0xffL) << 56 ) | ((t2 & 0xffL) << 48) 
				| ((t3 & 0xffL) << 40)  | ((t4 & 0xffL) << 32 ) 
				| ( (t5 & 0xffL) << 24)| ((t6 & 0xffL) << 16) | ((t7 & 0xffL) << 8) | (t8  & 0xffL));
		
		
		System.out.printf("1.retValue=%d", retValue);
		System.out.println();
		
		ByteBuffer longByteBuffer = ByteBuffer.allocate(8);
		longByteBuffer.order(ByteOrder.BIG_ENDIAN);
		longByteBuffer.putLong(retValue);
		longByteBuffer.flip();
		
		int i=0;
		while (longByteBuffer.hasRemaining()) {
			System.out.printf("longByteBuffer[%d]=0x%02x", i++, longByteBuffer.get());
			System.out.println();
		}
		
	}

}
