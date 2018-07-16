package kr.pe.codda.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MenuModifyReq.MenuModifyReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class MenuModifySvl extends AbstractAdminLoginServlet {
	
	private static final long serialVersionUID = -8652788264007358139L;

	private void printErrorMessageCallBackPage(HttpServletRequest req, HttpServletResponse res, String errorMessage) {
		req.setAttribute("errorMessage", errorMessage);
		printJspPage(req, res, "/jsp/menu/errorMessageCallback.jsp");
	}	

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String parmMenuNo = req.getParameter("menuNo");
		if (null == parmMenuNo) {
			String errorMessage = "파라미터 '메뉴번호'(=menuNo) 값을 넣어주세요";			
			log.warn(errorMessage);
			printErrorMessageCallBackPage(req, res, errorMessage);
			return;
		}
		
		long nativeMenuNo;
		
		try {
			nativeMenuNo = Long.parseLong(parmMenuNo);
		} catch(NumberFormatException e) {
			String errorMessage = new StringBuilder()
					.append("파라미터 '메뉴번호'(=menuNo[")
					.append(parmMenuNo)
					.append("])의 값이 long 타입 정수가 아닙니다").toString();
			
			log.warn(errorMessage);

			printErrorMessageCallBackPage(req, res, errorMessage);
			return;
		}
		
		if (nativeMenuNo < 0) {
			String errorMessage = new StringBuilder()
					.append("파라미터 '메뉴번호'(=menuNo[")
					.append(parmMenuNo)
					.append("])의 값이 음수입니다").toString();
			
			log.warn(errorMessage);

			printErrorMessageCallBackPage(req, res, errorMessage);
			return;
		}
		
		if (nativeMenuNo > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			String errorMessage = new StringBuilder()
					.append("파라미터 '메뉴번호'(=menuNo[")
					.append(parmMenuNo)
					.append("])의 값이 최대값[")
					.append(CommonStaticFinalVars.UNSIGNED_INTEGER_MAX)
					.append("] 보다 큽니다").toString();
			
			log.warn(errorMessage);
			printErrorMessageCallBackPage(req, res, errorMessage);
			return;
		}
		
		String parmMenuName = req.getParameter("menuName");
		log.info("the paramter 'menuName'=[{}]", parmMenuName);
		
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
		
		
		MenuModifyReq menuModifyReq = new MenuModifyReq();
		menuModifyReq.setMenuNo(nativeMenuNo);
		menuModifyReq.setMenuName(parmMenuName);
		menuModifyReq.setLinkURL(parmLinkURL);
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(menuModifyReq);
		
		if (outputMessage instanceof MessageResultRes) {
			MessageResultRes messageResultRes = (MessageResultRes)outputMessage;
			if (! messageResultRes.getIsSuccess()) {
				printErrorMessageCallBackPage(req, res, messageResultRes.getResultMessage());
				return;
			}
			
			printJspPage(req, res, "/jsp/menu/modifyMenuOkCallBack.jsp");
			return;
		} else {
			String errorMessage = new StringBuilder().append("메뉴[")
					.append(menuModifyReq.getMenuNo())
					.append("] 수정이 실패했습니다").toString();
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(menuModifyReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.warn(debugMessage);

			printErrorMessageCallBackPage(req, res, errorMessage);
			return;
		}
		
	}
}
