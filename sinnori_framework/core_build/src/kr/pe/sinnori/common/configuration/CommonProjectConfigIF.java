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
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import kr.pe.sinnori.common.lib.CommonType;

/**
 * 프로젝트의 공통 환경 변수 접근 인터페이스
 * @author Jonghoon won
 *
 */
public interface CommonProjectConfigIF {
	/**
	 * @return 프로젝트 명
	 */
	public String getProjectName();
	
	/**
	 * @return 메시지 정보 파일들이 위치한 경로
	 */
	public File getMessageInfoPath();

	/**
	 * @return 1개 메시지당 할당 받을 수 있는 바디 버퍼 최대수, 다른 말로 가변 크기 스트림에서 가질 수 있는 바디 버퍼 최대수
	 */
	public int getDataPacketBufferMaxCntPerMessage();
	
	/**
	 * @return 데이터 패킷 최대 크기
	 */
	public int getDataPacketBufferSize();
	
	/**
	 * @return 문자열인 메시지 식별자 최대 크기, 단위 byte
	 */
	public int getMessageIDFixedSize();
	
	/**
	 * @return 메시시 헤더 크기
	 */
	public int getMessageHeaderSize();
	
	/**
	 * @return 메시지 바디 최대 크기
	 */
	public int getMessageBodyMaxSize();
	
	/**
	 * @return 문자셋
	 */
	public Charset getCharset();
	
	/**
	 * @return 바이트 오더
	 */
	public ByteOrder getByteOrder();
	
	/**
	 * @return 이진 형식 종류
	 */
	public CommonType.MESSAGE_PROTOCOL getMessageProtocol();
	
	
	/**
	 * @return 서버 호스트 주소
	 */
	public String getServerHost();
	
	/**
	 * 새로운 서버 호스트 주소로 변경한다. configFileProperties 에도 반영.
	 * @param newServerHost 새로운 서버 호스트 주소
	 */
	public void setServerHost(String newServerHost);
	
	/**
	 * @return 서버 포트
	 */	
	public int getServerPort();
	
	/**
	 * 새로운 서버 포트로 변경한다. configFileProperties 에도 반영.
	 * @param newServerPort 새로운 서버 포트
	 */
	public void setServerPort(int newServerPort);
	
	/**
	 * 서버 접속 주소와 포트를 변경한다.
	 * @param newServerHost 새로운 서버 호스트 주소
	 * @param newServerPort 새로운 서버 포트
	 */
	public void changeServerAddress(String newServerHost, int newServerPort);
	
	/**
	 * @return 프로젝트의 공통 환경 변수들만의 toString 
	 */
	public String toCommonString();
}
