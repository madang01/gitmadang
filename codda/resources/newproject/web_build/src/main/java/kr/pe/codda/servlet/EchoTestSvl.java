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
		String goPage = "/EchoTest01.jsp";

		java.util.Random random = new java.util.Random();
			
		Echo echoReq = new Echo();
		
		echoReq.setRandomInt(random.nextInt());
		echoReq.setStartTime(new java.util.Date().getTime());

		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage messageFromServer = mainProjectConnectionPool.sendSyncInputMessage(echoReq);
		
		boolean isSame = false;
		String errorMessage = "";
		long erraseTime=0;
		
		if (messageFromServer instanceof Echo) {
			Echo echoOutObj = (Echo)messageFromServer;
			
			erraseTime = new java.util.Date().getTime() - echoReq.getStartTime();
			
			if ((echoOutObj.getRandomInt() == echoReq
					.getRandomInt())
					&& (echoOutObj.getStartTime() == echoReq.getStartTime())) {
				isSame = true;
				//log.info("성공::echo 메시지 입력/출력 동일함");
			} else {
				isSame = false;
				// log.info("실패::echo 메시지 입력/출력 다름");
			}
			
			req.setAttribute("echoOutObj", echoOutObj);
		} else {
			errorMessage = messageFromServer.toString();
			log.warn(errorMessage);
		}
				
		req.setAttribute("isSame", isSame);
		req.setAttribute("erraseTime", ""+erraseTime);
		req.setAttribute("errorMessage", errorMessage);
		req.setAttribute("echoInObj", echoReq);
		
		printJspPage(req, res, goPage);	
		
	}
	
}