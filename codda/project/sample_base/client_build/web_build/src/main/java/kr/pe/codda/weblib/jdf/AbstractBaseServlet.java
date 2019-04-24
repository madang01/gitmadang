package kr.pe.codda.weblib.jdf;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;

@SuppressWarnings("serial")
public abstract class AbstractBaseServlet extends HttpServlet {
	protected InternalLogger log = InternalLoggerFactory.getInstance(AbstractBaseServlet.class);
	
	public AccessedUserInformation getAccessedUserInformationFromSession(HttpServletRequest req) {
		HttpSession httpSession = req.getSession();
		
		Object accessedUserformationOfHttpSession = httpSession
				.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USER_INFORMATION);
		
		if (null == accessedUserformationOfHttpSession) {
			return WebCommonStaticFinalVars.GUEST_USER_SESSION_INFORMATION;
		}
				
		AccessedUserInformation  accessedUserformation= (AccessedUserInformation) accessedUserformationOfHttpSession;
		
		return accessedUserformation;
	}
	
	
	/**
	 * <pre>
	 * 서블릿 초기화 파라미터 메뉴그룹(=menuGroupURL) 존재 여부를 반환한다. 
	 * 참고) 서블릿 초기화 파라미터 메뉴그룹(=menuGroupURL)은 @{@link JDFBaseServlet#performBasePreTask} 에서 
	 *      request 의 속성에 메뉴그룹(=@{link {@link WebCommonStaticFinalVars#REQUEST_KEY_NAME_OF_MENU_GROUP_URL})으로 저장된다
	 * </pre>
	 * @param req
	 * @return 서블릿 초기화 파라미터 메뉴그룹(=menuGroupURL) 존재 여부 
	 */
	public boolean isMenuGroupURL(HttpServletRequest req) {
		Object menuGroupURL = req.getAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MENU_GROUP_URL);
		return (null != menuGroupURL);
	}
}
