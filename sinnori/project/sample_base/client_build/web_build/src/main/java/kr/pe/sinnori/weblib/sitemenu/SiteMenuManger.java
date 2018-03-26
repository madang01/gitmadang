package kr.pe.sinnori.weblib.sitemenu;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

public class SiteMenuManger {
	// private Logger log = LoggerFactory.getLogger(SiteMenuManger.class);
	
	private AbstractSiteMenuInfo siteMenuInfo = new SiteMenuInfo();
	
	private CommunityLeftMenuInfo communityLeftMenuInfo = new CommunityLeftMenuInfo();
	private TechDocumentLeftMenuInfo techDocumentLeftMenuInfo = new TechDocumentLeftMenuInfo();
	private MemberLeftMenuInfo memberLeftMenuInfo = new MemberLeftMenuInfo();
	private TestExampleLeftMenuInfo testExampleLeftMenuInfo = new TestExampleLeftMenuInfo();
	private DefulatLeftMenuInfoOfTopMenu defulatLeftMenuInfoOfTopMenu = new DefulatLeftMenuInfoOfTopMenu();
	
	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class SiteMenuMangerHolder {
		static final SiteMenuManger singleton = new SiteMenuManger();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static SiteMenuManger getInstance() {
		return SiteMenuMangerHolder.singleton;
	}
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 * @throws NoMoreDataPacketBufferException 
	 */
	private SiteMenuManger() {
	}
	
	public String getTopMenuPartString(SiteTopMenuType selectedSiteTopMenuType) {
		if (null == selectedSiteTopMenuType) {
			throw new IllegalArgumentException("the parameter selectedSiteTopMenuType is null");
		}
		
		StringBuilder topMenuPartStringBuilder = new StringBuilder();
		for (SiteTopMenuInfo siteTopMenuInfo : getSiteMenuInfo().getSiteTopMenuInfoList()) {
			topMenuPartStringBuilder.append("<li");
			if (selectedSiteTopMenuType.equals(siteTopMenuInfo.getSiteTopMenuType())) {
				topMenuPartStringBuilder.append(" class=\"active\"");
			}
			topMenuPartStringBuilder.append("><a href=\"");
			topMenuPartStringBuilder.append(siteTopMenuInfo.getBodyURL());			
			topMenuPartStringBuilder.append("\">");
			topMenuPartStringBuilder.append(siteTopMenuInfo.getSiteTopMenuName());
			topMenuPartStringBuilder.append("</a></li>");
		}
		
		return topMenuPartStringBuilder.toString();
	}
	
	public String getBodyURL(SiteTopMenuType selectedSiteTopMenuType) {
		if (null == selectedSiteTopMenuType) {
			throw new IllegalArgumentException("the parameter selectedSiteTopMenuType is null");
		}
		
		return getSiteMenuInfo().getSiteTopMenuInfoList().get(selectedSiteTopMenuType.getTopMenuIndex()).getBodyURL();
	}
	
	public String getLeftMenuPartString(SiteTopMenuType selectedSiteTopMenuType, String leftmenu) {
		if (null == selectedSiteTopMenuType) {
			throw new IllegalArgumentException("the parameter selectedSiteTopMenuType is null");
		}
		
		AbstractLeftMenuInfoOfTopMenu leftMenuInfoOfTopMenu = getLeftMenuInfoOfTopMenu(selectedSiteTopMenuType);
		
		return leftMenuInfoOfTopMenu.getLeftMenuPartString(leftmenu);
	}
	
	/** FIXME! 나중에 사이트 메뉴 DB 로 옮긴 경우 로직 구현 필요 */ 
	/* class DBLeftMenuInfoOfTopMenu extends AbstractLeftMenuInfoOfTopMenu {
		private SiteTopMenuType targetSiteTopMenuType = null;
		public DBLeftMenuInfoOfTopMenu(SiteTopMenuType targetSiteTopMenuType) {
			super();
			
			if (null == targetSiteTopMenuType) {
				throw new IllegalArgumentException("the parameter targetSiteTopMenuType is null");
			}
			
			this.targetSiteTopMenuType = targetSiteTopMenuType;
		}
 		
		@Override
		protected void build() {
		}
	}*/
	
	private AbstractSiteMenuInfo getSiteMenuInfo() {
		return siteMenuInfo;
	}
	
	private AbstractLeftMenuInfoOfTopMenu getLeftMenuInfoOfTopMenu(SiteTopMenuType targetSiteTopMenuType) {
		if (null == targetSiteTopMenuType) {
			throw new IllegalArgumentException("the parameter targetSiteTopMenuType is null");
		}
		
		if (targetSiteTopMenuType.equals(SiteTopMenuType.COMMUNITY)) {			
			return communityLeftMenuInfo;
		} else if (targetSiteTopMenuType.equals(SiteTopMenuType.TECH_DOCUMENT)) {			
			return techDocumentLeftMenuInfo;
		} else if (targetSiteTopMenuType.equals(SiteTopMenuType.MEMBER)) {
			return memberLeftMenuInfo;
		} else if (targetSiteTopMenuType.equals(SiteTopMenuType.TEST_EXAMPLE)) {
			return testExampleLeftMenuInfo;
		} else {
			return defulatLeftMenuInfoOfTopMenu;
		}
	}
}
