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

package kr.pe.codda.common.io;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.CharsetDecoderException;
import kr.pe.codda.common.exception.BufferUnderflowExceptionWithMessage;
import kr.pe.codda.common.util.HexUtil;

/**
 * 가변 크기를 갖는 입력 이진 스트림<br/>
 * 참고) 스트림은 데이터 패킷 버퍼 큐 관리자가 관리하는 랩 버퍼들로 구현된다.
 * 
 * @author Won Jonghoon
 *
 */
public class FreeSizeInputStream implements BinaryInputStreamIF {
	protected InternalLogger log = InternalLoggerFactory.getInstance(FreeSizeInputStream.class);

	private int dataPacketBufferMaxCount;
	private ArrayDeque<WrapBuffer> readableWrapBufferList = null;
	protected int readableWrapBufferListSize;
	protected List<ByteBuffer> streamBufferList = null;
	private Charset streamCharset;
	private CharsetDecoder streamCharsetDecoder = null;
	private ByteOrder streamByteOrder = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;

	private ByteBuffer workBuffer;
	protected int indexOfWorkBuffer;

	private long numberOfBytesRemaining = -1;
	private long inputStreamSize = 0;

	// private byte[] bytesOfShortBuffer = null;
	private byte[] bytesOfIntBuffer = null;
	private byte[] bytesOfLongBuffer = null;

	private ByteBuffer shortBuffer = null;
	private ByteBuffer intBuffer = null;
	private ByteBuffer longBuffer = null;

	public FreeSizeInputStream(int dataPacketBufferMaxCount, ArrayDeque<WrapBuffer> readableWrapBufferList,
			CharsetDecoder streamCharsetDecoder, DataPacketBufferPoolIF dataPacketBufferPool) {
		if (dataPacketBufferMaxCount <= 0) {
			String errorMessage = String.format(
					"the parameter dataPacketBufferMaxCount[%d] is less than or equal to zero",
					dataPacketBufferMaxCount);
			throw new IllegalArgumentException(errorMessage);
		}
		if (null == readableWrapBufferList) {
			String errorMessage = "the parameter readableWrapBufferList is null";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (readableWrapBufferList.size() > dataPacketBufferMaxCount) {
			String errorMessage = String.format(
					"the parameter readableWrapBufferList's size[%d] is greater than The maximum number[%d] of buffers that can be assigned per one message",
					readableWrapBufferList.size(), dataPacketBufferMaxCount);
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == streamCharsetDecoder) {
			String errorMessage = "the parameter streamCharsetDecoder is null";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == dataPacketBufferPool) {
			String errorMessage = "the parameter dataPacketBufferPool is null";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		
		this.dataPacketBufferMaxCount = dataPacketBufferMaxCount;
		this.readableWrapBufferList = readableWrapBufferList;		
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.dataPacketBufferPool = dataPacketBufferPool;
		
		readableWrapBufferListSize = readableWrapBufferList.size();
		streamCharset = streamCharsetDecoder.charset();
		streamByteOrder = dataPacketBufferPool.getByteOrder();
		
		streamBufferList = new ArrayList<ByteBuffer>(readableWrapBufferListSize);

		for (WrapBuffer wrapBuffer : readableWrapBufferList) {
			this.streamBufferList.add(wrapBuffer.getByteBuffer());
		}		

		
		numberOfBytesRemaining = 0L;
		for (ByteBuffer buffer : streamBufferList) {
			numberOfBytesRemaining += buffer.remaining();

		}

		inputStreamSize = numberOfBytesRemaining;

		if (streamBufferList.isEmpty()) {
			indexOfWorkBuffer = -1;
			workBuffer = null;
		} else {
			indexOfWorkBuffer = 0;
			workBuffer = streamBufferList.get(indexOfWorkBuffer);

			byte twoBytes[] = new byte[2];
			shortBuffer = ByteBuffer.wrap(twoBytes);
			shortBuffer.order(streamByteOrder);

			bytesOfIntBuffer = new byte[4];
			intBuffer = ByteBuffer.wrap(bytesOfIntBuffer);
			intBuffer.order(streamByteOrder);

			bytesOfLongBuffer = new byte[8];
			longBuffer = ByteBuffer.wrap(bytesOfLongBuffer);
			longBuffer.order(streamByteOrder);
		}

		// log.info("limitedRemainingBytes={}, streamBufferList size={}",
		// limitedRemainingBytes, streamBufferList.size());
	}

	/**
	 * 다음 데이터를 읽기 위해서 현재 작업버퍼를 다음 버퍼로 변경한다.
	 * 
	 * @throws BufferUnderflowExceptionWithMessage
	 */
	private void nextBuffer() throws BufferUnderflowExceptionWithMessage {
		if (indexOfWorkBuffer + 1 >= streamBufferList.size()) {
			throw new BufferUnderflowExceptionWithMessage("no more data packet buffer");
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

		// log.info(String.format("in nextBuffer, indexOfWorkBuffer=[%d],
		// workBuffer.capacity=[%d]", indexOfWorkBuffer, workBuffer.capacity()));
	}

	private String doGetString(int length, Charset stringCharset)
			throws BufferUnderflowExceptionWithMessage, CharsetDecoderException {
		byte dstBytes[] = new byte[length];
		doGetBytes(dstBytes, 0, dstBytes.length);

		String dst = null;

		try {
			dst = new String(dstBytes, stringCharset);
		} catch (Exception e) {
			String errorMessage = String.format("fail to get a new String. read data hex[%s], charset[%s]",
					HexUtil.getHexStringFromByteArray(dstBytes), stringCharset.name());
			// log.warn(errorMessage, e);
			throw new CharsetDecoderException(errorMessage);
		}

		return dst;
	}

	
	

	/**
	 * 목적지 바이트 배열에 지정된 시작위치에 지정된 크기 만큼 스트림의 내용을 읽어와서 저장한다.
	 * 
	 * @param dstBytes
	 *            목적지 바이트 배열
	 * @param offset
	 *            스트림 내용이 저장될 목적지 바이트 배열의 시작 위치
	 * @param len
	 *            스트림으로 부터 읽어 올 크기
	 */
	private void doGetBytes(byte[] dstBytes, int offset, int len) throws BufferUnderflowExceptionWithMessage {
		int remainingBytesOfWorkBuffer = workBuffer.remaining();

		if (0 == remainingBytesOfWorkBuffer) {
			nextBuffer();
			remainingBytesOfWorkBuffer = workBuffer.remaining();
		}

		do {
			if (remainingBytesOfWorkBuffer >= len) {
				workBuffer.get(dstBytes, offset, len);
				numberOfBytesRemaining -= len;
				// len = 0;
				break;
			}

			workBuffer.get(dstBytes, offset, remainingBytesOfWorkBuffer);
			numberOfBytesRemaining -= remainingBytesOfWorkBuffer;

			offset += remainingBytesOfWorkBuffer;
			len -= remainingBytesOfWorkBuffer;

			nextBuffer();
			remainingBytesOfWorkBuffer = workBuffer.remaining();

		} while (0 != len);
	}

	private void throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(long numberOfBytesRequired)
			throws BufferUnderflowExceptionWithMessage {
		if (numberOfBytesRemaining < numberOfBytesRequired) {
			throw new BufferUnderflowExceptionWithMessage(String.format(
					"the number[%d] of bytes remaining in this input stream is less than [%d] byte(s) that is required",
					numberOfBytesRemaining, numberOfBytesRequired));
		}
	}

	@Override
	public byte getByte() throws BufferUnderflowExceptionWithMessage {
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(1);

		// log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));

		// byte retValue;

		/*
		 * try { retValue = workBuffer.get(); } catch (BufferUnderflowException e) {
		 * nextBuffer(); retValue = workBuffer.get(); } retValue = workBuffer.get();
		 * return retValue;
		 */

		if (!workBuffer.hasRemaining()) {
			nextBuffer();
		}

		byte returnValue = workBuffer.get();
		numberOfBytesRemaining--;

		return returnValue;
	}

	@Override
	public short getUnsignedByte() throws BufferUnderflowExceptionWithMessage {
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(1);

		short retValue = (short) (getByte() & 0xff);
		return retValue;
	}

	@Override
	public short getShort() throws BufferUnderflowExceptionWithMessage {
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(2);

		// log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));

		short retValue = 0;

		/*
		 * try { retValue = workBuffer.getShort(); } catch (BufferUnderflowException e)
		 * { // int workRemaining = workBuffer.remaining(); clearShortBuffer();
		 * shortBuffer.put(workBuffer);
		 * 
		 * nextBuffer();
		 * 
		 * while (shortBuffer.hasRemaining()) { shortBuffer.put(workBuffer.get()); }
		 * 
		 * 
		 * workBuffer.get(shortBytes, shortBuffer.position(), shortBuffer.remaining());
		 * 
		 * shortBuffer.clear(); retValue = shortBuffer.getShort(); }
		 */

		shortBuffer.clear();
		do {
			if (!workBuffer.hasRemaining()) {
				nextBuffer();
			}

			shortBuffer.put(workBuffer.get());
			numberOfBytesRemaining--;

		} while (shortBuffer.hasRemaining());

		shortBuffer.rewind();
		retValue = shortBuffer.getShort();

		return retValue;
	}

	@Override
	public int getUnsignedShort() throws BufferUnderflowExceptionWithMessage {
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(2);

		// log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));

		// int retValue;

		// clearIntBuffer();

		intBuffer.clear();
		Arrays.fill(bytesOfIntBuffer, CommonStaticFinalVars.ZERO_BYTE);
		if (ByteOrder.BIG_ENDIAN == streamByteOrder) {
			intBuffer.position(2);
			intBuffer.limit(4);
		} else {
			intBuffer.position(0);
			intBuffer.limit(2);
		}

		/*
		 * try { intBuffer.put(workBuffer.get()); intBuffer.put(workBuffer.get()); }
		 * catch (BufferUnderflowException e) { // log.info("11. workBuffer[%s]",
		 * workBuffer.toString()); // log.info("11. valueBuffer[%s]",
		 * valueBuffer.toString());
		 * 
		 * nextBuffer();
		 * 
		 * 
		 * while (intBuffer.hasRemaining()) { intBuffer.put(workBuffer.get()); }
		 * 
		 * workBuffer.get(intBytes, intBuffer.position(), intBuffer.remaining()); }
		 * 
		 * intBuffer.clear();
		 */
		// log.info("22. workBuffer[%s]", workBuffer.toString());
		// log.info("22. valueBuffer[%s]", valueBuffer.toString());

		// retValue = intBuffer.getInt();

		// return retValue;

		do {
			if (!workBuffer.hasRemaining()) {
				nextBuffer();
			}

			intBuffer.put(workBuffer.get());
			numberOfBytesRemaining--;
		} while (intBuffer.hasRemaining());

		intBuffer.clear();
		return intBuffer.getInt();
	}

	@Override
	public int getInt() throws BufferUnderflowExceptionWithMessage {
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(4);
		// log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));

		/*
		 * int retValue;
		 * 
		 * try { retValue = workBuffer.getInt(); } catch (BufferUnderflowException e) {
		 * // int workRemaining = workBuffer.remaining(); //
		 * log.info("workRemaining=[%d]", workRemaining);
		 * 
		 * 
		 * clearIntBuffer(); intBuffer.put(workBuffer);
		 * 
		 * nextBuffer();
		 * 
		 * while (intBuffer.hasRemaining()) { intBuffer.put(workBuffer.get()); }
		 * 
		 * 
		 * workBuffer.get(intBytes, intBuffer.position(), intBuffer.remaining());
		 * 
		 * intBuffer.clear(); retValue = intBuffer.getInt(); } return retValue;
		 */

		intBuffer.clear();
		do {
			if (!workBuffer.hasRemaining()) {
				nextBuffer();
			}

			intBuffer.put(workBuffer.get());
			numberOfBytesRemaining--;
		} while (intBuffer.hasRemaining());

		intBuffer.rewind();
		return intBuffer.getInt();
	}

	@Override
	public long getUnsignedInt() throws BufferUnderflowExceptionWithMessage {
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(4);
		// log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));

		/*
		 * long retValue;
		 * 
		 * clearLongBuffer();
		 * 
		 * if (ByteOrder.BIG_ENDIAN == streamByteOrder) { longBuffer.position(4);
		 * longBuffer.limit(8); } else { longBuffer.position(0); longBuffer.limit(4); }
		 * 
		 * try { longBuffer.put(workBuffer.get()); longBuffer.put(workBuffer.get());
		 * longBuffer.put(workBuffer.get()); longBuffer.put(workBuffer.get()); } catch
		 * (BufferUnderflowException e) { nextBuffer();
		 * 
		 * while (longBuffer.hasRemaining()) { longBuffer.put(workBuffer.get()); }
		 * 
		 * workBuffer.get(longBytes, longBuffer.position(), longBuffer.remaining()); }
		 * 
		 * longBuffer.clear();
		 * 
		 * retValue = longBuffer.getLong(); return retValue;
		 */

		longBuffer.clear();
		Arrays.fill(bytesOfLongBuffer, CommonStaticFinalVars.ZERO_BYTE);
		if (ByteOrder.BIG_ENDIAN == streamByteOrder) {
			longBuffer.position(4);
			longBuffer.limit(8);
		} else {
			longBuffer.position(0);
			longBuffer.limit(4);
		}

		do {
			if (!workBuffer.hasRemaining()) {
				nextBuffer();
			}

			longBuffer.put(workBuffer.get());
			numberOfBytesRemaining--;
		} while (longBuffer.hasRemaining());

		longBuffer.clear();
		return longBuffer.getLong();
	}

	@Override
	public long getLong() throws BufferUnderflowExceptionWithMessage {
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(8);
		// log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));

		/*
		 * long retValue;
		 * 
		 * try { retValue = workBuffer.getLong(); } catch (BufferUnderflowException e) {
		 * // int workRemaining = workBuffer.remaining(); clearLongBuffer();
		 * longBuffer.put(workBuffer); nextBuffer();
		 * 
		 * while (longBuffer.hasRemaining()) { longBuffer.put(workBuffer.get()); }
		 * 
		 * workBuffer.get(longBytes, longBuffer.position(), longBuffer.remaining());
		 * 
		 * longBuffer.clear(); retValue = longBuffer.getLong(); } return retValue;
		 */

		longBuffer.clear();
		do {
			if (!workBuffer.hasRemaining()) {
				nextBuffer();
			}

			longBuffer.put(workBuffer.get());
			numberOfBytesRemaining--;
		} while (longBuffer.hasRemaining());

		longBuffer.rewind();
		return longBuffer.getLong();
	}

	@Override
	public String getFixedLengthString(final int fixedLength, final CharsetDecoder wantedCharsetDecoder)
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		if (fixedLength < 0) {
			throw new IllegalArgumentException(
					String.format("the parameter fixedLength[%d] is less than zero", fixedLength));
		}

		if (0 == fixedLength) {
			return "";
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(fixedLength);

		byte dstBytes[] = new byte[fixedLength];
		ByteBuffer dstByteBuffer = ByteBuffer.wrap(dstBytes);
		doGetBytes(dstBytes, 0, dstBytes.length);

		CharBuffer dstCharBuffer = null;

		try {
			dstCharBuffer = wantedCharsetDecoder.decode(dstByteBuffer);
		} catch (Exception e) {
			String errorMessage = String.format("fail to get a new string, read data hex[%s], charset[%s]",
					HexUtil.getHexStringFromByteArray(dstBytes), wantedCharsetDecoder.charset().name());
			// log.warn(errorMessage, e);
			throw new CharsetDecoderException(errorMessage);
		}

		return dstCharBuffer.toString();
	}

	@Override
	public String getFixedLengthString(int fixedLength)
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		return getFixedLengthString(fixedLength, streamCharsetDecoder);
	}

	@Override
	public String getStringAll()
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		return getStringAll(streamCharset);
	}

	@Override
	public String getStringAll(Charset wantedCharset)
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		if (null == wantedCharset) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}

		if (0 == numberOfBytesRemaining) {
			return "";
		}

		if (numberOfBytesRemaining > Integer.MAX_VALUE) {
			/**
			 * 자바 문자열에 입력 가능한 바이트 배열의 크기는 Integer.MAX_VALUE 이다.
			 */
			throw new BufferUnderflowExceptionWithMessage(String.format(
					"the number[%d] of bytes remaing in this input stream is greater than the maximum value[%d] of integer",
					numberOfBytesRemaining, Integer.MAX_VALUE));
		}

		int length = (int) numberOfBytesRemaining;
		

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(length);

		return doGetString(length, wantedCharset);
	}

	@Override
	public String getPascalString()
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		return getUBPascalString(streamCharset);
	}

	@Override
	public String getPascalString(Charset wantedCharset)
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		return getUBPascalString(wantedCharset);
	}

	@Override
	public String getUBPascalString()
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		return getUBPascalString(streamCharset);
	}

	@Override
	public String getUBPascalString(Charset wantedCharset)
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
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
	public String getUSPascalString()
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {				
		return getUSPascalString(streamCharset);
	}

	public String getUSPascalString(Charset wantedCharset)
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
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
	public String getSIPascalString()
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		return getSIPascalString(streamCharset);
	}
	
	@Override
	public String getSIPascalString(Charset wantedCharset)
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		if (null == wantedCharset) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(4);
		
		int length = getInt();
		if (length < 0)
			throw new IllegalArgumentException(
					String.format("the pascal string length[%d] whose type is integer is less than zero", length));
		
		if (0 == length) {
			return "";
		}
		
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(length);
		
		return doGetString(length, wantedCharset);
		
	}

	@Override
	public void getBytes(byte[] dst, int offset, int length)
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException {
		if (null == dst) {
			throw new IllegalArgumentException("the parameter dst is null");
		}

		if (offset < 0) {
			throw new IllegalArgumentException(String.format("the parameter offset[%d] less than zero", offset));
		}

		if (offset >= dst.length) {
			throw new IllegalArgumentException(
					String.format("the parameter offset[%d] greater than or equal to the dest buffer's length[%d]",
							offset, dst.length));
		}

		if (length < 0) {
			throw new IllegalArgumentException(String.format("the parameter length[%d] less than zero", length));
		}

		long sumOfOffsetAndLength = ((long) offset + length);
		if (sumOfOffsetAndLength > dst.length) {
			throw new IllegalArgumentException(String.format(
					"the sum[%d] of the parameter offset[%d] and the parameter length[%d] is greater than array.length[%d]",
					sumOfOffsetAndLength, offset, length, dst.length));
		}
		
		if (0 == length) {
			return;
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(length);

		// log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));

		doGetBytes(dst, offset, length);
	}

	@Override
	public void getBytes(byte[] dstBytes) throws BufferUnderflowExceptionWithMessage, IllegalArgumentException {
		if (null == dstBytes) {
			throw new IllegalArgumentException("paramerter dstBytes is null");
		}
		
		if (0 == dstBytes.length) {
			return;
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(dstBytes.length);

		// log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));

		doGetBytes(dstBytes, 0, dstBytes.length);
	}

	@Override
	public byte[] getBytes(int len) throws BufferUnderflowExceptionWithMessage, IllegalArgumentException {
		if (len < 0) {
			throw new IllegalArgumentException(String.format("parameter len[%d] less than zero", len));
		}
		
		if (0 == len) {
			return new byte[0];
		}
		

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(len);

		// log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));

		/*
		 * long remainingBytes = remaining(); if (len > remainingBytes) { throw new
		 * IllegalArgumentException(String.format(
		 * "지정된 bye 크기[%d]는  남아 있은 버퍼 크기[%d] 보다 작거나 같아야 합니다.", len, remainingBytes)); }
		 */

		byte srcBytes[] = null;
		try {
			srcBytes = new byte[len];
		} catch (OutOfMemoryError e) {
			log.warn("OutOfMemoryError", e);
			throw e;
		}
		doGetBytes(srcBytes, 0, srcBytes.length);
		return srcBytes;
	}

	@Override
	public void skip(int n) throws BufferUnderflowExceptionWithMessage, IllegalArgumentException {
		skip((long) n);
	}

	public void skip(long n) throws BufferUnderflowExceptionWithMessage, IllegalArgumentException {
		if (0 == n) {
			return;
		}

		if (n < 0) {
			throw new IllegalArgumentException(String.format("the parameter n[%d] less than zero", n));
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(n);

		int remainingBytesOfWorkBuffer = workBuffer.remaining();

		if (0 == remainingBytesOfWorkBuffer) {
			nextBuffer();
			remainingBytesOfWorkBuffer = workBuffer.remaining();
		}

		do {

			if (n <= remainingBytesOfWorkBuffer) {
				workBuffer.position(workBuffer.position() + (int) n);
				numberOfBytesRemaining -= n;
				break;
			}

			workBuffer.position(workBuffer.limit());
			numberOfBytesRemaining -= remainingBytesOfWorkBuffer;

			n -= remainingBytesOfWorkBuffer;

			nextBuffer();
			remainingBytesOfWorkBuffer = workBuffer.remaining();
		} while (n != 0);
	}

	@Override
	public ByteOrder getByteOrder() {
		return streamByteOrder;
	}

	@Override
	public Charset getCharset() {
		return streamCharset;
	}

	public int getDataPacketBufferMaxCount() {
		return dataPacketBufferMaxCount;
	}

	@Override
	public long available() {
		return numberOfBytesRemaining;
	}

	public long getNumberOfReadBytes() {
		/**
		 * <pre>
		 * 스트림을 구성하는 버퍼들의 각각의 상태가 중구난방이므로 
		 * 처음 스트림을 구성할때 '읽을 수 있을 바이트수', 즉 '입력 스트림 크기'(=inputStreamSize)와 
		 * '남은 바이트수'(=numberOfRemaingBytes) 의 차가 읽은 바이트 수가 된다.
		 * </pre>
		 */
		return (inputStreamSize - numberOfBytesRemaining);
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
			if ((inxOfBuffer + 1) >= streamBufferListSize && searchBytes.length > baseSearchWorkRemaining)
				return -1;

			while (baseSearchWorkBuffer.hasRemaining()) {
				ByteBuffer searchWorkBuffer = baseSearchWorkBuffer.duplicate();
				searchWorkBuffer.order(streamByteOrder);
				// int searchWorkPosition = searchWorkBuffer.position();

				/** 비교할 지점(startIndex) 에서 부터 찾고자 하는 바이트 배열을 순차 비교한다. */
				int j = 0;
				for (; j < searchBytes.length; j++) {
					/** 작업중인 버퍼에서 한 바이트를 읽어와서 j 번째 바이트 배열과 비교하여 다르면 루프 종료. */
					if (searchWorkBuffer.get() != searchBytes[j])
						break;

					if (!searchWorkBuffer.hasRemaining()) {
						/** 비교할 데이터 없음 */

						/** 비교할 데이터가 더 있는 경우 검색 실패 */
						if (inxOfBuffer + 1 >= streamBufferListSize)
							return -1;

						/** 다음 버퍼의 내용과 바꾼후 검색중인 바이트 배열과 비교를 계속 진행한다. */
						searchWorkBuffer = streamBufferList.get(inxOfBuffer + 1).duplicate();
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
	
	public final int getIndexOfWorkBuffer() {
		return indexOfWorkBuffer;
	}

	public int getPositionOfWorkBuffer() {
		if (null == workBuffer) {
			return 0;
		}
		return workBuffer.position();
	}

	public CharsetDecoder getStreamCharsetDecoder() {
		return streamCharsetDecoder;
	}

	@Override
	public void close() {
		/** 파라미터 데이터 패킷 버퍼목록 회수 2번 방지용 */
		if (null == workBuffer) {
			return;
		}

		/** 파라미터 데이터 패킷 버퍼목록 회수 */
		for (WrapBuffer inputStreamWrapBuffer : readableWrapBufferList) {
			// log.info("return the inputStreamWrapBuffer[hashcode={}] to the data packet buffer pool", inputStreamWrapBuffer.hashCode());

			dataPacketBufferPool.putDataPacketBuffer(inputStreamWrapBuffer);
		}
		readableWrapBufferList.clear();
		streamBufferList.clear();
		indexOfWorkBuffer = -1;
		workBuffer = null;
		numberOfBytesRemaining = 0;
	}
}
