package kr.pe.codda.common.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

public class DataPacketBufferPoolManagerTest extends AbstractJunitTest {

	@Test
	public void testPollDataPacketBuffer_NoMoreDataPacketBufferException() {
		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder dataPacketByteOrder = ByteOrder.LITTLE_ENDIAN;
		int dataPacketBufferSize = 4096;	
		int dataPacketBufferPoolSize = 1000;
		
		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect, 
					dataPacketByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}
		
		int warpBufferPoolSize = dataPacketBufferPool.getDataPacketBufferPoolSize();
		List<WrapBuffer> wrapBufferList = new ArrayList<WrapBuffer>();
		try {
			for (int i=0; i < warpBufferPoolSize; i++) {
				try {
					WrapBuffer wrapBuffer = dataPacketBufferPool.pollDataPacketBuffer();
					wrapBufferList.add(wrapBuffer);
				} catch (NoMoreDataPacketBufferException e) {
					fail("this code is a dead block but error::"+e.getMessage());
				}
			}
			
			try {
				dataPacketBufferPool.pollDataPacketBuffer();
				
				fail("no NoMoreDataPacketBufferException");
			} catch (NoMoreDataPacketBufferException e) {
				
			} catch (Exception e) {
				log.warn("", e);
				fail("unknown error::");
			}
		} finally {
			for (WrapBuffer wrapBuffer : wrapBufferList) {
				dataPacketBufferPool.putDataPacketBuffer(wrapBuffer);
			}
		}
		
	}
	
	@Test
	public void testPutDataPacketBuffer_addNotRegistedWrapBuffer() {
		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder dataPacketByteOrder = ByteOrder.LITTLE_ENDIAN;
		int dataPacketBufferSize = 4096;	
		int dataPacketBufferPoolSize = 1000;
		
		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect, 
					dataPacketByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}
		
		WrapBuffer notRegistedWrapBuffer = new WrapBuffer(isDirect, 1024, ByteOrder.BIG_ENDIAN);
		
		try {
			dataPacketBufferPool.putDataPacketBuffer(notRegistedWrapBuffer);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			String expectedMessage =  String.format("the parameter dataPacketBuffer[%d] is not a pool wrap buffer",
					notRegistedWrapBuffer.hashCode());		

			assertEquals(expectedMessage, errorMessage);
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}
		
		
	}
	
	@Test
	public void testPutDataPacketBuffer_addMoreThanTwice() {
		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder dataPacketByteOrder = ByteOrder.LITTLE_ENDIAN;
		int dataPacketBufferSize = 4096;	
		int dataPacketBufferPoolSize = 1000;
		
		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect, 
					dataPacketByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}
		
		WrapBuffer wrapBuffer = null;
		try {
			wrapBuffer = dataPacketBufferPool.pollDataPacketBuffer();
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}
		
		try {
			dataPacketBufferPool.putDataPacketBuffer(wrapBuffer);
		
			dataPacketBufferPool.putDataPacketBuffer(wrapBuffer);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			String expectedMessage =  String.format("the parameter dataPacketBuffer[%d] was added to the wrap buffer polling queue",
					wrapBuffer.hashCode());		

			assertEquals(expectedMessage, errorMessage);
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}		
	}	
	
	
	@Test
	public void testConstructor_theParameterByteOrder_null() {
		@SuppressWarnings("unused")
		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder dataPacketByteOrder = null;
		int dataPacketBufferSize = 1;	
		int dataPacketBufferPoolSize = 1000;
		
		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect, 
					dataPacketByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			String expectedMessage =  "the parameter dataPacketBufferByteOrder is null";	

			assertEquals(expectedMessage, errorMessage);
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}
	}
	
	@Test
	public void testConstructor_theParameterDataPacketBufferSize_lessThanOrEqualToZero() {
		@SuppressWarnings("unused")
		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder dataPacketByteOrder = ByteOrder.LITTLE_ENDIAN;
		int dataPacketBufferSize = 0;	
		int dataPacketBufferPoolSize = 1000;
		
		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect, 
					dataPacketByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			String expectedMessage =  String.format("the parameter dataPacketBufferSize[%d] is less than or equal to zero", dataPacketBufferSize);	

			assertEquals(expectedMessage, errorMessage);
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}
		
		dataPacketBufferSize = -1;
		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect, 
					dataPacketByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			String expectedMessage =  String.format("the parameter dataPacketBufferSize[%d] is less than or equal to zero", dataPacketBufferSize);	

			assertEquals(expectedMessage, errorMessage);
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}
	}
	
	@Test
	public void testConstructor_theParameterDataPacketBufferPoolSize_lessThanOrEqualToZero() {
		@SuppressWarnings("unused")
		DataPacketBufferPool dataPacketBufferPool = null;
		boolean isDirect = false;
		ByteOrder dataPacketByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;	
		int dataPacketBufferPoolSize = 0;
		
		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect, 
					dataPacketByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			String expectedMessage =  String.format("the parameter dataPacketBufferPoolSize[%d] is less than or equal to zero", dataPacketBufferPoolSize);	

			assertEquals(expectedMessage, errorMessage);
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}
		
		dataPacketBufferPoolSize = -1;
		
		try {
			dataPacketBufferPool = new DataPacketBufferPool(isDirect, 
					dataPacketByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			String expectedMessage =  String.format("the parameter dataPacketBufferPoolSize[%d] is less than or equal to zero", dataPacketBufferPoolSize);	

			assertEquals(expectedMessage, errorMessage);
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}
	}	
	
}
