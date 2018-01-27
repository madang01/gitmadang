package javapackage.java.nio.charset;

import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType.LOG_TYPE;

public class CharsetTest {

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

		log = LoggerFactory.getLogger(CharsetTest.class);
	}

	@After
	public void finish() {
		System.gc();
	}

	@Test
	public void test() {
		StringBuilder testStringBuilder = new StringBuilder();

		for (int i = 0; i < 2500; i++) {
			testStringBuilder.append("한글");
		}

		String src = testStringBuilder.toString();

		Charset myCharset = Charset.forName("utf8");
		CharsetEncoder charsetEncoder = myCharset.newEncoder();
		CharsetDecoder charsetDecoder = myCharset.newDecoder();

		// String errorMessage = selfExnReq.getErrorMessage();

		// byte[] t = selfExnReq.getErrorMessage().getBytes(myCharset);

		// utf8:16417
		int retryCount = 1000000;

		int numberOfEncodingBytes = 0;
		long beforeTime = new Date().getTime();

		for (int i = 0; i < retryCount; i++) {

			{
				ByteBuffer srcByteBuffer = null;
				try {
					srcByteBuffer = charsetEncoder.encode(CharBuffer.wrap(
							"1죽는 날까지 하늘을 우러러 한 점 부끄럼이 없기를, 잎새에 이는 바람에도 나는 괴로워했다. 별을 노래하는 마음으로 모든 죽어 가는 것을 사랑해야지."));
				} catch (CharacterCodingException e) {
					log.warn(e.getMessage(), e);
					fail("fail to call CharsetEncoder.encode");
				}
				// ByteBuffer srcByteBuffer =
				// ByteBuffer.wrap(selfExnReq.getErrorPlace().getBytes(myCharset));

				numberOfEncodingBytes += srcByteBuffer.remaining();

				CharBuffer dstCharBuffer = null;
				try {
					dstCharBuffer = charsetDecoder.decode(srcByteBuffer);
				} catch (CharacterCodingException e) {
					log.warn(e.getMessage(), e);
					fail("fail to call CharsetDecoder.decode");
				}
				@SuppressWarnings("unused")
				String dst = dstCharBuffer.toString();

				// assertEquals("원본 문자열과 인코딩과 디코딩 과정후 얻은 문자열 비교", src, dst);
			}

			{
				ByteBuffer srcByteBuffer = null;
				try {
					srcByteBuffer = charsetEncoder.encode(CharBuffer.wrap(
							"2죽는 날까지 하늘을 우러러 한 점 부끄럼이 없기를, 잎새에 이는 바람에도 나는 괴로워했다. 별을 노래하는 마음으로 모든 죽어 가는 것을 사랑해야지."));
				} catch (CharacterCodingException e) {
					log.warn(e.getMessage(), e);
					fail("fail to call CharsetEncoder.encode");
				}
				// ByteBuffer srcByteBuffer =
				// ByteBuffer.wrap(selfExnReq.getErrorPlace().getBytes(myCharset));

				numberOfEncodingBytes += srcByteBuffer.remaining();

				CharBuffer dstCharBuffer = null;
				try {
					dstCharBuffer = charsetDecoder.decode(srcByteBuffer);
				} catch (CharacterCodingException e) {
					log.warn(e.getMessage(), e);
					fail("fail to call CharsetDecoder.decode");
				}
				@SuppressWarnings("unused")
				String dst = dstCharBuffer.toString();

				// assertEquals("원본 문자열과 인코딩과 디코딩 과정후 얻은 문자열 비교", src, dst);
			}

			{
				ByteBuffer srcByteBuffer = null;
				try {
					srcByteBuffer = charsetEncoder.encode(CharBuffer.wrap(
							"3죽는 날까지 하늘을 우러러 한 점 부끄럼이 없기를, 잎새에 이는 바람에도 나는 괴로워했다. 별을 노래하는 마음으로 모든 죽어 가는 것을 사랑해야지."));
				} catch (CharacterCodingException e) {
					log.warn(e.getMessage(), e);
					fail("fail to call CharsetEncoder.encode");
				}
				// ByteBuffer srcByteBuffer =
				// ByteBuffer.wrap(selfExnReq.getErrorPlace().getBytes(myCharset));

				numberOfEncodingBytes += srcByteBuffer.remaining();

				CharBuffer dstCharBuffer = null;
				try {
					dstCharBuffer = charsetDecoder.decode(srcByteBuffer);
				} catch (CharacterCodingException e) {
					log.warn(e.getMessage(), e);
					fail("fail to call CharsetDecoder.decode");
				}
				@SuppressWarnings("unused")
				String dst = dstCharBuffer.toString();

				// assertEquals("원본 문자열과 인코딩과 디코딩 과정후 얻은 문자열 비교", src, dst);
			}

			{
				ByteBuffer srcByteBuffer = null;
				try {
					srcByteBuffer = charsetEncoder.encode(CharBuffer.wrap(src));
				} catch (CharacterCodingException e) {
					log.warn(e.getMessage(), e);
					fail("fail to call CharsetEncoder.encode");
				}
				// ByteBuffer srcByteBuffer =
				// ByteBuffer.wrap(selfExnReq.getErrorPlace().getBytes(myCharset));

				numberOfEncodingBytes += srcByteBuffer.remaining();

				CharBuffer dstCharBuffer = null;
				try {
					dstCharBuffer = charsetDecoder.decode(srcByteBuffer);
				} catch (CharacterCodingException e) {
					log.warn(e.getMessage(), e);
					fail("fail to call CharsetDecoder.decode");
				}
				@SuppressWarnings("unused")
				String dst = dstCharBuffer.toString();

				// assertEquals("원본 문자열과 인코딩과 디코딩 과정후 얻은 문자열 비교", src, dst);
			}

		}

		long afterTime = new Date().getTime();

		log.info("인코딩 데이터 크기={}::반복 횟수={}, 시간차={} ms, 평균={} ms", numberOfEncodingBytes, retryCount,
				(afterTime - beforeTime), (double) (afterTime - beforeTime) / retryCount);
	}

	@Test
	public void test2() {
		StringBuilder testStringBuilder = new StringBuilder();

		for (int i = 0; i < 5; i++) {
			testStringBuilder.append("한글");
		}

		String src = testStringBuilder.toString();

		Charset myCharset = Charset.forName("utf8");
		CharsetEncoder charsetEncoder = myCharset.newEncoder();
		CharsetDecoder charsetDecoder = myCharset.newDecoder();

		int retryCount = 1000000;

		int numberOfEncodingBytes = 0;
		long beforeTime = new Date().getTime();

		for (int i = 0; i < retryCount; i++) {
			for (int j = 0; j < 8; j++) {
				//for (int k = 0; k < 8; k++) {
					{
						ByteBuffer srcByteBuffer = null;
						try {
							srcByteBuffer = charsetEncoder.encode(CharBuffer.wrap(src));
						} catch (CharacterCodingException e) {
							log.warn(e.getMessage(), e);
							fail("fail to call CharsetEncoder.encode");
						}

						numberOfEncodingBytes = srcByteBuffer.remaining();

						CharBuffer dstCharBuffer = null;
						try {
							dstCharBuffer = charsetDecoder.decode(srcByteBuffer);
						} catch (CharacterCodingException e) {
							log.warn(e.getMessage(), e);
							fail("fail to call CharsetDecoder.decode");
						}
						@SuppressWarnings("unused")
						String dst = dstCharBuffer.toString();

						// assertEquals("원본 문자열과 인코딩과 디코딩 과정후 얻은 문자열 비교", src, dst);
					//}
				}
			}

		}

		long afterTime = new Date().getTime();

		log.info("인코딩 데이터 크기={}::반복 횟수={}, 시간차={} ms, 평균={} ms", numberOfEncodingBytes, retryCount,
				(afterTime - beforeTime), (double) (afterTime - beforeTime) / retryCount);
	}

	@Test
	public void test3() {
		StringBuilder testStringBuilder = new StringBuilder();

		for (int i = 0; i < 5; i++) {
			testStringBuilder.append("한글");
		}

		String src = testStringBuilder.toString();

		Charset myCharset = Charset.forName("utf8");
		// CharsetEncoder charsetEncoder = myCharset.newEncoder();
		// CharsetDecoder charsetDecoder = myCharset.newDecoder();

		int retryCount = 1000000;

		int numberOfEncodingBytes = 0;
		long beforeTime = new Date().getTime();

		for (int i = 0; i < retryCount; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 8; k++) {
					{
						byte encodingBytes[] = src.getBytes(myCharset);

						numberOfEncodingBytes = encodingBytes.length;

						@SuppressWarnings("unused")
						String dst = new String(encodingBytes, myCharset);

						// assertEquals("원본 문자열과 인코딩과 디코딩 과정후 얻은 문자열 비교", src, dst);
					}
				}
			}
		}

		long afterTime = new Date().getTime();

		log.info("인코딩 데이터 크기={}::반복 횟수={}, 시간차={} ms, 평균={} ms", numberOfEncodingBytes, retryCount,
				(afterTime - beforeTime), (double) (afterTime - beforeTime) / retryCount);
	}
}
