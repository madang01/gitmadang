package kr.pe.sinnori.weblib.sitemenu;

public class SiteMenuInfo extends AbstractSiteMenuInfo {
	
	protected void build() {
		add("소개", "/menu/about.jsp"); 
		add("시작하기", "/menu/stepbystep/main.jsp");
		add("다운로드", "/menu/download/main.jsp");
		add("사랑방", "/menu/board/body.jsp");
		add("문서", "/menu/techdoc/body.jsp");
		add("회원", "/menu/member/body.jsp");
		add("실험과 검증", "/menu/testcode/body.jsp");
	}
}
