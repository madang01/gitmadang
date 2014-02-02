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
import kr.pe.sinnori.client.io.LetterFromServer;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.servlet.AbstractServlet;

/**
 * Echo 메시지 교환 테스트
 * @author Jonghoon Won
 *
 */

@SuppressWarnings("serial")
public class EchoTestSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String goPage = "/testcode/EchoTest01.jsp";

		java.util.Random random = new java.util.Random();
		boolean isSame = false;
		long erraseTime = 0;
		
		String projectName = System.getenv("SINNORI_PROJECT_NAME");
		// FIXME!
		log.info(String.format("projectName=[%s]", projectName));
		
		ClientProject clientProject = ClientProjectManager.getInstance().getClientProject(projectName);
		
		InputMessage echoInObj = null;
		
		echoInObj = clientProject.createInputMessage("Echo");
		
		
		echoInObj.setAttribute("randomInt", random.nextInt());
		echoInObj.setAttribute("startTime", new java.util.Date().getTime());

		String errorMessage = "";
		
		LetterFromServer letterFromServer = clientProject
				.sendSyncInputMessage(echoInObj);

		if (null == letterFromServer) {
			errorMessage = String.format("input message[%s] is null, maybe socket close", echoInObj.getMessageID()); 
			log.warn(errorMessage);
		} else {
			OutputMessage echoOutObj = letterFromServer.getOutputMessage("Echo");
			
			erraseTime = new java.util.Date().getTime() - (long) echoOutObj.getAttribute("startTime");
			
			if (((int) echoOutObj.getAttribute("randomInt") == (int) echoInObj
					.getAttribute("randomInt"))
					&& ((long) echoOutObj.getAttribute("startTime") == (long) echoInObj
							.getAttribute("startTime"))) {
				isSame = true;
				//log.info("성공::echo 메시지 입력/출력 동일함");
			} else {
				isSame = false;
				// log.info("실패::echo 메시지 입력/출력 다름");
			}
			
			req.setAttribute("echoOutObj", echoOutObj);
			req.setAttribute("isSame", isSame);
			req.setAttribute("erraseTime", ""+erraseTime);
		}
		
		
		req.setAttribute("errorMessage", errorMessage);
		req.setAttribute("echoInObj", echoInObj);
		
		printJspPage(req, res, goPage);	
		
	}
	
}
