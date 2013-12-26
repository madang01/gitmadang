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
package kr.pe.sinnori.client.connection.sync;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.lib.CommonProjectInfo;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.message.OutputMessage;

/**
 * 클라이언트 소켓 채널 블락킹 모드가 블락인 동기 연결 클래스의 부모 추상화 클래스<br/>
 * 참고)  소켓 채널을 감싸아 소켓 채널관련 서비스를 구현하는 클래스, 즉 소켓 채널 랩 클래스를 연결 클래스로 명명한다.
 * 
 * @author Jonghoon Won
 * 
 */
public abstract class AbstractSyncConnection extends AbstractConnection {

	protected InputStream inputStream = null;
	
	/**
	 * 생성자
	 * @param index 연결 클래스 번호
	 * @param socketTimeOut 소켓 타임 아웃
	 * @param whetherToAutoConnect 자동 접속 여부
	 * @param commonProjectInfo 연결 공통 데이터
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자
	 * @param serverOutputMessageQueue 서버에서 보내는 불특정 출력 메시지를 받는 큐
	 * @throws InterruptedException 쓰레드 인터럽트
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼를 할당 받지 못했을 경우 던지는 예외
	 */
	public AbstractSyncConnection(int index, 
			long socketTimeOut,
			boolean whetherToAutoConnect,
			CommonProjectInfo commonProjectInfo,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager,
			LinkedBlockingQueue<OutputMessage> serverOutputMessageQueue) throws InterruptedException, NoMoreDataPacketBufferException {
		super(index, socketTimeOut, whetherToAutoConnect, commonProjectInfo, dataPacketBufferQueueManager, serverOutputMessageQueue);
		// log.info("whether_to_auto_connect=[%s]", whether_to_auto_connect);
		/**
		 * <pre> 
		 * 비동기 방식의 경우 신규 소켓 채널을 서버 접속하기전에 먼저 
		 * 비동기 입출력 지원용 출력 메시지 소켓 읽기 쓰레드에 등록되어야 한다.
		 * 소켓 연결 동작은 이렇게 비동기/동기에 따라 다르게 동작해야 하므로 이 지점에서 소켓 채널 연결 동작을 수행한다.
		 * </pre>
		 */
		if (whetherToAutoConnect) {
			try {
				serverOpen();
			} catch (ServerNotReadyException e) {
				log.fatal(String.format("projectName[%s][%d] ServerNotReadyException in AbstractConnection()",
						commonProjectInfo.projectName, index), e);
				System.exit(1);
			}
		}
	}

	
	@Override
	public void serverOpen() throws ServerNotReadyException {
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
			// log.info("open start");
			//synchronized (monitor) {
			
			InetSocketAddress remoteAddr = new InetSocketAddress(
					commonProjectInfo.serverHost,
					commonProjectInfo.serverPort);
			serverSC = SocketChannel.open();
			finalReadTime = new java.util.Date();
			
			infoStringBuilder.append("new serverSC[");
			infoStringBuilder.append(serverSC.hashCode());
			infoStringBuilder.append("] ");
			
			log.info(infoStringBuilder.toString());
			
			
			serverSC.configureBlocking(true);
			serverSC.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
			serverSC.setOption(StandardSocketOptions.TCP_NODELAY, true);
			serverSC.setOption(StandardSocketOptions.SO_LINGER, 0);

			Socket sc = serverSC.socket();

			// sc.setKeepAlive(true);
			// sc.setTcpNoDelay(true);
			sc.setSoTimeout((int) socketTimeOut);
			/**
			 * 주의할것 : serverSC.connect(remoteAddr); 는 무조건 블락되어 사용할 수 없음.
			 * 아래처럼 사용해야 타임아웃 걸림.
			 */
			// log.info("111111 socketTimeOut=[%d]", socketTimeOut);
			sc.connect(remoteAddr, (int) socketTimeOut);

			initSocketResource();
			
			/** 소켓 스트림은 소켓 연결후에 만들어야 한다. */
			try {
				if (null != inputStream) {
					inputStream.close();
				}
			} catch (IOException e) {
			}
			
			try {
				this.inputStream = sc.getInputStream();
			} catch (IOException e) {
				try {
					serverSC.close();
				} catch (IOException e1) {
				}
				throw e;
			}
			
			
			//}
		} catch (ConnectException e) {
			
			throw new ServerNotReadyException(String.format(
					"ConnectException::%s conn index[%02d], host[%s], port[%d]",
					commonProjectInfo.projectName, index, commonProjectInfo.serverHost,
					commonProjectInfo.serverPort));
		} catch (UnknownHostException e) {
			throw new ServerNotReadyException(String.format(
					"UnknownHostException::%s conn index[%02d], host[%s], port[%d]",
					commonProjectInfo.projectName, index, commonProjectInfo.serverHost,
					commonProjectInfo.serverPort));
		} catch (ClosedChannelException e) {
			throw new ServerNotReadyException(
					String.format(
							"ClosedChannelException::%s conn index[%02d], host[%s], port[%d]",
							commonProjectInfo.projectName, index, commonProjectInfo.serverHost,
							commonProjectInfo.serverPort));
		} catch (IOException e) {
			closeServer();
			
			throw new ServerNotReadyException(String.format(
					"IOException::%s conn index[%02d], host[%s], port[%d]",
					commonProjectInfo.projectName, index, commonProjectInfo.serverHost,
					commonProjectInfo.serverPort));
		} catch (Exception e) {
			closeServer();
			
			log.warn("unknown error", e);
			throw new ServerNotReadyException(
					String.format(
							"unknown::%s conn index[%02d], host[%s], port[%d]",
							commonProjectInfo.projectName, index, commonProjectInfo.serverHost,
							commonProjectInfo.serverPort));
		}

		

		// log.info("projectName[%s%02d] call serverOpen end", projectName,
		// index);
	}	
}
