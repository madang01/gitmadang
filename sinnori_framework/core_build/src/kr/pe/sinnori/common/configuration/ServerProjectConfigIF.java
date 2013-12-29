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

import java.io.File;
import java.util.TreeSet;

/**
 * 프로젝트의 공통 포함한 서버 환경 변수 접근 인터페이스.
 * @author Jonghoon won
 *
 */
public interface ServerProjectConfigIF extends CommonProjectConfigIF {
	/**
	 * @return 서버에 접속할 수 있는 클라이언트 최대 수
	 */
	public int getServerMaxClients();

	/**
	 * @return 서버 비지니스 로직 클래스명 접두어
	 */
	public String getServerExecutorPrefix();

	/**
	 * @return 서버 비지니스 로직 클래스명 접미어
	 */
	public String getServerExecutorSuffix();

	/**
	 * @return 서버 비지니스 로직 클래스 소스 파일 경로
	 */
	public File getServerExecutorSourcePath();

	/**
	 * @return 서버 비지니스 로직 클래스 파일 경로
	 */
	public File getServerExecutorClassPath();

	/**
	 * @return 접속 이벤트 전용 selector 에서 접속 이벤트 최대 대기 시간
	 */
	public long getServerAcceptSelectorTimeout();

	/**
	 * @return 접속 요청이 승락된 클라이언트의 등록을 담당하는 쓰레드 초기 갯수
	 */
	public int getServerAcceptProcessorSize();

	/**
	 * @return 접속 요청이 승락된 클라이언트의 등록을 담당하는 쓰레드 최대 갯수
	 */
	public int getServerAcceptProcessorMaxSize();

	/**
	 * @return 입력 메시지 소켓 읽기 담당 쓰레드 초기 갯수
	 */
	public int getServerInputMessageReaderSize();

	/**
	 * @return 입력 메시지 소켓 읽기 담당 쓰레드 최대 갯수
	 */
	public int getServerInputMessageReaderMaxSize();

	/**
	 * @return 입력 메시지 소켓 읽기 담당 쓰레드에서 블락된 읽기 이벤트 전용 selector 를 깨우는 주기
	 */
	public long getServerReadSelectorWakeupInterval();

	/**
	 * @return 서버 비지니스 로직 수행 담당 쓰레드 초기 갯수
	 */
	public int getServerExecutorProcessorSize();

	/**
	 * @return 서버 비지니스 로직 수행 담당 쓰레드 최대 갯수
	 */
	public int getServerExecutorProcessorMaxSize();

	/**
	 * @return 출력 메시지 소켓 쓰기 담당 쓰레드 초기 갯수
	 */
	public int getServerOutputMessageWriterSize();

	/**
	 * @return 출력 메시지 소켓 쓰기 담당 쓰레드 최대 갯수
	 */
	public int getServerOutputMessageWriterMaxSize();

	/**
	 * @return 접속 승인 큐 크기
	 */
	public int getServerAcceptQueueSize();
	
	/**
	 * @return 입력 메시지 큐 크기
	 */
	public int getServerInputMessageQueueSize();

	/**
	 * @return 출력 메시지 큐 크기
	 */
	public int getServerOutputMessageQueueSize();

	

	/**
	 * @return 데이터 패킷 버퍼 수
	 */
	public int getServerDataPacketBufferCnt();
	

	/**
	 * @return 데이터를 송신하지 않고 기다려주는 최대 시간, 단위 ms, 이 시간 초과된 클라이언트는 소켓을 닫은다. 
	 */
	public long getServerRequestTimeout();

	/**
	 * @return 프로젝트 모니터링 시간 간격, 단위 ms.
	 */
	public long getServerMonitorTimeInterval();
	
	/**
	 * @return 설정파일에서 정의한 익명 예외 발생 시키는 메시지 목록
	 */
	public TreeSet<String> getServerAnonymousExceptionInputMessageSet();
	
	/**
	 * @return 프로젝트의 공통 환경 변수들 제외한 서버 환경 변수들만의 toString
	 */
	public String toServerString();
}
