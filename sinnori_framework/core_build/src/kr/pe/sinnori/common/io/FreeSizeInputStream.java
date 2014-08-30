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
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Arrays;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriBufferUnderflowException;
import kr.pe.sinnori.common.exception.SinnoriCharsetCodingException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.lib.CommonType;
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
	private ArrayList<WrapBuffer> dataPacketBufferList = null;
	private CommonType.WRAPBUFFER_RECALL_GUBUN memoryRecallGubun;
	private ArrayList<ByteBuffer> streamBufferList = null;
	private Charset streamCharset;
	private CharsetDecoder streamCharsetDecoder = null;	
	private ByteOrder streamByteOrder = null;
	private DataPacketBufferQueueManagerIF dataPacketBufferQueueManager = null;
	
	
	private ByteBuffer workBuffer;
	private int indexOfWorkBuffer;
	
	private long limitedRemainingBytes = -1;
	
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
	 * 
	 * - 데이터 패킷 버퍼 반환 구분 -
	 * (1) 랩 버퍼 비 회수 : 파라미터로 넘어온 데이터 패킷 버퍼 목록 속성을 보존하기 위해 복사를 한 버퍼 목록 으로부터 스트림 구성한다. 
	 * 스트림을 닫으면 파라미터로 넘어온 데이터 패킷 버퍼 목록을 회수되지 않고 
	 * 단지 모든 스트림을 읽은 상태로 만든다.
	 * 스트림이 닫힌후 데이터를 요청하거나 
	 *   
	 * (2) 랩 버퍼 회수 : 파라미터로 넘어온 데이터 패킷 버퍼 목록으로 부터 얻은 버퍼 목록으로 부터 스트림을 구성한다.
	 *  스트림을 닫으면  파라미터로 넘어온 데이터 패킷 버퍼 목록을 회수을 하고 스트림 운영 변수들을 닫은 상태로 설정한다.
	 * 
	 * </pre>
	 * 
	 * @param dataPacketBufferList 데이터 패킷 버퍼 목록
	 * @param wrapbufferRecallGubun 데이터 패킷 버퍼 반환 구분, (1) 랩 버퍼 비 회수 와 (2) 랩 버퍼 회수 가 있다.  
	 * @param streamCharsetDecoder 스트림 문자셋 디코더
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자, 메시지당 받을 수 있는 최대 데이터 패킷 버퍼 갯수를 얻기 위해 사용된다.
	 */
	public FreeSizeInputStream(ArrayList<WrapBuffer> dataPacketBufferList,
			CommonType.WRAPBUFFER_RECALL_GUBUN wrapbufferRecallGubun,
			CharsetDecoder  streamCharsetDecoder,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) {
		if (null == dataPacketBufferList) {
			String errorMessage = "the parameter dataPacketBufferList is null";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == streamCharsetDecoder) {
			String errorMessage = "the parameter streamCharsetDecoder is null";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		this.dataPacketBufferList = dataPacketBufferList;
		this.memoryRecallGubun = wrapbufferRecallGubun;
		this.streamCharset = streamCharsetDecoder.charset();
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.streamByteOrder = dataPacketBufferQueueManager.getByteOrder();
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;		
		this.streamBufferList = new ArrayList<ByteBuffer>();
		
		if (wrapbufferRecallGubun == CommonType.WRAPBUFFER_RECALL_GUBUN.WRAPBUFFER_RECALL_NO) {
			/** 파라미터 데이터 패킷 버퍼목록 비 회수 */
			for (WrapBuffer wrapBuffer : dataPacketBufferList) {
				ByteBuffer dupByteBuffer = wrapBuffer.getByteBuffer().duplicate();
				dupByteBuffer.order(streamByteOrder);
				dupByteBuffer.flip();
				this.streamBufferList.add(dupByteBuffer);
			}
		} else {
			/** 파라미터 데이터 패킷 버퍼목록 회수 */
			for (WrapBuffer wrapBuffer : dataPacketBufferList) {
				this.streamBufferList.add(wrapBuffer.getByteBuffer());
			}
		}
		
		int dataPacketBufferMaxCntPerMessage = dataPacketBufferQueueManager.getDataPacketBufferMaxCntPerMessage();
		if (streamBufferList.size() > dataPacketBufferMaxCntPerMessage) {
			String errorMessage = String.format(
					"the parameter streamBufferList's size is greater than The maximum number[%d] of buffers that can be assigned per one message",
					streamBufferList.size(), dataPacketBufferMaxCntPerMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (streamBufferList.size() > 0) {
			indexOfWorkBuffer = 0;
			workBuffer = streamBufferList.get(indexOfWorkBuffer);
			
			shortBuffer = ByteBuffer.allocate(2);
			shortBuffer.order(streamByteOrder);
			shortBytes = shortBuffer.array();

			intBuffer = ByteBuffer.allocate(4);
			intBuffer.order(streamByteOrder);
			intBytes = intBuffer.array();		

			longBuffer = ByteBuffer.allocate(8);
			longBuffer.order(streamByteOrder);
			longBytes = longBuffer.array();
		} else {
			indexOfWorkBuffer = -1;
			workBuffer = null;
		}
		
		limitedRemainingBytes = remaining();
		
		// log.info("limitedRemainingBytes={}, streamBufferList size={}", limitedRemainingBytes, streamBufferList.size());
	}
	
	
	
	

	/**
	 * 다음 데이터를 읽기 위해서 현재 작업버퍼를 다음 버퍼로 변경한다.
	 * 
	 * @throws SinnoriBufferUnderflowException
	 */
	private void nextBuffer() throws SinnoriBufferUnderflowException {
		// FIXME!
		// log.info("indexOfWorkBuffer=[{}], streamBufferList size=[{}]", indexOfWorkBuffer, streamBufferList.size());

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
		
		//log.info(String.format("in nextBuffer, indexOfWorkBuffer=[%d], workBuffer.capacity=[%d]", indexOfWorkBuffer, workBuffer.capacity()));
	}
	
	/**
	 * 반복적으로 사용되는 unsinged byte 를 받아 줄 short 형 버퍼를 재사용을 위해서 초기화 한다.
	 */
	private void clearShortBuffer() {
		shortBuffer.clear();
		Arrays.fill(shortBytes, CommonStaticFinalVars.ZERO_BYTE);
	}

	/**
	 * 반복적으로 사용되는 unsinged short 를 받아 줄 int 형 버퍼를 재사용을 위해서 초기화 한다.
	 */
	private void clearIntBuffer() {
		intBuffer.clear();
		Arrays.fill(intBytes, CommonStaticFinalVars.ZERO_BYTE);
	}

	/**
	 * 반복적으로 사용되는 unsinged int 를 받아 줄 long 형 버퍼를 재사용을 위해서 초기화 한다.
	 */
	private void clearLongBuffer() {
		longBuffer.clear();
		Arrays.fill(longBytes, CommonStaticFinalVars.ZERO_BYTE);
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
		/*if (null == workBuffer) {
			String errorMessage = "input stream closed";
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}*/
		
		if (limitedRemainingBytes < 1) {
			String errorMessage = String.format("the member variable 'limitedRemainingBytes'[%d] less than one byte", limitedRemainingBytes);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedRemainingBytes--;
		//log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));
		
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
		/*if (null == workBuffer) {
			String errorMessage = "input stream closed";
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}*/
		
		if (limitedRemainingBytes < 2) {
			String errorMessage = String.format("the member variable 'limitedRemainingBytes'[%d] less than two bytes", limitedRemainingBytes);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedRemainingBytes-=2;
		//log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));
		
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
		/*if (null == workBuffer) {
			String errorMessage = "input stream closed";
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}*/
		
		if (limitedRemainingBytes < 2) {
			String errorMessage = String.format("the member variable 'limitedRemainingBytes'[%d] less than two bytes", limitedRemainingBytes);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedRemainingBytes-=2;
		//log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));
		
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
		/*if (null == workBuffer) {
			String errorMessage = "input stream closed";
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}*/
		
		if (limitedRemainingBytes < 4) {
			String errorMessage = String.format("the member variable 'limitedRemainingBytes'[%d] is less than four bytes", limitedRemainingBytes);
			SinnoriBufferUnderflowException e = new SinnoriBufferUnderflowException(errorMessage);
			log.info(errorMessage, e);
			throw e;
		}
		limitedRemainingBytes-=4;
		//log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));
		
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
		/*if (null == workBuffer) {
			String errorMessage = "input stream closed";
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}*/
		
		if (limitedRemainingBytes < 4) {
			String errorMessage = String.format("the member variable 'limitedRemainingBytes'[%d] less than four bytes", limitedRemainingBytes);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedRemainingBytes-=4;
		//log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));
		
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
		/*if (null == workBuffer) {
			String errorMessage = "input stream closed";
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}*/
		
		if (limitedRemainingBytes < 8) {
			String errorMessage = String.format("the member variable 'limitedRemainingBytes'[%d] less than eight bytes", limitedRemainingBytes);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedRemainingBytes-=8;
		//log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));
		
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
		
		/*if (null == workBuffer) {
			String errorMessage = "input stream closed";
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}*/

		if (len > limitedRemainingBytes) {
			String errorMessage = String.format("parameter len[%d] greater than the member variable 'limitedRemainingBytes'[%d]", len, limitedRemainingBytes);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedRemainingBytes-=len;
		//log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));

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
			String errorMessage = String.format("read data hex[%s], charset[%s]", HexUtil.getAllHexStringFromByteBuffer(dstBuffer), wantedCharsetDecoder.charset().name());
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
		// long remainingBytes = remaining();
		
		if (0 == limitedRemainingBytes) return "";
		else if (limitedRemainingBytes > Integer.MAX_VALUE) {
			/**
			 * 자바 문자열에 입력 가능한 바이트 배열의 크기는 Integer.MAX_VALUE 이다.
			 */
			throw new IllegalArgumentException(
					String.format(
							"the remaing bytes[%d] of stream is greater than the maximum value[%d] of integer",
							limitedRemainingBytes,
							Integer.MAX_VALUE));
		}
		
		return getString((int) limitedRemainingBytes, streamCharsetDecoder);
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
		
		/*if (null == workBuffer) {
			String errorMessage = "input stream closed";
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}*/
		
		if (len > limitedRemainingBytes) {
			String errorMessage = String.format("parameter len[%d] gretater than the member variable 'limitedRemainingBytes'[%d]", len, limitedRemainingBytes);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedRemainingBytes-=len;
		//log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));
		
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
		
		/*if (null == workBuffer) {
			String errorMessage = "input stream closed";
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}*/
		
		if (dstBytes.length > limitedRemainingBytes) {
			String errorMessage = String.format("parameter dstBytes's length[%d] greater than the member variable 'limitedRemainingBytes'[%d]", dstBytes.length, limitedRemainingBytes);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedRemainingBytes-=dstBytes.length;
		//log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));
		
		getBytesFromWorkBuffer(dstBytes);
	}

	@Override
	public byte[] getBytes(int len) throws SinnoriBufferUnderflowException,
			IllegalArgumentException {
		if (len < 0) {
			throw new IllegalArgumentException(String.format(
					"parameter len[%d] less than zero", len));
		}

		/*if (null == workBuffer) {
			String errorMessage = "input stream closed";
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}*/
		
		if (len > limitedRemainingBytes) {
			String errorMessage = String.format("parameter len[%d] gretater than the member variable 'limitedRemainingBytes'[%d]", len, limitedRemainingBytes);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedRemainingBytes-=len;
		//log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));

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

		/*if (null == workBuffer) {
			String errorMessage = "input stream closed";
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}*/
		
		if (len > limitedRemainingBytes) {
			String errorMessage = String.format("parameter len[%d] gretater than the member variable 'limitedRemainingBytes'[%d]", len, limitedRemainingBytes);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedRemainingBytes-=len;
		//log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));
		
		
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
		
		/*if (null == workBuffer) {
			String errorMessage = "input stream closed";
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}*/
		
		if (len > limitedRemainingBytes) {
			String errorMessage = String.format("parameter len[%d] gretater than the member variable 'limitedRemainingBytes'[%d]", len, limitedRemainingBytes);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		limitedRemainingBytes-=len;
		//log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));
		
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
	/**
	 * 주의 : "앞에서 부터 꽉찬 버퍼 목록" 이 전제되어야 정상 동작한다.
	 */
	public long remaining() {
		if (null == workBuffer) {
			return 0L;
		}
		
		long remaingBytes = streamBufferList.get(indexOfWorkBuffer).remaining();
		int lastIndexOfStreamBufferList = streamBufferList.size() -1;
		
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
		// log.info(String.format("in position, indexOfWorkBuffer=[%d], workBuffer.capacity=[%d]", indexOfWorkBuffer, workBuffer.capacity()));
		if (null == workBuffer) {
			return 0L;
		}
		
		long positionInBuffer = indexOfWorkBuffer*(long)workBuffer.capacity() 
				+ workBuffer.position();

		return positionInBuffer;
	}
	
	public int getIndexOfWorkBuffer() {
		return indexOfWorkBuffer;
	}
	
	public int getPositionOfWorkBuffer() {
		if (null == workBuffer) {
			return 0;
		}
		return workBuffer.position();
	}
	
	
	@Override
	public long indexOf(byte[] searchBytes) {
		if (null == workBuffer) {
			return -1L;
		}
		
		/**
		 * <pre>
		 * 비록 순차 검색 효율은 떨어지지만 안전하다.
		 * 스트림을 현재 위치에서 부터 1 byte 씩 증가하여 찾고자 하는 바이트 배열을 순차 비교하여 
		 * 일치하는 위치를 현재 위치로 부터의 상대 위치로 반환다.
		 * 일치하는 위치가 없다면 -1을 반환다.
		 * </pre>
		 */
		
		int streamBufferListSize = streamBufferList.size();
		
		long startIndex = 0;
		
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
				
				/** 비교할 지점(startIndex) 에서 부터 찾고자 하는 바이트 배열을 순차 비교한다. */
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
				
				/** 바이트 배열과 일치하면 비교할 지점을 반환한다. */
				if (j == searchBytes.length) {
					return startIndex;
				}
				
				/** 바이트 배열과 일치하지 않으면 비교할 지점을 1 byte 증가 */
				startIndex++;
				baseSearchWorkBuffer.get();
			}
		}
		
		return -1;
	}
	
	/**
	 * 스트림에서 지정된 크기만큼 MD5를 구한다. 복사된 스트림를 통해서 MD5를 구하기때문에 스트림 속성에 영향을 주지 않는다.
	 * @param size md5 를 구하고자 하는 스트림 내의 크기, 단 제한된 잔존 크기
	 * @param md5 객체 생성 비용을 줄이기 위해 받은 md5 객체
	 * @return
	 * @throws IllegalArgumentException
	 * @throws SinnoriBufferUnderflowException
	 */
	public byte[] getMD5FromDupStream(long size, java.security.MessageDigest md5) throws IllegalArgumentException, SinnoriBufferUnderflowException {
		if (size <= 0) {
			String errorMessage = new StringBuilder("parameter size[").append(size).append("] is less than or equal to zero").toString();
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (size > limitedRemainingBytes) {
			String errorMessage = String.format("parameter size'[%d] greater than limitedRemainingBytes[%d]", size, limitedRemainingBytes);
			log.info(errorMessage);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		
		int streamBufferListSize = streamBufferList.size();

		byte headerMD5[] = null;
		
		for (int i = indexOfWorkBuffer; i < streamBufferListSize; i++) {
			ByteBuffer dupByteBuffer = streamBufferList.get(i)
					.duplicate();
			dupByteBuffer.order(streamByteOrder);
			// log.info(String.format("1.i[%d] spaceBytesOfHeaderMD5[%d] %s", i, spaceBytesOfHeaderMD5, dupByteBuffer.toString()));
			
			
			int remainingBytesOfDupBuffer = dupByteBuffer
					.remaining();
			if (size <= remainingBytesOfDupBuffer) {
				dupByteBuffer.limit(dupByteBuffer.position()
						+ (int)size);
				
				// FIXME!
				//log.info(String.format("3.i[%d] spaceBytesOfHeaderMD5[%d]", i, spaceBytesOfHeaderMD5));
				//log.info(String.format("%s", dupByteBuffer.toString()));
				//log.info(String.format("%s", HexUtil.byteBufferAvailableToHex(dupByteBuffer)));
				
				md5.update(dupByteBuffer);
				headerMD5 = md5.digest();
				
				//log.info(String.format("3.%s", HexUtil.byteArrayAllToHex(headerMD5)));
				break;
			} else {
				// FIXME!
				//log.info(String.format("2.i[%d] spaceBytesOfHeaderMD5[%d] %s", i, spaceBytesOfHeaderMD5, dupByteBuffer.toString()));
				
				md5.update(dupByteBuffer);
				size -= remainingBytesOfDupBuffer;
			}
		}
		return headerMD5;
	}
	
	public void setLimitedRemainingBytes(long newLimitedRemainingBytes) throws SinnoriBufferUnderflowException {
		if (newLimitedRemainingBytes < 0) {
			String errorMessage = "the parameter newLimitedWorkSize is less than zero";
			throw new IllegalArgumentException(errorMessage);
		}
		
		long remainingBytes = remaining();
		if (newLimitedRemainingBytes > remainingBytes) {
			String errorMessage = String.format("the parameter newLimitedWorkSize[%d] is greater than the remaining bytes[%d]", newLimitedRemainingBytes, remainingBytes);
			throw new SinnoriBufferUnderflowException(errorMessage);
		}		
		
		limitedRemainingBytes = newLimitedRemainingBytes;
		//log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));
	}
	
	public long getLimitedRemainingBytes() {
		return limitedRemainingBytes;
	}
	
	public void resetLimitedRemainingBytes() {
		limitedRemainingBytes = remaining();
	}
	
	// FIXME!
	public FreeSizeInputStream getInputStream(long wantedInputStreamSize) throws IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferUnderflowException {
		if (wantedInputStreamSize < 0) {
			String errorMessage = "the parameter oneMessageStreamSize is less than zero";
			throw new IllegalArgumentException(errorMessage);
		}
		
		ArrayList<WrapBuffer> dstDataPacketBufferList = new ArrayList<WrapBuffer>();
		
		if (0 == wantedInputStreamSize) {
			log.info("wantedInputStreamSize is zero, so return empty FreeSizeInputStream");
			return new FreeSizeInputStream(dstDataPacketBufferList, CommonType.WRAPBUFFER_RECALL_GUBUN.WRAPBUFFER_RECALL_YES, streamCharsetDecoder, dataPacketBufferQueueManager); 
		}
		
		long remainingBytes = remaining();
		if (wantedInputStreamSize > remainingBytes) {
			/** 참고) 스트림이 닫혔을 경우에도 이곳 로직으로 들어온다. */
			String errorMessage = String.format("the parameter oneMessageInputStreamSize[%d] is greater than the remaining bytes[%d]", wantedInputStreamSize, remainingBytes);
			log.warn(errorMessage);
			
			throw new SinnoriBufferUnderflowException(errorMessage);
		}
		
		
		// int bufferCapacity = workBuffer.capacity();
		// int oneMessageBufferSize = (int)((oneMessageInputStreamSize + bufferCapacity - 1) / bufferCapacity);
		long remainingBytesOfOneMesssageStream = wantedInputStreamSize;
		WrapBuffer dstWrapBuffer = null;
		try {
			dstWrapBuffer = dataPacketBufferQueueManager.pollDataPacketBuffer();
		} catch (NoMoreDataPacketBufferException e) {
			for (WrapBuffer workWrapBuffer : dstDataPacketBufferList) {
				dataPacketBufferQueueManager.putDataPacketBuffer(workWrapBuffer);
			}
			throw e;
		}
		dstDataPacketBufferList.add(dstWrapBuffer);
		ByteBuffer dstByteBuffer = dstWrapBuffer.getByteBuffer();
		
		do {
			int remainingBytesOfBuffer = workBuffer.remaining();
			// FIXME!
			// log.info("remainingBytesOfOneMesssageStream=[{}], remainingBytesOfBuffer=[{}], indexOfWorkBuffer=[{}]", remainingBytesOfOneMesssageStream, remainingBytesOfBuffer, indexOfWorkBuffer);
			
			if (remainingBytesOfBuffer < remainingBytesOfOneMesssageStream) {
				try {
					dstByteBuffer.put(workBuffer);
				} catch(BufferOverflowException e) {
					try {
						dstWrapBuffer = dataPacketBufferQueueManager.pollDataPacketBuffer();
					} catch (NoMoreDataPacketBufferException e1) {
						for (WrapBuffer workWrapBuffer : dstDataPacketBufferList) {
							dataPacketBufferQueueManager.putDataPacketBuffer(workWrapBuffer);
						}
						throw e1;
					}
					dstDataPacketBufferList.add(dstWrapBuffer);
					dstByteBuffer = dstWrapBuffer.getByteBuffer();
					
					// FIXME!
					// log.info("1. new dstByteBuffer=[{}], workBuffer=[{}]", dstByteBuffer.toString(), workBuffer.toString());
					
					dstByteBuffer.put(workBuffer);
				}
				remainingBytesOfOneMesssageStream -= remainingBytesOfBuffer;
				// if (remainingBytesOfOneMesssageStream == 0) break;
				try {
					nextBuffer();
				} catch (SinnoriBufferUnderflowException e) {
					// String errorMessage = e.getMessage();
					log.error("원인 추적후 제거 필요. 스트림에 남아 있는 양보다 작거나 같은 크기", e);
					System.exit(1);
				}
			} else {
				int backupLimit = workBuffer.limit();
				workBuffer.limit(workBuffer.position()+(int)remainingBytesOfOneMesssageStream);
				try {
					dstByteBuffer.put(workBuffer);
				} catch(BufferOverflowException e) {
					try {
						dstWrapBuffer = dataPacketBufferQueueManager.pollDataPacketBuffer();
					} catch (NoMoreDataPacketBufferException e1) {
						for (WrapBuffer workWrapBuffer : dstDataPacketBufferList) {
							dataPacketBufferQueueManager.putDataPacketBuffer(workWrapBuffer);
						}
						throw e1;
					}
					dstDataPacketBufferList.add(dstWrapBuffer);
					dstByteBuffer = dstWrapBuffer.getByteBuffer();
					
					// FIXME!
					// log.info("2. new dstByteBuffer=[{}], workBuffer=[{}]", dstByteBuffer.toString(), workBuffer.toString());
					
					dstByteBuffer.put(workBuffer);
				} finally {
					workBuffer.limit(backupLimit);
				}
				remainingBytesOfOneMesssageStream = 0;
				break;
			}
		} while (remainingBytesOfOneMesssageStream > 0);		
		
		for (WrapBuffer workWrapBuffer : dstDataPacketBufferList) {
			workWrapBuffer.getByteBuffer().flip();
		}
		
		FreeSizeInputStream bodyFreeSizeInputStream = new FreeSizeInputStream(dstDataPacketBufferList, CommonType.WRAPBUFFER_RECALL_GUBUN.WRAPBUFFER_RECALL_YES, streamCharsetDecoder, dataPacketBufferQueueManager);
		
		// FIXME!
		// log.info("bodyFreeSizeInputStream remaining bytes={}", bodyFreeSizeInputStream.remaining());
		
		return bodyFreeSizeInputStream;
	}
	
	@Override
	public void close() {
		if (memoryRecallGubun == CommonType.WRAPBUFFER_RECALL_GUBUN.WRAPBUFFER_RECALL_NO) {
			/** 파라미터 데이터 패킷 버퍼목록 비 회수 */
			long remainingBytes = remaining();
			try {
				skip(remainingBytes);
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				log.error(errorMessage, e);
				System.exit(1);
			} catch (SinnoriBufferUnderflowException e) {
				String errorMessage = e.getMessage();
				log.error(errorMessage, e);
				System.exit(1);
			}
		} else {
			/** 파라미터 데이터 패킷 버퍼목록 회수 2번 방지용 */
			if (null == workBuffer) return;
			
			/** 파라미터 데이터 패킷 버퍼목록 회수 */
			for (WrapBuffer wrapBuffer : dataPacketBufferList) {
				dataPacketBufferQueueManager.putDataPacketBuffer(wrapBuffer);
			}
			dataPacketBufferList.clear();
			streamBufferList.clear();
			indexOfWorkBuffer = -1;
			workBuffer = null;
			limitedRemainingBytes = 0;
		}
	}
}
