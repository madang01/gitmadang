package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbSeqTb.SB_SEQ_TB;
import static kr.pe.codda.impl.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;

import javax.sql.DataSource;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DBCPDataSourceNotFoundException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.ArraySiteMenuReq.ArraySiteMenuReq;
import kr.pe.codda.impl.message.ArraySiteMenuRes.ArraySiteMenuRes;
import kr.pe.codda.impl.message.ChildMenuAddReq.ChildMenuAddReq;
import kr.pe.codda.impl.message.ChildMenuAddRes.ChildMenuAddRes;
import kr.pe.codda.impl.message.MenuDeleteReq.MenuDeleteReq;
import kr.pe.codda.impl.message.MenuMoveDownReq.MenuMoveDownReq;
import kr.pe.codda.impl.message.MenuMoveUpReq.MenuMoveUpReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.RootMenuAddReq.RootMenuAddReq;
import kr.pe.codda.impl.message.RootMenuAddRes.RootMenuAddRes;
import kr.pe.codda.impl.message.TreeSiteMenuReq.TreeSiteMenuReq;
import kr.pe.codda.impl.message.TreeSiteMenuRes.TreeSiteMenuRes;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.SequenceType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.SiteMenuTree;
import kr.pe.codda.server.lib.SiteMenuTreeNode;
import kr.pe.codda.server.lib.VirtualSiteMenuTreeBuilderIF;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * 사이트 메뉴 통합 테스트
 * @author Won Jonghoon
 *
 */
public class SiteMenuIntegrationTest extends AbstractJunitTest {
	private final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;
	// private static UInteger backupMenuNo = null;
	// private static ArraySiteMenuRes backupArraySiteMenuRes = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();
		ServerDBUtil.initializeDBEnvoroment(TEST_DBCP_NAME);		
	}
	
	@Before
	public void setUp() {
		UByte menuSequenceID = UByte.valueOf(SequenceType.MENU.getSequenceID());
		
		
		
		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance()
					.getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}		
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));

			create.update(SB_SEQ_TB).set(SB_SEQ_TB.SQ_VALUE, UInteger.valueOf(1))
					.where(SB_SEQ_TB.SQ_ID.eq(menuSequenceID)).execute();
			
			create.delete(SB_SITEMENU_TB).execute();

			conn.commit();	
			
			// backupMenuNo = menuSeqRecord.getValue(SB_SEQ_TB.SQ_VALUE);

		} catch (Exception e) {

			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
			}

			log.warn(e.getMessage(), e);

			fail(e.getMessage());
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}		
	}	
	
	@After
	public void tearDown(){
		
	} 
	/*
	private boolean compareMenuList(List<TreeSiteMenuRes.Menu> expectedMenuList,
			List<TreeSiteMenuRes.Menu> acutalMenuList) {
		if (null == expectedMenuList && null == acutalMenuList) {
			return true;
		}
		
		if (null == expectedMenuList) {
			if (acutalMenuList.size() == 0) {
				return true;
			}
			
			log.warn("예상된 목록이 null 인데 실제 목록은  이 아닙니다");
			return false;
		}		
		
		if (null == acutalMenuList) {
			if (expectedMenuList.size() == 0) {
				return true;
			}
			
			log.warn("예상된 목록이 null 이 아닌데 실제 목록은 null 입니다");
			return false;
		}

		if (expectedMenuList.size() != acutalMenuList.size()) {
			log.warn("예상된 목록의 크기가 실제 목록이 크기와 다릅니다");
			return false;
		}
		

		for (int i = 0; i < acutalMenuList.size(); i++) {
			TreeSiteMenuRes.Menu expectedMenu = expectedMenuList.get(i);
			TreeSiteMenuRes.Menu acutalMenu = acutalMenuList.get(i);
			
			//log.info("{}::expectedMenu={}", i, expectedMenu.toString());
			//log.info("{}::acutalMenu={}", i, acutalMenu.toString());

			if (expectedMenu.getMenuNo() != acutalMenu.getMenuNo()) {
				log.warn("예상된 메뉴 번호[{}]와 실메 메뉴 번호[{}]가 다릅니다", expectedMenu.toString(), acutalMenu.toString());
				return false;
			}
			
			if (expectedMenu.getParentNo() != acutalMenu.getParentNo()) {
				log.warn("예상된 메뉴의 부모 번호[{}]와 실메 메뉴의 부모 번호[{}]가 다릅니다", expectedMenu.toString(), acutalMenu.toString());
				return false;
			}

			if (!expectedMenu.getMenuName().equals(acutalMenu.getMenuName())) {
				log.warn("예상된 메뉴의 메뉴명[{}]와 실메 메뉴의 메뉴명[{}]가 다릅니다", expectedMenu.toString(), acutalMenu.toString());
				return false;
			}

			if (!expectedMenu.getLinkURL().equals(acutalMenu.getLinkURL())) {
				log.warn("예상된 메뉴의 링크 주소[{}]와 실메 메뉴의 링크 주소가[{}]가 다릅니다", expectedMenu.toString(), acutalMenu.toString());
				return false;
			}

			boolean result = compareMenuList(expectedMenu.getChildMenuList(), acutalMenu.getChildMenuList());

			if (!result) {
				return false;
			}
		}

		return true;

	}*/

	/**
	 * @return  2018년 8월 21일 기준 sample_base 프로젝트의 일반 유저 사이트 메뉴 구성과 같은 {@link TreeSiteMenuRes} 를 반환한다. 메뉴 깊이는 0부터 시작되는데 sample_base 프로젝트의 일반 유저 사이트 메뉴 최대 깊이는 1이다.
	 */
	/*private TreeSiteMenuRes getTreeSiteMenuResForTestScenarioNo1() {
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
					childMenu.setLinkURL("/servlet/JDFNotLogin");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("JDF-로그인 테스트");
					childMenu.setLinkURL("/servlet/JDFLogin");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("세션키 테스트");
					childMenu.setLinkURL("/servlet/JDFSessionKey");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("RSA 테스트");
					childMenu.setLinkURL("/servlet/JSRSAInput");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("메세지 다이제스트(MD) 테스트");
					childMenu.setLinkURL("/servlet/JSMessageDigestInput");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("대칭키 테스트");
					childMenu.setLinkURL("/servlet/JSSymmetricKeyInput");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("에코 테스트");
					childMenu.setLinkURL("/servlet/Echo");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("모든 데이터 타입 검증");
					childMenu.setLinkURL("/servlet/UserLoginInput");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("자바 문자열 변환 도구");
					childMenu.setLinkURL("/servlet/JavaStringConverterInput");
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
					childMenu.setLinkURL("/servlet/UserLoginInput");
					childMenuList.add(childMenu);
				}	
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("회원 가입");
					childMenu.setLinkURL("/servlet/UserSiteMembershipInput");
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
	}*/
	
	/**
	 * WARNING! 메뉴 이동 테스트를 위한 메뉴 구성이므로 수정시 주의 요망. 
	 * 메뉴 이동 테스트시 대상 메뉴는 자신을 루트로 한 트리 단위로 이동해야 한다.
	 * 하여 '세션키 테스트' 와 'RSA 테스트' 에 손자 메뉴를 추가하였다.
	 *  
	 * @return 메뉴 이동 테스트를 위한 {@link TreeSiteMenuRes} 를 반환한다.   
	 */
	/*private TreeSiteMenuRes getTreeSiteMenuResForMiddleMenuMovement() {
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
					childMenu.setLinkURL("/servlet/JDFNotLogin");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("JDF-로그인 테스트");
					childMenu.setLinkURL("/servlet/JDFLogin");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("세션키 테스트");
					childMenu.setLinkURL("/servlet/JDFSessionKey");
					
					List<TreeSiteMenuRes.Menu> twoDepthChildMenuList = new ArrayList<TreeSiteMenuRes.Menu>();
					{
						TreeSiteMenuRes.Menu twoDepthChildMenu = new TreeSiteMenuRes.Menu();
						twoDepthChildMenu.setMenuName("세션키_2단계_1");
						twoDepthChildMenu.setLinkURL("/servlet/sessionKey_twoDepth_1");
						
						List<TreeSiteMenuRes.Menu> threeDepthChildMenuList = new ArrayList<TreeSiteMenuRes.Menu>();
						{
							TreeSiteMenuRes.Menu threeDepthChildMenu = new TreeSiteMenuRes.Menu();
							threeDepthChildMenu.setMenuName("세션키_3단계_1");
							threeDepthChildMenu.setLinkURL("/servlet/sessionKey_threeDepth_1");
							
							threeDepthChildMenuList.add(threeDepthChildMenu);
						}
						twoDepthChildMenu.setChildMenuListSize(threeDepthChildMenuList.size());
						twoDepthChildMenu.setChildMenuList(threeDepthChildMenuList);

						twoDepthChildMenuList.add(twoDepthChildMenu);
					}					
					{
						TreeSiteMenuRes.Menu twoDepthChildMenu = new TreeSiteMenuRes.Menu();

						twoDepthChildMenu.setMenuName("세션키_2단계_2");
						twoDepthChildMenu.setLinkURL("/servlet/sessionKey_twoDepth_2");
						
						twoDepthChildMenuList.add(twoDepthChildMenu);
					}
					childMenu.setChildMenuListSize(twoDepthChildMenuList.size());
					childMenu.setChildMenuList(twoDepthChildMenuList);
					
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("RSA 테스트");
					childMenu.setLinkURL("/servlet/JSRSAInput");
					List<TreeSiteMenuRes.Menu> twoDepthChildMenuList = new ArrayList<TreeSiteMenuRes.Menu>();
					{
						TreeSiteMenuRes.Menu twoDepthChildMenu = new TreeSiteMenuRes.Menu();

						twoDepthChildMenu.setMenuName("RSA_2단계_1");
						twoDepthChildMenu.setLinkURL("/servlet/rsa_twoDepth_1");
						
						twoDepthChildMenuList.add(twoDepthChildMenu);
					}					
					{
						TreeSiteMenuRes.Menu twoDepthChildMenu = new TreeSiteMenuRes.Menu();

						twoDepthChildMenu.setMenuName("RSA_2단계_2");
						twoDepthChildMenu.setLinkURL("/servlet/rsa_twoDepth_2");
						
						List<TreeSiteMenuRes.Menu> threeDepthChildMenuList = new ArrayList<TreeSiteMenuRes.Menu>();
						{
							TreeSiteMenuRes.Menu threeDepthChildMenu = new TreeSiteMenuRes.Menu();
							threeDepthChildMenu.setMenuName("RSA_3단계_1");
							threeDepthChildMenu.setLinkURL("/servlet/rsa_threeDepth_1");
							
							threeDepthChildMenuList.add(threeDepthChildMenu);
						}
						twoDepthChildMenu.setChildMenuListSize(threeDepthChildMenuList.size());
						twoDepthChildMenu.setChildMenuList(threeDepthChildMenuList);
						
						twoDepthChildMenuList.add(twoDepthChildMenu);
					}
					childMenu.setChildMenuListSize(twoDepthChildMenuList.size());
					childMenu.setChildMenuList(twoDepthChildMenuList);
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
					childMenu.setLinkURL("/servlet/Echo");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("모든 데이터 타입 검증");
					childMenu.setLinkURL("/servlet/UserLoginInput");
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
					childMenu.setLinkURL("/servlet/UserLoginInput");
					childMenuList.add(childMenu);
				}	
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("회원 가입");
					childMenu.setLinkURL("/servlet/UserSiteMembershipInput");
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
	}*/
	
	/*private TreeSiteMenuRes getTreeSiteMenuResForBottomMenuMovement() {
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
					childMenu.setLinkURL("/servlet/JDFNotLogin");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("JDF-로그인 테스트");
					childMenu.setLinkURL("/servlet/JDFLogin");
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("세션키 테스트");
					childMenu.setLinkURL("/servlet/JDFSessionKey");
					
					List<TreeSiteMenuRes.Menu> twoDepthChildMenuList = new ArrayList<TreeSiteMenuRes.Menu>();
					{
						TreeSiteMenuRes.Menu twoDepthChildMenu = new TreeSiteMenuRes.Menu();
						twoDepthChildMenu.setMenuName("세션키_2단계_1");
						twoDepthChildMenu.setLinkURL("/servlet/sessionKey_twoDepth_1");
						
						List<TreeSiteMenuRes.Menu> threeDepthChildMenuList = new ArrayList<TreeSiteMenuRes.Menu>();
						{
							TreeSiteMenuRes.Menu threeDepthChildMenu = new TreeSiteMenuRes.Menu();
							threeDepthChildMenu.setMenuName("세션키_3단계_1");
							threeDepthChildMenu.setLinkURL("/servlet/sessionKey_threeDepth_1");
							
							threeDepthChildMenuList.add(threeDepthChildMenu);
						}
						twoDepthChildMenu.setChildMenuListSize(threeDepthChildMenuList.size());
						twoDepthChildMenu.setChildMenuList(threeDepthChildMenuList);

						twoDepthChildMenuList.add(twoDepthChildMenu);
					}					
					{
						TreeSiteMenuRes.Menu twoDepthChildMenu = new TreeSiteMenuRes.Menu();

						twoDepthChildMenu.setMenuName("세션키_2단계_2");
						twoDepthChildMenu.setLinkURL("/servlet/sessionKey_twoDepth_2");
						
						twoDepthChildMenuList.add(twoDepthChildMenu);
					}
					childMenu.setChildMenuListSize(twoDepthChildMenuList.size());
					childMenu.setChildMenuList(twoDepthChildMenuList);
					
					childMenuList.add(childMenu);
				}
				
				{
					TreeSiteMenuRes.Menu childMenu = new TreeSiteMenuRes.Menu();

					childMenu.setMenuName("RSA 테스트");
					childMenu.setLinkURL("/servlet/JSRSAInput");
					List<TreeSiteMenuRes.Menu> twoDepthChildMenuList = new ArrayList<TreeSiteMenuRes.Menu>();
					{
						TreeSiteMenuRes.Menu twoDepthChildMenu = new TreeSiteMenuRes.Menu();

						twoDepthChildMenu.setMenuName("RSA_2단계_1");
						twoDepthChildMenu.setLinkURL("/servlet/rsa_twoDepth_1");
						
						twoDepthChildMenuList.add(twoDepthChildMenu);
					}					
					{
						TreeSiteMenuRes.Menu twoDepthChildMenu = new TreeSiteMenuRes.Menu();

						twoDepthChildMenu.setMenuName("RSA_2단계_2");
						twoDepthChildMenu.setLinkURL("/servlet/rsa_twoDepth_2");
						
						List<TreeSiteMenuRes.Menu> threeDepthChildMenuList = new ArrayList<TreeSiteMenuRes.Menu>();
						{
							TreeSiteMenuRes.Menu threeDepthChildMenu = new TreeSiteMenuRes.Menu();
							threeDepthChildMenu.setMenuName("RSA_3단계_2");
							threeDepthChildMenu.setLinkURL("/servlet/rsa_threeDepth_2");
							
							threeDepthChildMenuList.add(threeDepthChildMenu);
						}
						twoDepthChildMenu.setChildMenuListSize(threeDepthChildMenuList.size());
						twoDepthChildMenu.setChildMenuList(threeDepthChildMenuList);
						
						twoDepthChildMenuList.add(twoDepthChildMenu);
					}
					childMenu.setChildMenuListSize(twoDepthChildMenuList.size());
					childMenu.setChildMenuList(twoDepthChildMenuList);
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
	}*/
	
	/**
	 * 지정한 '부모 메뉴'의 자식 메뉴들을 전위순회(Pre-order) 하면서 추가한다. 
	 *  
	 * @param childMenuAddReqServerTask 자식 메뉴 추가 서버 타스크
	 * @param parnetMenu 전위순회(Pre-order) 대상 ROOT 노드인 '부모 메뉴', WARNING! '부모 메뉴'는 DB 에 미리 반영 되어 있어야 하며 부모 메뉴의 '메뉴 번호'는  또한 반듯이 DB 에 넣어진 '메뉴 번호' 값이어야 한다.
	 */
	/*private void addMenuUsingPreOrderTraversal(ChildMenuAddReqServerTask childMenuAddReqServerTask,
			TreeSiteMenuRes.Menu parnetMenu) {
		// log.info("parnetMenu={}", parnetMenu.toString());
		
		
		List<TreeSiteMenuRes.Menu> expectedChildMenuList = parnetMenu.getChildMenuList();
		if (null == expectedChildMenuList) {
			return;
		}

		for (TreeSiteMenuRes.Menu expectedChildMenu : expectedChildMenuList) {
			ChildMenuAddReq childMenuAddReq = new ChildMenuAddReq();
			childMenuAddReq.setParentNo(parnetMenu.getMenuNo());
			childMenuAddReq.setMenuName(expectedChildMenu.getMenuName());
			childMenuAddReq.setLinkURL(expectedChildMenu.getLinkURL());

			try {
				ChildMenuAddRes childMenuAddRes = childMenuAddReqServerTask.doWork(TEST_DBCP_NAME, childMenuAddReq);
				expectedChildMenu.setMenuNo(childMenuAddRes.getMenuNo());
				expectedChildMenu.setParentNo(parnetMenu.getMenuNo());

				addMenuUsingPreOrderTraversal(childMenuAddReqServerTask, expectedChildMenu);

			} catch (Exception e) {
				String errorMessage = new StringBuilder().append("부모 메뉴[").append(parnetMenu.getMenuName())
						.append("]의 자식 메뉴[").append(expectedChildMenu.getMenuName()).append("] 추가 실패").toString();

				log.warn(errorMessage, e);

				fail(errorMessage);
			}
		}
	}*/
	
	
	@Test
	public void 초기상태일때빈목록조회테스트() {
		ArraySiteMenuReqServerTask arraySiteMenuReqServerTask = new ArraySiteMenuReqServerTask();
		TreeSiteMenuReqServerTask treeSiteMenuReqServerTask = new TreeSiteMenuReqServerTask();
		
		
		ArraySiteMenuReq arraySiteMenuReq = new ArraySiteMenuReq();
		try {
			ArraySiteMenuRes arraySiteMenuRes = arraySiteMenuReqServerTask.doWork(TEST_DBCP_NAME, arraySiteMenuReq);
			
			if (arraySiteMenuRes.getCnt() != 0) {
				log.warn("초기 상태에서는 배열 크기가 0이 아닙니다, arraySiteMenuRes={}", arraySiteMenuRes.toString());
				fail("초기 상태에서는 배열 크기가 0이 아닙니다");
			}
		} catch (Exception e) {
			String errorMessage = "배열형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}
		
		
		
		TreeSiteMenuReq treeSiteMenuReq = new TreeSiteMenuReq();
		try {
			TreeSiteMenuRes treeSiteMenuRes = treeSiteMenuReqServerTask.doWork(TEST_DBCP_NAME, treeSiteMenuReq);
			
			if (treeSiteMenuRes.getRootMenuListSize() != 0) {
				log.warn("초기 상태에서는 배열 크기가 0이 아닙니다, treeSiteMenuRes={}", treeSiteMenuRes.toString());
				fail("초기 상태에서는 배열 크기가 0이 아닙니다");
			}
		} catch (Exception e) {
			String errorMessage = "트리형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}
	}
	
		
	@Test 
	public void 메뉴이동테스트_상단이동후다시하단이동하여원복() {
		/**
		 * WARNING! 메뉴 이동 테스트 대상 메뉴는 메뉴 깊이 3을 갖는 '세션키 테스트' 와  'RSA 테스트' 이다.
		 * 입력한 메뉴 순서는  '세션키 테스트' 이고 다음이 'RSA 테스트' 이다.
		 */
		// final long menuNoForMoveUpDownTest = 14L;		
		
		ArraySiteMenuReqServerTask arraySiteMenuReqServerTask = new ArraySiteMenuReqServerTask();
		MenuMoveUpReqServerTask menuUpMoveReqServerTask = new MenuMoveUpReqServerTask();
		MenuMoveDownReqServerTask menuDownMoveReqServerTask = new MenuMoveDownReqServerTask();

		
		class VirtualSiteMenuTreeBuilder implements VirtualSiteMenuTreeBuilderIF {

			@Override
			public SiteMenuTree build() {
				SiteMenuTree siteMenuTree = new SiteMenuTree();
				
				{
					SiteMenuTreeNode rootSiteMenuTreeNode = new SiteMenuTreeNode();
					
					rootSiteMenuTreeNode.setMenuName("사랑방");
					rootSiteMenuTreeNode.setLinkURL("/jsp/community/body.jsp");
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("공지");
						childSiteMenuTreeNode.setLinkURL("/servlet/BoardList?boardID=0");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("자유게시판");
						childSiteMenuTreeNode.setLinkURL("/servlet/BoardList?boardID=1");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("FAQ");
						childSiteMenuTreeNode.setLinkURL("/servlet/BoardList?boardID=2");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}
				
				{
					SiteMenuTreeNode rootSiteMenuTreeNode = new SiteMenuTreeNode();
					
					rootSiteMenuTreeNode.setMenuName("문서");
					rootSiteMenuTreeNode.setLinkURL("/jsp/doc/body.jsp");
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("코다 활용 howto");
						childSiteMenuTreeNode.setLinkURL("/jsp/doc/CoddaHowTo.jsp");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}	
					
					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}
				
				{
					SiteMenuTreeNode rootSiteMenuTreeNode = new SiteMenuTreeNode();
					
					rootSiteMenuTreeNode.setMenuName("도구");
					rootSiteMenuTreeNode.setLinkURL("/jsp/doc/body.jsp");
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("JDF-비 로그인 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JDFNotLogin");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("JDF-로그인 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JDFLogin");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("세션키 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JDFSessionKey");
						
						{
							SiteMenuTreeNode childchildSiteMenuTreeNode = new SiteMenuTreeNode();

							childchildSiteMenuTreeNode.setMenuName("세션키_2단계_1");
							childchildSiteMenuTreeNode.setLinkURL("/servlet/sessionKey_twoDepth_1");
							
							{
								SiteMenuTreeNode childchildchildSiteMenuTreeNode = new SiteMenuTreeNode();

								childchildchildSiteMenuTreeNode.setMenuName("세션키_3단계_1");
								childchildchildSiteMenuTreeNode.setLinkURL("/servlet/sessionKey_twoDepth_1");
								
								childchildSiteMenuTreeNode.addChildSiteMenuNode(childchildchildSiteMenuTreeNode);
							}
							
							childSiteMenuTreeNode.addChildSiteMenuNode(childchildSiteMenuTreeNode);
						}
						
						{
							SiteMenuTreeNode childchildSiteMenuTreeNode = new SiteMenuTreeNode();

							childchildSiteMenuTreeNode.setMenuName("세션키_2단계_2");
							childchildSiteMenuTreeNode.setLinkURL("/servlet/sessionKey_twoDepth_2");
							
							childSiteMenuTreeNode.addChildSiteMenuNode(childchildSiteMenuTreeNode);
						}
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("RSA 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JSRSAInput");
						
						{
							SiteMenuTreeNode childchildSiteMenuTreeNode = new SiteMenuTreeNode();

							childchildSiteMenuTreeNode.setMenuName("RSA_2단계_1");
							childchildSiteMenuTreeNode.setLinkURL("/servlet/rsa_twoDepth_1");							
							
							childSiteMenuTreeNode.addChildSiteMenuNode(childchildSiteMenuTreeNode);
						}
						
						{
							SiteMenuTreeNode childchildSiteMenuTreeNode = new SiteMenuTreeNode();

							childchildSiteMenuTreeNode.setMenuName("RSA_2단계_2");
							childchildSiteMenuTreeNode.setLinkURL("/servlet/rsa_twoDepth_2");
							
							{
								SiteMenuTreeNode childchildchildSiteMenuTreeNode = new SiteMenuTreeNode();

								childchildchildSiteMenuTreeNode.setMenuName("RSA_3단계_1");
								childchildchildSiteMenuTreeNode.setLinkURL("/servlet/rsa_threeDepth_1");
								
								childchildSiteMenuTreeNode.addChildSiteMenuNode(childchildchildSiteMenuTreeNode);
							}
							
							childSiteMenuTreeNode.addChildSiteMenuNode(childchildSiteMenuTreeNode);
						}
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("메세지 다이제스트(MD) 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JSMessageDigestInput");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("대칭키 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JSSymmetricKeyInput");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("에코 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/Echo");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("모든 데이터 타입 검증");
						childSiteMenuTreeNode.setLinkURL("/servlet/UserLoginInput");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("자바 문자열 변환 도구");
						childSiteMenuTreeNode.setLinkURL("/servlet/JavaStringConverterInput");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}
				
				{
					SiteMenuTreeNode rootSiteMenuTreeNode = new SiteMenuTreeNode();
					
					rootSiteMenuTreeNode.setMenuName("회원");
					rootSiteMenuTreeNode.setLinkURL("/jsp/member/body.jsp");
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("로그인");
						childSiteMenuTreeNode.setLinkURL("/servlet/UserLoginInput");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("회원 가입");
						childSiteMenuTreeNode.setLinkURL("/servlet/UserSiteMembershipInput");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}
				
				return siteMenuTree;
			}
		}
		
		VirtualSiteMenuTreeBuilderIF virtualSiteMenuTreeBuilder = new VirtualSiteMenuTreeBuilder();		
		SiteMenuTree siteMenuTree = virtualSiteMenuTreeBuilder.build();
		siteMenuTree.makeDBRecord(TEST_DBCP_NAME);
		
		// RSA 테스트
		SiteMenuTreeNode sourceSiteMenuTreeNode = siteMenuTree.find("RSA 테스트");
		if (null == sourceSiteMenuTreeNode) {
			fail("상단 이동할 대상 메뉴[RSA 테스트] 찾기 실패");
		}
		
		SiteMenuTreeNode targetSiteMenuTreeNode = siteMenuTree.find("세션키 테스트");
		
		if (null == targetSiteMenuTreeNode) {
			fail("상단 이동할 위치에 있는 메뉴[세션키 테스트] 찾기 실패");
		}
		
		MenuMoveUpReq menuUpMoveReq = new MenuMoveUpReq();
		menuUpMoveReq.setMenuNo(sourceSiteMenuTreeNode.getMenuNo());
		
		try {
			MessageResultRes messageResultRes = menuUpMoveReqServerTask.doWork(TEST_DBCP_NAME, menuUpMoveReq);
			if (! messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		
		ArraySiteMenuReq arraySiteMenuReq = new ArraySiteMenuReq();
		ArraySiteMenuRes arraySiteMenuRes = null;
		
		try {
			arraySiteMenuRes = arraySiteMenuReqServerTask.doWork(TEST_DBCP_NAME, arraySiteMenuReq);
		} catch (Exception e) {
			String errorMessage = "배열형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}
		
		for (ArraySiteMenuRes.Menu siteMenu : arraySiteMenuRes.getMenuList()) {
			String menuName = siteMenu.getMenuName();
			
			
			if (menuName.equals("RSA 테스트")) {
				assertEquals("메뉴 순서 비교", targetSiteMenuTreeNode.getOrderSeq(), siteMenu.getOrderSeq());				
			} else if (menuName.equals("RSA_2단계_1")) {	
				assertEquals("메뉴 순서 비교", targetSiteMenuTreeNode.getOrderSeq()+1, siteMenu.getOrderSeq());
			} else if (menuName.equals("RSA_2단계_2")) {
				assertEquals("메뉴 순서 비교", targetSiteMenuTreeNode.getOrderSeq()+2, siteMenu.getOrderSeq());
			} else if (menuName.equals("RSA_3단계_1")) {	
				assertEquals("메뉴 순서 비교", targetSiteMenuTreeNode.getOrderSeq()+3, siteMenu.getOrderSeq());
			} else if (menuName.equals("세션키 테스트")) {
				assertEquals("메뉴 순서 비교", sourceSiteMenuTreeNode.getOrderSeq(), siteMenu.getOrderSeq());
			} else if (menuName.equals("세션키_2단계_1")) {
				assertEquals("메뉴 순서 비교", sourceSiteMenuTreeNode.getOrderSeq()+1, siteMenu.getOrderSeq());
			} else if (menuName.equals("세션키_3단계_1")) {
				assertEquals("메뉴 순서 비교", sourceSiteMenuTreeNode.getOrderSeq()+2, siteMenu.getOrderSeq());
			} else if (menuName.equals("세션키_2단계_2")) {
				assertEquals("메뉴 순서 비교", sourceSiteMenuTreeNode.getOrderSeq()+3, siteMenu.getOrderSeq());
			} else {
				SiteMenuTreeNode workingSiteMenuTreeNode = siteMenuTree.find(menuName);
				if (null == workingSiteMenuTreeNode) {
					fail("메뉴["+menuName+"] 찾기 실패");
				}				
				assertEquals("메뉴 순서 비교", workingSiteMenuTreeNode.getOrderSeq(), siteMenu.getOrderSeq());
			}
		}
		
		MenuMoveDownReq menuDownMoveReq = new MenuMoveDownReq();
		menuDownMoveReq.setMenuNo(sourceSiteMenuTreeNode.getMenuNo());
		
		try {
			MessageResultRes messageResultRes = menuDownMoveReqServerTask.doWork(TEST_DBCP_NAME, menuDownMoveReq);
			if (! messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		try {
			arraySiteMenuRes = arraySiteMenuReqServerTask.doWork(TEST_DBCP_NAME, arraySiteMenuReq);
		} catch (Exception e) {
			String errorMessage = "배열형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}
		
		for (ArraySiteMenuRes.Menu siteMenu : arraySiteMenuRes.getMenuList()) {
			String menuName = siteMenu.getMenuName();			
			
			SiteMenuTreeNode workingSiteMenuTreeNode = siteMenuTree.find(menuName);
			if (null == workingSiteMenuTreeNode) {
				fail("메뉴["+menuName+"] 찾기 실패");
			}
			assertEquals("메뉴 순서 비교", workingSiteMenuTreeNode.getOrderSeq(), siteMenu.getOrderSeq());
		}
	}
	
	@Test 
	public void 메뉴이동테스트_하단이동후다시상단이동하여원복() {
		// FIXME!
		/**
		 * WARNING! 메뉴 이동 테스트 대상 메뉴는 메뉴 깊이 3을 갖는 '세션키 테스트' 와  'RSA 테스트' 이다.
		 * 입력한 메뉴 순서는  '세션키 테스트' 이고 다음이 'RSA 테스트' 이다.
		 */		
		ArraySiteMenuReqServerTask arraySiteMenuReqServerTask = new ArraySiteMenuReqServerTask();
		MenuMoveUpReqServerTask menuUpMoveReqServerTask = new MenuMoveUpReqServerTask();
		MenuMoveDownReqServerTask menuDownMoveReqServerTask = new MenuMoveDownReqServerTask();

		
		class VirtualSiteMenuTreeBuilder implements VirtualSiteMenuTreeBuilderIF {

			@Override
			public SiteMenuTree build() {
				SiteMenuTree siteMenuTree = new SiteMenuTree();
				
				{
					SiteMenuTreeNode rootSiteMenuTreeNode = new SiteMenuTreeNode();
					
					rootSiteMenuTreeNode.setMenuName("사랑방");
					rootSiteMenuTreeNode.setLinkURL("/jsp/community/body.jsp");
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("공지");
						childSiteMenuTreeNode.setLinkURL("/servlet/BoardList?boardID=0");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("자유게시판");
						childSiteMenuTreeNode.setLinkURL("/servlet/BoardList?boardID=1");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("FAQ");
						childSiteMenuTreeNode.setLinkURL("/servlet/BoardList?boardID=2");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}
				
				{
					SiteMenuTreeNode rootSiteMenuTreeNode = new SiteMenuTreeNode();
					
					rootSiteMenuTreeNode.setMenuName("문서");
					rootSiteMenuTreeNode.setLinkURL("/jsp/doc/body.jsp");
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("코다 활용 howto");
						childSiteMenuTreeNode.setLinkURL("/jsp/doc/CoddaHowTo.jsp");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}	
					
					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}
				
				{
					SiteMenuTreeNode rootSiteMenuTreeNode = new SiteMenuTreeNode();
					
					rootSiteMenuTreeNode.setMenuName("도구");
					rootSiteMenuTreeNode.setLinkURL("/jsp/doc/body.jsp");
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("JDF-비 로그인 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JDFNotLogin");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("JDF-로그인 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JDFLogin");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("세션키 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JDFSessionKey");
						
						{
							SiteMenuTreeNode childchildSiteMenuTreeNode = new SiteMenuTreeNode();

							childchildSiteMenuTreeNode.setMenuName("세션키_2단계_1");
							childchildSiteMenuTreeNode.setLinkURL("/servlet/sessionKey_twoDepth_1");
							
							{
								SiteMenuTreeNode childchildchildSiteMenuTreeNode = new SiteMenuTreeNode();

								childchildchildSiteMenuTreeNode.setMenuName("세션키_3단계_1");
								childchildchildSiteMenuTreeNode.setLinkURL("/servlet/sessionKey_twoDepth_1");
								
								childchildSiteMenuTreeNode.addChildSiteMenuNode(childchildchildSiteMenuTreeNode);
							}
							
							childSiteMenuTreeNode.addChildSiteMenuNode(childchildSiteMenuTreeNode);
						}
						
						{
							SiteMenuTreeNode childchildSiteMenuTreeNode = new SiteMenuTreeNode();

							childchildSiteMenuTreeNode.setMenuName("세션키_2단계_2");
							childchildSiteMenuTreeNode.setLinkURL("/servlet/sessionKey_twoDepth_2");
							
							childSiteMenuTreeNode.addChildSiteMenuNode(childchildSiteMenuTreeNode);
						}
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("RSA 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JSRSAInput");
						
						{
							SiteMenuTreeNode childchildSiteMenuTreeNode = new SiteMenuTreeNode();

							childchildSiteMenuTreeNode.setMenuName("RSA_2단계_1");
							childchildSiteMenuTreeNode.setLinkURL("/servlet/rsa_twoDepth_1");							
							
							childSiteMenuTreeNode.addChildSiteMenuNode(childchildSiteMenuTreeNode);
						}
						
						{
							SiteMenuTreeNode childchildSiteMenuTreeNode = new SiteMenuTreeNode();

							childchildSiteMenuTreeNode.setMenuName("RSA_2단계_2");
							childchildSiteMenuTreeNode.setLinkURL("/servlet/rsa_twoDepth_2");
							
							{
								SiteMenuTreeNode childchildchildSiteMenuTreeNode = new SiteMenuTreeNode();

								childchildchildSiteMenuTreeNode.setMenuName("RSA_3단계_1");
								childchildchildSiteMenuTreeNode.setLinkURL("/servlet/rsa_threeDepth_1");
								
								childchildSiteMenuTreeNode.addChildSiteMenuNode(childchildchildSiteMenuTreeNode);
							}
							
							childSiteMenuTreeNode.addChildSiteMenuNode(childchildSiteMenuTreeNode);
						}
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("메세지 다이제스트(MD) 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JSMessageDigestInput");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("대칭키 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JSSymmetricKeyInput");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("에코 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/Echo");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("모든 데이터 타입 검증");
						childSiteMenuTreeNode.setLinkURL("/servlet/UserLoginInput");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("자바 문자열 변환 도구");
						childSiteMenuTreeNode.setLinkURL("/servlet/JavaStringConverterInput");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}
				
				{
					SiteMenuTreeNode rootSiteMenuTreeNode = new SiteMenuTreeNode();
					
					rootSiteMenuTreeNode.setMenuName("회원");
					rootSiteMenuTreeNode.setLinkURL("/jsp/member/body.jsp");
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("로그인");
						childSiteMenuTreeNode.setLinkURL("/servlet/UserLoginInput");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("회원 가입");
						childSiteMenuTreeNode.setLinkURL("/servlet/UserSiteMembershipInput");
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}
				
				return siteMenuTree;
			}
		}
		
		VirtualSiteMenuTreeBuilderIF virtualSiteMenuTreeBuilder = new VirtualSiteMenuTreeBuilder();		
		SiteMenuTree siteMenuTree = virtualSiteMenuTreeBuilder.build();
		siteMenuTree.makeDBRecord(TEST_DBCP_NAME);
		
		SiteMenuTreeNode sourceSiteMenuTreeNode = siteMenuTree.find("세션키 테스트");
		if (null == sourceSiteMenuTreeNode) {
			fail("하단 이동할 대상 메뉴[세션키 테스트] 찾기 실패");
		}
		
		SiteMenuTreeNode targetSiteMenuTreeNode = siteMenuTree.find("RSA 테스트");
		
		if (null == targetSiteMenuTreeNode) {
			fail("하단 이동할 위치에 있는 메뉴[RSA 테스트] 찾기 실패");
		}
		
		MenuMoveDownReq menuDownMoveReq = new MenuMoveDownReq();
		menuDownMoveReq.setMenuNo(sourceSiteMenuTreeNode.getMenuNo());
		
		try {
			MessageResultRes messageResultRes = menuDownMoveReqServerTask.doWork(TEST_DBCP_NAME, menuDownMoveReq);
			if (! messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		ArraySiteMenuReq arraySiteMenuReq = new ArraySiteMenuReq();
		ArraySiteMenuRes arraySiteMenuRes = null;
		
		try {
			arraySiteMenuRes = arraySiteMenuReqServerTask.doWork(TEST_DBCP_NAME, arraySiteMenuReq);
		} catch (Exception e) {
			String errorMessage = "배열형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}
		
		for (ArraySiteMenuRes.Menu siteMenu : arraySiteMenuRes.getMenuList()) {
			String menuName = siteMenu.getMenuName();			
			
			if (menuName.equals("RSA 테스트")) {
				assertEquals("메뉴 순서 비교", sourceSiteMenuTreeNode.getOrderSeq(), siteMenu.getOrderSeq());				
			} else if (menuName.equals("RSA_2단계_1")) {	
				assertEquals("메뉴 순서 비교", sourceSiteMenuTreeNode.getOrderSeq()+1, siteMenu.getOrderSeq());
			} else if (menuName.equals("RSA_2단계_2")) {
				assertEquals("메뉴 순서 비교", sourceSiteMenuTreeNode.getOrderSeq()+2, siteMenu.getOrderSeq());
			} else if (menuName.equals("RSA_3단계_1")) {	
				assertEquals("메뉴 순서 비교", sourceSiteMenuTreeNode.getOrderSeq()+3, siteMenu.getOrderSeq());
			} else if (menuName.equals("세션키 테스트")) {
				assertEquals("메뉴 순서 비교", targetSiteMenuTreeNode.getOrderSeq(), siteMenu.getOrderSeq());
			} else if (menuName.equals("세션키_2단계_1")) {
				assertEquals("메뉴 순서 비교", targetSiteMenuTreeNode.getOrderSeq()+1, siteMenu.getOrderSeq());
			} else if (menuName.equals("세션키_3단계_1")) {
				assertEquals("메뉴 순서 비교", targetSiteMenuTreeNode.getOrderSeq()+2, siteMenu.getOrderSeq());
			} else if (menuName.equals("세션키_2단계_2")) {
				assertEquals("메뉴 순서 비교", targetSiteMenuTreeNode.getOrderSeq()+3, siteMenu.getOrderSeq());
			} else {
				SiteMenuTreeNode workingSiteMenuTreeNode = siteMenuTree.find(menuName);
				if (null == workingSiteMenuTreeNode) {
					fail("메뉴["+menuName+"] 찾기 실패");
				}
				assertEquals("메뉴 순서 비교", workingSiteMenuTreeNode.getOrderSeq(), siteMenu.getOrderSeq());
			}
		}
		
		MenuMoveUpReq menuUpMoveReq = new MenuMoveUpReq();
		menuUpMoveReq.setMenuNo(sourceSiteMenuTreeNode.getMenuNo());
		
		try {
			MessageResultRes messageResultRes = menuUpMoveReqServerTask.doWork(TEST_DBCP_NAME, menuUpMoveReq);
			if (! messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		try {
			arraySiteMenuRes = arraySiteMenuReqServerTask.doWork(TEST_DBCP_NAME, arraySiteMenuReq);
		} catch (Exception e) {
			String errorMessage = "배열형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}
		
		for (ArraySiteMenuRes.Menu siteMenu : arraySiteMenuRes.getMenuList()) {
			String menuName = siteMenu.getMenuName();			
			
			SiteMenuTreeNode workingSiteMenuTreeNode = siteMenuTree.find(menuName);
			if (null == workingSiteMenuTreeNode) {
				fail("메뉴["+menuName+"] 찾기 실패");
			}
			assertEquals("메뉴 순서 비교", workingSiteMenuTreeNode.getOrderSeq(), siteMenu.getOrderSeq());
		}
	}
	
	@Test 
	public void 루트메뉴등록테스트_255개초과() {
		/*ch.qos.logback.classic.Logger logger = (Logger)LoggerFactory.getLogger("org.jooq");
		Level oldLogLevel = logger.getLevel(); 
		logger.setLevel(Level.OFF);*/
		
		RootMenuAddReqServerTask rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		
		for (int i=0; i < CommonStaticFinalVars.UNSIGNED_BYTE_MAX; i++) {
			RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
			rootMenuAddReq.setMenuName("temp"+i);
			rootMenuAddReq.setLinkURL("/temp"+i);
			try {
				rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, rootMenuAddReq);
			} catch (Exception e) {
				String errorMessage = new StringBuilder()
						.append("fail to add ")
						.append(i+1)
						.append("th root menu").toString();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}
		
		//logger.setLevel(oldLogLevel);
		
		
		RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
		rootMenuAddReq.setMenuName("temp255");
		rootMenuAddReq.setLinkURL("/temp255");
		try {
			rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, rootMenuAddReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String expectedMessage = "메뉴 갯수가 최대치(=255)에 도달하여 더 이상 추가할 수 없습니다";
			log.warn(e.getMessage(), e);
			
			assertEquals(expectedMessage, e.getMessage());
			
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("알수 없는 에러가 발생하여 메뉴 최대치 초과 테스트 실패");
		}
		
		//logger.setLevel(Level.OFF);
	}
	
	@Test 
	public void 자식메뉴추가테스트_255개초과() {
		ch.qos.logback.classic.Logger logger = (Logger)LoggerFactory.getLogger("org.jooq");
		Level oldLogLevel = logger.getLevel(); 
		logger.setLevel(Level.OFF);
		
		RootMenuAddReqServerTask rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		
		for (int i=0; (i+1) < CommonStaticFinalVars.UNSIGNED_BYTE_MAX; i++) {
			
			
			RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
			rootMenuAddReq.setMenuName("temp"+i);
			rootMenuAddReq.setLinkURL("/temp"+i);
			try {
				rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, rootMenuAddReq);
			} catch (Exception e) {
				String errorMessage = new StringBuilder()
						.append("fail to add ")
						.append(i+1)
						.append("th root menu").toString();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}
		
		logger.setLevel(oldLogLevel);
		
		RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
		rootMenuAddReq.setMenuName("temp254");
		rootMenuAddReq.setLinkURL("/temp254");
		
		RootMenuAddRes rootMenuAddRes = new RootMenuAddRes();
		try {
			rootMenuAddRes = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, rootMenuAddReq);			
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("fail to add ")
					.append(255)
					.append("th root menu").toString();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
		
		ChildMenuAddReqServerTask childMenuAddReqServerTask = new ChildMenuAddReqServerTask();
		
		ChildMenuAddReq firstChildMenuAddReq = new ChildMenuAddReq();
		firstChildMenuAddReq.setParentNo(rootMenuAddRes.getMenuNo());
		firstChildMenuAddReq.setMenuName("temp254_1");
		firstChildMenuAddReq.setLinkURL("/temp254_1");
		
		try {
			childMenuAddReqServerTask.doWork(TEST_DBCP_NAME, firstChildMenuAddReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String expectedMessage = "메뉴 갯수가 최대치(=255)에 도달하여 더 이상 추가할 수 없습니다";
			log.warn(e.getMessage(), e);
			
			assertEquals(expectedMessage, e.getMessage());
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("알수 없는 에러가 발생하여 메뉴 최대치 초과 테스트 실패");
		}		
		
		logger.setLevel(Level.OFF);
	}
	
	
	@Test 
	public void 자식메뉴추가테스트_부모없음() {
		final long parentMenuNo = 10;
		
		ChildMenuAddReqServerTask childMenuAddReqServerTask = new ChildMenuAddReqServerTask();
		
		ChildMenuAddReq firstChildMenuAddReq = new ChildMenuAddReq();
		firstChildMenuAddReq.setParentNo(parentMenuNo);
		firstChildMenuAddReq.setMenuName("tempNoParent_1");
		firstChildMenuAddReq.setLinkURL("/tempNoParent_1");
		
		try {
			childMenuAddReqServerTask.doWork(TEST_DBCP_NAME, firstChildMenuAddReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String expectedMessage = new StringBuilder()
					.append("부모 메뉴[")
					.append(parentMenuNo)
					.append("]가 존재하지 않습니다")
					.toString();
			log.warn(e.getMessage(), e);
			
			assertEquals(expectedMessage, e.getMessage());
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("알수 없는 에러가 발생하여 메뉴 최대치 초과 테스트 실패");
		}		
	}
	
	@Test 
	public void 메뉴삭제테스트_2개루트메뉴등록후1개만삭제한경우() {   
		RootMenuAddReqServerTask rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		
		RootMenuAddReq rootMenuAddReqForDelete = new RootMenuAddReq();
		rootMenuAddReqForDelete.setMenuName("temp1");
		rootMenuAddReqForDelete.setLinkURL("/temp01");
		
		RootMenuAddRes rootMenuAddResForDelete = null;
		try {
			rootMenuAddResForDelete  = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, rootMenuAddReqForDelete);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'RootMenuAddRes'");
		}
		
		
		RootMenuAddReq rootMenuAddReqForSpace = new RootMenuAddReq();
		rootMenuAddReqForSpace.setMenuName("temp2");
		rootMenuAddReqForSpace.setLinkURL("/temp02");
		
		RootMenuAddRes rootMenuAddResForSpace = null;
		try {
			rootMenuAddResForSpace  = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, rootMenuAddReqForSpace);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'RootMenuAddRes'");
		}
		
		MenuDeleteReqServerTask menuDeleteReqServerTask = new MenuDeleteReqServerTask();
		
		MenuDeleteReq menuDeleteReq = new MenuDeleteReq();
		menuDeleteReq.setMenuNo(rootMenuAddResForDelete.getMenuNo());
		
		MessageResultRes messageResultRes = null;
		try {
			messageResultRes = menuDeleteReqServerTask.doWork(TEST_DBCP_NAME, menuDeleteReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		if (! messageResultRes.getIsSuccess()) {
			fail(messageResultRes.getResultMessage());
		}		
		
		ArraySiteMenuReqServerTask arraySiteMenuReqServerTask = new ArraySiteMenuReqServerTask();
		ArraySiteMenuReq arraySiteMenuReq = new ArraySiteMenuReq();
		try {
			ArraySiteMenuRes arraySiteMenuRes = arraySiteMenuReqServerTask.doWork(TEST_DBCP_NAME, arraySiteMenuReq);
			
			if (arraySiteMenuRes.getCnt() != 1) {
				log.info(arraySiteMenuRes.toString());
				fail("메뉴 삭제 실패하였습니다");
			}
			
			for (ArraySiteMenuRes.Menu menu : arraySiteMenuRes.getMenuList()) {
				if (menu.getMenuNo() != rootMenuAddResForSpace.getMenuNo()) {
					fail("메뉴 삭제후 남은 메뉴 번호와 목록에서 얻은 메뉴 번호가 다릅니다");
				}
			}
			
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail("unknown error");
		}
	}
	
	
	
	/**
	 * 메뉴 테이블 초기 상태 즉 메뉴가 하나도 없는 상태에서 삭제 테스트
	 */
	@Test 
	public void 메뉴삭제테스트_삭제할대상메뉴없는경우() {		
		MenuDeleteReqServerTask menuDeleteReqServerTask = new MenuDeleteReqServerTask();
		
		MenuDeleteReq menuDeleteReq = new MenuDeleteReq();
		menuDeleteReq.setMenuNo(10);
		
		try {
			menuDeleteReqServerTask.doWork(TEST_DBCP_NAME, menuDeleteReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String expectedMessage = new StringBuilder()
					.append("삭제할 메뉴[")
					.append(menuDeleteReq.getMenuNo())
					.append("]가 존재하지 않습니다").toString();
			
			assertEquals(expectedMessage, e.getMessage());
		} catch (Exception e) {
			log.warn("error", e);
			fail("알수 없는 에러가 발생하여 삭제 대상이 없는 삭제 테스트 실패");
		}
	}
	
	@Test 
	public void 메뉴삭제테스트_자식이있는메뉴삭제할경우() {		
		RootMenuAddReqServerTask rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		
		RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
		rootMenuAddReq.setMenuName("temp1");
		rootMenuAddReq.setLinkURL("/temp01");
		
		RootMenuAddRes rootMenuAddRes = null;
		try {
			rootMenuAddRes  = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, rootMenuAddReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'RootMenuAddRes'");
		}
		
		ChildMenuAddReqServerTask childMenuAddReqServerTask = new ChildMenuAddReqServerTask();
		ChildMenuAddReq childMenuAddReq = new ChildMenuAddReq();
		childMenuAddReq.setParentNo(rootMenuAddRes.getMenuNo());
		childMenuAddReq.setMenuName("temp1_1");
		childMenuAddReq.setLinkURL("/temp01_1");
		
		ChildMenuAddRes childMenuAddRes = null;
		try {
			childMenuAddRes  = childMenuAddReqServerTask.doWork(TEST_DBCP_NAME, childMenuAddReq);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'RootMenuAddRes'");
		}		
		
		MenuDeleteReqServerTask menuDeleteReqServerTask = new MenuDeleteReqServerTask();
		
		MenuDeleteReq menuDeleteReq = new MenuDeleteReq();
		menuDeleteReq.setMenuNo(rootMenuAddRes.getMenuNo());
		
		MessageResultRes messageResultRes = null;
		try {
			messageResultRes = menuDeleteReqServerTask.doWork(TEST_DBCP_NAME, menuDeleteReq);
			
			fail("no ServerServiceException");
		} catch(ServerServiceException e) {
			log.info(e.getMessage(), e);
			
			String expectedErrorMessage = new StringBuilder()
					.append("자식이 있는 메뉴[")
					.append(menuDeleteReq.getMenuNo())
					.append("]는 삭제 할 수 없습니다").toString();
			
			String acutalErrorMessage = e.getMessage();
			
			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		menuDeleteReq.setMenuNo(childMenuAddRes.getMenuNo());
		
		try {
			messageResultRes = menuDeleteReqServerTask.doWork(TEST_DBCP_NAME, menuDeleteReq);
			
			if (! messageResultRes.getIsSuccess()) {
				fail("테스트용 자식 메뉴 삭제 실패");
			}
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		menuDeleteReq.setMenuNo(rootMenuAddRes.getMenuNo());
		
		try {
			messageResultRes = menuDeleteReqServerTask.doWork(TEST_DBCP_NAME, menuDeleteReq);
			
			if (! messageResultRes.getIsSuccess()) {
				fail("테스트용 루트 메뉴 삭제 실패");
			}
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("unknown error");
		}		
	}
}
