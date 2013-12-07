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

package kr.pe.sinnori.server.threadpool.outputmessage.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.MessageExchangeProtocolIF;
import kr.pe.sinnori.common.lib.CommonProjectInfo;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.ClientResourceManagerIF;
import kr.pe.sinnori.server.io.LetterToClient;

/**
 * 서버 출력 메시지 소켓 쓰기 담당 쓰레드
 * 
 * @author Jonghoon Won
 * 
 */
public class OutputMessageWriter extends Thread implements CommonRootIF {
	private int index;
	private CommonProjectInfo commonProjectInfo;
	private MessageExchangeProtocolIF messageProtocol;
	private MessageMangerIF messageManger;
	private DataPacketBufferQueueManagerIF dataPacketBufferQueueManager;
	private ClientResourceManagerIF clientResourceManager;
	private LinkedBlockingQueue<LetterToClient> outputMessageQueue;
	
	// private static DHBServerWriter dhbWriter = DHBServerWriter.getInstance();
	// private static DelimServerWriter delimWriter = DelimServerWriter.getInstance();

	// private ByteBuffer outputMessageBuffer = null;
	// private ByteBuffer outputByteBuffer = null;

	

	/**
	 * 생성자
	 * @param index 순번
	 * @param commonProjectInfo 공통 연결 데이터
	 * @param outputMessageQueue 출력 메시지 큐
	 * @param messageManger 메시지 관리자
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자
	 * @param clientResourceManager 클라이언트 자원 관리자
	 */
	public OutputMessageWriter(int index, 
			CommonProjectInfo commonProjectInfo,
			LinkedBlockingQueue<LetterToClient> outputMessageQueue,
			MessageExchangeProtocolIF messageProtocol,
			MessageMangerIF messageManger,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager,
			ClientResourceManagerIF clientResourceManager) {
		this.index = index;
		this.commonProjectInfo = commonProjectInfo;
		this.messageProtocol = messageProtocol;
		this.messageManger = messageManger;
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		this.clientResourceManager = clientResourceManager;
		this.outputMessageQueue = outputMessageQueue;
	}

	/**
	 * <b>출력메세지 queue</b>로부터 출력 메세지들을 얻어와서 socket channel 쓰기 작업을 수행한다.
	 */
	@Override
	public void run() {
		
		
		

		try {
			
			while (!Thread.currentThread().isInterrupted()) {
				LetterToClient toLetter = null;
				try {
					toLetter = outputMessageQueue.take();
				} catch (InterruptedException e) {
					log.warn(String.format("%s index[%d] stop", commonProjectInfo.projectName, index), e);
					break;
				}

				SocketChannel toSC = toLetter.getToSC();
				OutputMessage outObj = toLetter.getOutputMessage();

				// if (!toSC.isConnected()) continue;

				ClientResource clientResource = clientResourceManager.getClientResource(toSC);
				ByteOrder clientByteOrder = clientResource.getByteOrder();
				Charset clientCharset = clientResource.getCharset();
				
				
				ArrayList<WrapBuffer> outObjWrapBufferList = null;
				
				
				try {
					try {
						outObjWrapBufferList = messageProtocol.M2S(outObj, clientByteOrder, clientCharset);
					} catch (NoMoreDataPacketBufferException e) {
						log.warn("NoMoreDataPacketBufferException", e);
						
						OutputMessage errorOutObj = null;
						try {
							errorOutObj = messageManger.createOutputMessage(
									"SelfExn");
						} catch (MessageInfoNotFoundException e1) {
							log.fatal(	"시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.", e1);
							System.exit(1);
						}
						errorOutObj.messageHeaderInfo = outObj.messageHeaderInfo;
						errorOutObj.setAttribute("whereError", "C");
						errorOutObj.setAttribute("errorGubun", "N");
						errorOutObj.setAttribute("errorMessageID",
								outObj.getMessageID());
						errorOutObj.setAttribute("errorMessage",
								e.getMessage());

						try {
							outObjWrapBufferList = messageProtocol.M2S(errorOutObj, clientByteOrder, clientCharset);
						} catch (NoMoreDataPacketBufferException e1) {
							/**
							 * 대책 없음. 로그만 남기고 종료.
							 */
							String errorMessage = String.format("NoMoreDataPacketBufferException");
							log.warn(errorMessage, e1);
							continue;
						} catch (BodyFormatException e1) {
							/**
							 * 원이 제거 필요함. 로그만 남기고 종료.
							 */
							String errorMessage = String.format("BodyFormatException::errorOutObj=[%s], %s", errorOutObj.toString(), e1.toString());
							log.warn(errorMessage, e1);
							continue;
						}
						
					} catch (BodyFormatException e) {
						log.warn("BodyFormatException", e);
						
						/**
						 * <pre>
						 * 에러를 던지지 않고 직접 SelfExn 메시지를 만드는 이유가 1가지 있다.
						 * (1) 비동기 소켓 채널의 메시지 교환은 입력 메시지를 발생한 지점과 떨어져 있어
						 *     SelfExn 메시지로 보내서 SelfExn 를 받아 보는 순간에 예외로 던지기 위함이다.
						 *     
						 * 참고) 이곳은 죽은 코드이지만 혹여 발생해도 정상 처리 흐름을 따르도록 하였다.
						 * 원본 메시지를 이곳까지 보내기전에 원본 메시지를 랩하는 과정에서 
						 * 먼저 BodyFormatException 가 발생되어 뒷단인 이곳까지 
						 * BodyFormatException을 발생시키는 원본 메시지가 전달되지 않기때문에 이곳 로직은 죽은 코드이다.
						 * </pre>
						 */
						
						OutputMessage errorOutObj = null;
						try {
							errorOutObj = messageManger.createOutputMessage(
									"SelfExn");
						} catch (MessageInfoNotFoundException e1) {
							log.fatal(	"시스템 필수 메시지 정보[SelfExn]가 존재하지 않습니다.", e1);
							System.exit(1);
						}
						errorOutObj.messageHeaderInfo = outObj.messageHeaderInfo;
						errorOutObj.setAttribute("whereError", "C");
						errorOutObj.setAttribute("errorGubun", "B");
						errorOutObj.setAttribute("errorMessageID",
								outObj.getMessageID());
						errorOutObj.setAttribute("errorMessage",
								e.getMessage());

						// LetterFromServer letterFromServer = new
						// LetterFromServer(errorOutObj);
						try {
							outObjWrapBufferList = messageProtocol.M2S(errorOutObj, clientByteOrder, clientCharset);
						} catch (NoMoreDataPacketBufferException e1) {
							/**
							 * 대책 없음. 로그만 남기고 종료.
							 */
							String errorMessage = String.format("NoMoreDataPacketBufferException");
							log.warn(errorMessage, e1);
							continue;
						} catch (BodyFormatException e1) {
							/**
							 * 원이 제거 필요함. 로그만 남기고 종료.
							 */
							String errorMessage = String.format("BodyFormatException::errorOutObj=[%s], %s", errorOutObj.toString(), e1.toString());
							log.warn(errorMessage, e1);
							continue;
						}
					}
					
					int outObjWrapBufferListSize = outObjWrapBufferList.size();
					
					
					// ByteBuffer outObjByteBufferArray[] = new ByteBuffer[outObjWrapBufferListSize];
					
					synchronized (toSC) {
						/**
						 * 2013.07.24 잔존 데이타 발생하므로 GatheringByteChannel 를 이용하는 바이트 버퍼 배열 쓰기 방식 포기.
						 */
						for (int i=0; i < outObjWrapBufferListSize; i++) {
							WrapBuffer wrapBuffer = outObjWrapBufferList.get(i);
							ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();

							do {
								// try {
								toSC.write(byteBuffer);
									/*
								} catch(ClosedByInterruptException e) {
									log.warn("ClosedByInterruptException", e);
									try {
										toSC.write(byteBuffer);
									} catch(ClosedByInterruptException e1) {
										log.fatal("ClosedByInterruptException", e1);
										System.exit(1);
									}
									Thread.currentThread().interrupt();
								}
								*/
							} while(byteBuffer.hasRemaining());
						}
					}
				} catch (NotYetConnectedException e) {
					// ClosedChannelException
					log.warn(String.format("NotYetConnectedException:: %s index[%d] toSC[%d], inObj=[%s]", commonProjectInfo.projectName, index, toSC.hashCode(), outObj.toString()), e);
					try {
						toSC.close();
					} catch (IOException e1) {
					}
				} catch(ClosedByInterruptException e) {
					/** ClosedByInterruptException 는 IOException 상속 받기때문에 따라 처리  */
					log.warn(String.format("ClosedByInterruptException::%s index[%d] toSC[%d] write error",
							commonProjectInfo.projectName, index, toSC.hashCode()), e);
					try {
						toSC.close();
					} catch (IOException e1) {
					}
					
					throw e;
				} catch (IOException e) {
					log.warn(String.format("IOException::%s index[%d] toSC[%d] write error",
							commonProjectInfo.projectName, index, toSC.hashCode()), e);
					
					try {
						toSC.close();
					} catch (IOException e1) {
						
					}
				} finally {
					if (null != outObjWrapBufferList) {
						int bodyWrapBufferListSiz = outObjWrapBufferList.size();
						for (int i=0; i < bodyWrapBufferListSiz; i++) {
							WrapBuffer wrapBuffer = outObjWrapBufferList.get(0);
							outObjWrapBufferList.remove(0);
							dataPacketBufferQueueManager.putDataPacketBuffer(wrapBuffer);
						}
					}
				}
			}
		
			log.warn(String.format("%s index[%d] loop exit", commonProjectInfo.projectName, index));
		} catch(ClosedByInterruptException e) {
			/** 이미 로그를 찍은 상태로 nothing */
		} catch (Exception e) {
			log.warn(String.format("Exception::%s index[%d]", commonProjectInfo.projectName, index), e);
		}
		
		log.warn(String.format("%s index[%d] thread end", commonProjectInfo.projectName, index));
	}
	
	public void finalize() {
		log.warn(String.format("index[%d] 소멸::[%s]", index, toString()));
	}
}
