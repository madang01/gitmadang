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
package kr.pe.sinnori.client.connection.sync.noshare;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.client.connection.sync.AbstractSyncConnection;
import kr.pe.sinnori.client.io.LetterFromServer;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.io.MessageExchangeProtocolIF;
import kr.pe.sinnori.common.lib.CommonProjectInfo;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;

/**
 * 클라이언트 비공유 방식의 동기 연결 클래스.<br/>
 * 참고) 비공유 방식은 큐로 관리 되기에 비공유 방식의 비동기 연결 클래스는 큐 인터페이스를 구현하고 있다<br/>
 * 참고)  소켓 채널을 감싸아 소켓 채널관련 서비스를 구현하는 클래스, 즉 소켓 채널 랩 클래스를 연결 클래스로 명명한다.
 * 
 * @author Jonghoon Won
 * 
 */
public class NoShareSyncConnection extends AbstractSyncConnection {
	/** 개인 메일함 번호. 참고) 메일함을 가상으로 운영하여 전체적으로 메일함 운영의 틀을 유지한다. */
	private final static int mailboxID = 1;
	/** 메일 식별자 */
	private int mailID = 0;
	
	
	private MessageMangerIF messageManger = null;
	
	
	private MessageExchangeProtocolIF messageProtocol = null;
	

	
	
	/** 큐 등록 상태 */
	private boolean isQueueIn = true;
	
	
	
	

	/**
	 * 생성자
	 * @param index 연결 클래스 번호
	 * @param commonProjectInfo 연결 공통 데이터
	 * @param serverOutputMessageQueue 서버에서 보내는 공지등 불특정 다수한테 보내는 출력 메시지 큐
	 * @param messageProtocol 메시지 교환 프로토콜
	 * @param messageManger 메시지 관리자
	 * @param dataPacketBufferQueueManager 클라이언트 프로젝트에 종속적인 데이터 패킷 버퍼 관리자
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 부족시 던지는 예외
	 */
	public NoShareSyncConnection(int index, 
			long socketTimeOut,
			boolean whetherToAutoConnect,
			CommonProjectInfo commonProjectInfo,
			LinkedBlockingQueue<OutputMessage> serverOutputMessageQueue,
			MessageExchangeProtocolIF messageProtocol,
			MessageMangerIF messageManger, 
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) throws InterruptedException, NoMoreDataPacketBufferException {
		super(index, socketTimeOut, whetherToAutoConnect, commonProjectInfo, dataPacketBufferQueueManager, serverOutputMessageQueue);
		
		this.messageProtocol = messageProtocol;
		this.messageManger = messageManger;
	}
	
	/**
	 * 연결 클래스가 큐로 관리될때 큐 속에 있는지 여부 반환 메소드
	 * 
	 * @return 연결 클래스가 큐로 관리될때 큐 속에 있는지 여부
	 */
	public boolean isInQueue() {
		// return lastCaller.equals("");
		return isQueueIn;
	}

	/**
	 * 큐 속에 들어갈때 상태 변경 메소드
	 */
	public void queueIn() {
		isQueueIn = true;
	}

	/**
	 * 큐 밖으로 나갈때 상태 변경 메소드
	 */
	public void queueOut() {
		isQueueIn = false;
	}

	@Override
	public LetterFromServer sendInputMessage(InputMessage inObj)
			throws ServerNotReadyException, SocketTimeoutException,
			NoMoreDataPacketBufferException, BodyFormatException, MessageInfoNotFoundException {
		long startTime = 0;
		long endTime = 0;
		startTime = new java.util.Date().getTime();
		
		// String messageID = inObj.getMessageID();
		LetterFromServer letterFromServer = null;
		
		serverOpen();
		

		inObj.messageHeaderInfo.mailboxID = mailboxID;
		inObj.messageHeaderInfo.mailID = mailID;
		

		// MessageHeader messageHeader = new MessageHeader();

		boolean isInterrupted = false;
		
		
		
		ArrayList<WrapBuffer> inObjWrapBufferList = null;
		ArrayList<WrapBuffer> inputStreamWrapBufferList = null;
		
		try {
			inObjWrapBufferList = messageProtocol.M2S(inObj, commonProjectInfo.byteOrderOfProject, commonProjectInfo.charsetOfProject);
			
			int inObjWrapBufferListSize = inObjWrapBufferList.size();
			
			/**
			 * 2013.07.24 잔존 데이타 발생하므로 GatheringByteChannel 를 이용하는 바이트 버퍼 배열 쓰기 방식 포기.
			 */
			for (int i=0; i < inObjWrapBufferListSize; i++) {
				ByteBuffer inObjBuffer = inObjWrapBufferList.get(i).getByteBuffer();
				// log.debug(inObjBuffer.toString());
				do {
					try {
						serverSC.write(inObjBuffer);
					} catch(ClosedByInterruptException e) {
						log.warn("ClosedByInterruptException", e);
						
						try {
							serverSC.write(inObjBuffer);
						} catch(ClosedByInterruptException e1) {
							log.fatal("ClosedByInterruptException", e1);
							System.exit(1);
						}
						
						Thread.currentThread().interrupt();
					}
				} while(inObjBuffer.hasRemaining());
			}

		} catch (NotYetConnectedException e) {
			String errorMessage = String.format("NotYetConnectedException::%s, inObj=[%s]", getSimpleConnectionInfo(), inObj.toString());
			
			// ClosedChannelException
			log.warn(errorMessage, e);
			closeServer();
			
			throw new ServerNotReadyException(errorMessage);
		} catch (IOException e) {
			String errorMessage = String.format("IOException::%s, inObj=[%s]", getSimpleConnectionInfo(), inObj.toString()); 
			// ClosedChannelException
			log.warn(errorMessage, e);
			closeServer();
			
			throw new ServerNotReadyException(errorMessage);
		} finally {
			if (null != inObjWrapBufferList) {
				int inObjWrapBufferListSize = inObjWrapBufferList.size();
				for (int i=0; i < inObjWrapBufferListSize; i++) {
					WrapBuffer wrapBuffer = inObjWrapBufferList.get(0);
					inObjWrapBufferList.remove(0);
					dataPacketBufferQueueManager.putDataPacketBuffer(wrapBuffer);
				}
			}
		}
		
		try {
			
			/** DHB only */
			int numRead = 0;
			// long totalRead = 0;
			// MessageInputStreamResource messageInputStreamResource = getMessageInputStreamResource();
			

			// for(int i=0; i < 1; i++) {
			ByteBuffer lastInputStreamBuffer = messageInputStreamResource.getLastBuffer();
			lastInputStreamBuffer.order(commonProjectInfo.byteOrderOfProject);
			
			byte recvBytes[] = lastInputStreamBuffer.array();
			
			numRead = inputStream.read(recvBytes,
					lastInputStreamBuffer.position(),
					lastInputStreamBuffer.remaining());
			
			// log.debug(String.format("1.numRead[%d]", numRead));
			
			ArrayList<AbstractMessage> outputMessageList = null;
			int outputMessageListSize = 0;
			while (-1 != numRead) {
				// totalRead += numRead;
				// if (numRead == 0) System.exit(1);
				log.debug(String.format("2.numRead[%d], lastInputStreamBuffer=[%s]", numRead, lastInputStreamBuffer.toString()));
				
				lastInputStreamBuffer.position(lastInputStreamBuffer.position()+numRead);
				
				setFinalReadTime();
				
				outputMessageList = messageProtocol.S2MList(OutputMessage.class,  
						commonProjectInfo.charsetOfProject, messageInputStreamResource, messageManger);
				
				
				outputMessageListSize = outputMessageList.size();
				if (outputMessageListSize != 0) break;
				
				lastInputStreamBuffer = messageInputStreamResource.getLastBuffer();
				recvBytes = lastInputStreamBuffer.array();
				
				numRead = inputStream.read(recvBytes,
						lastInputStreamBuffer.position(),
						lastInputStreamBuffer.remaining());
			}

			if (outputMessageListSize > 1) {
				for (int i=0; i < outputMessageListSize; i++) {
					log.debug(String.format("비공유+동기화 연결에서 1개 이상 출력 메시지 추출 에러, outObj[%d]=[%s]", i, outputMessageList.get(i).toString()));
				}
				
				String errorMessage = "비공유 + 동기 연결 클래스는 오직 1개 입력 메시지에 1개 출력 메시지만 처리할 수 있습니다. 2개 이상 출력 메시지 검출 되었습니다.";
				throw new HeaderFormatException(errorMessage);
			}
			
			
			OutputMessage outObj = (OutputMessage)outputMessageList.get(0);
				// log.info(OutObj.toString());
			letterFromServer = new LetterFromServer(outObj);
			
			// }
			
		} catch (HeaderFormatException e) {
			log.warn(String.format("HeaderFormatException::%s", e.getMessage()), e);
			closeServer();
		} catch (SocketTimeoutException e) {
			log.warn(String.format("SocketTimeoutException, inObj=[%s]",
					inObj.toString()), e);
			/**
			 * <pre>
			 * blocking mode 이므로 소켓 타임 아웃 걸리면 소켓을 닫을 수 밖에 없다. 
			 * 소켓 입력 스트림에서 다음 메시지를 추출할 방법이 없기때문이다.
			 * 참고) none-blocking mode 에서는 입/출력을 담당하는 쓰레드가 따로 있고 
			 * SocketTimeoutException은 입/출력 큐에 타임 아웃 시간을 
			 * 적용한 것 이기때문에 소켓을 닫을 필요는 없다.
			 * </pre>
			 */
			closeServer();
			throw e;
		} catch (IOException e) {
			log.fatal("IOException", e);
			closeServer();
			// System.exit(1);
		} finally {
			
			if (null != inputStreamWrapBufferList) {
				int inputStreamWrapBufferListSize = inputStreamWrapBufferList.size();
				for (int i=1; i < inputStreamWrapBufferListSize; i++) {
					WrapBuffer outputMessageWrapBuffer = inputStreamWrapBufferList.get(0);
					inputStreamWrapBufferList.remove(0);
					dataPacketBufferQueueManager.putDataPacketBuffer(outputMessageWrapBuffer);
				}
	
				WrapBuffer outputMessageWrapBuffer = inputStreamWrapBufferList.get(0);
				outputMessageWrapBuffer.getByteBuffer().clear();
			}
			
			if (Integer.MAX_VALUE == mailID)
				mailID = Integer.MIN_VALUE;
			else
				mailID++;
			
			if (isInterrupted) Thread.currentThread().interrupt();
			
			endTime = new java.util.Date().getTime();
			log.info(String.format("sendInputMessage 시간차=[%d]", (endTime - startTime)));
		}

		// FIXME!
		if (null == letterFromServer) {
			log.info(String.format("letterFromServer is null, serverSC isConnected[%s]", serverSC.isConnected()));
		}
		

		return letterFromServer;
	}


	
	@Override
	public void finalize() {
		// MessageInputStreamResource messageInputStreamResource = getMessageInputStreamResource();
		messageInputStreamResource.destory();
		
		log.warn(String.format("소멸::[%s]", toString()));
	}

}
