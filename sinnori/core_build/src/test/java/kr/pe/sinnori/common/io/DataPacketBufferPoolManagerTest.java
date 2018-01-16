package kr.pe.sinnori.common.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType.LOG_TYPE;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

public class DataPacketBufferPoolManagerTest {
	Logger log = null;
	
	
	@Before
	public void setup() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_base";
		LOG_TYPE logType = LOG_TYPE.SERVER;
		String logbackConfigFilePathString = BuildSystemPathSupporter.getLogbackConfigFilePathString(sinnoriInstalledPathString, mainProjectName);
		String sinnoriLogPathString = BuildSystemPathSupporter.getLogPathString(sinnoriInstalledPathString, mainProjectName, logType);
		
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				mainProjectName);
		
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_LOG_PATH,
				sinnoriLogPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOGBACK_CONFIG_FILE,
				logbackConfigFilePathString);

		// SinnoriLogbackManger.getInstance().setup(sinnoriInstalledPathString, mainProjectName, logType);
		log = LoggerFactory.getLogger(DataPacketBufferPoolManagerTest.class);
	}
	
	@Test
	public void testPollDataPacketBuffer_NoMoreDataPacketBufferException() {
		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder dataPacketByteOrder = ByteOrder.LITTLE_ENDIAN;
		int dataPacketBufferSize = 4096;	
		int dataPacketBufferPoolSize = 1000;
		
		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
					.build(isDirect, dataPacketByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}
		
		int warpBufferPoolSize = dataPacketBufferPoolManager.getDataPacketBufferPoolSize();
		List<WrapBuffer> wrapBufferList = new ArrayList<WrapBuffer>();
		try {
			for (int i=0; i < warpBufferPoolSize; i++) {
				try {
					WrapBuffer wrapBuffer = dataPacketBufferPoolManager.pollDataPacketBuffer();
					wrapBufferList.add(wrapBuffer);
				} catch (NoMoreDataPacketBufferException e) {
					fail("this code is a dead block but error::"+e.getMessage());
				}
			}
			
			try {
				dataPacketBufferPoolManager.pollDataPacketBuffer();
				
				fail("no NoMoreDataPacketBufferException");
			} catch (NoMoreDataPacketBufferException e) {
				
			} catch (Exception e) {
				log.warn("", e);
				fail("unknown error::");
			}
		} finally {
			for (WrapBuffer wrapBuffer : wrapBufferList) {
				dataPacketBufferPoolManager.putDataPacketBuffer(wrapBuffer);
			}
		}
		
	}
	
	@Test
	public void testPutDataPacketBuffer_addNotRegistedButQueueInWrapBuffer() {
		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder dataPacketByteOrder = ByteOrder.LITTLE_ENDIAN;
		int dataPacketBufferSize = 4096;	
		int dataPacketBufferPoolSize = 1000;
		
		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
					.build(isDirect, dataPacketByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}
		
		WrapBuffer notRegistedWrapBuffer = new WrapBuffer(isDirect, 1024, ByteOrder.BIG_ENDIAN);		
		
		notRegistedWrapBuffer.queueIn();
		try {
			dataPacketBufferPoolManager.putDataPacketBuffer(notRegistedWrapBuffer);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			String expectedMessage =  String.format("the parameter dataPacketBuffer[%d] was added to the wrap buffer polling queue",
					notRegistedWrapBuffer.hashCode());			

			assertEquals(expectedMessage, errorMessage);
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}
	}
	
	@Test
	public void testPutDataPacketBuffer_addNotRegistedButQueueOutWrapBuffer() {
		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder dataPacketByteOrder = ByteOrder.LITTLE_ENDIAN;
		int dataPacketBufferSize = 4096;	
		int dataPacketBufferPoolSize = 1000;
		
		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
					.build(isDirect, dataPacketByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}
		
		WrapBuffer notRegistedWrapBuffer = new WrapBuffer(isDirect, 1024, ByteOrder.BIG_ENDIAN);
		notRegistedWrapBuffer.queueOut();
		try {
			dataPacketBufferPoolManager.putDataPacketBuffer(notRegistedWrapBuffer);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			
			String expectedMessage =  String.format("the parameter dataPacketBuffer[%d] is not a element of the queue-out state set",
					notRegistedWrapBuffer.hashCode());			

			assertEquals(expectedMessage, errorMessage);
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}
		
		
	}
	
	@Test
	public void testPutDataPacketBuffer_putMoreThanTwice() {
		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder dataPacketByteOrder = ByteOrder.LITTLE_ENDIAN;
		int dataPacketBufferSize = 4096;	
		int dataPacketBufferPoolSize = 1000;
		
		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
					.build(isDirect, dataPacketByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}
		
		WrapBuffer wrapBuffer = null;
		try {
			wrapBuffer = dataPacketBufferPoolManager.pollDataPacketBuffer();
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}
		
		try {
			dataPacketBufferPoolManager.putDataPacketBuffer(wrapBuffer);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail("error");
		}
		
		try {
			dataPacketBufferPoolManager.putDataPacketBuffer(wrapBuffer);
			
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
		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder dataPacketByteOrder = null;
		int dataPacketBufferSize = 1;	
		int dataPacketBufferPoolSize = 1000;
		
		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
					.build(isDirect, dataPacketByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			
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
		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder dataPacketByteOrder = ByteOrder.LITTLE_ENDIAN;
		int dataPacketBufferSize = 0;	
		int dataPacketBufferPoolSize = 1000;
		
		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
					.build(isDirect, dataPacketByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			
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
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
					.build(isDirect, dataPacketByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			
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
		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		ByteOrder dataPacketByteOrder = ByteOrder.BIG_ENDIAN;
		int dataPacketBufferSize = 1;	
		int dataPacketBufferPoolSize = 0;
		
		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
					.build(isDirect, dataPacketByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			
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
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
					.build(isDirect, dataPacketByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
			
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
