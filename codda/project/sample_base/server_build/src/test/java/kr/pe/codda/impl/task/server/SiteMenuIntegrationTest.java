package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbSeqTb.SB_SEQ_TB;
import static kr.pe.codda.impl.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record;
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

/**
 * 사이트 메뉴 통합 테스트
 * @author Won Jonghoon
 *
 */
public class SiteMenuIntegrationTest extends AbstractJunitTest {
	private final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;
	private static UInteger backupMenuNo = null;
	private static ArraySiteMenuRes backupArraySiteMenuRes = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();
		ServerDBUtil.initializeDBEnvoroment(TEST_DBCP_NAME);		
	}
	
	@Before
	public void setUp() {
		UByte menuSequenceID = UByte.valueOf(SequenceType.MENU.getSequenceID());
		
		ArraySiteMenuReq arraySiteMenuReq = new ArraySiteMenuReq();

		ArraySiteMenuReqServerTask arraySiteMenuReqServerTask = new ArraySiteMenuReqServerTask();

		try {

			backupArraySiteMenuRes = arraySiteMenuReqServerTask.doWork(TEST_DBCP_NAME, arraySiteMenuReq);

		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			fail("unknown error");
		}
		
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

			Record menuSeqRecord = create.select(SB_SEQ_TB.SQ_VALUE).from(SB_SEQ_TB)
					.where(SB_SEQ_TB.SQ_ID.eq(menuSequenceID)).forUpdate().fetchOne();

			if (null == menuSeqRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("메뉴 시퀀스 식별자[").append(menuSequenceID)
						.append("]의 시퀀스를 가져오는데 실패하였습니다").toString();

				log.warn(errorMessage);

				fail(errorMessage);
			}

			create.update(SB_SEQ_TB).set(SB_SEQ_TB.SQ_VALUE, UInteger.valueOf(1))
					.where(SB_SEQ_TB.SQ_ID.eq(menuSequenceID)).execute();
			
			create.delete(SB_SITEMENU_TB).execute();

			conn.commit();	
			
			backupMenuNo = menuSeqRecord.getValue(SB_SEQ_TB.SQ_VALUE);

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

			int menuSequenceUpdateCount = create.update(SB_SEQ_TB).set(SB_SEQ_TB.SQ_VALUE, backupMenuNo)
					.where(SB_SEQ_TB.SQ_ID.eq(menuSequenceID)).execute();

			if (0 == menuSequenceUpdateCount) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = "메뉴 시퀀스를 복구하는데 실패하였습니다";
				log.warn(errorMessage);
				fail(errorMessage);
			}

			create.delete(SB_SITEMENU_TB).execute();

			for (ArraySiteMenuRes.Menu menu : backupArraySiteMenuRes.getMenuList()) {
				int menuInsertCount = create.insertInto(SB_SITEMENU_TB)
						.set(SB_SITEMENU_TB.MENU_NO, UInteger.valueOf(menu.getMenuNo()))
						.set(SB_SITEMENU_TB.PARENT_NO, UInteger.valueOf(menu.getParentNo()))
						.set(SB_SITEMENU_TB.DEPTH, UByte.valueOf(menu.getDepth()))
						.set(SB_SITEMENU_TB.ORDER_SQ, UByte.valueOf(menu.getOrderSeq()))
						.set(SB_SITEMENU_TB.MENU_NM, menu.getMenuName()).set(SB_SITEMENU_TB.LINK_URL, menu.getLinkURL())
						.execute();

				if (0 == menuInsertCount) {
					try {
						conn.rollback();
					} catch (Exception e1) {
						log.warn("fail to rollback");
					}

					String errorMessage = new StringBuilder().append("백업한 메뉴[").append(menu.getMenuNo())
							.append("]를 저장하지 못했습니다, 전체 백업 메뉴=").append(backupArraySiteMenuRes.toString()).toString();
					log.warn(errorMessage);
					fail(errorMessage);
				}
			}

			conn.commit();

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
	
	/**
	 * WARNING! 메뉴 이동 테스트를 위한 메뉴 구성이므로 수정시 주의 요망. 
	 * 메뉴 이동 테스트시 대상 메뉴는 자신을 루트로 한 트리 단위로 이동해야 한다.
	 * 하여 '세션키 테스트' 와 'RSA 테스트' 에 손자 메뉴를 추가하였다.
	 *  
	 * @return 메뉴 이동 테스트를 위한 {@link TreeSiteMenuRes} 를 반환한다.   
	 */
	private TreeSiteMenuRes getTreeSiteMenuResForMiddleMenuMovement() {
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
					childMenu.setLinkURL("/servlet/JSRSATest");
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
	
	private TreeSiteMenuRes getTreeSiteMenuResForBottomMenuMovement() {
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
					childMenu.setLinkURL("/servlet/JSRSATest");
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
	}
	
	/**
	 * 지정한 '부모 메뉴'의 자식 메뉴들을 전위순회(Pre-order) 하면서 추가한다. 
	 *  
	 * @param childMenuAddReqServerTask 자식 메뉴 추가 서버 타스크
	 * @param parnetMenu 전위순회(Pre-order) 대상 ROOT 노드인 '부모 메뉴', WARNING! '부모 메뉴'는 DB 에 미리 반영 되어 있어야 하며 부모 메뉴의 '메뉴 번호'는  또한 반듯이 DB 에 넣어진 '메뉴 번호' 값이어야 한다.
	 */
	private void addMenuUsingPreOrderTraversal(ChildMenuAddReqServerTask childMenuAddReqServerTask,
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
	}
	
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
	
	/**
	 * 이 테스트는 메뉴 트리를 전위순회(Pre-order)하면서 메뉴를 추가를 하는 테스트 시나리오이다. 이 테스트에 사용되는 메뉴는 메뉴 트리 깊이가  최대 1인 2018년 8월 21일 기준 메뉴 구성을 바탕으로 작성되었다.
	 */
	@Test 
	public void 사이트메뉴테스트시나리오_메뉴트리를전위순회하면서메뉴추가() {
		ArraySiteMenuReqServerTask arraySiteMenuReqServerTask = new ArraySiteMenuReqServerTask();
		TreeSiteMenuReqServerTask treeSiteMenuReqServerTask = new TreeSiteMenuReqServerTask();
		RootMenuAddReqServerTask rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		ChildMenuAddReqServerTask childMenuAddReqServerTask = new ChildMenuAddReqServerTask();

		TreeSiteMenuRes expectedTreeSiteMenuRes = getTreeSiteMenuResForTestScenarioNo1();
		List<TreeSiteMenuRes.Menu> expectedRootMenuList = expectedTreeSiteMenuRes.getRootMenuList();

		for (TreeSiteMenuRes.Menu expectedRootMenu : expectedRootMenuList) {
			RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
			rootMenuAddReq.setMenuName(expectedRootMenu.getMenuName());
			rootMenuAddReq.setLinkURL(expectedRootMenu.getLinkURL());

			try {
				RootMenuAddRes rootMenuAddRes = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, rootMenuAddReq);

				expectedRootMenu.setMenuNo(rootMenuAddRes.getMenuNo());
				
				addMenuUsingPreOrderTraversal(childMenuAddReqServerTask, expectedRootMenu);
			} catch (Exception e) {
				String errorMessage = new StringBuilder().append("루트 메뉴[").append(expectedRootMenu.getMenuName())
						.append("] 추가 실패").toString();

				log.warn(errorMessage, e);

				fail(errorMessage);
			}
		}

		TreeSiteMenuReq treeSiteMenuReq = new TreeSiteMenuReq();
		TreeSiteMenuRes acutalTreeSiteMenuRes = null;
		try {
			acutalTreeSiteMenuRes = treeSiteMenuReqServerTask.doWork(TEST_DBCP_NAME, treeSiteMenuReq);
		} catch (Exception e) {
			String errorMessage = "트리형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}

		log.info("expected::{}", expectedTreeSiteMenuRes.toString());
		log.info("acutal::{}", acutalTreeSiteMenuRes.toString());

		if (acutalTreeSiteMenuRes.getRootMenuListSize() != expectedRootMenuList.size()) {
			fail("루트 메뉴 갯수 틀림");
		}

		boolean result = compareMenuList(expectedRootMenuList,
				acutalTreeSiteMenuRes.getRootMenuList());

		if (!result) {
			fail("기대한 것과 다릅니다");
		}
		
		ArraySiteMenuReq arraySiteMenuReq = new ArraySiteMenuReq();
		try {
			ArraySiteMenuRes  arraySiteMenuRes = arraySiteMenuReqServerTask.doWork(TEST_DBCP_NAME, arraySiteMenuReq);
			
			List<ArraySiteMenuRes.Menu> menuList = arraySiteMenuRes.getMenuList();
			for (int i=0; i < arraySiteMenuRes.getCnt(); i++) {
				ArraySiteMenuRes.Menu menu = menuList.get(i);
				if (menu.getOrderSeq() != i) {
					String errorMessage = new StringBuilder()
							.append("메뉴 배열에서 인덱스가 ")
							.append(i)
							.append(" 인 메뉴[")
							.append(menu.getMenuName())
							.append("의 '메뉴 순서'[")
							.append(menu.getOrderSeq())
							.append("]가 배열 인덱스 값과 일치하지않습니다").toString();
					fail(errorMessage);
				}
				
				if (menu.getMenuNo() != (i+1)) {
					String errorMessage = new StringBuilder()
							.append("메뉴 배열에서 인덱스가 ")
							.append(i)
							.append(" 인 메뉴[")
							.append(menu.getMenuName())
							.append("의 '메뉴 번호'[")
							.append(menu.getMenuNo())
							.append("]가 배열 인덱스 값에 하나 더한 값과 일치하지않습니다").toString();
					fail(errorMessage);
				}
			}
		} catch (Exception e) {
			String errorMessage = "배열형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}
	}
	
	
	/**
	 * 이 테스트는 메뉴 깊이가 0인 루트 메뉴부터 차례로 메뉴 추가한후 루트 메뉴의 자식 메뉴들은 전위순회(Pre-order)하면서 추가하는 테스트 시니리오이다. 이 테스트에 사용되는 메뉴는 메뉴 트리 깊이가  최대 1인 2018년 8월 21일 기준 메뉴 구성을 바탕으로 작성되었다. 
	 */
	@Test
	public void 사이트메뉴테스트시나리오_루트메뉴부터추가완료후루트의자식들은전위순회하면서추가() {
		ArraySiteMenuReqServerTask arraySiteMenuReqServerTask = new ArraySiteMenuReqServerTask();
		TreeSiteMenuReqServerTask treeSiteMenuReqServerTask = new TreeSiteMenuReqServerTask();
		RootMenuAddReqServerTask rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		ChildMenuAddReqServerTask childMenuAddReqServerTask = new ChildMenuAddReqServerTask();

		TreeSiteMenuRes expectedTreeSiteMenuRes = getTreeSiteMenuResForTestScenarioNo1();
		List<TreeSiteMenuRes.Menu> expectedRootMenuList = expectedTreeSiteMenuRes.getRootMenuList();

		for (TreeSiteMenuRes.Menu expectedRootMenu : expectedRootMenuList) {
			RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
			rootMenuAddReq.setMenuName(expectedRootMenu.getMenuName());
			rootMenuAddReq.setLinkURL(expectedRootMenu.getLinkURL());

			try {
				RootMenuAddRes rootMenuAddRes = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, rootMenuAddReq);

				expectedRootMenu.setMenuNo(rootMenuAddRes.getMenuNo());
			} catch (Exception e) {
				String errorMessage = new StringBuilder().append("루트 메뉴[").append(expectedRootMenu.getMenuName())
						.append("] 추가 실패").toString();

				log.warn(errorMessage, e);

				fail(errorMessage);
			}
		}

		for (TreeSiteMenuRes.Menu expectedRootMenu : expectedRootMenuList) {
			addMenuUsingPreOrderTraversal(childMenuAddReqServerTask, expectedRootMenu);
		}

		TreeSiteMenuReq treeSiteMenuReq = new TreeSiteMenuReq();
		TreeSiteMenuRes acutalTreeSiteMenuRes = null;
		try {
			acutalTreeSiteMenuRes = treeSiteMenuReqServerTask.doWork(TEST_DBCP_NAME, treeSiteMenuReq);
		} catch (Exception e) {
			String errorMessage = "트리형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}

		log.info("expected::{}", expectedTreeSiteMenuRes.toString());
		log.info("acutal::{}", acutalTreeSiteMenuRes.toString());

		if (acutalTreeSiteMenuRes.getRootMenuListSize() != expectedRootMenuList.size()) {
			fail("루트 메뉴 갯수 틀림");
		}

		boolean result = compareMenuList(expectedTreeSiteMenuRes.getRootMenuList(),
				acutalTreeSiteMenuRes.getRootMenuList());

		if (!result) {
			fail("기대한 것과 다릅니다");
		}
		
		ArraySiteMenuReq arraySiteMenuReq = new ArraySiteMenuReq();
		try {
			ArraySiteMenuRes  arraySiteMenuRes = arraySiteMenuReqServerTask.doWork(TEST_DBCP_NAME, arraySiteMenuReq);
			
			List<ArraySiteMenuRes.Menu> menuList = arraySiteMenuRes.getMenuList();
			for (int i=0; i < arraySiteMenuRes.getCnt(); i++) {
				ArraySiteMenuRes.Menu menu = menuList.get(i);
				if (menu.getOrderSeq() != i) {
					String errorMessage = new StringBuilder()
							.append("메뉴 배열에서 인덱스가 ")
							.append(i)
							.append(" 인 메뉴[")
							.append(menu.getMenuName())
							.append("의 순서[")
							.append(menu.getOrderSeq())
							.append("]가 배열 인덱스 값과 일치하지않습니다").toString();
					fail(errorMessage);
				}
			}
		} catch (Exception e) {
			String errorMessage = "배열형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}
	}

	
	
	@Test 
	public void 메뉴이동테스트_상단이동후다시하단이동하여원복() {
		/**
		 * WARNING! 메뉴 이동 테스트 대상 메뉴는 메뉴 깊이 3을 갖는 '세션키 테스트' 와  'RSA 테스트' 이다.
		 * 입력한 메뉴 순서는  '세션키 테스트' 이고 다음이 'RSA 테스트' 이다.
		 * 메뉴 트리를 전위 순회 하면서 메뉴를 추가하므로 상단 이동할 테스트 대상 메뉴 'RSA 테스트'의 메뉴 번호는 14번이 되며
		 * 상단 이동할 대상 메뉴는 '세션키 테스트' 로 메뉴 번호는 9번이 된다.
		 */
		final long menuNoForMoveUpDownTest = 14L;		
		
		TreeSiteMenuReqServerTask treeSiteMenuReqServerTask = new TreeSiteMenuReqServerTask();
		RootMenuAddReqServerTask rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		ChildMenuAddReqServerTask childMenuAddReqServerTask = new ChildMenuAddReqServerTask();
		MenuMoveUpReqServerTask menuUpMoveReqServerTask = new MenuMoveUpReqServerTask();
		MenuMoveDownReqServerTask menuDownMoveReqServerTask = new MenuMoveDownReqServerTask();

		TreeSiteMenuRes expectedTreeSiteMenuRes = getTreeSiteMenuResForMiddleMenuMovement();

		
		List<TreeSiteMenuRes.Menu> expectedRootMenuList = expectedTreeSiteMenuRes.getRootMenuList();
		for (TreeSiteMenuRes.Menu expectedRootMenu : expectedRootMenuList) {
			RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
			rootMenuAddReq.setMenuName(expectedRootMenu.getMenuName());
			rootMenuAddReq.setLinkURL(expectedRootMenu.getLinkURL());

			try {
				RootMenuAddRes rootMenuAddRes = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, rootMenuAddReq);

				expectedRootMenu.setMenuNo(rootMenuAddRes.getMenuNo());
				
				addMenuUsingPreOrderTraversal(childMenuAddReqServerTask, expectedRootMenu);
			} catch (Exception e) {
				String errorMessage = new StringBuilder().append("루트 메뉴[").append(expectedRootMenu.getMenuName())
						.append("] 추가 실패").toString();

				log.warn(errorMessage, e);

				fail(errorMessage);
			}
		}
		
		TreeSiteMenuReq treeSiteMenuReq = new TreeSiteMenuReq();
		TreeSiteMenuRes acutalTreeSiteMenuResBeforeMoveUp = null;
		try {
			acutalTreeSiteMenuResBeforeMoveUp = treeSiteMenuReqServerTask.doWork(TEST_DBCP_NAME, treeSiteMenuReq);
		} catch (Exception e) {
			String errorMessage = "트리형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}
		
		log.info("BeforeMoveUp::{}", acutalTreeSiteMenuResBeforeMoveUp.toString());
		/*, menu[2]=Menu[menuNo=9, parentNo=6, depth=1, orderSeq=9, menuName=세션키 테스트, linkURL=/servlet/JDFSessionKeyTest, 
	childMenuListSize=2, childMenuList=[
	menu[0]=Menu[menuNo=10, parentNo=9, depth=2, orderSeq=10, menuName=세션키_2단계_1, linkURL=/servlet/sessionKey_twoDepth_1, 
		childMenuListSize=1, childMenuList=[
		menu[0]=Menu[menuNo=11, parentNo=10, depth=3, orderSeq=11, menuName=세션키_3단계_1, linkURL=/servlet/sessionKey_threeDepth_1, childMenuListSize=0, childMenuList=empty]]
	, menu[1]=Menu[menuNo=12, parentNo=9, depth=2, orderSeq=12, menuName=세션키_2단계_2, linkURL=/servlet/sessionKey_twoDepth_2, childMenuListSize=0, childMenuList=empty]]
, menu[3]=Menu[menuNo=13, parentNo=6, depth=1, orderSeq=13, menuName=RSA 테스트, linkURL=/servlet/JSRSATest, 
	childMenuListSize=2, childMenuList=[
	menu[0]=Menu[menuNo=14, parentNo=13, depth=2, orderSeq=14, menuName=RSA_2단계_1, linkURL=/servlet/rsa_twoDepth_1, childMenuListSize=0, childMenuList=empty]
	, menu[1]=Menu[menuNo=15, parentNo=13, depth=2, orderSeq=15, menuName=RSA_2단계_2, linkURL=/servlet/rsa_twoDepth_2, 
		childMenuListSize=1, childMenuList=[
		menu[0]=Menu[menuNo=16, parentNo=15, depth=3, orderSeq=16, menuName=RSA_3단계_1, linkURL=/servlet/rsa_threeDepth_1, childMenuListSize=0, childMenuList=empty]]] */

		MenuMoveUpReq menuUpMoveReq = new MenuMoveUpReq();
		menuUpMoveReq.setMenuNo(menuNoForMoveUpDownTest);
		
		try {
			MessageResultRes messageResultRes = menuUpMoveReqServerTask.doWork(TEST_DBCP_NAME, menuUpMoveReq);
			if (! messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		TreeSiteMenuRes acutalTreeSiteMenuResAfterMoveUp = null;
		try {
			acutalTreeSiteMenuResAfterMoveUp = treeSiteMenuReqServerTask.doWork(TEST_DBCP_NAME, treeSiteMenuReq);
		} catch (Exception e) {
			String errorMessage = "트리형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}
		
		
		log.info("AfterMoveUp::{}", acutalTreeSiteMenuResAfterMoveUp.toString());
		

		boolean result = compareMenuList(acutalTreeSiteMenuResBeforeMoveUp.getRootMenuList(),
				acutalTreeSiteMenuResAfterMoveUp.getRootMenuList());

		if (result) {
			fail("상단 이동후 결과와 상단 이동전 결과가 같습니다");
		}
		
		MenuMoveDownReq menuDownMoveReq = new MenuMoveDownReq();
		menuDownMoveReq.setMenuNo(menuNoForMoveUpDownTest);
		
		try {
			MessageResultRes messageResultRes = menuDownMoveReqServerTask.doWork(TEST_DBCP_NAME, menuDownMoveReq);
			if (! messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		TreeSiteMenuRes acutalTreeSiteMenuResAfterMoveDown = null;
		try {
			acutalTreeSiteMenuResAfterMoveDown = treeSiteMenuReqServerTask.doWork(TEST_DBCP_NAME, treeSiteMenuReq);
		} catch (Exception e) {
			String errorMessage = "트리형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}
		
		log.info("AfterMoveDown::{}", acutalTreeSiteMenuResAfterMoveDown.toString());
		
		if (! acutalTreeSiteMenuResBeforeMoveUp.toString().equals(acutalTreeSiteMenuResAfterMoveDown.toString())) {
			fail("2.상단 이동전 결과와 상단 이동후 다시 하단 이동후 얻은 결과 즉 원복한 결과와 다릅니다");
		}
	}
	
	@Test 
	public void 메뉴이동테스트_하단이동후다시상단이동하여원복() {
		// FIXME!
		
		final long menuNoForMoveUpDownTest = 10L;		
		
		TreeSiteMenuReqServerTask treeSiteMenuReqServerTask = new TreeSiteMenuReqServerTask();
		RootMenuAddReqServerTask rootMenuAddReqServerTask = new RootMenuAddReqServerTask();
		ChildMenuAddReqServerTask childMenuAddReqServerTask = new ChildMenuAddReqServerTask();
		MenuMoveUpReqServerTask menuUpMoveReqServerTask = new MenuMoveUpReqServerTask();
		MenuMoveDownReqServerTask menuDownMoveReqServerTask = new MenuMoveDownReqServerTask();

		TreeSiteMenuRes expectedTreeSiteMenuRes = getTreeSiteMenuResForBottomMenuMovement();

		
		List<TreeSiteMenuRes.Menu> expectedRootMenuList = expectedTreeSiteMenuRes.getRootMenuList();
		for (TreeSiteMenuRes.Menu expectedRootMenu : expectedRootMenuList) {
			RootMenuAddReq rootMenuAddReq = new RootMenuAddReq();
			rootMenuAddReq.setMenuName(expectedRootMenu.getMenuName());
			rootMenuAddReq.setLinkURL(expectedRootMenu.getLinkURL());

			try {
				RootMenuAddRes rootMenuAddRes = rootMenuAddReqServerTask.doWork(TEST_DBCP_NAME, rootMenuAddReq);

				expectedRootMenu.setMenuNo(rootMenuAddRes.getMenuNo());
				
				addMenuUsingPreOrderTraversal(childMenuAddReqServerTask, expectedRootMenu);
			} catch (Exception e) {
				String errorMessage = new StringBuilder().append("루트 메뉴[").append(expectedRootMenu.getMenuName())
						.append("] 추가 실패").toString();

				log.warn(errorMessage, e);

				fail(errorMessage);
			}
		}
		
		TreeSiteMenuReq treeSiteMenuReq = new TreeSiteMenuReq();
		TreeSiteMenuRes acutalTreeSiteMenuResBeforeMoveDown = null;
		try {
			acutalTreeSiteMenuResBeforeMoveDown = treeSiteMenuReqServerTask.doWork(TEST_DBCP_NAME, treeSiteMenuReq);
		} catch (Exception e) {
			String errorMessage = "트리형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}
		
		log.info("BeforeMoveDown::{}", acutalTreeSiteMenuResBeforeMoveDown.toString());
		
		MenuMoveDownReq menuDownMoveReq = new MenuMoveDownReq();
		menuDownMoveReq.setMenuNo(menuNoForMoveUpDownTest);
		
		try {
			MessageResultRes messageResultRes = menuDownMoveReqServerTask.doWork(TEST_DBCP_NAME, menuDownMoveReq);
			if (! messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		TreeSiteMenuRes acutalTreeSiteMenuResAfterMoveDown = null;
		try {
			acutalTreeSiteMenuResAfterMoveDown = treeSiteMenuReqServerTask.doWork(TEST_DBCP_NAME, treeSiteMenuReq);
		} catch (Exception e) {
			String errorMessage = "트리형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}
		
		
		log.info("AfterMoveDown::{}", acutalTreeSiteMenuResAfterMoveDown.toString());
		
		
		boolean result = compareMenuList(acutalTreeSiteMenuResBeforeMoveDown.getRootMenuList(),
				acutalTreeSiteMenuResAfterMoveDown.getRootMenuList());

		if (result) {
			fail("하단 이동후 결과와 하단 이동전 결과가 같습니다");
		}
		
		
		MenuMoveUpReq menuUpMoveReq = new MenuMoveUpReq();
		menuUpMoveReq.setMenuNo(menuNoForMoveUpDownTest);
		
		try {
			MessageResultRes messageResultRes = menuUpMoveReqServerTask.doWork(TEST_DBCP_NAME, menuUpMoveReq);
			if (! messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
		} catch (Exception e) {
			log.warn("error", e);
			fail("fail to get a output message 'MessageResultRes'");
		}
		
		TreeSiteMenuRes acutalTreeSiteMenuResAfterMoveUp = null;
		try {
			acutalTreeSiteMenuResAfterMoveUp = treeSiteMenuReqServerTask.doWork(TEST_DBCP_NAME, treeSiteMenuReq);
		} catch (Exception e) {
			String errorMessage = "트리형 사이트 목록을 가져오는데 실패";

			log.warn(errorMessage, e);

			fail(errorMessage);
		}
		
		
		log.info("AfterMoveUp::{}", acutalTreeSiteMenuResAfterMoveUp.toString());
		
		if (! acutalTreeSiteMenuResBeforeMoveDown.toString().equals(acutalTreeSiteMenuResAfterMoveUp.toString())) {
			fail("2.하단 이동전 결과와 하단 이동후 다시 하단 이동후 얻은 결과 즉 원복한 결과와 다릅니다");
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
