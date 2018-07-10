package kr.pe.codda.weblib.jdf;

import java.util.ArrayList;
import java.util.Map;

public class LoginRequestPageInformation {
	private String requestURI = null;
	private ArrayList<Map.Entry<String, String>> parameterEntryList = null;
	
	public LoginRequestPageInformation(String requestURI, ArrayList<Map.Entry<String, String>> parameterEntryList) {
		if (null == requestURI) {
			throw new IllegalArgumentException("the parameter requestURI is null");
		}
		if (null == parameterEntryList) {
			throw new IllegalArgumentException("the parameter parameterEntryList is null");
		}
		
		this.requestURI = requestURI;
		this.parameterEntryList = parameterEntryList;
		
		/*for (Map.Entry<String, String> parameterEntry : parameterEntryList) {
			parameterEntry.getKey();
			parameterEntry.getValue();
		}*/
	}
	
	public String getRequestURI() {
		return requestURI;
	}
	
	public ArrayList<Map.Entry<String, String>> getParameterEntryList() {
		return parameterEntryList;
	}
}
