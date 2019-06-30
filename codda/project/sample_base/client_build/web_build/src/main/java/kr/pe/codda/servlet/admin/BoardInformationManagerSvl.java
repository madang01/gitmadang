package kr.pe.codda.servlet.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.BoardInfoListReq.BoardInfoListReq;
import kr.pe.codda.impl.message.BoardInfoListRes.BoardInfoListRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class BoardInformationManagerSvl extends AbstractAdminLoginServlet {


	private static final long serialVersionUID = -5709878115263211587L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		BoardInfoListReq boardInfoListReq = new BoardInfoListReq();
		
		AccessedUserInformation accessedUserformation = getAccessedUserInformationFromSession(req);		
		boardInfoListReq.setRequestedUserID(accessedUserformation.getUserID());
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), boardInfoListReq);
		
		if (! (outputMessage instanceof BoardInfoListRes)) {	
			if ((outputMessage instanceof MessageResultRes)) {			
				MessageResultRes  messageResultRes = (MessageResultRes)outputMessage;
				String errorMessage = new StringBuilder().append("게시판 정보 목록을 가져오는데 실패했습니다, 실패사유:")
						.append(messageResultRes.getResultMessage()).toString();
				
				String debugMessage = null;

				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			} else {
				String errorMessage = "게시판 정보 목로을 가져오는데 실패했습니다";
				
				String debugMessage = new StringBuilder("입력 메시지[")
						.append(boardInfoListReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[")
						.append(outputMessage.toString())
						.append("] 도착").toString();
				
				log.warn(debugMessage);

				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
		}
		
		BoardInfoListRes boardInfoListRes = (BoardInfoListRes)outputMessage;
		
		req.setAttribute("boardInfoListRes", boardInfoListRes);
		printJspPage(req, res, "/jsp/board/BoardInformationManager.jsp");
		return;
	}
}
