package kr.pe.sinnori.weblib.sitemenu;

public class TestExampleLeftMenuInfo extends AbstractLeftMenuInfoOfTopMenu {
	protected void build() {
		int topmenu = SiteTopMenuType.TEST_EXAMPLE.getTopMenuIndex();
		
		/** 0:좌측 메뉴명, 1:주 좌측 메뉴 링크 */
		/*final String[][] leftMenuInfoList = {
			{"JDF-비 로그인 테스트", "/servlet/JDFNotLoginTest"},
			{"JDF-로그인 테스트", "/servlet/JDFLoginTest?topmenu="+topmenu},
			{"JDF-세션키 테스트", "/servlet/SessionKeyTest"},
			{"RSA 암/복호화 테스트", "/servlet/JSBNTest"},
			{"메세지 다이제스트(MD) 테스트", "/servlet/CryptoJSMDTest"},
			{"대칭키 테스트", "/servlet/CryptoJSSKTest"},
			{"에코 테스트", "/servlet/EchoTest"},
			{"모든 데이터 타입 검증", "/servlet/AllItemTypeTest"},
			{"동적 클래스 호출 실패", "#"},
		};*/

		/** 0:좌측 메뉴키, 1:좌측 메뉴 번호 */
		/*final Object[][] leftMenuLinkInfoList = {
			{"/servlet/JDFNotLoginTest",  0},
			{"/servlet/JDFLoginTest",  1},
			{"/servlet/SessionKeyTest",  2},
			{"/servlet/JSBNTest",  3},
			{"/servlet/CryptoJSMDTest",  4},
			{"/servlet/CryptoJSSKTest",  5},
			{"/servlet/EchoTest",  6},
			{"/servlet/AllItemTypeTest",  7},
		};*/
		
		SiteLeftMenuInfo siteLeftMenuInfo = null;
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("JDF-비 로그인 테스트", "/servlet/JDFNotLoginTest");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/servlet/JDFNotLoginTest");
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("JDF-로그인 테스트", "/servlet/JDFLoginTest?topmenu="+topmenu);
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/servlet/JDFLoginTest");
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("JDF-세션키 테스트", "/servlet/SessionKeyTest");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/servlet/SessionKeyTest");
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("RSA 암/복호화 테스트", "/servlet/JSBNTest");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/servlet/JSBNTest");
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("메세지 다이제스트(MD) 테스트", "/servlet/CryptoJSMDTest");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/servlet/CryptoJSMDTest");
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("대칭키 테스트", "/servlet/CryptoJSSKTest");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/servlet/CryptoJSSKTest");
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("에코 테스트", "/servlet/EchoTest");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/servlet/EchoTest");
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("모든 데이터 타입 검증", "/servlet/AllItemTypeTest");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/servlet/AllItemTypeTest");		
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("Java GC", "/servlet/JavaGarbageCollection");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/servlet/JavaGarbageCollection");
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("자바 문자열 변환 도구", "/servlet/JavaStringConversionTool");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/servlet/JavaStringConversionTool");
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("동적 클래스 호출 실패", "#");
	}
}
