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

package kr.pe.sinnori.client;

import java.util.HashMap;
import java.util.List;

import kr.pe.sinnori.common.config.SinnoriConfiguration;
import kr.pe.sinnori.common.config.SinnoriConfigurationManager;
import kr.pe.sinnori.common.config.itemvalue.AllSubProjectPartConfiguration;
import kr.pe.sinnori.common.config.itemvalue.ProjectPartConfiguration;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NoMoreOutputMessageQueueException;
import kr.pe.sinnori.common.exception.NotFoundProjectException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 클라이언트 프로젝트 관리자
 * 
 * @author Won Jonghoon
 * 
 */
public final class MainClientManager {
	private Logger log = LoggerFactory.getLogger(MainClientManager.class);
	
	/** 모니터 객체 */
	// private final Object monitor = new Object();
	
	private HashMap<String, AnyProjectClient> subProjectClientHash = new HashMap<String, AnyProjectClient>();
	
	private AnyProjectClient mainProjectClient = null;
	
	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class ClientProjectManagerHolder {
		static final MainClientManager singleton = new MainClientManager();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static MainClientManager getInstance() {
		return ClientProjectManagerHolder.singleton;
	}
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 * @throws NoMoreDataPacketBufferException 
	 */
	private MainClientManager() {
		SinnoriConfiguration sinnoriRunningProjectConfiguration = 
				SinnoriConfigurationManager.getInstance()
				.getSinnoriRunningProjectConfiguration();
		ProjectPartConfiguration mainProjectPart = sinnoriRunningProjectConfiguration.getMainProjectPartConfiguration();
		AllSubProjectPartConfiguration allSubProjectPart = sinnoriRunningProjectConfiguration.getAllSubProjectPartConfiguration();
		
		try {
			mainProjectClient = new AnyProjectClient(mainProjectPart);
		} catch (NoMoreDataPacketBufferException | NoMoreOutputMessageQueueException | InterruptedException e) {
			log.error("fail to make main client project instance", e);
			System.exit(1);
		}
		
		List<String> subProjectNamelist = allSubProjectPart.getSubProjectNamelist();
				
		for (String subProjectName : subProjectNamelist) {
			AnyProjectClient subClientProject=null;
			try {
				subClientProject = new AnyProjectClient(allSubProjectPart.getSubProjectPartConfiguration(subProjectName));
			} catch (NoMoreDataPacketBufferException | NoMoreOutputMessageQueueException | InterruptedException e) {
				log.error("fail to make sub client project instance", e);
				System.exit(1);
			}
			subProjectClientHash.put(subProjectName, subClientProject);
		}
	}
	
	/**
	 * 프로젝트 이름에 해당하는 외부에서 바라보는 시각을 가지는 클라이언트 프로젝트를 얻는다. 
	 * @param subProjectName 프로젝트 이름
	 * @return 프로젝트 이름에 해당하는 외부 시각 클라이언트 프로젝트
	 * @throws NotFoundProjectException 
	 */
	public AnyProjectClient getSubProjectClient(String subProjectName) throws NotFoundProjectException {
		AnyProjectClient subProjectClient =  subProjectClientHash.get(subProjectName);
		if (null == subProjectClient) {
			StringBuilder errorBuilder = new StringBuilder("신놀이 프레임 워크 환경설정 파일에 찾고자 하는 클라이언트 프로젝트[");
			errorBuilder.append(subProjectName);
			errorBuilder.append("] 가 존재하지 않습니다.");
			log.error(errorBuilder.toString());
			throw new NotFoundProjectException(errorBuilder.toString());
			// System.exit(1);
		}
		
		return subProjectClient;
	}
	
	public AnyProjectClient getMainProjectClient() {
		return mainProjectClient;
	}
}
