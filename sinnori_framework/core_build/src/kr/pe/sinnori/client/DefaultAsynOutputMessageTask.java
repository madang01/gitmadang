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

import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.message.AbstractMessage;

/**
 * {@link ClientProject} 에서 지정하는 디폴트 익명 메시지 처리자로 단순 로그만 찍는다. 
 * @author Jonghoon Won
 *
 */
public class DefaultAsynOutputMessageTask implements CommonRootIF, AsynOutputMessageTaskIF {
	
	@Override
	public void doTask(ClientProjectConfig clientProjectConfig, AbstractMessage outObj) {
		log.info(String.format("projectName[%s] %s", clientProjectConfig.getProjectName(), outObj.toString()));
	}
}
