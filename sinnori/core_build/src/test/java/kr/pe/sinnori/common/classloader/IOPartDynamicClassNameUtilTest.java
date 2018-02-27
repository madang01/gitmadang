package kr.pe.sinnori.common.classloader;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitSupporter;
import kr.pe.sinnori.impl.message.Empty.Empty;
import kr.pe.sinnori.impl.message.Empty.EmptyClientCodec;
import kr.pe.sinnori.impl.message.Empty.EmptyDecoder;
import kr.pe.sinnori.impl.message.Empty.EmptyEncoder;
import kr.pe.sinnori.impl.message.Empty.EmptyServerCodec;
import kr.pe.sinnori.impl.task.client.EmptyClientTask;
import kr.pe.sinnori.impl.task.server.EmptyServerTask;

public class IOPartDynamicClassNameUtilTest extends AbstractJunitSupporter {

	@Test
	public void testAllIOPartDynamicClassFullNameIsValid() {
		String classLoaderClassPackagePrefixName = "kr.pe.sinnori.impl.";
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
