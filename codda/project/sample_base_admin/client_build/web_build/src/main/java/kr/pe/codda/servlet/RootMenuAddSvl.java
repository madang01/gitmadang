package kr.pe.codda.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.RootMenuAddReq.RootMenuAddReq;
import kr.pe.codda.impl.message.RootMenuAddRes.RootMenuAddRes;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class RootMenuAddSvl extends AbstractAdminLoginServlet {

	private static final long serialVersionUID = -2289014989962221383L;

	private void printErrorMessageCallBackPage(HttpServletRequest req, HttpServletResponse res, String errorMessage) {
		req.setAttribute("errorMessage", errorMessage);
		printJspPage(req, res, "/jsp/menu/errorMessageCallback.jsp");
	}
	
	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
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
		
		RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
		rootMenuAddReq.setMenuName(parmMenuName);
		rootMenuAddReq.setLinkURL(parmLinkURL);
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(rootMenuAddReq);
		
		if (outputMessage instanceof RootMenuAddRes) {
			req.setAttribute("rootMenuAddRes", outputMessage);
			printJspPage(req, res, "/jsp/menu/addRootMenuOkCallBack.jsp");
		} else if (outputMessage instanceof MessageResultRes) {
			MessageResultRes messageResultRes = (MessageResultRes)outputMessage;			
			printErrorMessageCallBackPage(req, res, messageResultRes.getResultMessage());	
			return;
		} else {
			String errorMessage = "루트 메뉴 추가하는데 실패하였습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(rootMenuAddReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.warn(debugMessage);

			printErrorMessageCallBackPage(req, res, errorMessage);
			return;
		}
		
	}

}
