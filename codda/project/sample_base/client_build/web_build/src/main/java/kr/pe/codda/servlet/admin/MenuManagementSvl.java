package kr.pe.codda.servlet.admin;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.ArraySiteMenuReq.ArraySiteMenuReq;
import kr.pe.codda.impl.message.ArraySiteMenuRes.ArraySiteMenuRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class MenuManagementSvl extends AbstractAdminLoginServlet {

	private static final long serialVersionUID = -5023286397753637436L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		ArraySiteMenuReq menuListReq = new ArraySiteMenuReq();
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		
		long startTime = 0;
		long endTime = 0;
		startTime = System.nanoTime();
		
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(menuListReq);
		
		endTime = System.nanoTime();
		
		log.info("elapsed={}", TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));
		
		
		if (! (outputMessage instanceof ArraySiteMenuRes) && ! (outputMessage instanceof MessageResultRes)) {
			String errorMessage = "메뉴 목록 조회가 실패했습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(menuListReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		} 
		
		if (outputMessage instanceof MessageResultRes) {
			MessageResultRes messageResultRes = (MessageResultRes)outputMessage;
			String errorMessage = "메뉴 목록 조회가 실패하였습니다";
			String debugMessage = messageResultRes.toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);	
			return;
		}
		
		ArraySiteMenuRes arraySiteMenuRes = (ArraySiteMenuRes)outputMessage;
		
		req.setAttribute("arraySiteMenuRes", arraySiteMenuRes);
		printJspPage(req, res, "/jsp/menu/MenuManagement.jsp");
		return;
	}

}
