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
package kr.pe.codda.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.weblib.common.BoardType;
import kr.pe.codda.weblib.jdf.AbstractLoginServlet;

/**
 * 게시판 댓글 등록 처리
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class BoardReplyInputSvl extends AbstractLoginServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		/**************** 파라미터 시작 *******************/		
		String paramBoardID = req.getParameter("boardID");
		String paramParentBoardNo = req.getParameter("parentBoardNo");
		/**************** 파라미터 종료 *******************/
		
		if (null == paramBoardID) {
			String errorMessage = "게시판 식별자 값을 넣어 주세요";
			String debugMessage = "the web parameter 'boardID' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);	
			return;
		}
		
		short boardID = 0;
		try {
			boardID = Short.parseShort(paramBoardID);
		}catch (NumberFormatException nfe) {
			String errorMessage = "잘못된 게시판 식별자 입니다";
			String debugMessage = new StringBuilder("the web parameter 'boardID'[")
					.append(paramBoardID).append("] is not a short").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		try {
			BoardType.valueOf(boardID);
		} catch(IllegalArgumentException e) {
			String errorMessage = "알 수 없는 게시판 식별자 입니다";
			String debugMessage = new StringBuilder("the web parameter 'boardID'[")
					.append(paramBoardID).append("] is not a element of set[")
					.append(BoardType.getSetString())
					.append("]").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
					
		
		
		if (null == paramParentBoardNo) {
			String errorMessage = "부모 게시판 번호 값을 넣어 주세요";
			String debugMessage = "the web parameter 'parentBoardNo' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		long parentBoardNo = 0L;
		
		try {
			parentBoardNo = Long.parseLong(paramParentBoardNo);
		}catch (NumberFormatException nfe) {
			String errorMessage = "잘못된 부모 게시판 번호입니다";
			String debugMessage = new StringBuilder("the web parameter \"parentBoardNo\"'s value[")
					.append(paramParentBoardNo)
					.append("] is not a Long").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}			
		
		if (parentBoardNo <= 0) {
			String errorMessage = "부모 게시판 번호는 0 보다 커야 합니다";
			String debugMessage = new StringBuilder("the web parameter \"parentBoardNo\"'s value[")
					.append(parentBoardNo)
					.append("] is less than or equal to zero").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		req.setAttribute("paramBoardID", paramBoardID);
		req.setAttribute("paramParentBoardNo", paramParentBoardNo);
		printJspPage(req, res, "/jsp/community/BoardReplyInput.jsp");
	}
}
