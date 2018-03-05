package javapackage.java.nio.charset;

import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Date;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;

public class CharsetTest extends AbstractJunitTest {

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
				// for (int k = 0; k < 8; k++) {
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
					// }
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

	@Test
	public void test4() {
		Charset myCharset = Charset.forName("utf8");
		
		StringBuilder testStringBuilder = new StringBuilder();

		for (int i = 0; i < 2500; i++) {
			testStringBuilder.append("한글");
		}

		int retryCount = 1000000;

		long beforeTime = new Date().getTime();

		for (int i = 0; i < retryCount; i++) {

			byte encodingBytes[] = testStringBuilder.toString().getBytes(myCharset);

			@SuppressWarnings("unused")
			String dst = new String(encodingBytes, myCharset);
		}

		long afterTime = new Date().getTime();

		log.info("반복 횟수={}, 시간차={} ms, 평균={} ms", retryCount, (afterTime - beforeTime),
				(double) (afterTime - beforeTime) / retryCount);
	}
}
