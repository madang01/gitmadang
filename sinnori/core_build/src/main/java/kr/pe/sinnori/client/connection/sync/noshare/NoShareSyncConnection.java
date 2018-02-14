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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.common.type.SelfExn;
import kr.pe.sinnori.impl.message.SelfExnRes.SelfExnRes;

/**
 * 클라이언트 비공유 방식의 동기 연결 클래스.<br/>
 * 참고) 비공유 방식은 큐로 관리 되기에 비공유 방식의 비동기 연결 클래스는 큐 인터페이스를 구현하고 있다<br/>
 * 참고)  소켓 채널을 감싸아 소켓 채널관련 서비스를 구현하는 클래스, 즉 소켓 채널 랩 클래스를 연결 클래스로 명명한다.
 * 
 * @author Won Jonghoon
 * 
 */
public class NoShareSyncConnection extends AbstractConnection {
	private Socket serverSocket = null;
	private InputStream inputStreamOfSocket = null;
	private OutputStream outputStreamOfSocket = null;
	
	private static final int MAILBOX_ID = 1;
	
	/** 메일 식별자 */
	private int mailID = 0;	
	
	/** 큐 등록 상태 */
	private boolean isQueueIn = true;
	
	public NoShareSyncConnection(String projectName, int index, 
			String host,
			int port,
			long socketTimeOut,
			boolean whetherToAutoConnect,
			SocketOutputStream socketOutputStream,
			MessageProtocolIF messageProtocol,
			DataPacketBufferPoolIF dataPacketBufferQueueManager,
			ClientObjectCacheManagerIF clientObjectCacheManager) throws InterruptedException, NoMoreDataPacketBufferException {
		super(projectName, index, 
				host, port, socketTimeOut, 
				whetherToAutoConnect, 
				socketOutputStream, messageProtocol,
				dataPacketBufferQueueManager, clientObjectCacheManager);
		
		
		this.messageProtocol = messageProtocol;
		
		/*try {
			reopenSocketChannel();
		} catch (IOException e) {
			String errorMessage = String.format("project[%s] NoShareSyncConnection[%d], fail to config a socket channel", projectName, index);
			log.error(errorMessage, e);
			System.exit(1);
		}*/
		
		/**
		 * 연결 종류별로 설정이 모두 다르다 따라서 설정 변수 "소켓 자동접속 여부"에 따른 서버 연결은 연결별 설정후 수행해야 한다.
		 */
		if (whetherToAutoConnect) {
			try {
				connectServer();
			} catch (IOException e) {
				log.warn(String.format("project[%s] NoShareSyncConnection[%d] fail to connect server", projectName, index), e);
				// System.exit(1);
			}
		}
		
		log.info(String.format("project[%s] NoShareSyncConnection[%d] 생성자 end", projectName, index));
	}
	
	/**
	 * 비공유 + 동기 연결 전용 소켓 채널 열기
	 * @throws IOException 소켓 채널을 개방할때 혹은 비공유 + 동기 에 맞도록 설정할때 에러 발생시 던지는 예외 
	 */
	private void openSyncSocket() throws IOException {
		serverSocket  = new Socket();
		serverSocket.setKeepAlive(true);
		serverSocket.setTcpNoDelay(true);
		serverSocket.setSoTimeout((int) socketTimeOut);
		
		StringBuilder infoBuilder = null;
		
		infoBuilder = new StringBuilder("projectName[");
		infoBuilder.append(projectName);
		infoBuilder.append("] sync+noshare connection[");
		infoBuilder.append(index);
		infoBuilder.append("]");		
		log.info(infoBuilder.append(" (re)open new serverSocket=").append(serverSocket.hashCode()).toString());
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
		log.info("put NoShareSyncConnection[{}] in the connection queue", monitor.hashCode());
	}

	/**
	 * 큐 밖으로 나갈때 상태 변경 메소드
	 */
	public void queueOut() {
		isQueueIn = false;
		log.info("get NoShareSyncConnection[{}] from the connection queue", monitor.hashCode());
	}
	
	@Override
	public void connectServer() throws IOException {
	
		try {
			// log.info("open start");
			// synchronized (monitor) {
			
			if (null == serverSocket) {
				openSyncSocket();
			}
			
			
				// (재)연결 판단 로직, 2번이상 SocketChannel.open() 호출하는것을 막는 역활을 한다.
				if (serverSocket.isConnected()) {
					//log.info(new StringBuilder(info).append(" connected").toString());
					return;
				}
				
				//log.info(new StringBuilder(info).append(" before connect").toString());
				
				finalReadTime = new java.util.Date();
				
				InetSocketAddress remoteAddr = new InetSocketAddress(
						host,
						port);
				
				// log.info("111111 socketTimeOut=[%d]", socketTimeOut);
				
				
				/**
				 * 주의할것 : serverSC.connect(remoteAddr); 는 무조건 블락되어 사용할 수 없음.
				 * 아래처럼 사용해야 타임아웃 걸림.
				 */
				serverSocket.connect(remoteAddr, (int) socketTimeOut);
	
				
				/** 소켓 스트림은 소켓 연결후에 만들어야 한다. */
				try {
					if (null != inputStreamOfSocket  ) {
						inputStreamOfSocket.close();
					}
				} catch (IOException e) {
				}
				
				try {
					this.inputStreamOfSocket  = serverSocket.getInputStream();
				} catch (IOException e) {
					try {
						serverSocket.close();
					} catch (IOException e1) {
					}
					throw e;
				}
				
				
				try {
					if (null != outputStreamOfSocket  ) {
						outputStreamOfSocket.close();
					}
				} catch (IOException e) {
				}
				
				try {
					this.outputStreamOfSocket  = serverSocket.getOutputStream();
				} catch (IOException e) {
					try {
						outputStreamOfSocket.close();
					} catch (IOException e1) {
					}
					throw e;
				}
				
				
				//log.info(new StringBuilder(info).append(" after connect").toString());
			//}
		
		} catch (IOException e) {
			closeSocket();
			
			throw new IOException(String.format(
					"IOException::%s conn index[%02d], host[%s], port[%d]",
					projectName, index, host,
					port));
		} catch (Exception e) {
			closeSocket();
			
			log.warn("unknown error", e);
			throw new IOException(
					String.format(
							"unknown::%s conn index[%02d], host[%s], port[%d]",
							projectName, index, host,
							port));
		}
		// log.info("projectName[%s%02d] call serverOpen end", projectName,
		// index);
	}	

	
	@Override
	public AbstractMessage sendSyncInputMessage(AbstractMessage inObj)
			throws IOException, 
			NoMoreDataPacketBufferException, BodyFormatException, 
			DynamicClassCallException, ServerTaskException, AccessDeniedException {
		long startTime = 0;
		long endTime = 0;
		startTime = new java.util.Date().getTime();
		
		// String messageID = inObj.getMessageID();
		// LetterFromServer letterFromServer = null;
		
		connectServer();

		ClassLoader classLoader = inObj.getClass().getClassLoader();
		inObj.messageHeaderInfo.mailboxID = MAILBOX_ID;
		inObj.messageHeaderInfo.mailID = this.mailID;
		
		boolean isInterrupted = false;
		
		List<WrapBuffer> inObjWrapBufferList = null;
		ArrayList<WrapBuffer> inputStreamWrapBufferList = null;
		AbstractMessage outObj = null;
		
		try {
			inObjWrapBufferList = getWrapBufferListOfInputMessage(classLoader, inObj);
			
			// int inObjWrapBufferListSize = inObjWrapBufferList.size();
			
			/**
			 * 2013.07.24 잔존 데이타 발생하므로 GatheringByteChannel 를 이용하는 바이트 버퍼 배열 쓰기 방식 포기.
			 */
			/*for (int i=0; i < inObjWrapBufferListSize; i++) {
				ByteBuffer inObjBuffer = inObjWrapBufferList.get(i).getByteBuffer();*/
			for (WrapBuffer wrapBuffer : inObjWrapBufferList) {
				ByteBuffer inObjBuffer = wrapBuffer.getByteBuffer();
				
				while (inObjBuffer.hasRemaining()) {
					outputStreamOfSocket.write(inObjBuffer.get());
				}
			}
		
		} catch (IOException e) {
			String errorMessage = String.format("IOException::%s, inObj=[%s]", toString(), inObj.toString()); 
			// ClosedChannelException
			log.warn(errorMessage, e);
			closeSocket();
			
			throw new IOException(errorMessage);
		} catch (DynamicClassCallException e) {
			String errorMessage = String.format(
					"DynamicClassCallException::inObj=[%s]",
					inObj.toString());
			log.warn(errorMessage, e);
			
			SelfExnRes selfExnRes = new SelfExnRes();

			selfExnRes.messageHeaderInfo = inObj.messageHeaderInfo;
			
			selfExnRes.setErrorPlace(SelfExn.ErrorPlace.CLIENT);
			selfExnRes.setErrorType(SelfExn.ErrorType.DynamicClassCallException);
			selfExnRes.setErrorMessageID(inObj.getMessageID());
			selfExnRes.setErrorReason(e.getMessage());
			
			//letterFromServer = new LetterFromServer(selfExn);
			return selfExnRes;
		} finally {
			if (null != inObjWrapBufferList) {
				int inObjWrapBufferListSize = inObjWrapBufferList.size();
				for (int i=0; i < inObjWrapBufferListSize; i++) {
					WrapBuffer wrapBuffer = inObjWrapBufferList.get(0);
					inObjWrapBufferList.remove(0);
					dataPacketBufferPoolManager.putDataPacketBuffer(wrapBuffer);
				}
			}
		}
		
		try {
			int numRead = socketOutputStream.read(serverSocket);
		
			
			ArrayList<WrapReadableMiddleObject> receivedLetterList = null;
			int receivedLetterListSize = 0;
			while (-1 != numRead) {
				setFinalReadTime();
				
				receivedLetterList = messageProtocol.S2MList(socketOutputStream);
				
				
				receivedLetterListSize = receivedLetterList.size();
				if (receivedLetterListSize != 0) {
					break;
				}

				numRead = socketOutputStream.read(serverSocket);
			}			
			
			if (receivedLetterListSize > 1) {
				// FIXME! debug
				int i=0;
				for (WrapReadableMiddleObject receivedLetter : receivedLetterList) {
					outObj = getMessageFromMiddleReadObj(classLoader, receivedLetter);
					
					log.debug(String.format("비공유+동기화 연결에서 1개 이상 출력 메시지 추출 에러, outObj[%d]=[%s]", i++, outObj.toString()));
				}
				
				String errorMessage = "비공유 + 동기 연결 클래스는 오직 1개 입력 메시지에 1개 출력 메시지만 처리할 수 있습니다. 2개 이상 출력 메시지 검출 되었습니다.";
				throw new HeaderFormatException(errorMessage);
			}
			
			WrapReadableMiddleObject  receivedLetter  = receivedLetterList.get(0);
			outObj = getMessageFromMiddleReadObj(classLoader, receivedLetter);
			
		} catch (HeaderFormatException e) {
			log.warn(String.format("HeaderFormatException::%s", e.getMessage()), e);
			
			closeSocket();
		} catch (SocketTimeoutException e) {
			log.warn(String.format("SocketTimeoutException, inObj=[%s]",
					inObj.toString()), e);
			
			closeSocket();
			throw e;
		} catch (IOException e) {
			log.error("IOException", e);
			
			closeSocket();
			// System.exit(1);
		} finally {
			
			
			if (null != inputStreamWrapBufferList) {
				int inputStreamWrapBufferListSize = inputStreamWrapBufferList.size();
				for (int i=1; i < inputStreamWrapBufferListSize; i++) {
					WrapBuffer outputMessageWrapBuffer = inputStreamWrapBufferList.get(0);
					inputStreamWrapBufferList.remove(0);
					dataPacketBufferPoolManager.putDataPacketBuffer(outputMessageWrapBuffer);
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
			log.info(String.format("outObj is null, isConnected[%s]", serverSocket.isConnected()));
		}

		return outObj;
	}
	
	
	
	
	@Override
	public void sendAsynInputMessage(AbstractMessage inObj) 
			throws IOException, 
			NoMoreDataPacketBufferException, BodyFormatException, 
			DynamicClassCallException, NotSupportedException {		
		throw new NotSupportedException("비공유+동기 연결은 출력 메시지를 받지 않고 입력 메시지만 보내는 기능을 지원하지 않습니다.");
	}
	
	@Override
	public void finalize() {
		// MessageInputStreamResource messageInputStreamResource = getMessageInputStreamResource();
		socketOutputStream.close();
		
		log.warn(String.format("소멸::[%s]", toString()));
	}

	@Override
	public boolean isConnected() {
		return serverSocket.isConnected();
	}
	
	public void closeSocket() throws IOException {
		serverSocket.close();
	}

}
