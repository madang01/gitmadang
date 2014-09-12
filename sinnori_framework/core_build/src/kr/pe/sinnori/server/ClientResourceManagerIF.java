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

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

/**
 * 서버에 접속하는 클라이언트 자원 관라자 인터페이스
 * @author Jonghoon Won
 *
 */
public interface ClientResourceManagerIF {
	/**
	 * 신규 Client 를 추가시 클라이언트 자원을 할당한다.<br/>
	 * 클라이언트 전용 읽기 전용 buffer, 문자셋, 메세지 이진 형식 등이 꾸려진다.
	 * 
	 * @param sc
	 *            신규 추가되는 client
	 * @throws NoMoreDataPacketBufferException
	 * @see ClientResource
	 */
	public void addNewClient(SocketChannel sc) throws NoMoreDataPacketBufferException; 
	
	/**
	 * Client에게 할당된 3가지 자원을 회수하고 "clinet 운영 정보"를 삭제한다.
	 * 
	 * @param sc
	 *            접속끊긴 client
	 */
	public void removeClient(SocketChannel sc);
	
	/**
	 * 입력 받은 Client(=Socket Channel) 에 해당하는 ClientResource 를 반환한다.
	 * 
	 * @param sc
	 *            Client(=Socket Channel)
	 * @return ClientResource
	 */
	public ClientResource getClientResource(SocketChannel sc);
	
	/**
	 * ClientManger가 관리하는 client 총수를 반환한다.
	 * 
	 * @return ClientManger가 관리하는 client 총수
	 */
	public int getCntOfAllClients();
}
