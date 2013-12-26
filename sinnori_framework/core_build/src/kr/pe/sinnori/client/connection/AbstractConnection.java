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
import java.nio.ByteOrder;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.client.io.LetterFromServer;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.lib.CommonProjectInfo;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonStaticFinal;
import kr.pe.sinnori.common.lib.CommonType;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.MessageInputStreamResourcePerSocket;
import kr.pe.sinnori.common.lib.WrapBuffer;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;

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
	/** 서버와 연결에 필요한 공통 항목및 자원 */
	protected CommonProjectInfo commonProjectInfo = null;
	/** 데이터 패킷 버퍼 관리자 */
	protected DataPacketBufferQueueManagerIF dataPacketBufferQueueManager = null;
	
	
	/** 소켓 채널 */
	protected SocketChannel serverSC = null;
	
	/** 소켓 채널 타임 아웃 시간, 입력에 대한 응답을 기다리는 시간 */
	protected long socketTimeOut;
	
	/** 연결 생성시 자동 접속 여부 */
	protected boolean whetherToAutoConnect;

	/** 소켓 채널 전용 읽기 자원 */
	protected MessageInputStreamResourcePerSocket messageInputStreamResource = null;
	

	/** 최종 읽기를 수행한 시간. 초기값은 클라이언트(=SocketChannel) 생성시간이다. */
	protected java.util.Date finalReadTime = new java.util.Date();
	/** echo 메세지를 보낸 횟수. */
	protected int echoMesgCount = 0;
	

	/**
	 * 서버에서 공지등 불특정 다수한테 메시지를 보낼때 출력 메시지를 담은 큐
	 */
	protected LinkedBlockingQueue<OutputMessage> serverOutputMessageQueue = null;
	
	/**
	 * 생성자
	 * @param index 연결 클래스 번호
	 * @param socketTimeOut 소켓 타임 아웃
	 * @param whetherToAutoConnect 자동 접속 여부
	 * @param commonProjectInfo 연결 공통 데이터
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자
	 * @param serverOutputMessageQueue  서버에서 보내는 불특정 출력 메시지를 받는 큐
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼를 할당 받지 못했을 경우 던지는 예외
	 */
	public AbstractConnection(int index, 
			long socketTimeOut,
			boolean whetherToAutoConnect,
			CommonProjectInfo commonProjectInfo,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager,
			LinkedBlockingQueue<OutputMessage> serverOutputMessageQueue) throws NoMoreDataPacketBufferException {
		this.index = index;
		this.socketTimeOut = socketTimeOut;
		this.whetherToAutoConnect = whetherToAutoConnect;
		this.commonProjectInfo = commonProjectInfo;
		
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		messageInputStreamResource = new MessageInputStreamResourcePerSocket(commonProjectInfo.byteOrderOfProject, dataPacketBufferQueueManager);
		
		this.serverOutputMessageQueue = serverOutputMessageQueue;
		
	}
	
	
	public CommonProjectInfo getCommonProjectInfo() {
		return commonProjectInfo;
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
		synchronized (serverSC) {
			if (null == serverSC) {
				log.warn(String.format("serverSC is null, conn=[%s]", getSimpleConnectionInfo()));
				return false;
			}
		
			return serverSC.isConnected();
		}
	}
	
	/**
	 * @return 임의 selector 에 등록 여부
	 */
	public boolean isRegistered() {
		synchronized (serverSC) {
			if (null == serverSC) {
				log.warn(String.format("serverSC is null, conn=[%s]", getSimpleConnectionInfo()));
				return false;
			}
		
		
			return serverSC.isRegistered();
		}
	}
	
	/**
	 * 입력 메시지 스트림이 담긴 WrapBuff 목록의 소켓 쓰기 
	 * @param inObjWrapBufferList 입력 메시지 스트림이 담긴 WrapBuff 목록
	 * @throws IOException 소켓 쓰기 에러 발생시 던지는 예외
	 */
	public void write(ArrayList<WrapBuffer> inObjWrapBufferList) throws IOException {
		// if (null == inObjWrapBufferList) return;
		
		int inObjWrapBufferListSize = inObjWrapBufferList.size();
		
		synchronized (serverSC) {
			if (null == serverSC) {
				log.warn(String.format("serverSC is null, conn=[%s]", getSimpleConnectionInfo()));
				return;
			}
			
			/**
			 * 2013.07.24 잔존 데이타 발생하므로 GatheringByteChannel 를 이용하는 바이트 버퍼 배열 쓰기 방식 포기.
			 */
			for (int i=0; i < inObjWrapBufferListSize; i++) {
				WrapBuffer wrapBuffer = inObjWrapBufferList.get(i);
				ByteBuffer byteBuffer = wrapBuffer.getByteBuffer();

				do {
					try {
						serverSC.write(byteBuffer);
					} catch(ClosedByInterruptException e) {
						log.warn("ClosedByInterruptException", e);
						try {
							serverSC.write(byteBuffer);
						} catch(ClosedByInterruptException e1) {
							log.fatal("ClosedByInterruptException", e1);
							System.exit(1);
						}
						Thread.currentThread().interrupt();
					}
				} while(byteBuffer.hasRemaining());
			}
		}	
	}
	
	/**
	 * 서버와 연결을 맺는다.
	 * 
	 * @throws ServerNotReadyException
	 *             서버와의 연결 실패시 발생한다.
	 */
	abstract public void serverOpen() throws ServerNotReadyException, InterruptedException;

	

	/**
	 * FIXME! 테스트 못하였음. 사이드 이팩트 영향력 측정 못하였음. 소켓 채널을 닫는다.
	 */
	public void closeServer() {
		synchronized (monitor) {
			try {
				if (serverSC != null)
					serverSC.close();

			} catch (Exception e) {
				log.warn(String.format("server name[%s] socket channel close fail",
						commonProjectInfo.projectName), e);
			} finally {
				serverSC = null;
			}
		}
	}

	/**
	 * @return 프로젝트 이름
	 */
	public String getProjectName() {
		return commonProjectInfo.projectName;
	}
	
	/**
	 * @return 소켓 타임 아웃
	 */
	public final long getSocketTimeOut() {
		return socketTimeOut;
	}

	/**
	 * @return 바이트 순서
	 */
	public final ByteOrder getByteOrderOfProject() {
		return commonProjectInfo.byteOrderOfProject;
	}

	/**
	 * @return 문자셋
	 */
	public final java.nio.charset.Charset getCharsetOfProject() {
		return commonProjectInfo.charsetOfProject;
	}

	

	/**
	 * @return 이진 데이터 형식
	 */
	public final CommonType.MESSAGE_PROTOCOL getBinaryFormatType() {
		return commonProjectInfo.messageProtocol;
	}

	
	
	
	
	/**
	 * @return 소켓 채널 전용 읽기 자원
	 */
	public MessageInputStreamResourcePerSocket getMessageInputStreamResource() {
		
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
	 * 입력 메시지를 받아 서버로 보낸후 출력 메시지를 얻더 반환한다.
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
	 * @throws MessageInfoNotFoundException 메시지 정보가 없을때 던지는 예외
	 */
	abstract public LetterFromServer sendInputMessage(
			InputMessage inputMessage) throws ServerNotReadyException,
			SocketTimeoutException, NoMoreDataPacketBufferException,
			BodyFormatException, MessageInfoNotFoundException;
	
	
	/**
	 * 메시지를 보내기만 한다. 파일 전송에 유효하다.
	 * @param inputMessage 입력 메시지
	 * @throws ServerNotReadyException 서버 연결 실패시 발생
	 * @throws SocketTimeoutException 서버 응답 시간 초과시 발생
	 * @throws NoMoreDataPacketBufferException 래퍼 메시지를 만들때 데이터 패킷 버퍼 큐에서 버퍼를 확보하는데 실패할때 발생
	 * @throws BodyFormatException 스트림에서 메시지로, 메시지에서 스트림으로 바꿀때 바디 부분 구성 실패시 발생
	 * @throws MessageInfoNotFoundException 메시지 정보가 없을때 던지는 예외
	 * @throws NotSupportedException 공유+비동기 연결 객체에서 이 메소드 호출시 던지는 예외, 공유+비동기 연결 폴은 직접적으로 연결 객체를 받을 수 없음.
	 */
	abstract public void sendOnlyInputMessage(
			InputMessage inputMessage) throws ServerNotReadyException,
			SocketTimeoutException, NoMoreDataPacketBufferException,
			BodyFormatException, MessageInfoNotFoundException, NotSupportedException;
	
	
	@Override
	public String toString() {
		StringBuilder strBuffer = new StringBuilder();
		strBuffer.append("projectName=[");
		strBuffer.append(commonProjectInfo.projectName);
		strBuffer.append("], index=[");
		strBuffer.append(index);
		strBuffer.append(", serverSC=[");
		if (null != serverSC) {
			strBuffer.append(serverSC.hashCode());
		} else {
			strBuffer.append("null");
		}
		strBuffer.append("]");
		strBuffer.append(CommonStaticFinal.NEWLINE);
		strBuffer.append(commonProjectInfo.toString());
		strBuffer.append(CommonStaticFinal.NEWLINE);
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
		strBuffer.append(commonProjectInfo.projectName);
		strBuffer.append("], index=[");
		strBuffer.append(index);
		strBuffer.append(", serverSC=[");
		if (null != serverSC) {
			strBuffer.append(serverSC.hashCode());
		} else {
			strBuffer.append("null");
		}
		strBuffer.append("]");

		return strBuffer.toString();
	}
}
