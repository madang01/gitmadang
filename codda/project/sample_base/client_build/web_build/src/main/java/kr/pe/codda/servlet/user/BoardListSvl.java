package kr.pe.codda.servlet.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.BoardListReq.BoardListReq;
import kr.pe.codda.impl.message.BoardListRes.BoardListRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractSessionKeyServlet;

@SuppressWarnings("serial")
public class BoardListSvl extends AbstractSessionKeyServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		/**************** 파라미터 시작 *******************/
		String paramBoardID = req.getParameter("boardID");
		String paramPageNo = req.getParameter("pageNo");
		/**************** 파라미터 종료 *******************/
		
		short boardID = -1;
		try {
			boardID = ValueChecker.checkValidBoardID(paramBoardID);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		int pageNo = 1;
		try {
			pageNo = ValueChecker.checkValidPageNo(paramPageNo);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		// int pageSize = 20;		
		
		BoardListReq boardListReq = new BoardListReq();
		boardListReq.setRequestedUserID(getLoginedUserIDFromHttpSession(req));
		boardListReq.setBoardID(boardID);
		boardListReq.setPageNo(pageNo);
		boardListReq.setPageSize(WebCommonStaticFinalVars.WEBSITE_BOARD_LIST_SIZE_PER_PAGE);
		// boardListReq.setPageOffset((pageNo - 1) * WebCommonStaticFinalVars.WEBSITE_BOARD_LIST_SIZE_PER_PAGE);
		// boardListReq.setPageLength(WebCommonStaticFinalVars.WEBSITE_BOARD_LIST_SIZE_PER_PAGE);
				
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), boardListReq);
		
		if (!(outputMessage instanceof BoardListRes)) {
			if (outputMessage instanceof MessageResultRes) {
				MessageResultRes messageResultRes = (MessageResultRes)outputMessage;
				String errorMessage = "게시판 목록 조회가 실패하였습니다";
				String debugMessage = messageResultRes.toString();
				printErrorMessagePage(req, res, errorMessage, debugMessage);	
				return;
			} else {
				String errorMessage = "게시판 목록 조회가 실패했습니다";
				String debugMessage = new StringBuilder("입력 메시지[")
						.append(boardListReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[")
						.append(outputMessage.toString())
						.append("] 도착").toString();
				
				log.error(debugMessage);

				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
		} 
		
		BoardListRes boardListRes = (BoardListRes)outputMessage;
		req.setAttribute("boardListRes", boardListRes);
		printJspPage(req, res, "/jsp/community/BoardList.jsp");
	}
}
