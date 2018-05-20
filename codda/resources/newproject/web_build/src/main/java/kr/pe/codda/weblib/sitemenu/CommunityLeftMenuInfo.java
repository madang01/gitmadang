package kr.pe.codda.weblib.sitemenu;

import kr.pe.codda.weblib.common.BoardType;

public class CommunityLeftMenuInfo extends AbstractLeftMenuInfoOfTopMenu {
	public static final String[] BOARD_LEFTMENU_BASE_URL_LIST = {
			"/servlet/BoardList",
			"/servlet/BoardWrite",
			"/servlet/BoardDetail",
			"/servlet/BoardModify",
			"/servlet/BoardReply"
	};
	
	
	
	private String buildFreeBoardURL(String baseURL, BoardType targetBoardType) {
		return new StringBuilder(baseURL)
				.append("?boardId=")
				.append(targetBoardType.getValue()).toString();
	}
	
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
		
		siteLeftMenuInfo = addSiteLeftMenuInfo(new StringBuilder(BoardType.FREE.getName())
				.append(" 게시판").toString(), 
				buildFreeBoardURL("/servlet/BoardList", BoardType.FREE));	
		
		for (String boardBaseURL : BOARD_LEFTMENU_BASE_URL_LIST) {
			addSiteLeftMenuGroup(siteLeftMenuInfo, buildFreeBoardURL(boardBaseURL, BoardType.FREE));
		}		
		
		siteLeftMenuInfo = addSiteLeftMenuInfo(new StringBuilder(BoardType.FAQ.getName())
				.append(" 게시판").toString(), 
				buildFreeBoardURL("/servlet/BoardList", BoardType.FAQ));		
		for (String boardBaseURL : BOARD_LEFTMENU_BASE_URL_LIST) {
			addSiteLeftMenuGroup(siteLeftMenuInfo, buildFreeBoardURL(boardBaseURL, BoardType.FAQ));
		}
	}
}
