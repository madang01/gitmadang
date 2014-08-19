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

import java.util.HashMap;
import java.util.StringTokenizer;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotFoundProjectException;
import kr.pe.sinnori.common.lib.CommonRootIF;



/**
 * 서버 프로젝트 관리자
 * 
 * @author Jonghoon Won
 * 
 */
public final class ServerProjectManager implements CommonRootIF {
	/** 모니터 객체 */
	// private final Object monitor = new Object();
	
	HashMap<String, ServerProject> serverProjectHash = new HashMap<String, ServerProject>(); 
	

	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class ServerProjectManagerHolder {
		static final ServerProjectManager singleton = new ServerProjectManager();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static ServerProjectManager getInstance() {
		return ServerProjectManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 * @throws NoMoreDataPacketBufferException 
	 */
	private ServerProjectManager() {
		String projectlist = (String)conf.getResource("common.projectlist.value");
		
		StringTokenizer tokenProject = new StringTokenizer(projectlist, ",");
		while(tokenProject.hasMoreElements()) {
			String projectName = tokenProject.nextToken().trim();
			
			ServerProject serverProject=null;
			try {
				serverProject = new ServerProject(projectName);
			} catch (NoMoreDataPacketBufferException e) {
				log.error("NoMoreDataPacketBufferException", e);
				System.exit(1);
			}
			serverProjectHash.put(projectName, serverProject);
		}
	}
	
	/**
	 * 프로젝터 이름에 1:1 대응하는 서버 프로젝트를 반환한다.
	 * @param projectName 프로젝트 이름
	 * @return 프로젝터 이름에 1:1 대응하는 서버 프로젝트 {@link ServerProject}
	 * @throws NotFoundProjectException 
	 */
	public ServerProject getServerProject(String projectName) throws NotFoundProjectException {
		ServerProject serverProject =  serverProjectHash.get(projectName);
		if (null == serverProject) {
			StringBuilder errorBuilder = new StringBuilder("신놀이 프레임 워크 환경설정 파일에 찾고자 하는 서버 프로젝트[");
			errorBuilder.append(projectName);
			errorBuilder.append("] 가 존재하지 않습니다.");
			log.error(errorBuilder.toString());
			throw new NotFoundProjectException(errorBuilder.toString());
			// System.exit(1);
		}
		
		return serverProject;
	}
}
