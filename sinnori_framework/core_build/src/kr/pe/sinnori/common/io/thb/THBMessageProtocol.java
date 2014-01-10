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

package kr.pe.sinnori.common.io.thb;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriCharsetCodingException;
import kr.pe.sinnori.common.io.FreeSizeInputStream;
import kr.pe.sinnori.common.io.FreeSizeOutputStream;
import kr.pe.sinnori.common.io.MessageProtocolIF;
import kr.pe.sinnori.common.io.dhb.DHBSingleItem2Stream;
import kr.pe.sinnori.common.io.dhb.header.DHBMessageHeader;
import kr.pe.sinnori.common.io.thb.header.THBMessageHeader;
import kr.pe.sinnori.common.lib.CharsetUtil;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.MessageInputStreamResourcePerSocket;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;

/**
 * THB 프로토콜<br/> 
 * DHB 의 축소형 프로토콜로 DHB 와 달리 쓰레드 세이프 검출및 데이터 검증에 취약하다.<br/> 
 * 따라서 쓰레드 세이프 검증이 필요 없고 데이터 신뢰성 높은 TCP/IP 환경에서 유효하다.<br/>
 * DHB 를 통해서 쓰레드 세이프 검증 완료한후 THB 프로토콜로 전환하는것을 추천함.<br/>
 * @author Jonghoon won
 *
 */
public class THBMessageProtocol implements CommonRootIF, MessageProtocolIF {
	
	/** 데이터 패킷 크기 */
	// private int dataPacketBufferSize;
	/** 1개 메시당 데이터 패킷 버퍼 최대수 */ 
	// private int dataPacketBufferMaxCntPerMessage;
	
	/** 메시지 헤더에 사용되는 문자열 메시지 식별자의 크기, 단위 byte */
	private int messageIDFixedSize;
	/** 메시지 헤더 크기, 단위 byte */
	private int messageHeaderSize;
	
	private DataPacketBufferQueueManagerIF dataPacketBufferQueueManager = null;
	private DHBSingleItem2Stream dhbSingleItem2Stream = null;
	
	private ByteOrder byteOrderOfProject = null;
	
	public THBMessageProtocol(
			int messageIDFixedSize, 
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) {
		
		this.messageIDFixedSize = messageIDFixedSize;
		this.messageHeaderSize = THBMessageHeader.getMessageHeaderSize(messageIDFixedSize);
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		this.byteOrderOfProject = dataPacketBufferQueueManager.getByteOrder();
		
		// this.dataPacketBufferSize= dataPacketBufferQueueManager.getDataPacketBufferSize();
		// this.dataPacketBufferMaxCntPerMessage = dataPacketBufferQueueManager.getDataPacketBufferMaxCntPerMessage();
		
		
		dhbSingleItem2Stream = new DHBSingleItem2Stream();
	}
	
	@Override
	public ArrayList<WrapBuffer> M2S(AbstractMessage messageObj, Charset charsetOfProject) throws NoMoreDataPacketBufferException, BodyFormatException {
		CharsetEncoder charsetOfProjectEncoder = CharsetUtil.createCharsetEncoder(charsetOfProject);		
		
		/** 바디 만들기 */
		FreeSizeOutputStream bodyOutputStream = 
				new FreeSizeOutputStream(charsetOfProject, 
						charsetOfProjectEncoder, messageHeaderSize, dataPacketBufferQueueManager);
		messageObj.M2O(bodyOutputStream, dhbSingleItem2Stream);

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
	public ArrayList<AbstractMessage> S2MList(Class<? extends AbstractMessage> targetClass,
			Charset charsetOfProject,
			MessageInputStreamResourcePerSocket messageInputStreamResource, 
			MessageMangerIF messageManger) 
					throws HeaderFormatException, NoMoreDataPacketBufferException {
		CharsetDecoder charsetOfProjectDecoder = CharsetUtil.createCharsetDecoder(charsetOfProject);		
		THBMessageHeader messageHeader = (THBMessageHeader)messageInputStreamResource.getUserDefObject();		
		ArrayList<AbstractMessage> messageList = new ArrayList<AbstractMessage>();		
		
		boolean isMoreMessage = false;
		int messageReadWrapBufferListSize = messageInputStreamResource
				.getDataPacketBufferListSize();
		if (messageReadWrapBufferListSize == 0) {
			log.fatal(String.format("messageReadWrapBufferListSize is zero"));
			System.exit(1);
		}
		
		try {
			ByteBuffer lastInputStreamBuffer = messageInputStreamResource
					.getLastDataPacketBuffer();
			int lastIndex = messageReadWrapBufferListSize - 1;
			int lastPosition = lastInputStreamBuffer.position();
			
			/**
			 * 소켓별 스트림 자원을 갖는다. 스트림은 데이터 패킷 버퍼 목록으로 구현한다.<br/>
			 * 반환되는 스트림은 데이터 패킷 버퍼의 속성을 건들지 않기 위해서 복사본으로 구성되며 읽기 가능 상태이다.<br/>
			 * 내부 처리를 요약하면 All ByteBuffer.duplicate().flip() 이다.<br/>
			 * 매번 새로운 스트림이 만들어지는 단점이 있다. <br/>
			 */
			FreeSizeInputStream freeSizeInputStream = messageInputStreamResource
					.getFreeSizeInputStream(charsetOfProjectDecoder);
			
			long inputStramSizeBeforeMessageWork = freeSizeInputStream
					.remaining();

			log.info(String
					.format("1. lastIndex=[%d], lastPosition=[%d], inputStramSizeBeforeMessageWork[%d]",
							lastIndex, lastPosition, inputStramSizeBeforeMessageWork));

			// ArrayList<ByteBuffer> streamBufferList = freeSizeInputStream.getStreamBufferList();
			// int streamBufferListSize = streamBufferList.size();
			
			do {
				isMoreMessage = false;

				if (null == messageHeader
						&& inputStramSizeBeforeMessageWork >= messageHeaderSize) {
					/** 헤더 읽기 */
					THBMessageHeader  workMessageHeader = new THBMessageHeader(messageIDFixedSize);
					
					try {
						workMessageHeader.messageID = freeSizeInputStream
								.getString( messageIDFixedSize,
										CharsetUtil.createCharsetDecoder(DHBMessageHeader.HEADER_CHARSET)).trim();
					} catch (SinnoriCharsetCodingException e1) {
						String errorMessage = e1.getMessage();
						log.warn(errorMessage, e1);
						throw new HeaderFormatException(errorMessage);
					}
					workMessageHeader.mailboxID = freeSizeInputStream
							.getUnsignedShort();
					workMessageHeader.mailID = freeSizeInputStream.getInt();
					workMessageHeader.bodySize = freeSizeInputStream.getLong();

					if (workMessageHeader.bodySize < 0) {
						// header format exception
						String errorMessage = String.format(
								"dhb header body size less than zero %s",
								workMessageHeader.toString());
						throw new HeaderFormatException(errorMessage);
					}
					
					
					messageHeader = workMessageHeader;
					
					long messageFrameSize = messageHeader.messageHeaderSize
							+ messageHeader.bodySize;
					if (inputStramSizeBeforeMessageWork >= messageFrameSize) {
						/** 메시지 추출 */
						long postionBeforeReadingBody = freeSizeInputStream
								.position();
						
						if (targetClass.equals(InputMessage.class)) {
							InputMessage workInObj = null;
							try {
								workInObj = messageManger
										.createInputMessage(messageHeader.messageID);
								workInObj.messageHeaderInfo.mailboxID = messageHeader.mailboxID;
								workInObj.messageHeaderInfo.mailID = messageHeader.mailID;

								workInObj.O2M(freeSizeInputStream,
										dhbSingleItem2Stream);

								long postionAfterReadingBody = freeSizeInputStream
										.position();

								if ((postionAfterReadingBody - postionBeforeReadingBody) != messageHeader.bodySize) {
									// FIXME! 잔존 데이터 있음.
									String errorMessage = String.format(
											"메시지[%s]를 읽는 과정에서 잔존 데이터가 남았습니다.",
											workInObj.toString());
									// log.warn(errorMessage, e);
									throw new HeaderFormatException(
											errorMessage);
								}
							} catch (MessageInfoNotFoundException e) {
								log.info(
										String.format(
												"MessageInfoNotFoundException::header=[%s]",
												messageHeader.toString()), e);

								InputMessage errorInObj = null;
								try {
									errorInObj = messageManger
											.createInputMessage("SelfExn");
								} catch (MessageInfoNotFoundException e1) {
									log.fatal(
											"시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.",
											e1);
									System.exit(1);
								}
								/**
								 * 참고) 메시지 정보 파일 없기때문에 정상적인 메시지를 생성할 수 없어 헤더 정보를
								 * 통해 메시지 헤더 정보를 저장한다.
								 */
								errorInObj.messageHeaderInfo.mailboxID = messageHeader.mailboxID;
								errorInObj.messageHeaderInfo.mailID = messageHeader.mailID;
								errorInObj.setAttribute("whereError", "S");
								errorInObj.setAttribute("errorGubun", "M");
								errorInObj.setAttribute("errorMessageID",
										messageHeader.messageID);
								errorInObj.setAttribute("errorMessage",
										e.getMessage());

								workInObj = errorInObj;

								long skipBytes = messageHeader.bodySize;
								freeSizeInputStream.skip(skipBytes);
							} catch (BodyFormatException e) {
								log.info(String.format(
										"BodyFormatException::header=[%s]",
										messageHeader.toString()), e);

								InputMessage errorInObj = null;
								try {
									errorInObj = messageManger
											.createInputMessage("SelfExn");
								} catch (MessageInfoNotFoundException e1) {
									log.fatal(
											"시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.",
											e1);
									System.exit(1);
								}

								errorInObj.messageHeaderInfo = workInObj.messageHeaderInfo;
								errorInObj.setAttribute("whereError", "S");
								errorInObj.setAttribute("errorGubun", "B");
								errorInObj.setAttribute("errorMessageID",
										messageHeader.messageID);
								errorInObj.setAttribute("errorMessage",
										e.getMessage());

								workInObj = errorInObj;

								long postionAfterReadingBody = freeSizeInputStream
										.position();
								long skipBytes = messageHeader.bodySize
										- (postionAfterReadingBody - postionBeforeReadingBody);
								freeSizeInputStream.skip(skipBytes);
							}

							// log.debug(String.format("10. lastInputStreamBuffer=[%s]",
							// lastInputStreamBuffer.toString()));

							/** 목록에 메시지 추가 */
							messageList.add(workInObj);
						} else {
							OutputMessage workOutObj = null;
							try {
								workOutObj = messageManger
										.createOutputMessage(messageHeader.messageID);
								workOutObj.messageHeaderInfo.mailboxID = messageHeader.mailboxID;
								workOutObj.messageHeaderInfo.mailID = messageHeader.mailID;

								workOutObj.O2M(freeSizeInputStream,
										dhbSingleItem2Stream);

								if (freeSizeInputStream.remaining() > 0) {
									// FIXME! 잔존 데이터 있음.
									String errorMessage = String.format(
											"메시지[%s]를 읽는 과정에서 잔존 데이터가 남았습니다.",
											workOutObj.toString());
									// log.warn(errorMessage, e);
									throw new HeaderFormatException(
											errorMessage);
								}
							} catch (MessageInfoNotFoundException e) {
								log.info(String.format(
										"BodyFormatException::header=[%s]",
										messageHeader.toString()), e);

								OutputMessage errorOutObj = null;
								try {
									errorOutObj = messageManger
											.createOutputMessage("SelfExn");
								} catch (MessageInfoNotFoundException e1) {
									log.fatal(
											"시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.",
											e1);
									System.exit(1);
								}
								/**
								 * 참고) 메시지 정보 파일 없기때문에 정상적인 메시지를 생성할 수 없어 헤더 정보를
								 * 통해 메시지 헤더 정보를 저장한다.
								 */
								errorOutObj.messageHeaderInfo.mailboxID = messageHeader.mailboxID;
								errorOutObj.messageHeaderInfo.mailID = messageHeader.mailID;
								errorOutObj.setAttribute("whereError", "S");
								errorOutObj.setAttribute("errorGubun", "M");
								errorOutObj.setAttribute("errorMessageID",
										messageHeader.messageID);
								errorOutObj.setAttribute("errorMessage",
										e.getMessage());

								workOutObj = errorOutObj;

								long skipBytes = messageHeader.bodySize;
								freeSizeInputStream.skip(skipBytes);
							} catch (BodyFormatException e) {
								log.info(String.format(
										"BodyFormatException::header=[%s]",
										messageHeader.toString()), e);

								OutputMessage errorOutObj = null;
								try {
									errorOutObj = messageManger
											.createOutputMessage("SelfExn");
								} catch (MessageInfoNotFoundException e1) {
									log.fatal(
											"시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.",
											e1);
									System.exit(1);
								}

								errorOutObj.messageHeaderInfo = workOutObj.messageHeaderInfo;
								errorOutObj.setAttribute("whereError", "S");
								errorOutObj.setAttribute("errorGubun", "B");
								errorOutObj.setAttribute("errorMessageID",
										messageHeader.messageID);
								errorOutObj.setAttribute("errorMessage",
										e.getMessage());

								workOutObj = errorOutObj;

								long postionAfterReadingBody = freeSizeInputStream
										.position();
								long skipBytes = messageHeader.bodySize
										- (postionAfterReadingBody - postionBeforeReadingBody);
								freeSizeInputStream.skip(skipBytes);
							}

							// log.debug(String.format("10. lastInputStreamBuffer=[%s]",
							// lastInputStreamBuffer.toString()));

							/** 목록에 메시지 추가 */
							messageList.add(workOutObj);
						}

						inputStramSizeBeforeMessageWork = freeSizeInputStream.remaining();
						if (inputStramSizeBeforeMessageWork > messageHeaderSize) {
							isMoreMessage = true;
							messageHeader = null;
						}
					}
				}
			} while (isMoreMessage);
			
			if (messageList.size() > 0) {
				int startIndex = freeSizeInputStream.getIndexOfWorkBuffer();
				int startPosition = freeSizeInputStream.getPositionOfWorkBuffer();
				messageInputStreamResource.truncate(startIndex, startPosition);
			} else if (!lastInputStreamBuffer.hasRemaining()) {
				/** 메시지 추출 실패했는데도 마지막 버퍼가 꽉차있다면 스트림 크기를 증가시킨다. 단 설정파일 환경변수 "메시지당 최대 데이터 패킷 갯수" 만큼만 증가될수있다. */
				lastInputStreamBuffer = messageInputStreamResource.nextDataPacketBuffer();
			}	
		} catch(MessageItemException e) {
			log.fatal(e.getMessage(), e);
			System.exit(1);
		} finally {
			messageInputStreamResource.setUserDefObject(messageHeader);
		}
		
		
		return messageList;
	}
}