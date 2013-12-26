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

package kr.pe.sinnori.common.io.dhb.header;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;
import java.util.regex.Pattern;

import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriCharsetCodingException;
import kr.pe.sinnori.common.io.FixedSizeInputStream;
import kr.pe.sinnori.common.io.FixedSizeOutputStream;
import kr.pe.sinnori.common.lib.CharsetUtil;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.util.HexUtil;

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
 * @author Jonghoon Won
 * 
 */
public class DHBMessageHeader implements CommonRootIF {
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
	
	/** 메시지 헤더의 내용을 버퍼를 통해 읽을 경우 헤어 부분만 복사한 바이트 배열 */
	private byte messageHeaderBytes[] = null;
	
	/**
	 * 생성자
	 * @param messageIDFixedSize 메시지 헤더의 고정 크기를 갖는 메시지 식별자 크기
	 */
	public DHBMessageHeader(int messageIDFixedSize) {
		this.messageIDFixedSize = messageIDFixedSize;
		this.messageHeaderSize = getMessageHeaderSize(messageIDFixedSize); 
	}
	
	public static int getMessageHeaderSize(int messageIDFixedSize) {
		return (messageIDFixedSize+ 2 + 4 + 8 + MD5_BYTESIZE*2);
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
	 * 소스 버퍼의 내용을 읽어 메시지 헤더의 내용을 채운다. 참고) 이 메소드 정상 종료후 소스 버퍼의 현재 읽을 위치는 메시지 헤더 크기이다.
	 * @param srcBuffer 메시지 헤더의 내용을 가지고 있는 소스 버퍼, 반듯이 position=0 그리고 limit=capacity 이어야 한다.
	 * @param md5 MD5
	 * @param streamCharsetDecoder 문자셋 디코더
	 * @throws HeaderFormatException 메시지 헤더의 내용을 소스 버퍼로 부터 읽을때 에러 발생시 던지는 예외
	 */
	public void readMessageHeader(ByteBuffer srcBuffer, java.security.MessageDigest md5, CharsetDecoder streamCharsetDecoder) throws HeaderFormatException {
		if (srcBuffer.remaining() < messageHeaderSize) {
			String errorMessage = String.format("파라미터 소스 버퍼의 크기[%d]가 메시지 헤더 크기[%d] 보다 작습니다.", srcBuffer.remaining(), messageHeaderSize);
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null != messageHeaderBytes) {
			String errorMessage = "메시지 헤더 읽기 메소드 중복 호출되었습니다.";
			log.warn(errorMessage);
		}
		
		int beforePosition = srcBuffer.position();
		
		if (0 != beforePosition) {
			/** 다음 메시지를 읽을때 이미 읽어 들인 메시지를 소거후 읽기를 강제하기 위한 방어 로직 */
			String errorMessage = "파라미터 소스 버퍼의 읽을 위치가 0이 아닙니다. 헤더 위치는 소스 버퍼 처음 즉 0에 위치해야 합니다.";
			log.warn(errorMessage);
			throw new HeaderFormatException(errorMessage);
		}
		
		FixedSizeInputStream headerInputStream = new FixedSizeInputStream(
				srcBuffer, streamCharsetDecoder);
		try {
			this.messageID = headerInputStream
					.getString(
							messageIDFixedSize,
							CharsetUtil
									.createCharsetDecoder(HEADER_CHARSET)).trim();
			
			if (null == this.messageID) {
				/** 메시지 식별자 값이 null 이 되는것을 막기 위한 방어 코드 */
				String errorMessage = "메시지 헤더에서 읽은 메시지 식별자 값이 null 입니다.";
				throw new HeaderFormatException(errorMessage);
			}
			
			/** 메시지명 유효성 검사 */
			if (!IsValidMessageID(messageID)) {
				String errorMessage = String.format("메시지 헤더에서 읽은 메시지 식별자 값[%s]이 유효하지 않습니다.", messageID);
				throw new HeaderFormatException(errorMessage);
			}
			
			this.mailboxID = headerInputStream
					.getUnsignedShort();

			this.mailID = headerInputStream
					.getInt();
			
			this.bodySize = headerInputStream.getLong();
			
			if (this.bodySize < 0) {
				// header format exception
				String errorMessage = String.format("바디 크기[%d]가 0 보다 작습니다.",
						this.bodySize);
				throw new HeaderFormatException(errorMessage);
			}

			this.bodyMD5 = new byte[MD5_BYTESIZE];			
			headerInputStream.getBytes(this.bodyMD5);
			
			srcBuffer.mark();
			
			this.headerMD5 = new byte[MD5_BYTESIZE];
			headerInputStream.getBytes(this.headerMD5);
			
			
			srcBuffer.reset();
			srcBuffer.flip();
			
			
			md5.update(srcBuffer);
			byte resultHeaderMD5[] = md5.digest();
			

			boolean isValidHeaderMD5 = java.util.Arrays.equals(
					resultHeaderMD5, this.headerMD5);

			if (!isValidHeaderMD5) {
				String errorMessage = String.format(
						"fail to check header MD5, header[%s]",
						this.toString());

				throw new HeaderFormatException(errorMessage);
			}
			
			messageHeaderBytes = new byte[messageHeaderSize];
			srcBuffer.clear();
			srcBuffer.get(messageHeaderBytes);
		} catch (BufferUnderflowException e) {
			log.fatal("BufferUnderflowException", e);
			System.exit(1);
		} catch (IllegalArgumentException e) {
			log.fatal("IllegalArgumentException", e);
			System.exit(1);
		} catch (SinnoriCharsetCodingException e) {
			String errorMessage = String.format("메시지 헤더에서 메시지 식별자 읽는 도중 CharsetCodingException 발생, %s", e.getMessage());
			// log.warn(errorMessage, e);
			throw new HeaderFormatException(errorMessage);
		}
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

			md5.update(dstBuffer);
			headerMD5 = md5.digest();
			
			/** 백업한 limit 복귀 */
			dstBuffer.limit(limitBeofreWork);
			
			headerOutputStream.putBytes(headerMD5);
			
		} catch (BufferOverflowException e) {
			log.fatal("BufferOverflowException", e);
			System.exit(1);
		} catch (IllegalArgumentException e) {
			log.fatal("IllegalArgumentException", e);
			System.exit(1);
		} catch (NoMoreDataPacketBufferException e) {
			/** 고정 크기 출력 스트림은 NoMoreDataPacketBufferException 를 발생시키니 않는다. */
			log.fatal("NoMoreDataPacketBufferException", e);
			System.exit(1);
		}
	}
	
	public boolean compareMessageHeader(byte srcMessageHeaderBytes[], CharsetDecoder streamCharsetDecoder) {
		if (null == messageHeaderBytes) {
			String errorMessage = "메시지 헤더 읽기 메소드를 먼저 호출하시기 바랍니다.";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == srcMessageHeaderBytes) {
			String errorMessage = "파라미터 소스 버퍼가 null 입니다.";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (srcMessageHeaderBytes.length != messageHeaderSize) {
			String errorMessage = String.format("파라미터 소스 버퍼의 크기[%d]가 메시지 헤더 크기[%d]와 다릅니다.", srcMessageHeaderBytes.length, messageHeaderSize);
			throw new IllegalArgumentException(errorMessage);
		}
		
		return Arrays.equals(srcMessageHeaderBytes, messageHeaderBytes);
		
		/*
		
		int beforePosition = srcBuffer.position();
		// int beforeLimit = srcBuffer.limit();
		
		FixedSizeInputStream headerInputStream = new FixedSizeInputStream(
				srcBuffer, streamCharsetDecoder);
		
		String srcMessageID = null;
		int srcMailboxID = -1;
		int srcMailID = -1;
		long srcBodySize = -1;
		byte srcBodyMD5[] = new byte[MD5_BYTESIZE];
		byte srcHeaderMD5[] = new byte[MD5_BYTESIZE];
		
		try {
			srcMessageID = headerInputStream
					.getString(
							messageIDFixedSize,
							CharsetUtil
									.createCharsetDecoder(HEADER_CHARSET)).trim();
		} catch (BufferUnderflowException e) {
			log.fatal("BufferUnderflowException", e);
			System.exit(1);
		} catch (IllegalArgumentException e) {
			log.fatal("IllegalArgumentException", e);
			System.exit(1);
		} catch(SinnoriCharsetCodingException e) {
			srcBuffer.position(beforePosition);
			
			String errorMessage = "SinnoriCharsetCodingException";
			log.warn(errorMessage, e);
			
			return false;
		}

		srcMailboxID = headerInputStream
				.getUnsignedShort();
		srcMailID = headerInputStream
				.getInt();
		
		srcBodySize = headerInputStream.getLong();
		
		if (this.bodySize < 0) {
			srcBuffer.position(beforePosition);
			
			// header format exception
			String errorMessage = String.format("바디 크기[%d]가 0 보다 작습니다.",
					this.bodySize);
			
			log.warn(errorMessage);
			
			return false;
		}
		
		headerInputStream.getBytes(srcBodyMD5);
		headerInputStream.getBytes(srcHeaderMD5);

		if (!srcMessageID.equals(this.messageID)) return false;
		
		if (srcMailboxID != this.mailboxID) return false;
		
		if (srcMailID != this.mailID) return false;
		
		if (srcBodySize != this.bodySize) return false;
		
		if (!Arrays.equals(srcBodyMD5, this.bodyMD5)) return false;
		
		if (!Arrays.equals(srcHeaderMD5, this.headerMD5)) return false;
		
		return true;
		*/
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
			headerInfo.append(HexUtil.byteArrayAllToHex(bodyMD5));
			headerInfo.append("]");
		}
		if (null != headerMD5) {
			headerInfo.append(", header MD5=[");
			headerInfo.append(HexUtil.byteArrayAllToHex(headerMD5));
			headerInfo.append("]");
		}		
		headerInfo.append("}");

		return headerInfo.toString();
	}
}
