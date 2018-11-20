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


import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.weblib.jdf.AbstractServlet;


/**
 * 비 로그인 JDF 상속 서블릿
 * @author Won Jonghoon
 *
 */
public class JDFNotLoginSvl extends AbstractServlet {
	
	private static final long serialVersionUID = 7042189810188588931L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
				
		// String title = "Not Login Test:MVC2 model ok";
		
		Enumeration<String> headerNames = req.getHeaderNames();
		
		Hashtable<String, String> headerInformationHash = new  Hashtable<String, String>();
		
		while (headerNames.hasMoreElements()) {
		    String key = (String)headerNames.nextElement();
		    String value = req.getHeader(key);
		    headerInformationHash.put(key, value);
        }
	
		req.setAttribute("headerInformationHash", headerInformationHash);		
		printJspPage(req, res, "/jsp/util/JDFNotLogin.jsp");
	}
}
