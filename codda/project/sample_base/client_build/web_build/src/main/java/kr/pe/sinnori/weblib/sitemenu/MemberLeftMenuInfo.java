package kr.pe.sinnori.weblib.sitemenu;

public class MemberLeftMenuInfo extends AbstractLeftMenuInfoOfTopMenu {
	protected void build() {
		/** 0:좌측 메뉴명, 1:주 좌측 메뉴 링크 */
		/*final String[][] leftMenuInfoList = {
			{"로그인", "/servlet/Login"},
			{"회원 가입", "/servlet/Member"}
		};*/

		/** 0:좌측 메뉴키, 1:좌측 메뉴 번호 */
		/*final Object[][] leftMenuLinkInfoList = {
			{"/servlet/Login",  0},
			{"/servlet/Member",  1}		
		};*/
		
		SiteLeftMenuInfo siteLeftMenuInfo = null;
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("로그인", "/servlet/Login");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/servlet/Login");
		
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("회원 가입", "/servlet/Member");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/servlet/Member");
	}
}
