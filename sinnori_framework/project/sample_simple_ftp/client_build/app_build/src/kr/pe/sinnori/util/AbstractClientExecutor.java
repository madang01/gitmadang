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

package kr.pe.sinnori.util;

import java.net.SocketTimeoutException;

import kr.pe.sinnori.client.ClientProjectIF;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.NoMatchOutputMessage;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.ServerExcecutorUnknownException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.MessageMangerIF;

/**
 * 신놀이 기동 편의 기능 클래스({@link SinnoriWorker}) 종속 클라이언트 비지니스 로직 부모 추상화 클래스
 * @author Jonghoon Won
 *
 */
public abstract class AbstractClientExecutor implements CommonRootIF {
	/**
	 * 클라이언트 비지니스 로직을 수행한다. 
	 * @param messageManger
	 * @param clientProject
	 * @throws InterruptedException
	 */
	public void execute(MessageMangerIF messageManger, ClientProjectIF clientProject) throws InterruptedException {
		long firstErraseTime = new java.util.Date().getTime();
		
		try {
			doTask(messageManger, clientProject);
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (ServerNotReadyException e) {
			e.printStackTrace();
		} catch (DynamicClassCallException e) {
			e.printStackTrace();
		} catch (NoMoreDataPacketBufferException e) {
			e.printStackTrace();
		} catch (BodyFormatException e) {
			e.printStackTrace();
		} catch (MessageInfoNotFoundException e) {
			e.printStackTrace();
		} catch(MessageItemException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception e) {
			log.warn("Exception::통제 못한 에러 발생", e);
		}
		
		long lastErraseTime = new java.util.Date().getTime() - firstErraseTime;
		log.info(String.format("수행 시간=[%f] ms", (float) lastErraseTime));
	}
	
	
	/**
	 * 지정된 반복 횟수 만큼 클라이언트 비지니스 로직을 수행한다. 
	 * @param clientProject 외부 시선 클라이언트 프로젝트
	 * @param count 반복 횟수
	 * @throws InterruptedException 쓰레드 인터럽트
	 */
	public void execute(MessageMangerIF messageManger, ClientProjectIF clientProject, int count) throws InterruptedException {
		long firstErraseTime = new java.util.Date().getTime();
		
		try {
			for (int i=0; i < count; i++) {
				try {
					doTask(messageManger, clientProject);
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
				}
			}
		} catch (ServerNotReadyException e) {
			e.printStackTrace();
		} catch (DynamicClassCallException e) {
			e.printStackTrace();
		} catch (NoMoreDataPacketBufferException e) {
			e.printStackTrace();
		} catch (BodyFormatException e) {
			e.printStackTrace();
		} catch (MessageInfoNotFoundException e) {
			e.printStackTrace();
		} catch(ServerExcecutorUnknownException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception e) {
			log.warn("Exception::통제 못한 에러 발생", e);
		}
		
		long lastErraseTime = new java.util.Date().getTime() - firstErraseTime;
		log.info(String.format("[%d]회 실행 평균 수행 시간=[%f] ms", count, (float) lastErraseTime / count));
	}
	
	
	/**
	 * 개발자가 작성해야할 클라이언트 비지니스 로직이 담기는 메소드
	 * @param messageManger 메시지 
	 * @param clientProject
	 * @throws SocketTimeoutException
	 * @throws ServerNotReadyException
	 * @throws DynamicClassCallException
	 * @throws NoMoreDataPacketBufferException
	 * @throws BodyFormatException
	 * @throws MessageInfoNotFoundException
	 * @throws MessageItemException
	 * @throws NoMatchOutputMessage
	 * @throws InterruptedException
	 * @throws ServerExcecutorUnknownException
	 */
	abstract protected void doTask(MessageMangerIF messageManger, ClientProjectIF clientProject)
			throws SocketTimeoutException, ServerNotReadyException, DynamicClassCallException, 
			NoMoreDataPacketBufferException, BodyFormatException, 
			MessageInfoNotFoundException, MessageItemException, NoMatchOutputMessage, ServerExcecutorUnknownException, InterruptedException;
}
