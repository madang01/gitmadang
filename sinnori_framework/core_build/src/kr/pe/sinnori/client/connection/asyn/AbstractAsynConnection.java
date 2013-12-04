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
package kr.pe.sinnori.client.connection.asyn;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.asyn.threadpool.outputmessage.OutputMessageReaderPoolIF;
import kr.pe.sinnori.client.io.LetterToServer;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.lib.CommonProjectInfo;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.message.OutputMessage;

/**
 * 클라이언트 소켓 채널 블락킹 모드가 넌블락인 비동기 연결 클래스의 부모 추상화 클래스<br/>
 * 참고) 소켓 채널관련 서비스관련 구현을 하는 클래스를 연결 클래스로 명명한다.
 * 
 * @author Jonghoon Won
 */
public abstract class AbstractAsynConnection extends AbstractConnection {
	/** 입력 메시지 큐 */
	protected LinkedBlockingQueue<LetterToServer> inputMessageQueue = null;

	/** 연결 클래스에 제공된 selector 를 가지고 비동기 방식으로 출력 메시지의 소켓 읽기를 시도하는 핸들러 관리자 인터페이스 */
	protected OutputMessageReaderPoolIF outputMessageReaderPool = null;

	/**
	 * 비동기 방식의 소켓 채널의 연결 확립 최대 시도 횟수
	 */
	private int finishConnectMaxCall;
	
	/**
	 * 비동기 방식의 소켓 채널의 연결 확립을 재 시도 간격
	 */
	private long finishConnectWaittingTime;
	
	
	/**
	 * 생성자
	 * @param index  연결 클래스 번호
	 * @param socketTimeOut 자동 접속 여부
	 * @param finishConnectMaxCall 비동기 방식에서 연결 확립 시도 최대 호출 횟수
	 * @param finishConnectWaittingTime 비동기 연결 확립 시도 간격
	 * @param commonProjectInfo 연결 공통 데이터
	 * @param serverOutputMessageQueue 서버에서 보내는 공지등 불특정 다수한테 보내는 출력 메시지 큐
	 * @param inputMessageQueue 입력 메시지 큐
	 * @param outputMessageReaderPool 서버에 접속한 소켓 채널을 균등하게 소켓 읽기 담당 쓰레드에 등록하기 위한 인터페이스
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자
	 * @throws InterruptedException 쓰레드 인터럽트
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼를 할당 받지 못했을 경우 던지는 예외
	 */
	public AbstractAsynConnection(int index, 
			long socketTimeOut,
			boolean whetherToAutoConnect,
			int finishConnectMaxCall,
			long finishConnectWaittingTime,
			CommonProjectInfo commonProjectInfo,
			LinkedBlockingQueue<OutputMessage> serverOutputMessageQueue,
			LinkedBlockingQueue<LetterToServer> inputMessageQueue,
			OutputMessageReaderPoolIF outputMessageReaderPool,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) throws InterruptedException, NoMoreDataPacketBufferException {
		super(index, socketTimeOut, whetherToAutoConnect, 
				commonProjectInfo, dataPacketBufferQueueManager, serverOutputMessageQueue);

		this.finishConnectMaxCall = finishConnectMaxCall;
		this.finishConnectWaittingTime = finishConnectWaittingTime;
		this.inputMessageQueue = inputMessageQueue;
		this.outputMessageReaderPool = outputMessageReaderPool;
		

		/**
		 * <pre>
		 * 비동기 신규 소켓 채널을 서버 접속하기전에 먼저 
		 * 비동기 입출력 지원용 출력 메시지 소켓 읽기 쓰레드에 등록되어야 한다.
		 * 그 동작을 가능하게 해주는것이 outputMessageReaderPool 이다.
		 * 소켓 연결 동작은 이렇게 비동기/동기에 따라 다르게 동작해야 하므로 이 지점에서 소켓 채널 연결 동작을 수행한다.
		 * </pre>
		 */
		if (whetherToAutoConnect) {
			try {
				serverOpen();
			} catch (ServerNotReadyException e) {
				log.fatal(String.format(
						"projectName[%s][%d] ServerNotReadyException in AbstractConnection()",
						commonProjectInfo.projectName, index), e);
				System.exit(1);
			}
		}
	}
	
	/**
	 * <pre>
	 * 비동기 방식의 소켓 채널의 연결을 확정시킨다. 
	 * 환경변수 연결 확립 최대 호출 횟수만큼 환경변수 연결 확립 간격으로 연결을 재시도한다
	 * </pre>
	 * @throws ServerNotReadyException
	 * @throws InterruptedException
	 */
	protected void finishConnect() throws ServerNotReadyException, InterruptedException {
		int callNumberOfFinishConnect = 0;
		try {
			do {
				
				if (callNumberOfFinishConnect >= finishConnectMaxCall) {
					closeServer();
					
					String errorMessage = String
							.format("%s asyn connection[%02d], host[%s], port[%d] 에서 연결 완결 확인 메소드(=finishConnect) 호출 횟수가 최대치 도달했습니다.",
									commonProjectInfo.projectName, index,
									commonProjectInfo.serverHost,
									commonProjectInfo.serverPort);
					log.warn(errorMessage);
					throw new ServerNotReadyException(errorMessage);
				}

				callNumberOfFinishConnect++;
				// log.info("callNumberOfFinishConnect[%d]",
				// callNumberOfFinishConnect);
				Thread.sleep(finishConnectWaittingTime);
			} while (!serverSC.finishConnect());
		} catch (IOException e) {
			closeServer();
			
			
			String errorMessage = String
					.format("%s asyn connection[%02d], host[%s], port[%d] 에서 연결중 에러가 발생하였습니다.",
							commonProjectInfo.projectName, index, commonProjectInfo.serverHost,
							commonProjectInfo.serverPort);
			log.warn(errorMessage, e);
			throw new ServerNotReadyException(errorMessage);
		}
	}

	

	/**
	 * 비동기 방식의 소켓 채널의 신규 생성시 후기 작업을 수행한다. 참고) 비동기 방식의 소켓 채널의 읽기 담당 클래스는 selector
	 * 를 가지고 있는데 여기에 소켓 채널을 등록시켜야 한다.
	 * 
	 * @see OutputMessageReaderPoolIF
	 */
	abstract protected void afterConnectionWork() throws InterruptedException;

	

	/**
	 * 연결 클래스가 가진 출력 메시지 큐로 출력 메시지를 넣는다. 비동기 방식의 소켓 채널에서는 소켓의 읽기/쓰기 담당 클래스가 따로
	 * 있는데, 출력 메시지 큐로
	 * 
	 * @param outObj 
	 */
	abstract public void putToOutputMessageQueue(OutputMessage outObj);
}
