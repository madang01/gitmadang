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


package kr.pe.codda.common.io;

import java.nio.ByteOrder;

import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

/**
 * 데이터 패킷 버퍼 관리자 인터페이스
 * @author Won Jonghoon
 *
 */
public interface DataPacketBufferPoolIF {
	
	/**
	 * 프로젝트 자원인 데이터 패킷 버퍼 큐에서 데이터 패킷 버퍼를 얻어온다.
	 * @return 데이터 패킷 버퍼
	 */
	public WrapBuffer pollDataPacketBuffer() throws NoMoreDataPacketBufferException;
	/**
	 * 프로젝트 자원인 데이터 패킷 버퍼 큐에 데이터 패킷 버퍼를 반환한다. 
	 * 이때 (1) 큐 등록 상태를 등록으로 
	 * (2) 데이터 패킷 버퍼 바이트 오더를 다시 프로잭트에서 설정된 값으로 재 설정시키고
	 * (3) 데이터 패킷 버퍼를 비어있는 상태로 만들기위해 ByteBuffer position, limit, mark 등 속성 초기화한다.
	 * @param buffer 데이터 패킷 버퍼
	 */
	public void putDataPacketBuffer(WrapBuffer buffer);
	
	
	/**
	 * @return 데이터 패킷 버퍼 크기
	 */
	public int getDataPacketBufferSize();
	
	/**
	 * @return 데이터 패킷 버퍼의 바이트 오더. 참고) 데이터 패킷 버퍼의 바이트 오더는 데이터 패킷 버퍼 관리자 생성시 결정되는 프로젝트의 바이트 오더이다.
	 */
	public ByteOrder getByteOrder();
	
	
	public int getDataPacketBufferPoolSize();
	
	public int size();
}
