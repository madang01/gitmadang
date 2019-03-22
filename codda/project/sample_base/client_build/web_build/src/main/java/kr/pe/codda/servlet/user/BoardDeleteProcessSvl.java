package kr.pe.codda.servlet.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.BoardDeleteReq.BoardDeleteReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.jdf.AbstractLoginServlet;

public class BoardDeleteProcessSvl extends AbstractLoginServlet {

	private static final long serialVersionUID = -238411848490485952L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramBoardID = req.getParameter("boardID");
		String paramBoardNo = req.getParameter("boardNo");
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
		
		long boardNo = 0L;
		try {
			boardNo = ValueChecker.checkValidBoardNo(paramBoardNo);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(getLoginedUserIDFromHttpSession(req));
		boardDeleteReq.setBoardID(boardID);
		boardDeleteReq.setBoardNo(boardNo);
		boardDeleteReq.setIp(req.getRemoteAddr());
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager
				.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(),
						boardDeleteReq);

		if (!(outputMessage instanceof MessageResultRes)) {

			String errorMessage = "게시판 삭제가 실패했습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(boardDeleteReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		MessageResultRes messageResultRes = (MessageResultRes) outputMessage;

		if (! messageResultRes.getIsSuccess()) {
			String errorMessage = messageResultRes.getResultMessage();
			printErrorMessagePage(req, res, errorMessage, null);
			return;
		}

		req.setAttribute("boardNo", paramBoardNo);
		printJspPage(req, res, "/jsp/community/BoardDeleteProcess.jsp");
		return;
	}

}
