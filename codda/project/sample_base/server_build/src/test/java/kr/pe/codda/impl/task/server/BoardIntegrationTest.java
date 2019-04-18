package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;
import static kr.pe.codda.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.jooq.tables.SbBoardVoteTb.SB_BOARD_VOTE_TB;
import static kr.pe.codda.jooq.tables.SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB;
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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junitlib.AbstractBoardTest;
import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.DBCPDataSourceNotFoundException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.BoardBlockReq.BoardBlockReq;
import kr.pe.codda.impl.message.BoardInfoAddReq.BoardInfoAddReq;
import kr.pe.codda.impl.message.BoardInfoAddRes.BoardInfoAddRes;
import kr.pe.codda.impl.message.BoardListReq.BoardListReq;
import kr.pe.codda.impl.message.BoardListRes.BoardListRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.PersonalActivityHistoryReq.PersonalActivityHistoryReq;
import kr.pe.codda.impl.message.PersonalActivityHistoryRes.PersonalActivityHistoryRes;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardListType;
import kr.pe.codda.server.lib.BoardReplyPolicyType;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.BoardTree;
import kr.pe.codda.server.lib.BoardTreeNode;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.VirtualBoardTreeBuilderIF;

public class BoardIntegrationTest extends AbstractBoardTest {
			// ServerCommonStaticFinalVars.LOAD_TEST_DBCP_NAME;
			// ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;
	// private final short boardID = 3;

	// ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();
		ServerDBUtil.initializeDBEnvoroment(TEST_DBCP_NAME);

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
			String userID = "guest";
			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's', (byte) 't', (byte) '1', (byte) '2', (byte) '3',
					(byte) '4', (byte) '$' };
			String nickname = "손님";
			String email = "guest@codda.pe.kr";
			String ip = "127.0.0.1";

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.GUEST, userID, nickname, email,
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
	}

	@Before
	public void setUp() {
		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(
					TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL,
					ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
		
			create.delete(SB_MEMBER_ACTIVITY_HISTORY_TB).execute();
			create.delete(SB_BOARD_VOTE_TB).execute();
			create.delete(SB_BOARD_FILELIST_TB).execute();
			create.delete(SB_BOARD_HISTORY_TB).execute();
			create.delete(SB_BOARD_TB).execute();
		 
			/** sample_base 프로젝에 예약된 0 ~ 3 까지의 게시판 식별자를 제외한 게시판 정보 삭제  */
			create.delete(SB_BOARD_INFO_TB).where(SB_BOARD_INFO_TB.BOARD_ID.ge(UByte.valueOf(4))).execute();
			
			create.update(SB_BOARD_INFO_TB)
					.set(SB_BOARD_INFO_TB.CNT, 0L)
					.set(SB_BOARD_INFO_TB.TOTAL, 0L)
					.set(SB_BOARD_INFO_TB.NEXT_BOARD_NO, UInteger.valueOf(1)).execute();

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
	public void tearDown() {

		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(
					TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}

		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL,
					ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));

			Result<Record4<UByte, Byte, Long, Long>> boardInfoResult = create.select(SB_BOARD_INFO_TB.BOARD_ID, 
					SB_BOARD_INFO_TB.LIST_TYPE,
					SB_BOARD_INFO_TB.CNT, SB_BOARD_INFO_TB.TOTAL)
			.from(SB_BOARD_INFO_TB).orderBy(SB_BOARD_INFO_TB.BOARD_ID.asc())
			.fetch();
			
			
			for (Record4<UByte, Byte, Long, Long> boardInfoRecord : boardInfoResult) {
				UByte boardID = boardInfoRecord.get(SB_BOARD_INFO_TB.BOARD_ID);
				byte boardListTypeValue = boardInfoRecord.get(SB_BOARD_INFO_TB.LIST_TYPE);
				long acutalTotal = boardInfoRecord.getValue(SB_BOARD_INFO_TB.TOTAL);
				long actualCountOfList = boardInfoRecord.getValue(SB_BOARD_INFO_TB.CNT);
				
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
	public void 개인활동이력조회_테스트() {
		PersonalActivityHistoryReq personalActivityHistoryReq = new PersonalActivityHistoryReq();
		personalActivityHistoryReq.setRequestedUserID("guest");
		personalActivityHistoryReq.setTargetUserID("test01");
		personalActivityHistoryReq.setPageNo(1);
		personalActivityHistoryReq.setPageSize(20);
		
		PersonalActivityHistoryReqServerTask  personalActivityHistoryReqServerTask = null;
		try {
			personalActivityHistoryReqServerTask = new PersonalActivityHistoryReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		PersonalActivityHistoryRes personalActivityHistoryRes = null;
		
		try {
			personalActivityHistoryRes = personalActivityHistoryReqServerTask.doWork(TEST_DBCP_NAME, personalActivityHistoryReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		log.info(personalActivityHistoryRes.toString());
	}

	@Test
	public void 목록조회_본문으로만이루어진목록_초기상태() {
		final short testBoardID = 2;
		
		
		int pageNo = 1;
		int pageSize = 20;

		BoardListReq boardListReq = new BoardListReq();
		boardListReq.setRequestedUserID("guest");
		boardListReq.setBoardID(testBoardID);
		boardListReq.setPageNo(pageNo);
		boardListReq.setPageSize(pageSize);

		BoardListReqServerTask boardListReqServerTask = null;
		try {
			boardListReqServerTask = new BoardListReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			BoardListRes acutalBoardListRes = boardListReqServerTask.doWork(
					TEST_DBCP_NAME, boardListReq);
			log.info(acutalBoardListRes.toString());

			if (acutalBoardListRes.getTotal() != 0) {
				fail("DB 초기 상태에에서 총 갯수가 0이 아닙니다");
			}

			if (acutalBoardListRes.getCnt() != 0) {
				fail("DB 초기 상태에에서 총 갯수가 0이 아닙니다");
			}
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판목록조회테스트_게시판목록유형전체_초기상태() {
		String requestedUserIDForAdmin = "admin";
		String requestedUserIDForMember = "guest";
		
		BoardListReqServerTask boardListReqServerTask = null;
		try {
			boardListReqServerTask = new BoardListReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		BoardInfoAddReqServerTask boardInfoAddReqServerTask = null;
		try {
			boardInfoAddReqServerTask = new BoardInfoAddReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		BoardInfoAddReq boardInfoAddReq = new BoardInfoAddReq();
		boardInfoAddReq.setRequestedUserID(requestedUserIDForAdmin);
		boardInfoAddReq.setBoardName("단위테스트용");
		boardInfoAddReq.setBoardListType(BoardListType.TREE.getValue());
		boardInfoAddReq.setBoardReplyPolicyType(BoardReplyPolicyType.ALL.getValue());
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.MEMBER.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.MEMBER.getValue());
		
		int pageNo = 1;
		int pageSize = 20;
		
		for (BoardListType boardListType : BoardListType.values()) {
			boardInfoAddReq.setBoardListType(boardListType.getValue());
			
			BoardInfoAddRes boardInfoAddRes = null;
			
			try {
				boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to execuate doTask");
			}

			BoardListReq boardListReq = new BoardListReq();
			boardListReq.setRequestedUserID(requestedUserIDForMember);
			boardListReq.setBoardID(boardInfoAddRes.getBoardID());
			boardListReq.setPageNo(pageNo);
			boardListReq.setPageSize(pageSize);			

			try {
				BoardListRes acutalBoardListRes = boardListReqServerTask.doWork(
						TEST_DBCP_NAME, boardListReq);
				log.info(acutalBoardListRes.toString());

				if (acutalBoardListRes.getTotal() != 0) {
					fail("DB 초기 상태에에서 총 갯수가 0이 아닙니다");
				}

				if (acutalBoardListRes.getCnt() != 0) {
					fail("DB 초기 상태에에서 총 갯수가 0이 아닙니다");
				}			
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to execuate doTask");
			}
		}
	}

	@Test
	public void 본문으로만이루어진목록_정상() {
		String requestedUserIDForAdmin = "admin";
		String requestedUserIDForMember = "guest";
		
		int pageNo = 1;
		int pageSize = 20;
		
		BoardListReqServerTask boardListReqServerTask = null;
		try {
			boardListReqServerTask = new BoardListReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		BoardInfoAddReqServerTask boardInfoAddReqServerTask = null;
		try {
			boardInfoAddReqServerTask = new BoardInfoAddReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		BoardInfoAddReq boardInfoAddReq = new BoardInfoAddReq();
		boardInfoAddReq.setRequestedUserID(requestedUserIDForAdmin);
		boardInfoAddReq.setBoardName("단위테스트용");
		boardInfoAddReq.setBoardListType(BoardListType.ONLY_GROUP_ROOT.getValue());
		boardInfoAddReq.setBoardReplyPolicyType(BoardReplyPolicyType.ALL.getValue());
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.GUEST.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.GUEST.getValue());
		
		
		
		BoardInfoAddRes boardInfoAddRes = null;
		
		try {
			boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
				
		class VirtualBoardTreeBuilder implements VirtualBoardTreeBuilderIF {
			@Override
			public BoardTree build(final short boardID) {
				String writerID = "test01";
				String otherID = "test02";

				BoardTree boardTree = new BoardTree();
				{
					BoardTreeNode root1BoardTreeNode = BoardTree
							.makeBoardTreeNodeWithoutTreeInfomation(boardID,
									writerID, "루트1", "루트1");

					{
						BoardTreeNode root1Child1BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, otherID, "루트1_자식1", "루트1_자식1");
						{
							BoardTreeNode root1Child1Child1BoardTreeNode = BoardTree
									.makeBoardTreeNodeWithoutTreeInfomation(
											boardID, otherID, "루트1_자식1_자식1",
											"루트1_자식1_자식1");
							{
								BoardTreeNode root1Child1Child1Child1BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, otherID,
												"루트1_자식1_자식1_자식1",
												"루트1_자식1_자식1_자식1");
								{
									BoardTreeNode root1Child1Child1Child1Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"루트1_자식1_자식1_자식1_자식1",
													"루트1_자식1_자식1_자식1_자식1");
									root1Child1Child1Child1BoardTreeNode
											.addChildNode(root1Child1Child1Child1Child1BoardTreeNode);
								}

								root1Child1Child1BoardTreeNode
										.addChildNode(root1Child1Child1Child1BoardTreeNode);
							}

							root1Child1BoardTreeNode
									.addChildNode(root1Child1Child1BoardTreeNode);
						}

						root1BoardTreeNode
								.addChildNode(root1Child1BoardTreeNode);
					}

					{
						BoardTreeNode root1Child2BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, writerID, "루트1_자식2", "루트1_자식2");

						{
							BoardTreeNode root1Child2Child1BoardTreeNode = BoardTree
									.makeBoardTreeNodeWithoutTreeInfomation(
											boardID, otherID, "루트1_자식2_자식1",
											"루트1_자식2_자식1");
							{
								BoardTreeNode root1Child2Child1Child1BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"루트1_자식2_자식1_자식1",
												"루트1_자식2_자식1_자식1");

								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child1BoardTreeNode);
							}

							{
								BoardTreeNode root1Child2Child1Child2BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"루트1_자식2_자식1_자식2",
												"루트1_자식2_자식1_자식2");
								{
									BoardTreeNode root1Child2Child1Child2Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"루트1_자식2_자식1_자식2_자식1",
													"루트1_자식2_자식3_자식2_자식1");
									root1Child2Child1Child2BoardTreeNode
											.addChildNode(root1Child2Child1Child2Child1BoardTreeNode);
								}

								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child2BoardTreeNode);
							}

							{
								BoardTreeNode root1Child2Child1Child3BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"루트1_자식2_자식1_자식3",
												"루트1_자식2_자식1_자식3");
								{
									BoardTreeNode root1Child2Child1Child3Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"루트1_자식2_자식1_자식3_자식1",
													"루트1_자식2_자식3_자식3_자식1");
									root1Child2Child1Child3BoardTreeNode
											.addChildNode(root1Child2Child1Child3Child1BoardTreeNode);
								}

								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child3BoardTreeNode);
							}

							root1Child2BoardTreeNode
									.addChildNode(root1Child2Child1BoardTreeNode);
						}
						root1BoardTreeNode
								.addChildNode(root1Child2BoardTreeNode);
					}

					{
						BoardTreeNode root1Child3BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, otherID, "루트1_자식3", "루트1_자식3");
						root1BoardTreeNode
								.addChildNode(root1Child3BoardTreeNode);
					}

					boardTree.addRootBoardTreeNode(root1BoardTreeNode);
				}
				
				{
					BoardTreeNode root1BoardTreeNode = BoardTree
							.makeBoardTreeNodeWithoutTreeInfomation(boardID,
									writerID, "루트2", "루트2");

					{
						BoardTreeNode root1Child1BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, otherID, "루트2_자식1", "루트2_자식1");
						{
							BoardTreeNode root1Child1Child1BoardTreeNode = BoardTree
									.makeBoardTreeNodeWithoutTreeInfomation(
											boardID, otherID, "루트2_자식1_자식1",
											"루트2_자식1_자식1");
							{
								BoardTreeNode root1Child1Child1Child1BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, otherID,
												"루트2_자식1_자식1_자식1",
												"루트2_자식1_자식1_자식1");
								{
									BoardTreeNode root1Child1Child1Child1Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"루트2_자식1_자식1_자식1_자식1",
													"루트2_자식1_자식1_자식1_자식1");
									root1Child1Child1Child1BoardTreeNode
											.addChildNode(root1Child1Child1Child1Child1BoardTreeNode);
								}

								root1Child1Child1BoardTreeNode
										.addChildNode(root1Child1Child1Child1BoardTreeNode);
							}

							root1Child1BoardTreeNode
									.addChildNode(root1Child1Child1BoardTreeNode);
						}

						root1BoardTreeNode
								.addChildNode(root1Child1BoardTreeNode);
					}

					{
						BoardTreeNode root1Child2BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, writerID, "루트2_자식2", "루트2_자식2");

						{
							BoardTreeNode root1Child2Child1BoardTreeNode = BoardTree
									.makeBoardTreeNodeWithoutTreeInfomation(
											boardID, otherID, "루트2_자식2_자식1",
											"루트2_자식2_자식1");
							{
								BoardTreeNode root1Child2Child1Child1BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"루트2_자식2_자식1_자식1",
												"루트2_자식2_자식1_자식1");

								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child1BoardTreeNode);
							}

							{
								BoardTreeNode root1Child2Child1Child2BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"루트2_자식2_자식1_자식2",
												"루트2_자식2_자식1_자식2");
								{
									BoardTreeNode root1Child2Child1Child2Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"루트2_자식2_자식1_자식2_자식1",
													"루트2_자식2_자식3_자식2_자식1");
									root1Child2Child1Child2BoardTreeNode
											.addChildNode(root1Child2Child1Child2Child1BoardTreeNode);
								}

								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child2BoardTreeNode);
							}

							{
								BoardTreeNode root1Child2Child1Child3BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"루트2_자식2_자식1_자식3",
												"루트2_자식2_자식1_자식3");
								{
									BoardTreeNode root1Child2Child1Child3Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"루트2_자식2_자식1_자식3_자식1",
													"루트2_자식2_자식3_자식3_자식1");
									root1Child2Child1Child3BoardTreeNode
											.addChildNode(root1Child2Child1Child3Child1BoardTreeNode);
								}

								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child3BoardTreeNode);
							}

							root1Child2BoardTreeNode
									.addChildNode(root1Child2Child1BoardTreeNode);
						}
						root1BoardTreeNode
								.addChildNode(root1Child2BoardTreeNode);
					}

					{
						BoardTreeNode root1Child3BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, otherID, "루트2_자식3", "루트2_자식3");
						root1BoardTreeNode
								.addChildNode(root1Child3BoardTreeNode);
					}

					boardTree.addRootBoardTreeNode(root1BoardTreeNode);
				}

				return boardTree;
			}
		}

		VirtualBoardTreeBuilderIF virtualBoardTreeBuilder = new VirtualBoardTreeBuilder();
		BoardTree boardTree = virtualBoardTreeBuilder.build(boardInfoAddRes.getBoardID());
		boardTree.makeDBRecord(TEST_DBCP_NAME);

		BoardTreeNode blockBoardTreeNode = boardTree.find("루트1");

		if (null == blockBoardTreeNode) {
			fail("목표 게시글(제목:루트1) 찾기 실패");
		}

		
		BoardBlockReq blcokBoardBlockReq = new BoardBlockReq();
		blcokBoardBlockReq.setRequestedUserID(requestedUserIDForAdmin);
		blcokBoardBlockReq.setBoardID(blockBoardTreeNode.getBoardID());
		blcokBoardBlockReq.setBoardNo(blockBoardTreeNode.getBoardNo());
		blcokBoardBlockReq.setIp("127.0.0.7");

		BoardBlockReqServerTask boardBlockReqServerTask = null;
		try {
			boardBlockReqServerTask = new BoardBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			MessageResultRes messageResultRes = boardBlockReqServerTask.doWork(
					TEST_DBCP_NAME, blcokBoardBlockReq);

			log.info(messageResultRes.toString());

			if (!messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}

		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
		
		BoardListReq boardListReq = new BoardListReq();
		boardListReq.setRequestedUserID(requestedUserIDForMember);
		boardListReq.setBoardID(boardInfoAddRes.getBoardID());
		boardListReq.setPageNo(pageNo);
		boardListReq.setPageSize(pageSize);			

		BoardListRes acutalBoardListRes = null;
		try {
			acutalBoardListRes = boardListReqServerTask.doWork(
					TEST_DBCP_NAME, boardListReq);
			// log.info(acutalBoardListRes.toString());						
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		assertEquals("본문으로만 이루어진 목록 갯수 비교",  1, acutalBoardListRes.getCnt());		
				
		for (BoardListRes.Board board : acutalBoardListRes.getBoardList()) {
			if (0 != board.getParentNo()) {
				fail("본문 글로만 이루어진 목록인데 댓글이 포함되었습니다");
			}
		}
	}
	
	@Test
	public void 계층형목록_정상() {
		String requestedUserIDForAdmin = "admin";
		String requestedUserIDForMember = "guest";
		
		int pageNo = 1;
		int pageSize = 20;
		
		BoardListReqServerTask boardListReqServerTask = null;
		try {
			boardListReqServerTask = new BoardListReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		BoardInfoAddReqServerTask boardInfoAddReqServerTask = null;
		try {
			boardInfoAddReqServerTask = new BoardInfoAddReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		BoardInfoAddReq boardInfoAddReq = new BoardInfoAddReq();
		boardInfoAddReq.setRequestedUserID(requestedUserIDForAdmin);
		boardInfoAddReq.setBoardName("계층형");
		boardInfoAddReq.setBoardListType(BoardListType.TREE.getValue());
		boardInfoAddReq.setBoardReplyPolicyType(BoardReplyPolicyType.ALL.getValue());
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.MEMBER.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.MEMBER.getValue());		
		
		BoardInfoAddRes boardInfoAddRes = null;
		
		try {
			boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
				
		class VirtualBoardTreeBuilder implements VirtualBoardTreeBuilderIF {
			@Override
			public BoardTree build(final short boardID) {
				String writerID = "test01";
				String otherID = "test02";

				BoardTree boardTree = new BoardTree();
				{
					BoardTreeNode root1BoardTreeNode = BoardTree
							.makeBoardTreeNodeWithoutTreeInfomation(boardID,
									writerID, "루트1", "루트1");

					{
						BoardTreeNode root1Child1BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, otherID, "루트1_자식1", "루트1_자식1");
						{
							BoardTreeNode root1Child1Child1BoardTreeNode = BoardTree
									.makeBoardTreeNodeWithoutTreeInfomation(
											boardID, otherID, "루트1_자식1_자식1",
											"루트1_자식1_자식1");
							{
								BoardTreeNode root1Child1Child1Child1BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, otherID,
												"루트1_자식1_자식1_자식1",
												"루트1_자식1_자식1_자식1");
								{
									BoardTreeNode root1Child1Child1Child1Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"루트1_자식1_자식1_자식1_자식1",
													"루트1_자식1_자식1_자식1_자식1");
									root1Child1Child1Child1BoardTreeNode
											.addChildNode(root1Child1Child1Child1Child1BoardTreeNode);
								}

								root1Child1Child1BoardTreeNode
										.addChildNode(root1Child1Child1Child1BoardTreeNode);
							}

							root1Child1BoardTreeNode
									.addChildNode(root1Child1Child1BoardTreeNode);
						}

						root1BoardTreeNode
								.addChildNode(root1Child1BoardTreeNode);
					}

					{
						BoardTreeNode root1Child2BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, writerID, "루트1_자식2", "루트1_자식2");

						{
							BoardTreeNode root1Child2Child1BoardTreeNode = BoardTree
									.makeBoardTreeNodeWithoutTreeInfomation(
											boardID, otherID, "루트1_자식2_자식1",
											"루트1_자식2_자식1");
							{
								BoardTreeNode root1Child2Child1Child1BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"루트1_자식2_자식1_자식1",
												"루트1_자식2_자식1_자식1");

								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child1BoardTreeNode);
							}

							{
								BoardTreeNode root1Child2Child1Child2BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"루트1_자식2_자식1_자식2",
												"루트1_자식2_자식1_자식2");
								{
									BoardTreeNode root1Child2Child1Child2Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"루트1_자식2_자식1_자식2_자식1",
													"루트1_자식2_자식3_자식2_자식1");
									root1Child2Child1Child2BoardTreeNode
											.addChildNode(root1Child2Child1Child2Child1BoardTreeNode);
								}

								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child2BoardTreeNode);
							}

							{
								BoardTreeNode root1Child2Child1Child3BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"루트1_자식2_자식1_자식3",
												"루트1_자식2_자식1_자식3");
								{
									BoardTreeNode root1Child2Child1Child3Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"루트1_자식2_자식1_자식3_자식1",
													"루트1_자식2_자식3_자식3_자식1");
									root1Child2Child1Child3BoardTreeNode
											.addChildNode(root1Child2Child1Child3Child1BoardTreeNode);
								}

								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child3BoardTreeNode);
							}

							root1Child2BoardTreeNode
									.addChildNode(root1Child2Child1BoardTreeNode);
						}
						root1BoardTreeNode
								.addChildNode(root1Child2BoardTreeNode);
					}

					{
						BoardTreeNode root1Child3BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, otherID, "루트1_자식3", "루트1_자식3");
						root1BoardTreeNode
								.addChildNode(root1Child3BoardTreeNode);
					}

					boardTree.addRootBoardTreeNode(root1BoardTreeNode);
				}
				
				{
					BoardTreeNode root1BoardTreeNode = BoardTree
							.makeBoardTreeNodeWithoutTreeInfomation(boardID,
									writerID, "루트2", "루트2");

					{
						BoardTreeNode root1Child1BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, otherID, "루트2_자식1", "루트2_자식1");
						{
							BoardTreeNode root1Child1Child1BoardTreeNode = BoardTree
									.makeBoardTreeNodeWithoutTreeInfomation(
											boardID, otherID, "루트2_자식1_자식1",
											"루트2_자식1_자식1");
							{
								BoardTreeNode root1Child1Child1Child1BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, otherID,
												"루트2_자식1_자식1_자식1",
												"루트2_자식1_자식1_자식1");
								{
									BoardTreeNode root1Child1Child1Child1Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"루트2_자식1_자식1_자식1_자식1",
													"루트2_자식1_자식1_자식1_자식1");
									root1Child1Child1Child1BoardTreeNode
											.addChildNode(root1Child1Child1Child1Child1BoardTreeNode);
								}

								root1Child1Child1BoardTreeNode
										.addChildNode(root1Child1Child1Child1BoardTreeNode);
							}

							root1Child1BoardTreeNode
									.addChildNode(root1Child1Child1BoardTreeNode);
						}

						root1BoardTreeNode
								.addChildNode(root1Child1BoardTreeNode);
					}

					{
						BoardTreeNode root1Child2BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, writerID, "루트2_자식2", "루트2_자식2");

						{
							BoardTreeNode root1Child2Child1BoardTreeNode = BoardTree
									.makeBoardTreeNodeWithoutTreeInfomation(
											boardID, otherID, "루트2_자식2_자식1",
											"루트2_자식2_자식1");
							{
								BoardTreeNode root1Child2Child1Child1BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"루트2_자식2_자식1_자식1",
												"루트2_자식2_자식1_자식1");

								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child1BoardTreeNode);
							}

							{
								BoardTreeNode root1Child2Child1Child2BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"루트2_자식2_자식1_자식2",
												"루트2_자식2_자식1_자식2");
								{
									BoardTreeNode root1Child2Child1Child2Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"루트2_자식2_자식1_자식2_자식1",
													"루트2_자식2_자식3_자식2_자식1");
									root1Child2Child1Child2BoardTreeNode
											.addChildNode(root1Child2Child1Child2Child1BoardTreeNode);
								}

								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child2BoardTreeNode);
							}

							{
								BoardTreeNode root1Child2Child1Child3BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"루트2_자식2_자식1_자식3",
												"루트2_자식2_자식1_자식3");
								{
									BoardTreeNode root1Child2Child1Child3Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"루트2_자식2_자식1_자식3_자식1",
													"루트2_자식2_자식3_자식3_자식1");
									root1Child2Child1Child3BoardTreeNode
											.addChildNode(root1Child2Child1Child3Child1BoardTreeNode);
								}

								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child3BoardTreeNode);
							}

							root1Child2BoardTreeNode
									.addChildNode(root1Child2Child1BoardTreeNode);
						}
						root1BoardTreeNode
								.addChildNode(root1Child2BoardTreeNode);
					}

					{
						BoardTreeNode root1Child3BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, otherID, "루트2_자식3", "루트2_자식3");
						root1BoardTreeNode
								.addChildNode(root1Child3BoardTreeNode);
					}

					boardTree.addRootBoardTreeNode(root1BoardTreeNode);
				}

				return boardTree;
			}
		}

		VirtualBoardTreeBuilderIF virtualBoardTreeBuilder = new VirtualBoardTreeBuilder();
		BoardTree boardTree = virtualBoardTreeBuilder.build(boardInfoAddRes.getBoardID());
		boardTree.makeDBRecord(TEST_DBCP_NAME);
		
		BoardTreeNode blockBoardTreeNode = boardTree.find("루트1");

		if (null == blockBoardTreeNode) {
			fail("목표 게시글(제목:루트1) 찾기 실패");
		}

		
		BoardBlockReq blcokBoardBlockReq = new BoardBlockReq();
		blcokBoardBlockReq.setRequestedUserID(requestedUserIDForAdmin);
		blcokBoardBlockReq.setBoardID(blockBoardTreeNode.getBoardID());
		blcokBoardBlockReq.setBoardNo(blockBoardTreeNode.getBoardNo());
		blcokBoardBlockReq.setIp("127.0.0.8");

		BoardBlockReqServerTask boardBlockReqServerTask = null;
		try {
			boardBlockReqServerTask = new BoardBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			MessageResultRes messageResultRes = boardBlockReqServerTask.doWork(
					TEST_DBCP_NAME, blcokBoardBlockReq);

			log.info(messageResultRes.toString());

			if (!messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}

		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
		
		BoardListReq boardListReq = new BoardListReq();
		boardListReq.setRequestedUserID(requestedUserIDForMember);
		boardListReq.setBoardID(boardInfoAddRes.getBoardID());
		boardListReq.setPageNo(pageNo);
		boardListReq.setPageSize(pageSize);			

		BoardListRes acutalBoardListRes = null;
		try {
			acutalBoardListRes = boardListReqServerTask.doWork(
					TEST_DBCP_NAME, boardListReq);
			// log.info(acutalBoardListRes.toString());						
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
				
		assertEquals("계층형 목록 갯수 비교",  boardTree.getTotal()/2, acutalBoardListRes.getCnt());
	}
	

	@Test
	public void 게시판목록_트리관련항목유효성검사() {
		final short boardID = 3;

		class VirtualBoardTreeBuilder implements VirtualBoardTreeBuilderIF {
			@Override
			public BoardTree build(final short boardID) {
				String writerID = "test01";
				String otherID = "test02";

				BoardTree boardTree = new BoardTree();
				{
					BoardTreeNode root1BoardTreeNode = BoardTree
							.makeBoardTreeNodeWithoutTreeInfomation(boardID,
									writerID, "루트1", "루트1");

					{
						BoardTreeNode root1Child1BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, otherID, "루트1_자식1", "루트1_자식1");
						{
							BoardTreeNode root1Child1Child1BoardTreeNode = BoardTree
									.makeBoardTreeNodeWithoutTreeInfomation(
											boardID, otherID, "루트1_자식1_자식1",
											"루트1_자식1_자식1");
							{
								BoardTreeNode root1Child1Child1Child1BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, otherID,
												"루트1_자식1_자식1_자식1",
												"루트1_자식1_자식1_자식1");
								{
									BoardTreeNode root1Child1Child1Child1Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"루트1_자식1_자식1_자식1_자식1",
													"루트1_자식1_자식1_자식1_자식1");
									root1Child1Child1Child1BoardTreeNode
											.addChildNode(root1Child1Child1Child1Child1BoardTreeNode);
								}

								root1Child1Child1BoardTreeNode
										.addChildNode(root1Child1Child1Child1BoardTreeNode);
							}

							root1Child1BoardTreeNode
									.addChildNode(root1Child1Child1BoardTreeNode);
						}

						root1BoardTreeNode
								.addChildNode(root1Child1BoardTreeNode);
					}

					{
						BoardTreeNode root1Child2BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, writerID, "루트1_자식2", "루트1_자식2");

						{
							BoardTreeNode root1Child2Child1BoardTreeNode = BoardTree
									.makeBoardTreeNodeWithoutTreeInfomation(
											boardID, otherID, "루트1_자식2_자식1",
											"루트1_자식2_자식1");
							{
								BoardTreeNode root1Child2Child1Child1BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"루트1_자식2_자식1_자식1",
												"루트1_자식2_자식1_자식1");

								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child1BoardTreeNode);
							}

							{
								BoardTreeNode root1Child2Child1Child2BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"루트1_자식2_자식1_자식2",
												"루트1_자식2_자식1_자식2");
								{
									BoardTreeNode root1Child2Child1Child2Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"루트1_자식2_자식1_자식2_자식1",
													"루트1_자식2_자식3_자식2_자식1");
									root1Child2Child1Child2BoardTreeNode
											.addChildNode(root1Child2Child1Child2Child1BoardTreeNode);
								}

								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child2BoardTreeNode);
							}

							{
								BoardTreeNode root1Child2Child1Child3BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"루트1_자식2_자식1_자식3",
												"루트1_자식2_자식1_자식3");
								{
									BoardTreeNode root1Child2Child1Child3Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"루트1_자식2_자식1_자식3_자식1",
													"루트1_자식2_자식3_자식3_자식1");
									root1Child2Child1Child3BoardTreeNode
											.addChildNode(root1Child2Child1Child3Child1BoardTreeNode);
								}

								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child3BoardTreeNode);
							}

							root1Child2BoardTreeNode
									.addChildNode(root1Child2Child1BoardTreeNode);
						}
						root1BoardTreeNode
								.addChildNode(root1Child2BoardTreeNode);
					}

					{
						BoardTreeNode root1Child3BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, otherID, "루트1_자식3", "루트1_자식3");
						root1BoardTreeNode
								.addChildNode(root1Child3BoardTreeNode);
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
		boardListReq.setRequestedUserID("guest");
		boardListReq.setBoardID(boardID);
		boardListReq.setPageNo(pageNo);
		boardListReq.setPageSize(pageSize);

		BoardListReqServerTask boardListReqServerTask = null;
		try {
			boardListReqServerTask = new BoardListReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardListRes firstBoardListRes = null;

		try {
			firstBoardListRes = boardListReqServerTask.doWork(TEST_DBCP_NAME,
					boardListReq);

		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		assertEquals("게시판 트리 노드 총 갯수와 게시판 레코드 총 갯수 비교", boardTree.getTotal(),
				firstBoardListRes.getTotal());

		assertEquals("게시판 트리 노드 총 갯수와 게시판 레코드 목록 갯수 비교", boardTree.getTotal(),
				firstBoardListRes.getCnt());

		java.util.List<BoardListRes.Board> firstBoardList = firstBoardListRes
				.getBoardList();

		for (BoardListRes.Board board : firstBoardList) {
			BoardTreeNode boardTreeNode = boardTree.find(board.getSubject());
			if (null == boardTreeNode) {
				String errorMessage = new StringBuilder()
						.append("this 'boardNoToBoardTreeNodeHash' map contains no mapping for the key(boardNo=")
						.append(board.getBoardNo()).append(")").toString();

				fail(errorMessage);
			}

			// log.info();

			assertEquals("게시판 트리 노드의 그룹 번호와 게시판 레코드의 그룹 번호 비교",
					boardTreeNode.getGroupNo(), board.getGroupNo());

			assertEquals("게시판 트리 노드의 그룹 시퀀스와 게시판 레코드의 그룹 시퀀스 비교",
					boardTreeNode.getGroupSeq(), board.getGroupSeq());

			assertEquals("게시판 트리 노드의 부모번호와 게시판 레코드의 부모번호 비교",
					boardTreeNode.getParentNo(), board.getParentNo());

			assertEquals(
					"게시판 트리 노드의 트리 깊이와 게시판 레코드의 트리 깊이 비교" + board.getBoardNo(),
					boardTreeNode.getDepth(), board.getDepth());

			assertEquals("게시판 트리 노드의 제목과 게시판 레코드의 제목 비교",
					boardTreeNode.getSubject(), board.getSubject());
		}
	}	

	@Test
	public void 계획된_게시판_테스트_데이터_실사화_검증() {
		final short boardID = 3;
	
		class VirtualBoardTreeBuilder implements VirtualBoardTreeBuilderIF {
			@Override
			public BoardTree build(final short boardID) {
				String writerID = "test01";
				String otherID = "test02";
	
				BoardTree boardTree = new BoardTree();
				{
					BoardTreeNode root1BoardTreeNode = BoardTree
							.makeBoardTreeNodeWithoutTreeInfomation(boardID,
									writerID, "루트1", "루트1");
	
					{
						BoardTreeNode root1Child1BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, otherID, "루트1_자식1", "루트1_자식1");
						{
							BoardTreeNode root1Child1Child1BoardTreeNode = BoardTree
									.makeBoardTreeNodeWithoutTreeInfomation(
											boardID, otherID, "루트1_자식1_자식1",
											"루트1_자식1_자식1");
							{
								BoardTreeNode root1Child1Child1Child1BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, otherID,
												"루트1_자식1_자식1_자식1",
												"루트1_자식1_자식1_자식1");
								{
									BoardTreeNode root1Child1Child1Child1Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"루트1_자식1_자식1_자식1_자식1",
													"루트1_자식1_자식1_자식1_자식1");
									root1Child1Child1Child1BoardTreeNode
											.addChildNode(root1Child1Child1Child1Child1BoardTreeNode);
								}
	
								root1Child1Child1BoardTreeNode
										.addChildNode(root1Child1Child1Child1BoardTreeNode);
							}
	
							root1Child1BoardTreeNode
									.addChildNode(root1Child1Child1BoardTreeNode);
						}
	
						root1BoardTreeNode
								.addChildNode(root1Child1BoardTreeNode);
					}
	
					{
						BoardTreeNode root1Child2BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, writerID, "루트1_자식2", "루트1_자식2");
	
						{
							BoardTreeNode root1Child2Child1BoardTreeNode = BoardTree
									.makeBoardTreeNodeWithoutTreeInfomation(
											boardID, otherID, "루트1_자식2_자식1",
											"루트1_자식2_자식1");
							{
								BoardTreeNode root1Child2Child1Child1BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"루트1_자식2_자식1_자식1",
												"루트1_자식2_자식1_자식1");
	
								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child1BoardTreeNode);
							}
	
							{
								BoardTreeNode root1Child2Child1Child2BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"루트1_자식2_자식1_자식2",
												"루트1_자식2_자식1_자식2");
								{
									BoardTreeNode root1Child2Child1Child2Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"루트1_자식2_자식1_자식2_자식1",
													"루트1_자식2_자식3_자식2_자식1");
									root1Child2Child1Child2BoardTreeNode
											.addChildNode(root1Child2Child1Child2Child1BoardTreeNode);
								}
	
								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child2BoardTreeNode);
							}
	
							{
								BoardTreeNode root1Child2Child1Child3BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"루트1_자식2_자식1_자식3",
												"루트1_자식2_자식1_자식3");
								{
									BoardTreeNode root1Child2Child1Child3Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"루트1_자식2_자식1_자식3_자식1",
													"루트1_자식2_자식3_자식3_자식1");
									root1Child2Child1Child3BoardTreeNode
											.addChildNode(root1Child2Child1Child3Child1BoardTreeNode);
								}
	
								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child3BoardTreeNode);
							}
	
							root1Child2BoardTreeNode
									.addChildNode(root1Child2Child1BoardTreeNode);
						}
						root1BoardTreeNode
								.addChildNode(root1Child2BoardTreeNode);
					}
	
					{
						BoardTreeNode root1Child3BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, otherID, "루트1_자식3", "루트1_자식3");
						root1BoardTreeNode
								.addChildNode(root1Child3BoardTreeNode);
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
		boardListReq.setRequestedUserID("guest");
		boardListReq.setBoardID(boardID);
		boardListReq.setPageNo(pageNo);
		boardListReq.setPageSize(pageSize);
	
		BoardListReqServerTask boardListReqServerTask = null;
		try {
			boardListReqServerTask = new BoardListReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardListRes firstBoardListRes = null;
	
		try {
			firstBoardListRes = boardListReqServerTask.doWork(TEST_DBCP_NAME,
					boardListReq);
	
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	
		assertEquals("게시판 트리 노드 총 갯수와 게시판 레코드 총 갯수 비교", boardTree.getTotal(),
				firstBoardListRes.getTotal());
	
		assertEquals("게시판 트리 노드 총 갯수와 게시판 레코드 목록 갯수 비교", boardTree.getTotal(),
				firstBoardListRes.getCnt());
	
		java.util.List<BoardListRes.Board> firstBoardList = firstBoardListRes
				.getBoardList();
	
		for (BoardListRes.Board board : firstBoardList) {
			BoardTreeNode boardTreeNode = boardTree.find(board.getSubject());
			if (null == boardTreeNode) {
				String errorMessage = new StringBuilder()
						.append("this 'boardNoToBoardTreeNodeHash' map contains no mapping for the key(boardNo=")
						.append(board.getBoardNo()).append(")").toString();
	
				fail(errorMessage);
			}
	
			// log.info();
	
			assertEquals("게시판 트리 노드의 그룹 번호와 게시판 레코드의 그룹 번호 비교",
					boardTreeNode.getGroupNo(), board.getGroupNo());
	
			assertEquals("게시판 트리 노드의 그룹 시퀀스와 게시판 레코드의 그룹 시퀀스 비교",
					boardTreeNode.getGroupSeq(), board.getGroupSeq());
	
			assertEquals("게시판 트리 노드의 부모번호와 게시판 레코드의 부모번호 비교",
					boardTreeNode.getParentNo(), board.getParentNo());
	
			assertEquals(
					"게시판 트리 노드의 트리 깊이와 게시판 레코드의 트리 깊이 비교" + board.getBoardNo(),
					boardTreeNode.getDepth(), board.getDepth());
	
			assertEquals("게시판 트리 노드의 제목과 게시판 레코드의 제목 비교",
					boardTreeNode.getSubject(), board.getSubject());
		}
	}

}
