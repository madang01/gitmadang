
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

package kr.pe.sinnori.gui.screen.asynfileupdownscreen.task;

import kr.pe.sinnori.client.AsynOutputMessageTaskIF;
import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.gui.lib.AsynMainControllerIF;

/**
 * 비동기 파일 송수신의 비동기 메시지 처리자
 * @author Won Jonghoon
 *
 */
public class AsynFileUpDownOutputMessageTask implements CommonRootIF, AsynOutputMessageTaskIF {
	private AsynMainControllerIF mainController = null;
	
	/**
	 * 생성자
	 * @param mainController 서버로부터 받은 비동기 메시지 처리를 맡을 메인 제어자 
	 */
	public AsynFileUpDownOutputMessageTask(AsynMainControllerIF mainController) {
		this.mainController = mainController;
	}
	
	@Override
	public void doTask(ClientProjectConfig clientProjectConfig, AbstractMessage outObj) {
		// log.info(String.format("projectName[%s] %s", clientProjectConfig.getProjectName(), outObj.toString()));
		// FIXME!
		// log.info("projectName={}, {}", clientProjectConfig.getProjectName(), outObj.toString());
		mainController.doAsynOutputMessageTask(outObj);
	}
}