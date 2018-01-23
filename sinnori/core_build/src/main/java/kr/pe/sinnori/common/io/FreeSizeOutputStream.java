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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriBufferOverflowException;

/**
 * 가변 크기를 갖는 출력 이진 스트림<br/>
 * 참고) 스트림은 데이터 패킷 버퍼 큐 관리자가 관리하는 랩 버퍼들로 구현된다.
 * 
 * @author Won Jonghoon
 *
 */
public final class FreeSizeOutputStream implements BinaryOutputStreamIF {
	private Logger log = LoggerFactory.getLogger(FreeSizeOutputStream.class);

	private final ArrayList<WrapBuffer> outputStreamWrapBufferList = new ArrayList<WrapBuffer>();;
	private ByteOrder streamByteOrder = null;
	private Charset streamCharset = null;
	private CharsetEncoder streamCharsetEncoder = null;
	private int dataPacketBufferMaxCount;
	private DataPacketBufferPoolManagerIF dataPacketBufferQueueManager = null;	

	private ByteBuffer shortBuffer = null;
	private ByteBuffer intBuffer = null;
	private ByteBuffer longBuffer = null;

	private ByteBuffer workBuffer = null;
	private long numberOfWrittenBytes = 0;
	private long outputStreamMaxSize = 0;

	public FreeSizeOutputStream(int dataPacketBufferMaxCount, CharsetEncoder streamCharsetEncoder,
			DataPacketBufferPoolManagerIF dataPacketBufferQueueManager) throws NoMoreDataPacketBufferException {
		if (dataPacketBufferMaxCount <= 0) {
			String errorMessage = String.format(
					"the parameter dataPacketBufferMaxCount[%d] is less than or equal to zero",
					dataPacketBufferMaxCount);
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

		// outputStreamWrapBufferList = new ArrayList<WrapBuffer>();
		WrapBuffer wrapBuffer = dataPacketBufferQueueManager.pollDataPacketBuffer();
		workBuffer = wrapBuffer.getByteBuffer();
		outputStreamWrapBufferList.add(wrapBuffer);

		shortBuffer = ByteBuffer.allocate(2);
		shortBuffer.order(streamByteOrder);

		intBuffer = ByteBuffer.allocate(4);
		intBuffer.order(streamByteOrder);

		longBuffer = ByteBuffer.allocate(8);
		longBuffer.order(streamByteOrder);

		outputStreamMaxSize = dataPacketBufferMaxCount * dataPacketBufferQueueManager.getDataPacketBufferSize();
	}

	private long remaining() {
		return (outputStreamMaxSize - numberOfWrittenBytes);
	}

	/**
	 * 랩 버퍼 확보 실패시 이전에 등록한 랩 버퍼 목록을 해제해 주는 메소드
	 */
	private void freeDataPacketBufferList() {
		if (null != outputStreamWrapBufferList) {
			for (WrapBuffer outputStreamWrapBuffer : outputStreamWrapBufferList) {
				log.info("return the outputStreamWrapBuffer[hashcode={}] to the data packet buffer pool", outputStreamWrapBuffer.hashCode());
				
				dataPacketBufferQueueManager.putDataPacketBuffer(outputStreamWrapBuffer);
			}
		}
	}

	/**
	 * 스트림에 랩 버퍼를 추가하여 확장한다.
	 * 
	 * @throws NoMoreDataPacketBufferException
	 *             데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	private void addBuffer() throws NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		/** FIXME! 남은 용량 없이 꽉 차서 들어와야 한다. */
		if (workBuffer.hasRemaining()) {
			String errorMessage = "the working buffer has a remaing data";
			log.warn(errorMessage);
			System.exit(1);
		}

		if (outputStreamWrapBufferList.size() == dataPacketBufferMaxCount) {
			String errorMessage = String.format(
					"this output stream is full. maximum number of data packet buffers=[%d]", dataPacketBufferMaxCount);
			// log.warn();
			throw new SinnoriBufferOverflowException(errorMessage);
		}

		/** 새로운 바디 버퍼 받아 오기 */
		WrapBuffer newWrapBuffer = null;
		try {
			newWrapBuffer = dataPacketBufferQueueManager.pollDataPacketBuffer();
		} catch (NoMoreDataPacketBufferException e) {
			// freeDataPacketBufferList();
			throw e;
		}

		workBuffer = newWrapBuffer.getByteBuffer();
		outputStreamWrapBufferList.add(newWrapBuffer);
	}

	private void doPutBytes(ByteBuffer src) throws BufferOverflowException, SinnoriBufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		do {
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(src.get());
			numberOfWrittenBytes++;
		} while (src.hasRemaining());
	}

	private void doPutBytes(byte[] src, int offset, int length) throws BufferOverflowException,
			SinnoriBufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException {
		int numberOfBytesRemainingInWorkBuffer = workBuffer.remaining();
		if (0 == numberOfBytesRemainingInWorkBuffer) {
			addBuffer();
			numberOfBytesRemainingInWorkBuffer = workBuffer.remaining();
		}

		do {
			if (numberOfBytesRemainingInWorkBuffer >= length) {
				workBuffer.put(src, offset, length);
				numberOfWrittenBytes += length;
				break;
			}

			workBuffer.put(src, offset, numberOfBytesRemainingInWorkBuffer);
			numberOfWrittenBytes += numberOfBytesRemainingInWorkBuffer;

			offset += numberOfBytesRemainingInWorkBuffer;
			length -= numberOfBytesRemainingInWorkBuffer;
			addBuffer();
			numberOfBytesRemainingInWorkBuffer = workBuffer.remaining();
		} while (0 != length);
	}

	private void doPutUnsignedByte(byte value)
			throws BufferOverflowException, SinnoriBufferOverflowException, NoMoreDataPacketBufferException {
		if (!workBuffer.hasRemaining()) {
			addBuffer();
		}
		workBuffer.put(value);
		numberOfWrittenBytes++;
	}

	private void doPutUnsignedShort(short value)
			throws SinnoriBufferOverflowException, NoMoreDataPacketBufferException {
		shortBuffer.clear();
		shortBuffer.putShort(value);
		shortBuffer.rewind();

		do {

			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}

			workBuffer.put(shortBuffer.get());
			numberOfWrittenBytes++;

		} while (shortBuffer.hasRemaining());
	}

	private void throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(long numberOfBytesRequired)
			throws SinnoriBufferOverflowException {
		long numberOfBytesRemaining = remaining();
		if (numberOfBytesRemaining < numberOfBytesRequired) {
			throw new SinnoriBufferOverflowException(
					String.format("the number[%d] of bytes remaining in this ouput stream is less than [%d] byte(s) that is required",
							numberOfBytesRemaining, numberOfBytesRequired));
		}
	}

	@Override
	public void putByte(byte value)
			throws BufferOverflowException, SinnoriBufferOverflowException, NoMoreDataPacketBufferException {
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(1);

		if (!workBuffer.hasRemaining()) {
			addBuffer();
		}
		workBuffer.put(value);
		numberOfWrittenBytes++;
	}

	@Override
	public void putUnsignedByte(short value) throws BufferOverflowException, SinnoriBufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]이 음수입니다.", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]은 unsigned byte 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.UNSIGNED_BYTE_MAX));
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(1);

		doPutUnsignedByte((byte) value);

	}

	@Override
	public void putUnsignedByte(int value) throws BufferOverflowException, SinnoriBufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]이 음수입니다.", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]은 unsigned byte 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.UNSIGNED_BYTE_MAX));
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(1);

		doPutUnsignedByte((byte) value);
	}

	@Override
	public void putUnsignedByte(long value) throws BufferOverflowException, SinnoriBufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]이 음수입니다.", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]은 unsigned byte 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.UNSIGNED_BYTE_MAX));
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(1);

		doPutUnsignedByte((byte) value);
	}

	@Override
	public void putShort(short value)
			throws BufferOverflowException, SinnoriBufferOverflowException, NoMoreDataPacketBufferException {

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(2);

		shortBuffer.clear();
		shortBuffer.putShort(value);
		shortBuffer.rewind();
		do {
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(shortBuffer.get());
			numberOfWrittenBytes++;
		} while (shortBuffer.hasRemaining());
	}

	@Override
	public void putUnsignedShort(int value) throws BufferOverflowException, SinnoriBufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException {

		if (value < 0) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]이 음수입니다.", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]은 unsigned short 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.UNSIGNED_SHORT_MAX));
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(2);

		doPutUnsignedShort((short) value);
	}

	@Override
	public void putUnsignedShort(long value) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, SinnoriBufferOverflowException {

		if (value < 0) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]이 음수입니다.", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]은 unsigned short 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.UNSIGNED_SHORT_MAX));
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(2);

		doPutUnsignedShort((short) value);
	}

	@Override
	public void putInt(int value)
			throws BufferOverflowException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException {

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(4);

		intBuffer.clear();
		intBuffer.putInt(value);
		intBuffer.rewind();
		do {
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(intBuffer.get());
			numberOfWrittenBytes++;
		} while (intBuffer.hasRemaining());

	}

	@Override
	public void putUnsignedInt(long value) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		if (value < 0) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]이 음수입니다.", value));
		}

		if (value > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			throw new IllegalArgumentException(String.format("파라미터 값[%d]은 unsigned integer 최대값[%d]을 넘을 수 없습니다.", value,
					CommonStaticFinalVars.UNSIGNED_INTEGER_MAX));
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(4);

		intBuffer.clear();
		intBuffer.putInt((int) value);
		intBuffer.rewind();

		do {

			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}

			workBuffer.put(intBuffer.get());
			numberOfWrittenBytes++;

		} while (intBuffer.hasRemaining());
	}

	@Override
	public void putLong(long value)
			throws BufferOverflowException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(8);

		longBuffer.clear();
		longBuffer.putLong(value);
		longBuffer.rewind();
		do {
			if (!workBuffer.hasRemaining()) {
				addBuffer();
			}
			workBuffer.put(longBuffer.get());
			numberOfWrittenBytes++;
		} while (longBuffer.hasRemaining());
	}

	@Override
	public void putBytes(byte[] src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}		
	
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(src.length);
	
		doPutBytes(src, 0, src.length);
	}

	@Override
	public void putBytes(byte[] src, int offset, int length) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}
	
		if (offset < 0) {
			throw new IllegalArgumentException(String.format("the parameter offset[%d] is less than zero", offset));
		}
		
		if (offset >= src.length) {
			throw new IllegalArgumentException(String.format("the parameter offset[%d] is greater than or equal to array.length[%d]", offset, src.length));
		}
	
		if (length < 0) {
			throw new IllegalArgumentException(String.format("the parameter length[%d] is less than zero", length));
		}
		
		if (0 == length) {
			return;
		}
		
		long sumOfOffsetAndLength = ((long)offset + length);
		if (sumOfOffsetAndLength > src.length) {
			throw new IllegalArgumentException(String.format(
					"the sum[%d] of the parameter offset[%d] and the parameter length[%d] is greater than array.length[%d]", 
					sumOfOffsetAndLength, offset, length, src.length));
		}
		
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(length);		
	
		doPutBytes(src, offset, length);
	}

	@Override
	public void putBytes(ByteBuffer src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}
		
		int numberOfBytesReamaining = src.remaining();
		if (0 == numberOfBytesReamaining) {
			return;
		}
	
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(numberOfBytesReamaining);
		
		/**
		 * slice 메소드를 통해 원본 버퍼의 속성과 별개의 속성을 갖는 <br/>
		 * 그리고 실제 필요한 영역만을 가지는 바이트 버퍼를 만든다.
		 */
		ByteBuffer sliceBuffer = src.slice();
	
		doPutBytes(sliceBuffer);
	
	}

	@Override
	public void putFixedLengthString(int fixedLength, String src) throws BufferOverflowException,
			IllegalArgumentException, NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		putFixedLengthString(fixedLength, src, streamCharsetEncoder);
	}

	@Override
	public void putFixedLengthString(int fixedLength, String src, CharsetEncoder wantedCharsetEncoder)
			throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException,
			SinnoriBufferOverflowException {
		if (fixedLength < 0) {
			throw new IllegalArgumentException(
					String.format("the parameter fixedLength[%d] is less than zero", fixedLength));
		}
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		if (wantedCharsetEncoder == null) {
			throw new IllegalArgumentException("the parameter wantedCharsetEncoder is null");
		}

		if (0 == fixedLength) {
			return;
		}	

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(fixedLength);

		CharBuffer strCharBuffer = CharBuffer.wrap(src);

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
		
		doPutBytes(strBytes, 0, strBytes.length);
	}

	@Override
	public void putStringAll(String src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		byte strBytes[] = src.getBytes(streamCharset);

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(strBytes.length);

		doPutBytes(strBytes, 0, strBytes.length);
	}

	@Override
	public void putPascalString(String src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		putUBPascalString(src);
	}

	@Override
	public void putSIPascalString(String src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		byte strBytes[] = src.getBytes(streamCharset);

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired((strBytes.length + 4));

		putInt(strBytes.length);
		putBytes(strBytes);
	}

	@Override
	public void putUSPascalString(String src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		byte strBytes[] = src.getBytes(streamCharset);		

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired((strBytes.length + 2));

		putUnsignedShort(strBytes.length);
		putBytes(strBytes);
	}

	@Override
	public void putUBPascalString(String src) throws BufferOverflowException, IllegalArgumentException,
			NoMoreDataPacketBufferException, SinnoriBufferOverflowException {
		if (src == null) {
			throw new IllegalArgumentException("the parameter src is null");
		}

		byte strBytes[] = src.getBytes(streamCharset);		

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired((strBytes.length + 1));

		putUnsignedByte(strBytes.length);
		putBytes(strBytes);

	}

	@Override
	public void skip(int n) throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException,
			SinnoriBufferOverflowException {
		skip((long)n);
	}
	
	public void skip(long n) throws BufferOverflowException, IllegalArgumentException, NoMoreDataPacketBufferException,
		SinnoriBufferOverflowException {
		if (n < 0) {
			throw new IllegalArgumentException(String.format("the parameter n is less than zero", n));
		}
		if (0 == n)
			return;

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(n);		

		int numberOfBytesRemainingInWorkBuffer = workBuffer.remaining();
		if (0 == numberOfBytesRemainingInWorkBuffer) {
			addBuffer();
			numberOfBytesRemainingInWorkBuffer = workBuffer.remaining();
		}

		do {
			if (n <= numberOfBytesRemainingInWorkBuffer) {
				workBuffer.position(workBuffer.position() + (int)n);
				numberOfWrittenBytes += n;
				break;
			}

			int limitOfWorkBuffer = workBuffer.limit();
			workBuffer.position(limitOfWorkBuffer);
			numberOfWrittenBytes += numberOfBytesRemainingInWorkBuffer;

			n -= numberOfBytesRemainingInWorkBuffer;
			addBuffer();
			numberOfBytesRemainingInWorkBuffer = workBuffer.remaining();
		} while (n != 0);
	}

	@Override
	public Charset getCharset() {
		return streamCharset;
	}

	public long size() {
		return numberOfWrittenBytes;
	}

	public long getNumberOfWrittenBytes() {
		long numberOfWrittenBytes = 0;

		for (WrapBuffer buffer : outputStreamWrapBufferList) {
			// numberOfWrittenBytes += buffer.getByteBuffer().position();
			
			ByteBuffer dupByteBuffer = buffer.getByteBuffer().duplicate();
			dupByteBuffer.flip();			
			numberOfWrittenBytes += dupByteBuffer.remaining();
		}

		return numberOfWrittenBytes;
	}

	public List<WrapBuffer> getReadableWrapBufferList() {
		flipAllOutputStreamWrapBuffer();

		return outputStreamWrapBufferList;
	}
	
	public void flipAllOutputStreamWrapBuffer() {
		/** flip all buffer */
		for (WrapBuffer outputStreamWrapBuffer : outputStreamWrapBufferList) {
			outputStreamWrapBuffer.getByteBuffer().flip();
		}
	}
	
	
	public List<WrapBuffer> getOutputStreamWrapBufferList() {
		return outputStreamWrapBufferList;
	}	

	@Override
	public void close() {
		freeDataPacketBufferList();
	}
}
