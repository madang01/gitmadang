package kr.pe.codda.servlet.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.BoardBlockReq.BoardBlockReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.jdf.AbstractUserLoginServlet;

public class BoardBlockProcessSvl extends AbstractUserLoginServlet {

	private static final long serialVersionUID = 3160934611426725072L;

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
		
		AccessedUserInformation accessedUserformation = getAccessedUserInformationFromSession(req);
		
		BoardBlockReq boardBlockReq = new BoardBlockReq();
		boardBlockReq.setRequestedUserID(accessedUserformation.getUserID());
		boardBlockReq.setBoardID(boardID);
		boardBlockReq.setBoardNo(boardNo);
		boardBlockReq.setIp(req.getRemoteAddr());
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager
				.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(),
						boardBlockReq);

		if (!(outputMessage instanceof MessageResultRes)) {

			String errorMessage = "게시판 차단이 실패했습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(boardBlockReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		MessageResultRes messageResultRes = (MessageResultRes) outputMessage;

		if (!messageResultRes.getIsSuccess()) {
			String errorMessage = messageResultRes.getResultMessage();
			printErrorMessagePage(req, res, errorMessage, null);
			return;
		}

		req.setAttribute("boardNo", paramBoardNo);
		printJspPage(req, res, "/jsp/community/BoardBlockProcess.jsp");
		return;
	}
	

	
}
