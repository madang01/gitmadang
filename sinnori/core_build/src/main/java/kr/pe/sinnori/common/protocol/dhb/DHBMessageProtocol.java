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
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import kr.pe.sinnori.common.etc.CharsetUtil;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriBufferUnderflowException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolManagerIF;
import kr.pe.sinnori.common.io.FreeSizeInputStream;
import kr.pe.sinnori.common.io.FreeSizeOutputStream;
import kr.pe.sinnori.common.io.SocketInputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.ReceivedLetter;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;
import kr.pe.sinnori.common.protocol.dhb.header.DHBMessageHeader;
import kr.pe.sinnori.common.util.HexUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	/** 메시지 헤더 크기, 단위 byte */
	private int messageHeaderSize;
	// private int bodyMD5Offset;
	private int headerMD5Offset;
	
	private ByteOrder byteOrderOfProject = null;

	private DataPacketBufferPoolManagerIF dataPacketBufferQueueManager = null;
	private DHBSingleItemDecoder dhbSingleItemDecoder = null;
	private DHBSingleItemEncoder dhbSingleItemEncoder = null;
	private int dataPacketBufferMaxCntPerMessage;

	//private ClientObjectManager clientMessageController = ClientObjectManager.getInstance();
	// private ServerObjectManager serverMessageController = ServerObjectManager.getInstance();
	
	public DHBMessageProtocol(int messageIDFixedSize,
			int dataPacketBufferMaxCntPerMessage,
			DataPacketBufferPoolManagerIF dataPacketBufferQueueManager) {
		/*if (messageIDFixedSize < 0) {
			String errorMessage = String.format("parameter messageIDFixedSize less than zero");
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		int dataPacketBufferSize = dataPacketBufferQueueManager.getDataPacketBufferSize();
		if (messageIDFixedSize > dataPacketBufferSize) {
			log.error(String.format("parameter messageIDFixedSize[%d] greater than dataPacketBufferSize[%d]", messageIDFixedSize, dataPacketBufferSize));
			System.exit(1);
		}*/

		this.messageIDFixedSize = messageIDFixedSize;
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		this.messageHeaderSize = DHBMessageHeader
				.getMessageHeaderSize(messageIDFixedSize);
		// this.bodyMD5Offset = DHBMessageHeader.getBodyMD5Offset(messageIDFixedSize);
		this.headerMD5Offset = DHBMessageHeader
				.getHeaderMD5Offset(messageIDFixedSize);		
		this.byteOrderOfProject = dataPacketBufferQueueManager.getByteOrder();
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		
		this.dhbSingleItemDecoder = new DHBSingleItemDecoder();
		this.dhbSingleItemEncoder = new DHBSingleItemEncoder();
	}
	
	
	@Override
	public ArrayList<WrapBuffer> M2S(AbstractMessage messageObj, AbstractMessageEncoder messageEncoder, Charset charsetOfProject) 
			throws NoMoreDataPacketBufferException, BodyFormatException {
		CharsetEncoder charsetOfProjectEncoder = CharsetUtil
				.createCharsetEncoder(charsetOfProject);
		java.security.MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			log.error("failed to get a MD5 instance", e);
			System.exit(1);
		}
		// java.security.MessageDigest md5 = DigestUtils.getMd5Digest();

		/** 바디 만들기 */
		FreeSizeOutputStream bodyOutputStream = new FreeSizeOutputStream(dataPacketBufferMaxCntPerMessage, charsetOfProjectEncoder, dataPacketBufferQueueManager);
		
		// messageHeaderSize
		
		// bodyOutputStream.skip(messageHeaderSize);
		
		/*String messageID = messageObj.getMessageID();
		MessageEncoder messageEncoder = null;
		Class<?> classObj = null;
		try {
			// messageEncoder = 
			classObj = Class.forName(new StringBuilder().append("impl.message.").append(messageID).append(".").append(messageID).append("Encoder").toString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			messageEncoder = (MessageEncoder)classObj.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		
		
		try {
			messageEncoder.encode(messageObj, dhbSingleItemEncoder, charsetOfProject, bodyOutputStream);
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
		messageHeader.bodySize = bodyOutputStream.getOutputStreamSize() - messageHeaderSize;

		/** 바디 MD5 */
		ArrayList<WrapBuffer> messageWrapBufferList = bodyOutputStream
				.getFlippedWrapBufferList();
		int bufferListSize = messageWrapBufferList.size();

		ByteBuffer firstWorkBuffer = messageWrapBufferList.get(0)
				.getByteBuffer();
		firstWorkBuffer.flip();
		ByteBuffer firstDupBuffer = firstWorkBuffer.duplicate();
		firstDupBuffer.order(byteOrderOfProject);
		firstDupBuffer.position(messageHeaderSize);
		md5.update(firstDupBuffer);

		for (int i = 1; i < bufferListSize; i++) {
			ByteBuffer workBuffer = messageWrapBufferList.get(i)
					.getByteBuffer();
			workBuffer.flip();
			ByteBuffer dupBuffer = workBuffer.duplicate();
			dupBuffer.order(byteOrderOfProject);
			md5.update(dupBuffer);
		}

		messageHeader.bodyMD5 = md5.digest();

		firstDupBuffer.clear();
		messageHeader.writeMessageHeader(firstDupBuffer, charsetOfProject,
				charsetOfProjectEncoder, md5);

		// log.debug(messageHeader.toString());
		// log.debug(firstWorkBuffer.toString());

		return messageWrapBufferList;
	}

	@Override
	public SingleItemDecoderIF getSingleItemDecoder() {
		return dhbSingleItemDecoder;
	}

	
	@Override
	public ArrayList<ReceivedLetter> S2MList(Charset charsetOfProject, 
			SocketInputStream socketInputStream) 
			throws HeaderFormatException, NoMoreDataPacketBufferException {
		CharsetDecoder charsetOfProjectDecoder = CharsetUtil
				.createCharsetDecoder(charsetOfProject);
		DHBMessageHeader messageHeader = (DHBMessageHeader) socketInputStream
				.getUserDefObject();

		ArrayList<ReceivedLetter> receivedLetterList = new ArrayList<ReceivedLetter>();

		java.security.MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		/*String errorWhere = null;
		if (serverClientGubun == CommonType.SERVER_CLIENT_GUBUN.CLIENT) {
			errorWhere = "C";
		} else {
			errorWhere = "S";
		}*/

		// java.security.MessageDigest md5 = DigestUtils.getMd5Digest();

		boolean isMoreMessage = false;
		int messageReadWrapBufferListSize = socketInputStream
				.getDataPacketBufferListSize();
		if (messageReadWrapBufferListSize == 0) {
			log.error(String.format("messageReadWrapBufferListSize is zero"));
			System.exit(1);
		}
		
		/** 최종적으로 읽어온 마지막 버퍼의 인덱스와 위치를 기억합니다. */
		// int lastIndex = messageReadWrapBufferListSize - 1;
		ByteBuffer lastInputStreamBuffer = socketInputStream
				.getLastDataPacketBuffer();
		// int lastPosition = lastInputStreamBuffer.position();

		/**
		 * 소켓별 스트림 자원을 갖는다. 스트림은 데이터 패킷 버퍼 목록으로 구현한다.<br/>
		 * 반환되는 스트림은 데이터 패킷 버퍼의 속성을 건들지 않기 위해서 복사본으로 구성되며 읽기 가능 상태이다.<br/>
		 * 내부 처리를 요약하면 All ByteBuffer.duplicate().flip() 이다.<br/>
		 * 매번 새로운 스트림이 만들어지는 단점이 있다. <br/>
		 */
		FreeSizeInputStream freeSizeInputStream = null;
		// ArrayList<ByteBuffer> streamBufferList = null;
		// int streamBufferListSize = -1;
		// int startIndex = -1;
		// int startPosition = -1;

		try {
			// long inputStramSizeBeforeMessageWork = freeSizeInputStream.remaining();
			long inputStramSizeBeforeMessageWork = socketInputStream.position();
			
			
			
			/*log.info("1. messageHeaderSize=[{}], inputStramSizeBeforeMessageWork[{}]",
					messageHeaderSize, inputStramSizeBeforeMessageWork);*/
			
			int lastPostionOfWorkBuffer = 0;
			int lastIndexOfWorkBuffer = 0;

			do {
				
				// log.info("2. isMoreMessage=[{}], inputStramSizeBeforeMessageWork[{}]", isMoreMessage,  inputStramSizeBeforeMessageWork);
				// FIMXE!
				// log.info("111111111122222222222::{}, {}", (messageHeader == null), inputStramSizeBeforeMessageWork);
				
				isMoreMessage = false;

				if (null == messageHeader
						&& inputStramSizeBeforeMessageWork >= messageHeaderSize) {
					/** 스트림 통해 DHB 헤더 읽기전 header MD5 구하기 */
					
					/*log.info(String.format("2. inputStramSizeBeforeMessageWork[%d]", inputStramSizeBeforeMessageWork));*/
					
					if (null == freeSizeInputStream) {
						freeSizeInputStream = socketInputStream
								.getFreeSizeInputStream(charsetOfProjectDecoder);
						/*startIndex = freeSizeInputStream.getIndexOfWorkBuffer();
						startPosition = freeSizeInputStream.getPositionOfWorkBuffer();*/
						// startIndex = 0;
						// startPosition = 0;
					}
					
					// FIXME!
					// log.info("freeSizeInputStream.remaining()={}", freeSizeInputStream.remaining());

					md5.reset();
					byte[] headerMD5 = null;
					try {
						headerMD5 = freeSizeInputStream.getMD5FromDupStream(headerMD5Offset, md5);
					} catch (IllegalArgumentException e) {
						String errorMessage = e.getMessage();
						log.error(errorMessage, e);
						System.exit(1);
					} catch (SinnoriBufferUnderflowException e) {
						String errorMessage = e.getMessage();
						log.error(errorMessage, e);
						System.exit(1);
					}

					/** 헤더 읽기 */
					DHBMessageHeader workMessageHeader = new DHBMessageHeader(messageIDFixedSize);
					workMessageHeader.readMessageHeader(freeSizeInputStream);

					if (workMessageHeader.bodySize < 0) {
						// header format exception
						String errorMessage = String.format(
								"dhb header::body size less than zero %s",
								workMessageHeader.toString());
						throw new HeaderFormatException(errorMessage);
					}

					boolean isValidHeaderMD5 = java.util.Arrays.equals(
							headerMD5, workMessageHeader.headerMD5);

					if (!isValidHeaderMD5) {
						String errorMessage = String.format(
								"dhb header::different header MD5, %s, headerMD5[%s]",
								workMessageHeader.toString(), HexUtil.getHexStringFromByteArray(headerMD5));

						throw new HeaderFormatException(errorMessage);
					}

					messageHeader = workMessageHeader;
				}

				if (null != messageHeader) {
					/*log.info(String.format("3. inputStramSizeBeforeMessageWork[%d]", inputStramSizeBeforeMessageWork));*/
					
					
					long messageFrameSize = messageHeader.messageHeaderSize
							+ messageHeader.bodySize;

					if (inputStramSizeBeforeMessageWork >= messageFrameSize) {
						/** 메시지 추출 */
						if (null == freeSizeInputStream) {
							freeSizeInputStream = socketInputStream
									.getFreeSizeInputStream(charsetOfProjectDecoder);
							/*startIndex = freeSizeInputStream.getIndexOfWorkBuffer();
							startPosition = freeSizeInputStream.getPositionOfWorkBuffer();							
							long skipBytes = startIndex*lastInputStreamBuffer.capacity()+startPosition+messageHeaderSize;*/
							
							/*startIndex = 0;
							startPosition = 0;*/
							/**
							 * <pre>
							 * 소켓 읽기 이벤트때마다 수신된 데이터가 저장되는 "소켓 출력 스트림" 으로 부터
							 * "소켓 출력 스트림" 에 영향을 주지 않는 
							 * "소켓 입력 스트림"(=freeSizeInputStream) 이 새롭게 만들어 진다.
							 * 따라서 이전 소켓 읽기 이벤트때의 "소켓 입력 스트림" 의 상태는 보존되지 않는다.
							 * 정리하자면 이전 소켓 읽기 이벤트때 "소켓 입력 스트림" 로 헤더 정보를 읽었어도 
							 * 현재의 "소켓 입력 스트림" 는 헤더 정보를 읽기전 백지 상태이다.
							 * 
							 * 헤더 정보는 효율을 위해서 1번만 읽도록 헤더 정보를 보존하여 재 사용한다.
							 * 이렇게 이전에 헤더 정보를 읽었다면 헤더 정보를 읽기전 백지 상태인 
							 * "소켓 입력 스트림" 도 헤더를 읽은 상태로 복원하기위해 헤더 크기만큼 건너뛰기한다. 
							 * </pre>
							 */
							long skipBytes = messageHeaderSize;
							
							try {
								freeSizeInputStream.skip(skipBytes);
							} catch (IllegalArgumentException e) {
								String errorMessage = e.getMessage();
								log.error(errorMessage, e);
								System.exit(1);
							} catch (SinnoriBufferUnderflowException e) {
								String errorMessage = e.getMessage();
								log.error(errorMessage, e);
								System.exit(1);
							}
							
							// FIXME!
							// log.info("444::freeSizeInputStream.remaining={}, messageHeader.bodySize={}, messageFrameSize={}", freeSizeInputStream.remaining(), messageHeader.bodySize, messageFrameSize);
						}/* else {
							// FIXME!
							log.info("555::freeSizeInputStream.remaining={}, messageHeader.bodySize={}, header.size={}, messageFrameSize={}", freeSizeInputStream.remaining(), messageHeader.bodySize, messageHeader.messageHeaderSize, messageFrameSize);
						}*/
						
						
						
						// long postionBeforeReadingBody = freeSizeInputStream.position();
						
						
						/*long expectedPosition = startIndex*lastInputStreamBuffer.capacity()+startPosition+messageHeaderSize;
						log.info(String.format("4.startIndex=[%d], startPosition=[%d], freeSizeInputStream.remaining=[%d], expectedPosition=[%d], postionBeforeReadingBody=[%d]"
								, startIndex, startPosition, freeSizeInputStream.remaining(), expectedPosition, postionBeforeReadingBody));						
						log.info(String.format("4.messageHeader=[%s]", messageHeader.toString()));*/

						/** body MD5 구하기 */
						// int startBodyIndex = freeSizeInputStream.getIndexOfWorkBuffer();
						// int startBodyPostion = freeSizeInputStream.getPositionOfWorkBuffer();
						md5.reset();
						
						byte[] bodyMD5 = null;
						try {
							bodyMD5 = freeSizeInputStream.getMD5FromDupStream(messageHeader.bodySize, md5);
						} catch (IllegalArgumentException e) {
							String errorMessage = e.getMessage();
							log.error(errorMessage, e);
							System.exit(1);
						} catch (SinnoriBufferUnderflowException e) {
							String errorMessage = e.getMessage();
							log.error(errorMessage, e);
							System.exit(1);
						}
						
						
						
						/*long spaceBytesOfBodyMD5 = messageHeader.bodySize;
						// md5.reset();
						for (int i = startBodyIndex; i < streamBufferListSize; i++) {
							ByteBuffer dupByteBuffer = streamBufferList.get(i)
									.duplicate();
							dupByteBuffer.order(byteOrderOfProject);
														
							int spaceBytesOfDupByteBuffer = dupByteBuffer.remaining();
							
							// FIXME!
							//log.info(String.format("4. dupByteBuffer[%d]=[%s], spaceBytesOfBodyMD5=[%d], spaceBytesOfDupByteBuffer=[%d]", i, dupByteBuffer.toString(), spaceBytesOfBodyMD5, spaceBytesOfDupByteBuffer));
							
							if (spaceBytesOfBodyMD5 <= spaceBytesOfDupByteBuffer) {
								dupByteBuffer.limit(dupByteBuffer.position() + (int)spaceBytesOfBodyMD5);
								md5.update(dupByteBuffer);
								bodyMD5 = md5.digest();
								break;
							} else {
								md5.update(dupByteBuffer);
								spaceBytesOfBodyMD5 -= spaceBytesOfDupByteBuffer;
							}
						}*/

						/** 바디 MD5 와 헤더 정보 바디 MD5 비교 */
						boolean isValidBodyMD5 = java.util.Arrays.equals(
								bodyMD5, messageHeader.bodyMD5);
						if (!isValidBodyMD5) {
							String errorMessage = String
									.format("different body MD5, header[%s], body md5[%s]",
											messageHeader.toString(),
											HexUtil.getHexStringFromByteArray(bodyMD5));

							throw new HeaderFormatException(errorMessage);
						}
						
						FreeSizeInputStream bodyInputStream = null;
						// FIXME!
						/*try {
							bodyInputStream = freeSizeInputStream.getInputStream(messageHeader.bodySize);
						} catch (IllegalArgumentException e) {
							String errorMessage = e.getMessage();
							log.error(errorMessage, e);
							System.exit(1);
						} catch (SinnoriBufferUnderflowException e) {
							String errorMessage = e.getMessage();
							log.error(errorMessage, e);
							System.exit(1);
						}*/
						
						lastPostionOfWorkBuffer = freeSizeInputStream.getPositionOfWorkBuffer();
						lastIndexOfWorkBuffer = freeSizeInputStream.getIndexOfWorkBuffer();
						// FIXME!
						// log.info("666::freeSizeInputStream.remaining={}", freeSizeInputStream.remaining());
						

						ReceivedLetter receivedLetter = 
								new ReceivedLetter(messageHeader.messageID, 
										messageHeader.mailboxID, messageHeader.mailID, bodyInputStream);
						
						receivedLetterList.add(receivedLetter);

						inputStramSizeBeforeMessageWork -= messageFrameSize;
						if (inputStramSizeBeforeMessageWork > messageHeaderSize) {
							isMoreMessage = true;
						}
						
						/*if (freeSizeInputStream.remaining() != inputStramSizeBeforeMessageWork) {
							log.warn(String.format("different freeSizeInputStream.remaining[%d] to inputStramSizeBeforeMessageWork[%d]"
									, freeSizeInputStream.remaining(), inputStramSizeBeforeMessageWork));
						}*/
						
						// startIndex = freeSizeInputStream.getIndexOfWorkBuffer();
						// startPosition = freeSizeInputStream.getPositionOfWorkBuffer();
						messageHeader = null;
					}
				}
			} while (isMoreMessage);
			
			if (receivedLetterList.size() > 0) {
				// socketInputStream.truncate(startIndex, startPosition);
				// socketInputStream.truncate(freeSizeInputStream.getIndexOfWorkBuffer(), freeSizeInputStream.getPositionOfWorkBuffer());
				socketInputStream.truncate(lastIndexOfWorkBuffer, lastPostionOfWorkBuffer);
			} else if (!lastInputStreamBuffer.hasRemaining()) {
				/**
				 * <pre>
				 * 메시지 추출 실패했는데도 마지막 버퍼가 꽉차있다면 스트림 크기를 증가시킨다. 
				 * 단 설정파일 환경변수 "메시지당 최대 데이터 패킷 갯수" 만큼만 증가될수있다.
				 * 
				 * 특이사항 : 데이터 패킷 버퍼가 없는 경우 예외를 던져 프로토콜 밖에서 처리를 하게 한다.
				 * </pre>
				 */
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
