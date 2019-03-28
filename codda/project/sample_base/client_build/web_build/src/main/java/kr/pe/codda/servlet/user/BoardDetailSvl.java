package kr.pe.codda.servlet.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.BoardDetailReq.BoardDetailReq;
import kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.ValueChecker;
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
		
		AccessedUserInformation accessedUserformation = getAccessedUserInformation(req);
		
		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setRequestedUserID(accessedUserformation.getUserID());
		boardDetailReq.setBoardID(boardID);
		boardDetailReq.setBoardNo(boardNo);
		
		// FIXME!
		//log.info("inObj={}, userId={}, ip={}", inObj.toString(), userId, req.getRemoteAddr());
		
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), boardDetailReq);
		
		if (! (outputMessage instanceof BoardDetailRes)){
			if (outputMessage instanceof MessageResultRes) {
				MessageResultRes messageResultRes = (MessageResultRes)outputMessage;
				String errorMessage = "게시판 상세 조회가 실패하였습니다";
				String debugMessage = messageResultRes.toString();
				printErrorMessagePage(req, res, errorMessage, debugMessage);	
				return;
			} else {
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
		}
		
		
		BoardDetailRes boardDetailRes = (BoardDetailRes)outputMessage;
		req.setAttribute("boardDetailRes", boardDetailRes);		
		
		printJspPage(req, res, "/jsp/community/BoardDetail.jsp");
		
		return;
		 	
	}
	
}
