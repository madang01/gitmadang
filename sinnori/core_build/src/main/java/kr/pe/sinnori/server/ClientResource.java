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

import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.io.SocketInputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResourceManager;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.io.LetterToClient;

/**
 * 서버에 접속하는 클라이언트 자원 클래스.
 * 
 * @author Won Jonghoon
 * 
 */
public class ClientResource {
	private Logger log = LoggerFactory.getLogger(ClientResource.class);
	
	private final Object monitorOfServerMailID = new Object();
	
	private String projectName = null;

	/** 이 자원을 소유한 소켓 채널 */
	private SocketChannel clientSC = null;
	
	// private ProjectPart projectPart = null;
	
	/**
	 * 최종 읽기를 수행한 시간. 초기값은 클라이언트(=SocketChannel) 생성시간이다.
	 */
	private java.util.Date finalReadTime = null;
	/**
	 * echo 메세지를 보낸 횟수.
	 */
	private int echoMesgCount = 0;
	
	/** 소켓 채널 전용 읽기 자원 */
	private SocketInputStream socketInputStream = null;

	/**
	 * 클라이언트에 할당되는 서버 편지 식별자.
	 */
	private int serverMailID = Integer.MIN_VALUE;

	/** 로그인 아이디 */
	private String loginID = null;
	
	/**
	 * 생성자
	 * @param clientSC 서버에 접속한 클라이언트 소켓 채널
	 * @param clientProjectConfig 프로젝트의 클라이언트 환경 변수
	 * @param messageInputStreamResource 소켓 채널 전용 읽기 자원
	 */
	public ClientResource(SocketChannel clientSC, 
			String projectName,
			SocketInputStream socketInputStream) {
		this.clientSC = clientSC;
		this.projectName = projectName;
		this.finalReadTime = new java.util.Date();
		this.socketInputStream = socketInputStream;
		this.loginID = null;
	}

	/**
	 * @return 소켓 채널 전용 읽기 자원
	 */
	public SocketInputStream getSocketInputStream() {
		
		return socketInputStream;
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
	 * 메일 식별자를 반환한다. 메일 식별자는 자동 증가된다.
	 */
	public int getServerMailID() {
		/*if (null == clientSC) {
			log.warn("clientSC is null");
			return serverMailID;
		}*/

		synchronized (monitorOfServerMailID) {
			if (Integer.MAX_VALUE == serverMailID) {
				serverMailID = Integer.MIN_VALUE;
			} else {
				serverMailID++;
			}
			return serverMailID;
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
		//if (null == clientSC) return false;
		
		return (clientSC.isConnected() && null != loginID);
	}
	
	/** 로그 아웃시 할당 받은 자원을 해제한다. */
	public void logout() {
		// FIXME!
		log.info(String.format("clientSC[%d] logout", clientSC.hashCode()));
		
		socketInputStream.destory();
		
		LocalSourceFileResourceManager.getInstance().removeUsingUserIDWithUnlockFile(loginID);
		LocalTargetFileResourceManager.getInstance().removeUsingUserIDWithUnlockFile(loginID);
		
		loginID = null;
	}
	
	
	
	
	
	/**
	 * @return 메시지 데이터 수신중 여부, true 이면 메시지 데이터 수신중, false 이면 메시지 데이터 수신 대기중
	 */
	public boolean isReading() {
		return socketInputStream.isReading();
	}
	
	public LetterToClient getLetterToClient(AbstractMessage messageToClient, ArrayList<WrapBuffer> wrapBufferList) {		
		LetterToClient letterToClient = new LetterToClient(clientSC, messageToClient, wrapBufferList);		
		return letterToClient;
	}
	
	public ArrayList<WrapBuffer> getMessageStream(AbstractServerTask serverTask, String messageIDFromClient, 
			AbstractMessage  messageToClient,
			Charset projectCharset,
			MessageProtocolIF messageProtocol,			
			ServerObjectCacheManagerIF serverObjectCacheManager) {
		return serverTask.getMessageStream(messageIDFromClient, clientSC, messageToClient, projectCharset, messageProtocol, serverObjectCacheManager);
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientResource [");
		builder.append("projectName=");
		builder.append(projectName);
		builder.append(", clientSC=");
		builder.append(clientSC.hashCode());
		builder.append(", monitorOfServerMailID=");
		builder.append(monitorOfServerMailID);
		builder.append(", finalReadTime=");
		builder.append(finalReadTime);
		builder.append(", echoMesgCount=");
		builder.append(echoMesgCount);
		builder.append(", messageInputStreamResource.WrapBufferListSize=");
		builder.append(socketInputStream.getDataPacketBufferListSize());		
		builder.append(", serverMailID=");
		builder.append(serverMailID);
		builder.append(", loginID=");
		builder.append(loginID);
		// builder.append(", localTargetFileResourceManager=");
		// builder.append(localTargetFileResourceManager);
		builder.append("]");
		return builder.toString();
	}
	
	public String toSimpleString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientResource [");
		builder.append("projectName=");
		builder.append(projectName);
		builder.append(", clientSC=");
		builder.append(clientSC.hashCode());
		builder.append("]");
		return builder.toString();
	}
}
