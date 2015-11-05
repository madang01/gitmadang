package kr.pe.sinnori.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.pe.sinnori.client.ClientProject;
import kr.pe.sinnori.client.ClientProjectManager;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.BoardVoteInDTO.BoardVoteInDTO;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;
import kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars;
import kr.pe.sinnori.weblib.jdf.AbstractAuthServlet;

/**
 * 
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class BoardVoteSvl extends AbstractAuthServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		String goPage = "/menu/board/BoardVote01.jsp";
		
		/*int pageNo = 1;
		
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
		}*/
		
		String parmBoardId = req.getParameter("boardId");
		if (null == parmBoardId) {
			String errorMessage = "게시판 식별자를 넣어주세요.";
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}
		
		long boardId = 0L;
		try {
			boardId = Long.parseLong(parmBoardId);
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
			String errorMessage = "게시판 번호를 넣어주세요.";
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
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}
		
		if (boardNo <= 0) {
			String errorMessage = new StringBuilder("게시판 번호 값[")
			.append(parmBoardId).append("]은 0 보다 커야합니다.").toString();
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
			return;
		}
		
		HttpSession httpSession = req.getSession();
		String userId = (String) httpSession.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_USERID_NAME);
		
		
		BoardVoteInDTO inObj =  new BoardVoteInDTO();
		inObj.setBoardId(boardId);
		inObj.setBoardNo(boardNo);
		inObj.setUserId(userId);
		inObj.setIp(req.getRemoteAddr());
		
		String errorMessage = "";
		ClientProject clientProject = ClientProjectManager.getInstance().getMainClientProject();
		AbstractMessage messageFromServer = clientProject.sendSyncInputMessage(inObj);
		
		if (messageFromServer instanceof MessageResult) {
			MessageResult messageResultOutObj = (MessageResult)messageFromServer;
			
			req.setAttribute("messageResultOutObj", messageResultOutObj);		
			
			log.warn("입력 메시지[{}]의 응답 메시지로 MessageResult 메시지 도착, 응답 메시지=[{}]", inObj.toString(), messageFromServer.toString());
		} else {
			errorMessage = "게시판 상세 조회가 실패하였습니다.";
			
			if (messageFromServer instanceof SelfExn) {
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
