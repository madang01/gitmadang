package kr.pe.codda.servlet.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.BoardInfoDeleteReq.BoardInfoDeleteReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class BoardInfoDeleteSvl extends AbstractAdminLoginServlet {
	
	private static final long serialVersionUID = 5980809617081252881L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramBoardID = req.getParameter("boardID");
		/**************** 파라미터 종료 *******************/
		
		short boardID = -1;
		try {			
			boardID = Short.parseShort(paramBoardID);
		} catch(NumberFormatException e) {
			String errorMessage = "게시판 식별자를 다시 넣어 주세요";
			String debugMessage = new StringBuilder()
					.append("파라미터 '게시판 식별자'[")
					.append(paramBoardID)
					.append("]가 잘못되었습니다").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (boardID < 0) {
			String errorMessage = "게시판 식별자 값이 음수입니다";
			String debugMessage = new StringBuilder()
					.append("파라미터 '게시판 식별자'[")
					.append(paramBoardID)
					.append("]값이 음수입니다").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (boardID > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = "게시판 식별자 값이 unsigned byte 최대값 보다 큽니다";
			String debugMessage = new StringBuilder()
					.append("파라미터 '게시판 식별자'[")
					.append(paramBoardID)
					.append("]값이 unsigned byte 최대값[")
					.append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX)
					.append("보다 큽니다").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		AccessedUserInformation accessedUserformation = getAccessedUserInformation(req);
		
		BoardInfoDeleteReq boardInfoDeleteReq = new BoardInfoDeleteReq();
		boardInfoDeleteReq.setRequestedUserID(accessedUserformation.getUserID());
		boardInfoDeleteReq.setBoardID(boardID);
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), boardInfoDeleteReq);
		
		
		if (! (outputMessage instanceof MessageResultRes)) {	
			String errorMessage = new StringBuilder().append("게시판 정보[")
					.append(boardID)
					.append("]를 삭제하는데 실패했습니다").toString();
			
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(boardInfoDeleteReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.warn(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		MessageResultRes  messageResultRes = (MessageResultRes)outputMessage;
		if (! messageResultRes.getIsSuccess()) {
			String errorMessage = new StringBuilder().append("게시판 정보[")
					.append(boardID)
					.append("]를 삭제하는데 실패했습니다, 실패사유:")
					.append(messageResultRes.getResultMessage()).toString();
			
			String debugMessage = null;

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		req.setAttribute("boardID", paramBoardID);
		printJspPage(req, res, "/jsp/board/BoardInfoDelete.jsp");
		return;
	}

}
