package kr.pe.codda.servlet;
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


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.Echo.Echo;
import kr.pe.codda.weblib.jdf.AbstractServlet;

/**
 * Echo 메시지 교환 테스트
 * @author Won Jonghoon
 *
 */

@SuppressWarnings("serial")
public class EchoTestSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		java.util.Random random = new java.util.Random();
		Echo echoReq = new Echo();		
		echoReq.setRandomInt(random.nextInt());
		echoReq.setStartTime(new java.util.Date().getTime());
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(echoReq);

		boolean isSame = false;		
		long erraseTime=0;
		
		if (!(outputMessage instanceof Echo)) {
			String errorMessage = "모든 데이터 타입 응답 메시지를 얻는데 실패하였습니다";
			
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(echoReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		Echo echoRes = (Echo)outputMessage;
		
		erraseTime = new java.util.Date().getTime() - echoReq.getStartTime();
		
		if ((echoRes.getRandomInt() == echoReq
				.getRandomInt())
				&& (echoRes.getStartTime() == echoReq.getStartTime())) {
			isSame = true;
			//log.info("성공::echo 메시지 입력/출력 동일함");
		} else {
			isSame = false;
			// log.info("실패::echo 메시지 입력/출력 다름");
		}
		
		req.setAttribute("isSame", String.valueOf(isSame));
		req.setAttribute("erraseTime", String.valueOf(erraseTime));
		req.setAttribute("echoRes", echoRes);
		printJspPage(req, res, "/jsp/util/EchoTest01.jsp");
		return;
	}
}
