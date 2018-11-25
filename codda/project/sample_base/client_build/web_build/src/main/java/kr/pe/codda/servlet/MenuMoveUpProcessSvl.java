package kr.pe.codda.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MenuMoveUpReq.MenuMoveUpReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class MenuMoveUpProcessSvl extends AbstractAdminLoginServlet {

	private static final long serialVersionUID = -2827334751125714227L;
	

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramMenuNo = req.getParameter("menuNo");
		/**************** 파라미터 종료 *******************/
				
		if (null == paramMenuNo) {
			String errorMessage = "파라미터 '메뉴번호'(=menuNo) 값을 넣어주세요";			
			log.warn(errorMessage);
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		long nativeMenuNo;
		
		try {
			nativeMenuNo = Long.parseLong(paramMenuNo);
		} catch(NumberFormatException e) {
			String errorMessage = new StringBuilder()
					.append("파라미터 '메뉴번호'(=menuNo[")
					.append(paramMenuNo)
					.append("])의 값이 long 타입 정수가 아닙니다").toString();
			
			log.warn(errorMessage);

			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (nativeMenuNo < 0) {
			String errorMessage = new StringBuilder()
					.append("파라미터 '메뉴번호'(=menuNo[")
					.append(paramMenuNo)
					.append("])의 값이 음수입니다").toString();
			
			log.warn(errorMessage);

			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (nativeMenuNo > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			String errorMessage = new StringBuilder()
					.append("파라미터 '메뉴번호'(=menuNo[")
					.append(paramMenuNo)
					.append("])의 값이 최대값[")
					.append(CommonStaticFinalVars.UNSIGNED_INTEGER_MAX)
					.append("] 보다 큽니다").toString();
			
			log.warn(errorMessage);
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		MenuMoveUpReq menuMoveUpReq = new MenuMoveUpReq();
		menuMoveUpReq.setMenuNo(nativeMenuNo);
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(menuMoveUpReq);
		
		if (outputMessage instanceof MessageResultRes) {
			MessageResultRes messageResultRes = (MessageResultRes)outputMessage;
			if (! messageResultRes.getIsSuccess()) {
				String debugMessage = null;
				printErrorMessagePage(req, res, messageResultRes.getResultMessage(), debugMessage);
				return;
			}
			
			printJspPage(req, res, "/jsp/menu/MenuMoveUpProcess.jsp");
			return;
		} else {
			String errorMessage = "메뉴 상단 이동이 실패하였습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(menuMoveUpReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.warn(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
	}

}
