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
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.SocketInputStream;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageDecoder;
import kr.pe.sinnori.common.message.codec.MessageEncoder;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.ReceivedLetter;

/**
 * 클라이언트 연결 클래스의 부모 추상화 클래스<br/>
 * 참고) 소켓 채널을 감싸아 소켓 채널관련 서비스를 구현하는 클래스, 즉 소켓 채널 랩 클래스를 연결 클래스로 명명한다.
 * 참고) 연결 클래스는 블락킹 모드에 따라서 그리고 공유 여부에 따라서 종류가 나뉘어 진다.
 * 
 * @author Jonghoon Won
 * 
 */
public abstract class AbstractConnection implements CommonRootIF {
	/** 모니터 전용 오브젝트 */
	protected final Object monitor = new Object();
	
	/** 연결 클래스 번호 */
	protected int index;
	/** 프로젝트의 클라이언트 환경 변수 */
	protected ClientProjectConfig clientProjectConfig = null;
	/** 데이터 패킷 버퍼 관리자 */
	protected DataPacketBufferQueueManagerIF dataPacketBufferQueueManager = null;
	/** */
	protected MessageProtocolIF messageProtocol = null;
	
	
	/** 소켓 채널 */
	protected SocketChannel serverSC = null;
	
	/** 소켓 채널 타임 아웃 시간, 입력에 대한 응답을 기다리는 시간 */
	protected long socketTimeOut;
	
	/** 연결 생성시 자동 접속 여부 */
	protected boolean whetherToAutoConnect;

	/** 소켓 채널 전용 읽기 자원 */
	protected SocketInputStream messageInputStreamResource = null;
	

	/** 최종 읽기를 수행한 시간. 초기값은 클라이언트(=SocketChannel) 생성시간이다. */
	protected java.util.Date finalReadTime = new java.util.Date();
	/** echo 메세지를 보낸 횟수. */
	protected int echoMesgCount = 0;
	

	/**
	 * 서버에서 공지등 불특정 다수한테 메시지를 보낼때 출력 메시지를 담은 큐
	 */
	protected LinkedBlockingQueue<ReceivedLetter> asynOutputMessageQueue = null;
	
	protected ClientObjectCacheManagerIF clientObjectCacheManager = null;
	
	/**
	 * 생성자
	 * @param index 연결 클래스 번호
	 * @param socketTimeOut 소켓 타임 아웃
	 * @param whetherToAutoConnect 자동 접속 여부
	 * @param clientProjectConfig 프로젝트의 클라이언트 환경 변수
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자
	 * @param asynOutputMessageQueue 서버에서 보내는 불특정 출력 메시지를 받는 큐
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼를 할당 받지 못했을 경우 던지는 예외
	 * @throws InterruptedException
	 */
	public AbstractConnection(int index, 
			long socketTimeOut,
			boolean whetherToAutoConnect,
			ClientProjectConfig clientProjectConfig,
			LinkedBlockingQueue<ReceivedLetter> asynOutputMessageQueue,
			MessageProtocolIF messageProtocol,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager,
			ClientObjectCacheManagerIF clientObjectCacheManager) throws NoMoreDataPacketBufferException, InterruptedException {
		this.index = index;
		this.socketTimeOut = socketTimeOut;
		this.whetherToAutoConnect = whetherToAutoConnect;
		this.clientProjectConfig = clientProjectConfig;
		this.messageProtocol = messageProtocol;
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		messageInputStreamResource = new SocketInputStream(dataPacketBufferQueueManager);
		
		this.asynOutputMessageQueue = asynOutputMessageQueue;
		this.clientObjectCacheManager = clientObjectCacheManager;
		
		
	
		/*
		try {
			serverSC = SocketChannel.open();
		} catch (IOException e) {
			String errorMessage = String.format("project[%s] connection[%d], fail to open a socket channel", commonProjectInfo.getProjectName(), index);
			log.fatal(errorMessage, e);
			System.exit(1);
		}
		*/
	}
	



	/**
	 * 소켓에 종속적인 자원 초기화.
	 */
	protected void initSocketResource() {
		messageInputStreamResource.initResource();
		
		echoMesgCount = 0;
		// finalReadTime = null;
	}
	
	/**
	 * @return 연결 번호
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * 소켓 채널를 반환한다. 주의점) 소켓 채널은 소중한 자원으로 이것을 받는 클래스는 오용하지 않고 용도에 맞게 사용해야 한다.
	 * 
	 * @return 소켓 채널
	 */
	/*
	public SocketChannel getSocketChannel() {
		return serverSC;
	}
	*/
	/**
	 * @return 소켓 연결 여부
	 */
	public boolean isConnected() {
		return serverSC.isConnected();
	}
	
	/**
	 * @return 소캣 개방 여부
	 */
	public boolean isOpen() {
		return serverSC.isOpen();
	}
	
	/**
	 * @return 연결 작업중 여부
	 */
	public boolean isConnectionPending() {
		return serverSC.isConnectionPending();
	}
	
	/**
	 * @return 임의 selector 에 등록 여부
	 */
	public boolean isRegistered() {
		return serverSC.isRegistered();
	}
	
	/**
	 * 입력 메시지 스트림이 담긴 WrapBuff 목록의 소켓 쓰기 
	 * @param inObjWrapBufferList 입력 메시지 스트림이 담긴 WrapBuff 목록
	 * @throws IOException 소켓 쓰기 에러 발생시 던지는 예외
	 * @throws ClosedByInterruptException 
	 */
	public void write(ArrayList<WrapBuffer> inObjWrapBufferList) throws ClosedByInterruptException, IOException {
		// if (null == inObjWrapBufferList) return;
		
		 
		
		// int inObjWrapBufferListSize = inObjWrapBufferList.size();
		
		// long startTime = System.currentTimeMillis();
		synchronized (serverSC) {
			/**
			 * 2013.07.24 잔존 데이타 발생하므로 GatheringByteChannel 를 이용하는 바이트 버퍼 배열 쓰기 방식 포기.
			 */			
			/*for (int i=0; i < inObjWrapBufferListSize; i++) {
				WrapBuffer wrapBuffer = inObjWrapBufferList.get(i);
				ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();
				
				do {
					serverSC.write(byteBuffer);
				} while(byteBuffer.hasRemaining());
			}*/
			
			for (WrapBuffer wrapBuffer : inObjWrapBufferList) {
				ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();
				do {
					serverSC.write(byteBuffer);
				} while(byteBuffer.hasRemaining());
			}
		}
		
		/*long endTime = System.currentTimeMillis();
		log.info(String.format("elapsed time=[%s]", endTime - startTime));*/
	}
	
	/**
	 * 서버와 연결을 맺는다.
	 * 
	 * @throws ServerNotReadyException
	 *             서버와의 연결 실패시 발생한다.
	 */
	abstract public void serverOpen() throws ServerNotReadyException;

	

	/**
	 * FIXME! 테스트 못하였음. 사이드 이팩트 영향력 측정 못하였음. 소켓 채널을 닫는다.
	 */
	public void serverClose() {
		synchronized (monitor) {
			try {
				serverSC.shutdownInput();
			} catch (Exception e) {
				log.warn(String.format("server name[%s] socket channel shutdownInput fail",
						clientProjectConfig.getProjectName()), e);
			}
			try {
				serverSC.shutdownOutput();
			} catch (Exception e) {
				log.warn(String.format("server name[%s] socket channel shutdownOutput fail",
						clientProjectConfig.getProjectName()), e);
			}
			
			try {
				serverSC.close();
			} catch (Exception e) {
				log.warn(String.format("server name[%s] socket channel close fail",
						clientProjectConfig.getProjectName()), e);
			}
		}
	}

	/**
	 * @return 프로젝트 이름
	 */
	public String getProjectName() {
		return clientProjectConfig.getProjectName();
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
	public SocketInputStream getMessageInputStreamResource() {
		
		return messageInputStreamResource;
	}
	
	

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

	/**
	 * 에코 메세지를 보낸 횟수를 반환한다. 미래 예약 변수임.
	 * 
	 * @return 에코 메세지를 보낸 횟수
	 */
	public int getEchoMesgCount() {
		return echoMesgCount;
	}
	
	/**
	 * 입력 메시지를 보낸후 출력 메시지를 받아 반환을 한다.
	 * 
	 * @param inputMessage
	 *            입력 메시지
	 * @return 출력 메시지 목록
	 * @throws ServerNotReadyException
	 *             서버 연결 실패시 발생
	 * @throws SocketTimeoutException
	 *             서버 응답 시간 초과시 발생
	 * @throws NoMoreDataPacketBufferException
	 *             래퍼 메시지를 만들때 데이터 패킷 버퍼 큐에서 버퍼를 확보하는데 실패할때 발생
	 * @throws BodyFormatException
	 *             스트림에서 메시지로, 메시지에서 스트림으로 바꿀때 바디 부분 구성 실패시 발생
	 */
	abstract public AbstractMessage sendSyncInputMessage(
			AbstractMessage inputMessage) throws ServerNotReadyException, SocketTimeoutException,
			NoMoreDataPacketBufferException, BodyFormatException, 
			DynamicClassCallException, ServerTaskException, NotLoginException;
	
	
	/**
	 * 입력 메시지를 보내기만 하며 출력 메시지를 기다리지 않는다.
	 * @param inputMessage 입력 메시지
	 * @throws ServerNotReadyException 서버 연결 실패시 발생
	 * @throws SocketTimeoutException 서버 응답 시간 초과시 발생
	 * @throws NoMoreDataPacketBufferException 래퍼 메시지를 만들때 데이터 패킷 버퍼 큐에서 버퍼를 확보하는데 실패할때 발생
	 * @throws BodyFormatException 스트림에서 메시지로, 메시지에서 스트림으로 바꿀때 바디 부분 구성 실패시 발생
	 * @throws NotSupportedException 
	 */
	abstract public void sendAsynInputMessage(
			AbstractMessage inputMessage) throws ServerNotReadyException, SocketTimeoutException, 
			NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException, NotSupportedException;
	
	protected AbstractMessage getMessageFromMiddleReadObj(ClassLoader classLoader, ReceivedLetter receivedLetter) throws DynamicClassCallException, BodyFormatException {
		String messageID = receivedLetter.getMessageID();
		int mailboxID = receivedLetter.getMailboxID();
		int mailID = receivedLetter.getMailID();
		Object middleReadObj = receivedLetter.getMiddleReadObj();
		
		MessageCodecIF messageCodec = clientObjectCacheManager.getClientCodec(classLoader, messageID);
		
		MessageDecoder  messageDecoder  = null;
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
			messageObj = messageDecoder.decode(messageProtocol.getSingleItemDecoder(), clientProjectConfig.getCharset(), middleReadObj);
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
		
		return messageObj;
	}
	
	
	
	
	protected ArrayList<WrapBuffer> getWrapBufferList(ClassLoader classLoader, AbstractMessage messageToClient) throws DynamicClassCallException, NoMoreDataPacketBufferException, BodyFormatException {
		MessageCodecIF messageCodec = null;
		
		try {
			messageCodec = clientObjectCacheManager.getClientCodec(classLoader, messageToClient.getMessageID());
		} catch (DynamicClassCallException e) {
			log.warn(e.getMessage());
			
			throw e;
		} catch(Exception e) {
			log.warn(e.getMessage(), e);
			
			throw new DynamicClassCallException("unkown error::"+e.getMessage());
		}
		
		MessageEncoder  messageEncoder  = null;
		
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
		
		ArrayList<WrapBuffer> wrapBufferList = null;	
		try {
			wrapBufferList = messageProtocol.M2S(messageToClient, messageEncoder, clientProjectConfig.getCharset());
		} catch(NoMoreDataPacketBufferException e) {
			throw e;
		} catch(DynamicClassCallException e) {
			throw e;
		} catch(BodyFormatException e) {
			throw e;
		} catch(Exception e) {
			log.warn("unkown error", e);
			throw new BodyFormatException("unkown error::"+e.getMessage());
		}
		
		return wrapBufferList;
	}
	
	
	
	@Override
	public String toString() {
		StringBuilder strBuffer = new StringBuilder();
		strBuffer.append(clientProjectConfig.getProjectName());
		strBuffer.append("index=[");
		strBuffer.append(index);
		strBuffer.append(", serverSC=[");
		if (null != serverSC) {
			strBuffer.append(serverSC.hashCode());
		} else {
			strBuffer.append("null");
		}
		strBuffer.append("]");
		strBuffer.append(CommonStaticFinalVars.NEWLINE);
		strBuffer.append(clientProjectConfig.toString());
		strBuffer.append(CommonStaticFinalVars.NEWLINE);
		strBuffer.append("], finalReadTime=[");
		strBuffer.append(finalReadTime);
		strBuffer.append("], echoMesgCount=[");
		strBuffer.append(echoMesgCount);
		strBuffer.append("], ");

		return strBuffer.toString();
	}
	
	public String getSimpleConnectionInfo() {
		StringBuilder strBuffer = new StringBuilder();
		strBuffer.append("projectName=[");
		strBuffer.append(clientProjectConfig.getProjectName());
		strBuffer.append("], connection=[");
		strBuffer.append(index);
		strBuffer.append("], serverSC=[");
		strBuffer.append(serverSC.hashCode());		
		strBuffer.append("]");
		return strBuffer.toString();
	}
}
