package kr.pe.codda.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.JavaGarbageCollectionReq.JavaGarbageCollectionReq;
import kr.pe.codda.impl.message.JavaGarbageCollectionRes.JavaGarbageCollectionRes;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractServlet;
import kr.pe.codda.weblib.sitemenu.SiteTopMenuType;

@SuppressWarnings("serial")
public class JavaGarbageCollectionSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_SITE_TOPMENU, 
				SiteTopMenuType.TEST_EXAMPLE);
		
		JavaGarbageCollectionReq javaGarbageCollectionReq = new JavaGarbageCollectionReq();
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();

		
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(javaGarbageCollectionReq);
		if (outputMessage instanceof JavaGarbageCollectionRes) {
			printJspPage(req, res, "/menu/testcode/JavaGarbageCollection01.jsp");
			return;
		} else {
			String errorMessage = "자바 가비지 컬렉터를 호출하는데 실패하였습니다";
			
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(javaGarbageCollectionReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
	}

}
