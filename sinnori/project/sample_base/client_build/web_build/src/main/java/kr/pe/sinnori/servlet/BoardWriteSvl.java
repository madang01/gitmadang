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
package kr.pe.sinnori.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.pe.sinnori.client.AnyProjectConnectionPoolIF;
import kr.pe.sinnori.client.ConnectionPoolManager;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.AccessDeniedException;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.ConnectionPoolException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.sinnori.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.sinnori.weblib.common.BoardType;
import kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars;
import kr.pe.sinnori.weblib.jdf.AbstractLoginServlet;

/**
 * 게시판 최상의 글 등록 처리
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class BoardWriteSvl extends AbstractLoginServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_SITE_TOPMENU, 
				kr.pe.sinnori.weblib.sitemenu.SiteTopMenuType.COMMUNITY);
		
		String parmRequestType = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE);
		if (null == parmRequestType) {
			firstPage(req, res);			
			return;
		}
		
		if (parmRequestType.equals("view")) {
			firstPage(req, res);
			return;
		} else if (parmRequestType.equals("proc")) {		
			processPage(req, res);
			return;
		} else {
			String errorMessage = "파라미터 '요청종류'의 값이 잘못되었습니다";
			String debugMessage = new StringBuilder("the web parameter \"")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE)
					.append("\"")
					.append("'s value[")
					.append(parmRequestType)			
					.append("] is not a elment of request type set[view, proc]").toString();
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
	}
	
	private void firstPage(HttpServletRequest req, HttpServletResponse res) {
		String parmBoardId = req.getParameter("boardId");
		if (null == parmBoardId) {
			String errorMessage = "게시판 식별자 값을 넣어 주세요";
			String debugMessage = "the web parameter 'boardId' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		short boardId = 0;
		try {
			boardId = Short.parseShort(parmBoardId);
		}catch (NumberFormatException nfe) {
			String errorMessage = "잘못된 게시판 식별자 입니다";
			String debugMessage = new StringBuilder("the web parameter 'boardId'[")
					.append(parmBoardId).append("] is not a short").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		try {
			BoardType.valueOf(boardId);
		} catch(IllegalArgumentException e) {
			String errorMessage = "알 수 없는 게시판 식별자 입니다";
			String debugMessage = new StringBuilder("the web parameter 'boardId'[")
					.append(parmBoardId).append("] is not a element of set[")
					.append(BoardType.getSetString())
					.append("]").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		doFirstPage(req, res, parmBoardId);
	}
	
	private void processPage(HttpServletRequest req, HttpServletResponse res) throws IOException, NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException, ServerTaskException, AccessDeniedException, InterruptedException, ConnectionPoolException {
		String parmBoardId = req.getParameter("boardId");
		if (null == parmBoardId) {
			String errorMessage = "게시판 식별자 값을 넣어 주세요";
			String debugMessage = "the web parameter 'boardId' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);	
			return;
		}
		
		short boardId = 0;
		try {
			boardId = Short.parseShort(parmBoardId);
		}catch (NumberFormatException nfe) {
			String errorMessage = "잘못된 게시판 식별자 입니다";
			String debugMessage = new StringBuilder("the web parameter 'boardId'[")
					.append(parmBoardId).append("] is not a short").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		try {
			BoardType.valueOf(boardId);
		} catch(IllegalArgumentException e) {
			String errorMessage = "알 수 없는 게시판 식별자 입니다";
			String debugMessage = new StringBuilder("the web parameter 'boardId'[")
					.append(parmBoardId).append("] is not a element of set[")
					.append(BoardType.getSetString())
					.append("]").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
	
		String parmSubject = req.getParameter("subject");
		if (null == parmSubject) {
			String errorMessage = "제목 값을 넣어주세요";
			String debugMessage = "the web parameter 'subject' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		String parmContent = req.getParameter("content");		
		if (null == parmContent) {
			String errorMessage = "글 내용 값을 넣어주세요";
			String debugMessage = "the web parameter 'content' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		String parmAttachId = req.getParameter("attachId");
		if (null == parmAttachId) {
			String errorMessage = "업로드 식별자를 넣어주세요";
			String debugMessage = "the web parameter 'attachId' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		long attachId = 0L;
		try {
			attachId = Long.parseLong(parmAttachId);
		}catch (NumberFormatException nfe) {
			String errorMessage = "업로드 파일 식별자 값이 잘못되었습니다";
			String debugMessage = new StringBuilder("the web parameter 'attachId'[")
					.append(parmBoardId).append("] is not a Long").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (attachId < 0) {
			String errorMessage = "업로드 파일 식별자 값은 0 보다 작거나 커야합니다";
			String debugMessage = new StringBuilder("the web parameter 'attachId'[")
					.append(parmBoardId).append("] is less than zero").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (CommonStaticFinalVars.UNSIGNED_INTEGER_MAX < attachId) {
			String errorMessage = new StringBuilder("업로드 파일 식별자 값[")
			.append(parmAttachId).append("]은 ")
			.append(CommonStaticFinalVars.UNSIGNED_INTEGER_MAX)
			.append(" 값 보다 작거나 같아야합니다.").toString();
			printErrorMessagePage(req, res, errorMessage, "");
			return;
		}
			
		
		HttpSession httpSession = req.getSession();
		
		Object userIDValue = httpSession.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGIN_USERID);	
		if (null == userIDValue)   {
			String errorMessage = "게시판 쓰기는 로그인한 후에 가능합니다";
			printErrorMessagePage(req, res, errorMessage, "");
			return;
		}
		
		String userId = (String) userIDValue;	
		
		BoardWriteReq inObj = new BoardWriteReq();
		inObj.setBoardId(boardId);
		inObj.setSubject(parmSubject);
		inObj.setContent(parmContent);
		inObj.setAttachId(attachId);
		inObj.setUserId(userId);
		inObj.setIp(req.getRemoteAddr());
		
		// FIXME!
		log.info("inObj={}", inObj.toString());
		
		System.out.println(inObj.toString());
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(inObj);
		if (outputMessage instanceof MessageResultRes) {
			MessageResultRes messageResultRes = (MessageResultRes)outputMessage;					
			
			doProcessPage(req, res, parmBoardId, messageResultRes);
			return;
		} else {			
			String errorMessage = "게시판 쓰기가 실패했습니다";
			String debugMessage = String.format("입력 메시지[%s]에 대한 비 정상 출력 메시지[%s] 도착", inObj.getMessageID(), outputMessage.toString());
			
			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
	}
	

	private void doFirstPage(HttpServletRequest req, HttpServletResponse res,
			String parmBoardId) {
		final String goPage = "/menu/board/BoardWrite01.jsp";		
		req.setAttribute("parmBoardId", parmBoardId);
		printJspPage(req, res, goPage);
	}

	private void doProcessPage(HttpServletRequest req, HttpServletResponse res,			 
			String parmBoardId, 
			MessageResultRes messageResultRes) {
		final String goPage = "/menu/board/BoardWrite02.jsp";
		req.setAttribute("parmBoardId", parmBoardId);
		req.setAttribute("messageResultRes", messageResultRes);
		printJspPage(req, res, goPage);
	}
}
