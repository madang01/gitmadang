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
package kr.pe.sinnori.impl.message.Echo;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractServerTask;
import kr.pe.sinnori.server.executor.LetterSender;

/**
 * Echo 서버 타스크
 * @author "Jonghoon Won"
 *
 */
public final class EchoServerTask extends AbstractServerTask {
	/**
	 * 생성자
	 *//*
	public EchoServerTask() {
		*//** 
		 * <pre>
		 * <처리 흐름도>
		 * 입력 메시지 -> 입력 스트림 => [요청(입력 스트림 -> 입력 메시지) -> 처리 -> 응답(출력 메시지 -> 출력 스트림) ] -> 출력 스트림 -> 출력 메시지 
		 *  
		 * 다른 클래스 로더에서 로딩된 클래스는 자바 리플렉션를 이용하여 접근할 수 밖에 없다.
		 * 따라서 처리 흐름도를 보았듯이 서버에서 입력 메시지를 추출하고 처리하며 출력까지의 일련의 과정에 필요한 로직을 담은 클래스들을
		 * 동일 클래스 로더에 로딩하여 서로간에 자바 리플렉션이 아닌 방법으로 접근하게 하는것이 기본 아이디어 이다.
		 * 
		 * 따라서 서버 코덱 클래스를 이곳에 지정하여 입력 메시지 인코더
		 * </pre>  
		 *//*
		AbstractCodec serverCodex = null;
		
		serverCodex = new EchoServerCodec();
		serverCodecHash.put(serverCodex.getMessageID(),  serverCodex);
	}*/
	
	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager,
			LetterSender letterSender, AbstractMessage messageFromClient)
			throws Exception {
		doWork(serverProjectConfig, letterSender, (Echo)messageFromClient);
	}
	
	private void doWork(ServerProjectConfig serverProjectConfig,
			LetterSender letterSender, Echo echoInObj)
			throws Exception {
		//log.info("echoInObj={}", echoInObj.toString());		
		Echo echoOutObj = new Echo();

		echoOutObj.setRandomInt(echoInObj.getRandomInt());
		echoOutObj.setStartTime(echoInObj.getStartTime());
		
		letterSender.addSyncMessage(echoOutObj);
	}	
}
