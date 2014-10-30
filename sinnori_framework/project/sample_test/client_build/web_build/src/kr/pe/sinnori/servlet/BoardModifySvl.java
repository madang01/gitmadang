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
import javax.servlet.http.HttpSession;

import kr.pe.sinnori.client.ClientProject;
import kr.pe.sinnori.client.ClientProjectManager;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.weblib.AbstractAuthServlet;
import kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars;
import kr.pe.sinnori.impl.message.BoardModifyInDTO.BoardModifyInDTO;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;

/**
 * 게시판 글 수정 처리
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class BoardModifySvl extends AbstractAuthServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String goPage = null;		
		
		String parmPageMode = req.getParameter("pageMode");
		if (null == parmPageMode) {
			parmPageMode = "view";
		}
		
		if (!parmPageMode.equals("view") && !parmPageMode.equals("proc")) {
			goPage = "/board/BoardModify01.jsp";
			String errorMessage = new StringBuilder("페이지 모드는 2가지(view, proc) 입니다.")
			.append(CommonStaticFinalVars.NEWLINE)
			.append("페이지 모드 값[").append(parmPageMode).append("]이 잘못 되었습니다.").toString();
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}
		
		
		if (parmPageMode.equals("view")) {
			goPage = "/board/BoardModify01.jsp";
			req.setAttribute("errorMessage", "");
			printJspPage(req, res, goPage);
			return;
		} else {			
			String errorMessage = "";
			goPage = "/board/BoardModify02.jsp";
			
						
			String parmBoardId = req.getParameter("boardId");			
			if (null == parmBoardId) {
				errorMessage = "게시판 식별자 값을 넣어 주세요.";
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			long boardId = 2L;
			try {
				boardId = Long.parseLong(parmBoardId);
			}catch (NumberFormatException nfe) {
				errorMessage = new StringBuilder("자바 long 타입 변수인 게시판 식별자 값[")
				.append(parmBoardId).append("]이 잘못되었습니다.").toString();
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			
			if (boardId <= 0) {
				errorMessage = new StringBuilder("게시판 식별자 값[")
				.append(parmBoardId).append("]은 0 보다 커야합니다.").toString();
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}	
			
						
			String parmBoardNo = req.getParameter("boardNo");
			
			if (null == parmBoardNo) {
				errorMessage = "게시판 번호 값을 넣어 주세요.";
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			long boardNo = 0L;
			
			try {
				boardNo = Long.parseLong(parmBoardNo);
			}catch (NumberFormatException nfe) {
				errorMessage = new StringBuilder("자바 long 타입 변수인 게시판 식별자 값[")
				.append(parmBoardNo).append("]이 잘못되었습니다.").toString();
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}			
			
			if (boardNo <= 0) {
				errorMessage = new StringBuilder("게시판 식별자 값[")
				.append(parmBoardNo).append("]은 0 보다 커야합니다.").toString();
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}		
			
			String parmSubject = req.getParameter("subject");
			if (null == parmSubject) {
				errorMessage = "제목 값을 넣어주세요.";
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			
			String parmContent = req.getParameter("content");		
			if (null == parmContent) {
				errorMessage = "글 내용 값을 넣어주세요.";
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}			
			
			
			HttpSession httpSession = req.getSession();
			String userId = (String) httpSession.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_USERID_NAME);
			String projectName = System.getProperty(CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME);
			
			BoardModifyInDTO inObj = new BoardModifyInDTO();
			inObj.setBoardId(boardId);
			inObj.setBoardNo(boardNo);
			inObj.setSubject(parmSubject);
			inObj.setContent(parmContent);
			inObj.setWriterId(userId);
			inObj.setIp(req.getRemoteAddr());
			
			ClientProject clientProject = ClientProjectManager.getInstance().getClientProject(projectName);
			AbstractMessage messageFromServer = clientProject.sendSyncInputMessage(inObj);
			if (messageFromServer instanceof MessageResult) {
				MessageResult outObj = (MessageResult)messageFromServer;					
				req.setAttribute("messageResult", outObj);
			} else {				
				errorMessage = "게시판 댓글 등록이 실패하였습니다.";
				
				if (messageFromServer instanceof SelfExn) {
					log.warn("입력 메시지[{}]의 응답 메시지로 SelfExn 메시지 도착, 응답 메시지=[{}]", inObj.toString(), messageFromServer.toString());
				} else {
					log.warn("입력 메시지[{}]의 응답 메시지로 알 수 없는 메시지 도착, 응답 메시지=[{}]", inObj.toString(), messageFromServer.toString());
				}
			}			
			
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
		}
	}

}
