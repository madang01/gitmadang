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

package kr.pe.sinnori.common.configuration;

import kr.pe.sinnori.common.lib.CommonType.CONNECTION_TYPE;


/**
 * 프로젝트의 공통 포함한 클라이언트 환경 변수 접근 인터페이스.
 * @author Jonghoon won
 *
 */
public interface ClientProjectConfigIF extends CommonProjectConfigIF {
	
	/**
	 * @return 연결 종류, NoShareAsyn(비공유+비동기), ShareAsyn(공유+비동기), NoShareSync(비공유+동기)
	 */
	public CONNECTION_TYPE getConnectionType();

	

	/**
	 * 연결 클래스 갯수를 반환한다.
	 * @return 연결 클래스 갯수
	 */
	public int getClientConnectionCount();

	/**
	 * 연결 생성시 자동 접속 여부
	 * @return 연결 생성시 자동 접속 여부
	 */
	public boolean getClientWhetherToAutoConnect();

	/**
	 * 소켓 타임 아웃 시간
	 * @return 소켓 타임 아웃 시간
	 */
	public long getClientSocketTimeout();

	/**
	 * 출력 메시지 큐 크기, 소켓 채널 blocking 모드가 false 즉 비동기 방식 연결 클래스에서만 유효하다.
	 * @return 출력 메시지 큐 크기
	 */
	public int getClientOutputMessageQueueSize();

	/**
	 * 메일함 갯수, 소켓 채널 blocking 모드가 false 즉 비동기 방식 연결 클래스에서만 유효하다.
	 * @return 메일함 갯수
	 */
	public int getClientShareAsynConnMailboxCnt();
	
	/**
	 * @return 클라이언트 비동기 소켓 채널의 연결 확립 최대 시도 횟수
	 */
	public int getClientFinishConnectMaxCall();
	
	/**
	 * @return 클라이언트 비동기 소켓 채널의 연결 확립을 재 시도 간격
	 */
	public long getClientFinishConnectWaittingTime();
	
	/**
	 * @return 비동기 출력 메시지 처리자 쓰레드 갯수
	 */
	public int getClientAsynOutputMessageExecutorThreadCnt();
	
	/**
	 * @return 입력 메시지 소켓 쓰기 담당 쓰레드 초기 갯수
	 */
	public int getClientInputMessageWriterSize();

	/**
	 * @return 입력 메시지 소켓 쓰기 담당 쓰레드 최대 갯수
	 */
	public int getClientInputMessageWriterMaxSize();

	/**
	 * @return 클라이언트 출력 메시지 소켓 읽기 담당 쓰레드 초기 갯수
	 */
	public int getClientOutputMessageReaderSize();

	/**
	 * @return 출력 메시지 소켓 읽기 담당 쓰레드 최대 갯수
	 */
	public int getClientOutputMessageReaderMaxSize();

	/**
	 * @return 출력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기
	 */
	public long getClientReadSelectorWakeupInterval();

	/**
	 * @return 클라이언트 입력 메시지 큐 크기
	 */
	public int getClientInputMessageQueueSize();
	
	/**
	 * 데이터 패킷 버퍼 수
	 * @return 데이터 패킷 버퍼 수
	 */
	public int getClientDataPacketBufferCnt();
	
	/**
	 * @return 프로젝트 모니터링 시간 간격, 단위 ms.
	 */
	public long getClientMonitorTimeInterval();
	
	/**
	 * @return 데이터를 송신하지 않고 기다려주는 최대 시간, 단위 ms, 이 시간 초과된 클라이언트는 소켓을 닫은다. 
	 */
	public long getClientRequestTimeout();
	
	/**
	 * @return 프로젝트의 공통 환경 변수들 제외한 클라이언트 환경 변수들만의 toString 
	 */
	public String toClientString();
}
