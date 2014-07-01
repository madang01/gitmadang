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

import kr.pe.sinnori.common.exception.SinnoriBufferUnderflowException;
import kr.pe.sinnori.common.exception.SinnoriCharsetCodingException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonStaticFinal;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.util.HexUtil;

/**
 * 가변 크기를 갖는 입력 이진 스트림<br/>
 * 참고) 스트림은 데이터 패킷 버퍼 큐 관리자가 관리하는 랩 버퍼들로 구현된다.  
 * @author Jonghoon Won
 *
 */
public class FreeSizeInputStream implements CommonRootIF, InputStreamIF {
	private ArrayList<ByteBuffer> streamBufferList;
	private Charset streamCharset;
	private CharsetDecoder streamCharsetDecoder = null;	
	private ByteOrder streamByteOrder = null;
	
	private ByteBuffer workBuffer;
	private int indexOfWorkBuffer;
	
	private long limitedSizeToRead = -1;
	
	/**
	 * 주) 아래 shortBytes, intBytes, longBytes 는 객체 인스턴스마다 필요합니다. 만약 static 으로 만들면 thread safe 문제에 직면할 것입니다.
	 */
	private byte[] shortBytes = null;
	private byte[] intBytes = null;
	private byte[] longBytes = null;
	
	private ByteBuffer shortBuffer = null;
	private ByteBuffer intBuffer = null;
	private ByteBuffer longBuffer = null;
	
	
	/**
	 * <pre>
	 * 생성자.
	 * 입려으로 받는 스트림 버퍼 목록은 동일 크기를 갖는 버퍼들로 이루어진 "앞에서 부터 꽉찬 버퍼 목록"임을 가정하여 동작한다.
	 * 먼저 스트림 버퍼 목록의 모든 버퍼의 "버퍼 크기"(=capacity) 는 동일해야 한다.
	 * "앞에서 부터 꽉찬 버퍼 목록"이란 
	 * 첫번째 부터 마지막 버퍼까지 읽어올 "시작 위치"(=position)는 0으로 시작되며, 
	 * "종료 위치"(=limit)는 마지막 버퍼를 제외하고 "버퍼 크기"(=capacity) 와 같다.
	 * 마지막 버퍼의 "종료 위치"는 0 보다 크며 "버퍼 크기"(=capacity) 보다는 작거나 같다.
	 * 
	 * - "앞에서 부터 꽉찬 버퍼 목록" 이 성립하지 않았을때 오 동작하는 메소드 목록 -
	 * 첫번째 {@link #position()}  
	 * 두번째 {@link #remaining()}
	 * </pre>
	 * 
	 * @param streamBufferList 스트림 버퍼 목록, 주의점) 동일 "버퍼 크기"(=capacity) 를 갖는 앞에서 부터 꽉찬 버퍼 목록이어야 한다.
	 * @param streamCharsetDecoder 스트림 문자셋 디코더
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자, 메시지당 받을 수 있는 최대 데이터 패킷 버퍼 갯수를 얻기 위해 사용된다.
	 */
	public FreeSizeInputStream(ArrayList<ByteBuffer> streamBufferList, 
			CharsetDecoder  streamCharsetDecoder,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) {
		if (null == streamBufferList) {
			String errorMessage = "the parameter streamBufferList is null";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		if (null == streamCharsetDecoder) {
			String errorMessage = "the parameter streamCharsetDecoder is null";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		this.streamBufferList = streamBufferList;
		this.streamCharset = streamCharsetDecoder.charset();
		this.streamCharsetDecoder = streamCharsetDecoder;
		
		int dataPacketBufferMaxCntPerMessage = dataPacketBufferQueueManager.getDataPacketBufferMaxCntPerMessage();
		if (streamBufferList.size() > dataPacketBufferMaxCntPerMessage) {
			String errorMessage = String.format(
					"the parameter streamBufferList's size is greater than The maximum number[%d] of buffers that can be assigned per one message",
					streamBufferList.size(), dataPacketBufferMaxCntPerMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		indexOfWorkBuffer = 0;
		workBuffer = streamBufferList.get(indexOfWorkBuffer);
		
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
		
		limitedSizeToRead = remaining();
	}
	
	/**
	 * @return 스트림을 구성하는 버퍼 목록
	 */
	public ArrayList<ByteBuffer> getStreamBufferList() {
		return streamBufferList;
	}
	
	public void setLimitedSizeToRead(long newLimitedWorkSize) {
		if (newLimitedWorkSize < 0) {
			String errorMessage = "the parameter newLimitedWorkSize is less than zero";
			throw new IllegalArgumentException(errorMessage);
		}
		
		long remainingBytes = remaining();
		if (newLimitedWorkSize > remainingBytes) {
			String errorMessage = String.format("the parameter newLimitedWorkSize[%d] is greater than the remaining bytes[%d]", newLimitedWorkSize, remainingBytes);
			throw new IllegalArgumentException(errorMessage);
		}		
		
		limitedSizeToRead = newLimitedWorkSize;
		//log.info(String.format("limitedSizeToRead=[%d]", limitedSizeToRead));
	}
	
	public long getLimitedSizeToRead() {
		return limitedSizeToRead;
	}
	
	public void freeLimitedSizeToRead() {
		limitedSizeToRead = remaining();
	}

	/**
	 * 다음 데이터를 읽기 위해서 현재 작업버퍼를 다음 버퍼로 변경한다.
	 * 
	 * @throws SinnoriBufferUnderflowException
	 */
	private void nextBuffer() throws SinnoriBufferUnderflowException {
		// log.info("indexOfWorkBuffer[%d]", indexOfWorkBuffer);
		// log.info("streamBufferList size[%d]", streamBufferList.size());

		if (indexOfWorkBuffer + 1 >= streamBufferList.size()) {
			throw new SinnoriBufferUnderflowException("no more data packet buffer");
		}

		/**
		 * 작업 버퍼 남은 용량 검사는 디버깅을 위한 코드이다.
		 */
		if (workBuffer.hasRemaining()) {
			String errorMessage = "the working buffer has remaining data";
			log.warn(errorMessage);
			System.exit(1);
		}

		indexOfWorkBuffer++;
		workBuffer = streamBufferList.get(indexOfWorkBuffer);
		
		// FIXME!
		//log.info(String.format("in nextBuffer, indexOfWorkBuffer=[%d], workBuffer.capacity=[%d]", indexOfWorkBuffer, workBuffer.capacity()));
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
	 * 목적지 바이트 버퍼에 스트림의 내용을 읽어 저장한다. 주) 목적지 바이트 버퍼의 내용을 읽을려면 flip 등을 해야 한다.
	 * @param dstBuffer 목적지 바이트 버퍼
	 */
	private void getBytesFromWorkBuffer(ByteBuffer dstByteBuffer) throws SinnoriBufferUnderflowException {		
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
	
	/**
	 *  목적지 바이트 배열 크기 만큼 목적지 바이트 배열에 스트림의 내용을 읽어 저장한다.
	 * @param dstBytes 목적지 바이트 버퍼
	 */
	private void getBytesFromWorkBuffer(byte[] dstBytes) throws SinnoriBufferUnderflowException {
		int offset = 0;
		int len = dstBytes.length;
		do {
			int remainingBytes = workBuffer.remaining();
			
			if (remainingBytes >= len) {
				workBuffer.get(dstBytes, offset, len);
				break;
			} else {
				workBuffer.get(dstBytes, offset, remainingBytes);
				offset += remainingBytes;
				len -= remainingBytes;
				nextBuffer();
			}
			
		} while (0 != len);
	}
	
	/**
	 * 목적지 바이트 배열에 지정된 시작위치에 지정된 크기 만큼 스트림의 내용을 읽어와서 저장한다.
	 * @param dstBytes 목적지 바이트 배열
	 * @param offset 스트림 내용이 저장될 목적지 바이트 배열의 시작 위치 
	 * @param len 스트림으로 부터 읽어 올 크기 
	 */
	private void getBytesFromWorkBuffer(byte[] dstBytes, int offset, int len) throws SinnoriBufferUnderflowException {
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
	public byte getByte() throws SinnoriBufferUnderflowException {
		if (limitedSizeToRead < 1) {
			String errorMessage = String.format("the member variable 'limitedSizeToRead'[%d] less than one byte", limitedSizeToRead);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedSizeToRead--;
		//log.info(String.format("limitedSizeToRead=[%d]", limitedSizeToRead));
		
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
	public short getUnsignedByte() throws SinnoriBufferUnderflowException {
		short retValue = (short) (0xFF & getByte());
		return retValue;
	}

	@Override
	public short getShort() throws SinnoriBufferUnderflowException {
		if (limitedSizeToRead < 2) {
			String errorMessage = String.format("the member variable 'limitedSizeToRead'[%d] less than two bytes", limitedSizeToRead);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedSizeToRead-=2;
		//log.info(String.format("limitedSizeToRead=[%d]", limitedSizeToRead));
		
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
	public int getUnsignedShort() throws SinnoriBufferUnderflowException {
		if (limitedSizeToRead < 2) {
			String errorMessage = String.format("the member variable 'limitedSizeToRead'[%d] less than two bytes", limitedSizeToRead);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedSizeToRead-=2;
		//log.info(String.format("limitedSizeToRead=[%d]", limitedSizeToRead));
		
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
	public int getInt() throws SinnoriBufferUnderflowException {
		if (limitedSizeToRead < 4) {
			String errorMessage = String.format("the member variable 'limitedSizeToRead'[%d] less than four bytes", limitedSizeToRead);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedSizeToRead-=4;
		//log.info(String.format("limitedSizeToRead=[%d]", limitedSizeToRead));
		
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
	public long getUnsignedInt() throws SinnoriBufferUnderflowException {
		if (limitedSizeToRead < 4) {
			String errorMessage = String.format("the member variable 'limitedSizeToRead'[%d] less than four bytes", limitedSizeToRead);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedSizeToRead-=4;
		//log.info(String.format("limitedSizeToRead=[%d]", limitedSizeToRead));
		
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
	public long getLong() throws SinnoriBufferUnderflowException {
		if (limitedSizeToRead < 8) {
			String errorMessage = String.format("the member variable 'limitedSizeToRead'[%d] less than eight bytes", limitedSizeToRead);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedSizeToRead-=8;
		//log.info(String.format("limitedSizeToRead=[%d]", limitedSizeToRead));
		
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
			throws SinnoriBufferUnderflowException, IllegalArgumentException, SinnoriCharsetCodingException {
		if (len < 0) {
			throw new IllegalArgumentException(String.format(
					"parameter len[%d] less than zero", len));
		}

		if (len > limitedSizeToRead) {
			String errorMessage = String.format("parameter len[%d] greater than the member variable 'limitedSizeToRead'[%d]", len, limitedSizeToRead);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedSizeToRead-=len;
		//log.info(String.format("limitedSizeToRead=[%d]", limitedSizeToRead));

		/*long remainingBytes = remaining();
		if (remainingBytes < len) {
			throw new IllegalArgumentException(String.format(
					"파라미터 길이[%d]는  남아 있은 버퍼 크기[%d] 보다 작거나 같아야 합니다.", len,
					remainingBytes));
		}*/

		ByteBuffer dstBuffer = null;
		try {
			dstBuffer = ByteBuffer.allocate(len);
		} catch (OutOfMemoryError e) {
			log.warn("OutOfMemoryError", e);
			throw e;
		}
		

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
	public String getString(int len) throws SinnoriBufferUnderflowException,
			IllegalArgumentException, SinnoriCharsetCodingException {
		return getString(len, streamCharsetDecoder);
	}

	@Override
	public String getStringAll() throws SinnoriBufferUnderflowException,
			IllegalArgumentException, SinnoriCharsetCodingException {
		long remainingBytes = remaining();
		
		/**
		 * 자바 문자열에 입력 가능한 바이트 배열의 크기는 Integer.MAX_VALUE 이다.
		 */
		if (remainingBytes > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					String.format(
							"the remaing bytes[%d] of stream is greater than the maximum value[%d] of integer",
							remainingBytes,
							Integer.MAX_VALUE));
		}
		
		if (0 == remainingBytes) return "";
		
		return getString((int) remainingBytes, streamCharsetDecoder);
	}

	@Override
	public String getPascalString() throws SinnoriBufferUnderflowException,
			IllegalArgumentException, SinnoriCharsetCodingException {
		return getUBPascalString();
	}

	@Override
	public String getSIPascalString() throws SinnoriBufferUnderflowException,
			IllegalArgumentException, SinnoriCharsetCodingException {
		int len = getInt();
		if (len < 0)
			throw new IllegalArgumentException(String.format(
					"the pascal string length[%d] whose type is integer is less than zero", len));

		if (0 == len) return "";

		return getString(len, streamCharsetDecoder);
	}

	@Override
	public String getUSPascalString() throws SinnoriBufferUnderflowException,
			IllegalArgumentException, SinnoriCharsetCodingException {
		int numOfBytes = getUnsignedShort();
		if (0 == numOfBytes)
			return "";
		return getString(numOfBytes, streamCharsetDecoder);
	}

	@Override
	public String getUBPascalString() throws SinnoriBufferUnderflowException,
	IllegalArgumentException, SinnoriCharsetCodingException {
		int numOfBytes = getUnsignedByte();

		if (0 == numOfBytes)
			return "";
		return getString(numOfBytes, streamCharsetDecoder);
	}

	@Override
	public void getBytes(byte[] dstBytes, int offset, int len)
			throws SinnoriBufferUnderflowException, IllegalArgumentException {
		if (null == dstBytes) {
			throw new IllegalArgumentException("parameter dstBytes is null");
		}

		if (offset < 0) {
			throw new IllegalArgumentException(String.format("parameter offset[%d] less than zero", offset));
		}

		if (len <= 0) {
			throw new IllegalArgumentException(String.format(
					"parameter len[%d] less than or equal to zero", len));
		}
		
		if (offset >= dstBytes.length) {
			throw new IllegalArgumentException(String.format(
					"parameter offset[%d] greater than or equal to the dest buffer's length[%d]", offset, dstBytes.length));
		}

		if (len > dstBytes.length) {
			throw new IllegalArgumentException(String.format(
					"parameter len[%d] greater than the dest buffer's length[%d]", len, dstBytes.length));
		}
		
		if (len > limitedSizeToRead) {
			String errorMessage = String.format("parameter len[%d] gretater than the member variable 'limitedSizeToRead'[%d]", len, limitedSizeToRead);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedSizeToRead-=len;
		//log.info(String.format("limitedSizeToRead=[%d]", limitedSizeToRead));
		
		/*long remainingBytes = remaining();
		if (len > remainingBytes) {
			throw new IllegalArgumentException(String.format(
					"지정된 bye 크기[%d]는  남아 있은 버퍼 크기[%d] 보다 작거나 같아야 합니다.", len,
					remainingBytes));
		}*/

		getBytesFromWorkBuffer(dstBytes, offset, len);
	}

	@Override
	public void getBytes(byte[] dstBytes) throws SinnoriBufferUnderflowException,
			IllegalArgumentException {
		if (null == dstBytes) {
			throw new IllegalArgumentException("paramerter dstBytes is null");
		}
		
		if (dstBytes.length > limitedSizeToRead) {
			String errorMessage = String.format("parameter dstBytes's length[%d] greater than the member variable 'limitedSizeToRead'[%d]", dstBytes.length, limitedSizeToRead);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedSizeToRead-=dstBytes.length;
		//log.info(String.format("limitedSizeToRead=[%d]", limitedSizeToRead));
		
		getBytesFromWorkBuffer(dstBytes);
	}

	@Override
	public byte[] getBytes(int len) throws SinnoriBufferUnderflowException,
			IllegalArgumentException {
		if (len < 0) {
			throw new IllegalArgumentException(String.format(
					"parameter len[%d] less than zero", len));
		}

		if (len > limitedSizeToRead) {
			String errorMessage = String.format("parameter len[%d] gretater than the member variable 'limitedSizeToRead'[%d]", len, limitedSizeToRead);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedSizeToRead-=len;
		//log.info(String.format("limitedSizeToRead=[%d]", limitedSizeToRead));

		/*long remainingBytes = remaining();
		if (len > remainingBytes) {
			throw new IllegalArgumentException(String.format(
					"지정된 bye 크기[%d]는  남아 있은 버퍼 크기[%d] 보다 작거나 같아야 합니다.", len,
					remainingBytes));
		}*/
	
		byte srcBytes[] = null;
		try {
			srcBytes = new byte[len];
		} catch (OutOfMemoryError e) {
			log.warn("OutOfMemoryError", e);
			throw e;
		}
		getBytesFromWorkBuffer(srcBytes);
		return srcBytes;
	}

	@Override
	public void skip(int len) throws SinnoriBufferUnderflowException,
			IllegalArgumentException {
		if (0 == len) return;
		
		if (len < 0) {
			throw new IllegalArgumentException(String.format(
					"parameter len[%d] less than zero", len));
		}

		if (len > limitedSizeToRead) {
			String errorMessage = String.format("parameter len[%d] gretater than the member variable 'limitedSizeToRead'[%d]", len, limitedSizeToRead);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedSizeToRead-=len;
		//log.info(String.format("limitedSizeToRead=[%d]", limitedSizeToRead));
		
		
		/*long remainingBytes = remaining();
		if (len > remainingBytes) {
			throw new IllegalArgumentException(String.format(
					"파라미터 생략할 길이[%d]는  남아 있은 버퍼 크기[%d] 보다 작거나 같아야 합니다.", len,
					remainingBytes));
		}*/

		int dstRemainingByte = len;
		do {
			int workRemainingByte = workBuffer.remaining();
			if (dstRemainingByte <= workRemainingByte) {
				workBuffer.position(workBuffer.position() + dstRemainingByte);
				dstRemainingByte = 0;
				break;
			}

			workBuffer.position(workBuffer.limit());
			dstRemainingByte -= workRemainingByte;
			
			/*if (dstRemainingByte <= 0) {
				log.warn(
String.format("dstRemainingByte equal to or less than zero, maybe remaining() bug, remainingBytes=[%d], len=[%d]"
					, remainingBytes, len));
				break;
			}*/
			nextBuffer();
		} while (true);
	}
	
	public void skip(long len) throws SinnoriBufferUnderflowException, IllegalArgumentException {
		if (0 == len) return;
		
		if (len < 0) {
			throw new IllegalArgumentException(String.format(
					"parameter len[%d] less than zero", len));
		}
		
		if (len > limitedSizeToRead) {
			String errorMessage = String.format("parameter len[%d] gretater than the member variable 'limitedSizeToRead'[%d]", len, limitedSizeToRead);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedSizeToRead-=len;
		//log.info(String.format("limitedSizeToRead=[%d]", limitedSizeToRead));
		
		/*long remainingBytes = remaining();
		if (len > remainingBytes) {
			throw new IllegalArgumentException(String.format(
					"파라미터 생략할 길이[%d]는  남아 있은 버퍼 크기[%d] 보다 작거나 같아야 합니다.", len,
					remainingBytes));
		}*/
		
		long dstRemainingByte = len;
		do {
			int workRemainingByte = workBuffer.remaining();
			if (dstRemainingByte <= workRemainingByte) {
				workBuffer.position(workBuffer.position() + (int)dstRemainingByte);
				dstRemainingByte = 0;
				break;
			}
		
			workBuffer.position(workBuffer.limit());
			dstRemainingByte -= workRemainingByte;
			/*if (dstRemainingByte <= 0) {
				log.warn(
String.format("dstRemainingByte equal to or less than zero, maybe remaining() bug, remainingBytes=[%d], len=[%d]"
						, remainingBytes, len));
				break;
			}*/
			nextBuffer();
		} while (true);
	}

	@Override
	public ByteOrder getByteOrder() {
		return streamByteOrder;
	}
	
	@Override
	public Charset getCharset() {
		return streamCharset;
	}

	@Override
	public long remaining() {
		long remaingBytes = 0;

		remaingBytes += streamBufferList.get(indexOfWorkBuffer).remaining();
		
		int streamBufferListSize = streamBufferList.size();		
		int lastIndexOfStreamBufferList = streamBufferListSize -1;
		
		if (lastIndexOfStreamBufferList != indexOfWorkBuffer) {
			int countOfFullStreamBuffer = lastIndexOfStreamBufferList - indexOfWorkBuffer - 1;
			remaingBytes += countOfFullStreamBuffer * (long)workBuffer.capacity();
			
			remaingBytes += streamBufferList.get(lastIndexOfStreamBufferList).remaining();
		}
		
		// FIXME! debug code
		/*long debugRemaingBytes = 0;
		for (int i = indexOfWorkBuffer; i < streamBufferListSize; i++) {
			debugRemaingBytes += streamBufferList.get(i).remaining();
		}
		if (debugRemaingBytes != remaingBytes) {
			log.warn(String.format("남아 있는 양 공식[%d]과 실체 측정치[%d] 다름", remaingBytes, debugRemaingBytes));
		}*/
		
		return remaingBytes;
	}

	@Override
	/**
	 * 주의 : "앞에서 부터 꽉찬 버퍼 목록" 이 전제되어야 정상 동작한다.
	 */
	public long position() {
		// FIXME!
		// log.info(String.format("in position, indexOfWorkBuffer=[%d], workBuffer.capacity=[%d]", indexOfWorkBuffer, workBuffer.capacity()));
		
		long positionInBuffer = indexOfWorkBuffer*(long)workBuffer.capacity() 
				+ workBuffer.position();

		return positionInBuffer;
	}
	
	public int getIndexOfWorkBuffer() {
		return indexOfWorkBuffer;
	}
	
	public int getPositionOfWorkBuffer() {
		return workBuffer.position();
	}
	
	
	@Override
	public long indexOf(byte[] searchBytes) {
		// long remainingBytes = remaining();
		int streamBufferListSize = streamBufferList.size();
		
		long retPosition = 0;
		
		for (int inxOfBuffer = indexOfWorkBuffer; inxOfBuffer < streamBufferListSize; inxOfBuffer++) {
			ByteBuffer baseSearchWorkBuffer = streamBufferList.get(inxOfBuffer).duplicate();
			baseSearchWorkBuffer.order(streamByteOrder);
			int baseSearchWorkRemaining = baseSearchWorkBuffer.remaining();

			/**
			 * 다음 버퍼가 없는데 남은 데이터가 검색중인 바이트 배열의 크기 보다 작은 경우 실패
			 */
			if ((inxOfBuffer+1) >= streamBufferListSize && searchBytes.length > baseSearchWorkRemaining) return -1;
	
			while (baseSearchWorkBuffer.hasRemaining()) {
				ByteBuffer searchWorkBuffer = baseSearchWorkBuffer.duplicate();
				searchWorkBuffer.order(streamByteOrder);
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
						searchWorkBuffer = streamBufferList.get(inxOfBuffer+1).duplicate();
						searchWorkBuffer.order(streamByteOrder);
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
