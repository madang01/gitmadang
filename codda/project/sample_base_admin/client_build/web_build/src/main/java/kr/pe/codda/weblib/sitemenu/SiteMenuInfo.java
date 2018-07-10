package kr.pe.codda.weblib.sitemenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SiteMenuInfo {
	private long siteMenuNo;
	private String siteMenuName = null;
	private String siteURL = null;
	
	private final List<SiteMenuInfo> childSiteMenuInfoList =	new ArrayList<SiteMenuInfo>();
	
	
	public SiteMenuInfo(long siteMenuNo, String siteMenuName, String siteURL) {
		if (null == siteMenuName) {
			throw new IllegalArgumentException("the paramter siteMenuName is null");
		}
		
		if (null == siteURL) {
			throw new IllegalArgumentException("the paramter siteURL is null");
		}		
		
		this.siteMenuNo = siteMenuNo;
		this.siteMenuName= siteMenuName;
		this.siteURL = siteURL;
	}
	
	public Long getSiteMenuNo() {
		return siteMenuNo;
	}
	
	public String getSiteMenuName() {
		return siteMenuName;
	}
	
	public String getSiteURL() {
		return siteURL;
	}
	
	
	public void addChildSiteMenu(SiteMenuInfo childSiteMenuInfo) {
		childSiteMenuInfoList.add(childSiteMenuInfo);
	}
	
	public List<SiteMenuInfo> getChildSiteMenuInfoList() {
		return Collections.unmodifiableList(childSiteMenuInfoList);
	}
	
	
	/*public SiteMenuInfo getSiteMenuInfo(HttpServletRequest req) {
		if (null == req) {
			throw new IllegalArgumentException("the parameter req is null");
		}
		
		if (siteURLInfo.isSiteURL(req)) {
			return this;
		}
		
		for (SiteMenuInfo childSiteMenuInfo : childSiteMenuInfoList) {
			SiteMenuInfo siteMenuInfo = childSiteMenuInfo.getSiteMenuInfo(req);
			
			if (null != siteMenuInfo) {
				return siteMenuInfo;
			}
		}
		
		return null;
	}
	
	public SiteMenuInfo getSiteMenuInfo(SiteURLInfo targetSiteURLInfo) {
		if (null == targetSiteURLInfo) {
			throw new IllegalArgumentException("the parameter targetSiteURLInfo is null");
		}
		
		if (siteURLInfo.equals(targetSiteURLInfo)) {
			return this;
		}
		
		for (SiteMenuInfo childSiteMenuInfo : childSiteMenuInfoList) {
			if (childSiteMenuInfo.equals(targetSiteURLInfo)) {
				return childSiteMenuInfo;
			}
		}
		
		return null;
	}*/
}
