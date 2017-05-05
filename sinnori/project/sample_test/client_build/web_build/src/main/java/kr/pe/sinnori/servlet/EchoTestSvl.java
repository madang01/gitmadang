package kr.pe.sinnori.servlet;
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

import kr.pe.sinnori.client.ClientProject;
import kr.pe.sinnori.client.ClientProjectManager;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.Echo.Echo;
import kr.pe.sinnori.weblib.jdf.AbstractServlet;

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
			
		Echo echoInObj = new Echo();
		
		echoInObj.setRandomInt(random.nextInt());
		echoInObj.setStartTime(new java.util.Date().getTime());

		ClientProject clientProject = ClientProjectManager.getInstance().getMainClientProject();
		AbstractMessage messageFromServer = clientProject.sendSyncInputMessage(echoInObj);
		
		boolean isSame = false;
		String errorMessage = "";
		long erraseTime=0;
		
		if (messageFromServer instanceof Echo) {
			Echo echoOutObj = (Echo)messageFromServer;
			
			erraseTime = new java.util.Date().getTime() - echoInObj.getStartTime();
			
			if ((echoOutObj.getRandomInt() == echoInObj
					.getRandomInt())
					&& (echoOutObj.getStartTime() == echoInObj.getStartTime())) {
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
		req.setAttribute("echoInObj", echoInObj);
		
		printJspPage(req, res, goPage);	
		
	}
	
}
