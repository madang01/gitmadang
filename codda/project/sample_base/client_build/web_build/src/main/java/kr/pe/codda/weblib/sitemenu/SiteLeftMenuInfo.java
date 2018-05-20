package kr.pe.codda.weblib.sitemenu;

public class SiteLeftMenuInfo {
	private int siteLeftMenuIndex;
	private String siteLeftMenuName = null;
	private String siteLeftMenuURL = null;
	
	public SiteLeftMenuInfo(int siteLeftMenuIndex, String siteLeftMenuName, String siteLeftMenuURL) {
		this.siteLeftMenuIndex = siteLeftMenuIndex;
		this.siteLeftMenuName = siteLeftMenuName;
		this.siteLeftMenuURL = siteLeftMenuURL;
	}

	public int getSiteLeftMenuIndex() {
		return siteLeftMenuIndex;
	}

	public String getSiteLeftMenuName() {
		return siteLeftMenuName;
	}

	public String getSiteLeftMenuURL() {
		return siteLeftMenuURL;
	}
}
