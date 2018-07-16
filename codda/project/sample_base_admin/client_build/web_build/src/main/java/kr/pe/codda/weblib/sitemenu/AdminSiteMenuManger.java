package kr.pe.codda.weblib.sitemenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

public class AdminSiteMenuManger {
	// private InternalLogger log = InternalLoggerFactory.getInstance(SiteMenuManger.class);
	
	private List<SiteMenuInfo > topSiteMenuInfoList =	new ArrayList<SiteMenuInfo>();
	
	
	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class SiteMenuMangerHolder {
		static final AdminSiteMenuManger singleton = new AdminSiteMenuManger();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static AdminSiteMenuManger getInstance() {
		return SiteMenuMangerHolder.singleton;
	}
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 * @throws NoMoreDataPacketBufferException 
	 */
	private AdminSiteMenuManger() {
		topSiteMenuInfoList.add(new SiteMenuInfo(1, "메뉴 관리", "/servlet/MenuManagement"));
		topSiteMenuInfoList.add(new SiteMenuInfo(2, "사용자 관리", "/servlet/UserManagement"));
		topSiteMenuInfoList.add(new SiteMenuInfo(3, "사용자별 메뉴 권한 설정", "/servlet/PagePermissionSetting"));
	}
	
	public  final List<SiteMenuInfo > getTopSiteMenuInfoList() {
		return Collections.unmodifiableList(topSiteMenuInfoList);
	}
	
	/*public SiteMenuInfo getSiteMenuInfo(HttpServletRequest req) {
		for (SiteMenuInfo siteMenuInfo : topSiteMenuInfoList) {
			
			SiteMenuInfo returnedSiteMenuInfo = siteMenuInfo.getSiteMenuInfo(req);
			
			if (null != returnedSiteMenuInfo) {
				return returnedSiteMenuInfo;
			}
		}
		return null;
	}
	
	public SiteMenuInfo getSiteMenuInfo(SiteURLInfo targetSiteURLInfo) {
		for (SiteMenuInfo siteMenuInfo : topSiteMenuInfoList) {
			
			SiteMenuInfo returnedSiteMenuInfo = siteMenuInfo.getSiteMenuInfo(targetSiteURLInfo);
			
			if (null != returnedSiteMenuInfo) {
				return returnedSiteMenuInfo;
			}
		}
		return null;
	}*/
	
	private String getTabStrings(int tapStep) {
		StringBuilder tapStringBuilder = new StringBuilder();
		for (int i=0; i < tapStep; i++) {
			tapStringBuilder.append("\t");
		}
		return tapStringBuilder.toString();
	}
	
	private String getSiteNavbarString(String url, List<SiteMenuInfo> siteMenuInfoList, int tapStep) {
		StringBuilder siteNavbarStringBuilder = new StringBuilder();
		for (SiteMenuInfo siteMenuInfo : siteMenuInfoList) {
			List<SiteMenuInfo> childSiteMenuInfoList =  siteMenuInfo.getChildSiteMenuInfoList();
			if (childSiteMenuInfoList.isEmpty()) {
				siteNavbarStringBuilder.append(getTabStrings(tapStep));
				
				if (url.equals(siteMenuInfo.getSiteURL())) {
					siteNavbarStringBuilder.append("<li class=\"active\">");
				} else {
					siteNavbarStringBuilder.append("<li>");
				}
				
				
				siteNavbarStringBuilder.append("<a href=\"");
				siteNavbarStringBuilder.append(siteMenuInfo.getSiteURL());
				siteNavbarStringBuilder.append("\">");
				siteNavbarStringBuilder.append(siteMenuInfo.getSiteMenuName());
				siteNavbarStringBuilder.append("</a></li>");
				siteNavbarStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			} else {
				siteNavbarStringBuilder.append(getTabStrings(tapStep));
				siteNavbarStringBuilder.append("<li class=\"dropdown\">");
				siteNavbarStringBuilder.append(CommonStaticFinalVars.NEWLINE);
				siteNavbarStringBuilder.append(getTabStrings(tapStep+1));
				siteNavbarStringBuilder.append("<a class=\"dropdown-toggle\" data-toggle=\"dropdown\" href=\"");
				siteNavbarStringBuilder.append(siteMenuInfo.getSiteURL());
				siteNavbarStringBuilder.append("\">");
				siteNavbarStringBuilder.append(siteMenuInfo.getSiteMenuName());
				siteNavbarStringBuilder.append("<span class=\"caret\"></span></a>");
				siteNavbarStringBuilder.append(CommonStaticFinalVars.NEWLINE);
				
				siteNavbarStringBuilder.append(getTabStrings(tapStep+1));
				siteNavbarStringBuilder.append("<ul class=\"dropdown-menu\">");
				siteNavbarStringBuilder.append(CommonStaticFinalVars.NEWLINE);
				
				siteNavbarStringBuilder.append(getSiteNavbarString(url, childSiteMenuInfoList, tapStep+2));
				siteNavbarStringBuilder.append(CommonStaticFinalVars.NEWLINE);
				
				siteNavbarStringBuilder.append(getTabStrings(tapStep+1));
				siteNavbarStringBuilder.append("</ul>");
				siteNavbarStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			}
		}
		return siteNavbarStringBuilder.toString();
	}
	
	public String getSiteNavbarString(String menuGroupURL, boolean isLogin) {
		if (null == menuGroupURL) {
			menuGroupURL = "/";
		}
		
		final int tapStep = 1;
		StringBuilder siteNavbarStringBuilder = new StringBuilder()
				.append(getTabStrings(tapStep))
				.append("<nav class=\"navbar navbar-default\">")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep+1))
				.append("<div class=\"container-fluid\">")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep+2))
				.append("<div class=\"navbar-header\">")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep+3))
				.append("<button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\"#myNavbar\">")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep+4))
				.append("<span class=\"icon-bar\"></span>")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep+4))
				.append("<span class=\"icon-bar\"></span>")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep+4))
				.append("<span class=\"icon-bar\"></span>")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep+3))
				.append("</button>")				
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep+3))
				.append("<a class=\"navbar-brand\" href=\"/\">Codda</a>")				
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep+2))
				.append("</div>")				
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep+2))
				.append("<div class=\"collapse navbar-collapse\" id=\"myNavbar\">")				
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep+3))
				.append("<ul class=\"nav navbar-nav\">")				
				.append(CommonStaticFinalVars.NEWLINE);
		
		
		if (menuGroupURL.equals("/")) {
			siteNavbarStringBuilder.append(getTabStrings(tapStep+4))
			.append("<li class=\"active\"><a href=\"/\">Home</a></li>")				
			.append(CommonStaticFinalVars.NEWLINE);
		} else {
			siteNavbarStringBuilder.append(getTabStrings(tapStep+4))
			.append("<li><a href=\"/\">Home</a></li>")				
			.append(CommonStaticFinalVars.NEWLINE);
		}		
				
		siteNavbarStringBuilder.append(getSiteNavbarString(menuGroupURL, topSiteMenuInfoList, tapStep+4))				
				.append(getTabStrings(tapStep+3))
				.append("</ul>")				
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep+3))
				.append("<ul class=\"nav navbar-nav navbar-right\">")				
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep+4))
				.append("<li><a href=\"")
				.append("/servlet/Member")
				.append("\"><span class=\"glyphicon glyphicon-user\"></span> Sign Up</a></li>")
				.append(CommonStaticFinalVars.NEWLINE);				
		
		if (isLogin) {			
			siteNavbarStringBuilder.append(getTabStrings(tapStep+4))
			.append("<li><a href=\"")
			.append("/jsp/member/logout.jsp")
			.append("\"><span class=\"glyphicon glyphicon-log-out\"></span> logout</a></li>")
			.append(CommonStaticFinalVars.NEWLINE);
		} else {
			siteNavbarStringBuilder.append(getTabStrings(tapStep+4))
			.append("<li><a href=\"")
			.append("/servlet/AdminLogin")
			.append("\"><span class=\"glyphicon glyphicon-log-in\"></span> login</a></li>")
			.append(CommonStaticFinalVars.NEWLINE);
		}
		
		siteNavbarStringBuilder.append(getTabStrings(tapStep+3))
		.append("</ul>")				
		.append(CommonStaticFinalVars.NEWLINE)
		.append(getTabStrings(tapStep+2))
		.append("</div>")				
		.append(CommonStaticFinalVars.NEWLINE)
		.append(getTabStrings(tapStep+1))
		.append("</div>")				
		.append(CommonStaticFinalVars.NEWLINE)
		.append(getTabStrings(tapStep))
		.append("</nav>")				
		.append(CommonStaticFinalVars.NEWLINE);
		
		return siteNavbarStringBuilder.toString();
	}
}
