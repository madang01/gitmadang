package kr.pe.codda.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardVoteReq.BoardVoteReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.BoardType;
import kr.pe.codda.weblib.jdf.AbstractLoginServlet;

/**
 * 
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class BoardVoteSvl extends AbstractLoginServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		
		String parmBoardId = req.getParameter("boardId");
		if (null == parmBoardId) {
			String errorMessage = "게시판 식별자 값을 넣어 주세요";
			String debugMessage = "the web parameter 'boardId' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);	
			return;
		}
		
		short boardId = 0;
		try {
			boardId = Short.parseShort(parmBoardId);
		}catch (NumberFormatException nfe) {
			String errorMessage = "잘못된 게시판 식별자 입니다";
			String debugMessage = new StringBuilder("the web parameter 'boardId'[")
					.append(parmBoardId).append("] is not a short").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		try {
			BoardType.valueOf(boardId);
		} catch(IllegalArgumentException e) {
			String errorMessage = "알 수 없는 게시판 식별자 입니다";
			String debugMessage = new StringBuilder("the web parameter 'boardId'[")
					.append(parmBoardId).append("] is not a element of set[")
					.append(BoardType.getSetString())
					.append("]").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		String parmBoardNo = req.getParameter("boardNo");
		if (null == parmBoardNo) {
			String errorMessage = "게시판 번호를 입력해 주세요";
			String debugMessage = "the web parameter 'boardNo' is null";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}		
		
		long boardNo = 0L;
		try {
			boardNo = Long.parseLong(parmBoardNo);
		}catch (NumberFormatException nfe) {
			String errorMessage = "잘못된 게시판 번호입니다";
			String debugMessage = new StringBuilder("the web parameter \"boardN\"'s value[")
					.append(parmBoardNo)
					.append("] is a Long").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (boardNo <= 0) {
			String errorMessage = "게시판 번호 값은 0 보다 커야합니다.";
			String debugMessage = new StringBuilder("the web parameter \"boardN\"'s value[")
					.append(parmBoardNo)
					.append("] is less than or equal to zero").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		BoardVoteReq inObj =  new BoardVoteReq();
		inObj.setBoardId(boardId);
		inObj.setBoardNo(boardNo);
		inObj.setUserId(getLoginedUserID(req));
		inObj.setIp(req.getRemoteAddr());
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(inObj);
		
		if (outputMessage instanceof MessageResultRes) {
			MessageResultRes messageResultRes = (MessageResultRes)outputMessage;
			
			doVotePage(req, res, parmBoardId, parmBoardNo, messageResultRes);
			return;
		} else {
			String errorMessage = "게시판 추천이 실패했습니다";
			String debugMessage = String.format("입력 메시지[%s]에 대한 비 정상 출력 메시지[%s] 도착", inObj.getMessageID(), outputMessage.toString());
			
			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}	
	}

	private void doVotePage(HttpServletRequest req, HttpServletResponse res, String parmBoardId, String parmBoardNo,
			MessageResultRes messageResultRes) {
		req.setAttribute("parmBoardId", parmBoardId);
		req.setAttribute("parmBoardNo", parmBoardNo);
		req.setAttribute("messageResultRes", messageResultRes);
		printJspPage(req, res, "/menu/board/BoardVote01.jsp");
	}
}
