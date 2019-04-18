package kr.pe.codda.server.lib;

import static kr.pe.codda.jooq.tables.SbSeqTb.SB_SEQ_TB;
import static kr.pe.codda.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.DBCPDataSourceNotFoundException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.ArraySiteMenuReq.ArraySiteMenuReq;
import kr.pe.codda.impl.message.ArraySiteMenuRes.ArraySiteMenuRes;
import kr.pe.codda.impl.task.server.ArraySiteMenuReqServerTask;
import kr.pe.codda.server.dbcp.DBCPManager;

public class SiteMenuTreeTest extends AbstractJunitTest {
	
	//private final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME;
	private final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();
		ServerDBUtil.initializeDBEnvoroment(TEST_DBCP_NAME);		
	}
	
	@Before
	public void setUp() {
		// UByte freeBoardSequenceID = UByte.valueOf(SequenceType.FREE_BOARD.getSequenceID());
		
		{
			String userID = "admin";
			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
					(byte) '4', (byte) '$' };
			String nickname = "단위테스터용어드민";
			String email = "admin@codda.pe.kr";
			String ip = "127.0.0.1";

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.ADMIN, userID, nickname, email,
						passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[").append(userID).append("] 입니다")
						.toString();
				String actualErrorMessag = e.getMessage();

				// log.warn(actualErrorMessag, e);

				assertEquals(expectedErrorMessage, actualErrorMessag);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to create a test ID");
			}
		}

		{
			String userID = "test01";
			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
					(byte) '4', (byte) '$' };
			String nickname = "단위테스터용아이디1";
			String email = "test01@codda.pe.kr";
			String ip = "127.0.0.1";

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, email,
						passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[").append(userID).append("] 입니다")
						.toString();
				String actualErrorMessag = e.getMessage();

				// log.warn(actualErrorMessag, e);

				assertEquals(expectedErrorMessage, actualErrorMessag);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to create a test ID");
			}
		}

		{
			String userID = "test02";
			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
					(byte) '4', (byte) '$' };
			String nickname = "단위테스터용아이디2";
			String email = "test02@codda.pe.kr";
			String ip = "127.0.0.1";

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, email,
						passwordBytes, new java.sql.Timestamp(System.currentTimeMillis()), ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[").append(userID).append("] 입니다")
						.toString();
				String actualErrorMessag = e.getMessage();

				// log.warn(actualErrorMessag, e);

				assertEquals(expectedErrorMessage, actualErrorMessag);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to create a test ID");
			}
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
			
			create.update(SB_SEQ_TB).set(SB_SEQ_TB.SQ_VALUE, UInteger.valueOf(1))
			.where(SB_SEQ_TB.SQ_ID.eq(SequenceType.MENU.getSequenceID()))
			.execute();			
						
			create.delete(SB_SITEMENU_TB).execute();			
			
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
	
	@After
	public void tearDown(){	
	}
	

	@Test
	public void testMakeDBRecord() {
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
						
						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}
					
					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("RSA 테스트");
						childSiteMenuTreeNode.setLinkURL("/servlet/JSRSAInput");
						
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
						childSiteMenuTreeNode.setLinkURL("/servlet/AllItemType");
						
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
						childSiteMenuTreeNode.setLinkURL("/servlet/UserLoginInputInput");
						
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
		siteMenuTree.toDBRecord(TEST_DBCP_NAME);
		
		ArraySiteMenuReqServerTask arraySiteMenuReqServerTask = null;
		try {
			arraySiteMenuReqServerTask = new ArraySiteMenuReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
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
			SiteMenuTreeNode siteMenuTreeNode = siteMenuTree.find(siteMenu.getMenuName());
			if (null == siteMenuTreeNode) {
				String errorMessage = new StringBuilder()
				.append("메뉴[menuNo=")
				.append(siteMenu.getMenuNo())
				.append(", menuName=")
				.append(siteMenu.getMenuName())
						.append("] 찾기 실패").toString();
				
				fail(errorMessage);
			}
			
			assertEquals("메뉴 번호 비교", siteMenuTreeNode.getMenuNo(), siteMenu.getMenuNo());
			assertEquals("메뉴 순서 비교", siteMenuTreeNode.getOrderSeq(), siteMenu.getOrderSeq());
			assertEquals("부모 메뉴 번호 비교", siteMenuTreeNode.getParentNo(), siteMenu.getParentNo());
			assertEquals("메뉴 깊이 비교", siteMenuTreeNode.getDepth(), siteMenu.getDepth());
			assertEquals("메뉴 이름 비교", siteMenuTreeNode.getMenuName(), siteMenu.getMenuName());
			assertEquals("메뉴 링크 비교", siteMenuTreeNode.getLinkURL(), siteMenu.getLinkURL());
		}
	}
	
	@Test
	public void testMakeDBRecord_3단계() {
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
						childSiteMenuTreeNode.setLinkURL("/servlet/AllItemType");
						
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
		siteMenuTree.toDBRecord(TEST_DBCP_NAME);
		
		ArraySiteMenuReqServerTask arraySiteMenuReqServerTask = null;
		try {
			arraySiteMenuReqServerTask = new ArraySiteMenuReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
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
			SiteMenuTreeNode siteMenuTreeNode = siteMenuTree.find(siteMenu.getMenuName());
			if (null == siteMenuTreeNode) {
				String errorMessage = new StringBuilder()
				.append("메뉴[menuNo=")
				.append(siteMenu.getMenuNo())
				.append(", menuName=")
				.append(siteMenu.getMenuName())
						.append("] 찾기 실패").toString();
				
				fail(errorMessage);
			}
			
			assertEquals("메뉴 번호 비교", siteMenuTreeNode.getMenuNo(), siteMenu.getMenuNo());
			assertEquals("메뉴 순서 비교", siteMenuTreeNode.getOrderSeq(), siteMenu.getOrderSeq());
			assertEquals("메뉴 번호 이용한 메뉴 순서 비교", siteMenuTreeNode.getMenuNo() - 1, siteMenu.getOrderSeq());
			assertEquals("부모 메뉴 번호 비교", siteMenuTreeNode.getParentNo(), siteMenu.getParentNo());
			assertEquals("메뉴 깊이 비교", siteMenuTreeNode.getDepth(), siteMenu.getDepth());
			assertEquals("메뉴 이름 비교", siteMenuTreeNode.getMenuName(), siteMenu.getMenuName());
			assertEquals("메뉴 링크 비교", siteMenuTreeNode.getLinkURL(), siteMenu.getLinkURL());
		}
	}

}
