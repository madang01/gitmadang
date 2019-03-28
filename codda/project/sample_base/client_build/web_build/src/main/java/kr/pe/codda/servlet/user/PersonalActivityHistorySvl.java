package kr.pe.codda.servlet.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.PersonalActivityHistoryReq.PersonalActivityHistoryReq;
import kr.pe.codda.impl.message.PersonalActivityHistoryRes.PersonalActivityHistoryRes;
import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractServlet;

public class PersonalActivityHistorySvl extends AbstractServlet {

	private static final long serialVersionUID = -4459725442983032392L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramTargetUserID = req.getParameter("targetUserID");
		String paramPageNo = req.getParameter("pageNo");
		/**************** 파라미터 종료 *******************/
		
		
		int pageNo = -1;
		try {
			pageNo = ValueChecker.checkValidPageNoAndPageSize(paramPageNo, WebCommonStaticFinalVars.WEBSITE_BOARD_LIST_SIZE_PER_PAGE);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		PersonalActivityHistoryReq personalActivityHistoryReq = new PersonalActivityHistoryReq();
		personalActivityHistoryReq.setRequestedUserID(getAccessedUserInformation(req).getUserID());
		personalActivityHistoryReq.setTargetUserID(paramTargetUserID);
		personalActivityHistoryReq.setPageNo(pageNo);
		personalActivityHistoryReq.setPageSize(WebCommonStaticFinalVars.WEBSITE_BOARD_LIST_SIZE_PER_PAGE);
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), personalActivityHistoryReq);
		
		if (!(outputMessage instanceof PersonalActivityHistoryRes)) {
			if (outputMessage instanceof MessageResultRes) {
				MessageResultRes messageResultRes = (MessageResultRes)outputMessage;
				String errorMessage = "개인 활동 내역 조회가 실패하였습니다";
				String debugMessage = messageResultRes.toString();
				printErrorMessagePage(req, res, errorMessage, debugMessage);	
				return;
			} else {
				String errorMessage = "개인 활동 내역 조회가 실패하였습니다";
				String debugMessage = new StringBuilder("입력 메시지[")
						.append(personalActivityHistoryReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[")
						.append(outputMessage.toString())
						.append("] 도착").toString();
				
				log.error(debugMessage);

				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
		}
		
		PersonalActivityHistoryRes personalActivityHistoryRes = (PersonalActivityHistoryRes)outputMessage;
		req.setAttribute("personalActivityHistoryRes", personalActivityHistoryRes);
		printJspPage(req, res, "/jsp/my/PersonalActivityHistory.jsp");
	}

}
