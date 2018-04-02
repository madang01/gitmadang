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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.client.AnyProjectConnectionPoolIF;
import kr.pe.sinnori.client.ConnectionPoolManager;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.BoardDetailReq.BoardDetailReq;
import kr.pe.sinnori.impl.message.BoardDetailRes.BoardDetailRes;
import kr.pe.sinnori.impl.message.BoardModifyReq.BoardModifyReq;
import kr.pe.sinnori.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.sinnori.impl.message.SelfExnRes.SelfExnRes;
import kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars;
import kr.pe.sinnori.weblib.jdf.AbstractLoginServlet;

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
		req.setAttribute(WebCommonStaticFinalVars.SITE_TOPMENU_REQUEST_KEY_NAME, 
				kr.pe.sinnori.weblib.sitemenu.SiteTopMenuType.COMMUNITY);
		
		String goPage = null;		
		
		String parmPageMode = req.getParameter("pageMode");
		if (null == parmPageMode) {
			parmPageMode = "view";
		}
		
		if (!parmPageMode.equals("view") && !parmPageMode.equals("proc")) {
			goPage = "/menu/board/BoardModify01.jsp";
			String errorMessage = new StringBuilder("페이지 모드는 2가지(view, proc) 입니다.")
			.append(CommonStaticFinalVars.NEWLINE)
			.append("페이지 모드 값[").append(parmPageMode).append("]이 잘못 되었습니다.").toString();
			
			log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
			
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}		
		
		if (parmPageMode.equals("view")) {
			goPage = "/menu/board/BoardModify01.jsp";
			
			String parmBoardId = req.getParameter("boardId");
			if (null == parmBoardId) {
				String errorMessage = "게시판 식별자를 넣어주세요.";
				log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
				
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			short boardId = 0;
			try {
				boardId = Short.parseShort(parmBoardId);
			}catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder("자바 long 타입 변수인 게시판 식별자 값[")
				.append(parmBoardId).append("]이 잘못되었습니다.").toString();
				log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
				
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			if (boardId <= 0) {
				String errorMessage = new StringBuilder("게시판 식별자 값[")
				.append(parmBoardId).append("]은 0 보다 커야합니다.").toString();
				log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
				
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			String parmBoardNo = req.getParameter("boardNo");
			if (null == parmBoardNo) {
				String errorMessage = "게시판 번호를 넣어주세요.";
				log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
				
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			
			long boardNo = 0L;
			try {
				boardNo = Long.parseLong(parmBoardNo);
			}catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder("자바 long 타입 변수인 게시판 번호 값[")
				.append(parmBoardId).append("]이 잘못되었습니다.").toString();
				log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
				
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			if (boardNo <= 0) {
				String errorMessage = new StringBuilder("게시판 번호 값[")
				.append(parmBoardId).append("]은 0 보다 커야합니다.").toString();
				log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
				
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			BoardDetailReq inObj = new BoardDetailReq();
			inObj.setBoardId(boardId);
			inObj.setBoardNo(boardNo);
			
			// FIXME!
			log.debug("inObj={}, userId={}, ip={}", inObj.toString(), getUserId(req), req.getRemoteAddr());
			
			String errorMessage = "";
			AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
			AbstractMessage messageFromServer = mainProjectConnectionPool.sendSyncInputMessage(inObj);
			
			// FIXME!
			log.debug("inObj={}, messageFromServer={}, userId={}, ip={}", inObj.toString(), messageFromServer.toString(), getUserId(req), req.getRemoteAddr());
			
			if (messageFromServer instanceof BoardDetailRes) {
				BoardDetailRes outObj = (BoardDetailRes)messageFromServer;				
				
				req.setAttribute("boardDetailOutDTO", outObj);
			} else {				
				if (messageFromServer instanceof MessageResultRes) {
					MessageResultRes messageResultOutObj = (MessageResultRes)messageFromServer;
					errorMessage = messageResultOutObj.getResultMessage();
					
					log.warn("입력 메시지[{}]의 응답 메시지[{}]로 MessageResult 메시지 도착, userId={}, ip={}", inObj.toString(), messageFromServer.toString(), getUserId(req), req.getRemoteAddr());
				} else {
					errorMessage = "게시판 상세 조회가 실패하였습니다.";
					
					if (messageFromServer instanceof SelfExnRes) {
						log.warn("입력 메시지[{}]의 응답 메시지로 SelfExn 메시지 도착, 응답 메시지=[{}]", inObj.toString(), messageFromServer.toString());
					} else {
						log.warn("입력 메시지[{}]의 응답 메시지로 알 수 없는 메시지 도착, 응답 메시지=[{}]", inObj.toString(), messageFromServer.toString());
					}
				}				
			}
			
			// FIXME!
			// log.info("parmBoardNo={}", parmBoardNo);
			
			req.setAttribute("parmBoardId", parmBoardId);
			req.setAttribute("parmBoardNo", parmBoardNo);
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		} else {		
			goPage = "/menu/board/BoardModify02.jsp";
			
						
			String parmBoardId = req.getParameter("boardId");			
			if (null == parmBoardId) {
				String errorMessage = "게시판 식별자 값을 넣어 주세요.";
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			short boardId = 2;
			try {
				boardId = Short.parseShort(parmBoardId);
			}catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder("자바 long 타입 변수인 게시판 식별자 값[")
				.append(parmBoardId).append("]이 잘못되었습니다.").toString();
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			
			if (boardId <= 0) {
				String errorMessage = new StringBuilder("게시판 식별자 값[")
				.append(parmBoardId).append("]은 0 보다 커야합니다.").toString();
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}	
			
						
			String parmBoardNo = req.getParameter("boardNo");
			
			if (null == parmBoardNo) {
				String errorMessage = "게시판 번호 값을 넣어 주세요.";
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			long boardNo = 0L;
			
			try {
				boardNo = Long.parseLong(parmBoardNo);
			}catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder("자바 long 타입 변수인 게시판 식별자 값[")
				.append(parmBoardNo).append("]이 잘못되었습니다.").toString();
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}			
			
			if (boardNo <= 0) {
				String errorMessage = new StringBuilder("게시판 식별자 값[")
				.append(parmBoardNo).append("]은 0 보다 커야합니다.").toString();
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}		
			
			String parmSubject = req.getParameter("subject");
			if (null == parmSubject) {
				String errorMessage = "제목 값을 넣어주세요.";
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			
			String parmContent = req.getParameter("content");		
			if (null == parmContent) {
				String errorMessage = "글 내용 값을 넣어주세요.";
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			String parmAttachId = req.getParameter("attachId");
			if (null == parmAttachId) {
				String errorMessage = "업로드 식별자를 넣어주세요.";
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			long attachId = 0L;
			try {
				attachId = Long.parseLong(parmAttachId);
			}catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder("자바 long 타입 변수인 업로드 식별자 값[")
				.append(parmAttachId).append("]이 잘못되었습니다.").toString();
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			if (attachId < 0) {
				String errorMessage = new StringBuilder("업로드 식별자 값[")
				.append(parmAttachId).append("]은 0 보다 작거나 커야합니다.").toString();
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			if (attachId > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
				String errorMessage = new StringBuilder("업로드 식별자 값[")
				.append(parmAttachId).append("]은 ")
				.append(CommonStaticFinalVars.UNSIGNED_INTEGER_MAX)
				.append(" 값 보다 작거나 같아야합니다.").toString();
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			
			String errorMessage = "";			
			BoardModifyReq inObj = new BoardModifyReq();
			inObj.setBoardId(boardId);
			inObj.setBoardNo(boardNo);
			inObj.setSubject(parmSubject);
			inObj.setContent(parmContent);
			inObj.setAttachId(attachId);
			inObj.setUserId(getUserId(req));
			inObj.setIp(req.getRemoteAddr());
			
			// FIXME!
			log.debug("inObj={}");
			
			AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
			AbstractMessage messageFromServer = mainProjectConnectionPool.sendSyncInputMessage(inObj);
			if (messageFromServer instanceof MessageResultRes) {
				MessageResultRes outObj = (MessageResultRes)messageFromServer;					
				req.setAttribute("messageResultOutObj", outObj);
			} else {				
				errorMessage = "게시판 글 수정 처리가 실패하였습니다.";
				
				if (messageFromServer instanceof SelfExnRes) {
					log.warn("입력 메시지[{}]의 응답 메시지로 SelfExn 메시지 도착, 응답 메시지=[{}]", inObj.toString(), messageFromServer.toString());
				} else {
					log.warn("입력 메시지[{}]의 응답 메시지로 알 수 없는 메시지 도착, 응답 메시지=[{}]", inObj.toString(), messageFromServer.toString());
				}
			}			
			
			req.setAttribute("parmBoardId", parmBoardId);	
			req.setAttribute("parmBoardNo", parmBoardNo);
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
		}
	}

}