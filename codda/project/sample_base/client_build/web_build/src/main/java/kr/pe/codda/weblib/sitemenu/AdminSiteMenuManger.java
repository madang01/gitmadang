package kr.pe.codda.weblib.sitemenu;

import java.util.List;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.impl.message.TreeSiteMenuRes.TreeSiteMenuRes;

public class AdminSiteMenuManger {
	// private InternalLogger log = InternalLoggerFactory.getInstance(SiteMenuManger.class);
	
	private TreeSiteMenuRes treeSiteMenuRes = new TreeSiteMenuRes();
	
	
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
		java.util.List<TreeSiteMenuRes.Menu> rootMenuList = new java.util.ArrayList<TreeSiteMenuRes.Menu>();
		{
			TreeSiteMenuRes.Menu menu = new TreeSiteMenuRes.Menu();
			menu.setMenuNo(1);
			menu.setDepth((short)0);
			menu.setMenuName("메뉴 관리");
			menu.setLinkURL("/servlet/MenuManagement");
			menu.setChildMenuListSize(0);
			menu.setChildMenuList(new java.util.ArrayList<TreeSiteMenuRes.Menu>());
			
			rootMenuList.add(menu);
		}
		
		{
			TreeSiteMenuRes.Menu menu = new TreeSiteMenuRes.Menu();
			menu.setMenuNo(2);
			menu.setDepth((short)0);
			menu.setMenuName("사용자 관리");
			menu.setLinkURL("/servlet/UserManagement");
			menu.setChildMenuListSize(0);
			menu.setChildMenuList(new java.util.ArrayList<TreeSiteMenuRes.Menu>());
			
			rootMenuList.add(menu);
		}
		
		{
			TreeSiteMenuRes.Menu menu = new TreeSiteMenuRes.Menu();
			menu.setMenuNo(3);
			menu.setDepth((short)0);
			menu.setMenuName("사용자별 메뉴 권한 설정");
			menu.setLinkURL("/servlet/PagePermissionSetting");
			menu.setChildMenuListSize(0);
			menu.setChildMenuList(new java.util.ArrayList<TreeSiteMenuRes.Menu>());
			
			rootMenuList.add(menu);
		}
		
		treeSiteMenuRes.setRootMenuListSize(rootMenuList.size());
		treeSiteMenuRes.setRootMenuList(rootMenuList);
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
	
	private String getSiteNavbarString(String menuGroupURL, List<TreeSiteMenuRes.Menu> menuList, int tapStep) {
		StringBuilder siteNavbarStringBuilder = new StringBuilder();
		for (TreeSiteMenuRes.Menu menu : menuList) {
			List<TreeSiteMenuRes.Menu> childMenuList = menu.getChildMenuList();
			
			if (null == childMenuList || childMenuList.isEmpty()) {
				siteNavbarStringBuilder.append(getTabStrings(tapStep));
				
				if (menuGroupURL.equals(menu.getLinkURL())) {
					siteNavbarStringBuilder.append("<li class=\"active\">");
				} else {
					siteNavbarStringBuilder.append("<li>");
				}
				
				
				siteNavbarStringBuilder.append("<a href=\"");
				siteNavbarStringBuilder.append(menu.getLinkURL());
				siteNavbarStringBuilder.append("\">");
				siteNavbarStringBuilder.append(menu.getMenuName());
				siteNavbarStringBuilder.append("</a></li>");
				siteNavbarStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			} else {
				siteNavbarStringBuilder.append(getTabStrings(tapStep));
				siteNavbarStringBuilder.append("<li class=\"dropdown\">");
				siteNavbarStringBuilder.append(CommonStaticFinalVars.NEWLINE);
				siteNavbarStringBuilder.append(getTabStrings(tapStep+1));
				siteNavbarStringBuilder.append("<a class=\"dropdown-toggle\" data-toggle=\"dropdown\" href=\"");
				siteNavbarStringBuilder.append(menu.getLinkURL());
				siteNavbarStringBuilder.append("\">");
				siteNavbarStringBuilder.append(menu.getMenuName());
				siteNavbarStringBuilder.append("<span class=\"caret\"></span></a>");
				siteNavbarStringBuilder.append(CommonStaticFinalVars.NEWLINE);
				
				siteNavbarStringBuilder.append(getTabStrings(tapStep+1));
				siteNavbarStringBuilder.append("<ul class=\"dropdown-menu\">");
				siteNavbarStringBuilder.append(CommonStaticFinalVars.NEWLINE);
				
				siteNavbarStringBuilder.append(getSiteNavbarString(menuGroupURL, childMenuList, tapStep+2));
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
				
		siteNavbarStringBuilder.append(getSiteNavbarString(menuGroupURL, treeSiteMenuRes.getRootMenuList(), tapStep+4))				
				.append(getTabStrings(tapStep+3))
				.append("</ul>")				
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep+3))
				.append("<ul class=\"nav navbar-nav navbar-right\">")				
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep+4))
				.append("<li><a href=\"")
				.append("/servlet/AdminMemberRegistration")
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
