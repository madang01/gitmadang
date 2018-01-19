package kr.pe.sinnori.common.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType.LOG_TYPE;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

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
		// CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();

		DataPacketBufferPoolManager dataPacketBufferPoolManager = null;
		boolean isDirect = false;
		int dataPacketBufferSize = 512;
		int dataPacketBufferPoolSize = 15;
		// NewSocketOutputStream 
		
		ByteOrder streamByteOrder = ByteOrder.BIG_ENDIAN;
		
		try {
			dataPacketBufferPoolManager = DataPacketBufferPoolManager.DataPacketBufferPoolManagerBuilder
					.build(isDirect, streamByteOrder, dataPacketBufferSize, dataPacketBufferPoolSize);
		} catch (Exception e) {
			log.warn(""+e.getMessage(), e);
			fail("error");
		}
		
		SocketChannel ownerSocketChannel = null;
		
		NewSocketOutputStream sos = null;
		try {
			sos = 
					new NewSocketOutputStream(ownerSocketChannel, 
					streamCharsetDecoder, dataPacketBufferMaxCount, dataPacketBufferPoolManager);
			
			{
				long expectedSize = dataPacketBufferSize*3+42;
				sos.makeEmptySocketOutputStream(expectedSize);
				
				long actualSize = sos.getNumberOfWrittenBytes();
				
				assertEquals(expectedSize, actualSize);
			}			
			
			FreeSizeInputStream fsis = null;
			try {
				long oldSize = sos.getNumberOfWrittenBytes();
				
				long expectedSize = oldSize - dataPacketBufferSize - 24;
				fsis = sos.cutMessageInputStreamFromStartingPosition(expectedSize);
				
				log.info("fsis size={}", fsis.available());
				
				long actualSize = sos.getNumberOfWrittenBytes();
				
				assertEquals(oldSize - expectedSize, actualSize);
			} finally {
				if (null != fsis) {
					fsis.close();
				}				
			}			
		} catch (NoMoreDataPacketBufferException e) {
			fail("fail to get one data packet buffer from dataPacketBufferPoolManager");
		} finally {
			if (null != sos) {
				sos.close();
			}
		}
		
		
	}
}
