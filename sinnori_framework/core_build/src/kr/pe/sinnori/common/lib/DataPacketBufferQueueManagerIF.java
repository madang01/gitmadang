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


package kr.pe.sinnori.common.lib;

import java.nio.ByteOrder;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

/**
 * 데이터 패킷 버퍼 관리자 인터페이스
 * @author Jonghoon Won
 *
 */
public interface DataPacketBufferQueueManagerIF {
	
	/**
	 * 클라이언트 프로젝트 자원인 데이터 패킷 버퍼 큐에서 데이터 패킷 버퍼를 얻어온다.
	 * @return 데이터 패킷 버퍼
	 */
	public WrapBuffer pollDataPacketBuffer(ByteOrder newByteOrder) throws NoMoreDataPacketBufferException;
	/**
	 * 클라이언트 프로젝트 자원인 데이터 패킷 버퍼 큐에 데이터 패킷 버퍼를 반환한다.
	 * @param buffer 데이터 패킷 버퍼
	 */
	public void putDataPacketBuffer(WrapBuffer buffer);
	
	/**
	 * @return 메시지 1개당 최대 데이터 패킷 갯수
	 */
	public int getDataPacketBufferMaxCntPerMessage();
	
	/**
	 * @return 데이터 패킷 버퍼 크기
	 */
	public int getDataPacketBufferSize();
	
	/**
	 * @return 큐 상태
	 */
	public String getQueueState();
}
