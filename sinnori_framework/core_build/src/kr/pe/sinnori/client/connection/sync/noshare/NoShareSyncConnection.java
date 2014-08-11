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
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.connection.sync.AbstractSyncConnection;
import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerExcecutorException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.ReceivedLetter;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;

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
	private final static int ONLY_ONE_MAILBOX_ID = 1;
	/** 메일 식별자 */
	private int mailID = 0;	
	
	/** 큐 등록 상태 */
	private boolean isQueueIn = true;
	
	private Socket serverSocket = null;
	
	

	/**
	 * 생성자
	 * @param index 연결 클래스 번호
	 * @param clientProjectConfig 프로젝트의 공통 포함 클라이언트 환경 변수 접근 인터페이스
	 * @param serverOutputMessageQueue 서버에서 보내는 공지등 불특정 다수한테 보내는 출력 메시지 큐
	 * @param messageProtocol 메시지 교환 프로토콜
	 * @param dataPacketBufferQueueManager 클라이언트 프로젝트에 종속적인 데이터 패킷 버퍼 관리자
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 부족시 던지는 예외
	 */
	public NoShareSyncConnection(int index, 
			long socketTimeOut,
			boolean whetherToAutoConnect,
			ClientProjectConfig clientProjectConfig,
			LinkedBlockingQueue<ReceivedLetter> serverOutputMessageQueue,
			MessageProtocolIF messageProtocol,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager,
			ClientObjectCacheManagerIF clientObjectCacheManager) throws InterruptedException, NoMoreDataPacketBufferException {
		super(index, socketTimeOut, whetherToAutoConnect, clientProjectConfig, serverOutputMessageQueue, messageProtocol, dataPacketBufferQueueManager, clientObjectCacheManager);
		
		this.messageProtocol = messageProtocol;
		
		try {
			reopenSocketChannel();
		} catch (IOException e) {
			String errorMessage = String.format("project[%s] NoShareSyncConnection[%d], fail to config a socket channel", clientProjectConfig.getProjectName(), index);
			log.error(errorMessage, e);
			System.exit(1);
		}
		
		/**
		 * 연결 종류별로 설정이 모두 다르다 따라서 설정 변수 "소켓 자동접속 여부"에 따른 서버 연결은 연결별 설정후 수행해야 한다.
		 */
		if (whetherToAutoConnect) {
			try {
				serverOpen();
			} catch (ServerNotReadyException e) {
				log.warn(String.format("project[%s] NoShareSyncConnection[%d] fail to connect server", clientProjectConfig.getProjectName(), index), e);
				// System.exit(1);
			}
		}
		
		log.info(String.format("project[%s] NoShareSyncConnection[%d] 생성자 end", clientProjectConfig.getProjectName(), index));
	}
	
	/**
	 * 비공유 + 동기 연결 전용 소켓 채널 열기
	 * @throws IOException 소켓 채널을 개방할때 혹은 비공유 + 동기 에 맞도록 설정할때 에러 발생시 던지는 예외 
	 */
	private void reopenSocketChannel() throws IOException {
		serverSC = SocketChannel.open();
		serverSC.configureBlocking(true);
		serverSC.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		serverSC.setOption(StandardSocketOptions.TCP_NODELAY, true);
		serverSC.setOption(StandardSocketOptions.SO_LINGER, 0);
		// serverSC.setOption(StandardSocketOptions.SO_SNDBUF, 65536);
		// serverSC.setOption(StandardSocketOptions.SO_RCVBUF, 65536);
		// serverSC.setOption(StandardSocketOptions.SO_RCVBUF, clientProjectConfig.getDataPacketBufferSize()*2);
		
		serverSocket = serverSC.socket();
		// serverSocket.setKeepAlive(true);
		// serverSocket.setTcpNoDelay(true);
		serverSocket.setSoTimeout((int) socketTimeOut);
		
		serverSocket = serverSC.socket();
		serverSocket.setSoTimeout((int) socketTimeOut);
		
		StringBuilder infoBuilder = null;
		
		infoBuilder = new StringBuilder("projectName[");
		infoBuilder.append(clientProjectConfig.getProjectName());
		infoBuilder.append("] sync+noshare connection[");
		infoBuilder.append(index);
		infoBuilder.append("]");		
		log.info(infoBuilder.append(" (re)open new serverSC=").append(serverSC.hashCode()).toString());
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
	public void serverOpen() throws ServerNotReadyException {
		// log.info("projectName[%s%02d] call serverOpen start", projectName,
		// index);
		/**
		 * <pre>
		 * 서버와 연결하는 시간보다 연결 이후 시간이 훨씬 길기때문에,
		 * 연결시 비용이 더 들어가도 연결후 비용을 더 줄일 수 만 있다면 좋을 것이다.
		 * 아래와 같이 재연결을 판단하는 if 문을 2번 사용하여 이를 달성한다.
		 * 그러면 소켓 채널이 서버와 연결된 후에는 동기화 비용 없어지고
		 * 연결 할때만 if 문 중복 비용만 더 추가될 것이다.
		 * </pre>
		 */
		
		 
		
	
		try {
			// log.info("open start");
			// synchronized (monitor) {
				// (재)연결 판단 로직, 2번이상 SocketChannel.open() 호출하는것을 막는 역활을 한다.
				if (serverSC.isConnected()) {
					//log.info(new StringBuilder(info).append(" connected").toString());
					return;
				}
				
				//log.info(new StringBuilder(info).append(" before connect").toString());
				
				finalReadTime = new java.util.Date();
				
				InetSocketAddress remoteAddr = new InetSocketAddress(
						clientProjectConfig.getServerHost(),
						clientProjectConfig.getServerPort());
				/**
				 * 주의할것 : serverSC.connect(remoteAddr); 는 무조건 블락되어 사용할 수 없음.
				 * 아래처럼 사용해야 타임아웃 걸림.
				 */
				// log.info("111111 socketTimeOut=[%d]", socketTimeOut);
				if (!serverSC.isOpen()) {
					reopenSocketChannel();
				}
				serverSocket.connect(remoteAddr, (int) socketTimeOut);
	
				initSocketResource();
				
				/** 소켓 스트림은 소켓 연결후에 만들어야 한다. */
				try {
					if (null != inputStream) {
						inputStream.close();
					}
				} catch (IOException e) {
				}
				
				try {
					this.inputStream = serverSocket.getInputStream();
				} catch (IOException e) {
					try {
						serverSC.close();
					} catch (IOException e1) {
					}
					throw e;
				}
				//log.info(new StringBuilder(info).append(" after connect").toString());
			//}
		} catch (ConnectException e) {
			throw new ServerNotReadyException(String.format(
					"ConnectException::%s conn index[%02d], host[%s], port[%d]",
					clientProjectConfig.getProjectName(), index, clientProjectConfig.getServerHost(),
					clientProjectConfig.getServerPort()));
		} catch (UnknownHostException e) {
			throw new ServerNotReadyException(String.format(
					"UnknownHostException::%s conn index[%02d], host[%s], port[%d]",
					clientProjectConfig.getProjectName(), index, clientProjectConfig.getServerHost(),
					clientProjectConfig.getServerPort()));
		} catch (ClosedChannelException e) {
			throw new ServerNotReadyException(
					String.format(
							"ClosedChannelException::%s conn index[%02d], host[%s], port[%d]",
							clientProjectConfig.getProjectName(), index, clientProjectConfig.getServerHost(),
							clientProjectConfig.getServerPort()));
		} catch (IOException e) {
			serverClose();
			
			throw new ServerNotReadyException(String.format(
					"IOException::%s conn index[%02d], host[%s], port[%d]",
					clientProjectConfig.getProjectName(), index, clientProjectConfig.getServerHost(),
					clientProjectConfig.getServerPort()));
		} catch (Exception e) {
			serverClose();
			
			log.warn("unknown error", e);
			throw new ServerNotReadyException(
					String.format(
							"unknown::%s conn index[%02d], host[%s], port[%d]",
							clientProjectConfig.getProjectName(), index, clientProjectConfig.getServerHost(),
							clientProjectConfig.getServerPort()));
		}
		// log.info("projectName[%s%02d] call serverOpen end", projectName,
		// index);
	}	

	
	@Override
	public AbstractMessage sendSyncInputMessage(AbstractMessage inObj)
			throws ServerNotReadyException, SocketTimeoutException,
			NoMoreDataPacketBufferException, BodyFormatException, 
			DynamicClassCallException, ServerExcecutorException, NotLoginException {
		long startTime = 0;
		long endTime = 0;
		startTime = new java.util.Date().getTime();
		
		// String messageID = inObj.getMessageID();
		// LetterFromServer letterFromServer = null;
		
		serverOpen();

		ClassLoader classLoader = inObj.getClass().getClassLoader();
		inObj.messageHeaderInfo.mailboxID = NoShareSyncConnection.ONLY_ONE_MAILBOX_ID;
		inObj.messageHeaderInfo.mailID = this.mailID;
		
		boolean isInterrupted = false;
		
		ArrayList<WrapBuffer> inObjWrapBufferList = null;
		ArrayList<WrapBuffer> inputStreamWrapBufferList = null;
		AbstractMessage outObj = null;
		
		try {
			inObjWrapBufferList = getWrapBufferList(classLoader, inObj);
			
			// int inObjWrapBufferListSize = inObjWrapBufferList.size();
			
			/**
			 * 2013.07.24 잔존 데이타 발생하므로 GatheringByteChannel 를 이용하는 바이트 버퍼 배열 쓰기 방식 포기.
			 */
			/*for (int i=0; i < inObjWrapBufferListSize; i++) {
				ByteBuffer inObjBuffer = inObjWrapBufferList.get(i).getByteBuffer();*/
			for (WrapBuffer wrapBuffer : inObjWrapBufferList) {
				ByteBuffer inObjBuffer = wrapBuffer.getByteBuffer();
				// log.debug(inObjBuffer.toString());
				do {
					try {
						serverSC.write(inObjBuffer);
					} catch(ClosedByInterruptException e) {
						log.warn("ClosedByInterruptException", e);
						
						try {
							serverSC.write(inObjBuffer);
						} catch(ClosedByInterruptException e1) {
							log.error("ClosedByInterruptException", e1);
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
			serverClose();
			
			throw new ServerNotReadyException(errorMessage);
		} catch (IOException e) {
			String errorMessage = String.format("IOException::%s, inObj=[%s]", getSimpleConnectionInfo(), inObj.toString()); 
			// ClosedChannelException
			log.warn(errorMessage, e);
			serverClose();
			
			throw new ServerNotReadyException(errorMessage);
		} catch (DynamicClassCallException e) {
			String errorMessage = String.format(
					"DynamicClassCallException::inObj=[%s]",
					inObj.toString());
			log.warn(errorMessage, e);
			
			SelfExn selfExn = new SelfExn();

			selfExn.messageHeaderInfo = inObj.messageHeaderInfo;
			
			selfExn.setErrorWhere("C");
			selfExn.setErrorGubun("D");
			selfExn.setErrorMessageID(inObj.getMessageID());
			selfExn.setErrorWhere(e.getMessage());
			
			//letterFromServer = new LetterFromServer(selfExn);
			return selfExn;
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
			ByteBuffer lastInputStreamBuffer = messageInputStreamResource.getLastDataPacketBuffer();
			lastInputStreamBuffer.order(clientProjectConfig.getByteOrder());
			
			byte recvBytes[] = lastInputStreamBuffer.array();
			
			numRead = inputStream.read(recvBytes,
					lastInputStreamBuffer.position(),
					lastInputStreamBuffer.remaining());
			
			// log.debug(String.format("1.numRead[%d]", numRead));
			
			ArrayList<ReceivedLetter> receivedLetterList = null;
			int receivedLetterListSize = 0;
			while (-1 != numRead) {
				// totalRead += numRead;
				// if (numRead == 0) System.exit(1);
				log.debug(String.format("2.numRead[%d], lastInputStreamBuffer=[%s]", numRead, lastInputStreamBuffer.toString()));
				
				lastInputStreamBuffer.position(lastInputStreamBuffer.position()+numRead);
				
				setFinalReadTime();
				
				
				// outputMessageList = messageProtocol.S2MList(clientProjectConfig.getCharset(), messageInputStreamResource);
				receivedLetterList = messageProtocol.S2MList(clientProjectConfig.getCharset(), messageInputStreamResource);
				
				
				receivedLetterListSize = receivedLetterList.size();
				if (receivedLetterListSize != 0) break;
				
				lastInputStreamBuffer = messageInputStreamResource.getLastDataPacketBuffer();
				recvBytes = lastInputStreamBuffer.array();
				
				numRead = inputStream.read(recvBytes,
						lastInputStreamBuffer.position(),
						lastInputStreamBuffer.remaining());
			}			
			
			if (receivedLetterListSize > 1) {
				// FIXME! debug
				int i=0;
				for (ReceivedLetter receivedLetter : receivedLetterList) {
					outObj = getMessageFromMiddleReadObj(classLoader, receivedLetter);
					
					log.debug(String.format("비공유+동기화 연결에서 1개 이상 출력 메시지 추출 에러, outObj[%d]=[%s]", i++, outObj.toString()));
				}
				
				String errorMessage = "비공유 + 동기 연결 클래스는 오직 1개 입력 메시지에 1개 출력 메시지만 처리할 수 있습니다. 2개 이상 출력 메시지 검출 되었습니다.";
				throw new HeaderFormatException(errorMessage);
			}
			
			ReceivedLetter  receivedLetter  = receivedLetterList.get(0);
			outObj = getMessageFromMiddleReadObj(classLoader, receivedLetter);
			// AbstractMessage outObj = outputMessageList.get(0);
				// log.info(OutObj.toString());
			// letterFromServer = new LetterFromServer(outObj);
			
			// }
			
		} catch (HeaderFormatException e) {
			log.warn(String.format("HeaderFormatException::%s", e.getMessage()), e);
			serverClose();
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
			serverClose();
			throw e;
		} catch (IOException e) {
			log.error("IOException", e);
			serverClose();
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
			log.info(String.format("시간차=[%d]", (endTime - startTime)));
		}

		// FIXME!
		if (null == outObj) {
			log.info(String.format("outObj is null, serverSC isConnected[%s]", serverSC.isConnected()));
		}
		

		return outObj;
	}
	
	
	@Override
	public void sendAsynInputMessage(AbstractMessage inObj) 
			throws ServerNotReadyException, SocketTimeoutException, 
			NoMoreDataPacketBufferException, BodyFormatException, 
			DynamicClassCallException, NotSupportedException {		
		throw new NotSupportedException("비공유+동기 연결은 출력 메시지를 받지 않고 입력 메시지만 보내는 기능을 지원하지 않습니다.");
	}
	
	@Override
	public void finalize() {
		// MessageInputStreamResource messageInputStreamResource = getMessageInputStreamResource();
		messageInputStreamResource.destory();
		
		log.warn(String.format("소멸::[%s]", toString()));
	}

}
