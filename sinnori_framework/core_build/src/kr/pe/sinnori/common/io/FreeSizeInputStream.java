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
import java.util.ArrayList;
import java.util.Arrays;

import kr.pe.sinnori.common.exception.SinnoriCharsetCodingException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonStaticFinal;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.util.HexUtil;

/**
 * 가변 크기를 갖는 입력 이진 스트림<br/>
 * 참고) 스트림은 데이터 패킷 버퍼 큐 관리자가 관리하는 랩 버퍼들로 구현된다.  
 * @author Jonghoon Won
 *
 */
public class FreeSizeInputStream implements CommonRootIF, InputStreamIF {
	private ArrayList<WrapBuffer> streamBufferList;
	private Charset streamCharset;
	private CharsetDecoder streamCharsetDecoder = null;
	private DataPacketBufferQueueManagerIF dataPacketBufferQueueManager = null;
	
	private ByteOrder streamByteOrder = null;
	private ByteBuffer workBuffer;
	private int indexOfWorkBuffer;
	private int dataPacketBufferMaxCntPerMessage;
	
	/**
	 * 주) 아래 shortBytes, intBytes, longBytes 는 객체 인스턴스마다 필요합니다. 만약 static 으로 만들면 thread safe 문제에 직면할 것입니다.
	 */
	/*
	private byte SHORT_BYTES[] = { CommonStaticFinal.ZERO_BYTE,
			CommonStaticFinal.ZERO_BYTE };
	private byte INTEGER_BYTES[] = { CommonStaticFinal.ZERO_BYTE,
			CommonStaticFinal.ZERO_BYTE, CommonStaticFinal.ZERO_BYTE,
			CommonStaticFinal.ZERO_BYTE };
	private byte LONG_BYTES[] = { CommonStaticFinal.ZERO_BYTE,
			CommonStaticFinal.ZERO_BYTE, CommonStaticFinal.ZERO_BYTE,
			CommonStaticFinal.ZERO_BYTE, CommonStaticFinal.ZERO_BYTE,
			CommonStaticFinal.ZERO_BYTE, CommonStaticFinal.ZERO_BYTE,
			CommonStaticFinal.ZERO_BYTE };
			*/

	private byte[] shortBytes = null;
	private byte[] intBytes = null;
	private byte[] longBytes = null;
	
	private ByteBuffer shortBuffer = null;
	private ByteBuffer intBuffer = null;
	private ByteBuffer longBuffer = null;
	
	
	/**
	 * 생성자
	 * @param streamBufferList 데이터 패킷 버퍼 목록, 주의점) 읽기 가능한 영역만을 다루기 때문에 flip 된 상태인지 확인 필요함.
	 * @param streamCharsetDecoder 스트림 문자셋 디코더
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 관리자
	 */
	public FreeSizeInputStream(ArrayList<WrapBuffer> streamBufferList, 
			CharsetDecoder  streamCharsetDecoder,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) {
		if (null == streamBufferList) {
			String errorMessage = "파라미터 데이터 패킷 버퍼 목록이 null 입니다.";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		if (null == streamCharsetDecoder) {
			String errorMessage = "파라미터 스트림 문자셋 디코더가 null 입니다.";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == dataPacketBufferQueueManager) {
			String errorMessage = "파라미터 데이터 패킷 버퍼 큐 관리자가 null 입니다.";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.streamBufferList = streamBufferList;
		this.streamCharset = streamCharsetDecoder.charset();
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		
		
		dataPacketBufferMaxCntPerMessage = dataPacketBufferQueueManager.getDataPacketBufferMaxCntPerMessage();
		if (streamBufferList.size() > dataPacketBufferMaxCntPerMessage) {
			String errorMessage = String.format(
					"파라미터 바디 버퍼 목록의 크기[%d]는 1개 메시지당 할당 받을 수 있는 최대 값[%d]을 넘을수 없습니다.",
					streamBufferList.size(), dataPacketBufferMaxCntPerMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		indexOfWorkBuffer = 0;
		workBuffer = streamBufferList.get(indexOfWorkBuffer).getByteBuffer();
		
		streamByteOrder = workBuffer.order();
		
		shortBuffer = ByteBuffer.allocate(2);
		shortBuffer.order(streamByteOrder);
		shortBytes = shortBuffer.array();
		

		intBuffer = ByteBuffer.allocate(4);
		intBuffer.order(streamByteOrder);
		intBytes = intBuffer.array();		

		longBuffer = ByteBuffer.allocate(8);
		longBuffer.order(streamByteOrder);
		longBytes = longBuffer.array();		
	}

	/**
	 * 다음 데이터를 읽기 위해서 현재 작업버퍼를 다음 버퍼로 변경한다.
	 * 
	 * @throws BufferUnderflowException
	 */
	private void nextBuffer() throws BufferUnderflowException {
		// log.info("indexOfWorkBuffer[%d]", indexOfWorkBuffer);
		// log.info("streamBufferList size[%d]", streamBufferList.size());

		if (indexOfWorkBuffer + 1 >= streamBufferList.size()) {
			throw new BufferUnderflowException();
		}

		/**
		 * 작업 버퍼 남은 용량 검사는 디버깅을 위한 코드이다.
		 */
		if (workBuffer.hasRemaining()) {
			String errorMessage = "작업 버퍼에 남은 용량이 있습니다.";
			log.warn(errorMessage);
			System.exit(1);
		}

		indexOfWorkBuffer++;
		workBuffer = streamBufferList.get(indexOfWorkBuffer).getByteBuffer();
	}
	
	/**
	 * 반복적으로 사용되는 unsinged byte 를 받아 줄 short 형 버퍼를 재사용을 위해서 초기화 한다.
	 */
	private void clearShortBuffer() {
		shortBuffer.clear();
		Arrays.fill(shortBytes, CommonStaticFinal.ZERO_BYTE);
	}

	/**
	 * 반복적으로 사용되는 unsinged short 를 받아 줄 int 형 버퍼를 재사용을 위해서 초기화 한다.
	 */
	private void clearIntBuffer() {
		intBuffer.clear();
		Arrays.fill(intBytes, CommonStaticFinal.ZERO_BYTE);
	}

	/**
	 * 반복적으로 사용되는 unsinged int 를 받아 줄 long 형 버퍼를 재사용을 위해서 초기화 한다.
	 */
	private void clearLongBuffer() {
		longBuffer.clear();
		Arrays.fill(longBytes, CommonStaticFinal.ZERO_BYTE);
	}
	
	
	/**
	 * 목적지 바이트 버퍼 크기 만큼 목적지 바이트 버퍼에 스트림의 내용을 읽어 저장한다. 주) 목적지 바이트 버퍼의 내용을 읽을려면 flip 등을 해야 한다.  
	 * 
	 * @param dstBuffer
	 *            목적지 바이트 버퍼
	 */
	private void getBytesFromWorkBuffer(ByteBuffer dstByteBuffer) {
		// log.info("getBytesFromWorkBuffer::dstBuffer=[%s]",
		// dstBuffer.toString());
		/*
		do {
			int workRemainingByte = workBuffer.remaining();
			int dstRemainingByte = dstByteBuffer.remaining();

			// FIXME!			
			// log.info("getBytesFromWorkBuffer::workBuffer=[%s]", workBuffer.toString());
			// log.info("getBytesFromWorkBuffer::dstBuffer=[%s]", dstBuffer.toString());
			// log.info("getBytesFromWorkBuffer::1.workBuffer=[%s]", workBuffer.toString());

			if (workRemainingByte > dstRemainingByte) {
				int limit = workBuffer.limit();
				workBuffer.limit(workBuffer.position() + dstRemainingByte);
				dstByteBuffer.put(workBuffer);
				workBuffer.limit(limit);
				// log.info("getBytesFromWorkBuffer::2.workBuffer=[%s]",
				// workBuffer.toString());
				break;
			}

			dstByteBuffer.put(workBuffer);
			if (!dstByteBuffer.hasRemaining())
				break;
			nextBuffer();
			
			
		} while (true);		
		
		// dstBuffer.flip();
		 */
		do {
			int len = dstByteBuffer.remaining();
			if (workBuffer.remaining() >= len) {
				int oldLimitOfSrc = workBuffer.limit();
				workBuffer.limit(workBuffer.position() + len);
				dstByteBuffer.put(workBuffer);
				workBuffer.limit(oldLimitOfSrc);				
				break;				
			} else {
				dstByteBuffer.put(workBuffer);
				nextBuffer();
			}
		} while (dstByteBuffer.hasRemaining());
		
		
	}
	
	// FIXME!
	private void getBytesFromWorkBuffer(byte[] dstBytes) {
		int offset = 0;
		int len = dstBytes.length;
		do {
			int remainingBytes = workBuffer.remaining();
			
			if (remainingBytes >= len) {
				workBuffer.get(dstBytes, offset, len);
				// len = 0;
				break;
			} else {
				workBuffer.get(dstBytes, offset, remainingBytes);
				offset += remainingBytes;
				len -= remainingBytes;
				nextBuffer();
			}
			
		} while (0 != len);
	}
	
	private void getBytesFromWorkBuffer(byte[] dstBytes, int offset, int len) {
		do {
			int remainingBytes = workBuffer.remaining();
			
			if (remainingBytes >= len) {
				workBuffer.get(dstBytes, offset, len);
				// len = 0;
				break;
			} else {
				workBuffer.get(dstBytes, offset, remainingBytes);
				offset += remainingBytes;
				len -= remainingBytes;
				nextBuffer();
			}
			
		} while (0 != len);
	}
	
	
	
	
	@Override
	public Charset getCharset() {
		return streamCharset;
	}
	
	@Override
	public byte getByte() throws BufferUnderflowException {
		byte retValue;
		
		try {
			retValue = workBuffer.get();
		} catch (BufferUnderflowException e) {
			nextBuffer();
			retValue = workBuffer.get();
		}
		/*
		if (workBuffer.hasRemaining()) {
			retValue = workBuffer.get();
		} else {
			nextBuffer();
			retValue = workBuffer.get();
		}
		*/
		return retValue;
	}

	@Override
	public short getUnsignedByte() throws BufferUnderflowException {
		short retValue = (short) (0xFF & getByte());
		return retValue;
	}

	@Override
	public short getShort() throws BufferUnderflowException {
		short retValue;

		try {
			retValue = workBuffer.getShort();
		} catch (BufferUnderflowException e) {
			// int workRemaining = workBuffer.remaining();
			clearShortBuffer();
			shortBuffer.put(workBuffer);

			nextBuffer();
			/*
			while (shortBuffer.hasRemaining()) {
				shortBuffer.put(workBuffer.get());
			}
			*/
			
			workBuffer.get(shortBytes, shortBuffer.position(), shortBuffer.remaining());
			
			shortBuffer.clear();
			retValue = shortBuffer.getShort();
		}
		return retValue;
	}

	@Override
	public int getUnsignedShort() throws BufferUnderflowException {
		int retValue;

		clearIntBuffer();

		if (ByteOrder.BIG_ENDIAN == streamByteOrder) {
			intBuffer.position(2);
			intBuffer.limit(4);
		} else {
			intBuffer.position(0);
			intBuffer.limit(2);
		}

		try {
			intBuffer.put(workBuffer.get());
			intBuffer.put(workBuffer.get());
		} catch (BufferUnderflowException e) {
			// log.info("11. workBuffer[%s]", workBuffer.toString());
			// log.info("11. valueBuffer[%s]", valueBuffer.toString());

			nextBuffer();
			
			/*
			while (intBuffer.hasRemaining()) {
				intBuffer.put(workBuffer.get());
			}
			*/
			workBuffer.get(intBytes, intBuffer.position(), intBuffer.remaining());
		}
		
		intBuffer.clear();
		// log.info("22. workBuffer[%s]", workBuffer.toString());
		// log.info("22. valueBuffer[%s]", valueBuffer.toString());

		retValue = intBuffer.getInt();

		return retValue;
	}

	@Override
	public int getInt() throws BufferUnderflowException {
		int retValue;

		try {
			retValue = workBuffer.getInt();
		} catch (BufferUnderflowException e) {
			// int workRemaining = workBuffer.remaining();
			// log.info("workRemaining=[%d]", workRemaining);
			
			
			clearIntBuffer();
			intBuffer.put(workBuffer);

			nextBuffer();
			/*
			while (intBuffer.hasRemaining()) {
				intBuffer.put(workBuffer.get());
			}
			*/
			
			workBuffer.get(intBytes, intBuffer.position(), intBuffer.remaining());
			
			intBuffer.clear();
			retValue = intBuffer.getInt();
		}
		return retValue;
	}

	@Override
	public long getUnsignedInt() throws BufferUnderflowException {
		long retValue;

		clearLongBuffer();

		if (ByteOrder.BIG_ENDIAN == streamByteOrder) {
			longBuffer.position(4);
			longBuffer.limit(8);
		} else {
			longBuffer.position(0);
			longBuffer.limit(4);
		}

		try {
			longBuffer.put(workBuffer.get());
			longBuffer.put(workBuffer.get());
			longBuffer.put(workBuffer.get());
			longBuffer.put(workBuffer.get());
		} catch (BufferUnderflowException e) {
			nextBuffer();
			/*
			while (longBuffer.hasRemaining()) {
				longBuffer.put(workBuffer.get());
			}
			*/
			workBuffer.get(longBytes, longBuffer.position(), longBuffer.remaining());
		}

		longBuffer.clear();

		retValue = longBuffer.getLong();
		return retValue;
	}

	@Override
	public long getLong() throws BufferUnderflowException {
		long retValue;

		try {
			retValue = workBuffer.getLong();
		} catch (BufferUnderflowException e) {
			// int workRemaining = workBuffer.remaining();
			clearLongBuffer();
			longBuffer.put(workBuffer);
			nextBuffer();
			/*
			while (longBuffer.hasRemaining()) {
				longBuffer.put(workBuffer.get());
			}
			*/
			workBuffer.get(longBytes, longBuffer.position(), longBuffer.remaining());

			longBuffer.clear();
			retValue = longBuffer.getLong();
		}
		return retValue;
	}

	@Override
	public String getString(final int len, final CharsetDecoder wantedCharsetDecoder)
			throws BufferUnderflowException, IllegalArgumentException, SinnoriCharsetCodingException {
		if (len < 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 길이[%d]는  0 보다 크거나 같아야 합니다.", len));
		}

		/*
		if (len > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(
					String.format(
							"파리미터 문자열 길이(=len)의 값[%d]은  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							len, CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}
		*/

		long remainingBytes = remaining();
		if (remainingBytes < len) {
			throw new IllegalArgumentException(String.format(
					"파라미터 길이[%d]는  남아 있은 버퍼 크기[%d] 보다 작거나 같아야 합니다.", len,
					remainingBytes));
		}

		ByteBuffer dstBuffer = ByteBuffer.allocate(len);
		

		getBytesFromWorkBuffer(dstBuffer);
		dstBuffer.flip();
				
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
	public String getString(int len) throws BufferUnderflowException,
			IllegalArgumentException, SinnoriCharsetCodingException {
		return getString(len, streamCharsetDecoder);
	}

	@Override
	public String getStringAll() throws BufferUnderflowException,
			IllegalArgumentException, SinnoriCharsetCodingException {
		long remainingBytes = remaining();
		/*
		if (remainingBytes > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(
					String.format(
							"문자열로 변환될 남아 있는 버퍼 크기[%d]는  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							remainingBytes,
							CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}
		*/
		
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

		/*
		if (len > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(String.format(
					"문자열 길이[%d]는  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.", len,
					CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}
		*/

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
	public void getBytes(byte[] dstBytes, int offset, int len)
			throws BufferUnderflowException, IllegalArgumentException {
		if (null == dstBytes) {
			throw new IllegalArgumentException("파라미터 목적지 버퍼는 null 입니다.");
		}

		if (offset < 0) {
			throw new IllegalArgumentException(String.format(
					"지정된 옵셋 크기[%d]는  0 보다 크거나 같아야 합니다.", offset));
		}

		if (len <= 0) {
			throw new IllegalArgumentException(String.format(
					"지정된 길이[%d]는  0 보다 커야 합니다.", len));
		}

		if (len > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(String.format(
					"파라미터 문자열 길이[%d]는  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
					len, CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}

		long remainingBytes = remaining();
		if (len > remainingBytes) {
			throw new IllegalArgumentException(String.format(
					"지정된 bye 크기[%d]는  남아 있은 버퍼 크기[%d] 보다 작거나 같아야 합니다.", len,
					remainingBytes));
		}

		if (offset >= dstBytes.length) {
			throw new IllegalArgumentException(String.format(
					"지정된 옵셋[%d]는  타겟 버퍼 크기[%d] 보다 작아야 합니다.", offset,
					dstBytes.length));
		}

		if (len > dstBytes.length) {
			throw new IllegalArgumentException(String.format(
					"지정된 길이[%d]는  타겟 버퍼 크기[%d] 보다 작거나 같아야 합니다.", len,
					dstBytes.length));
		}

		// ByteBuffer dstByteBuffer = ByteBuffer.wrap(dstBytes, offset, len);

		getBytesFromWorkBuffer(dstBytes, offset, len);
	}

	@Override
	public void getBytes(byte[] dstBytes) throws BufferUnderflowException,
			IllegalArgumentException {
		if (null == dstBytes) {
			throw new IllegalArgumentException("paramerter dstBytes is null");
		}
		
		

		// ByteBuffer dstByteBuffer = ByteBuffer.wrap(dstBytes);
		// getBytesFromWorkBuffer(dstByteBuffer);
		
		getBytesFromWorkBuffer(dstBytes);
	}

	@Override
	public byte[] getBytes(int len) throws BufferUnderflowException,
			IllegalArgumentException {
		if (len < 0) {
			throw new IllegalArgumentException(String.format(
					"지정된 길이[%d]는  0 과 같거나 커야 합니다.", len));
		}

		if (len > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(String.format(
					"파라미터 문자열 길이[%d]는  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
					len, CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}

		long remainingBytes = remaining();
		if (len > remainingBytes) {
			throw new IllegalArgumentException(String.format(
					"지정된 bye 크기[%d]는  남아 있은 버퍼 크기[%d] 보다 작거나 같아야 합니다.", len,
					remainingBytes));
		}
		
		// ByteBuffer dstBuffer = ByteBuffer.allocate(len);
		// byte srcBuffer[] = dstBuffer.array();
		// getBytesFromWorkBuffer(dstBuffer);
		// return srcBuffer;

		byte srcBytes[] = new byte[len];
		getBytesFromWorkBuffer(srcBytes);
		return srcBytes;
	}

	@Override
	public void skip(int len) throws BufferUnderflowException,
			IllegalArgumentException {
		if (len <= 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 생략할 길이[%d]는  0 보다 커야 합니다.", len));
		}

		if (len > CommonStaticFinal.MAX_UNSIGNED_BYTE) {
			throw new IllegalArgumentException(String.format(
					"파라미터 생략할 길이[%d]는  unsigned byte 최대값[%d] 보다 작거나 같아야 합니다.",
					len, CommonStaticFinal.MAX_UNSIGNED_BYTE));
		}

		long remainingBytes = remaining();
		if (len > remainingBytes) {
			throw new IllegalArgumentException(String.format(
					"파라미터 생략할 길이[%d]는  남아 있은 버퍼 크기[%d] 보다 작거나 같아야 합니다.", len,
					remainingBytes));
		}

		int dstRemainingByte = len;
		do {
			int workRemainingByte = workBuffer.remaining();
			if (dstRemainingByte < workRemainingByte) {
				workBuffer.position(workBuffer.position() + dstRemainingByte);
				dstRemainingByte = 0;
				break;
			}

			workBuffer.position(workBuffer.limit());
			dstRemainingByte -= workRemainingByte;
			if (dstRemainingByte <= 0)
				break;
			nextBuffer();
		} while (true);
	}

	@Override
	public ByteOrder getByteOrder() {
		return streamByteOrder;
	}

	@Override
	public long remaining() {
		long remaingBytes = 0;
		
		int bodyBufferSize = streamBufferList.size();
		for (int i = indexOfWorkBuffer; i < bodyBufferSize; i++) {
			remaingBytes += streamBufferList.get(i).getByteBuffer()
					.remaining();
		}
		
		return remaingBytes;
	}

	@Override
	public long position() {
		long positionInBuffer = 0;

		if (indexOfWorkBuffer > 0) {
			positionInBuffer = (indexOfWorkBuffer - 1)
					* workBuffer.capacity();
		}

		positionInBuffer += workBuffer.position();

		return positionInBuffer;
	}

	@Override
	public void freeDataPacketBufferList() {		
		int bodyBufferSize = streamBufferList.size();
		
		for (int i = 0; i < bodyBufferSize; i++) {
			dataPacketBufferQueueManager.putDataPacketBuffer(streamBufferList.remove(0));
		}
	}
	
	@Override
	public long indexOf(byte[] searchBytes) {
		// long remainingBytes = remaining();
		int streamBufferListSize = streamBufferList.size();
		
		long retPosition = 0;
		
		for (int inxOfBuffer = indexOfWorkBuffer; inxOfBuffer < streamBufferListSize; inxOfBuffer++) {
			ByteBuffer baseSearchWorkBuffer = streamBufferList.get(inxOfBuffer).getByteBuffer().duplicate();
			int baseSearchWorkRemaining = baseSearchWorkBuffer.remaining();

			/**
			 * 다음 버퍼가 없는데 남은 데이터가 검색중인 바이트 배열의 크기 보다 작은 경우 실패
			 */
			if ((inxOfBuffer+1) >= streamBufferListSize && searchBytes.length > baseSearchWorkRemaining) return -1;
	
			while (baseSearchWorkBuffer.hasRemaining()) {
				ByteBuffer searchWorkBuffer = baseSearchWorkBuffer.duplicate();
				// int searchWorkPosition = searchWorkBuffer.position();
				
				
				int j=0;
				for (; j < searchBytes.length; j++) {
					/** 작업중인 버퍼에서 한 바이트를 읽어와서 j 번째 바이트 배열과 비교하여 다르면 루프 종료. */
					if (searchWorkBuffer.get() != searchBytes[j]) break;
					
					
					if (!searchWorkBuffer.hasRemaining()) {
						/** 비교할 데이터 없음 */
						
						/** 비교할 데이터가 더 있는 경우 검색 실패 */
						if (inxOfBuffer+1 >= streamBufferListSize) return -1;
						
						/** 다음 버퍼의 내용과 바꾼후 검색중인 바이트 배열과 비교를 계속 진행한다. */
						searchWorkBuffer = streamBufferList.get(inxOfBuffer+1).getByteBuffer().duplicate();
					}
				}
				
					
				if (j == searchBytes.length) {
					return retPosition;
				}
				
				
				retPosition++;
				baseSearchWorkBuffer.get();
			}
		}
		
		return -1;
	}
}
