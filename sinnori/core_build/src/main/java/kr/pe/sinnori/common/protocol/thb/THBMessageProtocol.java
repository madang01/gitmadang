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

package kr.pe.sinnori.common.protocol.thb;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayDeque;
import java.util.List;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.FreeSizeInputStream;
import kr.pe.sinnori.common.io.FreeSizeOutputStream;
import kr.pe.sinnori.common.io.SocketInputStream;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

/**
 * THB 메시지 프로토콜<br/> 
 * DHB 의 축소형 프로토콜로 DHB 와 달리 쓰레드 세이프 검출및 데이터 검증에 취약하다.<br/> 
 * 따라서 쓰레드 세이프 검증이 필요 없고 데이터 신뢰성 높은 TCP/IP 환경에서 유효하다.<br/>
 * DHB 를 통해서 쓰레드 세이프 검증 완료한후 THB 프로토콜로 전환하는것을 추천함.<br/>
 * @author Won Jonghoon
 *
 */
public class THBMessageProtocol implements MessageProtocolIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(THBMessageProtocol.class);
	
	
	private int dataPacketBufferMaxCntPerMessage;
	// private Charset streamCharset = null;
	private CharsetEncoder streamCharsetEncoder;	
	@SuppressWarnings("unused")
	private CharsetDecoder streamCharsetDecoder;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	
	/** 메시지 헤더 크기, 단위 byte */
	private int messageHeaderSize;
	private THBSingleItemDecoder thbSingleItemDecoder = null;	
	private THBSingleItemEncoder thbSingleItemEncoder = null;
	
	
	private final Charset headerCharset = Charset.forName("UTF-8");
	private CharsetEncoder headerCharsetEncoder = null;
	private CharsetDecoder headerCharsetDecoder = null;
	
	public THBMessageProtocol( 
			int dataPacketBufferMaxCntPerMessage,
			CharsetEncoder streamCharsetEncoder,
			CharsetDecoder streamCharsetDecoder,
			DataPacketBufferPoolIF dataPacketBufferPool) {
		if (dataPacketBufferMaxCntPerMessage <= 0) {
			String errorMessage = String.format(
					"the parameter dataPacketBufferMaxCntPerMessage[%d] is less than or equal to zero",
					dataPacketBufferMaxCntPerMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == streamCharsetEncoder) {
			throw new IllegalArgumentException("the parameter streamCharsetEncoder is null");
		}

		if (null == streamCharsetDecoder) {
			throw new IllegalArgumentException("the parameter streamCharsetDecoder is null");
		}

		Charset streamCharsetOfEncoder = streamCharsetEncoder.charset();
		Charset streamCharsetOfDecoder = streamCharsetDecoder.charset();

		if (!streamCharsetOfEncoder.equals(streamCharsetOfDecoder)) {
			String errorMessage = String.format(
					"the parameter streamCharsetEncoder[%s] is not same to the parameter streamCharsetDecoder[%s]",
					streamCharsetOfEncoder.name(), streamCharsetOfDecoder.name());

			throw new IllegalArgumentException(errorMessage);
		}
		
		// streamCharset = streamCharsetOfEncoder;

		if (null == dataPacketBufferPool) {

			throw new IllegalArgumentException("the parameter dataPacketBufferPoolManager is null");
		}
		
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		this.streamCharsetEncoder = streamCharsetEncoder;
		this.streamCharsetDecoder = streamCharsetDecoder;
		
		this.dataPacketBufferPool = dataPacketBufferPool;
		
		this.messageHeaderSize = 8;
		
		THBSingleItemDecoderMatcherIF thbSingleItemDecoderMatcher = new THBSingleItemDecoderMatcher(streamCharsetDecoder);
		thbSingleItemDecoder = new THBSingleItemDecoder(thbSingleItemDecoderMatcher);
		
		
		THBSingleItemEncoderMatcherIF thbSingleItemEncoderMatcher = new THBSingleItemEncoderMatcher(streamCharsetEncoder);		
		thbSingleItemEncoder = new THBSingleItemEncoder(thbSingleItemEncoderMatcher);
		
		this.headerCharsetEncoder = headerCharset.newEncoder();
		this.headerCharsetEncoder.onMalformedInput(streamCharsetEncoder.malformedInputAction());
		this.headerCharsetEncoder.onUnmappableCharacter(streamCharsetEncoder.unmappableCharacterAction());
		
		
		this.headerCharsetDecoder = headerCharset.newDecoder();
		this.headerCharsetDecoder.onMalformedInput(streamCharsetDecoder.malformedInputAction());
		this.headerCharsetDecoder.onUnmappableCharacter(streamCharsetDecoder.unmappableCharacterAction());
	}
	
	@Override
	public List<WrapBuffer> M2S(AbstractMessage inputMessage, AbstractMessageEncoder messageEncoder) 
			throws NoMoreDataPacketBufferException, BodyFormatException, HeaderFormatException {
		String messageID = inputMessage.getMessageID();
		int mailboxID = inputMessage.messageHeaderInfo.mailboxID;
		int mailID = inputMessage.messageHeaderInfo.mailID;
		
		/** 바디 만들기 */
		FreeSizeOutputStream bodyOutputStream = 
				new FreeSizeOutputStream(dataPacketBufferMaxCntPerMessage, streamCharsetEncoder, dataPacketBufferPool);
		
		try {
			bodyOutputStream.putUBPascalString(messageID, headerCharset);
			bodyOutputStream.putUnsignedShort(mailboxID);
			bodyOutputStream.putInt(mailID);
		} catch (NoMoreDataPacketBufferException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("fail to make a body header of the the parameter inputMessage[")
					.append(inputMessage.toString())
					.append("], errmsg=").append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			throw new HeaderFormatException(errorMessage);
		}
		
		try {
			messageEncoder.encode(inputMessage, thbSingleItemEncoder, bodyOutputStream);
		} catch (NoMoreDataPacketBufferException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = String.format(
					"unknown error::header=[%s]",
					inputMessage.toString());
			log.warn(errorMessage, e);
			
			throw new BodyFormatException(errorMessage);
		}
				

		/** 데이터 헤더 만들기 */
		THBMessageHeader messageHeader = new THBMessageHeader();		
		messageHeader.bodySize =  bodyOutputStream.size();
		
		
		
		// log.info(messageHeader.toString());
		
		FreeSizeOutputStream headerOutputStream = new FreeSizeOutputStream(dataPacketBufferMaxCntPerMessage,
				headerCharsetEncoder, dataPacketBufferPool);
		
		
		
		try {
			messageHeader.toOutputStream(headerOutputStream, headerCharset);
		} catch (NoMoreDataPacketBufferException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = String.format("unknown error::messageObj=[%s]", inputMessage.toString());
			log.warn(errorMessage, e);

			throw new HeaderFormatException(errorMessage);
		}	
		
		List<WrapBuffer> readbleWrapBufferListOfHeaderOutputStream = headerOutputStream.getReadableWrapBufferList();
		List<WrapBuffer> readableWrapBufferListOfBodyOutputStream = bodyOutputStream.getReadableWrapBufferList();
		readbleWrapBufferListOfHeaderOutputStream.addAll(readableWrapBufferListOfBodyOutputStream);
		
		// log.debug(messageHeader.toString());
		// log.debug(firstWorkBuffer.toString());
		
		
		return readbleWrapBufferListOfHeaderOutputStream;
	}
	
	@Override
	public SingleItemDecoderIF getSingleItemDecoder() {
		return thbSingleItemDecoder;
	}

	
	@Override
	public void S2MList(SocketOutputStream socketOutputStream, ArrayDeque<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue) 
					throws HeaderFormatException, NoMoreDataPacketBufferException {		
		THBMessageHeader messageHeader = (THBMessageHeader)socketOutputStream.getUserDefObject();		
				
		
		boolean isMoreMessage = false;
		
		long socketOutputStreamSize = socketOutputStream.size();
		
		try {
			do {
				if (null == messageHeader
						&& socketOutputStreamSize >= messageHeaderSize) {
					/** 헤더 읽기 */
					THBMessageHeader workMessageHeader = new THBMessageHeader();
					
					SocketInputStream socketInputStream = socketOutputStream.createNewSocketInputStream();					
					try {
						workMessageHeader.fromInputStream(socketInputStream, headerCharsetDecoder);
					} finally {
						socketInputStream.close();
					}

					if (workMessageHeader.bodySize < 0) {
						// header format exception
						String errorMessage = String.format(
								"dhb header::body size less than zero %s",
								workMessageHeader.toString());
						throw new HeaderFormatException(errorMessage);
					}
					

					messageHeader = workMessageHeader;
				}
				
				if (null != messageHeader) {
					/*log.info(String.format("3. inputStramSizeBeforeMessageWork[%d]", inputStramSizeBeforeMessageWork));*/
					
					
					long messageFrameSize = messageHeaderSize
							+ messageHeader.bodySize;
					

					if (socketOutputStreamSize >= messageFrameSize) {
						/** 메시지 추출 */
						FreeSizeInputStream messageInputStream = socketOutputStream
								.cutMessageInputStreamFromStartingPosition(messageFrameSize);
						
						try {
							messageInputStream.skip(messageHeaderSize);
						} catch (Exception e) {
							log.error("unknown error::"+e.getMessage());
							System.exit(1);
						}
						
						String messageID = null;
						int mailboxID;
						int mailID;
						try {
							messageID = messageInputStream.getUBPascalString(headerCharset);
							mailboxID = messageInputStream.getUnsignedShort();
							mailID = messageInputStream.getInt();
						} catch (Exception e) {
							String errorMessage = new StringBuilder("fail to read a body header from the output stream, , errmsg=")
									.append(e.getMessage()).toString();
							log.warn(errorMessage, e);
							
							throw new HeaderFormatException(errorMessage);
						}

						WrapReadableMiddleObject wrapReadableMiddleObject = 
								new WrapReadableMiddleObject(messageID, 
										mailboxID, mailID, messageInputStream);
						
						wrapReadableMiddleObjectQueue.addLast(wrapReadableMiddleObject);


						socketOutputStreamSize = socketOutputStream.size();
						if (socketOutputStreamSize > messageHeaderSize) {
							isMoreMessage = true;
						} else {
							isMoreMessage = false;
						}
						
						messageHeader = null;
					}
				}
			} while (isMoreMessage);			
		} finally {
			socketOutputStream.setUserDefObject(messageHeader);
		}
	}
	
	public int getMessageHeaderSize() {
		return messageHeaderSize;
	}
}