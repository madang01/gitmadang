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
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.SocketResoruceIF;
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
	private SocketResoruceIF syncPrivateSocketResoruce = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	
	private static final int MAILBOX_ID = 1;
	
	/** 메일 식별자 */
	private int mailID = 0;	
	
	/** 큐 등록 상태 */
	private boolean isQueueIn = true;
	
	
	
	public NoShareSyncConnection(String projectName,  
			String host, int port,
			long socketTimeOut,
			boolean whetherToAutoConnect,
			SocketResoruceIF syncPrivateSocketResoruce,
			DataPacketBufferPoolIF dataPacketBufferPool,
			MessageProtocolIF messageProtocol,
			ClientObjectCacheManagerIF clientObjectCacheManager) throws InterruptedException, NoMoreDataPacketBufferException, IOException {
		super(projectName, host, port, socketTimeOut, whetherToAutoConnect, messageProtocol, clientObjectCacheManager);
		
		this.dataPacketBufferPool = dataPacketBufferPool;
		this.syncPrivateSocketResoruce = syncPrivateSocketResoruce;
		
		log.info(String.format("project[%s] NoShareSyncConnection[%d] 생성자 end", projectName, serverSC.hashCode()));
	}
	
	
	
	
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
	protected void openSocketChannel() throws IOException {
		serverSC = SocketChannel.open();
		serverSelectableChannel = serverSC.configureBlocking(true);
		serverSC.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		serverSC.setOption(StandardSocketOptions.TCP_NODELAY, true);
		serverSC.setOption(StandardSocketOptions.SO_LINGER, 0);		

		StringBuilder infoBuilder = null;

		infoBuilder = new StringBuilder("projectName[");
		infoBuilder.append(projectName);
		infoBuilder.append("] asyn+share connection[");
		infoBuilder.append(serverSC.hashCode());
		infoBuilder.append("]");
		log.info(infoBuilder.toString());
	}

	@Override
	protected void doConnect() throws IOException {
		InetSocketAddress remoteAddr = new InetSocketAddress(host, port);
		if (! serverSC.connect(remoteAddr)) {
			String errorMessage = String.format("fail to connect the remote address[host:{}, post:{}]", host, port);
			throw new IOException(errorMessage);
		}
	}
	
	public AbstractMessage sendSyncInputMessage(AbstractMessage inObj)
			throws IOException, 
			NoMoreDataPacketBufferException, BodyFormatException, 
			DynamicClassCallException, ServerTaskException, AccessDeniedException {
		long startTime = 0;
		long endTime = 0;
		startTime = new java.util.Date().getTime();
		
		// String messageID = inObj.getMessageID();
		// LetterFromServer letterFromServer = null;
		

		ClassLoader classLoader = inObj.getClass().getClassLoader();
		inObj.messageHeaderInfo.mailboxID = MAILBOX_ID;
		inObj.messageHeaderInfo.mailID = this.mailID;
		
		boolean isInterrupted = false;
		
		List<WrapBuffer> inObjWrapBufferList = null;
		ArrayList<WrapBuffer> inputStreamWrapBufferList = null;
		AbstractMessage outObj = null;
		
		try {
			inObjWrapBufferList = buildReadableWrapBufferList(classLoader, inObj);
			
			// int inObjWrapBufferListSize = inObjWrapBufferList.size();
			
			/**
			 * 2013.07.24 잔존 데이타 발생하므로 GatheringByteChannel 를 이용하는 바이트 버퍼 배열 쓰기 방식 포기.
			 */
			int numberOfBytesWritten = 0;
			for (WrapBuffer wrapBuffer : inObjWrapBufferList) {
				ByteBuffer inObjBuffer = wrapBuffer.getByteBuffer();
				while (inObjBuffer.hasRemaining()) {
					 
					numberOfBytesWritten = serverSC.write(inObjBuffer);
					if (0 == numberOfBytesWritten) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							log.warn("when the number of bytes written is zero,  this thread must sleep. but it fails.",
									e);
						}
					}
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
					dataPacketBufferPool.putDataPacketBuffer(wrapBuffer);
				}
			}
		}
		
		SocketOutputStream socketOutputStream = syncPrivateSocketResoruce.getSocketOutputStream();
		
		try {
			int numRead = socketOutputStream.read(serverSC);
		
			
			ArrayList<WrapReadableMiddleObject> receivedLetterList = null;
			int receivedLetterListSize = 0;
			while (-1 != numRead) {
				setFinalReadTime();
				
				receivedLetterList = messageProtocol.S2MList(socketOutputStream);
				
				
				receivedLetterListSize = receivedLetterList.size();
				if (receivedLetterListSize != 0) {
					break;
				}

				numRead = socketOutputStream.read(serverSC);
			}			
			
			if (receivedLetterListSize > 1) {
				// FIXME! debug
				int i=0;
				for (WrapReadableMiddleObject receivedLetter : receivedLetterList) {
					outObj = buildOutputMessage(classLoader, receivedLetter);
					
					log.debug(String.format("비공유+동기화 연결에서 1개 이상 출력 메시지 추출 에러, outObj[%d]=[%s]", i++, outObj.toString()));
				}
				
				String errorMessage = "비공유 + 동기 연결 클래스는 오직 1개 입력 메시지에 1개 출력 메시지만 처리할 수 있습니다. 2개 이상 출력 메시지 검출 되었습니다.";
				throw new HeaderFormatException(errorMessage);
			}
			
			WrapReadableMiddleObject  receivedLetter  = receivedLetterList.get(0);
			outObj = buildOutputMessage(classLoader, receivedLetter);
			
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
					dataPacketBufferPool.putDataPacketBuffer(outputMessageWrapBuffer);
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

		
		if (null == outObj) {
			log.info(String.format("outObj is null, isConnected[%s]", serverSC.isConnected()));
		}

		return outObj;
	}	
	
	public void sendAsynInputMessage(AbstractMessage inObj) 
			throws IOException, 
			NoMoreDataPacketBufferException, BodyFormatException, 
			DynamicClassCallException, NotSupportedException {		
		throw new NotSupportedException("비공유+동기 연결은 출력 메시지를 받지 않고 입력 메시지만 보내는 기능을 지원하지 않습니다.");
	}
	
	@Override
	public void finalize() {
		// MessageInputStreamResource messageInputStreamResource = getMessageInputStreamResource();
		try {
			serverSC.close();
		} catch (IOException e) {
		}
		
		syncPrivateSocketResoruce.releaseSocketResources();
		
		log.warn(String.format("소멸::[%s]", toString()));
	}


	public void releaseSocketResources() {
		syncPrivateSocketResoruce.releaseSocketResources();
	}

}
