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
import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.lib.CommonRootIF;

/**
 * 신놀이 기동 편의 기능 클래스({@link SinnoriClientWorker}) 종속 클라이언트 비지니스 로직 부모 추상화 클래스
 * @author Won Jonghoon
 *
 */
public abstract class AbstractClientExecutor implements CommonRootIF {
	
	/**
	 * 클라이언트 비지니스 로직을 수행한다.
	 * @param clientProjectConfig 프로젝트의 클라이언트 환경 변수
	 * @param messageManger 프로젝트의 메시지 관리자
	 * @param clientProject 외부에 제공되는 프로젝트 기능 인터페이스
	 * @throws InterruptedException 쓰레드 인터럽트 발생시 던지는 예외
	 */
	public void execute(ClientProjectConfig clientProjectConfig, ClientProjectIF clientProject) throws InterruptedException {
		long firstErraseTime = new java.util.Date().getTime();
		
		try {
			doTask(clientProjectConfig, clientProject);
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
		} catch (NotLoginException e) {
			e.printStackTrace();
		} catch (Exception e) {
			log.warn("Exception::통제 못한 에러 발생", e);
		}
		
		long lastErraseTime = new java.util.Date().getTime() - firstErraseTime;
		log.info(String.format("수행 시간=[%f] ms", (float) lastErraseTime));
	}
		
	/**
	 * 지정된 반복 횟수 만큼 클라이언트 비지니스 로직을 수행한다.
	 * @param clientProjectConfig 프로젝트의 클라이언트 환경 변수
	 * @param messageManger 프로젝트의 메시지 관리자
	 * @param clientProject 외부에 제공되는 프로젝트 기능 인터페이스
	 * @param count 반복 횟수
	 * @throws InterruptedException 쓰레드 인터럽트 발생시 던지는 예외
	 */
	public void execute(ClientProjectConfig clientProjectConfig, ClientProjectIF clientProject, int count) throws InterruptedException {
		long firstErraseTime = new java.util.Date().getTime();
		
		try {
			for (int i=0; i < count; i++) {
				try {
					doTask(clientProjectConfig, clientProject);
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
		} catch (Exception e) {
			log.warn("Exception::통제 못한 에러 발생", e);
		}
		
		long lastErraseTime = new java.util.Date().getTime() - firstErraseTime;
		log.info(String.format("[%d]회 실행 평균 수행 시간=[%f] ms", count, (float) lastErraseTime / count));
	}
	
	
	/**
	 * 개발자가 작성해야할 클라이언트 비지니스 로직이 담기는 메소드
	 * @param clientProjectConfig 프로젝트의 클라이언트 환경 변수
	 * @param messageManger 프로젝트의 메시지 관리자
	 * @param clientProject 외부에 제공되는 프로젝트 기능 인터페이스
	 */
	abstract protected void doTask(ClientProjectConfig clientProjectConfig, ClientProjectIF clientProject)
			throws SocketTimeoutException, ServerNotReadyException, NoMoreDataPacketBufferException, 
			BodyFormatException, DynamicClassCallException, ServerTaskException, NotLoginException;
}
