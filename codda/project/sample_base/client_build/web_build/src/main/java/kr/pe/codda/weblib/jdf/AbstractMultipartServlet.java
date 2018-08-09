package kr.pe.codda.weblib.jdf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

public abstract class AbstractMultipartServlet extends AbstractLoginServlet {
	
	private static final long serialVersionUID = -6436777887426672536L;

	protected void performPreTask(HttpServletRequest req, HttpServletResponse res) throws Exception  {
		if (! ServletFileUpload.isMultipartContent(req)) {
			String errorMessage = "the request doesn't contain multipart content";
			String debugMessage = new StringBuilder(errorMessage).append(", userID=")
					.append(getLoginedUserIDFromHttpSession(req)).toString();
			log.warn("{}, ip=", debugMessage, req.getRemoteAddr());		
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);		
			return;
		} 
		
		super.performPreTask(req, res);
	}
}
