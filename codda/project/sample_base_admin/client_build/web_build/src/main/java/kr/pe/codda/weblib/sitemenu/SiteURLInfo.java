package kr.pe.codda.weblib.sitemenu;

public class SiteURLInfo {
	private String siteURL = null;
	
	/*private String siteURI;
	private HashMap<String, String> parameterHash = new HashMap<String, String>();*/
	
	
	public SiteURLInfo(String siteURL) {
		if (null == siteURL) {
			throw new IllegalArgumentException("the paramter siteURL is null");
		}
		
		this.siteURL = siteURL;
		
		
		/*int inxOfQuerySeparator = siteURL.indexOf('?');
		
		if (inxOfQuerySeparator < 0) {
			siteURI = siteURL;
		} else {
			siteURI = siteURL.substring(0, inxOfQuerySeparator);
			
			String queryString = null;
			try {
				queryString = siteURL.substring(inxOfQuerySeparator+1);
			} catch(IndexOutOfBoundsException e) {
				return;
			}
			
			String parameterStrings[] = queryString.split("&");
			
			for (String parameterString : parameterStrings) {
				String parameter[] = parameterString.split("=");
				if (parameter.length != 2) {
					String errorMessage = new StringBuilder()
							.append("the paramter siteURL[")
							.append(siteURL)
							.append("] has a bad parameter[")
							.append(parameterString)
							.append("]").toString();
					throw new IllegalArgumentException(errorMessage);
				}
				
				parameterHash.put(parameter[0], parameter[1]);
			}
		}*/
	}
	
	
	/*public boolean isSiteURL(HttpServletRequest req) {
		if (null == req) {
			throw new IllegalArgumentException("the parameter req is null");
		}
		
		String requestURI = req.getRequestURI();
		
		if (! siteURI.equals(requestURI)) {
			return false;
		}
		
		for(String parameterKey : parameterHash.keySet()) {
			String expectedValue = parameterHash.get(parameterKey);
			String acutalValue = req.getParameter(parameterKey);
			
			if (! expectedValue.equals(acutalValue)) {
				return false;
			}
		}		
		
		return true;
	}*/
	
	
	public String getURL() {
		
		return siteURL;
	}
	
	/*public String getSiteURI() {
		return siteURI;
	}
	
	public HashMap<String, String> getParameterHash() {
		return parameterHash;
	}*/
	
	public boolean equals(SiteURLInfo targetSiteURLInfo) {
		if (null == targetSiteURLInfo) {
			return false;
		}
		
		if (! targetSiteURLInfo.siteURL.equals(siteURL)) {
			return false;
		}		
		
		return true;
	}
	
	  
}
