package kr.pe.codda.servlet.user;

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
		
		/**************** 파라미터 시작 *******************/	
		String paramBoardID = req.getParameter("boardID");
		String paramBoardNo = req.getParameter("boardNo");
		/**************** 파라미터 종료 *******************/	
		
		
		if (null == paramBoardID) {
			String errorMessage = "게시판 식별자 값을 넣어 주세요";
			String debugMessage = "the web parameter 'boardID' is null";
			log.warn(debugMessage);
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
			log.warn(debugMessage);
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
			log.warn(debugMessage);
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		if (null == paramBoardNo) {
			String errorMessage = "게시판 번호를 입력해 주세요";
			String debugMessage = "the web parameter 'boardNo' is null";
			log.warn(debugMessage);
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
			log.warn(debugMessage);
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (boardNo <= 0) {
			String errorMessage = "게시판 번호 값은 0 보다 커야합니다.";
			String debugMessage = new StringBuilder("the web parameter \"boardN\"'s value[")
					.append(paramBoardNo)
					.append("] is less than or equal to zero").toString();
			log.warn(debugMessage);
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		BoardVoteReq boardVoteReq =  new BoardVoteReq();
		boardVoteReq.setRequestedUserID(getLoginedUserIDFromHttpSession(req));
		boardVoteReq.setBoardID(boardID);
		boardVoteReq.setBoardNo(boardNo);
		boardVoteReq.setIp(req.getRemoteAddr());
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(boardVoteReq);
		
		if (!(outputMessage instanceof MessageResultRes)) {
			
			String errorMessage = "게시판 추천이 실패했습니다";
			String debugMessage = String.format("입력 메시지[%s]에 대한 비 정상 출력 메시지[%s] 도착", boardVoteReq.getMessageID(), outputMessage.toString());
			
			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;			
		}		
		
		MessageResultRes messageResultRes = (MessageResultRes)outputMessage;
		
		if (! messageResultRes.getIsSuccess()) {
			String errorMessage = messageResultRes.getResultMessage();			
			printErrorMessagePage(req, res, errorMessage, null);
			return;
		}
		
		printJspPage(req, res, "/jsp/community/BoardVote.jsp");
		return;
	}

}
