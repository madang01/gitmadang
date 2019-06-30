package kr.pe.codda.servlet.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.MemberBlockReq.MemberBlockReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class MemberBlockProcessSvl extends AbstractAdminLoginServlet {

	private static final long serialVersionUID = -6931121678953775065L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		/**************** 파라미터 시작 *******************/
		String paramTargetUserID = req.getParameter("targetUserID");
		/**************** 파라미터 종료 *******************/
		
		try {
			ValueChecker.checkValidBlockUserID(paramTargetUserID);
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String debugMessage = null;

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		MemberBlockReq memberBlockReq = new MemberBlockReq();
		memberBlockReq.setRequestedUserID(getAccessedUserInformationFromSession(req).getUserID());
		memberBlockReq.setIp(req.getRemoteAddr());
		memberBlockReq.setTargetUserID(paramTargetUserID);
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), memberBlockReq);
		
		if (!(outputMessage instanceof MessageResultRes)) {
			if (outputMessage instanceof MessageResultRes) {
				MessageResultRes messageResultRes = (MessageResultRes)outputMessage;
				String errorMessage = "회원 차단이 실패하였습니다";
				String debugMessage = messageResultRes.toString();
				printErrorMessagePage(req, res, errorMessage, debugMessage);	
				return;
			} else {
				String errorMessage = "회원 차단이 실패하였습니다";
				String debugMessage = new StringBuilder("입력 메시지[")
						.append(memberBlockReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[")
						.append(outputMessage.toString())
						.append("] 도착").toString();
				
				log.error(debugMessage);

				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
		} 
		
		
		req.setAttribute("targetUserID", paramTargetUserID);
		printJspPage(req, res, "/jsp/member/MemberBlockProcess.jsp");
	}

}
