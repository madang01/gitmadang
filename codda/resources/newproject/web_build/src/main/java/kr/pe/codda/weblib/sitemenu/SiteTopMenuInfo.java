package kr.pe.codda.weblib.sitemenu;

public class SiteTopMenuInfo {
	private SiteTopMenuType siteTopMenuType = null;
	private String siteTopMenuName = null;
	private String bodyURL = null;
	
	public SiteTopMenuInfo(SiteTopMenuType siteTopMenuType, String siteTopMenuName, String bodyURL) {
		this.siteTopMenuType = siteTopMenuType;
		this.siteTopMenuName = siteTopMenuName;
		this.bodyURL = bodyURL;
	}
	
	public SiteTopMenuType getSiteTopMenuType() {
		return siteTopMenuType;
	}
	
	public String getSiteTopMenuName() {
		return siteTopMenuName;
	}
	
	public String getBodyURL() {
		return bodyURL;
	}
}
