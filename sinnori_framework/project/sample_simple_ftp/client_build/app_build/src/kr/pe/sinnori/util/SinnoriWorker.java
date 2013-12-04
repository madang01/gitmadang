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

package kr.pe.sinnori.util;

import kr.pe.sinnori.client.ClientProject;
import kr.pe.sinnori.client.ClientProjectManager;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.server.ServerProject;
import kr.pe.sinnori.server.ServerProjectManager;

/**
 * 신놀이 기동을 위한 편의 기능 제공 클래스, 간단히 "신놀이 워커" 라 불린다.
 * @author Jonghoon Won
 *
 */
public class SinnoriWorker implements CommonRootIF {
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스
	 */
	private static final class SinnoriWorkerHolder {
		static final SinnoriWorker singleton = new SinnoriWorker();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드
	 */
	public static SinnoriWorker getInstance() {
		return SinnoriWorkerHolder.singleton;
	}
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 */
	private SinnoriWorker() {
		
	}
	
	/**
	 * <pre>
	 * 클라이언트 비지니스 로직 클래스 이름을 반환한다.
	 * 참고) 이곳에서 쓰이는 클라이언트 비지니스 로직은 단순히 "신놀이 기동을 위한 편의 기능 제공 클래스" 종속 부품일 뿐이다. 
	 * 서버 비지니스 로직은 콤퍼넌트화가 가능하지만 클라이언트 비지니스 로직은 컴포넌트화가 불가능하다.
	 * 따라서 신놀이 프레임 워크는 서버용 비지니스 로직만 관리를 하며    
	 * 클라이언트에게는 단순히 클라이언트용 서버 접속 API 만 제공할뿐 클라이언트 비지니스 로직은 관리 하지 않는다.
	 * </pre>
	 * @param clinetExecutorName 클라이언트 비지니스 로직 이름
	 * @return 클라이언트 비지니스 로직 클래스 이름
	 */
	private String getClientExecetorClassName(String clinetExecutorName) {
		String prefix = (String)conf.getResource("sinnori_worker.client.executor.prefix.value");
		String suffix = (String)conf.getResource("sinnori_worker.client.executor.suffix.value");
		
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(prefix);
		strBuilder.append(clinetExecutorName);
		strBuilder.append(suffix);
		return strBuilder.toString();
	}
	
	/**
	 * 운영 모드에 맞추어서 프로젝트 서버 혹은 클라이언트를 기동시킨다. 클라이언트를 기동시 클라이언트 비지니스 로직을 수행하다.
	 * @param projectName 프로젝트 이름
	 * @param clinetExecutorName 클라이언트 비지니스 로직 이름
	 * @throws InterruptedException 쓰레드 인터럽트
	 */
	public void start(String projectName, String clinetExecutorName) throws InterruptedException {
		String prop_sinnori_running_mode = (String)conf.getResource("sinnori_worker.running_mode.value");
		
		if (prop_sinnori_running_mode.equals("client")) {
			String clientExecetorClassName = getClientExecetorClassName(clinetExecutorName);
			ClientProject clientProject = ClientProjectManager.getInstance().getClientProject(projectName);
			
			try {
				Class<?> c = Class.forName(clientExecetorClassName);				
				AbstractClientExecutor clientExecutorObj = (AbstractClientExecutor)c.newInstance();
				clientExecutorObj.execute(clientProject, clientProject);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			((ClientProject)clientProject).stopAsynPool();
		} else if (prop_sinnori_running_mode.equals("server")) {
			ServerProject serverProject = ServerProjectManager.getInstance().getServerProject(projectName);
			
			serverProject.startServer();
		} else {
			String clientExecetorClassName = getClientExecetorClassName(clinetExecutorName);
			ServerProject serverProject = ServerProjectManager.getInstance().getServerProject(projectName);			
			serverProject.startServer();
			
			ClientProject clientProject = ClientProjectManager.getInstance().getClientProject(projectName);
			try {
				Class<?> c = Class.forName(clientExecetorClassName);				
				AbstractClientExecutor clientExecutorObj = (AbstractClientExecutor)c.newInstance();
				clientExecutorObj.execute(clientProject, clientProject);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			((ClientProject)clientProject).stopAsynPool();
		}
	}
	
	/**
	 * 운영 모드에 맞추어서 프로젝트 서버 혹은 클라이언트를 기동시킨다. 단 클라이언트를 기동시 지정된 횟수 만큼 클라이언트 비지니스 로직을 반복 수행하다.
	 * @param projectName 프로젝트 이름
	 * @param clinetExecutorName 클라이언트 비지니스 로직 이름
	 * @param count 클라이언트 비지니스 로직 반복 횟수
	 * @throws InterruptedException 쓰레드 인터럽트
	 */
	public void start(String projectName, String clinetExecutorName, int count) throws InterruptedException {
		String prop_sinnori_running_mode = (String)conf.getResource("sinnori_worker.running_mode.value");
		
		if (prop_sinnori_running_mode.equals("client")) {
			String clientExecetorClassName = getClientExecetorClassName(clinetExecutorName);
			ClientProject clientProject = ClientProjectManager.getInstance().getClientProject(projectName);
			
			try {
				Class<?> c = Class.forName(clientExecetorClassName);				
				AbstractClientExecutor clientExecutorObj = (AbstractClientExecutor)c.newInstance();
				clientExecutorObj.execute(clientProject, clientProject, count);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			// ((ClientProject)clientProject).stopAsynPool();
		} else if (prop_sinnori_running_mode.equals("server")) {
			ServerProject serverProject = ServerProjectManager.getInstance().getServerProject(projectName);
			
			serverProject.startServer();
		} else {
			String clientExecetorClassName = getClientExecetorClassName(clinetExecutorName);
			ServerProject serverProject = ServerProjectManager.getInstance().getServerProject(projectName);			
			serverProject.startServer();
			ClientProject clientProject = ClientProjectManager.getInstance().getClientProject(projectName);
			
			try {
				Class<?> c = Class.forName(clientExecetorClassName);				
				AbstractClientExecutor clientExecutorObj = (AbstractClientExecutor)c.newInstance();
				clientExecutorObj.execute(clientProject, clientProject, count);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			// ((ClientProject)clientProject).stopAsynPool();
		}
	}
}
