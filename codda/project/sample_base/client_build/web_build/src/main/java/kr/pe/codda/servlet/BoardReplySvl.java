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

import java.io.IOException;
import java.nio.file.AccessDeniedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.BoardType;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractLoginServlet;
import kr.pe.codda.weblib.sitemenu.SiteTopMenuType;

/**
 * 게시판 댓글 등록 처리
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class BoardReplySvl extends AbstractLoginServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_SITE_TOPMENU, 
				SiteTopMenuType.COMMUNITY);
		
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
					
		String parmParentBoardNo = req.getParameter("parentBoardNo");
		
		if (null == parmParentBoardNo) {
			String errorMessage = "부모 게시판 번호 값을 넣어 주세요";
			String debugMessage = "the web parameter 'parentBoardNo' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		long parentBoardNo = 0L;
		
		try {
			parentBoardNo = Long.parseLong(parmParentBoardNo);
		}catch (NumberFormatException nfe) {
			String errorMessage = "잘못된 부모 게시판 번호입니다";
			String debugMessage = new StringBuilder("the web parameter \"parentBoardNo\"'s value[")
					.append(parmParentBoardNo)
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
		
		doFirstPage(req, res, parmBoardId, parmParentBoardNo);
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
					
		String parmParentBoardNo = req.getParameter("parentBoardNo");
		
		if (null == parmParentBoardNo) {
			String errorMessage = "부모 게시판 번호 값을 넣어 주세요";
			String debugMessage = "the web parameter 'parentBoardNo' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		long parentBoardNo = 0L;
		
		try {
			parentBoardNo = Long.parseLong(parmParentBoardNo);
		}catch (NumberFormatException nfe) {
			String errorMessage = "잘못된 부모 게시판 번호입니다";
			String debugMessage = new StringBuilder("the web parameter \"parentBoardNo\"'s value[")
					.append(parmParentBoardNo)
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
		
		String parmSubject = req.getParameter("subject");
		if (null == parmSubject) {
			String errorMessage = "제목을 넣어 주세요";
			String debugMessage = "the web parameter 'subject' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		String parmContent = req.getParameter("content");		
		if (null == parmContent) {
			String errorMessage = "내용을 넣어 주세요";
			String debugMessage = "the web parameter 'content' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		String parmAttachId = req.getParameter("attachId");
		if (null == parmAttachId) {
			String errorMessage = "업로드 파일 식별자를 넣어 주세요";
			String debugMessage = "the web parameter 'attachId' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		long attachId = 0L;
		try {
			attachId = Long.parseLong(parmAttachId);
		}catch (NumberFormatException nfe) {
			String errorMessage = "잘못된 업로드 파일 식별자입니다";
			String debugMessage = new StringBuilder("the web parameter \"attachId\"'s value[")
					.append(parmParentBoardNo)
					.append("] is not a Long").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (attachId < 0) {
			String errorMessage = "잘못된 업로드 파일 식별자입니다";
			String debugMessage = new StringBuilder("the web parameter \"attachId\"'s value[")
					.append(parmParentBoardNo)
					.append("] is less than zero").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (attachId > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			String errorMessage = "잘못된 업로드 파일 식별자입니다";
			String debugMessage = new StringBuilder("the web parameter \"attachId\"'s value[")
					.append(parmParentBoardNo)
					.append("] is greater than the unsinged max integer[")
					.append(CommonStaticFinalVars.UNSIGNED_INTEGER_MAX)
					.append("]").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		BoardReplyReq boardReplyReq = new BoardReplyReq();
		boardReplyReq.setBoardId(boardId);
		boardReplyReq.setParentBoardNo(parentBoardNo);
		boardReplyReq.setSubject(parmSubject);
		boardReplyReq.setContent(parmContent);
		boardReplyReq.setAttachId(attachId);
		boardReplyReq.setUserId(getLoginUserIDFromHttpSession(req));
		boardReplyReq.setIp(req.getRemoteAddr());
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(boardReplyReq);
		if (outputMessage instanceof MessageResultRes) {
			MessageResultRes messageResultRes = (MessageResultRes)outputMessage;
			
			doProcessPage(req, res, parmBoardId, messageResultRes);
		} else {				
			String errorMessage = "게시판 댓글 등록이 실패했습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(boardReplyReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
	}

	private void doFirstPage(HttpServletRequest req, HttpServletResponse res, String parmBoardId,
			String parmParentBoardNo) {
		req.setAttribute("parmBoardId", parmBoardId);
		req.setAttribute("parmParentBoardNo", parmParentBoardNo);
		printJspPage(req, res, "/menu/board/BoardReply01.jsp");
	}

	private void doProcessPage(HttpServletRequest req, HttpServletResponse res, String parmBoardId,
			MessageResultRes messageResultRes) {
		req.setAttribute("parmBoardId", parmBoardId);
		req.setAttribute("messageResultRes", messageResultRes);
		printJspPage(req, res, "/menu/board/BoardReply02.jsp");
	}
}
