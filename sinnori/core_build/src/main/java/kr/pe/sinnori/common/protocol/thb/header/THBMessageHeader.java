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
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.CharsetEncoderException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriBufferOverflowException;
import kr.pe.sinnori.common.io.BinaryInputStreamIF;
import kr.pe.sinnori.common.io.BinaryOutputStreamIF;

/**
 * @author Won Jonghoon
 *
 */
public class THBMessageHeader {
	private Logger log = LoggerFactory.getLogger(THBMessageHeader.class);
	
	// public static final Charset HEADER_CHARSET = Charset.forName("ISO-8859-1");
	public static final int MESSAGE_HEADER_BYTE_SIZE_WITHOUT_MESSAGEID = 6;
	
	/** messageID : String */
	public String messageID = null;
	/** mailboxID : unsigned short 2byte */
	public int mailboxID = -1;
	/** mailID : int 4byte */
	public int mailID= -1;	
	/** bodySize : long 8byte */
	public long bodySize= -1;
	
	
	
	
	/**
	 * 메시지 헤더의 내용을 목적지 버퍼에 저장한다. <br/>
	 * 단, 헤더 MD5는 자동 계산되어 저장된다.<br/>
	 * 참고) 이 메소드 정상 종료후  목적지 버퍼의 현재 읽을 위치는 메시지 헤더 크기이다.
	 * @param dstBuffer 헤더를 저장할 목적지 바이트 버퍼, 반듯이 position=0 그리고 limit=capacity 이어야 한다.
	 * @param streamCharset 문자셋
	 * @param headerCharsetEncoder 문자셋 인코더
	 * @throws IllegalArgumentException 잘못된 파라미터 값이 들어온 경우 던지는 예외
	 * @throws CharsetEncoderException 
	 * @throws NoMoreDataPacketBufferException 
	 * @throws SinnoriBufferOverflowException 
	 * @throws BufferOverflowException 
	 */
	public void toOutputStream(BinaryOutputStreamIF headerOutputStream, int messageIDFixedSize, Charset headerCharset) throws IllegalArgumentException, CharsetEncoderException, BufferOverflowException, SinnoriBufferOverflowException, NoMoreDataPacketBufferException {
		if (null == headerOutputStream) {
			throw new IllegalArgumentException("the parameter headerOutputStream is null");
		}
		
		if (messageIDFixedSize <= 0) {
			String errorMessage = String.format("the parameter messageIDFixedSize[%d] is less than or equal to zero", messageIDFixedSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == headerCharset) {
			throw new IllegalArgumentException("the parameter headerCharset is null");
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
		
		byte[] messageIDBytes = messageID.getBytes(headerCharset);
		if (messageIDBytes.length > messageIDFixedSize) {
			String errorMessage= String.format("the var messageID[%s]'s charset bytes size[%d] is greater than the parameter messageIDFixedSize[%s]", 
					messageID, messageIDBytes.length, messageIDFixedSize);
			throw new IllegalArgumentException(errorMessage);
		}
		ByteBuffer messageIDWrapBuffer = ByteBuffer.wrap(messageIDBytes);
		
		for (int i=0; i < messageIDFixedSize; i++) {
			if (messageIDWrapBuffer.hasRemaining()) {
				headerOutputStream.putByte(messageIDWrapBuffer.get());
			} else {
				headerOutputStream.putByte(CommonStaticFinalVars.ZERO_BYTE);
			}
		}
		
		headerOutputStream.putUnsignedShort(mailboxID);
		headerOutputStream.putInt(mailID);
		headerOutputStream.putLong(bodySize);
		
	}
	
	/**
	 * 헤더 정보를 갖고 있는 입력 스트림으로 부터 THB 헤더 정보를 읽어 내용을 채운다.
	 * @param headerInputStream 헤더 정보를 갖고 있는 입력 스트림
	 * @throws HeaderFormatException 메시지 식별자 읽을때 문자셋 에러 발생시 던지는 예외
	 */
	public void fromInputStream(BinaryInputStreamIF headerInputStream, int messageIDFixedSize, CharsetDecoder headerCharsetDecoder) throws HeaderFormatException {		
		try {
			this.messageID = new String(headerInputStream.getBytes(messageIDFixedSize), headerCharsetDecoder.charset()).trim();
			this.mailboxID = headerInputStream
					.getUnsignedShort();
			this.mailID = headerInputStream.getInt();
			this.bodySize = headerInputStream.getLong();
		} catch (Exception e) {
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
