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
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriBufferOverflowException;

/**
 * 가변 크기를 갖는 출력 이진 스트림<br/>
 * 참고) 스트림은 데이터 패킷 버퍼 큐 관리자가 관리하는 랩 버퍼들로 구현된다. 
 * @author Won Jonghoon
 *
 */
public final class FreeSizeOutputStream implements SinnoriOutputStreamIF {
	private Logger log = LoggerFactory.getLogger(FreeSizeOutputStream.class);
	
	private ArrayList<WrapBuffer> dataPacketBufferList = null;
	private ByteOrder streamByteOrder = null;
	private Charset streamCharset = null;
	private CharsetEncoder streamCharsetEncoder = null;
	private int dataPacketBufferMaxCount;
	private DataPacketBufferPoolManagerIF dataPacketBufferQueueManager = null;
	
	
	
	/**
	 * 주) 아래 SHORT_BYTES, INTEGER_BYTES, LONG_BYTES 는 객체 인스턴스마다 필요합니다. 만약 static 으로 만들면 thread safe 문제에 직면할 것입니다.
	 */	
	/*private final byte SHORT_BYTES[] = { CommonStaticFinalVars.ZERO_BYTE,
			CommonStaticFinalVars.ZERO_BYTE };
	private final byte INTEGER_BYTES[] = { CommonStaticFinalVars.ZERO_BYTE,
			CommonStaticFinalVars.ZERO_BYTE, CommonStaticFinalVars.ZERO_BYTE,
			CommonStaticFinalVars.ZERO_BYTE };
	private final byte LONG_BYTES[] = { CommonStaticFinalVars.ZERO_BYTE,
			CommonStaticFinalVars.ZERO_BYTE, CommonStaticFinalVars.ZERO_BYTE,
			CommonStaticFinalVars.ZERO_BYTE, CommonStaticFinalVars.ZERO_BYTE,
			CommonStaticFinalVars.ZERO_BYTE, CommonStaticFinalVars.ZERO_BYTE,
			CommonStaticFinalVars.ZERO_BYTE };*/
			
	/*private byte[] shortBytes = null;
	private byte[] intBytes = null;
	private byte[] longBytes = null;*/

	private ByteBuffer shortBuffer = null;
	private ByteBuffer intBuffer = null;
	private ByteBuffer longBuffer = null;
	
	private ByteBuffer workBuffer = null;
	private long outputStreamSize = 0;

	
	public FreeSizeOutputStream(int dataPacketBufferMaxCount, CharsetEncoder streamCharsetEncoder, DataPacketBufferPoolManagerIF dataPacketBufferQueueManager) throws NoMoreDataPacketBufferException {	
		if (dataPacketBufferMaxCount <= 0) {
			String errorMessage = String.format("the parameter dataPacketBufferMaxCount[%d] is less than or equal to zero", dataPacketBufferMaxCount);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == streamCharsetEncoder) {
			throw new IllegalArgumentException("the parameter streamCharsetEncoder is null");
		}
		if (null == dataPacketBufferQueueManager) {
			throw new IllegalArgumentException("the parameter dataPacketBufferQueueManager is null");
		}
		
		this.dataPacketBufferMaxCount = dataPacketBufferMaxCount;
		this.streamByteOrder = dataPacketBufferQueueManager.getByteOrder();
		this.streamCharset = streamCharsetEncoder.charset();
		this.streamCharsetEncoder = streamCharsetEncoder;
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		
		
		
		dataPacketBufferList = new ArrayList<WrapBuffer>();
		WrapBuffer wrapBuffer = dataPacketBufferQueueManager.pollDataPacketBuffer();
		workBuffer = wrapBuffer.getByteBuffer();
		//workBuffer.position(firtBufferStartPosition);
		dataPacketBufferList.add(wrapBuffer);
		
		
		shortBuffer = ByteBuffer.allocate(2);
		shortBuffer.order(streamByteOrder);
		// shortBytes = shortBuffer.array();

		intBuffer = ByteBuffer.allocate(4);
		intBuffer.order(streamByteOrder);
		// intBytes = intBuffer.array();

		longBuffer = ByteBuffer.allocate(8);
		longBuffer.order(streamByteOrder);
		// longBytes = longBuffer.array();
	}
		
	/**
	 * 랩 버퍼 확보 실패시 이전에 등록한 랩 버퍼 목록을 해제해 주는 메소드
	 */
	private void freeDataPacketBufferList() {
		if (null != dataPacketBufferList) {
			for (WrapBuffer dataPacketBuffer : dataPacketBufferList) {
				dataPacketBufferQueueManager.putDataPacketBuffer(dataPacketBuffer);
			}
		}
	}

	/**
	 * 스트림에 랩 버퍼를 추가하여 확장한다.
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	private void addBuffer() throws NoMoreDataPacketBufferException, SinnoriBufferOverflowException  {
		/** FIXME! 남은 용량 없이 꽉 차서 들어와야 한다. */
		if (workBuffer.hasRemaining()) {
			String errorMessage = "the working buffer has a remaing data";
			log.warn(errorMessage);
			System.exit(1);
		}

		if (dataPacketBufferList.size() == dataPacketBufferMaxCount) {
			String errorMessage = String.format("this output stream is full. maximum number of data packet buffers=[%d]", dataPacketBufferMaxCount);
			// log.warn();
			throw new SinnoriBufferOverflowException(errorMessage);
		}

		/** 새로운 바디 버퍼 받아 오기 */
		WrapBuffer newWrapBuffer = null;
		try {
			newWrapBuffer = dataPacketBufferQueueManager.pollDataPacketBuffer();
		} catch (NoMoreDataPacketBufferException e) {
			freeDataPacketBufferList();
			throw e;
		}

		workBuffer = newWrapBuffer.getByteBuffer();
		dataPacketBufferList.add(newWrapBuffer);
	}
	
	
	
	
	
	
	private void doPutBytes(ByteBuffer src)
			throws BufferOverflowException, SinnoriBufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException {		
		do {
			/*int remainingBytes = workBuffer.remaining();
			if (remainingBytes >= srcByteBuffer.remaining()) {
				workBuffer.put(srcByteBuffer);
				break;
			} else {
				int oldLimitOfSrc = srcByteBuffer.limit();
				srcByteBuffer.limit(srcByteBuffer.position() + remainingBytes);
				workBuffer.put(srcByteBuffer);
				srcByteBuffer.limit(oldLimitOfSrc);				
				addBuffer();
			}*/
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}				
			workBuffer.put(src.get());
			outputStreamSize++;
		} while (src.hasRemaining());
	}
	
	
	/*private void doPutBytes(byte[] src)
			throws BufferOverflowException, SinnoriBufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException {
		int offset = 0;
		int len = srcBytes.length; 
		do {
			int remainingBytes = workBuffer.remaining();
			if (remainingBytes >= len) {
				workBuffer.put(srcBytes, offset, len);
				// len = 0;
				break;
			} else {
				workBuffer.put(srcBytes, offset, remainingBytes);
				offset += remainingBytes;
				len -= remainingBytes;
				addBuffer();
			}
		} while (0 != len);
		doPutBytes(src, 0, src.length);
	}*/
	
	private void doPutBytes(byte[] src, int offset, int length)
			throws BufferOverflowException, SinnoriBufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException { 
		int remainingBytesOfWorkBuffer = workBuffer.remaining();
		if (0 == remainingBytesOfWorkBuffer) {
			addBuffer();
			remainingBytesOfWorkBuffer = workBuffer.remaining();
		}
		
		do {			
			if (remainingBytesOfWorkBuffer >= length) {
				workBuffer.put(src, offset, length);
				outputStreamSize += length;
				break;
			}
			
			workBuffer.put(src, offset, remainingBytesOfWorkBuffer);
			outputStreamSize += remainingBytesOfWorkBuffer;
			
			offset += remainingBytesOfWorkBuffer;
			length -= remainingBytesOfWorkBuffer;
			addBuffer();
			remainingBytesOfWorkBuffer = workBuffer.remaining();
		} while (0 != length);
		
		
	}
	
	/** 
	 * @return 출력 스트림 쓰기 과정 결과 물이 담긴 버퍼 목록, 모든 버퍼는 읽기 가능 상태 flip 되어진다.
	 */
	/*public ArrayList<WrapBuffer> getFlipDataPacketBufferList() {
		int bufferSize = dataPacketBufferList.size();
		for (int i = 0; i < bufferSize; i++) {
			dataPacketBufferList.get(i).getByteBuffer().flip();
		}
		return dataPacketBufferList;
	}*/
	
	/*public FreeSizeInputStream getFreeSizeInputStream(CharsetDecoder  streamCharsetDecoder) {
		if (null == streamCharsetDecoder) {
			throw new IllegalArgumentException("the parameter streamCharsetDecoder is null");
		}
		
		if (! streamCharsetDecoder.charset().equals(streamCharset)) {
			String errorMessage = String.format("the parameter streamCharsetDecoder's charset[%s] is different from this stream charset[%s]", 
					streamCharsetDecoder.charset().name(), streamCharset.name());
			throw new IllegalArgumentException(errorMessage);
		}
		
		ArrayList<WrapBuffer> dupWrapBufferList = new ArrayList<WrapBuffer>();
		
		for (WrapBuffer srcBuffer : dataPacketBufferList) {
			ByteBuffer dupByteBuffer = srcBuffer.getByteBuffer().duplicate();
			*//** In ByteBuffer the duplicate method doesn't copy the byte order attribute *//*
			dupByteBuffer.order(streamByteOrder);
			dupByteBuffer.flip();
			WrapBuffer dupWrapBuffer = new WrapBuffer(dupByteBuffer);
			
			dupWrapBufferList.add(dupWrapBuffer);
		}
		
		return new FreeSizeInputStream(dupWrapBufferList, streamCharsetDecoder, dataPacketBufferQueueManager);
	}*/
	
	/*public ArrayList<WrapBuffer> getFlippedWrapBufferList() {
		ArrayList<WrapBuffer> dupWrapBufferList = new ArrayList<WrapBuffer>();
		
		for (WrapBuffer srcBuffer : dataPacketBufferList) {
			ByteBuffer dupByteBuffer = srcBuffer.getByteBuffer().duplicate();
			*//** In ByteBuffer the duplicate method doesn't copy the byte order attribute *//*
			dupByteBuffer.order(streamByteOrder);
			dupByteBuffer.flip();
			WrapBuffer dupWrapBuffer = new WrapBuffer(dupByteBuffer);
			
			dupWrapBufferList.add(dupWrapBuffer);
		}
		
		return dupWrapBufferList;
	}*/
	
	public void flip() {
		for (WrapBuffer srcBuffer : dataPacketBufferList) {
			srcBuffer.getByteBuffer().flip();
		}
	}
	
	/** 
	 * @return flip 없는 출력 스트림 쓰기 과정 결과 물이 담긴 버퍼 목록
	 */
	public ArrayList<WrapBuffer> getDataPacketBufferList() {
		return dataPacketBufferList;
	}
	
	public ArrayList<WrapBuffer> getFlippedWrapBufferList() {
		flip();
		return dataPacketBufferList;
	} 
	
	@Override
	public Charset getCharset() {
		return streamCharset;
	}
	
	@Override
	public void putByte(byte value) throws BufferOverflowException, SinnoriBufferOverflowException,
			NoMoreDataPacketBufferException {
		if (! workBuffer.hasRemaining()) {
			addBuffer();
		}
		workBuffer.put(value);
		outputStreamSize++;
		/*
		try {
			workBuffer.put(value);
		} catch (BufferOverflowException e) {
			addBuffer();
			workBuffer.put(value);
		}*/
	}

	@Override
	public void putUnsignedByte(short value) throws BufferOverflowException, SinnoriBufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]이 음수입니다.", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]은 unsigned byte 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.UNSIGNED_BYTE_MAX));
		}

		doPutUnsignedByte((byte)value);
		
	}
	
	private void doPutUnsignedByte(byte value) throws BufferOverflowException, SinnoriBufferOverflowException, NoMoreDataPacketBufferException {		
		putByte(value);
	}
	
	@Override
	public void putUnsignedByte(int value) throws BufferOverflowException, SinnoriBufferOverflowException, 
	IllegalArgumentException, NoMoreDataPacketBufferException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]이 음수입니다.", value));
		}
		
		if (value > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]은 unsigned byte 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.UNSIGNED_BYTE_MAX));
		}
		
		doPutUnsignedByte((byte)value);
	}
	
	@Override
	public void putUnsignedByte(long value) throws BufferOverflowException, SinnoriBufferOverflowException,
	IllegalArgumentException, NoMoreDataPacketBufferException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]이 음수입니다.", value));
		}
		
		if (value > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]은 unsigned byte 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.UNSIGNED_BYTE_MAX));
		}
		
		doPutUnsignedByte((byte)value);
	}
	

	@Override
	public void putShort(short value) throws BufferOverflowException, SinnoriBufferOverflowException,
			NoMoreDataPacketBufferException {
		/*try {
			workBuffer.putShort(value);
		} catch (BufferOverflowException e) {
			shortBuffer.clear();
			shortBuffer.putShort(value);
			shortBuffer.position(0);
			shortBuffer.limit(workBuffer.remaining());
			workBuffer.put(shortBuffer);
			shortBuffer.limit(shortBuffer.capacity());
			addBuffer();
			workBuffer.put(shortBuffer);
		}*/
		
		shortBuffer.clear();
		shortBuffer.putShort(value);
		shortBuffer.rewind();
		do {			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}				
			workBuffer.put(shortBuffer.get());
			outputStreamSize++;
		} while(shortBuffer.hasRemaining());		
	}

	@Override
	public void putUnsignedShort(int value) throws BufferOverflowException, 
	SinnoriBufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]이 음수입니다.", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]은 unsigned short 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.UNSIGNED_SHORT_MAX));
		}
		
		doPutUnsignedShort(value);		
	}
	
	private void doPutUnsignedShort(int value) throws SinnoriBufferOverflowException, NoMoreDataPacketBufferException {
		// ByteBuffer integerBuffer = getIntegerBufferForUnsignedShort(value);
		intBuffer.clear();
		intBuffer.putInt(value);

		if (ByteOrder.BIG_ENDIAN == streamByteOrder) {
			intBuffer.position(2);
			intBuffer.limit(4);
		} else {
			intBuffer.position(0);
			intBuffer.limit(2);
		}
		
		// log.info("1. unsingedShortBuffer=[%s]",
		// unsingedShortBuffer.toString());
		// log.info("1. workBuffer=[%s]", workBuffer.toString());

		// FIXME! old code
		
		do {
			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}
				
			workBuffer.put(intBuffer.get());
			outputStreamSize++;
			
			/*try {
				workBuffer.put(intBuffer);
			} catch (BufferOverflowException e) {
				int oldLimitOfIntBuffer = intBuffer.limit();
				intBuffer.limit(intBuffer.position()+workBuffer.remaining());
				workBuffer.put(intBuffer);
				intBuffer.limit(oldLimitOfIntBuffer);
	
				addBuffer();
				// workBuffer.put(intBuffer);
			}*/
		} while(intBuffer.hasRemaining());
		
		/*while(intBuffer.hasRemaining()) {
			try {
				workBuffer.put(intBuffer.get());
			} catch (BufferOverflowException e) {
				addBuffer();
				workBuffer.put(intBuffer.get());
			}
		}*/
	}
	
	@Override
	public void putUnsignedShort(long value) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]이 음수입니다.", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]은 unsigned short 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.UNSIGNED_SHORT_MAX));
		}
		
		doPutUnsignedShort((int)value);		
	}
	

	@Override
	public void putInt(int value) throws BufferOverflowException,
			NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		/*try {
			workBuffer.putInt(value);
		} catch (BufferOverflowException e) {
			intBuffer.clear();
			intBuffer.putInt(value);
			intBuffer.position(0);
			intBuffer.limit(workBuffer.remaining());
			workBuffer.put(intBuffer);
			intBuffer.limit(intBuffer.capacity());

			addBuffer();
			do {
				try {
					workBuffer.put(intBuffer);
				} catch (BufferOverflowException e1) {
					intBuffer.limit(longBuffer.position() + workBuffer.remaining());
					workBuffer.put(intBuffer);
					intBuffer.limit(intBuffer.capacity());
					addBuffer();
				}
			} while(intBuffer.hasRemaining());
		}*/
		
		intBuffer.clear();
		intBuffer.putInt(value);
		intBuffer.rewind();
		do {			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}				
			workBuffer.put(intBuffer.get());
			outputStreamSize++;
		} while(intBuffer.hasRemaining());
		
	}
	
	
	@Override
	public void putUnsignedInt(long value) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]이 음수입니다.", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			throw new IllegalArgumentException(String.format(
					"파라미터 값[%d]은 unsigned integer 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.UNSIGNED_INTEGER_MAX));
		}
		
		
		longBuffer.clear();
		longBuffer.putLong(value);

		if (ByteOrder.BIG_ENDIAN == streamByteOrder) {
			longBuffer.position(4);
			longBuffer.limit(8);
		} else {
			longBuffer.position(0);
			longBuffer.limit(4);
		}
		
		do {
			
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}
			
			workBuffer.put(longBuffer.get());
			outputStreamSize++;
			
			/*
			try {
				workBuffer.put(longBuffer);
			} catch (BufferOverflowException e) {
				int oldLimitOfLongBuffer = longBuffer.limit();
				longBuffer.limit(longBuffer.position() + workBuffer.remaining());
				workBuffer.put(longBuffer);
				longBuffer.limit(oldLimitOfLongBuffer);
	
				addBuffer();
				
				// workBuffer.put(longBuffer);
			}*/
		} while(longBuffer.hasRemaining());
		
		/*do {
			try {
				workBuffer.put(longBuffer.get());
			} catch (BufferOverflowException e) {
				addBuffer();
			}
		} while(longBuffer.hasRemaining()); */
	}

	@Override
	public void putLong(long value) throws BufferOverflowException,
			NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		/*try {
			workBuffer.putLong(value);
		} catch (BufferOverflowException e) {
			longBuffer.clear();
			longBuffer.putLong(value);
			
			
			longBuffer.position(0);
			longBuffer.limit(workBuffer.remaining());
			workBuffer.put(longBuffer);
			longBuffer.limit(longBuffer.capacity());
			
			addBuffer();
			workBuffer.put(longBuffer);
			
			do {				
				if (! workBuffer.hasRemaining()) {
					addBuffer();
				}				
				workBuffer.put(longBuffer.get());				
			} while(longBuffer.hasRemaining());
		}*/
		
		longBuffer.clear();
		longBuffer.putLong(value);
		longBuffer.rewind();
		do {				
			if (! workBuffer.hasRemaining()) {
				addBuffer();
			}				
			workBuffer.put(longBuffer.get());
			outputStreamSize++;
		} while(longBuffer.hasRemaining());
	}

	@Override
	public void putFixedLengthString(int fixedLength, String str,
			CharsetEncoder wantedCharsetEncoder)
			throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, SinnoriBufferOverflowException{
		if (fixedLength < 0) {
			throw new IllegalArgumentException(String.format(
					"the parameter fixedLength[%d] is less than zero", fixedLength));
		}

		/*
		if (len > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(
					String.format(
							"파리미터 문자열 길이(=len)의 값[%d]은  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							len, CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}
		*/

		/*long remainingBytes = remaining();
		if (len > remainingBytes) {
			throw new IllegalArgumentException(String.format(
					"파라미터 길이[%d]는 남아 있은 버퍼 크기[%d] 보다 작거나 같아야 합니다.", len,
					remainingBytes));
		}*/

		if (str == null) {
			throw new IllegalArgumentException("파리미터 문자열(=str)의 값이 null 입니다.");
		}

		if (wantedCharsetEncoder == null) {
			throw new IllegalArgumentException(
					"파리미터 문자셋(=clientEncoder)의 값이 null 입니다.");
		}
		
		if (0 == fixedLength) return;
		
		CharBuffer strCharBuffer = CharBuffer.wrap(str);

		ByteBuffer strByteBuffer = null;
		try {
			strByteBuffer = ByteBuffer.allocate(fixedLength);
		} catch (OutOfMemoryError e) {
			log.warn("OutOfMemoryError", e);
			throw e;
		}
		
		byte strBytes[] = strByteBuffer.array(); 
		Arrays.fill(strBytes, CommonStaticFinalVars.ZERO_BYTE);
		wantedCharsetEncoder.encode(strCharBuffer, strByteBuffer, true);
		// log.info("strBufer=[%s]", strBufer.toString());
		
		/*
		strByteBuffer.rewind();
		putBytesToWorkBuffer(strByteBuffer);
		*/
		doPutBytes(strBytes, 0, strBytes.length);
	}

	@Override
	public void putFixedLengthString(int fixedLength, String str) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		putFixedLengthString(fixedLength, str, streamCharsetEncoder);
	}

	@Override
	public void putStringAll(String str) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		if (str == null) {
			throw new IllegalArgumentException("파리미터 문자열(=str)의 값이 null 입니다.");
		}

		byte strBytes[] = str.getBytes(streamCharset);
		/*
		int len = strBytes.length;
		if (len > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(
					String.format(
							"파리미터 문자열 길이(=len)의 값[%d]은  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							len, CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}
		*/

		doPutBytes(strBytes, 0, strBytes.length);
	}

	@Override
	public void putPascalString(String str) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		putUBPascalString(str);
	}

	@Override
	public void putSIPascalString(String str) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		if (str == null) {
			throw new IllegalArgumentException("파리미터 문자열(=str)의 값이 null 입니다.");
		}

		byte strBytes[] = str.getBytes(streamCharset);

		/*
		if (strBytes.length > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(
					String.format(
							"파리미터 문자열 길이(=len)의 값[%d]은  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							strBytes.length,
							CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}
		*/

		putInt(strBytes.length);
		putBytes(strBytes);
	}

	@Override
	public void putUSPascalString(String str) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		if (str == null) {
			throw new IllegalArgumentException("파리미터 문자열(=str)의 값이 null 입니다.");
		}

		byte strBytes[] = str.getBytes(streamCharset);
		/*
		if (strBytes.length > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(String.format(
					"파라미터 문자열의 길이[%d]는 unsigned short 최대값[%d]을 넘을 수 없습니다.", strBytes.length, CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}
		*/
		
		putUnsignedShort(strBytes.length);
		putBytes(strBytes);
	}

	@Override
	public void putUBPascalString(String str) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
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
	public void putBytes(byte[] src, int offset, int length)
			throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		if (offset < 0) {
			throw new IllegalArgumentException(String.format(
					"the parameter offset[%d] is less than zero", offset));
		}

		if (length < 0) {
			throw new IllegalArgumentException(String.format(
					"the parameter length[%d] is less than zero", length));
		}
		
		if (0 == length) {
			return;
		}
		/*
		if (length > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(
					String.format(
							"파리미터 소스 바이트 배열 길이[%d]는  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							length, CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}
		*/

		/*long remainingBytes = remaining();
		if (length > remainingBytes) {
			throw new IllegalArgumentException(String.format(
					"파라미터 소스 바이트 배열 길이[%d]는 남아 있은 버퍼 크기[%d] 보다 작거나 같아야 합니다.",
					length, remainingBytes));
		}*/

		// ByteBuffer srcByteBuffer = ByteBuffer.wrap(srcBytes, offset, length);

		doPutBytes(src, offset, length);
	}

	@Override
	public void putBytes(byte[] src) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}
		/*
		if (srcBuffer.length > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(
					String.format(
							"파리미터 소스 바이트 배열 길이[%d]는  unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							srcBuffer.length,
							CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}
		*/

		// ByteBuffer srcByteBuffer = ByteBuffer.wrap(srcBuffer);

		doPutBytes(src, 0, src.length);
		
	}

	@Override
	public void putBytes(ByteBuffer src) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		/*
		int remainingBytes = srcBuffer.remaining();
		if (remainingBytes > CommonStaticFinal.MAX_UNSIGNED_SHORT) {
			throw new IllegalArgumentException(
					String.format(
							"파리미터 바이트 버퍼의 길이[%d]는 unsigned short 최대값[%d] 보다 작거나 같아야 합니다.",
							remainingBytes,
							CommonStaticFinal.MAX_UNSIGNED_SHORT));
		}
		*/

		/**
		 * slice 메소드를 통해 원본 버퍼의 속성과 별개의 속성을 갖는 <br/>
		 * 그리고 실제 필요한 영역만을 가지는 바이트 버퍼를 만든다.
		 */
		ByteBuffer sliceBuffer = src.slice();

		doPutBytes(sliceBuffer);
		
	}

	@Override
	public void skip(int n) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		if (n < 0) {
			throw new IllegalArgumentException(String.format(
					"the parameter n is less than zero", n));
		}
		if (0 == n) return;

		/*if (n >= CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			throw new IllegalArgumentException(String.format(
					"파라미터 생략할 크기[%d]는 unsinged byte 최대값[%d]보다 작어야 합니다.",
					skipBytes, CommonStaticFinalVars.UNSIGNED_BYTE_MAX));
		}*/
		
		int remainingBytesOfWorkBuffer = workBuffer.remaining();
		if (0 == remainingBytesOfWorkBuffer) {
			addBuffer();
			remainingBytesOfWorkBuffer = workBuffer.remaining();
		}
		
		do {
			if (n <= remainingBytesOfWorkBuffer) {
				workBuffer.position(workBuffer.position() + n);
				outputStreamSize += n;
				break;
			}

			int limitOfWorkBuffer = workBuffer.limit();
			workBuffer.position(limitOfWorkBuffer);
			outputStreamSize += remainingBytesOfWorkBuffer;
			
			n -= remainingBytesOfWorkBuffer;			
			addBuffer();
			remainingBytesOfWorkBuffer = workBuffer.remaining();
		} while (n != 0);
	}

	/*@Override
	public long remaining() {
		long remainingBufferCount = dataPacketBufferMaxCntPerMessage - dataPacketBufferList.size();
		long remainingByte = workBuffer.capacity()*remainingBufferCount
				+ workBuffer.remaining();
		return remainingByte;
		
		if (null == workBuffer) {
			return 0L;
		}
		
		int streamBufferListSize = streamBufferList.size();
		
		long availableBytes = 0;
		
		for (int i=indexOfWorkBuffer; i < streamBufferListSize; i++) {
			availableBytes += streamBufferList.get(i).remaining();
			
		}
		
		return availableBytes;
	}*/
	
	// @Override
	public long getOutputStreamSize() {
		/*int dataPacketBufferListSize = dataPacketBufferList.size();
		
		if (0 == dataPacketBufferListSize) return 0;
		
		long position = (dataPacketBufferListSize - 1)*workBuffer.capacity()+workBuffer.position();
		
		return position;*/
		return outputStreamSize;
	}
	
	public long getWrittenBytes() {
		long writtenBytes = 0;
		
		for (WrapBuffer buffer : dataPacketBufferList) {
			writtenBytes += buffer.getByteBuffer().position();
		}
		
		return writtenBytes;
	}

	@Override
	public void close() {
		freeDataPacketBufferList();
	}
}
