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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.WrapBufferUtil;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriBufferUnderflowException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolManagerIF;
import kr.pe.sinnori.common.io.FixedSizeInputStream;
import kr.pe.sinnori.common.io.FreeSizeInputStream;
import kr.pe.sinnori.common.io.FreeSizeOutputStream;
import kr.pe.sinnori.common.io.SocketInputStream;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;
import kr.pe.sinnori.common.protocol.dhb.header.DHBMessageHeader;
import kr.pe.sinnori.common.util.HexUtil;

/**
 * DHB 메시지 교환 프로토콜
 * 
 * @author Won Jonghoon
 * 
 */
public class DHBMessageProtocol implements MessageProtocolIF {
	private Logger log = LoggerFactory.getLogger(DHBMessageProtocol.class);

	/** 메시지 헤더에 사용되는 문자열 메시지 식별자의 크기, 단위 byte */
	private int messageIDFixedSize;
	private int dataPacketBufferMaxCntPerMessage;
	private Charset streamCharset = null;
	private CharsetEncoder streamCharsetEncoder;
	@SuppressWarnings("unused")
	private CharsetDecoder streamCharsetDecoder;
	private DataPacketBufferPoolManagerIF dataPacketBufferPoolManager = null;

	/** 메시지 헤더 크기, 단위 byte */
	private int messageHeaderSize;
	private int headerBodySize;

	private DHBSingleItemDecoder dhbSingleItemDecoder = null;
	private DHBSingleItemEncoder dhbSingleItemEncoder = null;
	
	private final Charset headerCharset = Charset.forName("ISO-8859-1");
	private CharsetEncoder headerCharsetEncoder = null;
	private CharsetDecoder headerCharsetDecoder = null;
	

	public DHBMessageProtocol(int messageIDFixedSize, int dataPacketBufferMaxCntPerMessage,
			CharsetEncoder streamCharsetEncoder, CharsetDecoder streamCharsetDecoder,
			DataPacketBufferPoolManagerIF dataPacketBufferPoolManager) {
		if (messageIDFixedSize <= 0) {
			String errorMessage = String.format("the parameter messageIDFixedSize[%d] is less than or equal to zero",
					messageIDFixedSize);
			throw new IllegalArgumentException(errorMessage);
		}

		if (dataPacketBufferMaxCntPerMessage <= 0) {
			String errorMessage = String.format(
					"the parameter dataPacketBufferMaxCntPerMessage[%d] is less than or equal to zero",
					dataPacketBufferMaxCntPerMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == streamCharsetEncoder) {
			throw new IllegalArgumentException("the parameter streamCharsetEncoder is null");
		}

		if (null == streamCharsetDecoder) {
			throw new IllegalArgumentException("the parameter streamCharsetDecoder is null");
		}

		Charset streamCharsetOfEncoder = streamCharsetEncoder.charset();
		Charset streamCharsetOfDecoder = streamCharsetDecoder.charset();

		if (!streamCharsetOfEncoder.equals(streamCharsetOfDecoder)) {
			String errorMessage = String.format(
					"the parameter streamCharsetEncoder[%s] is not same to the parameter streamCharsetDecoder[%s]",
					streamCharsetOfEncoder.name(), streamCharsetOfDecoder.name());

			throw new IllegalArgumentException(errorMessage);
		}
		
		streamCharset = streamCharsetOfEncoder;

		if (null == dataPacketBufferPoolManager) {

			throw new IllegalArgumentException("the parameter dataPacketBufferPoolManager is null");
		}

		this.messageIDFixedSize = messageIDFixedSize;
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		this.streamCharsetEncoder = streamCharsetEncoder;
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.dataPacketBufferPoolManager = dataPacketBufferPoolManager;

		
		this.headerBodySize = messageIDFixedSize + 2 + 4 + 8 + CommonStaticFinalVars.MD5_BYTESIZE;
		this.messageHeaderSize = headerBodySize + CommonStaticFinalVars.MD5_BYTESIZE;
		
		this.dhbSingleItemDecoder = new DHBSingleItemDecoder(streamCharsetDecoder);
		this.dhbSingleItemEncoder = new DHBSingleItemEncoder(streamCharsetEncoder);
		
		this.headerCharsetEncoder = headerCharset.newEncoder();
		this.headerCharsetEncoder.onMalformedInput(streamCharsetEncoder.malformedInputAction());
		this.headerCharsetEncoder.onUnmappableCharacter(streamCharsetEncoder.unmappableCharacterAction());
		
		
		this.headerCharsetDecoder = headerCharset.newDecoder();
		this.headerCharsetDecoder.onMalformedInput(streamCharsetDecoder.malformedInputAction());
		this.headerCharsetEncoder.onUnmappableCharacter(streamCharsetDecoder.unmappableCharacterAction());
	}

	private void throwExceptionIfBodyChecksumIsInvalid(DHBMessageHeader workingDHBMessageHeader,
			SocketInputStream socketInputStream) throws HeaderFormatException {
		byte[] bodyMD5Bytes = null;
		try {
			bodyMD5Bytes = socketInputStream.getMD5(workingDHBMessageHeader.bodySize, 1024);
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			log.error(errorMessage, e);
			System.exit(1);
		} catch (SinnoriBufferUnderflowException e) {
			String errorMessage = e.getMessage();
			log.error(errorMessage, e);
			System.exit(1);
		}
	
		/** 바디 MD5 와 헤더 정보 바디 MD5 비교 */
		boolean isValidBodyMD5 = java.util.Arrays.equals(bodyMD5Bytes, workingDHBMessageHeader.bodyMD5Bytes);
		if (!isValidBodyMD5) {
			String errorMessage = String.format("different body MD5, header's body md5[%s], body md5[%s]",
					workingDHBMessageHeader.toString(), HexUtil.getHexStringFromByteArray(bodyMD5Bytes));
	
			throw new HeaderFormatException(errorMessage);
		}
	}

	@Override
	public List<WrapBuffer> M2S(AbstractMessage messageObj, AbstractMessageEncoder messageEncoder)
			throws NoMoreDataPacketBufferException, BodyFormatException {
		if (null == messageObj) {
			throw new IllegalArgumentException("the parameter messageObj is null");
		}

		if (null == messageEncoder) {
			throw new IllegalArgumentException("the parameter messageEncoder is null");
		}

		String messageID = messageObj.getMessageID();
		int mailboxID = messageObj.messageHeaderInfo.mailboxID;
		int mailID = messageObj.messageHeaderInfo.mailID;
		long bodySize = -1;
		byte[] bodyMD5Bytes = null;
		byte[] headerBodyMD5Bytes = null;

		//log.info("1");
		
		/** 바디 만들기 */
		FreeSizeOutputStream bodyOutputStream = new FreeSizeOutputStream(dataPacketBufferMaxCntPerMessage,
				streamCharsetEncoder, dataPacketBufferPoolManager);
		
		//log.info("2");

		try {
			messageEncoder.encode(messageObj, dhbSingleItemEncoder, streamCharset, bodyOutputStream);
		} catch (NoMoreDataPacketBufferException e) {
			throw e;
		} catch (BodyFormatException e) {
			throw e;
		} catch (OutOfMemoryError e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = String.format("unknown error::messageObj=[%s]", messageObj.toString());
			log.warn(errorMessage, e);

			throw new BodyFormatException(errorMessage);
		}
		
		//log.info("3");

		bodySize = bodyOutputStream.size();

		List<WrapBuffer> readableWrapBufferListOfBodyOutputStream = bodyOutputStream.getReadableWrapBufferList();
		
		
		java.security.MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		{
			
			for (WrapBuffer readableWrapBufferOfBodyOutputStream : readableWrapBufferListOfBodyOutputStream) {
				ByteBuffer readableByteBufferOfBodyOutputStream = readableWrapBufferOfBodyOutputStream.getByteBuffer();
				
				md5.update(readableByteBufferOfBodyOutputStream);
				
				readableByteBufferOfBodyOutputStream.flip();
			}
			
			bodyMD5Bytes = md5.digest();
		}
		
		//log.info("2. readableWrapBufferListOfBodyOutputStream={}", readableWrapBufferListOfBodyOutputStream.toString());
		
		
		//log.info("4");
		
		//log.info("3. readableWrapBufferListOfBodyOutputStream={}", readableWrapBufferListOfBodyOutputStream.toString());

		FreeSizeOutputStream headerOutputStream = new FreeSizeOutputStream(dataPacketBufferMaxCntPerMessage,
				headerCharsetEncoder, dataPacketBufferPoolManager);
		try {
			headerOutputStream.putFixedLengthString(messageIDFixedSize, messageID);
			headerOutputStream.putUnsignedShort(mailboxID);
			headerOutputStream.putInt(mailID);
			headerOutputStream.putLong(bodySize);
			headerOutputStream.putBytes(bodyMD5Bytes);
		} catch (Exception e) {
			String errorMessage = new StringBuilder("dhb header packaging error::").append(e.getMessage()).toString();
			log.error(errorMessage, e);
			System.exit(1);
		}
		
		//log.info("5");
		
		List<WrapBuffer> wrapBufferListOfHeaderBodyOutputStream = headerOutputStream.getOutputStreamWrapBufferList();
		
		//log.info("4. wrapBufferListOfHeaderBodyOutputStream={}", wrapBufferListOfHeaderBodyOutputStream.toString());
		
		List<WrapBuffer> duplicatedAndReadableWrapBufferListOfHeaderBodyOutputStream 
			= WrapBufferUtil.getDuplicatedAndReadableWrapBufferList(wrapBufferListOfHeaderBodyOutputStream);
		
		md5.reset();
		{
			
			for (WrapBuffer duplicatedAndReadableWrapBufferOfHeaderBodyOutputStream : duplicatedAndReadableWrapBufferListOfHeaderBodyOutputStream) {
				ByteBuffer duplicatedAndReadableByteBufferOfHeaderBodyOutputStream = duplicatedAndReadableWrapBufferOfHeaderBodyOutputStream.getByteBuffer();
				
				md5.update(duplicatedAndReadableByteBufferOfHeaderBodyOutputStream);
			}
			
			headerBodyMD5Bytes = md5.digest();
		}		
		
		//log.info("6");
		
		//log.info("5. wrapBufferListOfHeaderBodyOutputStream={}", wrapBufferListOfHeaderBodyOutputStream.toString());

		try {
			headerOutputStream.putBytes(headerBodyMD5Bytes);
		} catch (Exception e) {
			String errorMessage = new StringBuilder("dhb header packaging error::").append(e.getMessage()).toString();
			log.error(errorMessage, e);
			System.exit(1);
		}
		
		//log.info("7");

		List<WrapBuffer> readableWrapBufferListOfHeaderOutputStream = headerOutputStream.getReadableWrapBufferList();

		List<WrapBuffer> messageReadableOutputStreamWrapBufferList = new ArrayList<WrapBuffer>();
		messageReadableOutputStreamWrapBufferList.addAll(readableWrapBufferListOfHeaderOutputStream);
		messageReadableOutputStreamWrapBufferList.addAll(readableWrapBufferListOfBodyOutputStream);

		//log.info("8");
		
		// log.debug(messageHeader.toString());
		// log.debug(firstWorkBuffer.toString());

		return messageReadableOutputStreamWrapBufferList;
	}

	@Override
	public ArrayList<WrapReadableMiddleObject> S2MList(SocketOutputStream socketOutputStream)
			throws HeaderFormatException, NoMoreDataPacketBufferException {
		if (null == socketOutputStream) {
			throw new IllegalArgumentException("the parameter socketOutputStream is null");
		}

		DHBMessageHeader workingDHBMessageHeader = (DHBMessageHeader) socketOutputStream.getUserDefObject();

		ArrayList<WrapReadableMiddleObject> wrapReadableMiddleObjectList = new ArrayList<WrapReadableMiddleObject>();
		boolean isMoreMessage = false;
		long socketOutputStreamSize = socketOutputStream.size();

		try {
			do {
				if (null == workingDHBMessageHeader && socketOutputStreamSize >= messageHeaderSize) {
					SocketInputStream socketInputStream = socketOutputStream.createNewSocketInputStream();
					byte[] headerBodyBytes = null;
					byte[] headerBodyMD5Bytes = null;
					try {
						headerBodyBytes = socketInputStream.getBytes(headerBodySize);
						headerBodyMD5Bytes = socketInputStream.getBytes(CommonStaticFinalVars.MD5_BYTESIZE);
					} catch (Exception e) {
						log.error("unknown error::" + e.getMessage());
						System.exit(1);
					}

					byte[] actualHeaderBodyMD5Bytes = null;
					{
						java.security.MessageDigest md5 = null;
						try {
							md5 = MessageDigest.getInstance("MD5");
						} catch (NoSuchAlgorithmException e) {
							log.error("unknown error::" + e.getMessage());
							System.exit(1);
						}
						md5.update(headerBodyBytes);
						actualHeaderBodyMD5Bytes = md5.digest();
					}

					boolean isValidHeaderBodyMD5 = java.util.Arrays.equals(actualHeaderBodyMD5Bytes,
							headerBodyMD5Bytes);

					if (!isValidHeaderBodyMD5) {
						String errorMessage = String.format(
								"dhb header::different header MD5, header body[%s]+header md5[%s], actual header body MD5[%s]",
								HexUtil.getHexStringFromByteArray(headerBodyBytes),
								HexUtil.getHexStringFromByteArray(headerBodyMD5Bytes),
								HexUtil.getHexStringFromByteArray(actualHeaderBodyMD5Bytes));

						throw new HeaderFormatException(errorMessage);
					}

					ByteBuffer headerBodyByteBuffer = ByteBuffer.wrap(headerBodyBytes);
					FixedSizeInputStream headerBodyInputStream = new FixedSizeInputStream(headerBodyByteBuffer,
							headerCharsetDecoder);					

					DHBMessageHeader dhbMessageHeader = new DHBMessageHeader();
					try {
						dhbMessageHeader.messageID = headerBodyInputStream
								.getFixedLengthString(messageIDFixedSize, headerCharsetDecoder).trim();
						dhbMessageHeader.mailboxID = headerBodyInputStream.getUnsignedShort();
						dhbMessageHeader.mailID = headerBodyInputStream.getInt();
						dhbMessageHeader.bodySize = headerBodyInputStream.getLong();
						dhbMessageHeader.bodyMD5Bytes = headerBodyInputStream
								.getBytes(CommonStaticFinalVars.MD5_BYTESIZE);
						dhbMessageHeader.headerBodyMD5Bytes = headerBodyMD5Bytes;
					} catch (Exception e) {
						String errorMessage = new StringBuilder("dhb header parsing error::").append(e.getMessage())
								.toString();
						log.warn(errorMessage, e);
						throw new HeaderFormatException(errorMessage);
					}
					
					/*socketInputStream = socketOutputStream.createNewSocketInputStream();
					
					DHBMessageHeader dhbMessageHeader = new DHBMessageHeader(messageIDFixedSize);
					try {
						dhbMessageHeader.messageID = socketInputStream
								.getFixedLengthString(messageIDFixedSize, headerCharsetDecoder).trim();
						dhbMessageHeader.mailboxID = socketInputStream.getUnsignedShort();
						dhbMessageHeader.mailID = socketInputStream.getInt();
						dhbMessageHeader.bodySize = socketInputStream.getLong();
						dhbMessageHeader.bodyMD5Bytes = socketInputStream
								.getBytes(CommonStaticFinalVars.MD5_BYTESIZE);
						dhbMessageHeader.headerBodyMD5Bytes = headerBodyMD5Bytes;
					} catch (Exception e) {
						String errorMessage = new StringBuilder("dhb header parsing error::").append(e.getMessage())
								.toString();
						log.warn(errorMessage, e);
						throw new HeaderFormatException(errorMessage);
					}*/

					if (dhbMessageHeader.bodySize < 0) {
						String errorMessage = String.format("dhb header::body size less than zero %s",
								dhbMessageHeader.toString());
						throw new HeaderFormatException(errorMessage);
					}

					workingDHBMessageHeader = dhbMessageHeader;
				}

				if (null != workingDHBMessageHeader) {
					/*
					 * log.info(String.format("3. inputStramSizeBeforeMessageWork[%d]",
					 * inputStramSizeBeforeMessageWork));
					 */

					long messageFrameSize = workingDHBMessageHeader.bodySize + messageHeaderSize;

					if (socketOutputStreamSize >= messageFrameSize) {						
						SocketInputStream socketInputStream = socketOutputStream.createNewSocketInputStream();
						try {
							socketInputStream.skip(messageHeaderSize);
						} catch (Exception e) {
							String errorMessage = e.getMessage();
							log.error(errorMessage, e);
							System.exit(1);
						}
						throwExceptionIfBodyChecksumIsInvalid(workingDHBMessageHeader, socketInputStream);

						FreeSizeInputStream messageInputStream = socketOutputStream
								.cutMessageInputStreamFromStartingPosition(messageFrameSize);
						
						try {
							messageInputStream.skip(messageHeaderSize);
						} catch (Exception e) {
							String errorMessage = e.getMessage();
							log.error(errorMessage, e);
							System.exit(1);
						}

						WrapReadableMiddleObject wrapReadableMiddleObject= new WrapReadableMiddleObject(workingDHBMessageHeader.messageID,
								workingDHBMessageHeader.mailboxID, workingDHBMessageHeader.mailID, messageInputStream);

						wrapReadableMiddleObjectList.add(wrapReadableMiddleObject);

						workingDHBMessageHeader = null;
						socketOutputStreamSize = socketOutputStream.size();
						if (socketOutputStreamSize > messageHeaderSize) {
							isMoreMessage = true;
						}
					}
				}
			} while (isMoreMessage);
		} finally {
			socketOutputStream.setUserDefObject(workingDHBMessageHeader);
		}

		return wrapReadableMiddleObjectList;
	}

	@Override
	public int getMessageHeaderSize() {
		return messageHeaderSize;
	}

	@Override
	public SingleItemDecoderIF getSingleItemDecoder() {
		return dhbSingleItemDecoder;
	}
}
