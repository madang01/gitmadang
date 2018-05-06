package kr.pe.sinnori.weblib.sitemenu;

public class SiteLeftMenuGroup {
	private SiteLeftMenuInfo siteLeftMenuInfo = null;
	private String siteLeftMenuURL = null;
	
	public SiteLeftMenuGroup(SiteLeftMenuInfo siteLeftMenuInfo, String siteLeftMenuURL) {
		this.siteLeftMenuInfo = siteLeftMenuInfo;
		this.siteLeftMenuURL = siteLeftMenuURL;
	}

	public SiteLeftMenuInfo getSiteLeftMenuInfo() {
		return siteLeftMenuInfo;
	}

	public String getSiteLeftMenuURL() {
		return siteLeftMenuURL;
	}
}
