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


package kr.pe.sinnori.common.io.dhb;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.FreeSizeInputStream;
import kr.pe.sinnori.common.io.FreeSizeOutputStream;
import kr.pe.sinnori.common.io.MessageExchangeProtocolIF;
import kr.pe.sinnori.common.io.dhb.header.DHBMessageHeader;
import kr.pe.sinnori.common.lib.CharsetUtil;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.MessageInputStreamResourcePerSocket;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.util.HexUtil;

/**
 * 메시지 교환 프로토콜 DHB 를 구현한 클래스.  
 * @author Jonghoon Won
 *
 */
public class DHBMessageProtocol implements CommonRootIF, MessageExchangeProtocolIF {
	
	/** 데이터 패킷 크기 */
	private int dataPacketBufferSize;
	/** 1개 메시당 데이터 패킷 버퍼 최대수 */ 
	private int dataPacketBufferMaxCntPerMessage;
	
	/** 메시지 헤더에 사용되는 문자열 메시지 식별자의 크기, 단위 byte */
	private int messageIDFixedSize;
	/** 메시지 헤더 크기, 단위 byte */
	private int messageHeaderSize;
	
	private DataPacketBufferQueueManagerIF dataPacketBufferQueueManager = null;
	private DHBSingleItemConverter dhbSingleItemConverter = null;
	
	public DHBMessageProtocol(
			int messageIDFixedSize, 
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) {
		
		this.messageIDFixedSize = messageIDFixedSize;
		this.messageHeaderSize = DHBMessageHeader.getMessageHeaderSize(messageIDFixedSize);
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		
		this.dataPacketBufferSize= dataPacketBufferQueueManager.getDataPacketBufferSize();
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferQueueManager.getDataPacketBufferMaxCntPerMessage();
		
		dhbSingleItemConverter = new DHBSingleItemConverter();
	}
	
	@Override
	public ArrayList<WrapBuffer> M2S(AbstractMessage messageObj, ByteOrder byteOrderOfProject, Charset charsetOfProject) throws NoMoreDataPacketBufferException, BodyFormatException {
		CharsetEncoder charsetOfProjectEncoder = CharsetUtil.createCharsetEncoder(charsetOfProject);		
		java.security.MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			log.fatal("failed to get a MD5 instance", e);
			System.exit(1);
		}
		// java.security.MessageDigest md5 = DigestUtils.getMd5Digest();
		
		
		/** 바디 만들기 */
		FreeSizeOutputStream bodyOutputStream = 
				new FreeSizeOutputStream(byteOrderOfProject, charsetOfProject, 
						charsetOfProjectEncoder, messageHeaderSize, dataPacketBufferQueueManager);
		messageObj.M2S(bodyOutputStream, dhbSingleItemConverter);

		/** 데이터 헤더 만들기 */
		DHBMessageHeader messageHeader = new DHBMessageHeader(messageIDFixedSize);
		messageHeader.messageID = messageObj.getMessageID();
		messageHeader.mailboxID = messageObj.messageHeaderInfo.mailboxID;
		messageHeader.mailID = messageObj.messageHeaderInfo.mailID;
		messageHeader.bodySize =  bodyOutputStream.postion() - messageHeaderSize;
		
		/** 바디 MD5 */
		ArrayList<WrapBuffer> messageWrapBufferList = bodyOutputStream.getNoFlipDataPacketBufferList();
		int bufferListSize = messageWrapBufferList.size();
		
		ByteBuffer firstWorkBuffer = messageWrapBufferList.get(0).getByteBuffer();
		firstWorkBuffer.flip();
		ByteBuffer firstDupBuffer = firstWorkBuffer.duplicate();
		firstDupBuffer.order(byteOrderOfProject);
		firstDupBuffer.position(messageHeaderSize);
		md5.update(firstDupBuffer);
		
		for (int i=1; i < bufferListSize; i++) {
			ByteBuffer workBuffer = messageWrapBufferList.get(i).getByteBuffer();
			workBuffer.flip();
			ByteBuffer dupBuffer = workBuffer.duplicate();
			md5.update(dupBuffer);
		}
		
		messageHeader.bodyMD5 = md5.digest();
		
		firstDupBuffer.clear();
		messageHeader.writeMessageHeader(firstDupBuffer, charsetOfProject, charsetOfProjectEncoder, md5);
		
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
		ArrayList<WrapBuffer> messageReadWrapBufferList = messageInputStreamResource.getMessageReadWrapBufferList();
		DHBMessageHeader messageHeader = (DHBMessageHeader)messageInputStreamResource.getEtcInfo();
		
		ArrayList<AbstractMessage> messageList = new ArrayList<AbstractMessage>();
		
		java.security.MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// java.security.MessageDigest md5 = DigestUtils.getMd5Digest();
		
		boolean isMoreMessage = false;
		
		try {
			do {
				int messageReadWrapBufferListSize = messageReadWrapBufferList.size();
				if (messageReadWrapBufferListSize == 0) {
					log.fatal(String.format("messageReadWrapBufferListSize is zero"));
					System.exit(1);
				}
	
				int lastIndex = messageReadWrapBufferListSize - 1;
				ByteBuffer lastInputStreamBuffer = messageReadWrapBufferList.get(lastIndex).getByteBuffer();
				ByteOrder byteOrderOfLastBuffer = lastInputStreamBuffer.order();
				int finalReadPosition = lastInputStreamBuffer.position();
				long inputStramSizeBeforeMessageWork = lastIndex	* dataPacketBufferSize + finalReadPosition;
				
				isMoreMessage = false;
				
				// log.debug(String.format("1. messageReadWrapBufferListSize=[%d], lastInputStreamBuffer=[%s], inputStramSizeBeforeMessageWork=[%d]", messageReadWrapBufferListSize, lastInputStreamBuffer.toString(), inputStramSizeBeforeMessageWork));
				
				if (null == messageHeader
						&& inputStramSizeBeforeMessageWork >= messageHeaderSize) {
					/** 헤더 읽기전 위치 마크및 헤더 읽을 위치 0으로 이동 */
					ByteBuffer dupMessageHeaderBuffer = messageReadWrapBufferList.get(0).getByteBuffer().duplicate();
					dupMessageHeaderBuffer.order(byteOrderOfLastBuffer);
					dupMessageHeaderBuffer.position(messageHeaderSize);
					dupMessageHeaderBuffer.flip();
					
					// log.debug(String.format("3.1 dupMessageHeaderBuffer=[%s]", dupMessageHeaderBuffer.toString()));
					
					/** 헤더 읽기 */
					DHBMessageHeader  workMessageHeader = new DHBMessageHeader(messageIDFixedSize);
					
					workMessageHeader.readMessageHeader(dupMessageHeaderBuffer, md5, charsetOfProjectDecoder);
					
					// log.debug(String.format("3.2 dupMessageHeaderBuffer=[%s]", dupMessageHeaderBuffer.toString()));
					//log.debug(String.format("4. lastInputStreamBuffer=[%s]", lastInputStreamBuffer.toString()));
					// log.debug(workMessageHeader.toString());
					
					messageHeader = workMessageHeader;
				} 
				
				
				if (null != messageHeader) {
					//log.debug(String.format("5. lastInputStreamBuffer=[%s]", lastInputStreamBuffer.toString()));
					
					long messagePacketSize = messageHeader.messageHeaderSize + messageHeader.bodySize;
					
					if (inputStramSizeBeforeMessageWork >= messagePacketSize) {
						/** 메시지 추출*/
						
						int endPositionOfMessage  = (int)(messagePacketSize - lastIndex * dataPacketBufferSize);
						
						/**
						 * 마지막 출력 메시지 래퍼 버퍼내 메시지의 끝 위치는 
						 * 마지막으로 데이터를 읽어 들인 위치 안쪽에 위치하므로 
						 * long 타입을 integer 타입으로 변환해도 문제가 안된다.
						 */
						lastInputStreamBuffer.position(endPositionOfMessage);
						
						//log.debug(String.format("8. lastInputStreamBuffer=[%s]", lastInputStreamBuffer.toString()));
						
						/** 헤더 제외한 body MD5 작업후 바디 부분 읽도록 조취 */
						WrapBuffer firstOutputMessageWrapBuffer = messageReadWrapBufferList.get(0);
						ByteBuffer firstOutputMessageBuffer = firstOutputMessageWrapBuffer.getByteBuffer();
						firstOutputMessageBuffer.flip();
						
						firstOutputMessageBuffer.position(messageHeaderSize);
						
						//log.debug(firstOutputMessageBuffer.toString());
						
						// md5.reset();
						md5.update(firstOutputMessageBuffer);
						firstOutputMessageBuffer.position(messageHeaderSize);
						
						
						/** body MD5 작업후 바디 부분 읽도록 조취 */
						// int outputMessageWrapBufferListSize = messageReadWrapBufferList.size();
						for (int j=1; j < messageReadWrapBufferListSize; j++) {
							WrapBuffer bodyWrapBuffer = messageReadWrapBufferList.get(j);
							ByteBuffer bodyBuffer = bodyWrapBuffer.getByteBuffer(); 
							
							/** 메시지 추출을 위한 읽기 가능 상태로 전환 */
							bodyBuffer.flip();
							
							/** body MD5 구하기 */
							md5.update(bodyBuffer);
							
							/** md5 결과로 변경된 position 속성 되돌리기 */
							bodyBuffer.position(0);
						}
						byte bodyMD5[] = md5.digest();
						
						/** 바디 MD5 와 헤더 정보 바디 MD5 비교 */
						boolean isValidBodyMD5 = java.util.Arrays.equals(
								bodyMD5, messageHeader.bodyMD5);
						if (!isValidBodyMD5) {
							String errorMessage = String.format(
									"fail to check body MD5, header[%s], body md5[%s]",
									messageHeader.toString(), HexUtil.byteArrayAllToHex(bodyMD5));
							
							throw new HeaderFormatException(errorMessage);
						}
						
						//log.debug(String.format("9. lastInputStreamBuffer=[%s]", lastInputStreamBuffer.toString()));
						
						/** 바디 스트림으로 부터 메시지 내용 추출 */
						FreeSizeInputStream bodyInputStream = 
								new FreeSizeInputStream(messageReadWrapBufferList, charsetOfProjectDecoder, dataPacketBufferQueueManager);
						if (targetClass.equals(InputMessage.class)) {
							InputMessage workInObj = null;
							try {
								workInObj = messageManger.createInputMessage(messageHeader.messageID);
								workInObj.messageHeaderInfo.mailboxID = messageHeader.mailboxID;
								workInObj.messageHeaderInfo.mailID = messageHeader.mailID;
								
								workInObj.S2M(bodyInputStream, dhbSingleItemConverter);
								
								if (bodyInputStream.remaining() > 0) {
									// FIXME! 잔존 데이터 있음. 
									String errorMessage = String
											.format("메시지[%s]를 읽는 과정에서 잔존 데이터가 남았습니다.",
													workInObj.toString());
									// log.warn(errorMessage, e);
									throw new HeaderFormatException(errorMessage);
								}
							} catch (MessageInfoNotFoundException e) {
								log.info(String.format("MessageInfoNotFoundException::header=[%s]",
										messageHeader.toString()), e);
	
								InputMessage errorInObj = null;
								try {
									errorInObj = messageManger.createInputMessage("SelfExn");
								} catch (MessageInfoNotFoundException e1) {
									log.fatal("시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.", e1);
									System.exit(1);
								}
								/** 참고) 메시지 정보 파일 없기때문에 정상적인 메시지를 생성할 수 없어 헤더 정보를 통해 메시지 헤더 정보를 저장한다. */
								errorInObj.messageHeaderInfo.mailboxID = messageHeader.mailboxID;
								errorInObj.messageHeaderInfo.mailID = messageHeader.mailID;
								errorInObj.setAttribute("whereError", "S");
								errorInObj.setAttribute("errorGubun", "M");
								errorInObj.setAttribute("errorMessageID",
										messageHeader.messageID);
								errorInObj.setAttribute("errorMessage", e.getMessage());
	
								workInObj = errorInObj;
							} catch (BodyFormatException e) {
								log.info(String.format("BodyFormatException::header=[%s]",
										messageHeader.toString()), e);
	
								InputMessage errorInObj = null;
								try {
									errorInObj = messageManger.createInputMessage("SelfExn");
								} catch (MessageInfoNotFoundException e1) {
									log.fatal("시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.", e1);
									System.exit(1);
								}
	
								errorInObj.messageHeaderInfo = workInObj.messageHeaderInfo;
								errorInObj.setAttribute("whereError", "S");
								errorInObj.setAttribute("errorGubun", "B");
								errorInObj.setAttribute("errorMessageID",
										messageHeader.messageID);
								errorInObj.setAttribute("errorMessage", e.getMessage());
	
								workInObj = errorInObj;
							}
							
							//log.debug(String.format("10. lastInputStreamBuffer=[%s]", lastInputStreamBuffer.toString()));
	
							/** 목록에 메시지 추가 */
							messageList.add(workInObj);
						} else {
							OutputMessage workOutObj = null;
							try {
								workOutObj = messageManger.createOutputMessage(messageHeader.messageID);
								workOutObj.messageHeaderInfo.mailboxID = messageHeader.mailboxID;
								workOutObj.messageHeaderInfo.mailID = messageHeader.mailID;
								
								workOutObj.S2M(bodyInputStream, dhbSingleItemConverter);
								
								if (bodyInputStream.remaining() > 0) {
									// FIXME! 잔존 데이터 있음. 
									String errorMessage = String
											.format("메시지[%s]를 읽는 과정에서 잔존 데이터가 남았습니다.",
													workOutObj.toString());
									// log.warn(errorMessage, e);
									throw new HeaderFormatException(errorMessage);
								}
							} catch (MessageInfoNotFoundException e) {
								log.info(String.format("BodyFormatException::header=[%s]",
										messageHeader.toString()), e);
	
								OutputMessage errorOutObj = null;
								try {
									errorOutObj = messageManger.createOutputMessage("SelfExn");
								} catch (MessageInfoNotFoundException e1) {
									log.fatal("시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.", e1);
									System.exit(1);
								}
								/** 참고) 메시지 정보 파일 없기때문에 정상적인 메시지를 생성할 수 없어 헤더 정보를 통해 메시지 헤더 정보를 저장한다. */
								errorOutObj.messageHeaderInfo.mailboxID = messageHeader.mailboxID;
								errorOutObj.messageHeaderInfo.mailID = messageHeader.mailID;
								errorOutObj.setAttribute("whereError", "S");
								errorOutObj.setAttribute("errorGubun", "M");
								errorOutObj.setAttribute("errorMessageID",
										messageHeader.messageID);
								errorOutObj.setAttribute("errorMessage", e.getMessage());
	
								workOutObj = errorOutObj;
							} catch (BodyFormatException e) {
								log.info(String.format("BodyFormatException::header=[%s]",
										messageHeader.toString()), e);
	
								OutputMessage errorOutObj = null;
								try {
									errorOutObj = messageManger.createOutputMessage("SelfExn");
								} catch (MessageInfoNotFoundException e1) {
									log.fatal("시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.", e1);
									System.exit(1);
								}
	
								errorOutObj.messageHeaderInfo = workOutObj.messageHeaderInfo;
								errorOutObj.setAttribute("whereError", "S");
								errorOutObj.setAttribute("errorGubun", "B");
								errorOutObj.setAttribute("errorMessageID",
										messageHeader.messageID);
								errorOutObj.setAttribute("errorMessage", e.getMessage());
	
								workOutObj = errorOutObj;
							}
							
							//log.debug(String.format("10. lastInputStreamBuffer=[%s]", lastInputStreamBuffer.toString()));
							
							
							/** 목록에 메시지 추가 */
							messageList.add(workOutObj);	
						}
						
						
						
						
						
						//log.debug(String.format("11. lastInputStreamBuffer=[%s]", lastInputStreamBuffer.toString()));
						
						/** 추출된 메시지 영역 삭제 - 추출된 메시지 내용이 담겨 있는 마지막을 제외한 출력 메시지 랩 버퍼를 삭제한다.  */
						messageInputStreamResource.freeWrapBufferWithoutLastBuffer(endPositionOfMessage, finalReadPosition);
						
						/** 메시지 추출 종료후 다음 메시지 추출을 위한 변수 초기화 */
						messageHeader = null;
						
						// lastIndex = 0;
						// inputStramSizeBeforeMessageWork = lastInputStreamBuffer.remaining();
	
						/**
						 * <pre>
						 * 다음 메시지 존재 여부를 판단하여 결과적으로 다음 메시지를 추출하도록 한다.   
						 * 참고) 소켓 채널 관점에서 읽기 이벤트 전용 selector 는 다음 읽은 데이터가 없을때까지 대기 모드로 빠진다.
						 * 때문에 출력 메시지 랩 버퍼에 존재하는 모든 메시지를 추츨해야만 무한 대기 없이 메시지 처리를 할 수 있다.
						 * 쉽게 예를 들면 "가" 메시지 와 "나" 메시지가 마지막 출력 메시지 랩 버퍼에 동시에 들어온 상태라면 
						 * "가" 메시지만 추출할 경우 "나" 메시지는 계속 대기 상태로 있게 된다. 
						 * 클라이언트에서 "다" 메시지를 보내어 읽기 이벤트가 발생되어야 깨어나서 "나" 메시지가 처리가 된다.
						 * </pre>
						 */
						
						inputStramSizeBeforeMessageWork = lastInputStreamBuffer.position();
						
						//log.debug(String.format("12. lastInputStreamBuffer=[%s]", lastInputStreamBuffer.toString()));
						
						if (inputStramSizeBeforeMessageWork >= messageHeaderSize) {
							/** 헤더 읽기전 위치 마크및 헤더 읽을 위치 0으로 이동 */
							ByteBuffer dupMessageHeaderBuffer = lastInputStreamBuffer.duplicate();
							dupMessageHeaderBuffer.order(byteOrderOfLastBuffer);
							dupMessageHeaderBuffer.position(messageHeaderSize);
							dupMessageHeaderBuffer.flip();
							
							//log.debug(String.format("14 dupMessageHeaderBuffer=[%s]", dupMessageHeaderBuffer.toString()));
							
							/** 헤더 읽기 */
							DHBMessageHeader  workMessageHeader = new DHBMessageHeader(messageIDFixedSize);
							workMessageHeader.readMessageHeader(dupMessageHeaderBuffer, md5, charsetOfProjectDecoder);
							
							//log.debug(String.format("15. lastInputStreamBuffer=[%s]", lastInputStreamBuffer.toString()));
							
							
							messageHeader = workMessageHeader;
							
							
							if (inputStramSizeBeforeMessageWork >= (workMessageHeader.bodySize + messageHeaderSize)) {
								/** 버퍼 안에 다음 메시지 존재 함. */
								isMoreMessage = true;
							}
						}
					} else if (!lastInputStreamBuffer.hasRemaining()) {
						/** 다음 버퍼 */
						lastInputStreamBuffer = addWrapBuffer(messageReadWrapBufferList, messageHeader, byteOrderOfLastBuffer);
					}
				}
			} while (isMoreMessage);
		} catch(MessageItemException e) {
			log.fatal(e.getMessage(), e);
			System.exit(1);
		} finally {
			messageInputStreamResource.setEtcInfo(messageHeader);
		}
		
		
		return messageList;
	}
	
	/**
	 * @return 읽기 전용 버퍼 목록에 추가된 읽기 전용 버퍼
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼를 확보 할 수 없을대 던지는 예외
	 */
	private ByteBuffer addWrapBuffer(ArrayList<WrapBuffer> messageReadWrapBufferList, DHBMessageHeader messageHeader, ByteOrder byteOrderOfLastBuffer) throws NoMoreDataPacketBufferException {
		/** 메시지 1개당 최대 데이터 패킷 버퍼 갯수에 도달했을 경우 에러 처리함 */
		if (dataPacketBufferMaxCntPerMessage == messageReadWrapBufferList
				.size()) {
			String errorMessage = String
					.format("메시지당 최대 데이터 패킷 갯수[%d]를 넘는 메시지[%s]입니다. ",
							dataPacketBufferMaxCntPerMessage,
							messageHeader
									.toString());
			// log.warn(errorMessage);
			throw new NoMoreDataPacketBufferException(
					errorMessage);
		}
		
		WrapBuffer wrapBuffer = dataPacketBufferQueueManager.pollDataPacketBuffer(byteOrderOfLastBuffer);
		messageReadWrapBufferList.add(wrapBuffer);
		ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();
		
		return byteBuffer;
	}
}
