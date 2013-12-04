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
import java.nio.charset.Charset;

import kr.pe.sinnori.common.io.MessageExchangeProtocolIF;


/**
 * 공통 환경 변수들중 네트워크에서 메시지 교환에 필요한 변수들 묶음 클래스
 * 
 * @author Jonghoon Won
 * 
 */
public class CommonProjectInfo {
	public String projectName = null;
	/** 서버 호스트 */
	public String serverHost = null;
	/** 서버 포트 */
	public int serverPort = -1;
	/** 이진 데이터 형식 종류 */
	public CommonType.MESSAGE_PROTOCOL messageProtocol = null;
	/** 바이트 순서 */
	public ByteOrder byteOrderOfProject = null;
	/** 문자셋 */
	public Charset charsetOfProject = null;
	/** 데이터 패킷 버퍼 크기 */
	public int dataPacketBufferSize;
	/** 메시지당 최대 데이터 패킷 할당 갯수 */
	public int dataPacketBufferMaxCntPerMessage;
	
	
	public MessageExchangeProtocolIF messageExchangeProtocol = null;
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConnectionPoolCommonData [projectName=");
		builder.append(projectName);
		builder.append(", serverHost=");
		builder.append(serverHost);
		builder.append(", serverPort=");
		builder.append(serverPort);
		builder.append(", messageProtocol=");
		builder.append(messageProtocol);
		builder.append(", clientByteOrder=");
		builder.append(byteOrderOfProject);
		builder.append(", clientCharset=");
		builder.append(charsetOfProject.toString());
		builder.append(", dataPacketBufferSize=");
		builder.append(dataPacketBufferSize);
		builder.append(", dataPacketBufferMaxCntPerMessage=");
		builder.append(dataPacketBufferMaxCntPerMessage);
		builder.append(", messageExchangeProtocol=");
		builder.append(messageExchangeProtocol.toString());
		builder.append("]");
		return builder.toString();
	}	
}
