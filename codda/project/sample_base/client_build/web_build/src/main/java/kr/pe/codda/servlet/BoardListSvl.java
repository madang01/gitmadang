package kr.pe.codda.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardListReq.BoardListReq;
import kr.pe.codda.impl.message.BoardListRes.BoardListRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.BoardType;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractSessionKeyServlet;

@SuppressWarnings("serial")
public class BoardListSvl extends AbstractSessionKeyServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		short boardID = BoardType.FREE.getBoardID();
		String paramBoardID = req.getParameter("boardID");
		if (null == paramBoardID) {
			String errorMessage = "게시판 식별자를 입력하세요";
			String debugMessage = new StringBuilder("the web parameter 'boardID'[")
					.append(paramBoardID).append("] is null").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		try {
			boardID = Short.parseShort(paramBoardID);
		}catch (NumberFormatException nfe) {
			String errorMessage = "잘못된 게시판 식별자 입니다";
			String debugMessage = new StringBuilder("the web parameter 'boardID'[")
					.append(paramBoardID).append("] is not a short").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		try {
			BoardType.valueOf(boardID);
		} catch(IllegalArgumentException e) {
			String errorMessage = "알 수 없는 게시판 식별자 입니다";
			String debugMessage = new StringBuilder("the web parameter 'boardId'[")
					.append(paramBoardID).append("] is not a element of set[")
					.append(BoardType.getSetString())
					.append("]").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		int pageNo = 1;
		
		String paramPageNo = req.getParameter("pageNo");
		if (null == paramPageNo) {
			paramPageNo = "1";
		}
		
		try {
			pageNo = Integer.parseInt(paramPageNo);
		}catch (NumberFormatException nfe) {
			String errorMessage = "잘못된 페이지 번호 입니다";
			String debugMessage = new StringBuilder("the web parameter 'pageNo'[")
					.append(paramPageNo).append("] is not a integer").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}		
		
		if (pageNo <= 0) {
			String errorMessage = "페이지 번호의 값은 0보다 커야 합니다";
			String debugMessage = new StringBuilder("the web parameter 'pageNo'[")
					.append(paramPageNo).append("] is less than or equal to zero").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		// int pageSize = 20;
		
		
		BoardListReq boardListReq = new BoardListReq();
		boardListReq.setBoardID(boardID);
		boardListReq.setPageOffset((pageNo - 1) * WebCommonStaticFinalVars.WEBSITE_BOARD_LIST_SIZE_PER_PAGE);
		boardListReq.setPageLength(WebCommonStaticFinalVars.WEBSITE_BOARD_LIST_SIZE_PER_PAGE);
				
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(boardListReq);
		
		if (outputMessage instanceof BoardListRes) {
			BoardListRes boardListRes = (BoardListRes)outputMessage;
			
			doListPage(req,res, paramBoardID, boardListRes);
			return;
		} else if (outputMessage instanceof MessageResultRes) {
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

	public void doListPage(HttpServletRequest req, HttpServletResponse res,
			String paramBoardId, 
			BoardListRes boardListRes) {
		final String goPage = "/jsp/community/BoardList.jsp";
		
		req.setAttribute("boardListRes", boardListRes);
		printJspPage(req, res, goPage);
	}
}
