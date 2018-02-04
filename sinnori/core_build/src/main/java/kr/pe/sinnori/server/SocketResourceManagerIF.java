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
 * @author Won Jonghoon
 *
 */
public interface SocketResourceManagerIF {	
	public void addNewSocketChannel(SocketChannel sc) throws NoMoreDataPacketBufferException; 
	
	
	public void remove(SocketChannel sc);
	
	/**
	 * 입력 받은 Client(=Socket Channel) 에 해당하는 ClientResource 를 반환한다.
	 * 
	 * @param sc
	 *            Client(=Socket Channel)
	 * @return ClientResource
	 */
	public SocketResource getSocketResource(SocketChannel sc);
	
	/**
	 * ClientManger가 관리하는 client 총수를 반환한다.
	 * 
	 * @return ClientManger가 관리하는 client 총수
	 */
	public int getNumberOfSocketResources();
}
