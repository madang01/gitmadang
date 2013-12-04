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

package kr.pe.sinnori.server;

import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.lib.CommonProjectInfo;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonType;
import kr.pe.sinnori.common.lib.DataPacketBufferQueueManagerIF;
import kr.pe.sinnori.common.lib.MessageInputStreamResourcePerSocket;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResource;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResource;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResourceManager;

/**
 * 서버에 접속하는 클라이언트 자원 클래스.
 * 
 * @author Jonghoon Won
 * 
 */
public class ClientResource implements CommonRootIF {
	private String projectName = null;
	
	/** 이 자원을 소유한 소켓 채널 */
	private SocketChannel clientSC = null;

	/**
	 * 최종 읽기를 수행한 시간. 초기값은 클라이언트(=SocketChannel) 생성시간이다.
	 */
	private java.util.Date finalReadTime = null;
	/**
	 * echo 메세지를 보낸 횟수.
	 */
	private int echoMesgCount = 0;
	
	/** 소켓 채널 전용 읽기 자원 */
	private MessageInputStreamResourcePerSocket messageInputStreamResource = null;


	// 클라이언트와 메세지를 주고 받을때의 메세지 이진 형식.
	private CommonType.MESSAGE_PROTOCOL messageProtocol = null;	
	// 클라이언트와 메세지를 주고 받을때의 문자셋.
	private java.nio.charset.Charset clientCharset = null;	
	// 클라이언트 ByteOrder
	private ByteOrder clientByteOrder = null;

	 

	/**
	 * 클라이언트에 할당되는 서버 편지 식별자.
	 */
	private int serverMailID = Integer.MIN_VALUE;

	/** 로그인 아이디 */
	private String loginID = null;
	
	
	private HashSet<Integer> localSourceFileIDSet = new HashSet<Integer>(); 
	private HashSet<Integer> localTargetFileIDSet = new HashSet<Integer>();
	

	/**
	 * 생성자
	 * @param commonProjectInfo 연결 공통 데이터
	 * @param clientSC 서버에 접속한 클라이언트 소켓 채널
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자 
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼를 확보하지 못했을 경우 던지는 예외
	 */
	public ClientResource(CommonProjectInfo commonProjectInfo, SocketChannel clientSC, DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) throws NoMoreDataPacketBufferException {
		this.projectName = commonProjectInfo.projectName;
		this.clientSC = clientSC;
		this.finalReadTime = new java.util.Date();
		messageInputStreamResource = new MessageInputStreamResourcePerSocket(commonProjectInfo.byteOrderOfProject, dataPacketBufferQueueManager);
		setByteOrder(commonProjectInfo.byteOrderOfProject);
		setClientCharset(commonProjectInfo.charsetOfProject);
		setBinaryFormatType(commonProjectInfo.messageProtocol);
	}

	/**
	 * @return 소켓 채널 전용 읽기 자원
	 */
	public MessageInputStreamResourcePerSocket getMessageInputStreamResource() {
		
		return messageInputStreamResource;
	}
	/**
	 * @return 소켓 채널이 속한 프로젝트명
	 */
	public String getProjectName() {
		return projectName;
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
	 * 바이트 순서를 반환한다.
	 * 
	 * @return 바이트 순서
	 */	
	public ByteOrder getByteOrder() {
		return clientByteOrder;
	}

	/**
	 * 바이트 순서를 새롭게 설정한다.
	 * 
	 * @param newByteOrder
	 *            새로운 바이트 순서
	 */
	public void setByteOrder(ByteOrder newByteOrder) {
		clientByteOrder = newByteOrder;
		// readOnlyBuffer.order(newByteOrder);
		// messageInputStreamResource.getLastBuffer().order(newByteOrder);
	}

	/**
	 * 바이트 순서를 새롭게 설정한다.
	 * 
	 * @param newByteOrderStr
	 *            새로운 바이트 순서명
	 */
	public void setByteOrder(String newByteOrderStr) {
		ByteOrder newByteOrder;
		if (newByteOrderStr.equals(ByteOrder.BIG_ENDIAN.toString())) {
			newByteOrder = ByteOrder.BIG_ENDIAN;
		} else if (newByteOrderStr.equals(ByteOrder.LITTLE_ENDIAN.toString())) {
			newByteOrder = ByteOrder.LITTLE_ENDIAN;
		} else {
			throw new IllegalArgumentException(
					String.format(
							"바이트 오더 파라미터[%s] 값이 잘못되었습니다. 바이트 오더 파라미터 값은 [%s]와 [%s] 중 하나이어야 입니다.",
							newByteOrderStr, ByteOrder.BIG_ENDIAN.toString(),
							ByteOrder.LITTLE_ENDIAN.toString()));
		}
		setByteOrder(newByteOrder);
	}
	
	/**
	 * 클라이언트 문자셋을 반환한다.
	 * 
	 * @return 클라이이언트 문자셋
	 */
	public java.nio.charset.Charset getCharset() {
		return clientCharset;
	}

	/**
	 * 클라이언트 문자셋을 변경한고 동시에 문자셋에 종속된 디코더도 새롭게 갱신한다.
	 * 
	 * @param newClientCharset
	 *            새로운 클라이언트 문자셋
	 */
	public void setClientCharset(java.nio.charset.Charset newClientCharset) {
		this.clientCharset = newClientCharset;
	}

	
	/**
	 * 메세지 이진 형식을 반환한다.
	 * 
	 * @return 메세지 이진 형식
	 */
	public CommonType.MESSAGE_PROTOCOL getBinaryFormatType() {
		return messageProtocol;
	}

	/**
	 * 메세지 이진 형식을 변경한다.
	 * 
	 * @param newMessageBinaryFormat
	 *            새로운 메세지 이진 형식명
	 */
	public void setBinaryFormatType(String newMessageBinaryFormat) {
		if (null == newMessageBinaryFormat) {
			throw new RuntimeException("newMessageBinaryFormat is null");
		}

		CommonType.MESSAGE_PROTOCOL message_binary_format_values[] = CommonType.MESSAGE_PROTOCOL
				.values();
		int i = 0;
		while (i < message_binary_format_values.length) {
			if (newMessageBinaryFormat.equals(message_binary_format_values[i]
					.toString())) {
				messageProtocol = message_binary_format_values[i];
				break;
			}
			i++;
		}
		if (i == message_binary_format_values.length) {
			log.warn(String.format("잘못된 값[%s]을 지정하였습니다.", newMessageBinaryFormat));
		}
	}

	/**
	 * 메세지 이진 형식을 변경한다.
	 * 
	 * @param newMessageBinaryFormat
	 *            변경을 원하는 이진 형식
	 */
	public void setBinaryFormatType(
			CommonType.MESSAGE_PROTOCOL newMessageBinaryFormat) {
		if (null == newMessageBinaryFormat) {
			throw new RuntimeException("newMessageType is null");
		}
		messageProtocol = newMessageBinaryFormat;
	}

	/**
	 * 이 자원을 소유한 소켓 채널을 반환한다.
	 * 
	 * @return 이 자원을 소유한 소켓 채널
	 */
	public final SocketChannel getSocketChannel() {
		return clientSC;
	}

	/**
	 * 메일 식별자를 반환한다. 메일 식별자는 자동 증가된다.
	 */
	public int getServerMailID() {
		if (null == clientSC)
			return serverMailID;

		synchronized (clientSC) {
			int retValue = serverMailID;

			if (Integer.MAX_VALUE == serverMailID) {
				serverMailID = Integer.MIN_VALUE;
			} else {
				serverMailID++;
			}
			return retValue;
		}
	}

	/**
	 * 로그인 아이디.
	 * 
	 * @return 로그인 아이디, 만약 null이면 비 로그인 상태임
	 */
	public String getLoginID() {
		return loginID;
	}

	/**
	 * 로그인시 로그인 아이디를 저장한다.
	 * 
	 * @param newLoginID
	 *            로그인 아이디
	 */
	public void setLoginID(String newLoginID) {
		loginID = newLoginID;
	}
	
	/**
	 * 로그인 여부를 반환한다.
	 * @return 로그인 여부
	 */
	public boolean isLogin() {
		return (null != loginID);
	}
	
	/** 로그 아웃시 할당 받은 자원을 해제한다. */
	public void logout() {
		messageInputStreamResource.destory();
		
		LocalSourceFileResourceManager localSourceFileResourceManager = LocalSourceFileResourceManager.getInstance();
		Iterator<Integer> localSourceFileIDIterator = localSourceFileIDSet.iterator();
		while (localSourceFileIDIterator.hasNext()) {
			int localSourceFileID = localSourceFileIDIterator.next();
			LocalSourceFileResource  localSourceFileResource  = localSourceFileResourceManager.getLocalSourceFileResource(localSourceFileID);
			localSourceFileResourceManager.putLocalSourceFileResource(localSourceFileResource);
			
		}
		
		LocalTargetFileResourceManager localTargetFileResourceManager = LocalTargetFileResourceManager.getInstance();
		Iterator<Integer> localTargetFileIDIterator = localTargetFileIDSet.iterator();
		while(localTargetFileIDIterator.hasNext()) {
			int localTargetFileID = localTargetFileIDIterator.next();
			LocalTargetFileResource localTargetFileResource = localTargetFileResourceManager.getLocalTargetFileResource(localTargetFileID);
			localTargetFileResourceManager.putLocalTargetFileResource(localTargetFileResource);
		}
	}
	
	
	public void addLocalSourceFileID(int localSourceFileID) {
		localSourceFileIDSet.add(localSourceFileID);
	}
	
	public void remoteLocalSourceFileID(int localSourceFileID) {
		boolean isLocalSourceFileID = localSourceFileIDSet.remove(localSourceFileID);
		if (isLocalSourceFileID) {
			LocalSourceFileResourceManager localSourceFileResourceManager = LocalSourceFileResourceManager.getInstance();
			LocalSourceFileResource  localSourceFileResource = localSourceFileResourceManager.getLocalSourceFileResource(localSourceFileID);
			localSourceFileResourceManager.putLocalSourceFileResource(localSourceFileResource);
		}
	}
	
	public boolean isLocalSourceFileID(int localSourceFileID) {
		return localSourceFileIDSet.contains(localSourceFileID);
	}

	public void addLocalTargetFileID(int localTargetFileID) {
		localTargetFileIDSet.add(localTargetFileID);
	}
	
	public void removeLocalTargetFileID(int localTargetFileID) {
		boolean isLocalTargetFileID = localTargetFileIDSet.remove(localTargetFileID);
		if (isLocalTargetFileID) {
			LocalTargetFileResourceManager localTargetFileResourceManager = LocalTargetFileResourceManager.getInstance();
			LocalTargetFileResource localTargetFileResource = localTargetFileResourceManager.getLocalTargetFileResource(localTargetFileID);
			localTargetFileResourceManager.putLocalTargetFileResource(localTargetFileResource);
		}
	}
	
	public boolean isLocalTargetFileID(int localTargetFileID) {
		return localTargetFileIDSet.contains(localTargetFileID);
	}
	
	
	
	/**
	 * @return 메시지 데이터 수신중 여부, true 이면 메시지 데이터 수신중, false 이면 메시지 데이터 수신 대기중
	 */
	public boolean isReading() {
		return messageInputStreamResource.isReading();
	}
	
	
	
}
