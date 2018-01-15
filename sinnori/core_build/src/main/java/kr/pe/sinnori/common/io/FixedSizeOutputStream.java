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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

/**
 * 고정 크기를 갖는 이진 출력 스트림.
 * 
 * @author Won Jonghoon
 * 
 */
public class FixedSizeOutputStream implements SinnoriOutputStreamIF {
	private Logger log = LoggerFactory.getLogger(FixedSizeOutputStream.class);
	/**
	 * 출력을 담을 ByteBuffer.
	 */
	private ByteBuffer outputStreamBuffer = null;

	private Charset streamCharset = null;
	private CharsetEncoder streamCharsetEncoder = null;
	private ByteOrder streamByteOrder = null;
	private int startPosition = -1;
	
	private ByteBuffer shortBuffer = null;
	private ByteBuffer intBuffer = null;

	
	
	/**
	 * 생성자
	 * @param outputStreamBuffer 출력 스트림으로 사용할 바이트 버퍼
	 * @param streamCharset 출력 스트림 문자셋
	 * @param streamCharsetEncoder  출력 스트림 문자셋 인코더
	 */
	public FixedSizeOutputStream(ByteBuffer outputStreamBuffer, CharsetEncoder streamCharsetEncoder) {
		if (null == outputStreamBuffer) {
			throw new IllegalArgumentException("the parameter outputStreamBuffer is null");
		}

		if (null == streamCharsetEncoder) {
			throw new IllegalArgumentException("the parameter streamCharsetEncoder is null");
		}

		this.outputStreamBuffer = outputStreamBuffer;
		this.streamCharset = streamCharsetEncoder.charset();
		this.streamCharsetEncoder = streamCharsetEncoder;
		this.streamByteOrder = outputStreamBuffer.order();
		this.startPosition = outputStreamBuffer.position();

		
		shortBuffer = ByteBuffer.allocate(2);
		shortBuffer.order(streamByteOrder);
		
		intBuffer = ByteBuffer.allocate(4);
		intBuffer.order(streamByteOrder);	
	}

	
	private void doPutUnsignedByte(byte value) {
		putByte(value);
	}


	private void doPutUnsignedShort(short value) {
		shortBuffer.clear();
		shortBuffer.putShort(value);
		shortBuffer.rewind();
		
		outputStreamBuffer.put(shortBuffer);
		/*
		
		byte t0 =  (byte)(value);
		byte t1 =  (byte)(value >>> 8);
		
		
		log.info("the parameter value=[{}], t0=[{}], t1=[{}]"
				, HexUtil.getHexString(value)
				, HexUtil.getHexString(t0)
				, HexUtil.getHexString(t1));
		
		
		if (streamByteOrder.equals(ByteOrder.BIG_ENDIAN)) {
			outputStreamBuffer.put(t1);
			outputStreamBuffer.put(t0);
		} else {
			outputStreamBuffer.put(t0);
			outputStreamBuffer.put(t1);
		}*/
	}


	public void clearOutputStreamBuffer() {
		outputStreamBuffer.clear();
	}
	
	public void flipOutputStreamBuffer() {
		outputStreamBuffer.flip();
	}
	
	@Override
	public Charset getCharset() {
		return streamCharset;
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
	/*protected ByteBuffer getIntegerBufferForUnsignedShort(int value)
			throws IllegalArgumentException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]은 음수입니다.", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]은 unsigned short 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.UNSIGNED_SHORT_MAX));
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
	}*/

	/**
	 * unsigned integer 를 위한 long 버퍼에 값을 저장후 long 버퍼를 반환한다.
	 * 
	 * @param value
	 *            unsigned integer 타입에 대응하는 값
	 * @return 값이 저장된 unsigned integer 를 위한 long 버퍼
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터를 입력하여 발생한다.
	 */
	/*private ByteBuffer getLongBufferForUnsignedInt(long value)
			throws IllegalArgumentException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]은 음수입니다.", value));
		}
		if (value > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]은 unsigned int 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.UNSIGNED_INTEGER_MAX));
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
	}*/
	
	

	@Override
	public void putByte(byte value) throws BufferOverflowException {
		outputStreamBuffer.put(value);
	}
	
	
	@Override
	public void putUnsignedByte(short value) throws BufferOverflowException,
			IllegalArgumentException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format(
					"the parameter value[%d] is less than zero", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			throw new IllegalArgumentException(String.format(
					"the parameter value[%d] is greater than the unsigned byte max[%d]", value,
					CommonStaticFinalVars.UNSIGNED_BYTE_MAX));
		}		
		
		doPutUnsignedByte((byte)value);
		
	}
	
	@Override
	public void putUnsignedByte(int value) throws BufferOverflowException, IllegalArgumentException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format(
					"the parameter value[%d] is less than zero", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			throw new IllegalArgumentException(String.format(
					"the parameter value[%d] is greater than the unsigned byte max[%d]", value,
					CommonStaticFinalVars.UNSIGNED_BYTE_MAX));
		}

		doPutUnsignedByte((byte)value);
	}
	
	@Override
	public void putUnsignedByte(long value) throws BufferOverflowException, IllegalArgumentException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format(
					"the parameter value[%d] is less than zero", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			throw new IllegalArgumentException(String.format(
					"the parameter value[%d] is greater than the unsigned byte max[%d]", value,
					CommonStaticFinalVars.UNSIGNED_BYTE_MAX));
		}

		doPutUnsignedByte((byte)value);
	}

	@Override
	public void putShort(short value) throws BufferOverflowException {
		outputStreamBuffer.putShort(value);
	}
	

	@Override
	public void putUnsignedShort(int value) throws BufferOverflowException,
			IllegalArgumentException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format(
					"the parameter value[%d] is less than zero", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			throw new IllegalArgumentException(String.format(
					"the parameter value[%d] is greater than the unsigned short max[%d]", value,
					CommonStaticFinalVars.UNSIGNED_SHORT_MAX));
		}
		/*ByteBuffer unsingedShortBuffer = getIntegerBufferForUnsignedShort(value);
		outputStreamBuffer.put(unsingedShortBuffer);*/
		doPutUnsignedShort((short)value);
	}
	
	@Override
	public void putUnsignedShort(long value) throws BufferOverflowException,
			IllegalArgumentException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format(
					"the parameter value[%d] is less than zero", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			throw new IllegalArgumentException(String.format(
					"the parameter value[%d] is greater than the unsigned short max[%d]", value,
					CommonStaticFinalVars.UNSIGNED_SHORT_MAX));
		}
		/*ByteBuffer unsingedShortBuffer = getIntegerBufferForUnsignedShort(value);
		outputStreamBuffer.put(unsingedShortBuffer);*/
		doPutUnsignedShort((short)value);
	}

	@Override
	public void putInt(int value) throws BufferOverflowException {
		outputStreamBuffer.putInt(value);
	}


	@Override
	public void putUnsignedInt(long value) throws BufferOverflowException,
			IllegalArgumentException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format(
					"the parameter value[%d] is less than zero", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			throw new IllegalArgumentException(String.format(
					"the parameter value[%d] is greater than the unsigned integer max[%d]", value,
					CommonStaticFinalVars.UNSIGNED_INTEGER_MAX));
		}
		
		/*ByteBuffer unsingedIntBuffer = getLongBufferForUnsignedInt(value);
		outputStreamBuffer.put(unsingedIntBuffer);*/
		/*
		byte t0 =  (byte) value;
		byte t1 =  (byte)(value >>> 8);
		byte t2 =  (byte)(value >>> 16);
		byte t3 =  (byte)(value >>> 24);
		
		log.info("the parameter value=[{}], t0=[{}], t1=[{}], t2=[{}], t3=[{}]"
				, HexUtil.getHexString(value)
				, HexUtil.getHexString(t0)
				, HexUtil.getHexString(t1)
				, HexUtil.getHexString(t2)
				, HexUtil.getHexString(t3));
		
		if (streamByteOrder.equals(ByteOrder.BIG_ENDIAN)) {
			outputStreamBuffer.put(t3);
			outputStreamBuffer.put(t2);
			outputStreamBuffer.put(t1);
			outputStreamBuffer.put(t0);
		} else {
			outputStreamBuffer.put(t0);
			outputStreamBuffer.put(t1);
			outputStreamBuffer.put(t2);
			outputStreamBuffer.put(t3);
		}*/
		
		intBuffer.clear();
		intBuffer.putInt((int)value);
		intBuffer.rewind();
		
		outputStreamBuffer.put(intBuffer);
	}

	@Override
	public void putLong(long value) throws BufferOverflowException {
		outputStreamBuffer.putLong(value);
	}

	@Override
	public void putFixedLengthString(int length, String src, CharsetEncoder wantedCharsetEncoder)
			throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException {
		if (length < 0) {
			throw new IllegalArgumentException(String.format(
					"the parameter length[%d] is less than zero", length));
		}

		/*if (length > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			throw new IllegalArgumentException(
					String.format(
							"the parameter length is greater than ",
							length, CommonStaticFinalVars.UNSIGNED_SHORT_MAX));
		}*/

		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		if (wantedCharsetEncoder == null) {
			throw new IllegalArgumentException(
					"the parameter wantedCharsetEncoder is null");
		}

		// The number of elements remaining in this buffer
		int remainingBytes = outputStreamBuffer.remaining();

		if (length > remainingBytes) {
			// String.format("남아 있는 저장 가능한 영역의 크기[%d]가 지정된 문자열[%d] 크기 보다 작습니다.", remainingBytes, length)
			throw new IllegalArgumentException(String.format("the parameter length[%d] is greater than the remaining bytes[%d]", length, remainingBytes));
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
		int oldLimit = outputStreamBuffer.limit();
		int newLimit = outputStreamBuffer.position() + length;
		outputStreamBuffer.limit(newLimit);

		wantedCharsetEncoder.encode(CharBuffer.wrap(src), outputStreamBuffer, true);

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
		outputStreamBuffer.limit(oldLimit);
	}

	@Override
	public void putFixedLengthString(int length, String src) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		putFixedLengthString(length, src, streamCharsetEncoder);
	}

	@Override
	public void putStringAll(String src) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		int numberOfResultBytes = src.getBytes(streamCharset).length;
		/*if (numOfBytes > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			throw new IllegalArgumentException(
					String.format(
							"파라미터로 넘어온 문자열[%s]을 지정한 문자셋[%s]에 맞추어 변환된 바이트 배열의 크기[%d]은  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							src, streamCharset.displayName(),
							numOfBytes, CommonStaticFinalVars.UNSIGNED_SHORT_MAX));
		}*/

		
		putFixedLengthString(numberOfResultBytes, src, streamCharsetEncoder);
	}

	@Override
	public void putPascalString(String src) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		putUBPascalString(src);
	}

	@Override
	public void putSIPascalString(String src) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		// Encodes this String into a sequence of bytes using the given charset,
		// the sequence of bytes encoded by the parameter src using the given charset
		byte resultBytes[]  = src.getBytes(streamCharset);

		/*if (srcBytesAppliedCharset.length > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			throw new IllegalArgumentException(
					String.format(
							"파리미터 문자열 길이(=len)의 값[%d]은  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							srcBytesAppliedCharset.length,
							CommonStaticFinalVars.UNSIGNED_SHORT_MAX));
		}*/

		putInt(resultBytes.length);
		putBytes(resultBytes);
	}

	@Override
	public void putUSPascalString(String src) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}
		byte resultBytes[] = src.getBytes(streamCharset);
		putUnsignedShort(resultBytes.length);
		putBytes(resultBytes);
	}

	@Override
	public void putUBPascalString(String str) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (str == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		byte resultBytes[] = str.getBytes(streamCharset);
		/*
		if (strBytes.length > CommonStaticFinal.MAX_UNSIGNED_BYTE) {
			throw new IllegalArgumentException(String.format(
					"파라미터 문자열의 길이[%d]는 unsigned byte 최대값[%d]을 넘을 수 없습니다.", strBytes.length, CommonStaticFinal.MAX_UNSIGNED_BYTE));
		}
		*/
		
		putUnsignedByte(resultBytes.length);
		putBytes(resultBytes);
	}

	@Override
	public void putBytes(byte[] src, int offset, int length)
			throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		if (offset < 0) {
			throw new IllegalArgumentException(String.format("the parameter offset[%d] is less than zero", offset));
		}
		
		/** check IndexOutOfBoundsException */
		if (offset >= src.length) {
			throw new IllegalArgumentException(String.format(
					"the parameter offset[%d] is greater than or equal to the length[%d] of the parameter src that is a byte array", offset,
					src.length));
		}

		if (length < 0) {
			throw new IllegalArgumentException(String.format("the parameter length[%d] is less than zero", length));
		}
		
		/** check IndexOutOfBoundsException */
		long sumOfOffsetAndLength = ((long)offset + length);
		if (sumOfOffsetAndLength > src.length) {
			throw new IllegalArgumentException(String.format(
					"the sum[%d] of the parameter offset[%d] and the parameter length[%d] is greater than the length[%d] of the parameter src that is a byte array", 
					sumOfOffsetAndLength, offset, length, src.length));
		}

		/*if (length > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			throw new IllegalArgumentException(
					String.format(
							"파리미터 목적지 바이트 배열 길이[%d]는  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							length, CommonStaticFinalVars.UNSIGNED_SHORT_MAX));
		}*/

		/** check BufferOverflowException */
		long remainingBytes = outputStreamBuffer.remaining();
		if (length > remainingBytes) {
			throw new IllegalArgumentException(String.format(
					"the parameter length[%d] is greater than the remaining bytes[%d]", length,
					remainingBytes));
		}	

		outputStreamBuffer.put(src, offset, length);
	}

	@Override
	public void putBytes(byte[] src) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (null == src) {
			throw new IllegalArgumentException(
					"the parameter src is null");
		}

		/*if (dstBuffer.length > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			throw new IllegalArgumentException(
					String.format(
							"파리미터 목적지 바이트 배열 길이[%d]는  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							dstBuffer.length,
							CommonStaticFinalVars.UNSIGNED_SHORT_MAX));
		}*/

		// if (dstBuffer.length == 0) return;

		putBytes(src, 0, src.length);
	}

	@Override
	public void putBytes(ByteBuffer src) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (null == src) {
			throw new IllegalArgumentException("the parameter src is null");
		}
		

		/*long remainingBytes = dstBuffer.remaining();
		if (remainingBytes > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			throw new IllegalArgumentException(
					String.format(
							"파리미터 바이트 버퍼의 길이[%d]는 unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							remainingBytes,
							CommonStaticFinalVars.UNSIGNED_SHORT_MAX));
		}*/
		
		long remainingBytesInOutputStreamBuffer = outputStreamBuffer.remaining();
		int remainingBytesInSrcByteBuffer= src.remaining();
		if (remainingBytesInSrcByteBuffer > remainingBytesInOutputStreamBuffer) {
			String errorMessage = String.format(
					"the bytes[%d] remaing in the parameter src that is a ByteBuffer greater than the remaining bytes[%d]",
					remainingBytesInSrcByteBuffer, remainingBytesInOutputStreamBuffer);
			throw new IllegalArgumentException(errorMessage);
		}	

		outputStreamBuffer.put(src);
	}

	@Override
	public void skip(int n) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (n < 0) {
			throw new IllegalArgumentException(String.format(
					"the parameter n is less than zero", n));
		}

		/*if (skipBytes >= CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			throw new IllegalArgumentException(String.format(
					"파라미터 생략할 쓰기 크기[%d]는 unsinged byte 최대값[%d]보다 작어야 합니다.",
					skipBytes, CommonStaticFinalVars.UNSIGNED_BYTE_MAX));
		}*/
		
		int remaingBytes = outputStreamBuffer.remaining();
		
		if (n > remaingBytes) {
			String errorMessage = String.format(
					"the parameter n[%d] is greater than the remaining bytes[%d]",
					n, remaingBytes);
			// log.info(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		int newLimit = outputStreamBuffer.position() + n;
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

	/*@Override
	public long remaining() {
		return outputStreamBuffer.remaining();
	}*/

	// @Override
	/*public long postion() {
		return outputStreamBuffer.position();
	}
	*/
	
	public long size() {
		return (outputStreamBuffer.position() - startPosition);
	}
	/**
	 * Closing a FixedSizeOutputStream has no effect
	 */
	public void close() {
	}
}
