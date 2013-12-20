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
package kr.pe.sinnori.client.connection.asyn.noshare;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import kr.pe.sinnori.client.connection.asyn.AbstractAsynConnection;
import kr.pe.sinnori.client.connection.asyn.share.mailbox.PrivateMailbox;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReaderPoolIF;
import kr.pe.sinnori.client.io.LetterFromServer;
import kr.pe.sinnori.client.io.LetterToServer;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NoMoreOutputMessageQueueException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.lib.CommonProjectInfo;
import kr.pe.sinnori.common.lib.CommonStaticFinal;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.lib.OutputMessageQueueQueueMangerIF;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;

/**
 * 클라이언트 비공유 방식의 비동기 연결 클래스.<br/>
 * 참고) 비공유 방식은 큐로 관리 되기에 비공유 방식의 동기 연결 클래스는 큐 인터페이스를 구현하고 있다<br/>
 * 참고)  소켓 채널을 감싸아 소켓 채널관련 서비스를 구현하는 클래스, 즉 소켓 채널 랩 클래스를 연결 클래스로 명명한다.
 * 
 * @author Jonghoon Won
 * 
 */
public class NoShareAsynConnection extends AbstractAsynConnection {
	// private MessageMangerIF messageManger = null;
	
	/** 개인 메일함 번호. 참고) 메일함을 가상으로 운영하여 전체적으로 메일함 운영의 틀을 유지한다. */
	private static final int mailboxID = 1;
		
	/** 큐 등록 상태 */
	private boolean isQueueIn = true;
	
	private PrivateMailbox mailbox = null;

	/**
	 * 생성자
	 * @param index 연결 클래스 번호
	 * @param commonProjectInfo 연결 공통 데이터
	 * @param serverOutputMessageQueue 서버에서 보내는 공지등 불특정 다수한테 보내는 출력 메시지 큐
	 * @param inputMessageQueue 입력 메시지 큐
	 * @param outputMessageQueueQueueManger 출력 메시지 큐를 원소로 가지는 큐 관리자
	 * @param outputMessageReaderPool 서버에 접속한 소켓 채널을 균등하게 소켓 읽기 담당 쓰레드에 등록하기 위한 인터페이스
	 * @param messageManger 메시지 관리자
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자
	 * @throws InterruptedException 쓰레드 인터럽트
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼를 할당 받지 못했을 경우 던지는 예외
	 * @throws NoMoreOutputMessageQueueException  출력 메시지 큐 부족시 실패시 던지는 예외
	 */
	public NoShareAsynConnection(int index, 
			long socketTimeOut,
			boolean whetherToAutoConnect,
			int finishConnectMaxCall,
			long finishConnectWaittingTime,
			CommonProjectInfo commonProjectInfo,
			LinkedBlockingQueue<OutputMessage> serverOutputMessageQueue,
			LinkedBlockingQueue<LetterToServer> inputMessageQueue,
			OutputMessageQueueQueueMangerIF outputMessageQueueQueueManger, 
			OutputMessageReaderPoolIF outputMessageReaderPool,
			MessageMangerIF messageManger,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) throws InterruptedException, 
			NoMoreDataPacketBufferException, NoMoreOutputMessageQueueException {		
		super(index, socketTimeOut, whetherToAutoConnect, 
				finishConnectMaxCall, finishConnectWaittingTime, 
				commonProjectInfo, serverOutputMessageQueue, inputMessageQueue, 
				outputMessageReaderPool, dataPacketBufferQueueManager);

		// this.messageManger = messageManger;
		// this.outputMessageQueue = outputMessageQueue;
		mailbox = new PrivateMailbox(this, mailboxID, inputMessageQueue, outputMessageQueueQueueManger);
				
		log.info(String.format("create SingleNoneBlockConnection, projectName=[%s][%d]",
				commonProjectInfo.projectName, index));
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
	public void serverOpen() throws ServerNotReadyException, InterruptedException {
		// log.info("projectName[%s%02d] call serverOpen start", projectName,
		// index);
		StringBuilder infoStringBuilder = null;
		
		/**
		 * <pre>
		 * 서버와 연결하는 시간보다 연결 이후 시간이 훨씬 길기때문에,
		 * 연결시 비용이 더 들어가도 연결후 비용을 더 줄일 수 만 있다면 좋을 것이다.
		 * 아래와 같이 재연결을 판단하는 if 문을 2번 사용하여 이를 달성한다.
		 * 그러면 소켓 채널이 서버와 연결된 후에는 동기화 비용 없어지고
		 * 연결 할때만 if 문 중복 비용만 더 추가될 것이다.
		 * </pre>
		 */
		if (null == serverSC) {
			infoStringBuilder = new StringBuilder("projectName[");
			infoStringBuilder.append(commonProjectInfo.projectName);
			infoStringBuilder.append("] asyn connection[");
			infoStringBuilder.append(index);
			infoStringBuilder.append("] ");
		} else if (!serverSC.isConnected()) {
			infoStringBuilder = new StringBuilder("projectName[");
			infoStringBuilder.append(commonProjectInfo.projectName);
			infoStringBuilder.append("] asyn connection[");
			infoStringBuilder.append(index);
			infoStringBuilder.append("] old serverSC[");
			infoStringBuilder.append(serverSC.hashCode());
			infoStringBuilder.append("] ");
		} else {
			return;
		}
		
		try {
			// synchronized (monitor) {
					
				
			serverSC = SocketChannel.open();

			infoStringBuilder.append("new serverSC[");
			infoStringBuilder.append(serverSC.hashCode());
			infoStringBuilder.append("] ");
			
			log.info(infoStringBuilder.toString());
			

			serverSC.configureBlocking(false);
			serverSC.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
			serverSC.setOption(StandardSocketOptions.TCP_NODELAY, true);
			serverSC.setOption(StandardSocketOptions.SO_LINGER, 0);
			
			// sc.setSendBufferSize(io_buffer_size);
			// sc.setReceiveBufferSize(io_buffer_size);
			InetSocketAddress remoteAddr = new InetSocketAddress(
					commonProjectInfo.serverHost,
					commonProjectInfo.serverPort);
			serverSC.connect(remoteAddr);
			finishConnect();
			initSocketResource();
			// log.info("serverSC isConnected=[%s]",
			// serverSC.isConnected());
			afterConnectionWork();
			// }
			

		} catch (ConnectException e) {
			throw new ServerNotReadyException(
					String.format(
							"ConnectException::%s conn index[%02d], host[%s], port[%d]", 
							commonProjectInfo.projectName,
							index, commonProjectInfo.serverHost,
							commonProjectInfo.serverPort));
		} catch (UnknownHostException e) {
			throw new ServerNotReadyException(
					String.format(
							"UnknownHostException::%s conn index[%02d], host[%s], port[%d]", commonProjectInfo.projectName
							, index, commonProjectInfo.serverHost,
							commonProjectInfo.serverPort));
		} catch (ClosedChannelException e) {
			throw new ServerNotReadyException(
					String.format(
							"ClosedChannelException::%s conn index[%02d], host[%s], port[%d]", 
							commonProjectInfo.projectName,
							index, commonProjectInfo.serverHost,
							commonProjectInfo.serverPort));
		} catch (IOException e) {
			try {
				if (null != serverSC) serverSC.close();
			} catch (IOException e1) {
			}
			
			throw new ServerNotReadyException(
					String.format(
							"IOException::%s conn index[%02d], host[%s], port[%d]", 
							commonProjectInfo.projectName,
							index, commonProjectInfo.serverHost,
							commonProjectInfo.serverPort));
		} catch (ServerNotReadyException e) {
			closeServer();
			throw e;
		} catch (InterruptedException e) {
			closeServer();
			
			throw e;
		} catch (Exception e) {
			closeServer();
			
			log.warn("unknown exception", e);
			throw new ServerNotReadyException(String.format(
					"unknown::index[%d], projectName[%s], host[%s], port[%d]",
					index, commonProjectInfo.projectName, commonProjectInfo.serverHost,
					commonProjectInfo.serverPort));
		}

		// log.info("projectName[%s%02d] call serverOpen end", projectName,
		// index);
	}
	

	@Override
	public LetterFromServer sendInputMessage(InputMessage inObj)
			throws ServerNotReadyException, SocketTimeoutException,
			NoMoreDataPacketBufferException, BodyFormatException, MessageInfoNotFoundException {

		long startTime = 0;
		long endTime = 0;
		startTime = new java.util.Date().getTime();

		// log.info("inputMessage=[%s]", inputMessage.toString());

		LetterFromServer letterFromServer = null;
		
		try {
			serverOpen();
		} catch (InterruptedException e2) {
			Thread.currentThread().interrupt();
			return letterFromServer;
		}

				
		boolean isInterrupted = false;

		try {
			mailbox.setActive();

			LetterToServer letterToServer = new LetterToServer(this, inObj);
			try {
				mailbox.putInputMessage(letterToServer);
			} catch (InterruptedException e) {
				isInterrupted = true;
				try {
					mailbox.putInputMessage(letterToServer);
				} catch (InterruptedException e1) {
					log.fatal("인터럽트 받아 후속 처리중 발생", e);
					System.exit(1);
				}
			}
			
			

			OutputMessage workOutObj = null;
			
			try {				
				workOutObj = mailbox.takeOutputMessage();
				
				letterFromServer = new LetterFromServer(workOutObj);
			} catch(InterruptedException e) {
				/** 인터럽트 발생시 메소드 끝가지 로직 수행후 인터럽트 상태를 복귀 시켜 최종 인터럽트 처리를 마무리 하도록 유도 */					
				if (isInterrupted) {
					log.fatal("인터럽트 받아 후속 처리중 발생", e);
					System.exit(1);
				} else {
					try {
						workOutObj = mailbox.takeOutputMessage();
					} catch(InterruptedException e1) {
						log.fatal("인터럽트 받아 후속 처리중 발생", e1);
						System.exit(1);
					}
					isInterrupted = true;
				}
			}
			
		} finally {
			mailbox.setDisable();

			if (isInterrupted)
				Thread.currentThread().interrupt();
		}

		endTime = new java.util.Date().getTime();
		log.info(String.format("sendInputMessage 시간차=[%d]", (endTime - startTime)));

		return letterFromServer;
	}
	
	@Override
	public void sendOnlyInputMessage(
			InputMessage inObj) throws ServerNotReadyException,
			SocketTimeoutException, NoMoreDataPacketBufferException,
			BodyFormatException, MessageInfoNotFoundException, NotSupportedException {
		long startTime = 0;
		long endTime = 0;
		startTime = new java.util.Date().getTime();
		// log.info("inputMessage=[%s]", inputMessage.toString());
		try {
			serverOpen();
		} catch (InterruptedException e2) {
			Thread.currentThread().interrupt();
			return;
		}
		
		boolean isInterrupted = false;

		try {
			mailbox.setActive();

			LetterToServer letterToServer = new LetterToServer(this, inObj);
			try {
				mailbox.putInputMessage(letterToServer);
			} catch (InterruptedException e) {
				isInterrupted = true;
				try {
					mailbox.putInputMessage(letterToServer);
				} catch (InterruptedException e1) {
					log.fatal("인터럽트 받아 후속 처리중 발생", e);
					System.exit(1);
				}
			}
			
		} finally {
			mailbox.setDisable();

			if (isInterrupted)
				Thread.currentThread().interrupt();
		}

		endTime = new java.util.Date().getTime();
		log.info(String.format("sendOnlyInputMessage 시간차=[%d]", (endTime - startTime)));
	}

	@Override
	protected void afterConnectionWork() throws InterruptedException {
		outputMessageReaderPool.addNewServer(this);
	}
	

	@Override
	public void putToOutputMessageQueue(OutputMessage outObj) {
		int mailboxID = outObj.messageHeaderInfo.mailboxID;
		
		if (mailboxID == CommonStaticFinal.SERVER_MAILBOX_ID) {
			/** 서버에서 보내는 공지등 불특정 다수한테 보내는 출력 메시지 */
			boolean result = false;
			try {
				result = serverOutputMessageQueue.offer(outObj, socketTimeOut, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				/**
				 * 인터럽트 발생시 메소드 끝가지 로직 수행후 인터럽트 상태를 복귀 시켜 최종 인터럽트 처리를 마무리 하도록 유도
				 */				
				try {
					result = serverOutputMessageQueue.offer(outObj, socketTimeOut, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e1) {
					log.fatal("인터럽트 받아 후속 처리중 발생", e1);
					System.exit(1);
				}
				Thread.currentThread().interrupt();
			}
			
			if (!result) {
				String errorMsg = String
						.format("서버 공용 출력 메시지 큐 응답 시간[%d]이 초과되었습니다. serverConnection=[%s], mailboxID=[%d], mailID=[%d]",
								socketTimeOut, toString(),
								mailboxID, outObj.messageHeaderInfo.mailID);
				log.warn(errorMsg);
			}
		} else {
			if (isInQueue()) {
				String errorMessage = String
						.format("연결 클래스가 사용중이 아닙니다. 연결 클래스 큐 대기중, serverConnection=[%s], outputMessage=[%s]",
								this.toString(), outObj.toString());

				log.warn(errorMessage);
				return;
			}

			mailbox.putToOutputMessageQueue(outObj);	
		}
	}
	
	
	@Override
	public void finalize() {
		// MessageInputStreamResource messageInputStreamResource = getMessageInputStreamResource();
		messageInputStreamResource.destory();
		log.warn(String.format("SingleNoneBlockConnection 소멸::[%s]", toString()));
	}
}
