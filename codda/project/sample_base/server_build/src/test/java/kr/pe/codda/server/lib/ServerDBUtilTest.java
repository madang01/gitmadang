package kr.pe.codda.server.lib;

import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;
import static kr.pe.codda.impl.jooq.tables.SbSeqTb.SB_SEQ_TB;
import static kr.pe.codda.impl.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.DBCPDataSourceNotFoundException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.ArraySiteMenuReq.ArraySiteMenuReq;
import kr.pe.codda.impl.message.ArraySiteMenuRes.ArraySiteMenuRes;
import kr.pe.codda.impl.message.MemberBlockReq.MemberBlockReq;
import kr.pe.codda.impl.task.server.ArraySiteMenuReqServerTask;
import kr.pe.codda.impl.task.server.MebmerBlockReqServerTask;
import kr.pe.codda.server.dbcp.DBCPManager;

public class ServerDBUtilTest extends AbstractJunitTest {
	private final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();
		ServerDBUtil.initializeDBEnvoroment(TEST_DBCP_NAME);

		{
			String userID = "admin";
			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
					(byte) '4', (byte) '$' };
			String nickname = "단위테스터용어드민";
			String pwdHint = "힌트 그것이 알고싶다";
			String pwdAnswer = "힌트답변 말이여 방구여";
			String ip = "127.0.0.1";

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.ADMIN, userID, nickname, pwdHint, pwdAnswer,
						passwordBytes, ip, new java.sql.Timestamp(System.currentTimeMillis()));
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
			String pwdHint = "힌트 그것이 알고싶다";
			String pwdAnswer = "힌트답변 말이여 방구여";
			String ip = "127.0.0.1";

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, pwdHint, pwdAnswer,
						passwordBytes, ip, new java.sql.Timestamp(System.currentTimeMillis()));
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
			String pwdHint = "힌트 그것이 알고싶다";
			String pwdAnswer = "힌트답변 말이여 방구여";
			String ip = "127.0.0.1";

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, pwdHint, pwdAnswer,
						passwordBytes, ip, new java.sql.Timestamp(System.currentTimeMillis()));
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
	}

	@After
	public void tearDown() {

		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));

			Result<Record4<UByte, Byte, Long, Long>> boardInfoResult = create
					.select(SB_BOARD_INFO_TB.BOARD_ID, SB_BOARD_INFO_TB.LIST_TYPE, SB_BOARD_INFO_TB.CNT,
							SB_BOARD_INFO_TB.TOTAL)
					.from(SB_BOARD_INFO_TB).orderBy(SB_BOARD_INFO_TB.BOARD_ID.asc()).fetch();

			for (Record4<UByte, Byte, Long, Long> boardInfoRecord : boardInfoResult) {
				UByte boardID = boardInfoRecord.get(SB_BOARD_INFO_TB.BOARD_ID);
				byte boardListTypeValue = boardInfoRecord.get(SB_BOARD_INFO_TB.LIST_TYPE);
				long acutalTotal = boardInfoRecord.getValue(SB_BOARD_INFO_TB.TOTAL);
				long actualCountOfList = boardInfoRecord.getValue(SB_BOARD_INFO_TB.CNT);

				BoardListType boardListType = BoardListType.valueOf(boardListTypeValue);
				int expectedTotal = create.selectCount().from(SB_BOARD_TB).where(SB_BOARD_TB.BOARD_ID.eq(boardID))
						.fetchOne().value1();

				int expectedCountOfList = -1;

				if (BoardListType.TREE.equals(boardListType)) {
					expectedCountOfList = create.selectCount().from(SB_BOARD_TB).where(SB_BOARD_TB.BOARD_ID.eq(boardID))
							.and(SB_BOARD_TB.BOARD_ST.eq(BoardStateType.OK.getValue())).fetchOne().value1();
				} else {
					expectedCountOfList = create.selectCount().from(SB_BOARD_TB).where(SB_BOARD_TB.BOARD_ID.eq(boardID))
							.and(SB_BOARD_TB.BOARD_ST.eq(BoardStateType.OK.getValue()))
							.and(SB_BOARD_TB.PARENT_NO.eq(UInteger.valueOf(0))).fetchOne().value1();
				}

				assertEquals("전체 글 갯수 비교", expectedTotal, acutalTotal);
				assertEquals("목록 글 갯수 비교", expectedCountOfList, actualCountOfList);
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

	private void initMenuDB() {
		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));

			create.update(SB_SEQ_TB).set(SB_SEQ_TB.SQ_VALUE, UInteger.valueOf(1)).execute();

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
	/*
	 * private void initBoardDB() { DataSource dataSource = null; try { dataSource =
	 * DBCPManager.getInstance().getBasicDataSource(TEST_DBCP_NAME); } catch
	 * (DBCPDataSourceNotFoundException e) { log.warn(e.getMessage(), e);
	 * fail(e.getMessage()); }
	 * 
	 * Connection conn = null;
	 * 
	 * try { conn = dataSource.getConnection(); conn.setAutoCommit(false);
	 * 
	 * DSLContext create = DSL.using(conn, SQLDialect.MYSQL,
	 * ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
	 * 
	 * create.delete(SB_BOARD_VOTE_TB).execute();
	 * create.delete(SB_BOARD_FILELIST_TB).execute();
	 * create.delete(SB_BOARD_HISTORY_TB).execute();
	 * create.delete(SB_BOARD_TB).execute();
	 * 
	 * create.update(SB_BOARD_INFO_TB).set(SB_BOARD_INFO_TB.CNT,
	 * 0).set(SB_BOARD_INFO_TB.TOTAL, 0) .set(SB_BOARD_INFO_TB.NEXT_BOARD_NO,
	 * UInteger.valueOf(1)).execute();
	 * 
	 * conn.commit();
	 * 
	 * } catch (Exception e) {
	 * 
	 * if (null != conn) { try { conn.rollback(); } catch (Exception e1) {
	 * log.warn("fail to rollback"); } }
	 * 
	 * log.warn(e.getMessage(), e);
	 * 
	 * fail(e.getMessage()); } finally { if (null != conn) { try { conn.close(); }
	 * catch (Exception e) { log.warn("fail to close the db connection", e); } } } }
	 */

	@Test
	public void DB초기화테스트상태점검() {
		/** 아무 동작 없이 상태 확인을 위한 테스트 메소드 */

		// initMenuDB();
	}

	@Test
	public void testgetToOrderSeqOfRelativeRootMenu_트리끝위치얻기2가지방법비교() {

		initMenuDB();

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
						childSiteMenuTreeNode.setLinkURL("/servlet/JSRSATest");

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
						childSiteMenuTreeNode.setLinkURL("/servlet/EchoTest");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("모든 데이터 타입 검증");
						childSiteMenuTreeNode.setLinkURL("/servlet/AllItemTypeTest");

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
						childSiteMenuTreeNode.setLinkURL("/servlet/UserLogin");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					{
						SiteMenuTreeNode childSiteMenuTreeNode = new SiteMenuTreeNode();

						childSiteMenuTreeNode.setMenuName("회원 가입");
						childSiteMenuTreeNode.setLinkURL("/servlet/MemberRegistration");

						rootSiteMenuTreeNode.addChildSiteMenuNode(childSiteMenuTreeNode);
					}

					siteMenuTree.addRootSiteMenuNode(rootSiteMenuTreeNode);
				}

				return siteMenuTree;
			}
		}

		VirtualSiteMenuTreeBuilderIF virtualSiteMenuTreeBuilder = new VirtualSiteMenuTreeBuilder();
		SiteMenuTree virtualSiteMenuTree = virtualSiteMenuTreeBuilder.build();
		virtualSiteMenuTree.toDBRecord(TEST_DBCP_NAME);

		ArraySiteMenuReqServerTask arraySiteMenuReqServerTask = null;
		try {
			arraySiteMenuReqServerTask = new ArraySiteMenuReqServerTask();
		} catch (DynamicClassCallException e2) {
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

		DataSource dataSource = null;

		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME);
		} catch (Exception e) {
			log.warn("", e);
			fail("fail to get a instance of DataSource class");
		}

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));

			for (ArraySiteMenuRes.Menu siteMenu : arraySiteMenuRes.getMenuList()) {

				UByte expectedFromOrderSeq = ServerDBUtil.getToOrderSeqOfRelativeRootMenu(create,
						UByte.valueOf(siteMenu.getOrderSeq()), UByte.valueOf(siteMenu.getDepth()));

				UByte acutalFromGroupSeq = ServerDBUtil.getToOrderSeqOfRelativeRootMenu(create,
						UByte.valueOf(siteMenu.getOrderSeq()), UInteger.valueOf(siteMenu.getParentNo()));

				// FIXME!
				log.info("siteMenu=[{}], expectedFromOrderSeq={}, acutalFromGroupSeq={}", siteMenu.toString(),
						expectedFromOrderSeq, acutalFromGroupSeq);

				assertEquals("트리의 마지막 그룹시퀀스를 얻는 방법 2가지(첫번째 트리 깊이를 이용한 방법, 두번째 직계 조상 이용한 방법) 비교", expectedFromOrderSeq,
						acutalFromGroupSeq);
			}

		} catch (Exception e) {
			log.warn("error", e);

			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
			}
			fail("에러 발생::errmsg=" + e.getMessage());
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

	

	@Test
	public void testCheckUserAccessRights_회원테이블미존재사용자() {
		final String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;

		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(TEST_DBCP_NAME);

		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));

			String requestedUserID = "testAA";
			try {
				try {
					ServerDBUtil.checkUserAccessRights(conn, create, log, "회원 미 존재 테스트 서비스", PermissionType.MEMBER,
							requestedUserID);
				} catch (ServerServiceException e) {
					String actualErrorMessag = e.getMessage();

					String expectedErrorMessage = new StringBuilder("서비스 요청자[").append(requestedUserID)
							.append("]가 회원 테이블에 존재하지 않습니다").toString();

					assertEquals("회원테이블 미존재 점검", expectedErrorMessage, actualErrorMessag);
				}
			} finally {
				conn.commit();
			}
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

	@Test
	public void testCheckUserAccessRights_회원상태가비정상상태() {
		final String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;

		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));

			final String requestedUserID = "test03";

			create.delete(SB_MEMBER_TB).where(SB_MEMBER_TB.USER_ID.eq(requestedUserID)).execute();

			conn.commit();

			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
					(byte) '4', (byte) '$' };
			String nickname = "단위테스터용아이디3";
			String pwdHint = "힌트 그것이 알고싶다3";
			String pwdAnswer = "힌트답변 말이여 방구여3";
			String ip = "127.0.0.3";

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, requestedUserID, nickname, pwdHint,
						pwdAnswer, passwordBytes, ip, new java.sql.Timestamp(System.currentTimeMillis()));
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to create a test ID");
			}

			MemberBlockReq memberBlockReq = new MemberBlockReq();
			memberBlockReq.setRequestedUserID("admin");
			memberBlockReq.setTargetUserID(requestedUserID);

			MebmerBlockReqServerTask userBlockReqServerTask = new MebmerBlockReqServerTask();

			try {
				userBlockReqServerTask.doWork(TEST_DBCP_NAME, memberBlockReq);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("사용자 정보 조회 실패::errmsg=" + e.getMessage());
			}

			try {
				ServerDBUtil.checkUserAccessRights(conn, create, log, "회원 상태가 비정상 테스트 서비스", PermissionType.MEMBER,
						requestedUserID);
			} catch (ServerServiceException e) {
				String actualErrorMessag = e.getMessage();

				String expectedErrorMessage = new StringBuilder("서비스 요청자[").append(requestedUserID).append("]의 회원 상태[")
						.append(MemberStateType.BLOCK.getName()).append("]가 정상이 아닙니다").toString();

				assertEquals("회원 상태가 비정상일 경우 점검", expectedErrorMessage, actualErrorMessag);
			} finally {
				conn.commit();
			}

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

	@Test
	public void testCheckUserAccessRights_회원역활값이잘못된경우() {
		final String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;

		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));

			final String requestedUserID = "test03";

			create.delete(SB_MEMBER_TB).where(SB_MEMBER_TB.USER_ID.eq(requestedUserID)).execute();

			conn.commit();

			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
					(byte) '4', (byte) '$' };
			String nickname = "단위테스터용아이디3";
			String pwdHint = "힌트 그것이 알고싶다3";
			String pwdAnswer = "힌트답변 말이여 방구여3";
			String ip = "127.0.0.3";

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, requestedUserID, nickname, pwdHint,
						pwdAnswer, passwordBytes, ip, new java.sql.Timestamp(System.currentTimeMillis()));
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to create a test ID");
			}

			create.update(SB_MEMBER_TB)
			.set(SB_MEMBER_TB.ROLE, (byte)'K').where(SB_MEMBER_TB.USER_ID.eq(requestedUserID))
					.execute();

			try {
				ServerDBUtil.checkUserAccessRights(conn, create, log, "회원 역활 유형이 손님인 테스트 서비스", PermissionType.MEMBER,
						requestedUserID);
			} catch (ServerServiceException e) {
				String actualErrorMessag = e.getMessage();

				String expectedErrorMessage = new StringBuilder("서비스 요청자[").append(requestedUserID)
						.append("]의 멤버 역활 유형[").append("K").append("]이 잘못되어있습니다").toString();

				assertEquals("회원 역활 유형이 손님일 경우 점검", expectedErrorMessage, actualErrorMessag);
			} finally {
				conn.commit();
			}

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

	@Test
	public void testCheckUserAccessRights_회원역활값이손님인경우() {
		final String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;

		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}
		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));

			final String requestedUserID = "test03";

			create.delete(SB_MEMBER_TB).where(SB_MEMBER_TB.USER_ID.eq(requestedUserID)).execute();

			conn.commit();

			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
					(byte) '4', (byte) '$' };
			String nickname = "단위테스터용아이디3";
			String pwdHint = "힌트 그것이 알고싶다3";
			String pwdAnswer = "힌트답변 말이여 방구여3";
			String ip = "127.0.0.3";

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.GUEST, requestedUserID, nickname, pwdHint,
						pwdAnswer, passwordBytes, ip, new java.sql.Timestamp(System.currentTimeMillis()));
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to create a test ID");
			}

			try {
				ServerDBUtil.checkUserAccessRights(conn, create, log, "회원 역활 유형이 손님인 테스트 서비스", PermissionType.MEMBER,
						requestedUserID);
			} catch (ServerServiceException e) {
				String actualErrorMessag = e.getMessage();

				String expectedErrorMessage = new StringBuilder("서비스 요청자[").append(requestedUserID)
						.append("]의 멤버 역활 유형[").append(MemberRoleType.GUEST.getValue()).append("]이 잘못되어있습니다")
						.toString();

				assertEquals("회원 역활 유형이 손님일 경우 점검", expectedErrorMessage, actualErrorMessag);
			} finally {
				conn.commit();
			}

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

	@Test
	public void testCheckUserAccessRights_관리자권한_관리자() {
		final String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;

		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		String requestedUserID = "admin";

		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));

			try {
				ServerDBUtil.checkUserAccessRights(conn, create, log, "관리저 전용 서비스에 관리자 접근 테스트 서비스",
						PermissionType.ADMIN, requestedUserID);
			} finally {
				conn.commit();
			}
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

	@Test
	public void testCheckUserAccessRights_관리자권한_일반회원() {
		final String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;

		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		String requestedUserID = "test01";

		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));

			try {
				ServerDBUtil.checkUserAccessRights(conn, create, log, "관리저 전용 서비스에 일반인 접근 테스트 서비스",
						PermissionType.ADMIN, requestedUserID);

				fail("no ServerServiceException");
			} catch (ServerServiceException e) {
				String actualErrorMessag = e.getMessage();

				String expectedErrorMessage = "관리저 전용 서비스에 일반인 접근 테스트 서비스는 관리자 전용 서비스입니다";

				assertEquals("관리저 전용 서비스에 일반회원가 접근한 경우 점검", expectedErrorMessage, actualErrorMessag);
			} finally {
				conn.commit();
			}

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

	@Test
	public void testCheckUserAccessRights_관리자이상권리요구_손님() {
		final String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;

		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		String requestedUserID = "guest";

		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));

			try {
				ServerDBUtil.checkUserAccessRights(conn, create, log, "관리저 전용 서비스에 손님 접근 테스트 서비스",
						PermissionType.ADMIN, requestedUserID);

				fail("no ServerServiceException");
			} catch (ServerServiceException e) {
				String actualErrorMessag = e.getMessage();

				String expectedErrorMessage = "관리저 전용 서비스에 손님 접근 테스트 서비스는 관리자 전용 서비스입니다";

				assertEquals("관리저 전용 서비스에 손님이 접근한 경우 점검", expectedErrorMessage, actualErrorMessag);
			} finally {
				conn.commit();
			}

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

	@Test
	public void testCheckUserAccessRights_일반회원권한_관리자() {
		final String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;

		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		String requestedUserID = "admin";

		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));

			try {
				ServerDBUtil.checkUserAccessRights(conn, create, log, "일반 사용자 서비스에 관리자 접근 테스트 서비스",
						PermissionType.MEMBER, requestedUserID);
			} finally {
				conn.commit();
			}
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

	@Test
	public void testCheckUserAccessRights_일반회원권한_일반회원() {
		final String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;

		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		String requestedUserID = "test01";

		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));

			try {
				ServerDBUtil.checkUserAccessRights(conn, create, log, "일반 사용자 서비스에 관리자 접근 테스트 서비스",
						PermissionType.MEMBER, requestedUserID);
			} finally {
				conn.commit();
			}
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

	@Test
	public void testCheckUserAccessRights_일반회원권한_손님() {
		final String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;

		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		String requestedUserID = "guest";

		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));

			try {
				ServerDBUtil.checkUserAccessRights(conn, create, log, "일반 사용자 서비스에 손님 접근 테스트 서비스", PermissionType.MEMBER,
						requestedUserID);

				fail("no ServerServiceException");
			} catch (ServerServiceException e) {
				String actualErrorMessag = e.getMessage();

				String expectedErrorMessage = "일반 사용자 서비스에 손님 접근 테스트 서비스는 로그인 해야만 이용할 수 있습니다";

				assertEquals("일반 사용자 서비스에 손님이 접근한 경우 점검", expectedErrorMessage, actualErrorMessag);
			} finally {
				conn.commit();
			}

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
	
	@Test
	public void testCheckUserAccessRights_손님권한_관리자() {
		final String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;

		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		String requestedUserID = "admin";

		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));

			try {
				ServerDBUtil.checkUserAccessRights(conn, create, log, "손님 권한일때 손님으로 접근 테스트 서비스", 
						PermissionType.GUEST, requestedUserID);				
			} finally {
				conn.commit();
			}

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
	
	@Test
	public void testCheckUserAccessRights_손님권한_일반회원() {
		final String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;

		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		String requestedUserID = "test01";

		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));

			try {
				ServerDBUtil.checkUserAccessRights(conn, create, log, "손님 권한일때 손님으로 접근 테스트 서비스", 
						PermissionType.GUEST, requestedUserID);				
			} finally {
				conn.commit();
			}

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
	
	@Test
	public void testCheckUserAccessRights_손님권한_손님() {
		final String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;

		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		String requestedUserID = "guest";

		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));

			try {
				ServerDBUtil.checkUserAccessRights(conn, create, log, "손님 권한일때 손님으로 접근 테스트 서비스", 
						PermissionType.GUEST, requestedUserID);				
			} finally {
				conn.commit();
			}

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
}
