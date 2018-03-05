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
	public void addNewSocketChannel(SocketChannel sc) throws NoMoreDataPacketBufferException, InterruptedException;
	/** Warning! 소켓에 1:1 할당된 리소스는 닫지 않고 관리 대상에서 제거만 하므로 개별적으로 닫아 주어야 한다 */
	public void remove(SocketChannel sc);
	public SocketResource getSocketResource(SocketChannel sc);
	public int getNumberOfSocketResources();
}
