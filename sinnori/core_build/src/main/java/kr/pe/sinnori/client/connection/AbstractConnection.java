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

package kr.pe.sinnori.client.connection;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;
import kr.pe.sinnori.common.type.SelfExn;
import kr.pe.sinnori.impl.message.SelfExnRes.SelfExnRes;

/**
 * 클라이언트 연결 클래스의 부모 추상화 클래스<br/>
 * 참고) 소켓 채널을 감싸아 소켓 채널관련 서비스를 구현하는 클래스, 즉 소켓 채널 랩 클래스를 연결 클래스로 명명한다.
 * 참고) 연결 클래스는 블락킹 모드에 따라서 그리고 공유 여부에 따라서 종류가 나뉘어 진다.
 * 
 * @author Won Jonghoon
 * 
 */
public abstract class AbstractConnection {
	protected Logger log = LoggerFactory.getLogger(AbstractConnection.class);
	
	/** 모니터 전용 오브젝트 */
	protected final Object monitor = new Object();
	
	
	protected String projectName = null;	
	protected int index;	
	
	protected String host = null;
	protected int port;
	protected long socketTimeOut;
	protected boolean whetherToAutoConnect;
	
	protected SocketOutputStream socketOutputStream = null;
	protected DataPacketBufferPoolIF dataPacketBufferPoolManager = null;
	protected MessageProtocolIF messageProtocol = null;
	

	protected ClientObjectCacheManagerIF clientObjectCacheManager = null;
	
	
	protected java.util.Date finalReadTime = new java.util.Date();
	
	public AbstractConnection(String projectName, int index, 
			String host,
			int port,
			long socketTimeOut,
			boolean whetherToAutoConnect,
			SocketOutputStream socketOutputStream,
			MessageProtocolIF messageProtocol,
			DataPacketBufferPoolIF dataPacketBufferPoolManager,
			ClientObjectCacheManagerIF clientObjectCacheManager) throws NoMoreDataPacketBufferException, InterruptedException {
		this.projectName = projectName;
		this.index = index;
		this.host = host;
		this.port = port;
		this.socketTimeOut = socketTimeOut;
		this.whetherToAutoConnect = whetherToAutoConnect;
		this.socketOutputStream = socketOutputStream;
		this.messageProtocol = messageProtocol;
		this.dataPacketBufferPoolManager = dataPacketBufferPoolManager;
		this.clientObjectCacheManager = clientObjectCacheManager;
	}
	
	abstract public void closeSocket() throws IOException;

	abstract public boolean isConnected();
	
	
	/**
	 * @return 임의 selector 에 등록 여부
	 */
	/*public boolean isRegistered() {
		return serverSC.isRegistered();
	}*/
	
	/**
	 * 입력 메시지 스트림이 담긴 WrapBuff 목록의 소켓 쓰기 
	 * @param inObjWrapBufferList 입력 메시지 스트림이 담긴 WrapBuff 목록
	 * @throws IOException 소켓 쓰기 에러 발생시 던지는 예외
	 * @throws ClosedByInterruptException 
	 */
	/*public void write(List<WrapBuffer> inObjWrapBufferList) throws IOException {
		// if (null == inObjWrapBufferList) return;
		
		 
		
		// int inObjWrapBufferListSize = inObjWrapBufferList.size();
		
		// long startTime = System.currentTimeMillis();
		synchronized (serverSC) {
			*//**
			 * 2013.07.24 잔존 데이타 발생하므로 GatheringByteChannel 를 이용하는 바이트 버퍼 배열 쓰기 방식 포기.
			 *//*			
			for (int i=0; i < inObjWrapBufferListSize; i++) {
				WrapBuffer wrapBuffer = inObjWrapBufferList.get(i);
				ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();
				
				do {
					serverSC.write(byteBuffer);
				} while(byteBuffer.hasRemaining());
			}
			// try {
			for (WrapBuffer wrapBuffer : inObjWrapBufferList) {
				ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();
				do {
					int numberOfBytesWritten = serverSC.write(byteBuffer);
					if (0 == numberOfBytesWritten) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							log.warn("when the number of bytes written is zero,  this thread must sleep. but it fails.", e);
						}
					}
				} while(byteBuffer.hasRemaining());
			}
			} catch(ClosedByInterruptException e) {
				serverClose();
				log.error("this eorr is very serious so exist", e);
				System.exit(1);
			} catch(AsynchronousCloseException e) {
				serverClose();
				log.error("this eorr is very serious so exist", e);
				System.exit(1);
			} catch(ClosedChannelException e) {
				serverClose();
				throw e;
			}
		}
		
		long endTime = System.currentTimeMillis();
		log.info(String.format("elapsed time=[%s]", endTime - startTime));
	}*/
	
	
	abstract public void connectServer() throws IOException, InterruptedException;

	

	

	/**
	 * @return 프로젝트 이름
	 */
	public String getProjectName() {
		return projectName;
	}
	
	/**
	 * @return 소켓 타임 아웃
	 */
	public final long getSocketTimeOut() {
		return socketTimeOut;
	}
	
	/**
	 * @return 소켓 채널 전용 읽기 자원
	 */
	/*public SocketOutputStream getSocketOutputStream() {
		
		return socketOutputStream;
	}
	*/
	

	/**
	 * 마지막으로 읽은 시간을 반환한다.
	 * 
	 * @return 마지막으로 읽은 시간
	 */
	public java.util.Date getFinalReadTime() {
		return finalReadTime;
	}

	/**
	 * 마지막으로 읽은 시간을 새롭게 설정한다.
	 */

	public void setFinalReadTime() {
		finalReadTime = new java.util.Date();
	}

	
	/*public int getEchoMesgCount() {
		return echoMesgCount;
	}*/
	
	
	public SocketOutputStream getSocketOutputStream() {
		return socketOutputStream;
	}
	
	public void releaseResources() {
		socketOutputStream.close();
	}
	
	abstract public AbstractMessage sendSyncInputMessage(
			AbstractMessage inputMessage) throws IOException, 
			NoMoreDataPacketBufferException, BodyFormatException, 
			DynamicClassCallException, ServerTaskException, AccessDeniedException, InterruptedException;
	
	
	
	abstract public void sendAsynInputMessage(
			AbstractMessage inputMessage) throws IOException,  
			NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException, NotSupportedException, InterruptedException;
	
	protected AbstractMessage getMessageFromMiddleReadObj(ClassLoader classLoader, WrapReadableMiddleObject receivedLetter) throws DynamicClassCallException, 
	BodyFormatException, NoMoreDataPacketBufferException, ServerTaskException, AccessDeniedException, IOException {
		String messageID = receivedLetter.getMessageID();
		int mailboxID = receivedLetter.getMailboxID();
		int mailID = receivedLetter.getMailID();
		Object middleReadObj = receivedLetter.getReadableMiddleObject();
		
		MessageCodecIF messageCodec = clientObjectCacheManager.getClientCodec(classLoader, messageID);
		
		AbstractMessageDecoder  messageDecoder  = null;
		try {
			messageDecoder = messageCodec.getMessageDecoder();
		} catch (DynamicClassCallException e) {
			String errorMessage = String.format("클라이언트에서 메시지 식별자[%s]에 해당하는 디코더를 얻는데 실패하였습니다.", messageID);
			log.warn("{}, mailboxID=[{}], mailID=[{}]", errorMessage, mailboxID, mailID);
			throw new DynamicClassCallException(errorMessage);
		} catch (Exception e) {
			String errorMessage = String.format("알 수 없는 원인으로 클라이언트에서 메시지 식별자[%s]에 해당하는 디코더를 얻는데 실패하였습니다.", messageID);
			log.warn("{}, mailboxID=[{}], mailID=[{}]", errorMessage, mailboxID, mailID);
			throw new DynamicClassCallException(errorMessage);
		}
		
		AbstractMessage messageObj = null;
		try {
			messageObj = messageDecoder.decode(messageProtocol.getSingleItemDecoder(), middleReadObj);
			messageObj.messageHeaderInfo.mailboxID = mailboxID;
			messageObj.messageHeaderInfo.mailID = mailID;
		} catch (BodyFormatException e) {
			String errorMessage = String.format("클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, mailboxID, mailID, e.getMessage());
			log.warn(errorMessage);
			throw new BodyFormatException(errorMessage);
		} catch (OutOfMemoryError e) {
			String errorMessage = String.format("메모리 부족으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, mailboxID, mailID, e.getMessage());
			log.warn(errorMessage);
			throw new BodyFormatException(errorMessage);
		} catch (Exception e) {
			String errorMessage = String.format("알 수 없는 원인으로 클라이언트에서 메시지[messageID=[%s], mailboxID=[%d], mailID=[%d]] 바디 디코딩 실패, %s", messageID, mailboxID, mailID, e.getMessage());
			log.warn(errorMessage);
			throw new BodyFormatException(errorMessage);
		}
		
		if (messageObj instanceof SelfExnRes) {
			SelfExnRes selfExnRes =(SelfExnRes)messageObj;
			log.warn(selfExnRes.toString());
			SelfExn.ErrorType.throwSelfExnException(selfExnRes);
		}
		
		return messageObj;
	}
	
	
	protected List<WrapBuffer> getWrapBufferListOfInputMessage(ClassLoader classLoader, AbstractMessage inputMessage) throws DynamicClassCallException, NoMoreDataPacketBufferException, BodyFormatException {
		MessageCodecIF messageCodec = null;
		
		try {
			messageCodec = clientObjectCacheManager.getClientCodec(classLoader, inputMessage.getMessageID());
		} catch (DynamicClassCallException e) {
			log.warn(e.getMessage());
			
			throw e;
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			
			throw new DynamicClassCallException("unkown error::"+e.getMessage());
		}
		
		AbstractMessageEncoder  messageEncoder  = null;
		
		try {
			messageEncoder = messageCodec.getMessageEncoder();
		} catch(DynamicClassCallException e) {
			// log.warn(e.getMessage());
			
			// throw new DynamicClassCallException(e.getMessage());
			throw e;
		} catch(Exception e) {
			log.warn(e.getMessage());
			throw new DynamicClassCallException("unkown error::"+e.getMessage());
		}
		
		List<WrapBuffer> wrapBufferList = null;	
		try {
			wrapBufferList = messageProtocol.M2S(inputMessage, messageEncoder);
		} catch(NoMoreDataPacketBufferException e) {
			throw e;		
		} catch(BodyFormatException e) {
			throw e;
		} catch(Exception e) {
			log.warn("unkown error", e);
			throw new BodyFormatException("unkown error::"+e.getMessage());
		}
		
		return wrapBufferList;
	}
	
	/*public void changeServerAddress(String newServerHost, int newServerPort) throws NotSupportedException {
		if (null == newServerHost) {
			throw new IllegalArgumentException("the parameter newServerHost is null");
		}
		
		if (newServerHost.equals(this.hostOfProject) && newServerPort == this.portOfProject) {
			*//** if the new address is same to the old address then nothing *//*
			return;
		}
		
		if (serverSC != null && serverSC.isConnected()) {
			String errorMessage = String.format("this client cann't change new server address[host:%s,port:%s] becase this client is connected", 
					newServerHost, newServerPort);
			throw new NotSupportedException(errorMessage);
		}
		this.hostOfProject = newServerHost;
		this.portOfProject = newServerPort;
	}*/
	
	
	// @Override
	/*public String toString() {
		StringBuilder strBuffer = new StringBuilder();
		strBuffer.append(projectName);
		strBuffer.append("index=[");
		strBuffer.append(index);
		strBuffer.append("], finalReadTime=[");
		strBuffer.append(finalReadTime);
		strBuffer.append("]");

		return strBuffer.toString();
	}
	*/
	/*public String getSimpleConnectionInfo() {
		StringBuilder strBuffer = new StringBuilder();
		strBuffer.append("projectName=[");
		strBuffer.append(projectName);
		strBuffer.append("], connection=[");
		strBuffer.append(index);
		strBuffer.append("]");
		return strBuffer.toString();
	}*/
}
