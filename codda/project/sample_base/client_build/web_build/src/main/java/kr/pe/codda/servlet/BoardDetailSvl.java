package kr.pe.codda.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardDetailReq.BoardDetailReq;
import kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.BoardType;
import kr.pe.codda.weblib.jdf.AbstractServlet;

@SuppressWarnings("serial")
public class BoardDetailSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		/**************** 파라미터 시작 *******************/
		String paramBoardID = req.getParameter("boardID");
		String paramBoardNo = req.getParameter("boardNo");
		/**************** 파라미터 종료 *******************/
		
		if (null == paramBoardID) {
			String errorMessage = "게시판 식별자를 입력해 주세요";
			String debugMessage = "the web parameter 'boardID' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		short boardID = 0;
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
			String debugMessage = new StringBuilder("the web parameter 'boardID'[")
					.append(paramBoardID).append("] is not a element of set[")
					.append(BoardType.getSetString())
					.append("]").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		if (null == paramBoardNo) {
			String errorMessage = "게시판 번호를 입력해 주세요";
			String debugMessage = "the web parameter 'boardNo' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		long boardNo = 0L;
		try {
			boardNo = Long.parseLong(paramBoardNo);
		}catch (NumberFormatException nfe) {
			String errorMessage = "잘못된 게시판 번호입니다";
			String debugMessage = new StringBuilder("the web parameter \"boardN\"'s value[")
					.append(paramBoardNo)
					.append("] is a Long").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (boardNo <= 0) {
			String errorMessage = "게시판 번호 값은 0 보다 커야합니다.";
			String debugMessage = new StringBuilder("the web parameter \"boardN\"'s value[")
					.append(paramBoardNo)
					.append("] is less than or equal to zero").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setBoardID(boardID);
		boardDetailReq.setBoardNo(boardNo);
		
		// FIXME!
		//log.info("inObj={}, userId={}, ip={}", inObj.toString(), userId, req.getRemoteAddr());
		
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(boardDetailReq);
		
		if (outputMessage instanceof MessageResultRes) {
			MessageResultRes messageResultRes = (MessageResultRes)outputMessage;
			String errorMessage = "게시판 상세 조회가 실패하였습니다";
			String debugMessage = messageResultRes.toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);	
			return;
		} else if (! (outputMessage instanceof BoardDetailRes)){
			String errorMessage = "게시판 상세 조회가 실패했습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(boardDetailReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}	
		
		
		BoardDetailRes boardDetailRes = (BoardDetailRes)outputMessage;
		req.setAttribute("boardDetailRes", boardDetailRes);
		printJspPage(req, res, "/jsp/community/BoardDetail.jsp");
		return;
		 	
	}
	
}
