package kr.pe.codda.weblib.sitemenu;

import java.util.ArrayList;
import java.util.List;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.TreeSiteMenuReq.TreeSiteMenuReq;
import kr.pe.codda.impl.message.TreeSiteMenuRes.TreeSiteMenuRes;

public class UserSiteMenuManger {
	private InternalLogger log = InternalLoggerFactory.getInstance(UserSiteMenuManger.class);
	
	
	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class UserSiteMenuMangerHolder {
		static final UserSiteMenuManger singleton = new UserSiteMenuManger();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static UserSiteMenuManger getInstance() {
		return UserSiteMenuMangerHolder.singleton;
	}
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 * @throws NoMoreDataPacketBufferException 
	 */
	private UserSiteMenuManger() {
	}
	
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
				
				siteNavbarStringBuilder.append(getTabStrings(tapStep));
				siteNavbarStringBuilder.append("</li>");
				siteNavbarStringBuilder.append(CommonStaticFinalVars.NEWLINE);
			}
		}
		return siteNavbarStringBuilder.toString();
	}
	
	
	private TreeSiteMenuRes getArraySiteMenuRes() {
		TreeSiteMenuReq treeSiteMenuReq = new TreeSiteMenuReq();
		TreeSiteMenuRes treeSiteMenuRes = null;
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		try {
			AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(treeSiteMenuReq);
			
			if ((outputMessage instanceof TreeSiteMenuRes)) {
				treeSiteMenuRes = (TreeSiteMenuRes)outputMessage;
			} else {
				log.warn("1차원 배열 구조를 갖는 사이트 메뉴 응답을 얻는데 에러 결과 메시지 혹은 잘못된 출력 메시지를 받았습니다. outputmessage={}", outputMessage.toString());
			}
		} catch(Exception e) {
			log.warn("1차원 배열 구조를 갖는 사이트 메뉴 응답을 얻는데 실패하였습니다. errmsg={}", e.getMessage());
			
		}
		
		if (null == treeSiteMenuRes) {
			/** 1차원 배열 구조를 갖는 사이트 메뉴 응답얻는데 실패하여 디폴트 값으로 설정 */
			treeSiteMenuRes = getTreeSiteMenuResForTestScenarioNo1();			
		}
		
		return treeSiteMenuRes;
	}
	
	/**
	 * @return  2018년 8월 21일 기준 sample_base 프로젝트의 일반 유저 사이트 메뉴 구성과 같은 {@link TreeSiteMenuRes} 를 반환한다. 메뉴 깊이는 0부터 시작되는데 sample_base 프로젝트의 일반 유저 사이트 메뉴 최대 깊이는 1이다.
	 */
	private TreeSiteMenuRes getTreeSiteMenuResForTestScenarioNo1() {
		TreeSiteMenuRes treeSiteMenuResForTestScenarioNo1 = new TreeSiteMenuRes();
		
		List<TreeSiteMenuRes.Menu> rootMenuList = new ArrayList<TreeSiteMenuRes.Menu>();

		{
			TreeSiteMenuRes.Menu rootMenu = new TreeSiteMenuRes.Menu();

			rootMenu.setParentNo(0L);
			rootMenu.setMenuName("사랑방");
			rootMenu.setLinkURL("/jsp/community/body.jsp");

			List<TreeSiteMenuRes.Menu> childMenuList = new ArrayList<TreeSiteMenuRes.Menu>();
			{
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("공지");
					childMenu.setLinkURL("/servlet/BoardList?boardID=0");
					childMenuList.add(childMenu);
				}

				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("자유게시판");
					childMenu.setLinkURL("/servlet/BoardList?boardID=1");
					childMenuList.add(childMenu);
				}

				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("FAQ");
					childMenu.setLinkURL("/servlet/BoardList?boardID=2");
					childMenuList.add(childMenu);
				}
			}

			rootMenu.setChildMenuListSize(childMenuList.size());
			rootMenu.setChildMenuList(childMenuList);

			rootMenuList.add(rootMenu);
		}

		
		{
			TreeSiteMenuRes.Menu rootMenu = new TreeSiteMenuRes.Menu();

			rootMenu.setParentNo(0L);
			rootMenu.setMenuName("문서");
			rootMenu.setLinkURL("/jsp/doc/body.jsp");

			List<TreeSiteMenuRes.Menu> childMenuList = new ArrayList<TreeSiteMenuRes.Menu>();
			{
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("코다 활용 howto");
					childMenu.setLinkURL("/jsp/doc/CoddaHowTo.jsp");
					childMenuList.add(childMenu);
				}				
			}

			rootMenu.setChildMenuListSize(childMenuList.size());
			rootMenu.setChildMenuList(childMenuList);

			rootMenuList.add(rootMenu);
		}
		
		{
			TreeSiteMenuRes.Menu rootMenu = new TreeSiteMenuRes.Menu();

			rootMenu.setParentNo(0L);
			rootMenu.setMenuName("도구");
			rootMenu.setLinkURL("/jsp/doc/body.jsp");

			List<TreeSiteMenuRes.Menu> childMenuList = new ArrayList<TreeSiteMenuRes.Menu>();
			{
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("JDF-비 로그인 테스트");
					childMenu.setLinkURL("/servlet/JDFNotLoginTest");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("JDF-로그인 테스트");
					childMenu.setLinkURL("/servlet/JDFLoginTest");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("세션키 테스트");
					childMenu.setLinkURL("/servlet/JDFSessionKeyTest");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("RSA 테스트");
					childMenu.setLinkURL("/servlet/JSRSATest");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("메세지 다이제스트(MD) 테스트");
					childMenu.setLinkURL("/servlet/JSMessageDigestTest");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("대칭키 테스트");
					childMenu.setLinkURL("/servlet/JSSymmetricKeyTest");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("에코 테스트");
					childMenu.setLinkURL("/servlet/EchoTest");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("모든 데이터 타입 검증");
					childMenu.setLinkURL("/servlet/AllItemTypeTest");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("자바 문자열 변환 도구");
					childMenu.setLinkURL("/servlet/JavaStringConverter");
					childMenuList.add(childMenu);
				}
			}

			rootMenu.setChildMenuListSize(childMenuList.size());
			rootMenu.setChildMenuList(childMenuList);

			rootMenuList.add(rootMenu);
		}
		
		{
			TreeSiteMenuRes.Menu rootMenu = new TreeSiteMenuRes.Menu();

			rootMenu.setParentNo(0L);
			rootMenu.setMenuName("회원");
			rootMenu.setLinkURL("/jsp/member/body.jsp");

			List<TreeSiteMenuRes.Menu> childMenuList = new ArrayList<TreeSiteMenuRes.Menu>();
			{
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("로그인");
					childMenu.setLinkURL("/servlet/UserLogin");
					childMenuList.add(childMenu);
				}	
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("회원 가입");
					childMenu.setLinkURL("/servlet/MemberRegistration");
					childMenuList.add(childMenu);
				}
			}

			rootMenu.setChildMenuListSize(childMenuList.size());
			rootMenu.setChildMenuList(childMenuList);

			rootMenuList.add(rootMenu);
		}
		
		treeSiteMenuResForTestScenarioNo1.setRootMenuListSize(rootMenuList.size());
		treeSiteMenuResForTestScenarioNo1.setRootMenuList(rootMenuList);
		
		return treeSiteMenuResForTestScenarioNo1;
	}
	
	
	public String getSiteNavbarString(String menuGroupURL, boolean isLogin) {
		final int tapStep = 1;
		TreeSiteMenuRes treeSiteMenuRes = getArraySiteMenuRes();
		String siteNavbarString = getSiteNavbarString(menuGroupURL, treeSiteMenuRes.getRootMenuList(), tapStep+4);
		
		
		if (null == menuGroupURL) {
			menuGroupURL = "/";
		}		
		
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
				.append("<button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\"#coddaNavbar\">")
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
				.append("<div class=\"collapse navbar-collapse\" id=\"coddaNavbar\">")				
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
				
		siteNavbarStringBuilder.append(siteNavbarString)				
				.append(getTabStrings(tapStep+3))
				.append("</ul>")				
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep+3))
				.append("<ul class=\"nav navbar-nav navbar-right\">")				
				.append(CommonStaticFinalVars.NEWLINE)
				.append(getTabStrings(tapStep+4))
				.append("<li><a href=\"")
				.append("/servlet/MemberRegistration")
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
			.append("/servlet/UserLogin")
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
