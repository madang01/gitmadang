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

package kr.pe.sinnori.common.protocol.dhb;

import java.nio.BufferOverflowException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.CharsetEncoderException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriBufferOverflowException;
import kr.pe.sinnori.common.io.BinaryInputStreamIF;
import kr.pe.sinnori.common.io.BinaryOutputStreamIF;
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
 * @author Won Jonghoon
 * 
 */
public class DHBMessageHeader {
	private InternalLogger log = InternalLoggerFactory.getInstance(DHBMessageHeader.class);

	/** messageID : String */
	// public String messageID = null;
	/** mailboxID : unsigned short 2byte */
	// public int mailboxID = -1;
	/** mailID : int 4byte */
	// public int mailID = -1;

	/** bodySize : long 8byte */
	public long bodySize = -1;
	/** bodyMD5 : byte array 16byte */
	public byte bodyMD5Bytes[] = null;
	/** headerMD5 : byte array 16byte */
	public byte headerBodyMD5Bytes[] = null;
	
	public void onlyHeaderBodyPartToOutputStream(BinaryOutputStreamIF headerOutputStream, Charset headerCharset) throws IllegalArgumentException, BufferOverflowException,
			SinnoriBufferOverflowException, NoMoreDataPacketBufferException, CharsetEncoderException {
		if (null == headerOutputStream) {
			throw new IllegalArgumentException("the parameter headerOutputStream is null");
		}
		
		if (null == headerCharset) {
			throw new IllegalArgumentException("the parameter headerCharset is null");
		}

		/*if (null == messageID) {
			String errorMessage = "the var messageID is null";
			// log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (mailboxID < 0) {
			String errorMessage = String.format("the var mailboxID[%d] is less than zero", mailboxID);
			// log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}*/

		if (null == bodyMD5Bytes) {
			String errorMessage = "the var bodyMD5Bytes is null";
			// log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (bodyMD5Bytes.length != CommonStaticFinalVars.MD5_BYTESIZE) {
			String errorMessage = String.format("the var bodyMD5Bytes's length[%d] is not MD5.size[%d]",
					bodyMD5Bytes.length, CommonStaticFinalVars.MD5_BYTESIZE);
			// log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		/*byte[] messageIDBytes = messageID.getBytes(headerCharset);
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
		headerOutputStream.putInt(mailID);*/
		headerOutputStream.putLong(bodySize);
		headerOutputStream.putBytes(bodyMD5Bytes);
	}

	public void toOutputStream(BinaryOutputStreamIF headerOutputStream, 
			Charset headerCharset) throws IllegalArgumentException, BufferOverflowException,
			SinnoriBufferOverflowException, NoMoreDataPacketBufferException, CharsetEncoderException {
		onlyHeaderBodyPartToOutputStream(headerOutputStream, headerCharset);

		if (null == headerBodyMD5Bytes) {
			String errorMessage = "the var headerBodyMD5Bytes is null";
			// log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (headerBodyMD5Bytes.length != CommonStaticFinalVars.MD5_BYTESIZE) {
			String errorMessage = String.format("the var headerBodyMD5Bytes's length[%d] is not MD5.size[%d]",
					headerBodyMD5Bytes.length, CommonStaticFinalVars.MD5_BYTESIZE);
			// log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		headerOutputStream.putBytes(headerBodyMD5Bytes);

	}

	
	public void fromInputStream(BinaryInputStreamIF headerInputStream, 
			CharsetDecoder headerCharsetDecoder) throws HeaderFormatException {
		try {
			/*// this.messageID = headerInputStream.getFixedLengthString(messageIDFixedSize, headerCharsetDecoder).trim();
			this.messageID = new String(headerInputStream.getBytes(messageIDFixedSize), headerCharsetDecoder.charset()).trim();
			
			this.mailboxID = headerInputStream.getUnsignedShort();
			this.mailID = headerInputStream.getInt();*/
			this.bodySize = headerInputStream.getLong();
			this.bodyMD5Bytes = headerInputStream.getBytes(CommonStaticFinalVars.MD5_BYTESIZE);
			this.headerBodyMD5Bytes = headerInputStream.getBytes(CommonStaticFinalVars.MD5_BYTESIZE);
		} catch (Exception e) {
			String errorMessage = new StringBuilder("dhb header parsing error::").append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			throw new HeaderFormatException(errorMessage);
		}
	}

	@Override
	public String toString() {
		StringBuffer headerInfo = new StringBuffer();
		headerInfo.append("DHBMessageHeader={body data size=[");
		headerInfo.append(bodySize);
		headerInfo.append("]");
		if (null != bodyMD5Bytes) {
			headerInfo.append(", body MD5=[");
			headerInfo.append(HexUtil.getHexStringFromByteArray(bodyMD5Bytes));
			headerInfo.append("]");
		}
		if (null != headerBodyMD5Bytes) {
			headerInfo.append(", header MD5=[");
			headerInfo.append(HexUtil.getHexStringFromByteArray(headerBodyMD5Bytes));
			headerInfo.append("]");
		}
		headerInfo.append("}");

		return headerInfo.toString();
	}
}
