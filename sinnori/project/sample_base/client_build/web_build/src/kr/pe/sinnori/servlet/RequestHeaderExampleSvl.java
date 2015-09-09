package kr.pe.sinnori.servlet;
/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/* $Id: RequestHeaderExample.java 500674 2007-01-27 23:15:00Z markt $
 *
 */

import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.common.weblib.AbstractServlet;
import kr.pe.sinnori.impl.javabeans.ReqHeaderInfoBean;

/**
 * Example servlet showing request headers
 *
 * @author James Duncan Davidson <duncan@eng.sun.com>
 * 복사자&수정자 : Won Jonghoon
 */

@SuppressWarnings("serial")
public class RequestHeaderExampleSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		log.info("call RequestHeaderExample");
		
		
		
		String title = "한글 테스트";
		Enumeration<?> e = req.getHeaderNames();
		
		ArrayList<String> headerList = new ArrayList<String>(); 
		while (e.hasMoreElements()) {
		    String headerName = (String)e.nextElement();
		    String headerValue = req.getHeader(headerName);
		    headerList.add(headerName);
		    headerList.add(headerValue);
        }
		
		ReqHeaderInfoBean reqHeaderInfo = new ReqHeaderInfoBean();
		reqHeaderInfo.title = title;
		reqHeaderInfo.headerInfoSize = headerList.size()/2;
		
		reqHeaderInfo.headerInfoList = new ReqHeaderInfoBean.HeaderInfoList[reqHeaderInfo.headerInfoSize];
		
		for (int i=0; i < reqHeaderInfo.headerInfoSize; i++) {
			reqHeaderInfo.headerInfoList[i] = reqHeaderInfo.new HeaderInfoList();
			reqHeaderInfo.headerInfoList[i].headerKey = headerList.get(i);
			reqHeaderInfo.headerInfoList[i].headerValue = headerList.get(i+1);
		}
		
		req.setAttribute("reqHeaderInfo", reqHeaderInfo);		
		printWebLayoutControlJspPage(req, res, "/menu/testcode/reqheaderinfo.jsp");
	}

}


