package kr.pe.sinnori.common.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType.LOG_TYPE;

public class SocketOutputStreamTest {
	Logger log = null;

	@Before
	public void setup() {
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		String mainProjectName = "sample_base";
		LOG_TYPE logType = LOG_TYPE.SERVER;
		String logbackConfigFilePathString = BuildSystemPathSupporter
				.getLogbackConfigFilePathString(sinnoriInstalledPathString, mainProjectName);
		String sinnoriLogPathString = BuildSystemPathSupporter.getLogPathString(sinnoriInstalledPathString,
				mainProjectName, logType);

		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				mainProjectName);

		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_LOG_PATH, sinnoriLogPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOGBACK_CONFIG_FILE,
				logbackConfigFilePathString);

		// SinnoriLogbackManger.getInstance().setup(sinnoriInstalledPathString,
		// mainProjectName, logType);

		log = LoggerFactory.getLogger(SocketOutputStreamTest.class);
	}
	
	@After
	public void finish() {
		System.gc();
	}
	
	
	@Test
	public void testCutMessageInputStreamFromStartingPosition_basic() {
		int dataPacketBufferMaxCount = 15;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 512;
		int dataPacketBufferPoolSize = 15;
		// SocketOutputStream 
		
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		
		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
					.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
		}
		
		
		SocketOutputStream sos = null;
		FreeSizeOutputStream fsos = null;
		try {
			{
				long expectedSize = dataPacketBufferSize*3+42;
				{
					// sos.makeEmptySocketOutputStream(expectedSize);
					
					List<WrapBuffer> emptyOutputStreamWrapBufferList = null;
					fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
							dataPacketBufferPoolManager);
					
					fsos.skip(expectedSize);
					emptyOutputStreamWrapBufferList = fsos.getOutputStreamWrapBufferList();
					sos = new SocketOutputStream(emptyOutputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPoolManager);
				}
				
				long actualSize = sos.getNumberOfWrittenBytesUsingList();
				
				assertEquals(expectedSize, actualSize);
			}			
			
			FreeSizeInputStream fsis = null;
			try {
				long oldSize = sos.getNumberOfWrittenBytesUsingList();
				
				long expectedSize = oldSize - dataPacketBufferSize - 24;
				fsis = sos.cutMessageInputStreamFromStartingPosition(expectedSize);
				
				//log.info("fsis size={}", fsis.available());
				// log.info("sos size={}", sos.size());
				
				long actualSize = sos.getNumberOfWrittenBytesUsingList();
				
				assertEquals(oldSize - expectedSize, actualSize);
			} finally {
				if (null != fsis) {
					fsis.close();
				}				
			}			
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::"+e.getMessage());
		} finally {
			if (null != sos) {
				sos.close();
			}
		}
	}
	
	// FIXME!
	@Test
	public void testCutMessageInputStreamFromStartingPosition_complex() {
		int dataPacketBufferMaxCount = 15;
		Charset streamCharset = Charset.forName("utf-8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 6;
		int dataPacketBufferPoolSize = 15;
		// SocketOutputStream 
		
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		
		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
					.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
		}
		
		final int integerTypeExpectedValueList[] = { 
				0x11223344, 
				0x55667788,
				0x99aabbcc,
				0xddeeff00
			};	
		
		final byte eofByte = (byte) 0xc1;
		 
		
		SocketOutputStream sos = null;
		
		FreeSizeOutputStream fsos = null;
		List<WrapBuffer> outputStreamWrapBufferListForTest = null;
		try {
			fsos = new FreeSizeOutputStream(dataPacketBufferMaxCount, streamCharsetEncoder,
					dataPacketBufferPoolManager);
			
			for (int expectedValue : integerTypeExpectedValueList) {
				fsos.putInt(expectedValue);
			}

			
			fsos.putByte(eofByte);
			
			outputStreamWrapBufferListForTest = fsos.getOutputStreamWrapBufferList();
			
			sos = 
					new SocketOutputStream(outputStreamWrapBufferListForTest, streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPoolManager);
			
			// log.info("sos.size={}", sos.size());
			FreeSizeInputStream fsis = null;
			for (int i=0; i < integerTypeExpectedValueList.length; i++) {
				int expectedValue = integerTypeExpectedValueList[i];
				try {
					fsis = sos.cutMessageInputStreamFromStartingPosition(4);
					int actualValue = fsis.getInt();
					
					assertEquals(String.format("integerTypeExpectedValueList's index[%d]'s value[%d] vs  actualValue[%d] 비교", i, expectedValue, actualValue), expectedValue, actualValue);				
				} finally {
					if (null != fsis) {
						fsis.close();
					}				
				}
			}
			
			if (1 != sos.getSocketOutputStreamWrapBufferList().size()) {
				fail(String.format("socketOutputStreamWrapBufferList size[%d] is not equal to one", 
						sos.getSocketOutputStreamWrapBufferList().size()));
			}
			
			try {
				fsis = sos.cutMessageInputStreamFromStartingPosition(1);
				byte actualValue = fsis.getByte();
				
				assertEquals(eofByte, actualValue);				
			} finally {
				if (null != fsis) {
					fsis.close();
				}				
			}
			
			if (0L != sos.size()) {
				fail("1. socketOutputStream is not empty");
			}
			
			if (0L != sos.getNumberOfWrittenBytesUsingList()) {
				fail("2. socketOutputStream is not empty");
			}
			
			if (0 != sos.getSocketOutputStreamWrapBufferList().size()) {
				fail(String.format("socketOutputStreamWrapBufferList size[%d] is not equal to zero", 
						sos.getSocketOutputStreamWrapBufferList().size()));
			}
			
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("unknown error::" + e.getMessage());
		} finally {
			if (null != sos) {
				sos.close();
			}
		}
	}
}
