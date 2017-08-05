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
package kr.pe.sinnori.impl.servertask;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.Echo.Echo;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

/**
 * Echo 서버 타스크
 * @author "Won Jonghoon"
 *
 */
public final class EchoServerTask extends AbstractServerTask {	
	
	@Override
	public void doTask(String projectName,
			LoginManagerIF loginManager,
			LetterSender letterSender, AbstractMessage messageFromClient)
			throws Exception {
		doWork(projectName, letterSender, (Echo)messageFromClient);
	}
	
	private void doWork(String projectName,
			LetterSender letterSender, Echo echoReq)
			throws Exception {		
		Echo echoRes = new Echo();

		echoRes.setRandomInt(echoReq.getRandomInt());
		echoRes.setStartTime(echoReq.getStartTime());
		
		letterSender.addSyncMessage(echoRes);
	}	
}
