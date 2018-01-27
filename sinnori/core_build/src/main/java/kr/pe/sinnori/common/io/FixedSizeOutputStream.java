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
import kr.pe.sinnori.common.exception.CharsetEncoderException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriBufferOverflowException;

/**
 * 고정 크기를 갖는 이진 출력 스트림.
 * 
 * @author Won Jonghoon
 * 
 */
public class FixedSizeOutputStream implements BinaryOutputStreamIF {
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
		outputStreamBuffer.put(value);
	}


	private void doPutUnsignedShort(short value) {
		/*shortBuffer.clear();
		shortBuffer.putShort(value);
		shortBuffer.rewind();*/
		
		outputStreamBuffer.putShort(value);
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
	
	private void throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(long numberOfBytesRequired)
			throws SinnoriBufferOverflowException {
		long numberOfBytesRemaining = outputStreamBuffer.remaining();
		if (numberOfBytesRemaining < numberOfBytesRequired) {
			throw new SinnoriBufferOverflowException(
					String.format("the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
							numberOfBytesRemaining, numberOfBytesRequired));
		}
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
	public void putFixedLengthString(int fixedLength, String src) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException, CharsetEncoderException {
		putFixedLengthString(fixedLength, src, streamCharsetEncoder);
	}


	@Override
	public void putFixedLengthString(int fixedLength, String src, CharsetEncoder wantedCharsetEncoder)
			throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, SinnoriBufferOverflowException, CharsetEncoderException {
		if (fixedLength < 0) {
			throw new IllegalArgumentException(String.format(
					"the parameter fixedLength[%d] is less than zero", fixedLength));
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

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(fixedLength);

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
		int newLimit = outputStreamBuffer.position() + fixedLength;
		outputStreamBuffer.limit(newLimit);

		try {
			wantedCharsetEncoder.encode(CharBuffer.wrap(src), outputStreamBuffer, true);
		} catch (Exception e) {
			String errorMessage = String.format("fail to get a charset[%s] bytes of the parameter src[%s]::%s", 
					wantedCharsetEncoder.charset().name(), src, e.getMessage());
			log.warn(errorMessage, e);
			throw new CharsetEncoderException(errorMessage);
		}

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
	public void putStringAll(String src) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException, CharsetEncoderException {
		putStringAll(src, streamCharset);
	}
	
	@Override
	public void putStringAll(String src, Charset wantedCharset) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException, CharsetEncoderException {
		if (null == src) {
			throw new IllegalArgumentException("the parameter src is null");
		}
		
		if (null == wantedCharset) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}
		
		byte strBytes[] = null;
		try {
			strBytes = src.getBytes(wantedCharset);
		} catch (Exception e) {
			String errorMessage = String.format("fail to get a charset[%s] bytes of the parameter src[%s]::%s", 
					wantedCharset.name(), src, e.getMessage());
			log.warn(errorMessage, e);
			throw new CharsetEncoderException(errorMessage);
		}
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(strBytes.length);
		
		outputStreamBuffer.put(strBytes);
	}

	@Override
	public void putPascalString(String src) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException, CharsetEncoderException {
		putUBPascalString(src, streamCharset);
	}
	
	@Override
	public void putPascalString(String src, Charset wantedCharset) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException, CharsetEncoderException {
		putUBPascalString(src, wantedCharset);
	}

	
	@Override
	public void putUBPascalString(String src) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException, CharsetEncoderException {
		putUBPascalString(src, streamCharset);
	}


	@Override
	public void putUBPascalString(String src, Charset wantedCharset) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException, CharsetEncoderException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}
		
		if (wantedCharset == null) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}
	
		/*CharBuffer srcCharBuffer = CharBuffer.wrap(src);
		ByteBuffer srcByteBuffer = null;
		try {
			srcByteBuffer = streamCharsetEncoder.encode(srcCharBuffer);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			throw new CharsetEncoderException("fail to call streamCharsetEncoder.encode::"+e.getMessage());
		}
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired((srcByteBuffer.remaining() + 1));
		
		putUnsignedByte(srcByteBuffer.remaining());
		putBytes(srcByteBuffer);*/
		
		byte strBytes[] = null;
		try {
			strBytes = src.getBytes(wantedCharset);
		} catch (Exception e) {
			String errorMessage = String.format("fail to get a charset[%s] bytes of the parameter src[%s]::%s", 
					wantedCharset.name(), src, e.getMessage());
			log.warn(errorMessage, e);
			throw new CharsetEncoderException(errorMessage);
		}
	
		if (strBytes.length > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = String.format(
					"the length[%d] of bytes encoding the parameter src as a charset[%s] is greater than the unsigned byte max[%d]", 
					strBytes.length, wantedCharset.name(),
					CommonStaticFinalVars.UNSIGNED_BYTE_MAX);
			throw new IllegalArgumentException(errorMessage);
		}
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(strBytes.length+1);
		
		doPutUnsignedByte((byte)strBytes.length);
		outputStreamBuffer.put(strBytes);
	}


	@Override
	public void putUSPascalString(String src) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException, CharsetEncoderException {
		putUSPascalString(src, streamCharset);
	}


	@Override
	public void putUSPascalString(String src, Charset wantedCharset) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException, CharsetEncoderException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}
		
		if (null == wantedCharset) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}
		
		/*CharBuffer srcCharBuffer = CharBuffer.wrap(src);
		ByteBuffer srcByteBuffer = null;
		try {
			srcByteBuffer = streamCharsetEncoder.encode(srcCharBuffer);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			throw new CharsetEncoderException("fail to call streamCharsetEncoder.encode::"+e.getMessage());
		}	
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired((srcByteBuffer.remaining() + 2));
		
		putUnsignedShort(srcByteBuffer.remaining());
		putBytes(srcByteBuffer);*/
		
		
		byte strBytes[] = null;
		try {
			strBytes = src.getBytes(wantedCharset);
		} catch (Exception e) {
			String errorMessage = String.format("fail to get a charset[%s] bytes of the parameter src[%s]::%s", 
					wantedCharset.name(), src, e.getMessage());
			log.warn(errorMessage, e);
			throw new CharsetEncoderException(errorMessage);
		}
		
		if (strBytes.length > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			String errorMessage = String.format(
					"the length[%d] of bytes encoding the parameter src as a charset[%s] is greater than the unsigned short max[%d]", 
					strBytes.length, wantedCharset.name(),
					CommonStaticFinalVars.UNSIGNED_SHORT_MAX);
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(strBytes.length+2);
		
		doPutUnsignedShort((short)strBytes.length);
		outputStreamBuffer.put(strBytes);
		
	}


	@Override
	public void putSIPascalString(String src) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException, CharsetEncoderException {
		putSIPascalString(src, streamCharset);
	}


	@Override
	public void putSIPascalString(String src, Charset wantedCharset) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException, CharsetEncoderException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}
		
		if (null == wantedCharset) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}

		/*CharBuffer srcCharBuffer = CharBuffer.wrap(src);
		ByteBuffer srcByteBuffer = null;
		try {
			srcByteBuffer = streamCharsetEncoder.encode(srcCharBuffer);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			throw new CharsetEncoderException("fail to call streamCharsetEncoder.encode::"+e.getMessage());
		}	

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired((srcByteBuffer.remaining() + 4));

		putInt(srcByteBuffer.remaining());
		putBytes(srcByteBuffer);*/
		
		byte strBytes[] = null;
		try {
			strBytes = src.getBytes(wantedCharset);
		} catch (Exception e) {
			String errorMessage = String.format("fail to get a charset[%s] bytes of the parameter src[%s]::%s", 
					wantedCharset.name(), src, e.getMessage());
			log.warn(errorMessage, e);
			throw new CharsetEncoderException(errorMessage);
		}
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(strBytes.length+4);
		
		outputStreamBuffer.putInt(strBytes.length);
		outputStreamBuffer.put(strBytes);
	}

	@Override
	public void putBytes(byte[] src) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
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
	public void putBytes(byte[] src, int offset, int length)
			throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}
	
		if (offset < 0) {
			throw new IllegalArgumentException(String.format("the parameter offset[%d] is less than zero", offset));
		}
		
		/** check IndexOutOfBoundsException */
		if (offset >= src.length) {
			throw new IllegalArgumentException(String.format(
					"the parameter offset[%d] is greater than or equal to array.length[%d]", offset, src.length));
		}
	
		if (length < 0) {
			throw new IllegalArgumentException(String.format("the parameter length[%d] is less than zero", length));
		}
		
		/** check IndexOutOfBoundsException */
		long sumOfOffsetAndLength = ((long)offset + length);
		if (sumOfOffsetAndLength > src.length) {
			throw new IllegalArgumentException(String.format(
					"the sum[%d] of the parameter offset[%d] and the parameter length[%d] is greater than array.length[%d]", 
					sumOfOffsetAndLength, offset, length, src.length));
		}
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(length);
	
		outputStreamBuffer.put(src, offset, length);
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
	
	public int size() {
		return (outputStreamBuffer.position() - startPosition);
	}
	public void clearOutputStreamBuffer() {
		outputStreamBuffer.clear();
	}


	public void flipOutputStreamBuffer() {
		outputStreamBuffer.flip();
	}

	public int remaining() {
		return outputStreamBuffer.remaining();
	}
	

	@Override
	public Charset getCharset() {
		return streamCharset;
	}


	/**
	 * Closing a FixedSizeOutputStream has no effect
	 */
	public void close() {
	}
}
