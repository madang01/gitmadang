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
import java.nio.ByteOrder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
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
import kr.pe.sinnori.common.protocol.ReceivedLetter;
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
	private CharsetEncoder streamCharsetEncoder;
	@SuppressWarnings("unused")
	private CharsetDecoder streamCharsetDecoder;
	private DataPacketBufferPoolManagerIF dataPacketBufferPoolManager = null;
	
	/** 메시지 헤더 크기, 단위 byte */
	private int messageHeaderSize;	
	private int headerBodySize;

	private ByteOrder streamByteOrder = null;
	
	private DHBSingleItemDecoder dhbSingleItemDecoder = new DHBSingleItemDecoder();;
	private DHBSingleItemEncoder dhbSingleItemEncoder = new DHBSingleItemEncoder();;
	
	
	public DHBMessageProtocol(int messageIDFixedSize,
			int dataPacketBufferMaxCntPerMessage,
			CharsetEncoder streamCharsetEncoder,
			CharsetDecoder streamCharsetDecoder,
			DataPacketBufferPoolManagerIF dataPacketBufferPoolManager) {

		this.messageIDFixedSize = messageIDFixedSize;
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		this.streamCharsetEncoder = streamCharsetEncoder;
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.dataPacketBufferPoolManager = dataPacketBufferPoolManager;
		
		
		this.messageHeaderSize = DHBMessageHeader
				.getMessageHeaderSize(messageIDFixedSize);
		this.headerBodySize = DHBMessageHeader
				.getHeaderBodySize(messageIDFixedSize);		
		this.streamByteOrder = dataPacketBufferPoolManager.getByteOrder();
	}
	
	
	@Override
	public List<WrapBuffer> M2S(AbstractMessage messageObj, AbstractMessageEncoder messageEncoder) 
			throws NoMoreDataPacketBufferException, BodyFormatException {
		
		/** 바디 만들기 */
		FreeSizeOutputStream messageOutputStream = new FreeSizeOutputStream(dataPacketBufferMaxCntPerMessage, streamCharsetEncoder, dataPacketBufferPoolManager);
		
		try {
			messageOutputStream.skip(messageHeaderSize);
		} catch (Exception e) {
			String errorMessage = String.format(
					"unknown error::messageObj=[%s]",
					messageObj.toString());
			log.warn(errorMessage, e);
			
			throw new BodyFormatException(errorMessage);
		}
		
		try {
			messageEncoder.encode(messageObj, dhbSingleItemEncoder, streamCharsetEncoder.charset(), messageOutputStream);
		} catch (NoMoreDataPacketBufferException e) {
			throw e;
		} catch (BodyFormatException e) {
			throw e;
		} catch (OutOfMemoryError e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = String.format(
					"unknown error::messageObj=[%s]",
					messageObj.toString());
			log.warn(errorMessage, e);
			
			throw new BodyFormatException(errorMessage);
		}
		// messageObj.M2O(bodyOutputStream, dhbSingleItem2Stream);

		/** 데이터 헤더 만들기 */
		DHBMessageHeader messageHeader = new DHBMessageHeader(
				messageIDFixedSize);
		messageHeader.messageID = messageObj.getMessageID();
		messageHeader.mailboxID = messageObj.messageHeaderInfo.mailboxID;
		messageHeader.mailID = messageObj.messageHeaderInfo.mailID;
		messageHeader.bodySize = messageOutputStream.size() - messageHeaderSize;

		/** 바디 MD5 */
		List<WrapBuffer> messageReadableOutputStreamWrapBufferList = messageOutputStream
				.getReadableWrapBufferList();		
		
		int bufferListSize = messageReadableOutputStreamWrapBufferList.size();

		ByteBuffer firstWorkBuffer = messageReadableOutputStreamWrapBufferList.get(0)
				.getByteBuffer();
		// firstWorkBuffer.flip();
		ByteBuffer firstDupBuffer = firstWorkBuffer.duplicate();
		firstDupBuffer.order(streamByteOrder);
		firstDupBuffer.position(messageHeaderSize);
		
		{
			java.security.MessageDigest md5 = null;
			try {
				md5 = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				log.error("failed to get a MD5 instance", e);
				System.exit(1);
			}
			md5.update(firstDupBuffer);

			for (int i = 1; i < bufferListSize; i++) {
				ByteBuffer workBuffer = messageReadableOutputStreamWrapBufferList.get(i)
						.getByteBuffer();
				workBuffer.flip();
				ByteBuffer dupBuffer = workBuffer.duplicate();
				dupBuffer.order(streamByteOrder);
				md5.update(dupBuffer);
			}

			messageHeader.bodyMD5Bytes = md5.digest();
		}
		

		firstDupBuffer.position(0);
		messageHeader.toBuffer(firstDupBuffer, streamCharsetEncoder);
		firstDupBuffer.position(0);

		// log.debug(messageHeader.toString());
		// log.debug(firstWorkBuffer.toString());

		return messageReadableOutputStreamWrapBufferList;
	}

	@Override
	public SingleItemDecoderIF getSingleItemDecoder() {
		return dhbSingleItemDecoder;
	}

	
	@Override
	public ArrayList<ReceivedLetter> S2MList(SocketOutputStream socketOutputStream) 
			throws HeaderFormatException, NoMoreDataPacketBufferException {
		DHBMessageHeader messageHeader = (DHBMessageHeader) socketOutputStream
				.getUserDefObject();

		
		ArrayList<ReceivedLetter> receivedLetterList = new ArrayList<ReceivedLetter>();
		boolean isMoreMessage = false;
		SocketInputStream socketInputStream = socketOutputStream.createNewSocketInputStream();;
		long socketOutputStreamSize = socketOutputStream.size();

		try {
			do {
				if (null == messageHeader
						&& socketOutputStreamSize >= messageHeaderSize) {
					/** 스트림 통해 DHB 헤더 읽기전 header MD5 구하기 */
					
					byte[] headerBodyBytes = null;
					byte[] headerBodyMD5Bytes = null;
					try {
						headerBodyBytes = socketInputStream.getBytes(headerBodySize);
						headerBodyMD5Bytes =  socketInputStream.getBytes(CommonStaticFinalVars.MD5_BYTESIZE);
					} catch (Exception e) {
						log.error("unknown error::"+e.getMessage());
						System.exit(1);
					}
					
					byte[] actualHeaderBodyMD5Bytes = null;					
					{
						java.security.MessageDigest md5 = null;
						try {
							md5 = MessageDigest.getInstance("MD5");
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
							System.exit(1);
						}
						// md5.reset();
						md5.update(headerBodyBytes);
						actualHeaderBodyMD5Bytes = md5.digest();
					}
					
					
					
					ByteBuffer headerBodyByteBuffer = ByteBuffer.wrap(headerBodyBytes);
					headerBodyByteBuffer.order(streamByteOrder);
					FixedSizeInputStream headerBodyInputStream = new FixedSizeInputStream(headerBodyByteBuffer, socketInputStream.getStreamCharsetDecoder());
					

					/** 헤더 읽기 */
					DHBMessageHeader workMessageHeader = new DHBMessageHeader(messageIDFixedSize);
					workMessageHeader.fromBodyInputStream(headerBodyInputStream);
					workMessageHeader.setHeaderBodyMD5Bytes(headerBodyMD5Bytes);

					if (workMessageHeader.bodySize < 0) {
						// header format exception
						String errorMessage = String.format(
								"dhb header::body size less than zero %s",
								workMessageHeader.toString());
						throw new HeaderFormatException(errorMessage);
					}

					boolean isValidHeaderBodyMD5 = java.util.Arrays.equals(
							actualHeaderBodyMD5Bytes, workMessageHeader.headerBodyMD5Bytes);

					if (!isValidHeaderBodyMD5) {
						String errorMessage = String.format(
								"dhb header::different header MD5, %s, headerMD5[%s]",
								workMessageHeader.toString(), HexUtil.getHexStringFromByteArray(actualHeaderBodyMD5Bytes));

						throw new HeaderFormatException(errorMessage);
					}

					messageHeader = workMessageHeader;
				}

				if (null != messageHeader) {
					/*log.info(String.format("3. inputStramSizeBeforeMessageWork[%d]", inputStramSizeBeforeMessageWork));*/
					
					
					long messageFrameSize = messageHeader.messageHeaderSize
							+ messageHeader.bodySize;

					if (socketOutputStreamSize >= messageFrameSize) {
						/** 메시지 추출 */
						
						byte[] bodyMD5Bytes = null;
						try {
							bodyMD5Bytes = socketInputStream.getMD5(messageHeader.bodySize, 1024);
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
						boolean isValidBodyMD5 = java.util.Arrays.equals(
								bodyMD5Bytes, messageHeader.bodyMD5Bytes);
						if (!isValidBodyMD5) {
							String errorMessage = String
									.format("different body MD5, header[%s], body md5[%s]",
											messageHeader.toString(),
											HexUtil.getHexStringFromByteArray(bodyMD5Bytes));

							throw new HeaderFormatException(errorMessage);
						}
						
						FreeSizeInputStream messageInputStream = socketOutputStream
								.cutMessageInputStreamFromStartingPosition(messageFrameSize);
						
						try {
							messageInputStream.skip(messageHeader.messageHeaderSize);
						} catch (Exception e) {
							log.error("unknown error::"+e.getMessage());
							System.exit(1);
						}					

						ReceivedLetter receivedLetter = 
								new ReceivedLetter(messageHeader.messageID, 
										messageHeader.mailboxID, messageHeader.mailID, messageInputStream);
						
						receivedLetterList.add(receivedLetter);


						socketOutputStreamSize = socketOutputStream.size();
						if (socketOutputStreamSize > messageHeaderSize) {
							isMoreMessage = true;
						}
						
						messageHeader = null;
						
						socketInputStream = socketOutputStream.createNewSocketInputStream();
					}
				}
			} while (isMoreMessage);
		} finally {
			socketOutputStream.setUserDefObject(messageHeader);
		}

		return receivedLetterList;
	}
	
	public int getMessageHeaderSize() {
		return messageHeaderSize;
	}
}
