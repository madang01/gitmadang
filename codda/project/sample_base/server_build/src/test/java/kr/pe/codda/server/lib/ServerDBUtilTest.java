package kr.pe.codda.server.lib;

import static kr.pe.codda.impl.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardVoteTb.SB_BOARD_VOTE_TB;
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
import org.jooq.types.UShort;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.DBCPDataSourceNotFoundException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.ArraySiteMenuReq.ArraySiteMenuReq;
import kr.pe.codda.impl.message.ArraySiteMenuRes.ArraySiteMenuRes;
import kr.pe.codda.impl.message.BoardListReq.BoardListReq;
import kr.pe.codda.impl.message.BoardListRes.BoardListRes;
import kr.pe.codda.impl.task.server.ArraySiteMenuReqServerTask;
import kr.pe.codda.impl.task.server.BoardListReqServerTask;
import kr.pe.codda.server.dbcp.DBCPManager;

public class ServerDBUtilTest extends AbstractJunitTest {
	private final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();
		ServerDBUtil.initializeDBEnvoroment(TEST_DBCP_NAME);		
		
		{
			String userID = "admin";
			byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
			String nickname = "단위테스터용어드민";
			String pwdHint = "힌트 그것이 알고싶다";
			String pwdAnswer = "힌트답변 말이여 방구여";
			String ip = "127.0.0.1";
			
			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.ADMIN, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[")
						.append(userID)
						.append("] 입니다").toString();
				String actualErrorMessag = e.getMessage();
				
				//log.warn(actualErrorMessag, e);
				
				assertEquals(expectedErrorMessage, actualErrorMessag);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to create a test ID");
			}
		}
		
		{
			String userID = "test01";
			byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
			String nickname = "단위테스터용아이디1";
			String pwdHint = "힌트 그것이 알고싶다";
			String pwdAnswer = "힌트답변 말이여 방구여";
			String ip = "127.0.0.1";
			
			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.USER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[")
						.append(userID)
						.append("] 입니다").toString();
				String actualErrorMessag = e.getMessage();
				
				//log.warn(actualErrorMessag, e);
				
				assertEquals(expectedErrorMessage, actualErrorMessag);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to create a test ID");
			}
		}
		
		{
			String userID = "test02";
			byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
			String nickname = "단위테스터용아이디2";
			String pwdHint = "힌트 그것이 알고싶다";
			String pwdAnswer = "힌트답변 말이여 방구여";
			String ip = "127.0.0.1";
			
			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.USER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[")
						.append(userID)
						.append("] 입니다").toString();
				String actualErrorMessag = e.getMessage();
				
				//log.warn(actualErrorMessag, e);
				
				assertEquals(expectedErrorMessage, actualErrorMessag);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to create a test ID");
			}
		}
	}
	
	@After
	public void tearDown(){		
		
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
			
			Result<Record4<UByte, Byte, Integer, Integer>> boardInfoResult = create.select(SB_BOARD_INFO_TB.BOARD_ID, 
					SB_BOARD_INFO_TB.LIST_TYPE,
					SB_BOARD_INFO_TB.CNT, SB_BOARD_INFO_TB.TOTAL)
			.from(SB_BOARD_INFO_TB).orderBy(SB_BOARD_INFO_TB.BOARD_ID.asc())
			.fetch();
			
			
			for (Record4<UByte, Byte, Integer, Integer> boardInfoRecord : boardInfoResult) {
				UByte boardID = boardInfoRecord.get(SB_BOARD_INFO_TB.BOARD_ID);
				byte boardListTypeValue = boardInfoRecord.get(SB_BOARD_INFO_TB.LIST_TYPE);
				int acutalTotal = boardInfoRecord.getValue(SB_BOARD_INFO_TB.TOTAL);
				int actualCountOfList = boardInfoRecord.getValue(SB_BOARD_INFO_TB.CNT);
				
				BoardListType boardListType = BoardListType.valueOf(boardListTypeValue);				
				int expectedTotal = create.selectCount()
						.from(SB_BOARD_TB)
						.where(SB_BOARD_TB.BOARD_ID.eq(boardID)).fetchOne().value1();
				
				int expectedCountOfList = -1;
				
				if (BoardListType.TREE.equals(boardListType)) {
					expectedCountOfList = create.selectCount().from(SB_BOARD_TB)
							.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
							.and(SB_BOARD_TB.BOARD_ST.eq(BoardStateType.OK.getValue())).fetchOne().value1();
				} else {
					expectedCountOfList = create.selectCount().from(SB_BOARD_TB)
							.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
							.and(SB_BOARD_TB.BOARD_ST.eq(BoardStateType.OK.getValue()))
							.and(SB_BOARD_TB.PARENT_NO.eq(UInteger.valueOf(0))).fetchOne().value1();
				}
								
				
				
				assertEquals("전체 글 갯수 비교",  expectedTotal, acutalTotal);
				assertEquals("목록 글 갯수 비교",  expectedCountOfList, actualCountOfList);
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
	
	
	private void initBoardDB() {
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
			
			create.delete(SB_BOARD_VOTE_TB).execute();
			create.delete(SB_BOARD_FILELIST_TB).execute();
			create.delete(SB_BOARD_HISTORY_TB).execute();
			create.delete(SB_BOARD_TB).execute();
			
			create.update(SB_BOARD_INFO_TB)
			.set(SB_BOARD_INFO_TB.CNT, 0)
			.set(SB_BOARD_INFO_TB.TOTAL, 0)
			.set(SB_BOARD_INFO_TB.NEXT_BOARD_NO, UInteger.valueOf(1))
			.execute();						
			
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
		dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME);
		} catch(Exception e) {
			log.warn("", e);
			fail("fail to get a instance of DataSource class");
		}

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			for (ArraySiteMenuRes.Menu siteMenu : arraySiteMenuRes.getMenuList()) {
				
				UByte expectedFromOrderSeq = ServerDBUtil
						.getToOrderSeqOfRelativeRootMenu(create, 
								UByte.valueOf(siteMenu.getOrderSeq()), 
								UByte.valueOf(siteMenu.getDepth()));
				
				UByte acutalFromGroupSeq = ServerDBUtil.getToOrderSeqOfRelativeRootMenu(create, 
						UByte.valueOf(siteMenu.getOrderSeq()), 
						UInteger.valueOf(siteMenu.getParentNo()));
				
				// FIXME!
				log.info("siteMenu=[{}], expectedFromOrderSeq={}, acutalFromGroupSeq={}", 
						siteMenu.toString(), expectedFromOrderSeq, acutalFromGroupSeq);
				
				assertEquals("트리의 마지막 그룹시퀀스를 얻는 방법 2가지(첫번째 트리 깊이를 이용한 방법, 두번째 직계 조상 이용한 방법) 비교", expectedFromOrderSeq,  acutalFromGroupSeq);
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
			fail("에러 발생::errmsg="+e.getMessage());
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
	public void testGetToGroupSeqOfRelativeRootBoard_트리끝위치얻기2가지방법비교() {
		initBoardDB();
		
		final short boardID = 3;
		
		class VirtualBoardTreeBuilder implements VirtualBoardTreeBuilderIF {
			@Override
			public BoardTree build(final short boardID) {
				String writerID = "test01";
				String otherID = "test02";
				
				BoardTree boardTree = new BoardTree();				
				
				{
					BoardTreeNode root1BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, writerID, "루트1", "루트1");
					
					{
						BoardTreeNode root1Child1BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, otherID, "루트1_자식1", "루트1_자식1");
						{
							BoardTreeNode root1Child1Child1Child1BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, otherID, "루트1_자식1_자식1_자식1", "루트1_자식1_자식1_자식1");
							{
								BoardTreeNode root1Child1Child1Child1Child1BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, otherID, "루트1_자식1_자식1_자식1_자식1", "루트1_자식1_자식1_자식1_자식1");
								root1Child1Child1Child1BoardTreeNode.addChildNode(root1Child1Child1Child1Child1BoardTreeNode);
							}
							root1Child1BoardTreeNode.addChildNode(root1Child1Child1Child1BoardTreeNode);
						}		
						
						root1BoardTreeNode.addChildNode(root1Child1BoardTreeNode);
					}
					
					{
						BoardTreeNode root1Child2BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, writerID, "루트1_자식2", "루트1_자식2");
						{
							BoardTreeNode root1Child2Child1BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, otherID, "루트1_자식2_자식1", "루트1_자식2_자식1");
							
							root1Child2BoardTreeNode.addChildNode(root1Child2Child1BoardTreeNode);
						}
						{
							BoardTreeNode root1Child2Child2BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, writerID, "루트1_자식2_자식2", "루트1_자식2_자식2");
							root1Child2BoardTreeNode.addChildNode(root1Child2Child2BoardTreeNode);
						}
						
						{
							BoardTreeNode root1Child2Child3BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, otherID, "루트1_자식2_자식3", "루트1_자식2_자식3");
							{
								BoardTreeNode root1Child2Child3Child1BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, writerID, "루트1_자식2_자식3_자식1", "루트1_자식2_자식3_자식1");
								{
									BoardTreeNode root1Child2Child3Child1Child1BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, otherID, "루트1_자식2_자식3_자식1_자식1", "루트1_자식2_자식3_자식1_자식1");
									root1Child2Child3Child1BoardTreeNode.addChildNode(root1Child2Child3Child1Child1BoardTreeNode);									
								}
								{
									BoardTreeNode root1Child2Child3Child1Child2BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, writerID, "루트1_자식2_자식3_자식1_자식2", "루트1_자식2_자식3_자식1_자식2");
									root1Child2Child3Child1BoardTreeNode.addChildNode(root1Child2Child3Child1Child2BoardTreeNode);
								}
								{
									BoardTreeNode root1Child2Child3Child1Child3BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, otherID, "루트1_자식2_자식3_자식1_자식3", "루트1_자식2_자식3_자식1_자식3");
									root1Child2Child3Child1BoardTreeNode.addChildNode(root1Child2Child3Child1Child3BoardTreeNode);
								}
								root1Child2Child3BoardTreeNode.addChildNode(root1Child2Child3Child1BoardTreeNode);
							}
							root1Child2BoardTreeNode.addChildNode(root1Child2Child3BoardTreeNode);
						}
						root1BoardTreeNode.addChildNode(root1Child2BoardTreeNode);
					}
					
					{
						BoardTreeNode root1Child3BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, otherID, "루트1_자식3", "루트1_자식3");
						root1BoardTreeNode.addChildNode(root1Child3BoardTreeNode);
					}					
					
					boardTree.addRootBoardTreeNode(root1BoardTreeNode);
				}
				
				return boardTree;
			}			
		}
		
		VirtualBoardTreeBuilderIF virtualBoardTreeBuilder = new VirtualBoardTreeBuilder();
		BoardTree boardTree = virtualBoardTreeBuilder.build(boardID);
		boardTree.makeDBRecord(TEST_DBCP_NAME);
		
		
		int pageNo = 1;
		int pageSize = boardTree.getHashSize();
		
		BoardListReq boardListReq = new BoardListReq();
		boardListReq.setRequestedUserID("admin");
		boardListReq.setBoardID(boardID);
		boardListReq.setPageNo(pageNo);
		boardListReq.setPageSize(pageSize);
		
		BoardListReqServerTask boardListReqServerTask = null;
		try {
			boardListReqServerTask = new BoardListReqServerTask();
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}
		BoardListRes boardListRes = null;
		
		try {
			boardListRes = boardListReqServerTask.doWork(TEST_DBCP_NAME, boardListReq);
			
		} catch(ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
						
		DataSource dataSource = null;
		
		try {
		dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME);
		} catch(Exception e) {
			log.warn("", e);
			fail("fail to get a instance of DataSource class");
		}

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			for (BoardListRes.Board board : boardListRes.getBoardList()) {
				
				UShort expectedFromGroupSq = ServerDBUtil.getToGroupSeqOfRelativeRootBoard(create, 
						UByte.valueOf(boardListRes.getBoardID()), 
						UInteger.valueOf(board.getGroupNo()), 
						UShort.valueOf(board.getGroupSeq()), 
						UByte.valueOf(board.getDepth()));
				
				UShort acutalFromGroupSq = ServerDBUtil.getToGroupSeqOfRelativeRootBoard(create, 
						UByte.valueOf(boardListRes.getBoardID()), 
						UShort.valueOf(board.getGroupSeq()), 
						UInteger.valueOf(board.getParentNo()));
				
				assertEquals("트리의 마지막 그룹시퀀스를 얻는 방법 2가지(첫번째 depth이용한방법, 두번째 직계조상이용방법) 비교", expectedFromGroupSq,  acutalFromGroupSq);
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
			fail("에러 발생::errmsg="+e.getMessage());
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
	public void testGetToGroupSeqOfRelativeRootBoard_트리끝위치얻기방법2가지속도비교() {
		initBoardDB();
		
		final short boardID = 3;
		
		class VirtualBoardTreeBuilder implements VirtualBoardTreeBuilderIF {
			@Override
			public BoardTree build(final short boardID) {
				String writerID = "test01";
				String otherID = "test02";
				
				BoardTree boardTree = new BoardTree();				
				
				{
					BoardTreeNode root1BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, writerID, "루트1", "루트1");
					
					{
						BoardTreeNode root1Child1BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, otherID, "루트1_자식1", "루트1_자식1");
						{
							BoardTreeNode root1Child1Child1Child1BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, otherID, "루트1_자식1_자식1_자식1", "루트1_자식1_자식1_자식1");
							{
								BoardTreeNode root1Child1Child1Child1Child1BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, otherID, "루트1_자식1_자식1_자식1_자식1", "루트1_자식1_자식1_자식1_자식1");
								root1Child1Child1Child1BoardTreeNode.addChildNode(root1Child1Child1Child1Child1BoardTreeNode);
							}
							root1Child1BoardTreeNode.addChildNode(root1Child1Child1Child1BoardTreeNode);
						}		
						
						root1BoardTreeNode.addChildNode(root1Child1BoardTreeNode);
					}
					
					{
						BoardTreeNode root1Child2BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, writerID, "루트1_자식2", "루트1_자식2");
						{
							BoardTreeNode root1Child2Child1BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, otherID, "루트1_자식2_자식1", "루트1_자식2_자식1");
							
							root1Child2BoardTreeNode.addChildNode(root1Child2Child1BoardTreeNode);
						}
						{
							BoardTreeNode root1Child2Child2BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, writerID, "루트1_자식2_자식2", "루트1_자식2_자식2");
							root1Child2BoardTreeNode.addChildNode(root1Child2Child2BoardTreeNode);
						}
						
						{
							BoardTreeNode root1Child2Child3BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, otherID, "루트1_자식2_자식3", "루트1_자식2_자식3");
							{
								BoardTreeNode root1Child2Child3Child1BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, writerID, "루트1_자식2_자식3_자식1", "루트1_자식2_자식3_자식1");
								{
									BoardTreeNode root1Child2Child3Child1Child1BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, otherID, "루트1_자식2_자식3_자식1_자식1", "루트1_자식2_자식3_자식1_자식1");
									root1Child2Child3Child1BoardTreeNode.addChildNode(root1Child2Child3Child1Child1BoardTreeNode);									
								}
								{
									BoardTreeNode root1Child2Child3Child1Child2BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, writerID, "루트1_자식2_자식3_자식1_자식2", "루트1_자식2_자식3_자식1_자식2");
									root1Child2Child3Child1BoardTreeNode.addChildNode(root1Child2Child3Child1Child2BoardTreeNode);
								}
								{
									BoardTreeNode root1Child2Child3Child1Child3BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, otherID, "루트1_자식2_자식3_자식1_자식3", "루트1_자식2_자식3_자식1_자식3");
									root1Child2Child3Child1BoardTreeNode.addChildNode(root1Child2Child3Child1Child3BoardTreeNode);
								}
								root1Child2Child3BoardTreeNode.addChildNode(root1Child2Child3Child1BoardTreeNode);
							}
							root1Child2BoardTreeNode.addChildNode(root1Child2Child3BoardTreeNode);
						}
						root1BoardTreeNode.addChildNode(root1Child2BoardTreeNode);
					}
					
					{
						BoardTreeNode root1Child3BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, otherID, "루트1_자식3", "루트1_자식3");
						root1BoardTreeNode.addChildNode(root1Child3BoardTreeNode);
					}					
					
					boardTree.addRootBoardTreeNode(root1BoardTreeNode);
				}
				
				return boardTree;
			}			
		}
		
		VirtualBoardTreeBuilderIF virtualBoardTreeBuilder = new VirtualBoardTreeBuilder();
		BoardTree boardTree = virtualBoardTreeBuilder.build(boardID);
		boardTree.makeDBRecord(TEST_DBCP_NAME);
		
		BoardTreeNode boardTreeNode = boardTree.find("루트1_자식2");
		
		if (null == boardTreeNode) {
			fail("속도 비교를 위한 대상 글(제목:루트1_자식2) 찾기 실패");
		}
		
		DataSource dataSource = null;
		
		try {
		dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME);
		} catch(Exception e) {
			log.warn("", e);
			fail("fail to get a instance of DataSource class");
		}
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			int count = 10000;
			
			long beforeTime = 0, afterTime = 0;
			
			beforeTime = System.currentTimeMillis();
			for (int i=0;i < count; i++) {
				ServerDBUtil.getToGroupSeqOfRelativeRootBoard(create, 
						UByte.valueOf(boardTreeNode.getBoardID()), 
						UShort.valueOf(boardTreeNode.getGroupSeq()), 
						UInteger.valueOf(boardTreeNode.getParentNo()));
			}
				
			afterTime = System.currentTimeMillis();
			
			log.info(new StringBuilder("직계 부모 이용한 트리 끝 위치 얻기 ")
					.append(count).append(" 회 :end(elapsed=")
					.append((afterTime - beforeTime)).append(" ms)").toString());
			
			beforeTime = System.currentTimeMillis();
			for (int i=0;i < count; i++) {
				ServerDBUtil.getToGroupSeqOfRelativeRootBoard(create, 
						UByte.valueOf(boardTreeNode.getBoardID()), 
						UInteger.valueOf(boardTreeNode.getGroupNo()), 
						UShort.valueOf(boardTreeNode.getGroupSeq()), 
						UByte.valueOf(boardTreeNode.getDepth()));
			}
			
			afterTime = System.currentTimeMillis();
			
			log.info(new StringBuilder("트리 깊이를 이용한 트리 끝 위치 얻기 ")
					.append(count).append(" 회:end(elapsed=")
					.append((afterTime - beforeTime)).append(" ms)").toString());
			
			
			
		
		} catch (Exception e) {
			log.warn("error", e);
			
			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
			}
			fail("에러 발생::errmsg="+e.getMessage());
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
