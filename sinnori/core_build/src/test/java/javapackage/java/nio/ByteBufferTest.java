package javapackage.java.nio;

import static org.junit.Assert.fail;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitSupporter;

public class ByteBufferTest extends AbstractJunitSupporter {
	
	
	@Test
	public void test_wrap과allocate속도비교() {
		
		int retryCount = 1000000;
		int fixedLength = 16417;
		{
			long beforeTime= new Date().getTime();
			for (int i=0; i < retryCount; i++) {
				@SuppressWarnings("unused")
				ByteBuffer strByteBuffer = null;
				try {
					strByteBuffer = ByteBuffer.allocate(fixedLength);
				} catch (OutOfMemoryError e) {
					log.warn("OutOfMemoryError", e);
					throw e;
				}
			}
			
			long afterTime= new Date().getTime();
			
			log.info("{} 번 시간차={} ms, 평균={} ms", retryCount, (afterTime-beforeTime), (double)(afterTime-beforeTime)/retryCount);
		}
		
		{
			long beforeTime= new Date().getTime();
			for (int i=0; i < retryCount; i++) {
				byte srcBytes[] = new byte[fixedLength];
				@SuppressWarnings("unused")
				ByteBuffer strByteBuffer = ByteBuffer.wrap(srcBytes);
			}
			
			long afterTime= new Date().getTime();
			
			log.info("{} 번 시간차={} ms, 평균={} ms", retryCount, (afterTime-beforeTime), (double)(afterTime-beforeTime)/retryCount);
		}
		
	}
	
	@Test
	public void testPut_파라미터length가파라미터src로지정되는바이트배열의크기보다큰경우() {
		ByteBuffer streambuffer =  ByteBuffer.allocate(10);
		
		byte src[] = {0x32, 0x33, 0x34, 0x35};
		int offset = 0;
		int length = 7;
		try {
			streambuffer.put(src, offset, length);
			
			fail("no IndexOutOfBoundsException");
		} catch(IndexOutOfBoundsException e) {
			//log.warn(e.toString(), e);
		} catch(Exception e) {
			log.warn(e.toString(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPut_버퍼크기가파라미터src로지정되는바이트배열의크기보다작은경우() {
		ByteBuffer streambuffer =  ByteBuffer.allocate(2);
		
		byte src[] = {0x32, 0x33, 0x34, 0x35};
		int offset = 0;
		int length = 3;
		try {
			streambuffer.put(src, offset, length);
			
			fail("no BufferOverflowException");
		} catch(BufferOverflowException e) {
			//log.warn(e.toString(), e);
		} catch(Exception e) {
			log.warn(e.toString(), e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPut_길이를0으로지정할경우() {
		ByteBuffer streambuffer =  ByteBuffer.allocate(2);
		
		byte src[] = {0x32, 0x33, 0x34, 0x35};
		int offset = 1;
		int length = 0;
		try {
			streambuffer.put(src, offset, length);
		
			log.info("길이 0을 지정해도 문제 없음");
		} catch(Exception e) {
			log.warn("길이 0으로 지정하여 문제 발생", e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDuplicate_바이트오더도복제하는지여부() {
		// ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
		
		ByteBuffer streambuffer =  ByteBuffer.allocate(2);
		
		
		ByteOrder[] streamByteOrderList = { ByteOrder.BIG_ENDIAN, ByteOrder.LITTLE_ENDIAN };
		
		for (ByteOrder byteOrder : streamByteOrderList) {
			streambuffer.order(byteOrder);
			
			ByteBuffer dupBuffer = streambuffer.duplicate();
			
			if (byteOrder.equals(dupBuffer.order())) {
				log.warn("srcByteBuffer.order() is not same to dupByteBuff.order()");
				return;
			}
		}
		log.info("srcByteBuffer.order() is same to dupByteBuff.order()");
	}
	
	@Test
	public void testArray_유효한범위인지전체범위인지테스트() {
		byte src[] = {0x32, 0x33, 0x34, 0x35};
		ByteBuffer streambuffer =  ByteBuffer.wrap(src);
		streambuffer.get();
		streambuffer.get();
		
		byte result[] = streambuffer.array();
		
		if (src.length == result.length) {
			log.info("결과:전체, streambuffer={}", streambuffer.toString());
		} else {
			log.info("결과:유효한부분만, streambuffer={}", streambuffer.toString());
		}
	}
	
	@Test
	public void testReset_2번이상호출시에도마크한위치로이동하는지여부테스트() {
		ByteBuffer streambuffer = ByteBuffer.allocate(12);
		
		streambuffer.putInt(12);
		int markPosition = streambuffer.position();
		streambuffer.mark();
		
		// log.info("1.streambuffer={}", streambuffer.toString());
		
		streambuffer.putInt(15);
		// log.info("2.streambuffer={}", streambuffer.toString());
		
		streambuffer.reset();
		
		// log.info("3.streambuffer={}", streambuffer.toString());
		
		streambuffer.putLong(17L);
		
		// log.info("4.streambuffer={}", streambuffer.toString());
		
		streambuffer.reset();
		
		// log.info("5.streambuffer={}", streambuffer.toString());
		
		if (streambuffer.position() != markPosition) {
			fail("2번째 reset 호출시 마크 위치로 이동 안함");
		}
	}
}
