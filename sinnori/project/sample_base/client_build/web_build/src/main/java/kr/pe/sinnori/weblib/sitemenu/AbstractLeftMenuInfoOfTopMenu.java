package kr.pe.sinnori.weblib.sitemenu;

import java.util.ArrayList;
import java.util.List;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;

public abstract class AbstractLeftMenuInfoOfTopMenu {
	private List<SiteLeftMenuInfo> siteLeftMenuInfoList = new ArrayList<SiteLeftMenuInfo>();
	private List<SiteLeftMenuGroup> siteLeftMenuGroupList = new ArrayList<SiteLeftMenuGroup>();
	
	protected AbstractLeftMenuInfoOfTopMenu() {
		build();
	}
	
	protected void setup(List<SiteLeftMenuInfo> siteLeftMenuInfoList, List<SiteLeftMenuGroup> siteLeftMenuGroupList) {
		this.siteLeftMenuInfoList = siteLeftMenuInfoList;
		this.siteLeftMenuGroupList = siteLeftMenuGroupList;
	}
	
	protected SiteLeftMenuInfo addSiteLeftMenuInfo(String siteLeftMenuName, String siteLeftMenuURL) {
		SiteLeftMenuInfo siteLeftMenuInfo = new SiteLeftMenuInfo(siteLeftMenuInfoList.size(), siteLeftMenuName, siteLeftMenuURL);
		siteLeftMenuInfoList.add(siteLeftMenuInfo);
		return siteLeftMenuInfo;
	}
	
	protected void addSiteLeftMenuGroup(SiteLeftMenuInfo ownerSiteLeftMenuInfo, String siteLeftMenuURL) {
		siteLeftMenuGroupList.add(new SiteLeftMenuGroup(ownerSiteLeftMenuInfo, siteLeftMenuURL));
	}
	
	abstract protected void build();
	
	public List<SiteLeftMenuInfo> getSiteLeftMenuInfoList() {
		return siteLeftMenuInfoList;
	}
	
	public List<SiteLeftMenuGroup> getSiteLeftMenuGroupList() {
		return siteLeftMenuGroupList;
	}	
	
	/**
	 * @param leftmenuURL if leftmenuURL is null then ignore
	 * @return
	 */
	public SiteLeftMenuInfo match(String leftmenuURL) {
		for (SiteLeftMenuGroup siteLeftMenuGroup : siteLeftMenuGroupList) {			
			if (siteLeftMenuGroup.getSiteLeftMenuURL().equals(leftmenuURL)) {
				return siteLeftMenuGroup.getSiteLeftMenuInfo();
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param leftmenuURL if leftmenuURL is null then ignore
	 * @return
	 */
	public String getLeftMenuPartString(String leftmenuURL) {
		if (siteLeftMenuInfoList.size() == 0) {
			return "";
		}
		
		StringBuilder leftMenuPartStringBuilder = new StringBuilder();
		
		SiteLeftMenuInfo targetSiteLeftMenuInfo = match(leftmenuURL);
		
		leftMenuPartStringBuilder.append("<div id=\"sidemenu\">");
		leftMenuPartStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		leftMenuPartStringBuilder.append("<div id=\"smtop\">&nbsp;</div>");
		leftMenuPartStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		leftMenuPartStringBuilder.append("<div id=\"smtitle\"><h1>");
		if (null == targetSiteLeftMenuInfo) {
			leftMenuPartStringBuilder.append("외부 링크");
		} else {
			leftMenuPartStringBuilder.append(targetSiteLeftMenuInfo.getSiteLeftMenuName());
		}	
		
		leftMenuPartStringBuilder.append("</h1></div> <!-- side menu current page title -->");
		leftMenuPartStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		leftMenuPartStringBuilder.append("<ul class=\"normal\">");
		leftMenuPartStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		for (SiteLeftMenuInfo siteLeftMenuInfo : siteLeftMenuInfoList) {
			leftMenuPartStringBuilder.append("<li><a href=\"#\" onClick=\"goURL('");
			leftMenuPartStringBuilder.append(siteLeftMenuInfo.getSiteLeftMenuURL());
			leftMenuPartStringBuilder.append("');\"");
			if (siteLeftMenuInfo.equals(targetSiteLeftMenuInfo)) {
				leftMenuPartStringBuilder.append(" class=\"currentpage\"");
			}
			leftMenuPartStringBuilder.append(">");
			leftMenuPartStringBuilder.append(siteLeftMenuInfo.getSiteLeftMenuName());
			leftMenuPartStringBuilder.append("</a></li>");			
			leftMenuPartStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		}		
		
		leftMenuPartStringBuilder.append("</ul>");
		leftMenuPartStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		leftMenuPartStringBuilder.append("<div id=\"smbottom\">&nbsp;</div>");
		leftMenuPartStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		leftMenuPartStringBuilder.append("</div>");
		
		return leftMenuPartStringBuilder.toString();
	}
}
