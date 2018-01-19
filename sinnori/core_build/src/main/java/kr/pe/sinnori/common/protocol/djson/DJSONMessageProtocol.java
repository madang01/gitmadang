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
package kr.pe.sinnori.common.protocol.djson;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;

import kr.pe.sinnori.common.etc.CharsetUtil;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriBufferUnderflowException;
import kr.pe.sinnori.common.exception.SinnoriCharsetCodingException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolManagerIF;
import kr.pe.sinnori.common.io.FreeSizeInputStream;
import kr.pe.sinnori.common.io.FreeSizeOutputStream;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.ReceivedLetter;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;
import kr.pe.sinnori.common.protocol.djson.header.DJSONHeader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSON 메시지 프로토콜<br/>
 * JSON 프로토콜은 DHB 응용으로 프레임 구조는 4byte 존슨 문자열 크기 + 존슨 문자열 로 구성된다.<br/>
 * 바디 부분은 존슨 문자열이며 헤더는 단지 그 크기만을 지정한다.<br/>
 * 존슨 문자열은 외부라이브러리에 전적으로 의존한다.<br/>
 * 신놀이 메시지의 전달 개념도는 신놀이 메시지 -> 존슨 객체 -> 4byte 존슨 문자열 크기 + 존슨 문자열 전송<br/> 
 *   ==> 4byte 존슨 문자열 크기 + 존슨 문자열 수신 -> 존슨 객체 -> 신놀이 메시지 와 같다.<br/>
 *   
 * @author Won Jonghoon
 *
 */
public class DJSONMessageProtocol implements MessageProtocolIF {
	private Logger log = LoggerFactory.getLogger(DJSONMessageProtocol.class);
	
	private DJSONSingleItemDecoder jsonSingleItemDecoder = null;
	private DJSONSingleItemEncoder jsonSingleItemEncoder = null;
	
	/** 데이터 패킷 크기 */
	// private int dataPacketBufferSize;
	/** 1개 메시당 데이터 패킷 버퍼 최대수 */ 
	// private int dataPacketBufferMaxCntPerMessage;
	
	private int dataPacketBufferMaxCntPerMessage;
	private DataPacketBufferPoolManagerIF dataPacketBufferQueueManager = null;
	
	
	private static final int messageHeaderSize = 4;
	
	private JSONParser jsonParser = new JSONParser();	
	
	// private ClientObjectManager clientMessageController = ClientObjectManager.getInstance();
	// private ServerObjectManager serverMessageController = ServerObjectManager.getInstance();
	
	public DJSONMessageProtocol(int dataPacketBufferMaxCntPerMessage,  
			DataPacketBufferPoolManagerIF dataPacketBufferQueueManager) {
		// this.dataPacketBufferSize = dataPacketBufferQueueManager.getDataPacketBufferSize();
		// this.dataPacketBufferMaxCntPerMessage = dataPacketBufferQueueManager.getDataPacketBufferMaxCntPerMessage();
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		this.jsonSingleItemDecoder = new DJSONSingleItemDecoder();
		this.jsonSingleItemEncoder = new DJSONSingleItemEncoder();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<WrapBuffer> M2S(AbstractMessage messageObj, AbstractMessageEncoder messageEncoder,  Charset charsetOfProject)
			throws NoMoreDataPacketBufferException, BodyFormatException {

		JSONObject jsonWriteObj = new JSONObject();
		jsonWriteObj.put("messageID", messageObj.getMessageID());
		jsonWriteObj.put("mailboxID", messageObj.messageHeaderInfo.mailboxID);
		jsonWriteObj.put("mailID", messageObj.messageHeaderInfo.mailID);
		
		
		
		try {
			messageEncoder.encode(messageObj, jsonSingleItemEncoder, charsetOfProject, jsonWriteObj);
		} catch (BodyFormatException e) {
			throw e;
		} catch (NoMoreDataPacketBufferException e) {
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
		
		// String jsonStr = messageObj.toJSONString();
		
		String jsonStr = jsonWriteObj.toJSONString();
		
		// FIXME!
		// log.info(jsonStr);
		
		CharsetEncoder clientCharsetEncoder = CharsetUtil.createCharsetEncoder(charsetOfProject);
		FreeSizeOutputStream bodyOutputStream = 
				new FreeSizeOutputStream(dataPacketBufferMaxCntPerMessage, clientCharsetEncoder, dataPacketBufferQueueManager);
		
		byte[] jsonStrBytes = jsonStr.getBytes(DJSONHeader.JSON_STRING_CHARSET);
		try {
			bodyOutputStream.putInt(jsonStrBytes.length);
			bodyOutputStream.putBytes(jsonStrBytes);
		} catch(IllegalArgumentException e) {
			String errorMessage = String.format(
					"잘못된 파라미터::jsonStr=[%s]",
					jsonStr);
			log.info(errorMessage, e);
			
			throw new BodyFormatException(errorMessage);
		} catch(BufferOverflowException  e) {
			String errorMessage = String.format(
					"BufferOverflowException::jsonStr=[%s]",
					jsonStr);
			log.info(errorMessage, e);
			
			throw new BodyFormatException(errorMessage);
		} catch(NoMoreDataPacketBufferException e) {
			String errorMessage = String.format(
					"NoMoreDataPacketBufferException::jsonStr=[%s]",
					jsonStr);
			log.warn(errorMessage, e);
			throw new NoMoreDataPacketBufferException(errorMessage);
		} catch(Exception  e) {
			String errorMessage = String.format(
					"unknown error::jsonStr=[%s]",
					jsonStr);
			log.warn(errorMessage, e);
			
			throw new BodyFormatException(errorMessage);
		}
		
		ArrayList<WrapBuffer> messageWrapBufferList = bodyOutputStream.getFlippedWrapBufferList();
		
		return messageWrapBufferList;
	}
	
	@Override
	public SingleItemDecoderIF getSingleItemDecoder() {
		return jsonSingleItemDecoder;
	}
	
	@Override
	public ArrayList<ReceivedLetter> S2MList(Charset charsetOfProject,
			SocketOutputStream socketInputStream) throws HeaderFormatException,
			NoMoreDataPacketBufferException {
		
		CharsetDecoder charsetOfProjectDecoder = CharsetUtil.createCharsetDecoder(charsetOfProject);
		DJSONHeader messageHeader = (DJSONHeader)socketInputStream.getUserDefObject();
		
		ArrayList<ReceivedLetter> receivedLetterList = new ArrayList<ReceivedLetter>();
		
		boolean isMoreMessage = false;
		
		int messageReadWrapBufferListSize = socketInputStream.getDataPacketBufferListSize();
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
		/*int startIndex = -1;
		int startPosition = -1;*/
		
		try {			
			// long inputStramSizeBeforeMessageWork = (lastIndex - startIndex) * dataPacketBufferSize - startPosition + lastPosition;
			// long inputStramSizeBeforeMessageWork = freeSizeInputStream.remaining();
			long inputStramSizeBeforeMessageWork = socketInputStream.position();
			int lastPostionOfWorkBuffer = 0;
			int lastIndexOfWorkBuffer = 0;
			
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
						/*startIndex = freeSizeInputStream.getIndexOfWorkBuffer();
						startPosition = freeSizeInputStream.getPositionOfWorkBuffer();*/
						
						/*startIndex = 0;
						startPosition = 0;*/
					}
					
					DJSONHeader  workMessageHeader = new DJSONHeader();
					try {
						workMessageHeader.lenOfJSONStr = freeSizeInputStream.getInt();
					} catch (SinnoriBufferUnderflowException e) {
						String errorMessage = e.getMessage();
						log.warn(String.format("존슨 문자열 추출을 위한 위치이동 실패::SinnoriBufferUnderflowException::%s", errorMessage), e);
						/**
						 * json 객체를 얻지 못하면 json 객체에 포함된 
						 * 신놀이 메시지 운영정보(메시지 식별자, 메일 박스 식별자, 메일 식별자)를 얻을 수 없다.
						 * 신놀이 메시지 운영정보는 헤더 정보이므로 이를 정상적으로 얻지 못했기때문에 헤더 포맷 에러 처리를 한다.  
						 */
						throw new HeaderFormatException(errorMessage);
					}
					
					
					messageHeader = workMessageHeader;
				}
				
				if (null != messageHeader) {					
					long messageFrameSize = (long)messageHeader.lenOfJSONStr + messageHeaderSize;
					
					if (inputStramSizeBeforeMessageWork >= messageFrameSize) {
						/** 메시지 추출*/
						if (null == freeSizeInputStream) {
							freeSizeInputStream = socketInputStream
									.getFreeSizeInputStream(charsetOfProjectDecoder);
							/*startIndex = freeSizeInputStream.getIndexOfWorkBuffer();
							startPosition = freeSizeInputStream.getPositionOfWorkBuffer();
							long skipBytes = startIndex*lastInputStreamBuffer.capacity()+startPosition+messageHeaderSize;*/
							
							/*startIndex = 0;
							startPosition = 0;*/
							long skipBytes = messageHeaderSize;

							try {
								freeSizeInputStream.skip(skipBytes);
							} catch (IllegalArgumentException e) {
								String errorMessage = e.getMessage();
								log.warn(String.format("존슨 문자열 추출을 위한 위치이동 실패::IllegalArgumentException::%s", errorMessage), e);
								/**
								 * json 객체를 얻지 못하면 json 객체에 포함된 
								 * 신놀이 메시지 운영정보(메시지 식별자, 메일 박스 식별자, 메일 식별자)를 얻을 수 없다.
								 * 신놀이 메시지 운영정보는 헤더 정보이므로 이를 정상적으로 얻지 못했기때문에 헤더 포맷 에러 처리를 한다.  
								 */
								throw new HeaderFormatException(errorMessage);
							} catch (SinnoriBufferUnderflowException e) {
								String errorMessage = e.getMessage();
								log.warn(String.format("존슨 문자열 추출을 위한 위치이동 실패::SinnoriBufferUnderflowException::%s", errorMessage), e);
								/**
								 * json 객체를 얻지 못하면 json 객체에 포함된 
								 * 신놀이 메시지 운영정보(메시지 식별자, 메일 박스 식별자, 메일 식별자)를 얻을 수 없다.
								 * 신놀이 메시지 운영정보는 헤더 정보이므로 이를 정상적으로 얻지 못했기때문에 헤더 포맷 에러 처리를 한다.  
								 */
								throw new HeaderFormatException(errorMessage);
							}
						}
						
						String jsonStr = null;
						try {
							jsonStr = freeSizeInputStream.getFixedLengthString(messageHeader.lenOfJSONStr, DJSONHeader.JSON_STRING_CHARSET.newDecoder());
						} catch (IllegalArgumentException e) {
							String errorMessage = e.getMessage();
							log.warn(String.format("존슨 문자열 추출 실패::IllegalArgumentException::%s", errorMessage), e);
							/**
							 * json 객체를 얻지 못하면 json 객체에 포함된 
							 * 신놀이 메시지 운영정보(메시지 식별자, 메일 박스 식별자, 메일 식별자)를 얻을 수 없다.
							 * 신놀이 메시지 운영정보는 헤더 정보이므로 이를 정상적으로 얻지 못했기때문에 헤더 포맷 에러 처리를 한다.  
							 */
							throw new HeaderFormatException(errorMessage);
						} catch (SinnoriBufferUnderflowException e) {
							String errorMessage = e.getMessage();
							log.warn(String.format("존슨 문자열 추출 실패::SinnoriBufferUnderflowException::%s", errorMessage), e);
							/**
							 * json 객체를 얻지 못하면 json 객체에 포함된 
							 * 신놀이 메시지 운영정보(메시지 식별자, 메일 박스 식별자, 메일 식별자)를 얻을 수 없다.
							 * 신놀이 메시지 운영정보는 헤더 정보이므로 이를 정상적으로 얻지 못했기때문에 헤더 포맷 에러 처리를 한다.  
							 */
							throw new HeaderFormatException(errorMessage);
						} catch (SinnoriCharsetCodingException e) {
							String errorMessage = e.getMessage();
							log.warn(String.format("존슨 문자열 추출 실패::SinnoriCharsetCodingException::%s", errorMessage), e);
							/**
							 * json 객체를 얻지 못하면 json 객체에 포함된 
							 * 신놀이 메시지 운영정보(메시지 식별자, 메일 박스 식별자, 메일 식별자)를 얻을 수 없다.
							 * 신놀이 메시지 운영정보는 헤더 정보이므로 이를 정상적으로 얻지 못했기때문에 헤더 포맷 에러 처리를 한다.  
							 */
							throw new HeaderFormatException(errorMessage);
						}
						
						lastPostionOfWorkBuffer = freeSizeInputStream.getPositionOfWorkBuffer();
						lastIndexOfWorkBuffer = freeSizeInputStream.getIndexOfWorkBuffer();
											
						JSONObject jsonObj = null;
						
						try {
							jsonObj = (JSONObject)jsonParser.parse(jsonStr);
						} catch(ParseException pe){
							log.warn("ParseException", pe);
							throw new HeaderFormatException(String.format("JSON ParseException::%s::%s", pe.toString(), jsonStr));
						}
						
						Object valueObj = null;
						
						valueObj = jsonObj.get("messageID");
						if (null == valueObj) {
							throw new HeaderFormatException(String.format("Bad JSON str[%s], messageID not exit", jsonStr));
						}
						if (! (valueObj instanceof String)) {
							throw new HeaderFormatException(String.format("Bad JSON str[%s], messageID type is not String", jsonStr));
						}
						
						String messageID = (String)valueObj;
						
						valueObj = jsonObj.get("mailboxID");
						if (null == valueObj) {
							throw new HeaderFormatException(String.format("Bad JSON str[%s], mailboxID not exit", jsonStr));
						}
						if (! (valueObj instanceof Long)) {
							throw new HeaderFormatException(String.format("Bad JSON str[%s], mailboxID type is not Long", jsonStr));
						}
						
						Long jsonMailboxID = (Long)valueObj;
						
						
						valueObj = jsonObj.get("mailID");
						if (null == valueObj) {
							throw new HeaderFormatException(String.format("Bad JSON str[%s], mailID not exit", jsonStr));
						}
						if (! (valueObj instanceof Long)) {
							throw new HeaderFormatException(String.format("Bad JSON str[%s], mailID type is not Long", jsonStr));
						}
						
						Long jsonMailID = (Long)valueObj;
						
						if (jsonMailboxID < Integer.MIN_VALUE || jsonMailboxID > Integer.MAX_VALUE) {
							throw new HeaderFormatException(String.format("Bad JSON str[%s], mailboxID type is not Integer", jsonStr));
						}
						
						if (jsonMailID < Integer.MIN_VALUE || jsonMailID > Integer.MAX_VALUE) {
							throw new HeaderFormatException(String.format("Bad JSON str[%s], mailID type is not Integer", jsonStr));
						}
						
						int mailboxID = jsonMailboxID.intValue();
						int mailID = jsonMailID.intValue();
						
						ReceivedLetter receivedLetter = 
								new ReceivedLetter(messageID, 
										mailboxID, mailID, jsonObj);
						
						receivedLetterList.add(receivedLetter);
						
						inputStramSizeBeforeMessageWork = freeSizeInputStream.available();
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
