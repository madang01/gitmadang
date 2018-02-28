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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import kr.pe.sinnori.common.protocol.thb.header.THBMessageHeader;

/**
 * THB 메시지 프로토콜<br/> 
 * DHB 의 축소형 프로토콜로 DHB 와 달리 쓰레드 세이프 검출및 데이터 검증에 취약하다.<br/> 
 * 따라서 쓰레드 세이프 검증이 필요 없고 데이터 신뢰성 높은 TCP/IP 환경에서 유효하다.<br/>
 * DHB 를 통해서 쓰레드 세이프 검증 완료한후 THB 프로토콜로 전환하는것을 추천함.<br/>
 * @author Won Jonghoon
 *
 */
public class THBMessageProtocol implements MessageProtocolIF {
	private Logger log = LoggerFactory.getLogger(THBMessageProtocol.class);
	
	
	/** 메시지 헤더에 사용되는 문자열 메시지 식별자의 크기, 단위 byte */
	private int messageIDFixedSize;
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
	
	
	private final Charset headerCharset = Charset.forName("ISO-8859-1");
	private CharsetEncoder headerCharsetEncoder = null;
	private CharsetDecoder headerCharsetDecoder = null;
	
	public THBMessageProtocol(
			int messageIDFixedSize, 
			int dataPacketBufferMaxCntPerMessage,
			CharsetEncoder streamCharsetEncoder,
			CharsetDecoder streamCharsetDecoder,
			DataPacketBufferPoolIF dataPacketBufferPool) {
		if (messageIDFixedSize <= 0) {
			String errorMessage = String.format("the parameter messageIDFixedSize[%d] is less than or equal to zero",
					messageIDFixedSize);
			throw new IllegalArgumentException(errorMessage);
		}

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
		
		this.messageIDFixedSize = messageIDFixedSize;
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		this.streamCharsetEncoder = streamCharsetEncoder;
		this.streamCharsetDecoder = streamCharsetDecoder;
		
		this.dataPacketBufferPool = dataPacketBufferPool;
		
		this.messageHeaderSize = messageIDFixedSize+ 2 + 4 + 8;
		
		thbSingleItemDecoder = new THBSingleItemDecoder(streamCharsetDecoder);
		thbSingleItemEncoder = new THBSingleItemEncoder(streamCharsetEncoder);
		
		this.headerCharsetEncoder = headerCharset.newEncoder();
		this.headerCharsetEncoder.onMalformedInput(streamCharsetEncoder.malformedInputAction());
		this.headerCharsetEncoder.onUnmappableCharacter(streamCharsetEncoder.unmappableCharacterAction());
		
		
		this.headerCharsetDecoder = headerCharset.newDecoder();
		this.headerCharsetDecoder.onMalformedInput(streamCharsetDecoder.malformedInputAction());
		this.headerCharsetEncoder.onUnmappableCharacter(streamCharsetDecoder.unmappableCharacterAction());
	}
	
	@Override
	public List<WrapBuffer> M2S(AbstractMessage messageObj, AbstractMessageEncoder messageEncoder) 
			throws NoMoreDataPacketBufferException, BodyFormatException, HeaderFormatException {
		/** 바디 만들기 */
		FreeSizeOutputStream bodyOutputStream = 
				new FreeSizeOutputStream(dataPacketBufferMaxCntPerMessage, streamCharsetEncoder, dataPacketBufferPool);
		
		
		
		try {
			messageEncoder.encode(messageObj, thbSingleItemEncoder, bodyOutputStream);
		} catch (NoMoreDataPacketBufferException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = String.format(
					"unknown error::header=[%s]",
					messageObj.toString());
			log.warn(errorMessage, e);
			
			throw new BodyFormatException(errorMessage);
		}
		
		

		/** 데이터 헤더 만들기 */
		THBMessageHeader messageHeader = new THBMessageHeader();
		messageHeader.messageID = messageObj.getMessageID();
		messageHeader.mailboxID = messageObj.messageHeaderInfo.mailboxID;
		messageHeader.mailID = messageObj.messageHeaderInfo.mailID;
		messageHeader.bodySize =  bodyOutputStream.size();
		
		
		
		// log.info(messageHeader.toString());
		
		FreeSizeOutputStream headerOutputStream = new FreeSizeOutputStream(dataPacketBufferMaxCntPerMessage,
				headerCharsetEncoder, dataPacketBufferPool);
		
		
		
		try {
			messageHeader.toOutputStream(headerOutputStream, messageIDFixedSize, headerCharset);
		} catch (NoMoreDataPacketBufferException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = String.format("unknown error::messageObj=[%s]", messageObj.toString());
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
	public ArrayList<WrapReadableMiddleObject> S2MList(SocketOutputStream socketOutputStream) 
					throws HeaderFormatException, NoMoreDataPacketBufferException {		
		THBMessageHeader messageHeader = (THBMessageHeader)socketOutputStream.getUserDefObject();		
		
		ArrayList<WrapReadableMiddleObject> wrapReadableMiddleObjectList = new ArrayList<WrapReadableMiddleObject>();		
		
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
						workMessageHeader.fromInputStream(socketInputStream, messageIDFixedSize, headerCharsetDecoder);
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

						WrapReadableMiddleObject wrapReadableMiddleObject = 
								new WrapReadableMiddleObject(messageHeader.messageID, 
										messageHeader.mailboxID, messageHeader.mailID, messageInputStream);
						
						wrapReadableMiddleObjectList.add(wrapReadableMiddleObject);


						socketOutputStreamSize = socketOutputStream.size();
						if (socketOutputStreamSize > messageHeaderSize) {
							isMoreMessage = true;
						}
						
						messageHeader = null;
						/*socketInputStream.close();
						socketInputStream = socketOutputStream.createNewSocketInputStream();*/
					}
				}
			} while (isMoreMessage);			
		} finally {
			socketOutputStream.setUserDefObject(messageHeader);
		}
		
		
		return wrapReadableMiddleObjectList;
	}
	
	public int getMessageHeaderSize() {
		return messageHeaderSize;
	}
}