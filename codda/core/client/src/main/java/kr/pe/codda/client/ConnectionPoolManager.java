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

package kr.pe.codda.client;

import java.util.HashMap;
import java.util.List;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.subset.AllSubProjectPartConfiguration;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.exception.NotFoundProjectException;

/**
 * 클라이언트 프로젝트 관리자
 * 
 * @author Won Jonghoon
 * 
 */
public final class ConnectionPoolManager {
	private InternalLogger log = InternalLoggerFactory.getInstance(ConnectionPoolManager.class);

	/** 모니터 객체 */
	// private final Object monitor = new Object();
	private List<String> subProjectNamelist = null;
	private HashMap<String, AnyProjectConnectionPoolIF> subProjectConnectionPoolHash = new HashMap<String, AnyProjectConnectionPoolIF>();

	private AnyProjectConnectionPoolIF mainProjectConnectionPool = null;

	private String mainProjectName = null;

	private AnyProjectConnectionPoolMonitor anyProjectConnectionPoolMonitor = null;

	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class ClientProjectManagerHolder {
		static final ConnectionPoolManager singleton = new ConnectionPoolManager();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static ConnectionPoolManager getInstance() {
		return ClientProjectManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 * 
	 * @throws NoMoreDataPacketBufferException
	 */
	private ConnectionPoolManager() {
		CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();
		ProjectPartConfiguration mainProjectPartConfiguration = runningProjectConfiguration
				.getMainProjectPartConfiguration();
		AllSubProjectPartConfiguration allSubProjectPart = runningProjectConfiguration
				.getAllSubProjectPartConfiguration();

		mainProjectName = mainProjectPartConfiguration.getProjectName();

		try {
			mainProjectConnectionPool = new AnyProjectConnectionPool(mainProjectPartConfiguration);
		} catch (Exception e) {
			String errorMessage = new StringBuilder("fail to initialize a main project connection pool[")
					.append(mainProjectName).append("]").toString();
			log.error(errorMessage, e);
			System.exit(1);
		}
		

		subProjectNamelist = allSubProjectPart.getSubProjectNamelist();

		for (String subProjectName : subProjectNamelist) {
			AnyProjectConnectionPool subClientProject = null;
			try {
				subClientProject = new AnyProjectConnectionPool(
						allSubProjectPart.getSubProjectPartConfiguration(subProjectName));
				subProjectConnectionPoolHash.put(subProjectName, subClientProject);
			} catch (Exception e) {
				String errorMessage = new StringBuilder("fail to initialize a sub project connection pool[")
						.append(subProjectName).append("] of main project[").append(mainProjectName).append("]")
						.toString();
				log.error(errorMessage, e);
				System.exit(1);
			}
		}
		
		anyProjectConnectionPoolMonitor = new AnyProjectConnectionPoolMonitor(
				mainProjectPartConfiguration.getClientMonitorTimeInterval());
		anyProjectConnectionPoolMonitor.start();
	}

	/**
	 * 프로젝트 이름에 해당하는 외부에서 바라보는 시각을 가지는 클라이언트 프로젝트를 얻는다.
	 * 
	 * @param subProjectName
	 *            프로젝트 이름
	 * @return 프로젝트 이름에 해당하는 외부 시각 클라이언트 프로젝트
	 * @throws NotFoundProjectException
	 */
	public AnyProjectConnectionPoolIF getSubProjectConnectionPool(String subProjectName) throws IllegalStateException {
		AnyProjectConnectionPoolIF subProjectConnectionPool = subProjectConnectionPoolHash.get(subProjectName);
		if (null == subProjectConnectionPool) {
			String errorMessage = new StringBuilder("fail to initialize a sub project connection pool[")
					.append(subProjectName).append("] of main project[").append(mainProjectName).append("]").toString();
			throw new IllegalStateException(errorMessage);
		}

		return subProjectConnectionPool;
	}

	public AnyProjectConnectionPoolIF getMainProjectConnectionPool() throws IllegalStateException {
		if (null == mainProjectConnectionPool) {
			try {
				CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager.getInstance()
						.getRunningProjectConfiguration();
				ProjectPartConfiguration mainProjectPart = runningProjectConfiguration
						.getMainProjectPartConfiguration();
				mainProjectConnectionPool = new AnyProjectConnectionPool(mainProjectPart);
			} catch (Exception e) {
				String errorMessage = new StringBuilder("fail to initialize a main project connection pool[")
						.append(mainProjectName).append("]").toString();
				log.warn(errorMessage, e);

				throw new IllegalStateException(errorMessage);
			}

		}

		return mainProjectConnectionPool;
	}

	private class AnyProjectConnectionPoolMonitor extends Thread {
		private long monitorTimeInterval;

		public AnyProjectConnectionPoolMonitor(long monitorTimeInterval) {
			this.monitorTimeInterval = monitorTimeInterval;
		}

		@Override
		public void run() {
			log.info("AnyProjectConnectionPoolMonitor start");
			try {
				while (!Thread.currentThread().isInterrupted()) {
					log.info(getPoolState());
					Thread.sleep(monitorTimeInterval);
				}
			} catch (InterruptedException e) {
				log.info("AnyProjectConnectionPoolMonitor::interrupr");
			} catch (Exception e) {
				log.info("AnyProjectConnectionPoolMonitor::unknow error", e);
			}
			log.info("AnyProjectConnectionPoolMonitor end");
		}

	}

	private String getPoolState() {
		StringBuilder pollStateStringBuilder = new StringBuilder();
		pollStateStringBuilder.append("main projectName[");
		pollStateStringBuilder.append(mainProjectName);
		pollStateStringBuilder.append("]'s AnyProjectConnectionPool state");
		pollStateStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		pollStateStringBuilder.append(mainProjectConnectionPool.getPoolState());

		for (String subProjectName : subProjectNamelist) {
			AnyProjectConnectionPoolIF subProjectConnectionPool = subProjectConnectionPoolHash.get(subProjectName);

			pollStateStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			pollStateStringBuilder.append("sub projectName[");
			pollStateStringBuilder.append(subProjectName);
			pollStateStringBuilder.append("]'s AnyProjectConnectionPool state");
			pollStateStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			pollStateStringBuilder.append(subProjectConnectionPool.getPoolState());
		}
		return pollStateStringBuilder.toString();
	}
}
