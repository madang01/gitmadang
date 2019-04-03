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
package kr.pe.codda.servlet.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.jdf.AbstractServlet;

/**
 * 관리자 로그인 입력 화면 서블릿
 * 
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class AdminLoginInputSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {		
		/**************** 파라미터 시작 *******************/
		String paramUserID = req.getParameter("userID");
		if (null == paramUserID) {
			paramUserID = "";
		} else {
			try {
				ValueChecker.checkValidLoginUserID(paramUserID);
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String debugMessage = null;

				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
		}
		/**************** 파라미터 종료 *******************/
		
		req.setAttribute("requestURI", "/");
		req.setAttribute("userID", paramUserID);
		
		/** /jsp/member/AdminLoginInput.jsp */
		printJspPage(req, res, JDF_ADMIN_LOGIN_INPUT_PAGE);
	}
}
