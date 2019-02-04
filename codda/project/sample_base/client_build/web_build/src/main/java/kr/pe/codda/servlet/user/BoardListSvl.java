package kr.pe.codda.servlet.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
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
		
		/**************** 파라미터 시작 *******************/
		String paramBoardID = req.getParameter("boardID");
		String paramPageNo = req.getParameter("pageNo");
		/**************** 파라미터 종료 *******************/
		
		if (null == paramBoardID) {
			String errorMessage = "게시판 식별자를 입력하세요";
			String debugMessage = new StringBuilder("the web parameter 'boardID'[")
					.append(paramBoardID).append("] is null").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		short boardID = BoardType.FREE.getBoardID();
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
		
		if (null == paramPageNo) {
			paramPageNo = "1";
		} else {
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
			
			if (pageNo > CommonStaticFinalVars.UNSIGNED_SHORT_MAX) {
				String errorMessage = "페이지 번호가  unsigned short 타입의 최대값 65535 를 넘었습니다";
				String debugMessage = new StringBuilder("the web parameter 'pageNo'[")
						.append(paramPageNo).append("] is greater than unsinged short max(=65535)").toString();
				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
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
