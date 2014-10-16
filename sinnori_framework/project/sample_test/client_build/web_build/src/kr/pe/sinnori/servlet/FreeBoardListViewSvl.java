package kr.pe.sinnori.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.client.ClientProject;
import kr.pe.sinnori.client.ClientProjectManager;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.servlet.AbstractServlet;
import kr.pe.sinnori.common.servlet.WebCommonType;
import kr.pe.sinnori.impl.message.BoardListRequest.BoardListRequest;
import kr.pe.sinnori.impl.message.BoardListResponse.BoardListResponse;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;

@SuppressWarnings("serial")
public class FreeBoardListViewSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String goPage = "/board/FreeBoardListView01.jsp";
		
		BoardListRequest boardListRequestInObj = new BoardListRequest();
		boardListRequestInObj.setBoardTypeID(WebCommonType.BOARD_TYPE.FREE_BOARD.getBoardTypeID());
		
		String projectName = System.getProperty(CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME);		
		ClientProject clientProject = ClientProjectManager.getInstance().getClientProject(projectName);
		AbstractMessage messageFromServer = clientProject.sendSyncInputMessage(boardListRequestInObj);
		
		String errorMessage = "";
		if (!(messageFromServer instanceof BoardListResponse)) {
			errorMessage = "게시판 조회가 실패하였습니다.";
			
			if (!(messageFromServer instanceof SelfExn)) {
				log.warn("입력 메시지[{}]의 응답 메시지로 알 수 없는 메시지 도착, 응답 메시지=[{}]", boardListRequestInObj.toString(), messageFromServer.toString());
			} else {
				log.warn("입력 메시지[{}]의 응답 메시지로 SelfExn 메시지 도착, 응답 메시지=[{}]", boardListRequestInObj.toString(), messageFromServer.toString());
			}
			
		} else {
			BoardListResponse boardListResponseOutObj = (BoardListResponse)messageFromServer;
			req.setAttribute("boardListResponseOutObj", boardListResponseOutObj);
		}
		
		req.setAttribute("errorMessage", errorMessage);
		printJspPage(req, res, goPage);		
	}

}
