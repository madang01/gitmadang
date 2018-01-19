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

package kr.pe.sinnori.common.protocol.thb.header;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.regex.Pattern;

import kr.pe.sinnori.common.etc.CharsetUtil;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriBufferOverflowException;
import kr.pe.sinnori.common.exception.SinnoriBufferUnderflowException;
import kr.pe.sinnori.common.exception.SinnoriCharsetCodingException;
import kr.pe.sinnori.common.io.FixedSizeOutputStream;
import kr.pe.sinnori.common.io.BinaryInputStreamIF;
import kr.pe.sinnori.common.protocol.dhb.header.DHBMessageHeader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Won Jonghoon
 *
 */
public class THBMessageHeader {
	private Logger log = LoggerFactory.getLogger(THBMessageHeader.class);
	
	public static final int MD5_BYTESIZE = 16;
	public static final Charset HEADER_CHARSET = Charset.forName("ISO-8859-1");
	public static final int MESSAGE_HEADER_BYTE_SIZE_WITHOUT_MESSAGEID = 6;
	
	/** messageID : String */
	public String messageID = null;
	/** mailboxID : unsigned short 2byte */
	public int mailboxID = -1;
	/** mailID : int 4byte */
	public int mailID= -1;
	
	/** bodySize : long 8byte */
	public long bodySize= -1;
	
	/** 메시지 헤더 정보에서 고정 크기를 갖는 메시지 식별자의 크기, 환경 변수를 통해 지정된다. */
	public int messageIDFixedSize = -1;
	/** 메시지 헤더 크기, 참고) 환경 변수로 지정되는 고정 크기를 갖는 메시지 식별자 크기 값으로 정해지는 크기이다. */
	public int messageHeaderSize = -1;
	
	/**
	 * 생성자
	 * @param messageIDFixedSize 메시지 헤더의 고정 크기를 갖는 메시지 식별자 크기
	 */
	public THBMessageHeader(int messageIDFixedSize) {
		this.messageIDFixedSize = messageIDFixedSize;
		this.messageHeaderSize = getMessageHeaderSize(messageIDFixedSize); 
	}
	
	/**
	 * 지정된 메시지 식별자 크기를 갖는 THB 헤더 크기를 반환한다.
	 * @param messageIDFixedSize 메시지 식별자 크기
	 * @return 지정된 메시지 식별자 크기를 갖는 THB 헤더 크기
	 */
	public static int getMessageHeaderSize(int messageIDFixedSize) {
		return (messageIDFixedSize+ 2 + 4 + 8);
	}
	/**
	 * 입력 받은 메세지 식별자의 유효성을 판별해 준다. 단 크기에 대해서는 검사하지 않는다.
	 * 
	 * @param messageID
	 *            메세지 식별자
	 * @return 입력 받은 "메세지 식별자"의 유효성 여부
	 */
	public static boolean IsValidMessageID(String messageID) {
		// 첫자는 영문으로 시작하며 이후 문자는 영문과 숫자로 구성되는 문자열임을 검사한다.
		// 특수 문자 제거를 위해서임
		Pattern p = Pattern.compile("[[a-zA-Z][a-zA-Z0-9]]+");
		boolean isValid = p.matcher(messageID).matches();
		return isValid;
	}
	
	/**
	 * 메시지 헤더의 내용을 목적지 버퍼에 저장한다. <br/>
	 * 단, 헤더 MD5는 자동 계산되어 저장된다.<br/>
	 * 참고) 이 메소드 정상 종료후  목적지 버퍼의 현재 읽을 위치는 메시지 헤더 크기이다.
	 * @param dstBuffer 헤더를 저장할 목적지 바이트 버퍼, 반듯이 position=0 그리고 limit=capacity 이어야 한다.
	 * @param streamCharset 문자셋
	 * @param streamCharsetEncoder 문자셋 인코더
	 * @throws IllegalArgumentException 잘못된 파라미터 값이 들어온 경우 던지는 예외
	 */
	public void writeMessageHeader(ByteBuffer dstBuffer, Charset streamCharset, CharsetEncoder streamCharsetEncoder) throws IllegalArgumentException {
		if (dstBuffer.remaining() < messageHeaderSize) {
			String errorMessage = String.format("파라미터 목적지 버퍼의 크기[%d]가 메시지 헤더 크기[%d] 보다 작습니다.");
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == messageID) {
			String errorMessage = "메시지 헤더 정보의 메시지 식별자 값이 null 입니다.";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (mailboxID < 0) {
			String errorMessage = String.format("메시지 헤더 정보의 메일함[%d]은 unsinged short 타입으로 0보다 작은 수를 지정할 수 없습니다.", mailboxID);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		FixedSizeOutputStream headerOutputStream = new FixedSizeOutputStream(dstBuffer, streamCharsetEncoder);
		
		try {
			headerOutputStream.putFixedLengthString(messageIDFixedSize, messageID, CharsetUtil.createCharsetEncoder(HEADER_CHARSET));
			headerOutputStream.putUnsignedShort(mailboxID);
			headerOutputStream.putInt(mailID);
			headerOutputStream.putLong(bodySize);
		} catch (SinnoriBufferOverflowException e) {
			log.error("SinnoriBufferOverflowException", e);
			System.exit(1);
		} catch (BufferOverflowException e) {
			log.error("BufferOverflowException", e);
			System.exit(1);
		} catch (IllegalArgumentException e) {
			log.error("IllegalArgumentException", e);
			System.exit(1);
		} catch (NoMoreDataPacketBufferException e) {
			/** 고정 크기 출력 스트림은 NoMoreDataPacketBufferException 를 발생시키니 않는다. */
			log.error("NoMoreDataPacketBufferException", e);
			System.exit(1);
		}
	}
	
	/**
	 * 헤더 정보를 갖고 있는 입력 스트림으로 부터 THB 헤더 정보를 읽어 내용을 채운다.
	 * @param headerInputStream 헤더 정보를 갖고 있는 입력 스트림
	 * @throws HeaderFormatException 메시지 식별자 읽을때 문자셋 에러 발생시 던지는 예외
	 */
	public void readMessageHeader(BinaryInputStreamIF headerInputStream) throws HeaderFormatException {
		
		try {
			this.messageID = headerInputStream
					.getFixedLengthString( messageIDFixedSize,
							CharsetUtil.createCharsetDecoder(DHBMessageHeader.HEADER_CHARSET)).trim();
			this.mailboxID = headerInputStream
					.getUnsignedShort();
			this.mailID = headerInputStream.getInt();
			this.bodySize = headerInputStream.getLong();
		} catch (IllegalArgumentException e) {
			log.error("IllegalArgumentException", e);
			System.exit(1);
		} catch (SinnoriBufferUnderflowException e) {
			log.error("SinnoriBufferUnderflowException", e);
			System.exit(1);
		} catch (BufferUnderflowException e) {
			log.error("BufferUnderflowException", e);
			System.exit(1);
		} catch (SinnoriCharsetCodingException e) {
			String errorMessage = e.getMessage();
			log.warn(errorMessage, e);
			throw new HeaderFormatException(errorMessage);
		}
	}
	
	
	

	@Override
	public String toString() {
		StringBuffer headerInfo = new StringBuffer();
		headerInfo.append("THBMessageHeader={messageID=[");
		headerInfo.append(messageID);
		headerInfo.append("], mailboxID=[");
		headerInfo.append(mailboxID);
		headerInfo.append("], mailID=[");
		headerInfo.append(mailID);
		headerInfo.append("], body data size=[");
		headerInfo.append(bodySize);
		headerInfo.append("]");		
		headerInfo.append("}");

		return headerInfo.toString();
	}
}
