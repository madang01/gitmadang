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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.client.ClientObjectCacheManagerIF;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.MailboxTimeoutException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolManagerIF;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.ReceivedLetter;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;

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
	/** 연결 클래스 번호 */
	protected int index;
	
	protected String hostOfProject = null;
	protected int portOfProject;
	protected Charset charsetOfProject = null;
	
	
	
	
	/** 데이터 패킷 버퍼 관리자 */
	protected DataPacketBufferPoolManagerIF dataPacketBufferQueueManager = null;
	/** */
	protected MessageProtocolIF messageProtocol = null;
	
	
	/** 소켓 채널 */
	protected SocketChannel serverSC = null;
	
	/** 소켓 채널 타임 아웃 시간, 입력에 대한 응답을 기다리는 시간 */
	protected long socketTimeOut;
	
	/** 연결 생성시 자동 접속 여부 */
	protected boolean whetherToAutoConnect;

	/** 소켓 채널 전용 읽기 자원 */
	protected SocketOutputStream messageInputStreamResource = null;
	

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
	public AbstractConnection(String projectName, int index, 
			String hostOfProject,
			int portOfProject,
			Charset charsetOfProject,
			long socketTimeOut,
			boolean whetherToAutoConnect,
			LinkedBlockingQueue<ReceivedLetter> asynOutputMessageQueue,
			MessageProtocolIF messageProtocol,
			int dataPacketBufferMaxCntPerMessage,
			DataPacketBufferPoolManagerIF dataPacketBufferQueueManager,
			ClientObjectCacheManagerIF clientObjectCacheManager) throws NoMoreDataPacketBufferException, InterruptedException {
		this.projectName = projectName;
		this.index = index;
		this.hostOfProject = hostOfProject;
		this.portOfProject = portOfProject;
		this.charsetOfProject = charsetOfProject;
		this.socketTimeOut = socketTimeOut;
		this.whetherToAutoConnect = whetherToAutoConnect;
		this.messageProtocol = messageProtocol;
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		messageInputStreamResource = new SocketOutputStream(dataPacketBufferMaxCntPerMessage, dataPacketBufferQueueManager);
		
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
		if (null == serverSC) {
			return false;
		}
		return serverSC.isConnected();
	}
	
	/**
	 * @return 소캣 개방 여부
	 */
	public boolean isOpen() {
		if (null == serverSC) {
			return false;
		}
		
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
	public void write(ArrayList<WrapBuffer> inObjWrapBufferList) throws IOException {
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
			/*} catch(ClosedByInterruptException e) {
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
			}*/
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
	abstract public void connectServerIfNoConnection() throws ServerNotReadyException;

	

	/**
	 * FIXME! 테스트 못하였음. 사이드 이팩트 영향력 측정 못하였음. 소켓 채널을 닫는다.
	 */
	public void serverClose() {
		synchronized (monitor) {
			try {
				serverSC.shutdownInput();
			} catch (Exception e) {
				log.warn(String.format("server name[%s] socket channel shutdownInput fail",
						projectName), e);
			}
			try {
				serverSC.shutdownOutput();
			} catch (Exception e) {
				log.warn(String.format("server name[%s] socket channel shutdownOutput fail",
						projectName), e);
			}
			
			try {
				serverSC.close();
			} catch (Exception e) {
				log.warn(String.format("server name[%s] socket channel close fail",
						projectName), e);
			}
		}
	}

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
	public SocketOutputStream getMessageInputStreamResource() {
		
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
	 * @throws InterruptedException 
	 * @throws MailboxTimeoutException 
	 */
	abstract public AbstractMessage sendSyncInputMessage(
			AbstractMessage inputMessage) throws ServerNotReadyException, SocketTimeoutException,
			NoMoreDataPacketBufferException, BodyFormatException, 
			DynamicClassCallException, ServerTaskException, NotLoginException, InterruptedException, MailboxTimeoutException;
	
	
	/**
	 * 입력 메시지를 보내기만 하며 출력 메시지를 기다리지 않는다.
	 * @param inputMessage 입력 메시지
	 * @throws ServerNotReadyException 서버 연결 실패시 발생
	 * @throws SocketTimeoutException 서버 응답 시간 초과시 발생
	 * @throws NoMoreDataPacketBufferException 래퍼 메시지를 만들때 데이터 패킷 버퍼 큐에서 버퍼를 확보하는데 실패할때 발생
	 * @throws BodyFormatException 스트림에서 메시지로, 메시지에서 스트림으로 바꿀때 바디 부분 구성 실패시 발생
	 * @throws NotSupportedException 
	 * @throws InterruptedException 
	 */
	abstract public void sendAsynInputMessage(
			AbstractMessage inputMessage) throws ServerNotReadyException, SocketTimeoutException, 
			NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException, NotSupportedException, InterruptedException;
	
	protected AbstractMessage getMessageFromMiddleReadObj(ClassLoader classLoader, ReceivedLetter receivedLetter) throws DynamicClassCallException, BodyFormatException, NoMoreDataPacketBufferException, ServerTaskException, NotLoginException {
		String messageID = receivedLetter.getMessageID();
		int mailboxID = receivedLetter.getMailboxID();
		int mailID = receivedLetter.getMailID();
		Object middleReadObj = receivedLetter.getMiddleReadObj();
		
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
			messageObj = messageDecoder.decode(messageProtocol.getSingleItemDecoder(), charsetOfProject, middleReadObj);
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
		
		if (messageObj instanceof SelfExn) {
			SelfExn selfExnOutObj =(SelfExn)messageObj;
			log.warn(selfExnOutObj.getReport());
			selfExnOutObj.throwException(); 
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
		
		ArrayList<WrapBuffer> wrapBufferList = null;	
		try {
			wrapBufferList = messageProtocol.M2S(messageToClient, messageEncoder, charsetOfProject);
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
	
	public void changeServerAddress(String newServerHost, int newServerPort) throws NotSupportedException {
		if (null == newServerHost) {
			throw new IllegalArgumentException("the parameter newServerHost is null");
		}
		
		if (newServerHost.equals(this.hostOfProject) && newServerPort == this.portOfProject) {
			/** if the new address is same to the old address then nothing */
			return;
		}
		
		if (serverSC != null && serverSC.isConnected()) {
			String errorMessage = String.format("this client cann't change new server address[host:%s,port:%s] becase this client is connected", 
					newServerHost, newServerPort);
			throw new NotSupportedException(errorMessage);
		}
		this.hostOfProject = newServerHost;
		this.portOfProject = newServerPort;
	}
	
	
	@Override
	public String toString() {
		StringBuilder strBuffer = new StringBuilder();
		strBuffer.append(projectName);
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
		strBuffer.append(projectName);
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
		strBuffer.append(projectName);
		strBuffer.append("], connection=[");
		strBuffer.append(index);
		strBuffer.append("], serverSC=[");
		strBuffer.append(serverSC.hashCode());		
		strBuffer.append("]");
		return strBuffer.toString();
	}
}
