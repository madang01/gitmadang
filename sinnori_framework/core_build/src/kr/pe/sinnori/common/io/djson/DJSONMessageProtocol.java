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


package kr.pe.sinnori.common.io.djson;

import java.nio.ByteBuffer;
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
import kr.pe.sinnori.common.io.djson.header.DJSONHeader;
import kr.pe.sinnori.common.lib.CharsetUtil;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.SocketInputStream;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * JSON 프로토콜<br/>
 * JSON 프로토콜은 DHB 응용으로 프레임 구조는 4byte 존슨 문자열 크기 + 존슨 문자열 로 구성된다.<br/>
 * 바디 부분은 존슨 문자열이며 헤더는 단지 그 크기만을 지정한다.<br/>
 * 존슨 문자열은 외부라이브러리에 전적으로 의존한다.<br/>
 * 신놀이 메시지의 전달 개념도는 신놀이 메시지 -> 존슨 객체 -> 4byte 존슨 문자열 크기 + 존슨 문자열 전송<br/> 
 *   ==> 4byte 존슨 문자열 크기 + 존슨 문자열 수신 -> 존슨 객체 -> 신놀이 메시지 와 같다.<br/>
 *   
 * @author Jonghoon won
 *
 */
public class DJSONMessageProtocol implements CommonRootIF, MessageProtocolIF {
	private DJSONSingleItem2JSON djsonSingleItemConverter = null;
	
	/** 데이터 패킷 크기 */
	// private int dataPacketBufferSize;
	/** 1개 메시당 데이터 패킷 버퍼 최대수 */ 
	// private int dataPacketBufferMaxCntPerMessage;
	
	private DataPacketBufferQueueManagerIF dataPacketBufferQueueManager = null;
	
	
	private static final int messageHeaderSize = 4;
	
	private JSONParser jsonParser = new JSONParser();	
	
	public DJSONMessageProtocol( 
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) {
		// this.dataPacketBufferSize = dataPacketBufferQueueManager.getDataPacketBufferSize();
		// this.dataPacketBufferMaxCntPerMessage = dataPacketBufferQueueManager.getDataPacketBufferMaxCntPerMessage();
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		this.djsonSingleItemConverter = new DJSONSingleItem2JSON();
	}
	
	@Override
	public ArrayList<WrapBuffer> M2S(AbstractMessage messageObj,
			Charset clientCharset)
			throws NoMoreDataPacketBufferException, BodyFormatException {
		CharsetEncoder clientCharsetEncoder = CharsetUtil.createCharsetEncoder(clientCharset);
		FreeSizeOutputStream bodyOutputStream = 
				new FreeSizeOutputStream(clientCharset, 
						clientCharsetEncoder, 0, dataPacketBufferQueueManager);
		String jsonStr = messageObj.toJSONString();
		
		// FIXME!
		// log.info(jsonStr);
		
		byte[] jsonStrBytes = jsonStr.getBytes(DJSONHeader.JSON_STRING_CHARSET);
		bodyOutputStream.putInt(jsonStrBytes.length);
		bodyOutputStream.putBytes(jsonStrBytes);
		
		ArrayList<WrapBuffer> messageWrapBufferList = bodyOutputStream.getFlipDataPacketBufferList();
		
		return messageWrapBufferList;
	}

	@Override
	public ArrayList<AbstractMessage> S2MList(
			Class<? extends AbstractMessage> targetClass,
			Charset clientCharset,
			SocketInputStream socketInputStream,
			MessageMangerIF messageManger) throws HeaderFormatException,
			NoMoreDataPacketBufferException {
		
		CharsetDecoder charsetOfProjectDecoder = CharsetUtil.createCharsetDecoder(clientCharset);
		// ArrayList<WrapBuffer> messageReadWrapBufferList = messageInputStreamResource.getMessageReadWrapBufferList();
		DJSONHeader messageHeader = (DJSONHeader)socketInputStream.getUserDefObject();
		// ByteOrder byteOrderOfProject = messageInputStreamResource.getByteOrder();
		
		ArrayList<AbstractMessage> messageList = new ArrayList<AbstractMessage>();
		
		boolean isMoreMessage = false;
		
		int messageReadWrapBufferListSize = socketInputStream.getDataPacketBufferListSize();
		if (messageReadWrapBufferListSize == 0) {
			log.fatal(String.format("messageReadWrapBufferListSize is zero"));
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
		int startIndex = -1;
		int startPosition = -1;
		
		try {			
			// long inputStramSizeBeforeMessageWork = (lastIndex - startIndex) * dataPacketBufferSize - startPosition + lastPosition;
			// long inputStramSizeBeforeMessageWork = freeSizeInputStream.remaining();
			long inputStramSizeBeforeMessageWork = socketInputStream.position();
			
			/*log.info(String.format("1. messageHeaderSize=[%d], inputStramSizeBeforeMessageWork[%d]",
					messageHeaderSize, inputStramSizeBeforeMessageWork));*/
			
			do {
				isMoreMessage = false;
				
				if (null == messageHeader
						&& inputStramSizeBeforeMessageWork >= messageHeaderSize) {
					
					/** 스트림 통해서 헤더 읽기 */
					if (null == freeSizeInputStream) {
						freeSizeInputStream = socketInputStream
								.getFreeSizeInputStream(charsetOfProjectDecoder);
						startIndex = freeSizeInputStream.getIndexOfWorkBuffer();
						startPosition = freeSizeInputStream.getPositionOfWorkBuffer();
					}
					
					DJSONHeader  workMessageHeader = new DJSONHeader();
					workMessageHeader.lenOfJSONStr = freeSizeInputStream.getInt();
					
					
					messageHeader = workMessageHeader;
				}
				
				if (null != messageHeader) {					
					long messageFrameSize = (long)messageHeader.lenOfJSONStr + messageHeaderSize;
					
					if (inputStramSizeBeforeMessageWork >= messageFrameSize) {
						/** 메시지 추출*/
						if (null == freeSizeInputStream) {
							freeSizeInputStream = socketInputStream
									.getFreeSizeInputStream(charsetOfProjectDecoder);
							startIndex = freeSizeInputStream.getIndexOfWorkBuffer();
							startPosition = freeSizeInputStream.getPositionOfWorkBuffer();
							long expectedPosition = startIndex*lastInputStreamBuffer.capacity()+startPosition+messageHeaderSize;
							freeSizeInputStream.skip(expectedPosition);
						}
						
						String jsonStr = null;
						try {
							jsonStr = freeSizeInputStream.getString(messageHeader.lenOfJSONStr, DJSONHeader.JSON_STRING_CHARSET.newDecoder());
						} catch (SinnoriCharsetCodingException e1) {
							String errorMessage = e1.getMessage();
							log.warn(String.format("문자셋 문제로 JSON 문자열 추출 실패, %s", errorMessage), e1);
							/**
							 * json 객체를 얻지 못하면 json 객체에 포함된 
							 * 신놀이 메시지 운영정보(메시지 식별자, 메일 박스 식별자, 메일 식별자)를 얻을 수 없다.
							 * 신놀이 메시지 운영정보는 헤더 정보이므로 이를 정상적으로 얻지 못했기때문에 헤더 포맷 에러 처리를 한다.  
							 */
							throw new HeaderFormatException(errorMessage);
						}
						
											
						JSONObject jsonObj = null;
						
						try {
							jsonObj = (JSONObject)jsonParser.parse(jsonStr);
						} catch(ParseException pe){
							log.warn("ParseException", pe);
							throw new HeaderFormatException(String.format("JSON ParseException::%s::%s", pe.toString(), jsonStr));
						}
						
						String messageID = (String)jsonObj.get("messageID");
						if (null == messageID) {
							throw new HeaderFormatException(String.format("Bad JSON str, check messageID::%s", jsonStr));
						}
						
						Long jsonMailboxID = (Long)jsonObj.get("mailboxID");
						if (null == jsonMailboxID) {
							throw new HeaderFormatException(String.format("Bad JSON str, check mailboxID::%s", jsonStr));
						}
						
						Long jsonMailID = (Long)jsonObj.get("mailID");
						if (null == jsonMailID) {
							throw new HeaderFormatException(String.format("Bad JSON str, check mailID::%s", jsonStr));
						}
						
						if (jsonMailboxID < Integer.MIN_VALUE || jsonMailboxID > Integer.MAX_VALUE) {
							throw new HeaderFormatException(String.format("Bad JSON str, check mailID::%s", jsonStr));
						}
						
						if (jsonMailID < Integer.MIN_VALUE || jsonMailID > Integer.MAX_VALUE) {
							throw new HeaderFormatException(String.format("Bad JSON str, check mailID::%s", jsonStr));
						}
						
						int mailboxID = jsonMailboxID.intValue();
						int mailID = jsonMailID.intValue();
						
						if (targetClass.equals(InputMessage.class)) {
							InputMessage workInObj = null;
							try {
								workInObj = messageManger.createInputMessage(messageID);
								workInObj.messageHeaderInfo.mailboxID = mailboxID;
								workInObj.messageHeaderInfo.mailID = mailID;
								
								workInObj.O2M(jsonObj, djsonSingleItemConverter);
								
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
								errorInObj.messageHeaderInfo.mailboxID = mailboxID;
								errorInObj.messageHeaderInfo.mailID = mailID;
								errorInObj.setAttribute("whereError", "S");
								errorInObj.setAttribute("errorGubun", "M");
								errorInObj.setAttribute("errorMessageID", messageID);
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
								errorInObj.setAttribute("errorMessageID", messageID);
								errorInObj.setAttribute("errorMessage", e.getMessage());
	
								workInObj = errorInObj;
							}
							
							//log.debug(String.format("10. lastInputStreamBuffer=[%s]", lastInputStreamBuffer.toString()));
	
							/** 목록에 메시지 추가 */
							messageList.add(workInObj);
						} else {
							OutputMessage workOutObj = null;
							try {
								workOutObj = messageManger.createOutputMessage(messageID);
								workOutObj.messageHeaderInfo.mailboxID = mailboxID;
								workOutObj.messageHeaderInfo.mailID = mailID;
								
								workOutObj.O2M(jsonObj, djsonSingleItemConverter);
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
								errorOutObj.messageHeaderInfo.mailboxID = mailboxID;
								errorOutObj.messageHeaderInfo.mailID = mailID;
								errorOutObj.setAttribute("whereError", "S");
								errorOutObj.setAttribute("errorGubun", "M");
								errorOutObj.setAttribute("errorMessageID", messageID);
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
								errorOutObj.setAttribute("errorMessageID", messageID);
								errorOutObj.setAttribute("errorMessage", e.getMessage());
	
								workOutObj = errorOutObj;
							}
							
							//log.debug(String.format("10. lastInputStreamBuffer=[%s]", lastInputStreamBuffer.toString()));
							
							
							/** 목록에 메시지 추가 */
							messageList.add(workOutObj);							
						}
						
						inputStramSizeBeforeMessageWork = freeSizeInputStream.remaining();
						if (inputStramSizeBeforeMessageWork > messageHeaderSize) {
							isMoreMessage = true;
						}
						messageHeader = null;
						startPosition = freeSizeInputStream.getPositionOfWorkBuffer();
						startIndex = freeSizeInputStream.getIndexOfWorkBuffer();
					}
				}
			} while (isMoreMessage);
				
			if (messageList.size() > 0) {
				socketInputStream.truncate(startIndex, startPosition);
			} else if (!lastInputStreamBuffer.hasRemaining()) {
				/** 메시지 추출 실패했는데도 마지막 버퍼가 꽉차있다면 스트림 크기를 증가시킨다. 단 설정파일 환경변수 "메시지당 최대 데이터 패킷 갯수" 만큼만 증가될수있다. */
				lastInputStreamBuffer = socketInputStream.nextDataPacketBuffer();
			}
			
		} catch(MessageItemException e) {
			log.fatal(e.getMessage(), e);
			System.exit(1);
		} finally {
			socketInputStream.setUserDefObject(messageHeader);
		}
		
		
		return messageList;
	}	
}
