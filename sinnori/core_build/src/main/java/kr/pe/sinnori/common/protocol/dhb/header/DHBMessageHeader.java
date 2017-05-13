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

package kr.pe.sinnori.common.protocol.dhb.header;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import kr.pe.sinnori.common.etc.CharsetUtil;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriBufferUnderflowException;
import kr.pe.sinnori.common.exception.SinnoriCharsetCodingException;
import kr.pe.sinnori.common.io.FixedSizeOutputStream;
import kr.pe.sinnori.common.io.SinnoriInputStreamIF;
import kr.pe.sinnori.common.util.HexUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 메시시 헤더 클래스.
 * 
 * <pre>
 * 신놀이 서버와 클라이언트사이에 DHB 방식의 데이터 교환은 DHB 데이터 패킷 단위로 이루어진다.
 * DHB 데이터 패킷은 DHB 데이터 헤더와 DHB 데이터 바디로 구성된다.
 * 
 * 신놀이 메시지 교환은 DHB 데이터 패킷를 기반으로 실행된다. 
 * DHB 데이터 바디안에 메시지 헤더 정보와 메시지 내용을 넣어서 메시지를 교환하게 된다. 
 * 단, 덩치 큰 메시지는 DHB 데이터 패킷의 최대 크기 제한 때문에 보낼 수 없다.
 * 덩치 큰 메시지를 교환하기 위해서는 나누어서 보낸후 조립해야 한다.
 * 이런 목적을 가진 메시지가 바로 래퍼(Wrapper) 메시지 이다.
 * 송신측에서는 래퍼 메시지 그룹에 덩치 큰 메시지를 나누어 담아서 수신측에 보낸후 
 * 수신측에서는 래퍼 메시지 그룹을 조립하여 송신측에서 보내고자 하는 덩치 큰 메시지로 복원한다.
 * 서버/클라이언트 모두 래퍼 메시지 그룹 관리자를 두어서 
 * 래퍼 메시지 그룹을 관리하며 래퍼 메시지 그룹 완성시 덩치 큰 메시지로 복원 시킨다.
 * </pre>
 * 
 * @author Won Jonghoon
 * 
 */
public class DHBMessageHeader {
	private Logger log = LoggerFactory.getLogger(DHBMessageHeader.class);
	
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
	/** bodyMD5 : byte array 16byte */
	public byte bodyMD5[] = null;
	/** headerMD5 : byte array 16byte */
	public byte headerMD5[] = null;
	
	/** 메시지 헤더 정보에서 고정 크기를 갖는 메시지 식별자의 크기, 환경 변수를 통해 지정된다. */
	public int messageIDFixedSize = -1;
	/** 메시지 헤더 크기, 참고) 환경 변수로 지정되는 고정 크기를 갖는 메시지 식별자 크기 값으로 정해지는 크기이다. */
	public int messageHeaderSize = -1;
	
	/**
	 * 생성자
	 * @param messageIDFixedSize 메시지 헤더의 고정 크기를 갖는 메시지 식별자 크기
	 */
	public DHBMessageHeader(int messageIDFixedSize) {
		this.messageIDFixedSize = messageIDFixedSize;
		this.messageHeaderSize = getMessageHeaderSize(messageIDFixedSize); 
		
	}
	
	/**
	 * 지정된 메시지 식별자 크기를 갖는 DHB 헤더 크기를 반환한다.
	 * @param messageIDFixedSize 메시지 식별자 크기
	 * @return 지정된 메시지 식별자 크기를 갖는 THB 헤더 크기
	 */
	public static int getMessageHeaderSize(int messageIDFixedSize) {
		return (messageIDFixedSize + 2 + 4 + 8 + MD5_BYTESIZE*2);
	}
	
	
	/*public static int getBodyMD5Offset(int messageIDFixedSize) {
		return (messageIDFixedSize+ 2 + 4 + 8);
	}*/
	
	/**
	 * <pre>
	 * 지정된 메시지 식별자 크기를 갖는 DHB 헤더의 헤더 MD5 옵셋을 반환한다. 
	 * 참고) 헤더 MD5를 구하기 위해서 필요한 정보, 헤더 MD5를 제외한 헤더 부분의 크기이다.
	 * </pre>
	 *  
	 * @param messageIDFixedSize 메시지 식별자 크기
	 * @return 지정된 메시지 식별자 크기를 갖는 DHB 헤더의 헤더 MD5 옵셋
	 */
	public static int getHeaderMD5Offset(int messageIDFixedSize) {
		return (messageIDFixedSize+ 2 + 4 + 8 + MD5_BYTESIZE);
	}
	
	
	
	/**
	 * 메시지 헤더의 내용을 목적지 버퍼에 저장한다. <br/>
	 * 단, 헤더 MD5는 자동 계산되어 저장된다.<br/>
	 * 참고) 이 메소드 정상 종료후  목적지 버퍼의 현재 읽을 위치는 메시지 헤더 크기이다.
	 * @param dstBuffer 헤더를 저장할 목적지 바이트 버퍼, 반듯이 position=0 그리고 limit=capacity 이어야 한다.
	 * @param streamCharset 문자셋
	 * @param streamCharsetEncoder 문자셋 인코더
	 * @param md5 MD5
	 * @throws IllegalArgumentException 잘못된 파라미터 값이 들어온 경우 던지는 예외
	 */
	public void writeMessageHeader(ByteBuffer dstBuffer, Charset streamCharset, CharsetEncoder streamCharsetEncoder, java.security.MessageDigest md5) throws IllegalArgumentException {
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
		
		
		if (null == bodyMD5) {
			String errorMessage = "메시지 헤더 정보의 바디 MD5가 null 입니다.";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (bodyMD5.length != MD5_BYTESIZE) {
			String errorMessage = String.format("메시지 헤더 정보의 바디 MD5의 크기[%d]가 MD5 바이트 크기[%d]와 다릅니다.", bodyMD5.length, MD5_BYTESIZE);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null != headerMD5) {
			String errorMessage = "메시지 헤더 정보 정보는 자동 계산 되므로 값을 지정할 필요가 없습니다.";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		int positionBeforeWork = dstBuffer.position();
		int limitBeofreWork = dstBuffer.limit();
		
		FixedSizeOutputStream headerOutputStream = new FixedSizeOutputStream(dstBuffer, streamCharset, streamCharsetEncoder);
		// int beforePosition = dstBuffer.position();
		// int beforeLimit = dstBuffer.limit();
		
		try {
			headerOutputStream.putString(messageIDFixedSize, messageID, CharsetUtil.createCharsetEncoder(HEADER_CHARSET));
			headerOutputStream.putUnsignedShort(mailboxID);
			headerOutputStream.putInt(mailID);
			headerOutputStream.putLong(bodySize);
			headerOutputStream.putBytes(bodyMD5);
			// header MD5 구히기 위한 메시지 헤더 쓰기전 위치로 돌리기
			dstBuffer.limit(dstBuffer.position());
			/** 백업한 position 복귀, 즉 헤더 시작 위치 */
			dstBuffer.position(positionBeforeWork);

			// FIXME!
			// log.info(String.format("%s", dstBuffer.toString()));
			//log.info(String.format("%s", HexUtil.byteBufferAvailableToHex(dstBuffer)));
			
			md5.update(dstBuffer);
			headerMD5 = md5.digest();
			
			/** 백업한 limit 복귀 */
			dstBuffer.limit(limitBeofreWork);
			
			headerOutputStream.putBytes(headerMD5);		
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
	 * 헤더 정보를 갖고 있는 입력 스트림으로 부터 DHB 헤더 정보를 읽어 내용을 채운다.
	 * @param headerInputStream 헤더 정보를 갖고 있는 입력 스트림
	 * @throws HeaderFormatException 메시지 식별자 읽을때 문자셋 에러 발생시 던지는 예외
	 */
	public void readMessageHeader(SinnoriInputStreamIF headerInputStream) throws HeaderFormatException {
		try {
			this.messageID = headerInputStream
					.getString( messageIDFixedSize,
							CharsetUtil.createCharsetDecoder(DHBMessageHeader.HEADER_CHARSET)).trim();
			this.mailboxID = headerInputStream
					.getUnsignedShort();
			this.mailID = headerInputStream.getInt();
			this.bodySize = headerInputStream.getLong();
			this.bodyMD5 = headerInputStream
					.getBytes(DHBMessageHeader.MD5_BYTESIZE);
			this.headerMD5 = headerInputStream
					.getBytes(DHBMessageHeader.MD5_BYTESIZE);
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
		headerInfo.append("DHBMessageHeader={messageID=[");
		headerInfo.append(messageID);
		headerInfo.append("], mailboxID=[");
		headerInfo.append(mailboxID);
		headerInfo.append("], mailID=[");
		headerInfo.append(mailID);
		headerInfo.append("], body data size=[");
		headerInfo.append(bodySize);
		headerInfo.append("]");
		if (null != bodyMD5) {
			headerInfo.append(", data MD5=[");
			headerInfo.append(HexUtil.getHexStringFromByteArray(bodyMD5));
			headerInfo.append("]");
		}
		if (null != headerMD5) {
			headerInfo.append(", header MD5=[");
			headerInfo.append(HexUtil.getHexStringFromByteArray(headerMD5));
			headerInfo.append("]");
		}		
		headerInfo.append("}");

		return headerInfo.toString();
	}
}
