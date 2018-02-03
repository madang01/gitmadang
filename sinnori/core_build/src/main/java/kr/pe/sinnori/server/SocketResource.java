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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.io.SocketOutputStream;

/**
 * 서버에 접속하는 클라이언트 자원 클래스.
 * 
 * @author Won Jonghoon
 * 
 */
public class SocketResource {
	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(SocketResource.class);

	/** 이 자원을 소유한 소켓 채널 */
	private SocketChannel ownerSC = null;	
	
	/** 최종 읽기를 수행한 시간. 초기값은 클라이언트(=SocketChannel) 생성시간이다 */
	private java.util.Date finalReadTime = null;
	
	/** 소켓 채널 전용 읽기 자원 */
	private SocketOutputStream socketOutputStream = null;
	
	private final Object monitorOfServerMailID = new Object();
	/** 클라이언트에 할당되는 서버 편지 식별자 */
	private int serverMailID = Integer.MIN_VALUE;
	
	private PersonalLoginManager personalLoginManager = null;
	
	public SocketResource(SocketChannel ownerSC,
			SocketOutputStream socketOutputStream,
			PersonalLoginManager personalLoginManager) {
		if (null == ownerSC) {
			throw new IllegalArgumentException("the parameter ownerSC is null");
		}
		
		if (null == socketOutputStream) {
			throw new IllegalArgumentException("the parameter socketOutputStream is null");
		}
		
		if (null == personalLoginManager) {
			throw new IllegalArgumentException("the parameter personalLoginManager is null");
		}
		
		
		this.ownerSC = ownerSC;
		this.finalReadTime = new java.util.Date();
		this.socketOutputStream = socketOutputStream;
		this.personalLoginManager = personalLoginManager;
	}

	/**
	 * @return 소켓 채널 전용 읽기 자원
	 */
	public SocketOutputStream getSocketOutputStream() {
		
		return socketOutputStream;
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
	 * 메일 식별자를 반환한다. 메일 식별자는 자동 증가된다.
	 */
	public int getServerMailID() {
		synchronized (monitorOfServerMailID) {
			if (Integer.MAX_VALUE == serverMailID) {
				serverMailID = Integer.MIN_VALUE;
			} else {
				serverMailID++;
			}
			return serverMailID;
		}
	}
	
	
	public SocketChannel getOwnerSC() {
		return ownerSC;
	}
	
	public PersonalLoginManager getPersonalLoginManager() {
		return personalLoginManager;
	}
	
	public void close() {
		socketOutputStream.close();
		personalLoginManager.releaseLoginUserResource();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientResource [");		
		builder.append("ownerSC=");
		builder.append(ownerSC.hashCode());
		builder.append(", finalReadTime=");
		builder.append(finalReadTime);		
		builder.append(", serverMailID=");
		builder.append(serverMailID);
		builder.append("]");
		return builder.toString();
	}
}
