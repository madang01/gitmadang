package kr.pe.sinnori.weblib.sitemenu;

public class CommunityLeftMenuInfo extends AbstractLeftMenuInfoOfTopMenu {
	protected void build() {
		/** 0:좌측 메뉴명, 1:주 좌측 메뉴 링크 */
		/*final String[][] leftMenuInfoList = {
			{"자유 게시판", "/servlet/BoardList"}
		};*/

		/** 0:좌측 메뉴키, 1:좌측 메뉴 번호 */
		/*final Object[][] leftMenuLinkInfoList = {
			{"/servlet/BoardList",  0},
			{"/servlet/BoardWrite",  0},
			{"/servlet/BoardDetail",  0},
			{"/servlet/BoardModify",  0},
			{"/servlet/BoardReply",  0},
			{"/servlet/BoardVote",  0},		
		};*/
		
		SiteLeftMenuInfo siteLeftMenuInfo = null;
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("자유 게시판", "/servlet/BoardList");		
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/servlet/BoardList");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/servlet/BoardWrite");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/servlet/BoardDetail");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/servlet/BoardModify");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/servlet/BoardReply");
		// siteLeftMenuGroupList.add(new SiteLeftMenuGroup(siteLeftMenuInfo, "/servlet/BoardVote"));
	}
}
