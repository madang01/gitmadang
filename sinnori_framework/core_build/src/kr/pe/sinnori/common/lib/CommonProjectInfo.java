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


/**
 * 공통 환경 변수들중 네트워크에서 메시지 교환에 필요한 변수들 묶음 클래스
 * 
 * @author Jonghoon Won
 * 
 */
public class CommonProjectInfo {
	private String projectName = null;
	/** 서버 호스트 */
	private String serverHost = null;
	/** 서버 포트 */
	private int serverPort = -1;
	/** 이진 데이터 형식 종류 */
	private CommonType.MESSAGE_PROTOCOL messageProtocol = null;
	/** 바이트 순서 */
	private ByteOrder byteOrderOfProject = null;
	/** 문자셋 */
	private Charset charsetOfProject = null;
	/** 데이터 패킷 버퍼 크기 */
	private int dataPacketBufferSize;
	/** 메시지당 최대 데이터 패킷 할당 갯수 */
	private int dataPacketBufferMaxCntPerMessage;
	
	public CommonProjectInfo(String projectName, 
			String serverHost, int serverPort, 
			CommonType.MESSAGE_PROTOCOL messageProtocol,
			ByteOrder byteOrderOfProject,
			Charset charsetOfProject,
			int dataPacketBufferSize,
			int dataPacketBufferMaxCntPerMessage) {
		this.projectName = projectName;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.messageProtocol = messageProtocol;
		this.byteOrderOfProject = byteOrderOfProject;
		this.charsetOfProject = charsetOfProject;
		this.dataPacketBufferSize = dataPacketBufferSize;
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @return the serverHost
	 */
	public String getServerHost() {
		return serverHost;
	}

	/**
	 * @return the serverPort
	 */
	public int getServerPort() {
		return serverPort;
	}
		

	/**
	 * @param serverHost the serverHost to set
	 */
	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	/**
	 * @param serverPort the serverPort to set
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	/**
	 * @return the messageProtocol
	 */
	public CommonType.MESSAGE_PROTOCOL getMessageProtocol() {
		return messageProtocol;
	}

	/**
	 * @return the byteOrderOfProject
	 */
	public ByteOrder getByteOrderOfProject() {
		return byteOrderOfProject;
	}

	/**
	 * @return the charsetOfProject
	 */
	public Charset getCharsetOfProject() {
		return charsetOfProject;
	}

	/**
	 * @return the dataPacketBufferSize
	 */
	public int getDataPacketBufferSize() {
		return dataPacketBufferSize;
	}

	/**
	 * @return the dataPacketBufferMaxCntPerMessage
	 */
	public int getDataPacketBufferMaxCntPerMessage() {
		return dataPacketBufferMaxCntPerMessage;
	}

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
		builder.append("]");
		return builder.toString();
	}	
}
