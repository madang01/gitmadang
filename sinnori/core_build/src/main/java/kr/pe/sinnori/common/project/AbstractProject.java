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

package kr.pe.sinnori.common.project;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.etc.CharsetUtil;
import kr.pe.sinnori.common.etc.ObjectCacheManager;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.DataPacketBufferPool;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;
import kr.pe.sinnori.common.protocol.dhb.DHBMessageProtocol;
import kr.pe.sinnori.common.protocol.djson.DJSONMessageProtocol;
import kr.pe.sinnori.common.protocol.thb.THBMessageProtocol;

/**
 * 프로젝트 부모 추상화 클래스. 서버/클라이언트 프로젝트 공통 분모를 모은 추상화 클래스이다.
 * 
 * @author Won Jonghoon
 * 
 */
public abstract class AbstractProject {
	protected Logger log = LoggerFactory.getLogger(AbstractProject.class);
	
	protected ProjectPartConfiguration projectPartConfiguration = null;
	
	protected ObjectCacheManager objectCacheManager = ObjectCacheManager
			.getInstance();	
	
	protected MessageProtocolIF messageProtocol = null;
	
	
	protected DataPacketBufferPoolIF dataPacketBufferPoolManager = null;
	
	protected CharsetEncoder charsetEncoderOfProject = null;
	
	protected CharsetDecoder charsetDecoderOfProject = null;
	
	

	/**
	 * 생성자
	 * 
	 * @param projectPartConfiguration
	 *            프로젝트 파트 설정 내용
	 * @throws NoMoreDataPacketBufferException 
	 */
	public AbstractProject(ProjectPartConfiguration projectPartConfiguration) throws NoMoreDataPacketBufferException {
		this.projectPartConfiguration = projectPartConfiguration;		
		
		charsetEncoderOfProject = CharsetUtil.createCharsetEncoder(projectPartConfiguration.getCharset());
		charsetDecoderOfProject = CharsetUtil.createCharsetDecoder(projectPartConfiguration.getCharset());
		
		/*projectName = projectPartConfiguration.getProjectName();
		hostOfProject = projectPartConfiguration.getServerHost();
		portOfProject = projectPartConfiguration.getServerPort();
		byteOrderOfProject = projectPartConfiguration.getByteOrder();
		charsetOfProject = projectPartConfiguration.getCharset();
		charsetEncoderOfProject = CharsetUtil.createCharsetEncoder(charsetOfProject);
		charsetDecoderOfProject = CharsetUtil.createCharsetDecoder(charsetOfProject);
		classLoaderClassPackagePrefixName = projectPartConfiguration.getClassLoaderClassPackagePrefixName();
		int dataPacketBufferSize = projectPartConfiguration.getDataPacketBufferSize();
		dataPacketBufferMaxCntPerMessage = projectPartConfiguration.getDataPacketBufferMaxCntPerMessage();
		
		int messageIDFixedSize = projectPartConfiguration.getMessageIDFixedSize();		
		int dataPacketBufferPoolSize = projectPartConfiguration.getDataPacketBufferPoolSize();
		MessageProtocolType messageProtocolGubun = projectPartConfiguration.getMessageProtocol();*/
		
		boolean isDirect = false;
		this.dataPacketBufferPoolManager = new DataPacketBufferPool(isDirect, 
				projectPartConfiguration.getByteOrder()
						, projectPartConfiguration.getDataPacketBufferSize()
						, projectPartConfiguration.getDataPacketBufferPoolSize());
		

		switch (projectPartConfiguration.getMessageProtocolType()) {
			case DHB: {
				messageProtocol = new DHBMessageProtocol(
						projectPartConfiguration.getMessageIDFixedSize(), 
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(),
						charsetEncoderOfProject, charsetDecoderOfProject, 
						dataPacketBufferPoolManager);
	
				break;
			}
			case DJSON: {
				messageProtocol = new DJSONMessageProtocol(
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(), 
						charsetEncoderOfProject, charsetDecoderOfProject, 
						dataPacketBufferPoolManager);
				break;
			}
			case THB: {
				messageProtocol = new THBMessageProtocol(
						projectPartConfiguration.getMessageIDFixedSize(), 
						projectPartConfiguration.getDataPacketBufferMaxCntPerMessage(), 
						charsetEncoderOfProject, charsetDecoderOfProject, dataPacketBufferPoolManager);
				break;
			}
			default: {
				log.error(String.format("project[%s] 지원하지 않는 메시지 프로토콜[%s] 입니다.",
						projectPartConfiguration.getProjectName(), projectPartConfiguration
								.getMessageProtocolType().toString()));
				System.exit(1);
			}
		}
	}
	
	public final ByteOrder getByteOrder() {
		return projectPartConfiguration.getByteOrder();
	}

		
	
	
	public String getProjectName() {
		return projectPartConfiguration.getProjectName();
	}
	
	public Charset getCharset() {
		return projectPartConfiguration.getCharset();
	}


	public String getHostOfProject() {
		return projectPartConfiguration.getServerHost();
	}


	public int getPortOfProject() {
		return projectPartConfiguration.getServerPort();
	}
	
}
