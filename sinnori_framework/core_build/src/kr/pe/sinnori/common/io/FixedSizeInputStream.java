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

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import kr.pe.sinnori.common.exception.SinnoriCharsetCodingException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonStaticFinal;
import kr.pe.sinnori.common.util.HexUtil;

/**
 * 고정 크기를 갖는 입력 이진 스트림.<br/>
 * 1개의 고정 바이트 버퍼로 구성된다.
 * 
 * @author Jonghoon Won
 * 
 */
public class FixedSizeInputStream implements CommonRootIF, InputStreamIF {
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
		this.streamBuffer = streamBuffer;
		this.streamCharset = streamCharsetDecoder.charset();
		this.streamCharsetDecoder = streamCharsetDecoder;
		streamByteOrder = streamBuffer.order();

		intBuffer = ByteBuffer.allocate(4);
		intBuffer.order(streamByteOrder);

		longBuffer = ByteBuffer.allocate(8);
		longBuffer.order(streamByteOrder);
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
	public long remaining() {
		return streamBuffer.remaining();
	}

	@Override
	public long position() {
		return streamBuffer.position();
	}

	@Override
	public byte getByte() throws BufferUnderflowException {
		return streamBuffer.get();
	}

	@Override
	public short getUnsignedByte() throws BufferUnderflowException {
		short retValue = (short) (0xFF & getByte());
		return retValue;
	}

	@Override
	public short getShort() throws BufferUnderflowException {
		short value = streamBuffer.getShort();
		return value;
	}

	@Override
	public int getUnsignedShort() throws BufferUnderflowException {
		int retValue;

		intBuffer.clear();

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

		return retValue;
	}

	@Override
	public int getInt() throws BufferUnderflowException {
		int value = streamBuffer.getInt();
		return value;
	}

	@Override
	public long getUnsignedInt() throws BufferUnderflowException {
		long retValue;

		longBuffer.clear();
		if (ByteOrder.BIG_ENDIAN == streamByteOrder) {
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
		return retValue;
	}

	@Override
	public long getLong() throws BufferUnderflowException {
		long value = streamBuffer.getLong();
		return value;
	}

	@Override
	public String getString(final int len, final CharsetDecoder wantedCharsetDecoder)
			throws BufferUnderflowException, IllegalArgumentException, SinnoriCharsetCodingException {
		if (len < 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 길이[%d]는  0 보다 크거나 같아야 합니다.", len));
		}

		if (len > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(
					String.format(
							"파리미터 문자열 길이(=len)의 값[%d]은  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							len, CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}

		long remainingBytes = remaining();
		if (len > remainingBytes) {
			throw new IllegalArgumentException(String.format(
					"파라미터 길이[%d]는  남아 있은 버퍼 크기[%d] 보다 작거나 같아야 합니다.", len,
					remainingBytes));
		}

		byte dstBytes[] = null;
		try {
			dstBytes = new byte[len];
		} catch (OutOfMemoryError e) {
			log.warn("OutOfMemoryError", e);
			throw e;
		}
				
		/*
		 * ByteBuffer.get(Byte[]) 메소드는 내부적으로 ByteBuffer.get() 으로 동작하므로 버퍼 속성
		 * position 은 입력 받은 배열 크기 만큼 변환다.
		 */
		streamBuffer.get(dstBytes);
		
		
		ByteBuffer dstBuffer = ByteBuffer.wrap(dstBytes);
		
		CharBuffer dstCharBuffer = null;
		try {
			dstCharBuffer = wantedCharsetDecoder.decode(dstBuffer);
		} catch(CharacterCodingException e) {
			
			String errorMessage = String.format("read data hex[%s], charset[%s]", HexUtil.byteBufferAllToHex(dstBuffer), wantedCharsetDecoder.charset().name());
			// log.warn(errorMessage, e);
			throw new SinnoriCharsetCodingException(errorMessage);
		}
		
		return dstCharBuffer.toString();
	}

	@Override
	public String getString(final int len) throws BufferUnderflowException,
			IllegalArgumentException, SinnoriCharsetCodingException {
		return getString(len, streamCharsetDecoder);
	}

	@Override
	public String getStringAll() throws BufferUnderflowException,
			IllegalArgumentException, SinnoriCharsetCodingException {
		long remainingBytes = remaining();

		if (remainingBytes > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(
					String.format(
							"문자열로 변환될 남아 있는 버퍼 크기[%d]는  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							remainingBytes,
							CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}

		if (0 == remainingBytes)
			return "";
		return getString((int) remainingBytes, streamCharsetDecoder);
	}

	@Override
	public String getPascalString() throws BufferUnderflowException,
			IllegalArgumentException, SinnoriCharsetCodingException {
		return getUBPascalString();
	}

	@Override
	public String getSIPascalString() throws BufferUnderflowException,
			IllegalArgumentException, SinnoriCharsetCodingException {
		int len = getInt();
		if (len < 0)
			throw new IllegalArgumentException(String.format(
					"문자열 크기[%d]로 음수값이 전달되었습니다.", len));

		if (len > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(String.format(
					"문자열 길이[%d]는  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.", len,
					CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}

		if (0 == len)
			return "";

		return getString(len, streamCharsetDecoder);
	}

	@Override
	public String getUSPascalString() throws BufferUnderflowException,
			IllegalArgumentException, SinnoriCharsetCodingException {
		int numOfBytes = getUnsignedShort();
		if (0 == numOfBytes)
			return "";
		return getString(numOfBytes, streamCharsetDecoder);
	}

	@Override
	public String getUBPascalString() throws BufferUnderflowException,
			IllegalArgumentException, SinnoriCharsetCodingException {
		int numOfBytes = getUnsignedByte();
		if (0 == numOfBytes)
			return "";
		return getString(numOfBytes, streamCharsetDecoder);
	}

	@Override
	public void getBytes(byte[] dstBuffer, int offset, int len)
			throws BufferUnderflowException, IllegalArgumentException {
		if (null == dstBuffer) {
			throw new IllegalArgumentException("파라미터 목적지 버퍼는 null 입니다.");
		}

		if (offset < 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 옵셋 크기[%d]는  0 보다 크거나 같아야 합니다.", offset));
		}

		if (len <= 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 길이[%d]는  0 보다 커야 합니다.", len));
		}

		if (len > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(String.format(
					"파라미터 문자열 길이[%d]는  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
					len, CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}

		long remainingBytes = remaining();
		if (len > remainingBytes) {
			throw new IllegalArgumentException(String.format(
					"파라미터 길이[%d]는  남아 있은 버퍼 크기[%d] 보다 작거나 같아야 합니다.", len,
					remainingBytes));
		}

		if (offset >= dstBuffer.length) {
			throw new IllegalArgumentException(String.format(
					"파라미터 옵셋[%d]는  목적지 버퍼 크기[%d] 보다 작아야 합니다.", offset,
					dstBuffer.length));
		}

		if (len > dstBuffer.length) {
			throw new IllegalArgumentException(String.format(
					"파라미터 길이[%d]는  목적지 버퍼 크기[%d] 보다 작거나 같아야 합니다.", len,
					dstBuffer.length));
		}

		// int position = streamBuffer.position();

		streamBuffer.get(dstBuffer, offset, len);
	}

	@Override
	public void getBytes(byte[] targetBuffer) throws BufferUnderflowException,
			IllegalArgumentException {
		if (null == targetBuffer) {
			throw new IllegalArgumentException(
					"paramerter targetBuffer is null");
		}
		getBytes(targetBuffer, 0, targetBuffer.length);
	}

	@Override
	public byte[] getBytes(int len) throws BufferUnderflowException,
			IllegalArgumentException, OutOfMemoryError {
		if (len < 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 길이[%d]는  0 과 같거나 커야 합니다.", len));
		}

		if (len > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(String.format(
					"파라미터 문자열 길이[%d]는  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
					len, CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}

		long remainingBytes = remaining();
		if (len > remainingBytes) {
			throw new IllegalArgumentException(String.format(
					"파라미터 길이[%d]는  남아 있은 버퍼 크기[%d] 보다 작거나 같아야 합니다.", len,
					remainingBytes));
		}

		byte buffer[] = null;
		try {
			buffer = new byte[len];
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
	public void skip(int len) {
		if (len <= 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 길이[%d]는  0 보다 커야 합니다.", len));
		}

		if (len > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(String.format(
					"파라미터 문자열 길이[%d]는  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
					len, CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}

		long remainingBytes = remaining();
		if (len > remainingBytes) {
			throw new IllegalArgumentException(String.format(
					"파라미터 길이[%d]는  남아 있은 버퍼 크기[%d] 보다 작거나 같아야 합니다.", len,
					remainingBytes));
		}

		streamBuffer.position(streamBuffer.position() + len);
	}
	
	
	@Override
	public void freeDataPacketBufferList() {
		// 파라미터로 받는 고정 크기 버퍼를 입력 스트림으로 취급하므로 해당 사항 없음
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
}
