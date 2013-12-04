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

import java.util.concurrent.LinkedBlockingQueue;

import kr.pe.sinnori.client.ClientProjectIF;
import kr.pe.sinnori.common.lib.CommonProjectInfo;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.message.OutputMessage;

/**
 * 클라이언트 연결 클래스 폴 관리자 부모 추상화 클래스.
 * 
 * @author Jonghoon Won
 * 
 */
public abstract class AbstractConnectionPool implements ClientProjectIF, CommonRootIF {
	/** 모니터 */
	protected final Object monitor = new Object();
	
	/**
	 * 연결 클래스 묶음은 서버 이름으로 추상화된 1개의 연결 클래스로<br/>
	 * 연결에 필요한 정보는 연결 클래스에서 공통적으로 사용되어야 한다.
	 */
	protected CommonProjectInfo commonProjectInfo = null;

	/**
	 * 서버에서 공지등 불특정 다수한테 메시지를 보낼때 출력 메시지를 담은 큐
	 */
	protected LinkedBlockingQueue<OutputMessage> serverOutputMessageQueue = null;

	
	/**
	 * 생성자
	 * @param commonProjectInfo 연결 공통 데이터
	 * @param serverOutputMessageQueue 서버에서 보내는 불특정 다수 메시지를 받는 큐
	 */
	protected AbstractConnectionPool(CommonProjectInfo commonProjectInfo, LinkedBlockingQueue<OutputMessage> serverOutputMessageQueue) {
		this.commonProjectInfo = commonProjectInfo;
		this.serverOutputMessageQueue = serverOutputMessageQueue;
	}
	
	@Override
	public CommonProjectInfo getCommonProjectInfo() {
		return commonProjectInfo;
	}
	
	/**
	 * @return 메일함 갯수
	 */
	abstract public int getUsedMailboxCnt();

	/**
	 * @return 전체 메일함 갯수
	 */
	abstract public int getTotalMailbox();
}
