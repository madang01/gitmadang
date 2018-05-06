package kr.pe.codda.common.classloader;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.impl.message.Empty.Empty;
import kr.pe.codda.impl.message.Empty.EmptyClientCodec;
import kr.pe.codda.impl.message.Empty.EmptyDecoder;
import kr.pe.codda.impl.message.Empty.EmptyEncoder;
import kr.pe.codda.impl.message.Empty.EmptyServerCodec;
import kr.pe.codda.impl.task.client.EmptyClientTask;
import kr.pe.codda.impl.task.server.EmptyServerTask;

public class IOPartDynamicClassNameUtilTest extends AbstractJunitTest {

	/**
	 * 테스트용 {@link EmptyServerTask} 와 {@link EmptyClientTask} 이용
	 */
	@Test
	public void testAllIOPartDynamicClassFullNameIsValid() {
		String classLoaderClassPackagePrefixName = CommonStaticFinalVars.FIRST_PREFIX_OF_DYNAMIC_CLASS_FULL_NAME;
		IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = new IOPartDynamicClassNameUtil(classLoaderClassPackagePrefixName);

		String messageID = "Empty";
		String messageClassFullName = ioPartDynamicClassNameUtil.getMessageClassFullName(messageID);
		assertEquals(Empty.class.getCanonicalName(), messageClassFullName);
			
		String clientMessageCodecClassFullName = ioPartDynamicClassNameUtil.getClientMessageCodecClassFullName(messageID);		
		assertEquals(EmptyClientCodec.class.getCanonicalName(), clientMessageCodecClassFullName);
		
		String serverMessageCodecClassFullName = ioPartDynamicClassNameUtil.getServerMessageCodecClassFullName(messageID);		
		assertEquals(EmptyServerCodec.class.getCanonicalName(), serverMessageCodecClassFullName);		
		
		String messageEncoderClassFullName = ioPartDynamicClassNameUtil.getMessageEncoderClassFullName(messageID);
		assertEquals(EmptyEncoder.class.getCanonicalName(), messageEncoderClassFullName);
		
		String messageDecoderClassFullName = ioPartDynamicClassNameUtil.getMessageDecoderClassFullName(messageID);
		assertEquals(EmptyDecoder.class.getCanonicalName(), messageDecoderClassFullName);
		
		String clientTaskClassFullName = ioPartDynamicClassNameUtil.getClientTaskClassFullName(messageID);
		assertEquals(EmptyClientTask.class.getCanonicalName(), clientTaskClassFullName);
		
		String serverTaskClassFullName = ioPartDynamicClassNameUtil.getServerTaskClassFullName(messageID);
		assertEquals(EmptyServerTask.class.getCanonicalName(), serverTaskClassFullName);
	}
}
