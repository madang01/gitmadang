package kr.pe.codda.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.ChildMenuAddReq.ChildMenuAddReq;
import kr.pe.codda.impl.message.ChildMenuAddRes.ChildMenuAddRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class ChildMenuAddSvl extends AbstractAdminLoginServlet {

	private static final long serialVersionUID = -4564816006231510832L;

	private void printErrorMessageCallBackPage(HttpServletRequest req, HttpServletResponse res, String errorMessage) {
		req.setAttribute("errorMessage", errorMessage);
		printJspPage(req, res, "/jsp/menu/errorMessageCallback.jsp");
	}
	
	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String parmParentNo = req.getParameter("parentNo");
		if (null == parmParentNo) {
			String errorMessage = "파라미터 '부모메뉴번호'(=parentNo) 값을 넣어주세요";			
			log.warn(errorMessage);
			printErrorMessageCallBackPage(req, res, errorMessage);
			return;
		}
		
		long nativeParentNo;
		
		try {
			nativeParentNo = Long.parseLong(parmParentNo);
		} catch(NumberFormatException e) {
			String errorMessage = new StringBuilder()
					.append("파라미터 '부모메뉴번호'(=parentNo[")
					.append(parmParentNo)
					.append("])의 값이 long 타입 정수가 아닙니다").toString();
			
			log.warn(errorMessage);

			printErrorMessageCallBackPage(req, res, errorMessage);
			return;
		}
		
		if (nativeParentNo < 0) {
			String errorMessage = new StringBuilder()
					.append("파라미터 '부모메뉴번호'(=parentNo[")
					.append(parmParentNo)
					.append("])의 값이 음수입니다").toString();
			
			log.warn(errorMessage);

			printErrorMessageCallBackPage(req, res, errorMessage);
			return;
		}
		
		if (nativeParentNo > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			String errorMessage = new StringBuilder()
					.append("파라미터 '부모메뉴번호'(=parentNo[")
					.append(parmParentNo)
					.append("])의 값이 최대값[")
					.append(CommonStaticFinalVars.UNSIGNED_INTEGER_MAX)
					.append("] 보다 큽니다").toString();
			
			log.warn(errorMessage);
			printErrorMessageCallBackPage(req, res, errorMessage);
			return;
		}
		
		String parmMenuName = req.getParameter("menuName");
		if (null == parmMenuName) {
			String errorMessage = "파라미터 '메뉴이름'(=menuName) 값을 넣어주세요";
			
			
			log.warn(errorMessage);

			printErrorMessageCallBackPage(req, res, errorMessage);
			return;
		}
		
		String parmLinkURL = req.getParameter("linkURL");
		
		if (null == parmLinkURL) {
			String errorMessage = "파라미터 '링크 URL'(=linkURL) 값을 넣어주세요";
			
			log.warn(errorMessage);

			printErrorMessageCallBackPage(req, res, errorMessage);
			return;
		}
		
		ChildMenuAddReq childMenuAddReq = new ChildMenuAddReq();
		childMenuAddReq.setParentNo(nativeParentNo);
		childMenuAddReq.setMenuName(parmMenuName);
		childMenuAddReq.setLinkURL(parmLinkURL);
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(childMenuAddReq);
		
		if (outputMessage instanceof ChildMenuAddRes) {
			req.setAttribute("childMenuAddRes", outputMessage);
			printJspPage(req, res, "/jsp/menu/addChildMenuOkCallBack.jsp");
		} else if (outputMessage instanceof MessageResultRes) {
			MessageResultRes messageResultRes = (MessageResultRes)outputMessage;			
			printErrorMessageCallBackPage(req, res, messageResultRes.getResultMessage());	
			return;
		} else {
			String errorMessage = "자식 메뉴 추가하는데 실패하였습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(childMenuAddReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.warn(debugMessage);

			printErrorMessageCallBackPage(req, res, errorMessage);
			return;
		}
		
	}

}
