package kr.pe.codda.weblib.sitemenu;

import java.util.HashMap;

public class SiteURLInfo {
	private String baseURI = null;
	private HashMap<String, String> parameterHash = new HashMap<String, String>();
	
	public SiteURLInfo(String baseURI) {
		if (null == baseURI) {
			throw new IllegalArgumentException("the paramter baseURI is null");
		}
		
		this.baseURI = baseURI;
	}
	
	public void addParameter(String key, String value) {
		if (null == key) {
			throw new IllegalArgumentException("the paramter key is null");
		}
		
		if (null == value) {
			throw new IllegalArgumentException("the paramter value is null");
		}
		
		parameterHash.put(key, value);
	}
	
	/*public boolean isSiteURL(HttpServletRequest req) {
		if (null == req) {
			throw new IllegalArgumentException("the parameter req is null");
		}
		
		String requestURI = req.getRequestURI();
		
		if (! baseURI.equals(requestURI)) {
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
		if (parameterHash.isEmpty()) {
			return baseURI;
		}
		
		StringBuilder urlStringBuilder = new StringBuilder(baseURI);
		
		for(String parameterKey : parameterHash.keySet()) {
			String parameterValue = parameterHash.get(parameterKey);
			
			if (urlStringBuilder.length() == baseURI.length()) {
				urlStringBuilder.append("?");
			} else {
				urlStringBuilder.append("&");
			}			
						
			urlStringBuilder.append(parameterKey);
			urlStringBuilder.append("=");
			urlStringBuilder.append(parameterValue);
		}
		
		return urlStringBuilder.toString();
	}
	
	public boolean equals(SiteURLInfo targetSiteURLInfo) {
		if (null == targetSiteURLInfo) {
			return false;
		}
		
		if (! targetSiteURLInfo.baseURI.equals(baseURI)) {
			return false;
		}
		
		for(String parameterKey : parameterHash.keySet()) {
			String expectedParameterValue = parameterHash.get(parameterKey);
			
			String acutalParameterValue = targetSiteURLInfo.parameterHash.get(parameterKey);
			
			if (! expectedParameterValue.equals(acutalParameterValue)) {
				return false;
			}
		}		
		
		return true;
	}
}
