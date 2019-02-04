package kr.pe.codda.servlet.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.RootMenuAddReq.RootMenuAddReq;
import kr.pe.codda.impl.message.RootMenuAddRes.RootMenuAddRes;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class RootMenuAdditionProcessSvl extends AbstractAdminLoginServlet {
	private static final long serialVersionUID = -2289014989962221383L;
	
	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramMenuName = req.getParameter("menuName");
		String paramLinkURL = req.getParameter("linkURL");
		/**************** 파라미터 종료 *******************/		
		
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
		
		RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
		rootMenuAddReq.setMenuName(paramMenuName);
		rootMenuAddReq.setLinkURL(paramLinkURL);
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), rootMenuAddReq);
		
		if (! (outputMessage instanceof RootMenuAddRes)) {
			if (outputMessage instanceof MessageResultRes) {
				MessageResultRes messageResultRes = (MessageResultRes)outputMessage;
				String debugMessage = null;
				printErrorMessagePage(req, res, messageResultRes.getResultMessage(), debugMessage);
				return;
			} else {
				String errorMessage = "루트 메뉴 추가하는데 실패하였습니다";
				String debugMessage = new StringBuilder("입력 메시지[")
						.append(rootMenuAddReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[")
						.append(outputMessage.toString())
						.append("] 도착").toString();
				
				log.warn(debugMessage);

				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
		}	

		req.setAttribute("rootMenuAddRes", outputMessage);
		printJspPage(req, res, "/jsp/menu/RootMenuAdditionProcess.jsp");
	}
}
