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

import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractServlet;

/**
 * 어드민 사이트용 회원 가입 화면
 * @author Won Jonghoon
 *
 */

@SuppressWarnings("serial")
public class AdminSiteMembershipInputSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {		
		
		ServerSessionkeyIF webServerSessionkey = null;
		try {
			ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
			webServerSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();
		} catch (SymmetricException e) {
			String errorMessage = "fail to get a ServerSessionkeyManger class instance";
			log.warn(errorMessage, e);			
			
			String debugMessage = e.getMessage();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING,
				webServerSessionkey.getModulusHexStrForWeb());
		
		printJspPage(req, res, "/jsp/member/AdminSiteMembershipInput.jsp");
		
		/*SiteType siteType = SiteType.USER;
		String nativeSiteType = getServletConfig().getInitParameter("siteType");
		
		if (null == nativeSiteType) {
			String errorMessage = "web.xml 에서 init-param 'siteType' 를  지정하지 않았습니다. 참고) siteType 값은 {admin, user} 로 구성되어 있습니다";
			
			printErrorMessagePage(req, res, errorMessage, null);
			return;
		}
		
		nativeSiteType = nativeSiteType.toUpperCase();
			
		try {
			siteType = SiteType.valueOf(nativeSiteType);
		} catch(IllegalArgumentException e) {
			String errorMessage = new StringBuilder().append("web.xml 에서 init-param 'siteType' 값[")
					.append(nativeSiteType)
					.append("]이 잘못되었습니다.  참고) siteType 값은 {admin, user} 로 구성되어 있습니다").toString();			
			printErrorMessagePage(req, res, errorMessage, null);
			return;	
		}		
		
		if (siteType.equals(SiteType.ADMIN)) {
			printJspPage(req, res, "/jsp/member/MemberRegistrationInputForAdminSite.jsp");
		} else {
			printJspPage(req, res, "/jsp/member/MemberRegistrationInputForUserSite.jsp");
		}*/
	}
}
