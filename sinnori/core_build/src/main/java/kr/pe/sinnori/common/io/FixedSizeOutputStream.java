/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.pe.sinnori.common.io;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 고정 크기를 갖는 이진 출력 스트림.
 * 
 * @author Won Jonghoon
 * 
 */
public class FixedSizeOutputStream implements OutputStreamIF {
	private Logger log = LoggerFactory.getLogger(FixedSizeOutputStream.class);
	/**
	 * 출력을 담을 ByteBuffer.
	 */
	private ByteBuffer outputStreamBuffer = null;

	private Charset streamCharset = null;
	private CharsetEncoder streamCharsetEncoder = null;
	private ByteOrder bufferByteOrder = null;

	private ByteBuffer intBuffer = null;
	private ByteBuffer longBuffer = null;
	
	/**
	 * 생성자
	 * @param outputStreamBuffer 출력 스트림으로 사용할 바이트 버퍼
	 * @param streamCharset 출력 스트림 문자셋
	 * @param streamCharsetEncoder  출력 스트림 문자셋 인코더
	 */
	public FixedSizeOutputStream(ByteBuffer outputStreamBuffer, Charset streamCharset, 
			CharsetEncoder streamCharsetEncoder) {
		if (null == outputStreamBuffer) {
			throw new IllegalArgumentException("파라미터 바이트 버퍼 값이 null 입니다.");
		}

		if (null == streamCharsetEncoder) {
			throw new IllegalArgumentException("파라미터 문자셋의 인코더 값이 null 입니다.");
		}

		this.outputStreamBuffer = outputStreamBuffer;
		this.streamCharset = streamCharset;
		this.streamCharsetEncoder = streamCharsetEncoder;
		this.bufferByteOrder = outputStreamBuffer.order();

		intBuffer = ByteBuffer.allocate(4);
		intBuffer.order(bufferByteOrder);

		longBuffer = ByteBuffer.allocate(8);
		longBuffer.order(bufferByteOrder);
	}

	

	/**
	 * 소스 바이트 버퍼 초기화, 즉 스트림 초기화.
	 */
	public void clear() {
		outputStreamBuffer.clear();
	}

	/**
	 * unsigned short 을 위한 integer 버퍼에 값을 저장훈 Integer 버퍼를 반환한다.
	 * 
	 * @param value
	 *            unsigned short 타입에 대응하는 값
	 * @return 값이 저장된 unsigned short 을 위한 integer 버퍼
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터를 입력하여 발생한다.
	 */
	protected ByteBuffer getIntegerBufferForUnsignedShort(int value)
			throws IllegalArgumentException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]은 음수입니다.", value));
		}

		if (value > CommonStaticFinalVars.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]은 unsigned short 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.MAX_UNSIGNED_SHORT));
		}

		intBuffer.clear();
		intBuffer.putInt(value);

		if (ByteOrder.BIG_ENDIAN == bufferByteOrder) {
			intBuffer.position(2);
			intBuffer.limit(4);
		} else {
			intBuffer.position(0);
			intBuffer.limit(2);
		}
		return intBuffer;
	}

	/**
	 * unsigned integer 를 위한 long 버퍼에 값을 저장후 long 버퍼를 반환한다.
	 * 
	 * @param value
	 *            unsigned integer 타입에 대응하는 값
	 * @return 값이 저장된 unsigned integer 를 위한 long 버퍼
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터를 입력하여 발생한다.
	 */
	private ByteBuffer getLongBufferForUnsignedInt(long value)
			throws IllegalArgumentException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]은 음수입니다.", value));
		}
		if (value > CommonStaticFinalVars.MAX_UNSIGNED_INTEGER) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]은 unsigned int 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.MAX_UNSIGNED_INTEGER));
		}

		longBuffer.clear();
		longBuffer.putLong(value);

		if (ByteOrder.BIG_ENDIAN == bufferByteOrder) {
			longBuffer.position(4);
			longBuffer.limit(8);
		} else {
			longBuffer.position(0);
			longBuffer.limit(4);
		}
		return longBuffer;
	}
	
	@Override
	public Charset getCharset() {
		return streamCharset;
	}

	@Override
	public void putByte(byte value) throws BufferOverflowException {
		outputStreamBuffer.put(value);
	}

	@Override
	public void putUnsignedByte(short value) throws BufferOverflowException,
			IllegalArgumentException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]이 음수입니다.", value));
		}

		if (value > CommonStaticFinalVars.MAX_UNSIGNED_BYTE) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]이 unsigned byte 최대값[%d]을 넘었습니다.", value,
					CommonStaticFinalVars.MAX_UNSIGNED_BYTE));
		}
		/*ByteBuffer int_buffer = ByteBuffer.allocate(2);
		int_buffer.order(bufferByteOrder);
		int_buffer.putShort((short) value);

		if (ByteOrder.BIG_ENDIAN == bufferByteOrder) {
			int_buffer.position(1);
			int_buffer.limit(2);
		} else {
			int_buffer.position(0);
			int_buffer.limit(1);
		}
		outputStreamBuffer.put(int_buffer);*/
		
		/**
		 * 우분투 jdk 1.6.x에서 테스트한 시스템 디폴트 ByteOrder는 LITTLE_ENDIAN 로 정수(=Integer)
		 * 0xff 를 byte 형 변환하면 0xff 이다.
		 */
		putByte((byte) value);
	}
	
	@Override
	public void putUnsignedByte(int value) throws BufferOverflowException, IllegalArgumentException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]이 음수입니다.", value));
		}
		
		if (value > CommonStaticFinalVars.MAX_UNSIGNED_BYTE) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]이 unsigned byte 최대값[%d]을 넘었습니다.", value,
					CommonStaticFinalVars.MAX_UNSIGNED_BYTE));
		}

		/*ByteBuffer int_buffer = ByteBuffer.allocate(2);
		int_buffer.order(bufferByteOrder);
		int_buffer.putShort((short) value);
		
		if (ByteOrder.BIG_ENDIAN == bufferByteOrder) {
			int_buffer.position(1);
			int_buffer.limit(2);
		} else {
			int_buffer.position(0);
			int_buffer.limit(1);
		}
		outputStreamBuffer.put(int_buffer);*/
		
		/**
		 * 우분투 jdk 1.6.x에서 테스트한 시스템 디폴트 ByteOrder는 LITTLE_ENDIAN 로 정수(=Integer)
		 * 0xff 를 byte 형 변환하면 0xff 이다.
		 */
		putByte((byte) value);
	}

	@Override
	public void putShort(short value) throws BufferOverflowException {
		outputStreamBuffer.putShort(value);
	}

	@Override
	public void putUnsignedShort(int value) throws BufferOverflowException,
			IllegalArgumentException {
		ByteBuffer unsingedShortBuffer = getIntegerBufferForUnsignedShort(value);
		outputStreamBuffer.put(unsingedShortBuffer);
	}

	@Override
	public void putInt(int value) throws BufferOverflowException {
		outputStreamBuffer.putInt(value);
	}

	@Override
	public void putUnsignedInt(long value) throws BufferOverflowException,
			IllegalArgumentException {
		ByteBuffer unsingedIntBuffer = getLongBufferForUnsignedInt(value);
		outputStreamBuffer.put(unsingedIntBuffer);
	}

	@Override
	public void putLong(long value) throws BufferOverflowException {
		outputStreamBuffer.putLong(value);
	}

	@Override
	public void putString(int len, String str, CharsetEncoder clientEncoder)
			throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException {
		if (len < 0) {
			throw new IllegalArgumentException(String.format(
					"파리미터 문자열 길이(=len)의 값[%d]은  0 보다 크거나 같아야 합니다.", len));
		}

		if (len > CommonStaticFinalVars.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(
					String.format(
							"파리미터 문자열 길이(=len)의 값[%d]은  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							len, CommonStaticFinalVars.MAX_UNSIGNED_SHORT));
		}

		if (str == null) {
			throw new IllegalArgumentException("파리미터 문자열(=str)의 값이 null 입니다.");
		}

		if (clientEncoder == null) {
			throw new IllegalArgumentException(
					"파리미터 문자셋(=clientEncoder)의 값이 null 입니다.");
		}

		int cur_pos = outputStreamBuffer.position();
		int cur_limit = outputStreamBuffer.limit();

		if (cur_limit < (cur_pos + len)) {
			throw new IllegalArgumentException(String.format(
					"남아 있는 저장 가능한 영역의 크기[%d]가 지정된 문자열[%d] 크기 보다 작습니다.",
					cur_limit - cur_pos, len));
		}

		/**
		 * limit 속성을 position 속성에서 len 만큼 확보한 위치로 이동을 한다. 이렇게 하는 목적은
		 * CharsetEncoder.encode 메소드의 특성을 이용하고자 함이다.
		 * 
		 * CharsetEncoder.encode 메소드는 1개의 문자가 온전하게 "출력 바이트 버퍼"에 저장될수 없다면 해당 문자를
		 * 저장하지 않는다.
		 * 
		 * 예) "a한꾬b"를 EUC-KR 로 길이 2에 저장할때, 출력 바이트 버퍼에는 "a"만 저장된다.
		 * 
		 * 참고1) 신놀이 서버는 문자셋 인코딩/디코딩시 2가지 예외에 대한 처리를 문자 대체로 한다. (1) 매핑 불가능한 문자에
		 * 대한 처리 방식(CharsetEncoder.onUnmappableCharacter) (2) 알수없는 문자에 대한 처리
		 * 방식(=CharsetEncoder.onMalformedInput)
		 * 
		 * 따라서, 위의 예에서 길이 3로 하면 "a한"로 저장된다. 길이 4로 하면 "a한?"로 저장된다. 이는
		 * "매핑 불가능한 문자"인 '꾬' 자가 대체문자인 '?' 자로 변환되었기때문이다.
		 * 
		 * 참고2) 자바 내부적인 문자열에 대한 인코딩시 "알수없는 문자"는 발생할수가 없다. 오직 디코딩시에만 발생한다.
		 */
		outputStreamBuffer.limit(cur_pos + len);

		clientEncoder.encode(CharBuffer.wrap(str), outputStreamBuffer, true);

		/**
		 * 파라미터로 넘어온 len 길이중 문자열을 저장하고 남은 영역만큼 영(=0x00) 값을 넣는다. 쓰레기 값이 읽혀지는것을
		 * 방지하기 위해서 넣는 방어 코드임.
		 */
		while (outputStreamBuffer.hasRemaining()) {
			outputStreamBuffer.put((byte) 0x00);
		}

		/**
		 * 백업 받은 limit 속성을 복구한다.
		 */
		outputStreamBuffer.limit(cur_limit);
	}

	@Override
	public void putString(int len, String str) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		putString(len, str, streamCharsetEncoder);
	}

	@Override
	public void putStringAll(String str) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (str == null) {
			throw new IllegalArgumentException("파리미터 문자열(=str)의 값이 null 입니다.");
		}

		int len = str.getBytes(streamCharset).length;
		if (len > CommonStaticFinalVars.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(
					String.format(
							"파리미터 문자열 길이(=len)의 값[%d]은  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							len, CommonStaticFinalVars.MAX_UNSIGNED_SHORT));
		}

		int numOfBytes = str.getBytes(streamCharset).length;
		putString(numOfBytes, str, streamCharsetEncoder);
	}

	@Override
	public void putPascalString(String str) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		putUBPascalString(str);
	}

	@Override
	public void putSIPascalString(String str) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (str == null) {
			throw new IllegalArgumentException("파리미터 문자열(=str)의 값이 null 입니다.");
		}

		byte strBytes[] = str.getBytes(streamCharset);

		if (strBytes.length > CommonStaticFinalVars.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(
					String.format(
							"파리미터 문자열 길이(=len)의 값[%d]은  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							strBytes.length,
							CommonStaticFinalVars.MAX_UNSIGNED_SHORT));
		}

		putInt(strBytes.length);
		putBytes(strBytes);
	}

	@Override
	public void putUSPascalString(String str) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (str == null) {
			throw new IllegalArgumentException("파리미터 문자열(=str)의 값이 null 입니다.");
		}
		byte strBytes[] = str.getBytes(streamCharset);
		putUnsignedShort(strBytes.length);
		putBytes(strBytes);
	}

	@Override
	public void putUBPascalString(String str) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (str == null) {
			throw new IllegalArgumentException("파리미터 문자열(=str)의 값이 null 입니다.");
		}

		byte strBytes[] = str.getBytes(streamCharset);
		/*
		if (strBytes.length > CommonStaticFinal.MAX_UNSIGNED_BYTE) {
			throw new IllegalArgumentException(String.format(
					"파라미터 문자열의 길이[%d]는 unsigned byte 최대값[%d]을 넘을 수 없습니다.", strBytes.length, CommonStaticFinal.MAX_UNSIGNED_BYTE));
		}
		*/
		
		putUnsignedByte(strBytes.length);
		putBytes(strBytes);
	}

	@Override
	public void putBytes(byte[] dstBuffer, int offset, int length)
			throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException {
		if (dstBuffer == null) {
			throw new IllegalArgumentException(
					"파리미터 목적지 바이트 배열(=dstBuffer)의 값이 null 입니다.");
		}

		if (offset < 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 옵셋[%d]은  0 보다 커야 합니다.", offset));
		}

		if (length < 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 길이[%d]는  0 보다 커야 합니다.", length));
		}

		if (length > CommonStaticFinalVars.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(
					String.format(
							"파리미터 목적지 바이트 배열 길이[%d]는  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							length, CommonStaticFinalVars.MAX_UNSIGNED_SHORT));
		}

		long remainingBytes = remaining();
		if (length > remainingBytes) {
			throw new IllegalArgumentException(String.format(
					"파라미터 길이[%d]는 남아 있은 버퍼 크기[%d] 보다 작거나 같아야 합니다.", length,
					remainingBytes));
		}

		if (dstBuffer.length > 0 && offset >= dstBuffer.length) {
			throw new IllegalArgumentException(String.format(
					"파라미터 옵셋[%d]는 목적지 버퍼 크기[%d] 보다 작아야 합니다.", offset,
					dstBuffer.length));
		}

		if (length > dstBuffer.length) {
			throw new IllegalArgumentException(String.format(
					"지정된 길이[%d]는  목적지 버퍼 크기[%d] 보다 작아야 합니다.", length,
					dstBuffer.length));
		}

		outputStreamBuffer.put(dstBuffer, offset, length);
	}

	@Override
	public void putBytes(byte[] dstBuffer) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (null == dstBuffer) {
			throw new IllegalArgumentException(
					"파리미터 목적지 바이트 배열(=dstBuffer)의 값이 null 입니다.");
		}

		if (dstBuffer.length > CommonStaticFinalVars.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(
					String.format(
							"파리미터 목적지 바이트 배열 길이[%d]는  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							dstBuffer.length,
							CommonStaticFinalVars.MAX_UNSIGNED_SHORT));
		}

		// if (dstBuffer.length == 0) return;

		putBytes(dstBuffer, 0, dstBuffer.length);
	}

	@Override
	public void putBytes(ByteBuffer dstBuffer) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (null == dstBuffer) {
			throw new IllegalArgumentException("파리미터 목적지 버퍼의 값이 null 입니다.");
		}

		long remainingBytes = dstBuffer.remaining();
		if (remainingBytes > CommonStaticFinalVars.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(
					String.format(
							"파리미터 바이트 버퍼의 길이[%d]는 unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							remainingBytes,
							CommonStaticFinalVars.MAX_UNSIGNED_SHORT));
		}

		outputStreamBuffer.put(dstBuffer);
	}

	@Override
	public void skip(int skipBytes) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (skipBytes <= 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 생략할 쓰기 크기[%d]는 0보다 커야 합니다.", skipBytes));
		}

		if (skipBytes >= CommonStaticFinalVars.MAX_UNSIGNED_BYTE) {
			throw new IllegalArgumentException(String.format(
					"파라미터 생략할 쓰기 크기[%d]는 unsinged byte 최대값[%d]보다 작어야 합니다.",
					skipBytes, CommonStaticFinalVars.MAX_UNSIGNED_BYTE));
		}
		
		if (skipBytes > outputStreamBuffer.remaining()) {
			String errorMessage = String.format(
					"parameter skipBytes greater than remainging bytes[%d] of outputStreamBuffer",
					skipBytes, outputStreamBuffer.remaining());
			log.info(errorMessage);
			throw new BufferOverflowException();
		}

		int newLimit = outputStreamBuffer.position() + skipBytes;
		outputStreamBuffer.position(newLimit);
	}

	/**
	 * 출력 스트림에 이제까지 쓰기 작업을한 모든바이트들을 반환한다.
	 * 
	 * @return 출력 스트림에 이제까지 쓰기 작업을한 모든바이트들
	 */
	public final byte[] toByteArray() {
		if (outputStreamBuffer.hasArray()) {
			return outputStreamBuffer.array();
		} else {
			ByteBuffer dupBuffer = outputStreamBuffer.duplicate();
			dupBuffer.flip();
			int size = dupBuffer.remaining();			
			byte resultBytes[] = null;
			try {
				resultBytes = new byte[size];
			} catch (OutOfMemoryError e) {
				log.warn("OutOfMemoryError", e);
				throw e;
			}
			
			dupBuffer.get(resultBytes);
			return resultBytes;
		}
	}

	/**
	 * 남아 있는 byte 가 있는지 여부를 반환한다.
	 * 
	 * @return 남아 있는 byte 가 있는지 여부
	 */
	public boolean hasRemaining() {
		return outputStreamBuffer.hasRemaining();
	}

	@Override
	public long remaining() {
		return outputStreamBuffer.remaining();
	}

	@Override
	public long postion() {
		return outputStreamBuffer.position();
	}
}
