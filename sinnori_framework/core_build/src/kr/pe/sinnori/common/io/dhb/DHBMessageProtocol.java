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

package kr.pe.sinnori.common.io.dhb;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import kr.pe.sinnori.common.io.dhb.header.DHBMessageHeader;
import kr.pe.sinnori.common.lib.CharsetUtil;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.SocketInputStream;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.util.HexUtil;

import org.apache.commons.codec.binary.Hex;

/**
 * 메시지 교환 프로토콜 DHB 를 구현한 클래스.
 * 
 * @author Jonghoon Won
 * 
 */
public class DHBMessageProtocol implements CommonRootIF, MessageProtocolIF {

	/** 데이터 패킷 크기 */
	// private int dataPacketBufferSize;
	/** 1개 메시당 데이터 패킷 버퍼 최대수 */
	//private int dataPacketBufferMaxCntPerMessage;

	/** 메시지 헤더에 사용되는 문자열 메시지 식별자의 크기, 단위 byte */
	private int messageIDFixedSize;
	/** 메시지 헤더 크기, 단위 byte */
	private int messageHeaderSize;
	// private int bodyMD5Offset;
	private int headerMD5Offset;
	
	private ByteOrder byteOrderOfProject = null;

	private DataPacketBufferQueueManagerIF dataPacketBufferQueueManager = null;
	private DHBSingleItem2Stream dhbSingleItem2Stream = null;

	public DHBMessageProtocol(int messageIDFixedSize,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) {

		this.messageIDFixedSize = messageIDFixedSize;
		this.messageHeaderSize = DHBMessageHeader
				.getMessageHeaderSize(messageIDFixedSize);
		// this.bodyMD5Offset =
		// DHBMessageHeader.getBodyMD5Offset(messageIDFixedSize);
		this.headerMD5Offset = DHBMessageHeader
				.getHeaderMD5Offset(messageIDFixedSize);
		
		this.byteOrderOfProject = dataPacketBufferQueueManager.getByteOrder();

		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;

		// this.dataPacketBufferSize = dataPacketBufferQueueManager.getDataPacketBufferSize();
		// this.dataPacketBufferMaxCntPerMessage = dataPacketBufferQueueManager.getDataPacketBufferMaxCntPerMessage();

		dhbSingleItem2Stream = new DHBSingleItem2Stream();
	}

	@Override
	public ArrayList<WrapBuffer> M2S(AbstractMessage messageObj,
			Charset charsetOfProject) throws NoMoreDataPacketBufferException,
			BodyFormatException {
		CharsetEncoder charsetOfProjectEncoder = CharsetUtil
				.createCharsetEncoder(charsetOfProject);
		java.security.MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			log.fatal("failed to get a MD5 instance", e);
			System.exit(1);
		}
		// java.security.MessageDigest md5 = DigestUtils.getMd5Digest();

		/** 바디 만들기 */
		FreeSizeOutputStream bodyOutputStream = new FreeSizeOutputStream(
				charsetOfProject, charsetOfProjectEncoder, messageHeaderSize,
				dataPacketBufferQueueManager);
		messageObj.M2O(bodyOutputStream, dhbSingleItem2Stream);

		/** 데이터 헤더 만들기 */
		DHBMessageHeader messageHeader = new DHBMessageHeader(
				messageIDFixedSize);
		messageHeader.messageID = messageObj.getMessageID();
		messageHeader.mailboxID = messageObj.messageHeaderInfo.mailboxID;
		messageHeader.mailID = messageObj.messageHeaderInfo.mailID;
		messageHeader.bodySize = bodyOutputStream.postion() - messageHeaderSize;

		/** 바디 MD5 */
		ArrayList<WrapBuffer> messageWrapBufferList = bodyOutputStream
				.getNoFlipDataPacketBufferList();
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
	public ArrayList<AbstractMessage> S2MList(
			Class<? extends AbstractMessage> targetClass,
			Charset charsetOfProject,
			SocketInputStream messageInputStreamResource,
			MessageMangerIF messageManger) throws HeaderFormatException,
			NoMoreDataPacketBufferException {
		CharsetDecoder charsetOfProjectDecoder = CharsetUtil
				.createCharsetDecoder(charsetOfProject);
		// ArrayList<WrapBuffer> messageReadWrapBufferList =
		// messageInputStreamResource.getMessageReadWrapBufferList();
		DHBMessageHeader messageHeader = (DHBMessageHeader) messageInputStreamResource
				.getUserDefObject();

		ArrayList<AbstractMessage> messageList = new ArrayList<AbstractMessage>();

		java.security.MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// java.security.MessageDigest md5 = DigestUtils.getMd5Digest();

		boolean isMoreMessage = false;
		int messageReadWrapBufferListSize = messageInputStreamResource
				.getDataPacketBufferListSize();
		if (messageReadWrapBufferListSize == 0) {
			log.fatal(String.format("messageReadWrapBufferListSize is zero"));
			System.exit(1);
		}
		
		/** 최종적으로 읽어온 마지막 버퍼의 인덱스와 위치를 기억합니다. */
		// int lastIndex = messageReadWrapBufferListSize - 1;
		ByteBuffer lastInputStreamBuffer = messageInputStreamResource
				.getLastDataPacketBuffer();
		// int lastPosition = lastInputStreamBuffer.position();

		/**
		 * 소켓별 스트림 자원을 갖는다. 스트림은 데이터 패킷 버퍼 목록으로 구현한다.<br/>
		 * 반환되는 스트림은 데이터 패킷 버퍼의 속성을 건들지 않기 위해서 복사본으로 구성되며 읽기 가능 상태이다.<br/>
		 * 내부 처리를 요약하면 All ByteBuffer.duplicate().flip() 이다.<br/>
		 * 매번 새로운 스트림이 만들어지는 단점이 있다. <br/>
		 */
		FreeSizeInputStream freeSizeInputStream = null;
		ArrayList<ByteBuffer> streamBufferList = null;
		int streamBufferListSize = -1;
		int startIndex = -1;
		int startPosition = -1;

		try {
			// long inputStramSizeBeforeMessageWork = freeSizeInputStream.remaining();
			long inputStramSizeBeforeMessageWork = messageInputStreamResource.position();
			
			
			/*log.info(String.format("1. messageHeaderSize=[%d], inputStramSizeBeforeMessageWork[%d]",
					messageHeaderSize, inputStramSizeBeforeMessageWork));*/

			do {
				// log.debug(String.format("1. messageReadWrapBufferListSize=[%d], lastInputStreamBuffer=[%s], inputStramSizeBeforeMessageWork=[%d]",
				// messageReadWrapBufferListSize,
				// lastInputStreamBuffer.toString(),
				// inputStramSizeBeforeMessageWork));
				isMoreMessage = false;

				if (null == messageHeader
						&& inputStramSizeBeforeMessageWork >= messageHeaderSize) {
					/** 스트림 통해 DHB 헤더 읽기전 header MD5 구하기 */
					
					if (null == freeSizeInputStream) {
						freeSizeInputStream = messageInputStreamResource
								.getFreeSizeInputStream(charsetOfProjectDecoder);
						streamBufferList = freeSizeInputStream
								.getStreamBufferList();
						streamBufferListSize = streamBufferList.size();
						startIndex = freeSizeInputStream.getIndexOfWorkBuffer();
						startPosition = freeSizeInputStream.getPositionOfWorkBuffer();
					}

					byte[] headerMD5 = null;

					int spaceBytesOfHeaderMD5 = headerMD5Offset;

					for (int i = startIndex; i < streamBufferListSize; i++) {
						ByteBuffer dupByteBuffer = streamBufferList.get(i)
								.duplicate();
						dupByteBuffer.order(byteOrderOfProject);
						// log.info(String.format("1.i[%d] spaceBytesOfHeaderMD5[%d] %s", i, spaceBytesOfHeaderMD5, dupByteBuffer.toString()));
						
						
						int spaceBytesOfDupByteBuffer = dupByteBuffer
								.remaining();
						if (spaceBytesOfHeaderMD5 <= spaceBytesOfDupByteBuffer) {
							dupByteBuffer.limit(dupByteBuffer.position()
									+ spaceBytesOfHeaderMD5);
							
							// FIXME!
							//log.info(String.format("3.i[%d] spaceBytesOfHeaderMD5[%d]", i, spaceBytesOfHeaderMD5));
							//log.info(String.format("%s", dupByteBuffer.toString()));
							//log.info(String.format("%s", HexUtil.byteBufferAvailableToHex(dupByteBuffer)));
							
							md5.update(dupByteBuffer);
							headerMD5 = md5.digest();
							
							//log.info(String.format("3.%s", HexUtil.byteArrayAllToHex(headerMD5)));
							break;
						} else {
							// FIXME!
							//log.info(String.format("2.i[%d] spaceBytesOfHeaderMD5[%d] %s", i, spaceBytesOfHeaderMD5, dupByteBuffer.toString()));
							
							md5.update(dupByteBuffer);
							spaceBytesOfHeaderMD5 -= spaceBytesOfDupByteBuffer;
						}
					}

					/** 헤더 읽기 */
					DHBMessageHeader workMessageHeader = new DHBMessageHeader(
							messageIDFixedSize);
					try {
						workMessageHeader.messageID = freeSizeInputStream
								.getString( messageIDFixedSize,
										CharsetUtil.createCharsetDecoder(DHBMessageHeader.HEADER_CHARSET)).trim();
					} catch (SinnoriCharsetCodingException e1) {
						String errorMessage = e1.getMessage();
						log.warn(errorMessage, e1);
						throw new HeaderFormatException(errorMessage);
					}
					workMessageHeader.mailboxID = freeSizeInputStream
							.getUnsignedShort();
					workMessageHeader.mailID = freeSizeInputStream.getInt();
					workMessageHeader.bodySize = freeSizeInputStream.getLong();
					workMessageHeader.bodyMD5 = freeSizeInputStream
							.getBytes(DHBMessageHeader.MD5_BYTESIZE);
					workMessageHeader.headerMD5 = freeSizeInputStream
							.getBytes(DHBMessageHeader.MD5_BYTESIZE);

					if (workMessageHeader.bodySize < 0) {
						// header format exception
						String errorMessage = String.format(
								"dhb header body size less than zero %s",
								workMessageHeader.toString());
						throw new HeaderFormatException(errorMessage);
					}

					boolean isValidHeaderMD5 = java.util.Arrays.equals(
							headerMD5, workMessageHeader.headerMD5);

					if (!isValidHeaderMD5) {
						String errorMessage = String.format(
								"different header MD5, %s, headerMD5[%s]",
								workMessageHeader.toString(), Hex.encodeHexString(headerMD5));

						throw new HeaderFormatException(errorMessage);
					}

					messageHeader = workMessageHeader;
				}

				if (null != messageHeader) {
					//log.info(String.format("2. startIndex=[%d], startPosition=[%d], messageHeader=[%s]", startIndex, startPosition, messageHeader.toString()));
					
					long messageFrameSize = messageHeader.messageHeaderSize
							+ messageHeader.bodySize;

					if (inputStramSizeBeforeMessageWork >= messageFrameSize) {
						/** 메시지 추출 */
						if (null == freeSizeInputStream) {
							freeSizeInputStream = messageInputStreamResource
									.getFreeSizeInputStream(charsetOfProjectDecoder);
							streamBufferList = freeSizeInputStream
									.getStreamBufferList();
							streamBufferListSize = streamBufferList.size();
							
							/*startIndex = freeSizeInputStream.getIndexOfWorkBuffer();
							startPosition = freeSizeInputStream.getPositionOfWorkBuffer();							
							long expectedPosition = startIndex*lastInputStreamBuffer.capacity()+startPosition+messageHeaderSize;
							freeSizeInputStream.skip(expectedPosition);*/
							startIndex = 0;
							startPosition = 0;
							freeSizeInputStream.skip(messageHeaderSize);
						}
						
						long postionBeforeReadingBody = freeSizeInputStream.position();
						/*
						long expectedPosition = startIndex*lastInputStreamBuffer.capacity()+startPosition+messageHeaderSize;
						
						//log.info(String.format("3. messageFrameSize=[%d], postionBeforeReadingBody=[%d], expectedPosition=[%d]", messageFrameSize, postionBeforeReadingBody, expectedPosition));
						
						if (expectedPosition != postionBeforeReadingBody) {
							postionBeforeReadingBody = expectedPosition;
							freeSizeInputStream.skip(expectedPosition);
						}*/

						/** body MD5 구하기 */
						int startBodyIndex = freeSizeInputStream
								.getIndexOfWorkBuffer();
						// int startBodyPostion = freeSizeInputStream.getPositionOfWorkBuffer();
						byte[] bodyMD5 = null;
						long spaceBytesOfBodyMD5 = messageHeader.bodySize;
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
						}

						/** 바디 MD5 와 헤더 정보 바디 MD5 비교 */
						boolean isValidBodyMD5 = java.util.Arrays.equals(
								bodyMD5, messageHeader.bodyMD5);
						if (!isValidBodyMD5) {
							String errorMessage = String
									.format("different body MD5, header[%s], body md5[%s]",
											messageHeader.toString(),
											HexUtil.byteArrayAllToHex(bodyMD5));

							throw new HeaderFormatException(errorMessage);
						}

						

						if (targetClass.equals(InputMessage.class)) {
							InputMessage workInObj = null;
							try {
								workInObj = messageManger
										.createInputMessage(messageHeader.messageID);
								workInObj.messageHeaderInfo.mailboxID = messageHeader.mailboxID;
								workInObj.messageHeaderInfo.mailID = messageHeader.mailID;
								
								workInObj.O2M(freeSizeInputStream,
										dhbSingleItem2Stream);								

								long postionAfterReadingBody = freeSizeInputStream.position();
								long bodySize = postionAfterReadingBody - postionBeforeReadingBody;
								
								if (bodySize != messageHeader.bodySize) {
									// FIXME! 잔존 데이터 있음.
									String errorMessage = String.format(
											"메시지[%s]를 읽는 과정에서 잔존 데이터[%d bytes]가 남았습니다.",
											workInObj.toString(), (messageHeader.bodySize - bodySize));
									// log.warn(errorMessage, e);
									throw new HeaderFormatException(
											errorMessage);
								}
							} catch (MessageInfoNotFoundException e) {
								log.info(
										String.format(
												"MessageInfoNotFoundException::header=[%s]",
												messageHeader.toString()), e);

								InputMessage errorInObj = null;
								try {
									errorInObj = messageManger
											.createInputMessage("SelfExn");
								} catch (MessageInfoNotFoundException e1) {
									log.fatal(
											"시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.",
											e1);
									System.exit(1);
								}
								/**
								 * 참고) 메시지 정보 파일 없기때문에 정상적인 메시지를 생성할 수 없어 헤더 정보를
								 * 통해 메시지 헤더 정보를 저장한다.
								 */
								errorInObj.messageHeaderInfo.mailboxID = messageHeader.mailboxID;
								errorInObj.messageHeaderInfo.mailID = messageHeader.mailID;
								errorInObj.setAttribute("whereError", "S");
								errorInObj.setAttribute("errorGubun", "M");
								errorInObj.setAttribute("errorMessageID",
										messageHeader.messageID);
								errorInObj.setAttribute("errorMessage",
										e.getMessage());

								workInObj = errorInObj;

								long skipBytes = messageHeader.bodySize;
								freeSizeInputStream.skip(skipBytes);
							} catch (BodyFormatException e) {
								log.info(String.format(
										"BodyFormatException::header=[%s]",
										messageHeader.toString()), e);

								InputMessage errorInObj = null;
								try {
									errorInObj = messageManger
											.createInputMessage("SelfExn");
								} catch (MessageInfoNotFoundException e1) {
									log.fatal(
											"시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.",
											e1);
									System.exit(1);
								}

								errorInObj.messageHeaderInfo = workInObj.messageHeaderInfo;
								errorInObj.setAttribute("whereError", "S");
								errorInObj.setAttribute("errorGubun", "B");
								errorInObj.setAttribute("errorMessageID",
										messageHeader.messageID);
								errorInObj.setAttribute("errorMessage",
										e.getMessage());

								workInObj = errorInObj;

								long postionAfterReadingBody = freeSizeInputStream
										.position();
								long skipBytes = messageHeader.bodySize
										- (postionAfterReadingBody - postionBeforeReadingBody);
								freeSizeInputStream.skip(skipBytes);
							}

							// log.debug(String.format("10. lastInputStreamBuffer=[%s]",
							// lastInputStreamBuffer.toString()));

							/** 목록에 메시지 추가 */
							messageList.add(workInObj);
						} else {
							OutputMessage workOutObj = null;
							try {
								workOutObj = messageManger
										.createOutputMessage(messageHeader.messageID);
								workOutObj.messageHeaderInfo.mailboxID = messageHeader.mailboxID;
								workOutObj.messageHeaderInfo.mailID = messageHeader.mailID;

								workOutObj.O2M(freeSizeInputStream,
										dhbSingleItem2Stream);

								long postionAfterReadingBody = freeSizeInputStream.position();
								
								long bodySize = postionAfterReadingBody - postionBeforeReadingBody;
								
								if (bodySize != messageHeader.bodySize) {
									// FIXME! 잔존 데이터 있음.
									String errorMessage = String.format(
											"메시지[%s]를 읽는 과정에서 잔존 데이터[%d bytes]가 남았습니다.",
											workOutObj.toString(), (messageHeader.bodySize - bodySize));
									// log.warn(errorMessage, e);
									throw new HeaderFormatException(
											errorMessage);
								}
							} catch (MessageInfoNotFoundException e) {
								log.info(String.format(
										"BodyFormatException::header=[%s]",
										messageHeader.toString()), e);

								OutputMessage errorOutObj = null;
								try {
									errorOutObj = messageManger
											.createOutputMessage("SelfExn");
								} catch (MessageInfoNotFoundException e1) {
									log.fatal(
											"시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.",
											e1);
									System.exit(1);
								}
								/**
								 * 참고) 메시지 정보 파일 없기때문에 정상적인 메시지를 생성할 수 없어 헤더 정보를
								 * 통해 메시지 헤더 정보를 저장한다.
								 */
								errorOutObj.messageHeaderInfo.mailboxID = messageHeader.mailboxID;
								errorOutObj.messageHeaderInfo.mailID = messageHeader.mailID;
								errorOutObj.setAttribute("whereError", "S");
								errorOutObj.setAttribute("errorGubun", "M");
								errorOutObj.setAttribute("errorMessageID",
										messageHeader.messageID);
								errorOutObj.setAttribute("errorMessage",
										e.getMessage());

								workOutObj = errorOutObj;

								long skipBytes = messageHeader.bodySize;
								freeSizeInputStream.skip(skipBytes);
							} catch (BodyFormatException e) {
								log.info(String.format(
										"BodyFormatException::header=[%s]",
										messageHeader.toString()), e);

								OutputMessage errorOutObj = null;
								try {
									errorOutObj = messageManger
											.createOutputMessage("SelfExn");
								} catch (MessageInfoNotFoundException e1) {
									log.fatal(
											"시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.",
											e1);
									System.exit(1);
								}

								errorOutObj.messageHeaderInfo = workOutObj.messageHeaderInfo;
								errorOutObj.setAttribute("whereError", "S");
								errorOutObj.setAttribute("errorGubun", "B");
								errorOutObj.setAttribute("errorMessageID",
										messageHeader.messageID);
								errorOutObj.setAttribute("errorMessage",
										e.getMessage());

								workOutObj = errorOutObj;

								long postionAfterReadingBody = freeSizeInputStream
										.position();
								long skipBytes = messageHeader.bodySize
										- (postionAfterReadingBody - postionBeforeReadingBody);
								freeSizeInputStream.skip(skipBytes);
							}

							// log.debug(String.format("10. lastInputStreamBuffer=[%s]",
							// lastInputStreamBuffer.toString()));

							/** 목록에 메시지 추가 */
							messageList.add(workOutObj);
						}

						inputStramSizeBeforeMessageWork = freeSizeInputStream.remaining();
						if (inputStramSizeBeforeMessageWork > messageHeaderSize) {
							isMoreMessage = true;
						}
						
						startPosition = freeSizeInputStream.getPositionOfWorkBuffer();
						startIndex = freeSizeInputStream.getIndexOfWorkBuffer();
						messageHeader = null;
					}
				}
			} while (isMoreMessage);
			
			if (messageList.size() > 0) {
				messageInputStreamResource.truncate(startIndex, startPosition);
			} else if (!lastInputStreamBuffer.hasRemaining()) {
				/** 메시지 추출 실패했는데도 마지막 버퍼가 꽉차있다면 스트림 크기를 증가시킨다. 단 설정파일 환경변수 "메시지당 최대 데이터 패킷 갯수" 만큼만 증가될수있다. */
				lastInputStreamBuffer = messageInputStreamResource.nextDataPacketBuffer();
			}
		} catch (MessageItemException e) {
			log.fatal(e.getMessage(), e);
			System.exit(1);
		} finally {
			messageInputStreamResource.setUserDefObject(messageHeader);
		}

		return messageList;
	}	
}
