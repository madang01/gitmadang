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


import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars;
import kr.pe.sinnori.weblib.jdf.AbstractServlet;


/**
 * 로그인 하지 않는 경우 테스트
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class JDFNotLoginTestSvl extends AbstractServlet {
	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setAttribute(WebCommonStaticFinalVars.SITE_TOPMENU_REQUEST_KEY_NAME, 
				kr.pe.sinnori.weblib.sitemenu.SiteTopMenuType.TEST_EXAMPLE);
		
		// String title = "Not Login Test:MVC2 model ok";
		
		Enumeration<String> headerNames = req.getHeaderNames();
		
		Hashtable<String, String> headerInformationHash = new  Hashtable<String, String>();
		
		while (headerNames.hasMoreElements()) {
		    String key = (String)headerNames.nextElement();
		    String value = req.getHeader(key);
		    headerInformationHash.put(key, value);
        }
	
		req.setAttribute("headerInformationHash", headerInformationHash);		
		printJspPage(req, res, "/menu/testcode/JDFNotLoginTest01.jsp");
	}
}
