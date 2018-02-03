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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.CharsetDecoderException;
import kr.pe.sinnori.common.exception.SinnoriBufferUnderflowException;
import kr.pe.sinnori.common.util.HexUtil;

/**
 * 고정 크기를 갖는 입력 이진 스트림.<br/>
 * 1개의 고정 바이트 버퍼로 구성된다.
 * 
 * @author Won Jonghoon
 * 
 */
public class FixedSizeInputStream implements BinaryInputStreamIF {
	private Logger log = LoggerFactory.getLogger(FixedSizeInputStream.class);
	/**
	 * 입력받은 InputStream 처럼 동작 시킬 ByteBuffer
	 */
	private ByteBuffer streamBuffer;

	private Charset streamCharset;
	private CharsetDecoder streamCharsetDecoder;

	/**
	 * 입력 받은 ByteBuffer의 ByteOrder, 속도를 위해서 사용함
	 */
	private ByteOrder streamByteOrder;

	private ByteBuffer intBuffer = null;
	private ByteBuffer longBuffer = null;
	
	private byte[] bytesOfIntBuffer = null;
	private byte[] bytesOfLongBuffer = null;

	/**
	 * 생성자
	 * 
	 * @param streamBuffer
	 *            스트림 구현 바이트 버퍼
	 * @param streamCharsetDecoder
	 *            스트림 고유 문자셋
	 */
	public FixedSizeInputStream(final ByteBuffer streamBuffer,
			CharsetDecoder streamCharsetDecoder) {
		if (null == streamBuffer) {
			throw new IllegalArgumentException("the parameter streamBuffer is null");
		}
		if (null == streamCharsetDecoder) {
			throw new IllegalArgumentException("the parameter streamCharsetDecoder is null");
		}
		
		this.streamBuffer = streamBuffer;
		this.streamCharset = streamCharsetDecoder.charset();
		this.streamCharsetDecoder = streamCharsetDecoder;
		streamByteOrder = streamBuffer.order();

		bytesOfIntBuffer = new byte[4];
		intBuffer = ByteBuffer.wrap(bytesOfIntBuffer);
		intBuffer.order(streamByteOrder);

		bytesOfLongBuffer = new byte[8];
		longBuffer = ByteBuffer.wrap(bytesOfLongBuffer);
		longBuffer.order(streamByteOrder);		
		
	}
	
	private String doGetString(int length, Charset stringCharset) throws SinnoriBufferUnderflowException, CharsetDecoderException {
		byte dstBytes[] = new byte[length];
		streamBuffer.get(dstBytes);
		
		String dst = null;
		
		try {
			dst = new String(dstBytes, stringCharset);
		} catch(Exception e) {
			String errorMessage = String.format("fail to get a new String. read data hex[%s], charset[%s]",
					HexUtil.getHexStringFromByteArray(dstBytes), stringCharset.name());
			// log.warn(errorMessage, e);
			throw new CharsetDecoderException(errorMessage);
		}
	
		return dst;
	}
	
	private void throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(int numberOfBytesRequired)
			throws SinnoriBufferUnderflowException {
		int numberOfBytesRemaining = streamBuffer.remaining();
		if (numberOfBytesRemaining < numberOfBytesRequired) {
			throw new SinnoriBufferUnderflowException(
					String.format("the number[%d] of bytes remaining in this input stream is less than [%d] byte(s) that is required",
							numberOfBytesRemaining, numberOfBytesRequired));
		}
	}

	@Override
	public byte getByte() throws SinnoriBufferUnderflowException {
		long remainingBytes = available();
		if (0 == remainingBytes) {
			throw new SinnoriBufferUnderflowException("the remaining bytes is zero");
		}
		
		return streamBuffer.get();
	}

	@Override
	public short getUnsignedByte() throws SinnoriBufferUnderflowException {
		short retValue = (short) (getByte() & 0xff);
		return retValue;
	}

	@Override
	public short getShort() throws SinnoriBufferUnderflowException {
		long remainingBytes = available();
		if (2 > remainingBytes) {
			throw new SinnoriBufferUnderflowException(String.format("the remaining bytes[%d] is less than two bytes", remainingBytes));
		}
		
		short value = streamBuffer.getShort();
		return value;
	}

	@Override
	public int getUnsignedShort() throws SinnoriBufferUnderflowException {
		long remainingBytes = available();
		if (2 > remainingBytes) {
			throw new SinnoriBufferUnderflowException(String.format("the remaining bytes[%d] is less than two bytes", remainingBytes));
		}
		
		int retValue=0;
		
		intBuffer.clear();
		Arrays.fill(bytesOfIntBuffer, CommonStaticFinalVars.ZERO_BYTE);
		if (ByteOrder.BIG_ENDIAN == streamByteOrder) {
			intBuffer.position(2);
			intBuffer.limit(4);
		} else {
			intBuffer.position(0);
			intBuffer.limit(2);
		}

		intBuffer.put(streamBuffer.get());
		intBuffer.put(streamBuffer.get());

		intBuffer.clear();

		retValue = intBuffer.getInt();
		
		/*byte t0;
		byte t1;
		
		if (ByteOrder.BIG_ENDIAN == streamByteOrder) {
			t1 = streamBuffer.get();
			t0 = streamBuffer.get();
					
		} else {
			t0 = streamBuffer.get();
			t1 = streamBuffer.get();
		}
		
		*//**
		 * Warning! don't delete '0xff', if no 0xff, then it fails to get a valid value because of signed byte. 
		 *//*
		retValue = (t0 & 0xff) | ((t1 & 0xff) << 8);
		
		// log.info("{}", String.format("retValue=[%x], t0=[%x], t1=[%x]", retValue, t0, t1));
		log.info("retValue=[{}], t0=[{}], t1=[{}]"
				, HexUtil.getHexString(retValue)
				, HexUtil.getHexString(t0)
				, HexUtil.getHexString(t1));*/

		return retValue;
	}

	@Override
	public int getInt() throws SinnoriBufferUnderflowException {
		long remainingBytes = available();
		if (4 > remainingBytes) {
			throw new SinnoriBufferUnderflowException(String.format("the remaining bytes[%d] is less than four bytes", remainingBytes));
		}
		
		int value = streamBuffer.getInt();
		return value;
	}

	@Override
	public long getUnsignedInt() throws SinnoriBufferUnderflowException {
		long remainingBytes = available();
		if (4 > remainingBytes) {
			throw new SinnoriBufferUnderflowException(String.format("the remaining bytes[%d] is less than four bytes", remainingBytes));
		}
		
		long retValue=0L;

		longBuffer.clear();
		Arrays.fill(bytesOfLongBuffer, CommonStaticFinalVars.ZERO_BYTE);
		
		if (ByteOrder.BIG_ENDIAN.equals(streamByteOrder)) {
			longBuffer.position(4);
			longBuffer.limit(8);
		} else {
			longBuffer.position(0);
			longBuffer.limit(4);
		}

		longBuffer.put(streamBuffer.get());
		longBuffer.put(streamBuffer.get());
		longBuffer.put(streamBuffer.get());
		longBuffer.put(streamBuffer.get());

		longBuffer.clear();

		retValue = longBuffer.getLong();
		
		/*byte t0;
		byte t1;
		byte t2;
		byte t3;
		
		if (ByteOrder.BIG_ENDIAN == streamByteOrder) {
			t3 = streamBuffer.get();
			t2 = streamBuffer.get();
			t1 = streamBuffer.get();
			t0 = streamBuffer.get();
					
		} else {
			t0 = streamBuffer.get();
			t1 = streamBuffer.get();
			t2 = streamBuffer.get();
			t3 = streamBuffer.get();
		}
		
		*//**
		 * Warning! don't delete '0xff', if no 0xff, then it fails to get a valid value because of signed byte. 
		 *//*
		retValue = (t0 & 0xffL) | ((t1 & 0xffL) << 8) | ((t2 & 0xffL) << 16) | ((t3 & 0xffL) << 24);
		
		// log.info("{}", String.format("retValue=[%x], t0=[%x], t1=[%x], t2=[%x], t3=[%x]", retValue, t0, t1, t2, t3));
		log.info("retValue=[{}], t0=[{}], t1=[{}], t2=[{}], t3=[{}]"
				, HexUtil.getHexString(retValue)
				, HexUtil.getHexString(t0)
				, HexUtil.getHexString(t1)
				, HexUtil.getHexString(t2)
				, HexUtil.getHexString(t3));*/
		
		return retValue;
	}

	@Override
	public long getLong() throws SinnoriBufferUnderflowException {
		long remainingBytes = available();
		if (8 > remainingBytes) {
			throw new SinnoriBufferUnderflowException(String.format("the remaining bytes[%d] is less than eight bytes", remainingBytes));
		}
		
		long value = streamBuffer.getLong();
		return value;
	}

	@Override
	public String getFixedLengthString(final int fixedLength, final CharsetDecoder wantedCharsetDecoder)
			throws SinnoriBufferUnderflowException, IllegalArgumentException, CharsetDecoderException {
		if (fixedLength < 0) {
			throw new IllegalArgumentException(String.format(
					"the parameter fixedLength[%d] less than zero", fixedLength));
		}

		/*if (len > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(
					String.format(
							"파리미터 문자열 길이(=len)의 값[%d]은  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							len, CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}*/

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(fixedLength);
		
		
		ByteBuffer dstBuffer = null;
		byte dstBytes[] = new byte[fixedLength];
		dstBuffer = ByteBuffer.wrap(dstBytes);
				
		/*
		 * ByteBuffer.get(Byte[]) 메소드는 내부적으로 ByteBuffer.get() 으로 동작하므로 버퍼 속성
		 * position 은 입력 받은 배열 크기 만큼 변환다.
		 */
		streamBuffer.get(dstBytes);
		
		
		CharBuffer dstCharBuffer = null;
		try {
			dstCharBuffer = wantedCharsetDecoder.decode(dstBuffer);
		} catch(Exception e) {			
			String errorMessage = String.format("charset decoding error:read data hex[%s], charset[%s]", 
					HexUtil.getAllHexStringFromByteBuffer(dstBuffer), wantedCharsetDecoder.charset().name());
			throw new CharsetDecoderException(errorMessage);
		}
		
		return dstCharBuffer.toString();
	}

	@Override
	public String getFixedLengthString(final int fixedLength) throws SinnoriBufferUnderflowException,
			IllegalArgumentException, CharsetDecoderException {
		return getFixedLengthString(fixedLength, streamCharsetDecoder);
	}

	@Override
	public String getStringAll() throws SinnoriBufferUnderflowException,
			IllegalArgumentException, CharsetDecoderException {
		return getStringAll(streamCharset);
	}
	
	@Override
	public String getStringAll(Charset wantedCharset) throws SinnoriBufferUnderflowException,
			IllegalArgumentException, CharsetDecoderException {
		if (null == wantedCharset) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}
		
		long numberOfBytesRemaining = available();
		
		if (0 == numberOfBytesRemaining) {
			return "";
		}

		if (numberOfBytesRemaining > Integer.MAX_VALUE) {
			/**
			 * 자바 문자열에 입력 가능한 바이트 배열의 크기는 Integer.MAX_VALUE 이다.
			 */
			throw new SinnoriBufferUnderflowException(
					String.format("the number[%d] of bytes remaing in this input stream is greater than the maximum value[%d] of integer",
							numberOfBytesRemaining, Integer.MAX_VALUE));
		}
		
		int length = (int) numberOfBytesRemaining;
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(length);
		
		return doGetString(length, wantedCharset);
	}

	@Override
	public String getPascalString() throws SinnoriBufferUnderflowException,
			IllegalArgumentException, CharsetDecoderException {
		return getUBPascalString(streamCharset);
	}
	
	@Override
	public String getPascalString(Charset wantedCharset) throws SinnoriBufferUnderflowException,
			IllegalArgumentException, CharsetDecoderException {
		return getUBPascalString(wantedCharset);
	}

	@Override
	public String getUBPascalString() throws SinnoriBufferUnderflowException,
			IllegalArgumentException, CharsetDecoderException {
		return getUBPascalString(streamCharset);
	}

	@Override
	public String getUBPascalString(Charset wantedCharset) throws SinnoriBufferUnderflowException,
			IllegalArgumentException, CharsetDecoderException {
		if (null == wantedCharset) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(1);
		
		int length = getUnsignedByte();
		if (0 == length) {
			return "";
		}
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(length);
		
		return doGetString(length, wantedCharset);
	}

	@Override
	public String getUSPascalString() throws SinnoriBufferUnderflowException,
			IllegalArgumentException, CharsetDecoderException {
		return getUSPascalString(streamCharset);
	}

	@Override
	public String getUSPascalString(Charset wantedCharset) throws SinnoriBufferUnderflowException,
			IllegalArgumentException, CharsetDecoderException {
		if (null == wantedCharset) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(2);
		
		int length = getUnsignedShort();
		if (0 == length) {
			return "";
		}
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(length);
		
		return doGetString(length, wantedCharset);
	}

	@Override
	public String getSIPascalString() throws SinnoriBufferUnderflowException,
			IllegalArgumentException, CharsetDecoderException {
		return getSIPascalString(streamCharset);
	}
	
	@Override
	public String getSIPascalString(Charset wantedCharset) throws SinnoriBufferUnderflowException,
			IllegalArgumentException, CharsetDecoderException {
		if (null == wantedCharset) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(4);
		
		int length = getInt();
		if (length < 0) {
			throw new IllegalArgumentException(String.format(
					"the pascal string length[%d] whose type is integer is less than zero", length));
		}

		if (0 == length) {
			return "";
		}
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(length);

		return doGetString(length, wantedCharset);
	}

	@Override
	public void getBytes(byte[] dst, int offset, int length)
			throws SinnoriBufferUnderflowException, IllegalArgumentException {
		if (null == dst) {
			throw new IllegalArgumentException("ths parameter dst is null");
		}

		if (offset < 0) {
			throw new IllegalArgumentException(String.format(
					"the parameter offset[%d] is less than zero", offset));
		}
		
		if (offset >= dst.length) {
			throw new IllegalArgumentException(String.format(
					"the parameter offset[%d] is greater than or equal to the dest buffer's length[%d]", offset, dst.length));
		}

		if (length < 0) {
			throw new IllegalArgumentException(String.format(
					"the parameter length[%d] is less than or equal to zero", length));
		}
		
		/** check IndexOutOfBoundsException */
		long sumOfOffsetAndLength = ((long)offset + length);
		if (sumOfOffsetAndLength > dst.length) {
			throw new IllegalArgumentException(String.format(
					"the sum[%d] of the parameter offset[%d] and the parameter length[%d] is greater than the length[%d] of the parameter dst that is a byte array", 
					sumOfOffsetAndLength, offset, length, dst.length));
		}

		/*if (len > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(String.format(
					"파라미터 문자열 길이[%d]는  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
					len, CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}*/		
		
		
		long remainingBytes = available();
		if (length > remainingBytes) {
			throw new SinnoriBufferUnderflowException(String.format("the parameter length[%d] is greater than the remaining bytes[%d]", length, remainingBytes));
		}

		// int position = streamBuffer.position();

		streamBuffer.get(dst, offset, length);
	}

	@Override
	public void getBytes(byte[] dst) throws SinnoriBufferUnderflowException,
			IllegalArgumentException {
		if (null == dst) {
			throw new IllegalArgumentException(
					"the paramerter dst is null");
		}
		getBytes(dst, 0, dst.length);
	}

	@Override
	public byte[] getBytes(int length) throws SinnoriBufferUnderflowException,
			IllegalArgumentException, OutOfMemoryError {
		if (length < 0) {
			throw new IllegalArgumentException(String.format(
					"the parameter length[%d] is less than zero", length));
		}

		/*if (len > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(String.format(
					"파라미터 문자열 길이[%d]는  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
					len, CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}*/

		long remainingBytes = available();
		if (length > remainingBytes) {
			throw new SinnoriBufferUnderflowException(String.format("the parameter length[%d] is greater than the remaining bytes[%d]", length, remainingBytes));
		}

		byte buffer[] = null;
		try {
			buffer = new byte[length];
		} catch (OutOfMemoryError e) {
			log.warn("OutOfMemoryError", e);
			throw e;
		}

		// ByteBuffer.get(Byte[]) 메소드는 내부적으로 ByteBuffer.get() 으로 동작하므로 버퍼 속성
		// position 은 입력 받은 배열 크기 만큼 변환다.
		streamBuffer.get(buffer);
		return buffer;
	}

	@Override
	public void skip(int n) throws SinnoriBufferUnderflowException {
		if (n < 0) {
			throw new IllegalArgumentException(String.format(
					"the parameter n[%d] is less than zero", n));
		}

		/*if (len > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(String.format(
					"파라미터 문자열 길이[%d]는  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
					len, CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}*/

		long remainingBytes = available();
		if (n > remainingBytes) {
			throw new SinnoriBufferUnderflowException(String.format("the parameter n[%d] is greater than the remaining bytes[%d]", n, remainingBytes));
		}

		streamBuffer.position(streamBuffer.position() + n);
	}
	
	
	
	// @Override
	public long position() {
		return streamBuffer.position();
	}

	@Override
	public long available() {
		return streamBuffer.remaining();
	}

	@Override
	public Charset getCharset() {
		return streamCharset;
	}

	@Override
	public ByteOrder getByteOrder() {
		return streamByteOrder;
	}

	@Override
	public long indexOf(byte[] searchBytes) {
		int remainingBytes = streamBuffer.remaining();
		
		for (int i=0; i < remainingBytes; i++) {
			if (i+searchBytes.length > remainingBytes) return -1;
			
			int j=0;
			for (; j < searchBytes.length; j++) {
				if (streamBuffer.get(i+j) != searchBytes[j]) break;
			}
			
			if (j == searchBytes.length) return i;
		}
		
		return -1;
	}
	
	/**
	 * Closing a FixedSizeOutputStream has no effect
	 */
	@Override
	public void close() {
	}
}
