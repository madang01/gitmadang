package kr.pe.codda.servlet.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.ChildMenuAddReq.ChildMenuAddReq;
import kr.pe.codda.impl.message.ChildMenuAddRes.ChildMenuAddRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class ChildMenuAdditionProcessSvl extends AbstractAdminLoginServlet {

	private static final long serialVersionUID = -4564816006231510832L;

	
	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		/**************** 파라미터 시작 *******************/	
		String paramParentNo = req.getParameter("parentNo");
		String paramMenuName = req.getParameter("menuName");
		String paramLinkURL = req.getParameter("linkURL");
		/**************** 파라미터 종료 *******************/	
		
		if (null == paramParentNo) {
			String errorMessage = "파라미터 '부모메뉴번호'(=parentNo) 값을 넣어주세요";			
			log.warn(errorMessage);
			
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		long nativeParentNo;
		
		try {
			nativeParentNo = Long.parseLong(paramParentNo);
		} catch(NumberFormatException e) {
			String errorMessage = new StringBuilder()
					.append("파라미터 '부모메뉴번호'(=parentNo[")
					.append(paramParentNo)
					.append("])의 값이 long 타입 정수가 아닙니다").toString();
			
			log.warn(errorMessage);

			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (nativeParentNo < 0) {
			String errorMessage = new StringBuilder()
					.append("파라미터 '부모메뉴번호'(=parentNo[")
					.append(paramParentNo)
					.append("])의 값이 음수입니다").toString();
			
			log.warn(errorMessage);
			
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (nativeParentNo > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			String errorMessage = new StringBuilder()
					.append("파라미터 '부모메뉴번호'(=parentNo[")
					.append(paramParentNo)
					.append("])의 값이 최대값[")
					.append(CommonStaticFinalVars.UNSIGNED_INTEGER_MAX)
					.append("] 보다 큽니다").toString();
			
			log.warn(errorMessage);
			
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		if (null == paramMenuName) {
			String errorMessage = "파라미터 '메뉴이름'(=menuName) 값을 넣어주세요";
			
			
			log.warn(errorMessage);

			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		
		if (null == paramLinkURL) {
			String errorMessage = "파라미터 '링크 URL'(=linkURL) 값을 넣어주세요";
			
			log.warn(errorMessage);

			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		AccessedUserInformation accessedUserformation = getAccessedUserInformation(req);
		
		ChildMenuAddReq childMenuAddReq = new ChildMenuAddReq();
		childMenuAddReq.setRequestedUserID(accessedUserformation.getUserID());
		childMenuAddReq.setParentNo(nativeParentNo);
		childMenuAddReq.setMenuName(paramMenuName);
		childMenuAddReq.setLinkURL(paramLinkURL);
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), childMenuAddReq);
		
		if (! (outputMessage instanceof ChildMenuAddRes)) {
			if (outputMessage instanceof MessageResultRes) {
				MessageResultRes messageResultRes = (MessageResultRes)outputMessage;	
				
				String debugMessage = null;
				printErrorMessagePage(req, res, messageResultRes.getResultMessage(), debugMessage);
				return;
			} else {
				String errorMessage = "자식 메뉴 추가하는데 실패하였습니다";
				String debugMessage = new StringBuilder("입력 메시지[")
						.append(childMenuAddReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[")
						.append(outputMessage.toString())
						.append("] 도착").toString();
				
				log.warn(debugMessage);

				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
		}

		req.setAttribute("childMenuAddRes", outputMessage);
		printJspPage(req, res, "/jsp/menu/ChildMenuAdditionProcess.jsp");
	}
}
