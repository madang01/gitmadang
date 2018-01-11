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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;

import kr.pe.sinnori.common.etc.CharsetUtil;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriBufferUnderflowException;
import kr.pe.sinnori.common.io.FreeSizeInputStream;
import kr.pe.sinnori.common.io.FreeSizeOutputStream;
import kr.pe.sinnori.common.io.SocketInputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.project.DataPacketBufferPoolManagerIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.ReceivedLetter;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;
import kr.pe.sinnori.common.protocol.thb.header.THBMessageHeader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	/** 데이터 패킷 크기 */
	// private int dataPacketBufferSize;
	/** 1개 메시당 데이터 패킷 버퍼 최대수 */ 
	// private int dataPacketBufferMaxCntPerMessage;
	
	/** 메시지 헤더에 사용되는 문자열 메시지 식별자의 크기, 단위 byte */
	private int messageIDFixedSize;
	/** 메시지 헤더 크기, 단위 byte */
	private int messageHeaderSize;
	
	private DataPacketBufferPoolManagerIF dataPacketBufferQueueManager = null;
	private THBSingleItemDecoder thbSingleItemDecoder = null;
	private THBSingleItemEncoder thbSingleItemEncoder = null;
	
	private ByteOrder byteOrderOfProject = null;
	
	// private ClientObjectManager clientMessageController = ClientObjectManager.getInstance();
	// private ServerObjectManager serverMessageController = ServerObjectManager.getInstance();
	
	public THBMessageProtocol(
			int messageIDFixedSize, 
			DataPacketBufferPoolManagerIF dataPacketBufferQueueManager) {
		
		this.messageIDFixedSize = messageIDFixedSize;
		this.messageHeaderSize = THBMessageHeader.getMessageHeaderSize(messageIDFixedSize);
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		this.byteOrderOfProject = dataPacketBufferQueueManager.getByteOrder();
		
		// this.dataPacketBufferSize= dataPacketBufferQueueManager.getDataPacketBufferSize();
		// this.dataPacketBufferMaxCntPerMessage = dataPacketBufferQueueManager.getDataPacketBufferMaxCntPerMessage();
		
		
		this.thbSingleItemDecoder = new THBSingleItemDecoder();
		this.thbSingleItemEncoder = new THBSingleItemEncoder();
	}
	
	@Override
	public ArrayList<WrapBuffer> M2S(AbstractMessage messageObj, AbstractMessageEncoder messageEncoder, Charset charsetOfProject) 
			throws NoMoreDataPacketBufferException, BodyFormatException {
		CharsetEncoder charsetOfProjectEncoder = CharsetUtil.createCharsetEncoder(charsetOfProject);		
		
		/** 바디 만들기 */
		FreeSizeOutputStream bodyOutputStream = 
				new FreeSizeOutputStream(charsetOfProject, 
						charsetOfProjectEncoder, messageHeaderSize, dataPacketBufferQueueManager);
		
		try {
			messageEncoder.encode(messageObj, thbSingleItemEncoder, charsetOfProject, bodyOutputStream);
		} catch (NoMoreDataPacketBufferException e) {
			throw e;
		} catch (BodyFormatException e) {
			throw e;
		} catch (OutOfMemoryError e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = String.format(
					"unknown error::header=[%s]",
					messageObj.toString());
			log.warn(errorMessage, e);
			
			throw new BodyFormatException(errorMessage);
		}

		/** 데이터 헤더 만들기 */
		THBMessageHeader messageHeader = new THBMessageHeader(messageIDFixedSize);
		messageHeader.messageID = messageObj.getMessageID();
		messageHeader.mailboxID = messageObj.messageHeaderInfo.mailboxID;
		messageHeader.mailID = messageObj.messageHeaderInfo.mailID;
		messageHeader.bodySize =  bodyOutputStream.postion() - messageHeaderSize;
		
		// FIXME!
		//log.info(messageHeader.toString());
		
		ArrayList<WrapBuffer> messageWrapBufferList = bodyOutputStream.getFlipDataPacketBufferList();
		
		ByteBuffer firstWorkBuffer = messageWrapBufferList.get(0).getByteBuffer();
		
		ByteBuffer firstDupBuffer = firstWorkBuffer.duplicate();
		firstDupBuffer.order(byteOrderOfProject);
		
		messageHeader.writeMessageHeader(firstDupBuffer, charsetOfProject, charsetOfProjectEncoder);
		
		// log.debug(messageHeader.toString());
		// log.debug(firstWorkBuffer.toString());
		
		
		return messageWrapBufferList;
	}
	
	@Override
	public SingleItemDecoderIF getSingleItemDecoder() {
		return thbSingleItemDecoder;
	}

	
	@Override
	public ArrayList<ReceivedLetter> S2MList( 
			Charset charsetOfProject,
			SocketInputStream socketInputStream) 
					throws HeaderFormatException, NoMoreDataPacketBufferException {
		CharsetDecoder charsetOfProjectDecoder = CharsetUtil.createCharsetDecoder(charsetOfProject);		
		THBMessageHeader messageHeader = (THBMessageHeader)socketInputStream.getUserDefObject();		
		ArrayList<ReceivedLetter> receivedLetterList = new ArrayList<ReceivedLetter>();		
		
		boolean isMoreMessage = false;
		int messageReadWrapBufferListSize = socketInputStream
				.getDataPacketBufferListSize();
		if (messageReadWrapBufferListSize == 0) {
			log.error(String.format("messageReadWrapBufferListSize is zero"));
			System.exit(1);
		}
		
		ByteBuffer lastInputStreamBuffer = socketInputStream
				.getLastDataPacketBuffer();
		
		/**
		 * 소켓별 스트림 자원을 갖는다. 스트림은 데이터 패킷 버퍼 목록으로 구현한다.<br/>
		 * 반환되는 스트림은 데이터 패킷 버퍼의 속성을 건들지 않기 위해서 복사본으로 구성되며 읽기 가능 상태이다.<br/>
		 * 내부 처리를 요약하면 All ByteBuffer.duplicate().flip() 이다.<br/>
		 * 매번 새로운 스트림이 만들어지는 단점이 있다. <br/>
		 */
		FreeSizeInputStream freeSizeInputStream = null;
		
		try {
			// long inputStramSizeBeforeMessageWork = freeSizeInputStream.remaining();
			long inputStramSizeBeforeMessageWork = socketInputStream.position();
			int lastPostionOfWorkBuffer = 0;
			int lastIndexOfWorkBuffer = 0;
			
			do {
				isMoreMessage = false;

				if (null == messageHeader
						&& inputStramSizeBeforeMessageWork >= messageHeaderSize) {
					/** 헤더 읽기 */
					if (null == freeSizeInputStream) {
						freeSizeInputStream = socketInputStream
								.getFreeSizeInputStream(charsetOfProjectDecoder);
					}
					
					THBMessageHeader  workMessageHeader = new THBMessageHeader(messageIDFixedSize);
					workMessageHeader.readMessageHeader(freeSizeInputStream);

					if (workMessageHeader.bodySize < 0) {
						// header format exception
						String errorMessage = String.format(
								"thb header::body size less than zero %s",
								workMessageHeader.toString());
						throw new HeaderFormatException(errorMessage);
					}
					
					
					messageHeader = workMessageHeader;
				}
				
				if (null != messageHeader) {
					// log.info(String.format("2. startIndex=[%d], startPosition=[%d], messageHeader=[%s]", startIndex, startPosition, messageHeader.toString()));
					
					long messageFrameSize = messageHeaderSize + messageHeader.bodySize;
					
					if (inputStramSizeBeforeMessageWork >= messageFrameSize) {
						/** 메시지 추출 */
						if (null == freeSizeInputStream) {
							freeSizeInputStream = socketInputStream
									.getFreeSizeInputStream(charsetOfProjectDecoder);
							long skipBytes = messageHeaderSize;
							
							try {
								freeSizeInputStream.skip(skipBytes);
							} catch (IllegalArgumentException e) {
								String errorMessage = e.getMessage();
								log.error(errorMessage, e);
								System.exit(1);
							} catch (SinnoriBufferUnderflowException e) {
								String errorMessage = e.getMessage();
								log.error(errorMessage, e);
								System.exit(1);
							}
						}
						
						//long postionBeforeReadingBody = freeSizeInputStream.position();
						
						// log.info(String.format("3. messageFrameSize=[%d], postionBeforeReadingBody=[%d], expectedPosition=[%d]", messageFrameSize, postionBeforeReadingBody, expectedPosition));
						
						FreeSizeInputStream bodyInputStream = null;
						try {
							bodyInputStream = freeSizeInputStream.getInputStream(messageHeader.bodySize);
						} catch (IllegalArgumentException e) {
							String errorMessage = e.getMessage();
							log.error(errorMessage, e);
							System.exit(1);
						} catch (SinnoriBufferUnderflowException e) {
							String errorMessage = e.getMessage();
							log.error(errorMessage, e);
							System.exit(1);
						}
						
						lastPostionOfWorkBuffer = freeSizeInputStream.getPositionOfWorkBuffer();
						lastIndexOfWorkBuffer = freeSizeInputStream.getIndexOfWorkBuffer();

						ReceivedLetter receivedLetter = 
								new ReceivedLetter(messageHeader.messageID, 
										messageHeader.mailboxID, messageHeader.mailID, bodyInputStream);
						
						receivedLetterList.add(receivedLetter);

						inputStramSizeBeforeMessageWork -= messageFrameSize;
						if (inputStramSizeBeforeMessageWork > messageHeaderSize) {
							isMoreMessage = true;
						}
						messageHeader = null;
						// startPosition = freeSizeInputStream.getPositionOfWorkBuffer();
						// startIndex = freeSizeInputStream.getIndexOfWorkBuffer();
					}
				}
			} while (isMoreMessage);
			
			if (receivedLetterList.size() > 0) {
				// socketInputStream.truncate(startIndex, startPosition);
				// socketInputStream.truncate(freeSizeInputStream.getIndexOfWorkBuffer(), freeSizeInputStream.getPositionOfWorkBuffer());
				socketInputStream.truncate(lastIndexOfWorkBuffer, lastPostionOfWorkBuffer);
			} else if (!lastInputStreamBuffer.hasRemaining()) {
				/** 메시지 추출 실패했는데도 마지막 버퍼가 꽉차있다면 스트림 크기를 증가시킨다. 단 설정파일 환경변수 "메시지당 최대 데이터 패킷 갯수" 만큼만 증가될수있다. */
				lastInputStreamBuffer = socketInputStream.nextDataPacketBuffer();
			}
		} finally {
			socketInputStream.setUserDefObject(messageHeader);
		}
		
		
		return receivedLetterList;
	}
	
	public int getMessageHeaderSize() {
		return messageHeaderSize;
	}
}