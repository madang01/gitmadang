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
import kr.pe.codda.impl.message.BoardDetailReq.BoardDetailReq;
import kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes;
import kr.pe.codda.impl.message.BoardModifyReq.BoardModifyReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.BoardType;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractLoginServlet;
import kr.pe.codda.weblib.sitemenu.SiteTopMenuType;

/**
 * 게시판 글 수정 처리
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class BoardModifySvl extends AbstractLoginServlet {	
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
	
	private void firstPage(HttpServletRequest req, HttpServletResponse res) throws IOException, NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException, ServerTaskException, AccessDeniedException, InterruptedException, ConnectionPoolException {		
		String parmBoardId = req.getParameter("boardId");
		if (null == parmBoardId) {
			String errorMessage = "게시판 식별자 값을 넣어 주세요.";
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
		
		
		
		String parmBoardNo = req.getParameter("boardNo");
		if (null == parmBoardNo) {
			String errorMessage = "게시판 번호를 넣어주세요";
			String debugMessage = "the web parameter 'boardNo' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);	
			return;
		}
		
		
		long boardNo = 0L;
		try {
			boardNo = Long.parseLong(parmBoardNo);
		}catch (NumberFormatException nfe) {
			String errorMessage = new StringBuilder("자바 long 타입 변수인 게시판 번호 값[")
			.append(parmBoardId).append("]이 잘못되었습니다.").toString();
			log.warn("{}, userId={}, ip={}", errorMessage, getLoginUserIDFromHttpSession(req), req.getRemoteAddr());
			
			printErrorMessagePage(req, res, errorMessage, "");
			return;
		}
		
		if (boardNo <= 0) {
			String errorMessage = new StringBuilder("게시판 번호 값[")
			.append(parmBoardId).append("]은 0 보다 커야합니다.").toString();
			log.warn("{}, userId={}, ip={}", errorMessage, getLoginUserIDFromHttpSession(req), req.getRemoteAddr());
			
			printErrorMessagePage(req, res, errorMessage, "");
			return;
		}
		
		BoardDetailReq inObj = new BoardDetailReq();
		inObj.setBoardId(boardId);
		inObj.setBoardNo(boardNo);
		
		// FIXME!
		log.debug("inObj={}, userId={}, ip={}", inObj.toString(), getLoginUserIDFromHttpSession(req), req.getRemoteAddr());
		
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage messageFromServer = mainProjectConnectionPool.sendSyncInputMessage(inObj);
		
		// FIXME!
		log.debug("inObj={}, messageFromServer={}, userId={}, ip={}", inObj.toString(), messageFromServer.toString(), getLoginUserIDFromHttpSession(req), req.getRemoteAddr());
		
		if (messageFromServer instanceof BoardDetailRes) {
			doFirstPage(req, res, parmBoardId, parmBoardNo, (BoardDetailRes)messageFromServer);
			return;
		} else if (messageFromServer instanceof MessageResultRes) {				
			MessageResultRes messageResultRes = (MessageResultRes)messageFromServer;
			
			if (messageResultRes.getIsSuccess()) {
				log.error("입력 메시지[{}]에 대한 에러 결과를 담은 출력 메시지[{}]가 아닙니다", inObj.getMessageID(), messageResultRes.toString());
				System.exit(1);
			}
			
			String errorMessage = "게시판 글 수정이 실패하였습니다";
			String debugMessage = messageResultRes.getResultMessage();
			printErrorMessagePage(req, res, errorMessage, debugMessage);	
			return;
		} else {
			log.error("입력 메시지[{}]에 대한 비 정상 출력 메시지[{}] 도착", inObj.getMessageID(), messageFromServer.toString());
			System.exit(1);
		}
		
	}

	
	
	private void processPage(HttpServletRequest req, HttpServletResponse res) throws IOException, NoMoreDataPacketBufferException, BodyFormatException, DynamicClassCallException, ServerTaskException, AccessDeniedException, InterruptedException, ConnectionPoolException {
		String parmBoardId = req.getParameter("boardId");
		if (null == parmBoardId) {
			String errorMessage = "게시판 식별자 값을 넣어 주세요.";
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
					
		String parmBoardNo = req.getParameter("boardNo");
		
		if (null == parmBoardNo) {
			String errorMessage = "게시판 번호 값을 넣어 주세요";
			String debugMessage = "the web parameter 'boardNo' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		long boardNo = 0L;
		
		try {
			boardNo = Long.parseLong(parmBoardNo);
		}catch (NumberFormatException nfe) {
			String errorMessage = "잘못된 게시판 번호입니다";
			String debugMessage = new StringBuilder("the web parameter \"boardNo\"'s value[")
					.append(parmBoardNo)
					.append("] is not a Long").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}			
		
		if (boardNo <= 0) {
			String errorMessage = "게시판 번호는 0 보다 커야 합니다";
			String debugMessage = new StringBuilder("the web parameter \"boardNo\"'s value[")
					.append(parmBoardNo)
					.append("] is less than or equal to zero").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}		
		
		String parmSubject = req.getParameter("subject");
		if (null == parmSubject) {
			String errorMessage = "제목 값을 넣어주세요.";
			String debugMessage = "the web parameter 'subject' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		String parmContent = req.getParameter("content");		
		if (null == parmContent) {
			String errorMessage = "글 내용 값을 넣어주세요.";
			String debugMessage = "the web parameter 'content' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		String parmAttachId = req.getParameter("attachId");
		if (null == parmAttachId) {
			String errorMessage = "업로드 식별자를 넣어주세요.";
			String debugMessage = "the web parameter 'attachId' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		long attachId = 0L;
		try {
			attachId = Long.parseLong(parmAttachId);
		}catch (NumberFormatException nfe) {
			String errorMessage = new StringBuilder("자바 long 타입 변수인 업로드 식별자 값[")
			.append(parmAttachId).append("]이 잘못되었습니다.").toString();
			printErrorMessagePage(req, res, errorMessage, "");
			return;
		}
		
		if (attachId < 0) {
			String errorMessage = new StringBuilder("업로드 식별자 값[")
			.append(parmAttachId).append("]은 0 보다 작거나 커야합니다.").toString();
			printErrorMessagePage(req, res, errorMessage, "");
			return;
		}
		
		if (attachId > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			String errorMessage = new StringBuilder("업로드 식별자 값[")
			.append(parmAttachId).append("]은 ")
			.append(CommonStaticFinalVars.UNSIGNED_INTEGER_MAX)
			.append(" 값 보다 작거나 같아야합니다.").toString();
			printErrorMessagePage(req, res, errorMessage, "");
			return;
		}
					
		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setBoardId(boardId);
		boardModifyReq.setBoardNo(boardNo);
		boardModifyReq.setSubject(parmSubject);
		boardModifyReq.setContent(parmContent);
		boardModifyReq.setAttachId(attachId);
		boardModifyReq.setUserId(getLoginUserIDFromHttpSession(req));
		boardModifyReq.setIp(req.getRemoteAddr());
		
		// FIXME!
		log.debug("inObj={}");
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(boardModifyReq);
		if (outputMessage instanceof MessageResultRes) {
			MessageResultRes outObj = (MessageResultRes)outputMessage;
			
			if (! outObj.getIsSuccess()) {
				String errorMessage = outObj.getResultMessage();
				printErrorMessagePage(req, res, errorMessage, "");
				return;
			} else {
				doProcessPage(req, res, parmBoardId, parmBoardNo, outObj);
				return;
			}
		} else {
			String errorMessage = "게시판 수정이 실패했습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(boardModifyReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
	}
	
	private void doFirstPage(HttpServletRequest req, HttpServletResponse res,
			String parmBoardId, String parmBoardNo,
			BoardDetailRes boardDetailRes) {
		final String goPage = "/menu/board/BoardModify01.jsp";
		req.setAttribute("parmBoardId", parmBoardId);
		req.setAttribute("parmBoardNo", parmBoardNo);
		req.setAttribute("boardDetailRes", boardDetailRes);
		printJspPage(req, res, goPage);
	}

	private void doProcessPage(HttpServletRequest req, HttpServletResponse res,			 
			String parmBoardId, String parmBoardNo,
			MessageResultRes messageResultRes) {
		final String goPage = "/menu/board/BoardModify02.jsp";
		req.setAttribute("parmBoardId", parmBoardId);
		req.setAttribute("parmBoardNo", parmBoardNo);
		req.setAttribute("messageResultRes", messageResultRes);
		printJspPage(req, res, goPage);
	}

}
