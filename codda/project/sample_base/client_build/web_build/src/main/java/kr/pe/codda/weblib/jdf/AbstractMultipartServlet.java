package kr.pe.codda.weblib.jdf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

import kr.pe.codda.weblib.common.AccessedUserInformation;

public abstract class AbstractMultipartServlet extends AbstractServlet {
	
	private static final long serialVersionUID = -6436777887426672536L;

	protected void performPreTask(HttpServletRequest req, HttpServletResponse res) throws Exception  {
		if (! ServletFileUpload.isMultipartContent(req)) {
			String errorMessage = "the request doesn't contain multipart content";
			
			AccessedUserInformation  accessedUserformation = getAccessedUserInformation(req);
			
			String userID = (null == accessedUserformation) ? "guest" : accessedUserformation.getUserID();			
			
			String debugMessage = new StringBuilder(errorMessage).append(", userID=")
					.append(userID)
					.append(", ip=")
					.append(req.getRemoteAddr()).toString();
			log.warn("errorMessage={}, debugMessage={}", errorMessage, debugMessage);		
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);		
			return;
		}
		
		/*
		 * if (! isUserLoginedIn(req)) { String requestURI = req.getRequestURI();
		 * 
		 * ServerSessionkeyIF webServerSessionkey = null; try { ServerSessionkeyManager
		 * serverSessionkeyManager = ServerSessionkeyManager.getInstance();
		 * webServerSessionkey =
		 * serverSessionkeyManager.getMainProjectServerSessionkey(); } catch
		 * (SymmetricException e) {
		 * log.warn("ServerSessionkeyManger instance init error, errormessage=[{}]",
		 * e.getMessage());
		 * 
		 * String errorMessage = "ServerSessionkeyManger instance init error"; String
		 * debugMessage =
		 * String.format("ServerSessionkeyManger instance init error, errormessage=[%s]"
		 * , e.getMessage()); printErrorMessagePage(req, res, errorMessage,
		 * debugMessage); return; }
		 * 
		 * req.setAttribute("successURL", requestURI);
		 * req.setAttribute(WebCommonStaticFinalVars.
		 * REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING,
		 * webServerSessionkey.getModulusHexStrForWeb()); printJspPage(req, res,
		 * JDF_USER_LOGIN_INPUT_PAGE); return; }
		 */
		
		super.performPreTask(req, res);
	}
}
