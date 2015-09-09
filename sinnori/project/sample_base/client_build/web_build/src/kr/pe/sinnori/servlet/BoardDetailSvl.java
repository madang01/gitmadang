package kr.pe.sinnori.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.client.ClientProject;
import kr.pe.sinnori.client.ClientProjectManager;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.weblib.AbstractServlet;
import kr.pe.sinnori.impl.message.BoardDetailInDTO.BoardDetailInDTO;
import kr.pe.sinnori.impl.message.BoardDetailOutDTO.BoardDetailOutDTO;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;

@SuppressWarnings("serial")
public class BoardDetailSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String goPage = "/menu/board/BoardDetail01.jsp";
		
		String parmBoardId = req.getParameter("boardId");
		if (null == parmBoardId) {
			String errorMessage = "게시판 식별자를 넣어주세요.";
			req.setAttribute("errorMessage", errorMessage);
			printWebLayoutControlJspPage(req, res, goPage);
			return;
		}
		
		long boardId = 0L;
		try {
			boardId = Long.parseLong(parmBoardId);
		}catch (NumberFormatException nfe) {
			String errorMessage = new StringBuilder("자바 long 타입 변수인 게시판 식별자 값[")
			.append(parmBoardId).append("]이 잘못되었습니다.").toString();
			req.setAttribute("errorMessage", errorMessage);
			printWebLayoutControlJspPage(req, res, goPage);
			return;
		}
		
		if (boardId <= 0) {
			String errorMessage = new StringBuilder("게시판 식별자 값[")
			.append(parmBoardId).append("]은 0 보다 커야합니다.").toString();
			req.setAttribute("errorMessage", errorMessage);
			printWebLayoutControlJspPage(req, res, goPage);
			return;
		}
		
		String parmBoardNo = req.getParameter("boardNo");
		if (null == parmBoardNo) {
			String errorMessage = "게시판 번호를 넣어주세요.";
			req.setAttribute("errorMessage", errorMessage);
			printWebLayoutControlJspPage(req, res, goPage);
			return;
		}
		
		
		long boardNo = 0L;
		try {
			boardNo = Long.parseLong(parmBoardNo);
		}catch (NumberFormatException nfe) {
			String errorMessage = new StringBuilder("자바 long 타입 변수인 게시판 번호 값[")
			.append(parmBoardId).append("]이 잘못되었습니다.").toString();
			req.setAttribute("errorMessage", errorMessage);
			printWebLayoutControlJspPage(req, res, goPage);
			return;
		}
		
		if (boardNo <= 0) {
			String errorMessage = new StringBuilder("게시판 번호 값[")
			.append(parmBoardId).append("]은 0 보다 커야합니다.").toString();
			req.setAttribute("errorMessage", errorMessage);
			printWebLayoutControlJspPage(req, res, goPage);
			return;
		}
		
		
		// String userId = getUserId(req);
		
		
		String projectName = System.getProperty(CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME);
		
		BoardDetailInDTO inObj = new BoardDetailInDTO();
		inObj.setBoardId(boardId);
		inObj.setBoardNo(boardNo);
		
		// FIXME!
		//log.info("inObj={}, userId={}, ip={}", inObj.toString(), userId, req.getRemoteAddr());
		
		String errorMessage = "";
		ClientProject clientProject = ClientProjectManager.getInstance().getClientProject(projectName);
		AbstractMessage messageFromServer = clientProject.sendSyncInputMessage(inObj);
		if (messageFromServer instanceof BoardDetailOutDTO) {
			BoardDetailOutDTO outObj = (BoardDetailOutDTO)messageFromServer;				
			
			req.setAttribute("boardDetailOutDTO", outObj);
		} else {				
			if (messageFromServer instanceof MessageResult) {
				MessageResult messageResultOutObj = (MessageResult)messageFromServer;
				errorMessage = messageResultOutObj.getResultMessage();
				
				log.warn("입력 메시지[{}]의 응답 메시지로 MessageResult 메시지 도착, 응답 메시지=[{}], userId={}, ip={}", 
						inObj.toString(), messageFromServer.toString(), getUserId(req), req.getRemoteAddr());
			} else {
				errorMessage = "게시판 상세 조회가 실패하였습니다.";
				
				if (messageFromServer instanceof SelfExn) {
					log.warn("입력 메시지[{}]의 응답 메시지로 SelfExn 메시지 도착, 응답 메시지=[{}]", inObj.toString(), messageFromServer.toString());
				} else {
					log.warn("입력 메시지[{}]의 응답 메시지로 알 수 없는 메시지 도착, 응답 메시지=[{}]", inObj.toString(), messageFromServer.toString());
				}
			}				
		}
		
		
		// FIXME!
		//log.info("parmBoardNo={}", parmBoardNo);
		
		req.setAttribute("parmBoardId", parmBoardId);
		req.setAttribute("parmBoardNo", parmBoardNo);
		req.setAttribute("errorMessage", errorMessage);
		printWebLayoutControlJspPage(req, res, goPage);
		
	}
}
