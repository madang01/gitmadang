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
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.client.connection.ClientMessageUtilityIF;
import kr.pe.sinnori.client.connection.SocketResoruceIF;
import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.io.FreeSizeInputStream;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.common.type.SelfExn;
import kr.pe.sinnori.impl.message.SelfExnRes.SelfExnRes;


public class SyncPrivateConnection extends AbstractConnection {
	private SocketResoruceIF syncPrivateSocketResoruce = null;
	
	private static final int MAILBOX_ID = 1;
	
	/** 메일 식별자 */
	private int mailID = 0;	
	
	/** 큐 등록 상태 */
	private boolean isQueueIn = true;
	
	
	
	public SyncPrivateConnection(String projectName,  
			String host, int port,
			long socketTimeOut,
			ClientMessageUtilityIF clientMessageUtility,
			SocketResoruceIF syncPrivateSocketResoruce) throws InterruptedException, NoMoreDataPacketBufferException, IOException {
		super(projectName, host, port, socketTimeOut, clientMessageUtility);
		
		this.syncPrivateSocketResoruce = syncPrivateSocketResoruce;
		
		doConnect();
		
		log.info(String.format("project[%s] NoShareSyncConnection[%d] 생성자 end", projectName, serverSC.hashCode()));
	}
	
	
	
	
	public boolean isInQueue() {
		// return lastCaller.equals("");
		return isQueueIn;
	}

	/**
	 * 큐 속에 들어갈때 상태 변경 메소드
	 */
	protected void queueIn() {
		isQueueIn = true;
		log.info("put NoShareSyncConnection[{}] in the connection queue", monitor.hashCode());
	}

	/**
	 * 큐 밖으로 나갈때 상태 변경 메소드
	 */
	protected void queueOut() {
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
			throws InterruptedException, NoMoreDataPacketBufferException,  
			DynamicClassCallException, ServerTaskException, AccessDeniedException, BodyFormatException, IOException {
		long startTime = 0;
		long endTime = 0;
		startTime = new java.util.Date().getTime();
		
		// String messageID = inObj.getMessageID();
		// LetterFromServer letterFromServer = null;
		

		ClassLoader classLoader = inObj.getClass().getClassLoader();
		inObj.messageHeaderInfo.mailboxID = MAILBOX_ID;
		inObj.messageHeaderInfo.mailID = this.mailID;
		
		List<WrapBuffer> warpBufferList = null;
		AbstractMessage outObj = null;
		
		try {
			warpBufferList = clientMessageUtility.buildReadableWrapBufferList(classLoader, inObj);
			
			// int inObjWrapBufferListSize = inObjWrapBufferList.size();
			
			/**
			 * 2013.07.24 잔존 데이타 발생하므로 GatheringByteChannel 를 이용하는 바이트 버퍼 배열 쓰기 방식 포기.
			 */
			int numberOfBytesWritten = 0;
			for (WrapBuffer wrapBuffer : warpBufferList) {
				ByteBuffer byteBufferOfInputMessage = wrapBuffer.getByteBuffer();
				while (byteBufferOfInputMessage.hasRemaining()) {
					 
					numberOfBytesWritten = serverSC.write(byteBufferOfInputMessage);
					if (0 == numberOfBytesWritten) {
						
						Thread.sleep(10);
						
					}
				}
			}
		} catch(InterruptedException e) {
			throw e;
		} catch (Exception e) {
			log.warn("Exception", e);
			
			throw new IOException("fail to write input message to socket channel");
		/*} catch (DynamicClassCallException e) {
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
			return selfExnRes;*/
		} finally {
			clientMessageUtility.releaseWrapBufferList(warpBufferList);
		}
		
		SocketOutputStream socketOutputStream = syncPrivateSocketResoruce.getSocketOutputStream();
	
		try {
			ArrayList<WrapReadableMiddleObject> wrapReadableMiddleObjectList = null;
			int wrapReadableMiddleObjectListSize = 0;
			
			boolean loop = true;
			while (loop) {
				int numRead = socketOutputStream.read(serverSC);
				
				if (numRead == 0) {
					Thread.sleep(10);
					continue;
				}
				
				setFinalReadTime();
				wrapReadableMiddleObjectList = clientMessageUtility.getWrapReadableMiddleObjectList(socketOutputStream);
				wrapReadableMiddleObjectListSize = wrapReadableMiddleObjectList.size();
				if (wrapReadableMiddleObjectListSize > 0) {
					loop = false;
				}
								
			}
			
			List<AbstractMessage> messageList = new ArrayList<AbstractMessage>();			
			try {
				for (WrapReadableMiddleObject wrapReadableMiddleObject : wrapReadableMiddleObjectList) {
					messageList.add(clientMessageUtility.buildOutputMessage(classLoader, wrapReadableMiddleObject));
				}
			} finally {
				for (WrapReadableMiddleObject wrapReadableMiddleObject : wrapReadableMiddleObjectList) {
					Object readableMiddleObject = wrapReadableMiddleObject.getReadableMiddleObject();
					if (readableMiddleObject instanceof FreeSizeInputStream) {
						FreeSizeInputStream  fsis = (FreeSizeInputStream)readableMiddleObject;
						fsis.close();
					}
				}
			}
			
			if (wrapReadableMiddleObjectListSize > 1) {				
				log.info(messageList.toString());
				
				String errorMessage = "비공유 + 동기 연결 클래스는 오직 1개 입력 메시지에 1개 출력 메시지만 처리할 수 있습니다. 2개 이상 출력 메시지 검출 되었습니다.";
				throw new IOException(errorMessage);
			}
			
			outObj = messageList.get(0);
			
			if (outObj instanceof SelfExnRes) {
				SelfExnRes selfExnRes = (SelfExnRes) outObj;
				log.warn(selfExnRes.toString());
				SelfExn.ErrorType.throwSelfExnException(selfExnRes);
			}
		
		
		} finally {
			
			if (Integer.MAX_VALUE == mailID) {
				mailID = Integer.MIN_VALUE;
			} else {
				mailID++;
			}
			
			
			endTime = new java.util.Date().getTime();
			log.info(String.format("시간차=[%d]", (endTime - startTime)));
		}		

		return outObj;
	}	
	
	public void sendAsynInputMessage(AbstractMessage inObj) 
			throws NotSupportedException {		
		throw new NotSupportedException("this synchronous connection doesn't support this method 'sendAsynInputMessage'");
	}
	
	@Override
	public void finalize() {
		try {
			close();
		} catch (IOException e) {
			
		}
		
		if (! isQueueIn) {
			log.warn("큐로 복귀 못한 동기 비공유 연결[{}]", hashCode());
		}
	}


	protected void doReleaseSocketResources() {
		syncPrivateSocketResoruce.releaseSocketResources();
	}



}
