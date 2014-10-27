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
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;

@SuppressWarnings("serial")
public class BoardListSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		String goPage = "/board/BoardList01.jsp";
		
		String parmBoardId = req.getParameter("boardId");
		
		long boardID = 2L;
		
		if (null != parmBoardId) {
			try {
				boardID = Long.parseLong(parmBoardId);
			}catch (NumberFormatException nfe) {
				String errorMessage = "parameter pageNo type is a not long";
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
		}
		
		if (boardID <= 0) {
			String errorMessage = "parameter boardID is less than or equal to zero";
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}
		
		int pageNo = 1;
		
		String parmPageNo = req.getParameter("pageNo");
		if (null != parmPageNo) {
			try {
				pageNo = Integer.parseInt(parmPageNo);
			}catch (NumberFormatException nfe) {
				String errorMessage = "parameter pageNo type is a not integer";
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
		}		
		
		
		if (pageNo <= 0) {
			String errorMessage = "parameter pageNo is less than or equal to zero";
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}
		
		int pageSize = 10;
		String errorMessage = "";
		
		BoardListInDTO inObj = new BoardListInDTO();
		inObj.setBoardId(boardID);
		inObj.setStartNo((pageNo - 1) * pageSize);
		inObj.setPageSize(pageSize);
		
		String projectName = System.getProperty(CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME);		
		ClientProject clientProject = ClientProjectManager.getInstance().getClientProject(projectName);
		AbstractMessage messageFromServer = clientProject.sendSyncInputMessage(inObj);
		
		if (messageFromServer instanceof BoardListOutDTO) {
			BoardListOutDTO boardListOutDTO = (BoardListOutDTO)messageFromServer;
			req.setAttribute("boardListOutDTO", boardListOutDTO);
		} else {
			errorMessage = "게시판 목록 메시지를 얻는데 실패하였습니다.";
			
			if (!(messageFromServer instanceof SelfExn)) {
				log.warn("입력 메시지[{}]의 응답 메시지로 알 수 없는 메시지 도착, 응답 메시지=[{}]", inObj.toString(), messageFromServer.toString());
			} else {
				log.warn("입력 메시지[{}]의 응답 메시지로 SelfExn 메시지 도착, 응답 메시지=[{}]", inObj.toString(), messageFromServer.toString());
			}
		}
		
		req.setAttribute("errorMessage", errorMessage);
		printJspPage(req, res, goPage);
	}

}
