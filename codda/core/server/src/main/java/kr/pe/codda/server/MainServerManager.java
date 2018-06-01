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

package kr.pe.codda.server;

import java.util.HashMap;
import java.util.List;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.buildsystem.pathsupporter.ServerBuildSytemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.subset.AllSubProjectPartConfiguration;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.CoddaConfigurationException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;



/**
 * 서버 프로젝트 관리자
 * 
 * @author Won Jonghoon
 * 
 */
public final class MainServerManager {
	private InternalLogger log = InternalLoggerFactory.getInstance(MainServerManager.class);
	
	/** 모니터 객체 */
	// private final Object monitor = new Object();

	private List<String> subProjectNamelist = null;;
	private HashMap<String, AnyProjectServer> subProjectServerHash = new HashMap<String, AnyProjectServer>(); 
	private AnyProjectServer mainProjectServer = null;
	private ServerProjectMonitor serverProjectMonitor = null;
	private String installedPathString = null;
	private String mainProjectName = null;

	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class MainProjectServerManagerHolder {
		static final MainServerManager singleton = new MainServerManager();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static MainServerManager getInstance() {
		return MainProjectServerManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 * @throws NoMoreDataPacketBufferException 
	 */
	private MainServerManager() {
		// try {
			CoddaConfiguration sinnoriRunningProjectConfiguration = 
					CoddaConfigurationManager.getInstance()
					.getRunningProjectConfiguration();
			ProjectPartConfiguration mainProjectPartConfiguration = sinnoriRunningProjectConfiguration.getMainProjectPartConfiguration();
			AllSubProjectPartConfiguration allSubProjectPartConfiguration = sinnoriRunningProjectConfiguration.getAllSubProjectPartConfiguration();
			
			installedPathString = sinnoriRunningProjectConfiguration.getInstalledPathString();
			mainProjectName = mainProjectPartConfiguration.getProjectName();
			
			String serverAPPINFClassPathString = ServerBuildSytemPathSupporter
					.getServerAPPINFClassPathString(installedPathString, 
							mainProjectName);
			String projectResourcesPathString = ProjectBuildSytemPathSupporter.getProjectResourcesDirectoryPathString(installedPathString, mainProjectName);
			
			try {
				mainProjectServer = new AnyProjectServer(serverAPPINFClassPathString, projectResourcesPathString, mainProjectPartConfiguration);
			} catch (NoMoreDataPacketBufferException e) {
				log.warn("NoMoreDataPacketBufferException", e);
			} catch (CoddaConfigurationException e) {
				log.warn("CoddaConfigurationException", e);
			}
			
			subProjectNamelist = allSubProjectPartConfiguration.getSubProjectNamelist();
					
			for (String subProjectName : subProjectNamelist) {
				AnyProjectServer subProjectServer = null;
				try {
					subProjectServer = new AnyProjectServer(serverAPPINFClassPathString, projectResourcesPathString, allSubProjectPartConfiguration.getSubProjectPartConfiguration(subProjectName));
					
					subProjectServerHash.put(subProjectName, subProjectServer);
				} catch (NoMoreDataPacketBufferException e) {
					log.warn("NoMoreDataPacketBufferException", e);
				} catch (CoddaConfigurationException e) {
					log.warn("CoddaConfigurationException", e);
				}
				
			}
			
			serverProjectMonitor = new ServerProjectMonitor(
					mainProjectPartConfiguration.getServerMonitorTimeInterval());
			serverProjectMonitor.start();
	}
	
	/**
	 * 프로젝터 이름에 1:1 대응하는 서버 프로젝트를 반환한다.
	 * @param projectName 프로젝트 이름
	 * @return 프로젝터 이름에 1:1 대응하는 서버 프로젝트 {@link AnyProjectServer}
	 * @throws IllegalStateException 
	 */
	public AnyProjectServer getSubProjectServer(String subProjectName) throws IllegalStateException {
		AnyProjectServer subProjectServer =  subProjectServerHash.get(subProjectName);
		if (null == subProjectServer) {
			StringBuilder errorBuilder = new StringBuilder("신놀이 프레임 워크 환경설정 파일에 찾고자 하는 서버 프로젝트[");
			errorBuilder.append(subProjectName);
			errorBuilder.append("] 가 존재하지 않습니다.");
			log.error(errorBuilder.toString());
			throw new IllegalStateException(errorBuilder.toString());
		}
		
		return subProjectServer;
	}
	
	public AnyProjectServer getMainProjectServer() throws IllegalStateException {
		if (null == mainProjectServer) {
			StringBuilder errorBuilder = new StringBuilder("신놀이 프레임 워크 환경설정 파일에 찾고자 하는 메인 프로젝트가 존재하지 않습니다.");
			log.error(errorBuilder.toString());
			throw new IllegalStateException(errorBuilder.toString());
		}
		
		return mainProjectServer;
	}
	
	private class ServerProjectMonitor extends Thread {		
		private long serverMonitorTimeInterval;
		public ServerProjectMonitor(long serverMonitorTimeInterval) {
			this.serverMonitorTimeInterval = serverMonitorTimeInterval;
		}
		
		@Override
		public void run() {
			log.info("ServerProjectMonitor start");
			try {
				while (!Thread.currentThread().isInterrupted()) {
					log.info(getServerState());
					
					Thread.sleep(serverMonitorTimeInterval);
				}
			} catch(InterruptedException e) {
				log.info("ServerProjectMonitor::interrupr");
			} catch(Exception e) {
				log.info("ServerProjectMonitor::unknow error", e);
			}

			log.info("ServerProjectMonitor end");
		}
	}
	
	public String getServerState() {
		StringBuilder pollStateStringBuilder = new StringBuilder();
		pollStateStringBuilder.append("main projectName[");
		pollStateStringBuilder.append(mainProjectName);
		pollStateStringBuilder.append("]'s AnyProjectServer state");
		pollStateStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		pollStateStringBuilder.append(mainProjectServer.getProjectServerState());
		
		for (String subProjectName : subProjectNamelist) {
			AnyProjectServer subProjectServer = subProjectServerHash.get(subProjectName);
			pollStateStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			pollStateStringBuilder.append("sub projectName[");
			pollStateStringBuilder.append(subProjectName);
			pollStateStringBuilder.append("]'s AnyProjectServer state");
			pollStateStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			pollStateStringBuilder.append(subProjectServer.getProjectServerState());
		}
		
		return pollStateStringBuilder.toString();
	}
}
