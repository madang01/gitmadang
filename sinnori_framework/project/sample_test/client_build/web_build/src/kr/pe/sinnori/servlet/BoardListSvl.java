package kr.pe.sinnori.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.client.ClientProject;
import kr.pe.sinnori.client.ClientProjectManager;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.weblib.AbstractServlet;
import kr.pe.sinnori.impl.message.BoardListInDTO.BoardListInDTO;
import kr.pe.sinnori.impl.message.BoardListOutDTO.BoardListOutDTO;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;

@SuppressWarnings("serial")
public class BoardListSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		String goPage = "/board/BoardList01.jsp";
		
		String parmBoardId = req.getParameter("boardId");
		if (null == parmBoardId) parmBoardId = "2";
		
		
		long boardId = 2L;		
		try {
			boardId = Long.parseLong(parmBoardId);
		}catch (NumberFormatException nfe) {
			String errorMessage = new StringBuilder("자바 long 타입 변수인 게시판 식별자(boardId) 값[")
			.append(parmBoardId).append("]이 잘못되었습니다.").toString();
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}
		
		
		if (boardId <= 0) {
			String errorMessage = new StringBuilder("게시판 식별자(boardId) 값[")
			.append(parmBoardId).append("]은 0 보다 커야합니다.").toString();
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}
		
		int pageNo = 1;
		
		String parmPageNo = req.getParameter("pageNo");
		if (null == parmPageNo) {
			parmPageNo = "1";
		}
		
		try {
			pageNo = Integer.parseInt(parmPageNo);
		}catch (NumberFormatException nfe) {
			String errorMessage = "parameter pageNo type is a not integer";
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}	
		
		
		if (pageNo <= 0) {
			String errorMessage = "parameter pageNo is less than or equal to zero";
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}
		
		int pageSize = 5;
		String errorMessage = "";
		
		BoardListInDTO inObj = new BoardListInDTO();
		inObj.setBoardId(boardId);
		inObj.setStartNo((pageNo - 1) * pageSize);
		inObj.setPageSize(pageSize);
		
		String projectName = System.getProperty(CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME);		
		ClientProject clientProject = ClientProjectManager.getInstance().getClientProject(projectName);
		AbstractMessage messageFromServer = clientProject.sendSyncInputMessage(inObj);
		
		if (messageFromServer instanceof BoardListOutDTO) {
			BoardListOutDTO boardListOutDTO = (BoardListOutDTO)messageFromServer;
			req.setAttribute("boardListOutDTO", boardListOutDTO);
		} else {			
			if (messageFromServer instanceof MessageResult) {
				log.warn("입력 메시지[{}]의 응답 메시지로 MessageResult 메시지 도착, 응답 메시지=[{}]", inObj.toString(), messageFromServer.toString());
				errorMessage = ((MessageResult)messageFromServer).getResultMessage();
			} else {
				errorMessage = "게시판 목록 메시지를 얻는데 실패하였습니다.";
				
				if (messageFromServer instanceof SelfExn) {
					log.warn("입력 메시지[{}]의 응답 메시지로 SelfExn 메시지 도착, 응답 메시지=[{}]", inObj.toString(), messageFromServer.toString());
				} else {
					log.warn("입력 메시지[{}]의 응답 메시지로 알 수 없는 메시지 도착, 응답 메시지=[{}]", inObj.toString(), messageFromServer.toString());
				}
			}
				
		}
		
		req.setAttribute("parmBoardId", parmBoardId);
		req.setAttribute("errorMessage", errorMessage);	
		printJspPage(req, res, goPage);
	}

}
