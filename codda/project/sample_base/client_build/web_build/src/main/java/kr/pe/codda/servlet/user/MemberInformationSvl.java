package kr.pe.codda.servlet.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.MemberInformationReq.MemberInformationReq;
import kr.pe.codda.impl.message.MemberInformationRes.MemberInformationRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractServlet;

public class MemberInformationSvl extends AbstractServlet {
	private static final long serialVersionUID = -3958492469145113292L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramTargetUserID = req.getParameter("targetUserID");
		/**************** 파라미터 종료 *******************/
		
		AccessedUserInformation accessedUserInformation = getAccessedUserInformation(req);
		
		String targetUserID = null;
		if (null != paramTargetUserID) {
			
			if (WebCommonStaticFinalVars.GUEST_USER_SESSION_INFORMATION.getUserID().equals(paramTargetUserID)) {
				String errorMessage = "개인 정보 조회 대상으로 손님은 지정할 수 없습니다";
				String debugMessage = null;
				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
			
			targetUserID = paramTargetUserID;
		} else {
			
			if (accessedUserInformation.isLoginedIn()) {
				targetUserID = accessedUserInformation.getUserID();
			}
		}
			
		if (null != targetUserID) {
			MemberInformationReq memberInformationReq = new MemberInformationReq();
			memberInformationReq.setRequestedUserID(accessedUserInformation.getUserID());
			memberInformationReq.setTargetUserID(targetUserID);
			
			AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
			AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), memberInformationReq);
			
			if (!(outputMessage instanceof MemberInformationRes)) {
				if (outputMessage instanceof MessageResultRes) {
					MessageResultRes messageResultRes = (MessageResultRes)outputMessage;
					String errorMessage = "개인 정보 조회가 실패하였습니다";
					String debugMessage = messageResultRes.toString();
					printErrorMessagePage(req, res, errorMessage, debugMessage);	
					return;
				} else {
					String errorMessage = "개인 정보 조회가 실패하였습니다";
					String debugMessage = new StringBuilder("입력 메시지[")
							.append(memberInformationReq.getMessageID())
							.append("]에 대한 비 정상 출력 메시지[")
							.append(outputMessage.toString())
							.append("] 도착").toString();
					
					log.error(debugMessage);

					printErrorMessagePage(req, res, errorMessage, debugMessage);
					return;
				}
			}
			
			MemberInformationRes memberInformationRes = (MemberInformationRes)outputMessage;
			req.setAttribute("memberInformationRes", memberInformationRes);	
		}
		
		
		printJspPage(req, res, "/jsp/my/MemberInformation.jsp");
	}

}
