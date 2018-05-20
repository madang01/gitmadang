package kr.pe.codda.weblib.sitemenu;

public enum SiteTopMenuType {
	INTRODUCE(0), GETTING_STARTED(1), DOWNLOAD(2), COMMUNITY(3), TECH_DOCUMENT(4), MEMBER(5), TEST_EXAMPLE(6);
	
	private int topMenuIndex;
	private SiteTopMenuType(int topMenuIndex) {
		this.topMenuIndex = topMenuIndex;
	}
	
	public int getTopMenuIndex() {
		return topMenuIndex;
	}
	
	/**
	 * @param targetTopMenuIndex
	 * @return 일치하는 사이트 메뉴 타입을 반환한다. 단 불일치시 디폴트 값을 반환한다.
	 */
	public static SiteTopMenuType match(int targetTopMenuIndex) {			
		SiteTopMenuType[] siteTopMenuTypes = SiteTopMenuType.values();
		for (SiteTopMenuType siteTopMenuType : siteTopMenuTypes) {
			if (siteTopMenuType.getTopMenuIndex() == targetTopMenuIndex) {
				return siteTopMenuType;
			}
		}
		
		return SiteTopMenuType.INTRODUCE;
	}
}
