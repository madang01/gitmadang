package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardVoteTb.SB_BOARD_VOTE_TB;
import static kr.pe.codda.impl.jooq.tables.SbSeqTb.SB_SEQ_TB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.DBCPDataSourceNotFoundException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.BoardBlockReq.BoardBlockReq;
import kr.pe.codda.impl.message.BoardDeleteReq.BoardDeleteReq;
import kr.pe.codda.impl.message.BoardDetailReq.BoardDetailReq;
import kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes;
import kr.pe.codda.impl.message.BoardListReq.BoardListReq;
import kr.pe.codda.impl.message.BoardListRes.BoardListRes;
import kr.pe.codda.impl.message.BoardModifyReq.BoardModifyReq;
import kr.pe.codda.impl.message.BoardModifyRes.BoardModifyRes;
import kr.pe.codda.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.codda.impl.message.BoardReplyRes.BoardReplyRes;
import kr.pe.codda.impl.message.BoardUnBlockReq.BoardUnBlockReq;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.BoardTree;
import kr.pe.codda.server.lib.BoardTreeNode;
import kr.pe.codda.server.lib.BoardType;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.SequenceType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.VirtualBoardTreeBuilderIF;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BoardIntegrationTest extends AbstractJunitTest {
	private final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;

	// ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();
		ServerDBUtil.initializeDBEnvoroment(TEST_DBCP_NAME);

		{
			String userID = "admin";
			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's',
					(byte) 't', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
					(byte) '$' };
			String nickname = "단위테스터용어드민";
			String pwdHint = "힌트 그것이 알고싶다";
			String pwdAnswer = "힌트답변 말이여 방구여";
			String ip = "127.0.0.1";

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME,
						MemberRoleType.ADMIN, userID, nickname, pwdHint,
						pwdAnswer, passwordBytes, ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder(
						"기존 회원과 중복되는 아이디[").append(userID).append("] 입니다")
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
			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's',
					(byte) 't', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
					(byte) '$' };
			String nickname = "단위테스터용아이디1";
			String pwdHint = "힌트 그것이 알고싶다";
			String pwdAnswer = "힌트답변 말이여 방구여";
			String ip = "127.0.0.1";

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME,
						MemberRoleType.USER, userID, nickname, pwdHint,
						pwdAnswer, passwordBytes, ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder(
						"기존 회원과 중복되는 아이디[").append(userID).append("] 입니다")
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
			byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's',
					(byte) 't', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
					(byte) '$' };
			String nickname = "단위테스터용아이디2";
			String pwdHint = "힌트 그것이 알고싶다";
			String pwdAnswer = "힌트답변 말이여 방구여";
			String ip = "127.0.0.1";

			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME,
						MemberRoleType.USER, userID, nickname, pwdHint,
						pwdAnswer, passwordBytes, ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder(
						"기존 회원과 중복되는 아이디[").append(userID).append("] 입니다")
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
		UByte freeBoardSequenceID = UByte.valueOf(SequenceType.FREE_BOARD
				.getSequenceID());

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

			create.update(SB_SEQ_TB)
					.set(SB_SEQ_TB.SQ_VALUE, UInteger.valueOf(1))
					.where(SB_SEQ_TB.SQ_ID.eq(freeBoardSequenceID)).execute();

			create.delete(SB_BOARD_VOTE_TB).execute();
			create.delete(SB_BOARD_FILELIST_TB).execute();
			create.delete(SB_BOARD_HISTORY_TB).execute();
			create.delete(SB_BOARD_TB).execute();

			create.update(SB_BOARD_INFO_TB)
					.set(SB_BOARD_INFO_TB.ADMIN_TOTAL, 0)
					.set(SB_BOARD_INFO_TB.USER_TOTAL, 0).execute();

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

			for (BoardType boardType : BoardType.values()) {
				UByte boardID = UByte.valueOf(boardType.getBoardID());
				int adminCount = create.selectCount().from(SB_BOARD_TB)
						.where(SB_BOARD_TB.BOARD_ID.eq(boardID)).fetchOne()
						.value1();

				int userCount = create
						.selectCount()
						.from(SB_BOARD_TB)
						.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
						.and(SB_BOARD_TB.BOARD_ST.eq(BoardStateType.OK
								.getValue())).fetchOne().value1();

				Record2<Integer, Integer> boardInfoRecord = create
						.select(SB_BOARD_INFO_TB.ADMIN_TOTAL,
								SB_BOARD_INFO_TB.USER_TOTAL)
						.from(SB_BOARD_INFO_TB)
						.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID))
						.fetchOne();

				int adminCountOfBoardInfoTable = boardInfoRecord
						.getValue(SB_BOARD_INFO_TB.ADMIN_TOTAL);
				int userCountOfBoardInfoTable = boardInfoRecord
						.getValue(SB_BOARD_INFO_TB.USER_TOTAL);

				assertEquals("어드민인 경우 게시판 전체 글 갯수 비교", adminCount,
						adminCountOfBoardInfoTable);
				assertEquals("유저인 경우 게시판 전체 글 갯수 비교", userCount,
						userCountOfBoardInfoTable);
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

	@Test
	public void 목록조회테스트_초기상태_게스트() {
		int pageNo = 1;
		int pageSize = 20;

		BoardListReq boardListReq = new BoardListReq();
		boardListReq.setRequestedUserID("guest");
		boardListReq.setBoardID(BoardType.FREE.getBoardID());
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
	public void 목록조회테스트_초기상태_어드민() {
		int pageNo = 1;
		int pageSize = 20;

		BoardListReq boardListReq = new BoardListReq();
		boardListReq.setRequestedUserID("admin");
		boardListReq.setBoardID(BoardType.FREE.getBoardID());
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
	public void 목록조회테스트_모든종류의게시글상태_게스트() {
		String writerID = "test01";
		String adminID = "admin";
		int pageNo = 1;
		int pageSize = 20;

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(writerID);
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("삭제::제목");
		boardWriteReq.setContent("삭제::내용");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();

		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);

		BoardWriteRes deleteSateBoardWriteRes = null;
		try {
			deleteSateBoardWriteRes = boardWriteReqServerTask.doWork(
					TEST_DBCP_NAME, boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(writerID);
		boardDeleteReq.setBoardID(deleteSateBoardWriteRes.getBoardID());
		boardDeleteReq.setBoardNo(deleteSateBoardWriteRes.getBoardNo());

		try {
			MessageResultRes messageResultRes = boardDeleteReqServerTask
					.doWork(TEST_DBCP_NAME, boardDeleteReq);

			if (!messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}

		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		boardWriteReq.setSubject("블락::제목");
		boardWriteReq.setContent("블락::내용");
		BoardWriteRes blockSateBoardWriteRes = null;
		try {
			blockSateBoardWriteRes = boardWriteReqServerTask.doWork(
					TEST_DBCP_NAME, boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardBlockReqServerTask boardBlockReqServerTask = null;
		try {
			boardBlockReqServerTask = new BoardBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardBlockReq boardBlockReq = new BoardBlockReq();
		boardBlockReq.setRequestedUserID(adminID);
		boardBlockReq.setBoardID(blockSateBoardWriteRes.getBoardID());
		boardBlockReq.setBoardNo(blockSateBoardWriteRes.getBoardNo());

		try {
			MessageResultRes messageResultRes = boardBlockReqServerTask.doWork(
					TEST_DBCP_NAME, boardBlockReq);
			log.info(messageResultRes.toString());
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		boardWriteReq.setSubject("정상::제목");
		boardWriteReq.setContent("정상::내용");
		BoardWriteRes okSateBoardWriteRes = null;
		try {
			okSateBoardWriteRes = boardWriteReqServerTask.doWork(
					TEST_DBCP_NAME, boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardListReq boardListReq = new BoardListReq();
		boardListReq.setRequestedUserID("guest");
		boardListReq.setBoardID(BoardType.FREE.getBoardID());
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

			if (acutalBoardListRes.getTotal() != 1) {
				fail("DB 초기 상태에에서 총 갯수가 1이 아닙니다");
			}

			if (acutalBoardListRes.getCnt() != 1) {
				fail("DB 초기 상태에에서 총 갯수가 1이 아닙니다");
			}

			BoardListRes.Board board = acutalBoardListRes.getBoardList().get(0);

			assertEquals("게시판 번호 비교", okSateBoardWriteRes.getBoardNo(),
					board.getBoardNo());
			assertEquals("게시판 제목 비교", boardWriteReq.getSubject(),
					board.getSubject());
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 목록조회테스트_모든종류의게시글상태_어드민() {
		String writerID = "test01";
		String adminID = "admin";
		int pageNo = 1;
		int pageSize = 20;

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(writerID);
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("삭제::제목");
		boardWriteReq.setContent("삭제::내용");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();

		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);

		BoardWriteRes deleteSateBoardWriteRes = null;
		try {
			deleteSateBoardWriteRes = boardWriteReqServerTask.doWork(
					TEST_DBCP_NAME, boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(writerID);
		boardDeleteReq.setBoardID(deleteSateBoardWriteRes.getBoardID());
		boardDeleteReq.setBoardNo(deleteSateBoardWriteRes.getBoardNo());

		try {
			MessageResultRes messageResultRes = boardDeleteReqServerTask
					.doWork(TEST_DBCP_NAME, boardDeleteReq);
			log.info(messageResultRes.toString());
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		boardWriteReq.setSubject("블락::제목");
		boardWriteReq.setContent("블락::내용");
		BoardWriteRes blockSateBoardWriteRes = null;
		try {
			blockSateBoardWriteRes = boardWriteReqServerTask.doWork(
					TEST_DBCP_NAME, boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardBlockReqServerTask boardBlockReqServerTask = null;
		try {
			boardBlockReqServerTask = new BoardBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardBlockReq boardBlockReq = new BoardBlockReq();
		boardBlockReq.setRequestedUserID(adminID);
		boardBlockReq.setBoardID(blockSateBoardWriteRes.getBoardID());
		boardBlockReq.setBoardNo(blockSateBoardWriteRes.getBoardNo());

		try {
			MessageResultRes messageResultRes = boardBlockReqServerTask.doWork(
					TEST_DBCP_NAME, boardBlockReq);
			log.info(messageResultRes.toString());
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		boardWriteReq.setSubject("정상::제목");
		boardWriteReq.setContent("정상::내용");
		@SuppressWarnings("unused")
		BoardWriteRes okSateBoardWriteRes = null;
		try {
			okSateBoardWriteRes = boardWriteReqServerTask.doWork(
					TEST_DBCP_NAME, boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardListReq boardListReq = new BoardListReq();
		boardListReq.setRequestedUserID(adminID);
		boardListReq.setBoardID(BoardType.FREE.getBoardID());
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

			if (acutalBoardListRes.getTotal() != 3) {
				fail("DB 초기 상태에에서 총 갯수가 3이 아닙니다");
			}

			if (acutalBoardListRes.getCnt() != 3) {
				fail("DB 초기 상태에에서 총 갯수가 3이 아닙니다");
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
	public void 게시판목록_트리관련항목유효성검사() {
		final BoardType boardType = BoardType.FREE;

		class VirtualBoardTreeBuilder implements VirtualBoardTreeBuilderIF {
			@Override
			public BoardTree build(final BoardType boardType) {
				String writerID = "test01";
				String otherID = "test02";

				BoardTree boardTree = new BoardTree();

				final short boardID = boardType.getBoardID();
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
		BoardTree boardTree = virtualBoardTreeBuilder.build(boardType);
		boardTree.makeDBRecord(TEST_DBCP_NAME);

		int pageNo = 1;
		int pageSize = boardTree.getHashSize();

		BoardListReq boardListReq = new BoardListReq();
		boardListReq.setRequestedUserID("guest");
		boardListReq.setBoardID(boardType.getBoardID());
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
	public void 게시글삭제_대상글없음() {
		String writerID = "test01";

		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(writerID);
		boardDeleteReq.setBoardID(BoardType.FREE.getBoardID());
		boardDeleteReq.setBoardNo(1);

		try {
			boardDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardDeleteReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "1.해당 게시글이 존재 하지 않습니다";

			assertEquals("삭제 대상 글이 없을때  경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글삭제_본인글아님() {
		String writerID = "test01";
		String otherID = "test02";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(writerID);
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("삭제::타인::제목");
		boardWriteReq.setContent("삭제::타인::내용");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();

		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);

		BoardWriteRes deleteSateBoardWriteRes = null;
		try {
			deleteSateBoardWriteRes = boardWriteReqServerTask.doWork(
					TEST_DBCP_NAME, boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(otherID);
		boardDeleteReq.setBoardID(deleteSateBoardWriteRes.getBoardID());
		boardDeleteReq.setBoardNo(deleteSateBoardWriteRes.getBoardNo());

		try {
			boardDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardDeleteReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "타인 글은 삭제 할 수 없습니다";

			assertEquals("타인 글 삭제할때 경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글삭제_이미삭제된글() {
		String writerID = "test01";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(writerID);
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("삭제::제목");
		boardWriteReq.setContent("삭제::내용");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();

		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);

		BoardWriteRes deleteSateBoardWriteRes = null;
		try {
			deleteSateBoardWriteRes = boardWriteReqServerTask.doWork(
					TEST_DBCP_NAME, boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(writerID);
		boardDeleteReq.setBoardID(deleteSateBoardWriteRes.getBoardID());
		boardDeleteReq.setBoardNo(deleteSateBoardWriteRes.getBoardNo());

		try {
			boardDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardDeleteReq);

		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		boardDeleteReq.setRequestedUserID(writerID);
		try {
			boardDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardDeleteReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "해당 게시글은 삭제된 글입니다";

			assertEquals("타인 글 삭제할때 경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글삭제_정상() {
		String writerID = "test01";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(writerID);
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("삭제::제목");
		boardWriteReq.setContent("삭제::내용");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();

		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);

		BoardWriteRes deleteSateBoardWriteRes = null;
		try {
			deleteSateBoardWriteRes = boardWriteReqServerTask.doWork(
					TEST_DBCP_NAME, boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(writerID);
		boardDeleteReq.setBoardID(deleteSateBoardWriteRes.getBoardID());
		boardDeleteReq.setBoardNo(deleteSateBoardWriteRes.getBoardNo());

		try {
			boardDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardDeleteReq);

		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setBoardID(deleteSateBoardWriteRes.getBoardID());
		boardDetailReq.setBoardNo(deleteSateBoardWriteRes.getBoardNo());
		boardDetailReq.setRequestedUserID(writerID);

		BoardDetailReqServerTask boardDetailReqServerTask = null;
		try {
			boardDetailReqServerTask = new BoardDetailReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		try {
			boardDetailReqServerTask.doWork(TEST_DBCP_NAME, boardDetailReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "해당 게시글은 삭제된 글입니다";

			assertEquals("게시글 삭제 후 에러 검사", expectedErrorMessage,
					acutalErrorMessage);

		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}

		boardDetailReq.setRequestedUserID("admin");
		try {
			BoardDetailRes boardDetailRes = boardDetailReqServerTask.doWork(
					TEST_DBCP_NAME, boardDetailReq);

			// log.info(boardDetailRes.toString());

			assertEquals("게시글 삭제 후 에러 검사", BoardStateType.DELETE.getValue(),
					boardDetailRes.getBoardSate());

		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글삭제_차단된글() {
		String writerID = "test01";
		String adminID = "admin";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(writerID);
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContent("차단::내용");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();

		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);

		BoardWriteRes blockSateBoardWriteRes = null;
		try {
			blockSateBoardWriteRes = boardWriteReqServerTask.doWork(
					TEST_DBCP_NAME, boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardBlockReqServerTask boardBlockReqServerTask = null;
		try {
			boardBlockReqServerTask = new BoardBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardBlockReq boardBlockReq = new BoardBlockReq();
		boardBlockReq.setRequestedUserID(adminID);
		boardBlockReq.setBoardID(blockSateBoardWriteRes.getBoardID());
		boardBlockReq.setBoardNo(blockSateBoardWriteRes.getBoardNo());

		try {
			MessageResultRes messageResultRes = boardBlockReqServerTask.doWork(
					TEST_DBCP_NAME, boardBlockReq);
			log.info(messageResultRes.toString());
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(writerID);
		boardDeleteReq.setBoardID(blockSateBoardWriteRes.getBoardID());
		boardDeleteReq.setBoardNo(blockSateBoardWriteRes.getBoardNo());

		try {
			boardDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardDeleteReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "해당 게시글은 관리자에 의해 차단된 글입니다";

			assertEquals("관리자에 의해 차단된 글 삭제할때 경고 메시지인지 검사",
					expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글차단_대상글없음() {
		String adminID = "admin";

		BoardBlockReqServerTask boardBlockReqServerTask = null;
		try {
			boardBlockReqServerTask = new BoardBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardBlockReq boardBlockReq = new BoardBlockReq();
		boardBlockReq.setRequestedUserID(adminID);
		boardBlockReq.setBoardID(BoardType.FREE.getBoardID());
		boardBlockReq.setBoardNo(1);

		try {
			boardBlockReqServerTask.doWork(TEST_DBCP_NAME, boardBlockReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "1.해당 게시글이 존재 하지 않습니다";

			assertEquals("대상 글 없는 경우 차단할때 경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글차단_일반유저() {
		String writerID = "test01";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(writerID);
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContent("차단::내용");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();

		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);

		BoardWriteRes blockSateBoardWriteRes = null;
		try {
			blockSateBoardWriteRes = boardWriteReqServerTask.doWork(
					TEST_DBCP_NAME, boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardBlockReqServerTask boardBlockReqServerTask = null;
		try {
			boardBlockReqServerTask = new BoardBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardBlockReq boardBlockReq = new BoardBlockReq();
		boardBlockReq.setRequestedUserID(writerID);
		boardBlockReq.setBoardID(blockSateBoardWriteRes.getBoardID());
		boardBlockReq.setBoardNo(blockSateBoardWriteRes.getBoardNo());

		try {
			boardBlockReqServerTask.doWork(TEST_DBCP_NAME, boardBlockReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "게시글 차단은 관리자 전용 서비스입니다";

			assertEquals("게시글 차단 기능 호출자 관리자 여부 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글차단_삭제된글차단() {
		String writerID = "test01";
		String adminID = "admin";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(writerID);
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContent("차단::내용");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();

		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);

		BoardWriteRes delteSateBoardWriteRes = null;
		try {
			delteSateBoardWriteRes = boardWriteReqServerTask.doWork(
					TEST_DBCP_NAME, boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(writerID);
		boardDeleteReq.setBoardID(delteSateBoardWriteRes.getBoardID());
		boardDeleteReq.setBoardNo(delteSateBoardWriteRes.getBoardNo());

		try {
			boardDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardDeleteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardBlockReqServerTask boardBlockReqServerTask = null;
		try {
			boardBlockReqServerTask = new BoardBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardBlockReq boardBlockReq = new BoardBlockReq();
		boardBlockReq.setRequestedUserID(adminID);
		boardBlockReq.setBoardID(delteSateBoardWriteRes.getBoardID());
		boardBlockReq.setBoardNo(delteSateBoardWriteRes.getBoardNo());

		try {
			boardBlockReqServerTask.doWork(TEST_DBCP_NAME, boardBlockReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "해당 게시글은 삭제된 글입니다";

			assertEquals("이미 삭제된 글 차단할때 경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글차단_차단된글재차단() {
		String writerID = "test01";
		String adminID = "admin";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(writerID);
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContent("차단::내용");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();

		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);

		BoardWriteRes delteSateBoardWriteRes = null;
		try {
			delteSateBoardWriteRes = boardWriteReqServerTask.doWork(
					TEST_DBCP_NAME, boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardBlockReqServerTask boardBlockReqServerTask = null;
		try {
			boardBlockReqServerTask = new BoardBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardBlockReq boardBlockReq = new BoardBlockReq();
		boardBlockReq.setRequestedUserID(adminID);
		boardBlockReq.setBoardID(delteSateBoardWriteRes.getBoardID());
		boardBlockReq.setBoardNo(delteSateBoardWriteRes.getBoardNo());

		try {
			boardBlockReqServerTask.doWork(TEST_DBCP_NAME, boardBlockReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		try {
			boardBlockReqServerTask.doWork(TEST_DBCP_NAME, boardBlockReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "해당 게시글은 관리자에 의해 차단된 글입니다";

			assertEquals("이미 차단된 글 다시 차단할때 경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 계획된_게시판_테스트_데이터_실사화_검증() {
		final BoardType boardType = BoardType.FREE;

		class VirtualBoardTreeBuilder implements VirtualBoardTreeBuilderIF {
			@Override
			public BoardTree build(final BoardType boardType) {
				String writerID = "test01";
				String otherID = "test02";

				BoardTree boardTree = new BoardTree();

				final short boardID = boardType.getBoardID();
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
		BoardTree boardTree = virtualBoardTreeBuilder.build(boardType);
		boardTree.makeDBRecord(TEST_DBCP_NAME);

		int pageNo = 1;
		int pageSize = boardTree.getHashSize();

		BoardListReq boardListReq = new BoardListReq();
		boardListReq.setRequestedUserID("guest");
		boardListReq.setBoardID(boardType.getBoardID());
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
	public void 게시판차단_블락포함된블락() {
		final BoardType boardType = BoardType.FREE;

		class VirtualBoardTreeBuilder implements VirtualBoardTreeBuilderIF {
			@Override
			public BoardTree build(final BoardType boardType) {
				String writerID = "test01";
				String otherID = "test02";

				BoardTree boardTree = new BoardTree();

				final short boardID = boardType.getBoardID();
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
		BoardTree boardTree = virtualBoardTreeBuilder.build(boardType);
		boardTree.makeDBRecord(TEST_DBCP_NAME);

		BoardTreeNode firstBlockBoardTreeNode = boardTree
				.find("루트1_자식2_자식1_자식2");

		if (null == firstBlockBoardTreeNode) {
			fail("첫번째 블락 대상 글(제목:루트1_자식2_자식1_자식2) 찾기 실패");
		}

		BoardTreeNode deleteBoardTreeNode = boardTree
				.find("루트1_자식2_자식1_자식2_자식1");

		if (null == deleteBoardTreeNode) {
			fail("삭제 대상 글(제목:루트1_자식2_자식1_자식2_자식1) 찾기 실패");
		}

		BoardTreeNode secondBlockBoardTreeNode = boardTree.find("루트1_자식2");

		if (null == secondBlockBoardTreeNode) {
			fail("두번째 블락 대상 글(제목:루트1_자식2) 찾기 실패");
		}

		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(deleteBoardTreeNode.getWriterID());
		boardDeleteReq.setBoardID(deleteBoardTreeNode.getBoardID());
		boardDeleteReq.setBoardNo(deleteBoardTreeNode.getBoardNo());

		try {
			MessageResultRes messageResultRes = boardDeleteReqServerTask
					.doWork(TEST_DBCP_NAME, boardDeleteReq);
			if (!messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardBlockReqServerTask boardBlockReqServerTask = null;
		try {
			boardBlockReqServerTask = new BoardBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardBlockReq firstBlcokBoardBlockReq = new BoardBlockReq();
		firstBlcokBoardBlockReq.setRequestedUserID("admin");
		firstBlcokBoardBlockReq
				.setBoardID(firstBlockBoardTreeNode.getBoardID());
		firstBlcokBoardBlockReq
				.setBoardNo(firstBlockBoardTreeNode.getBoardNo());

		try {
			MessageResultRes messageResultRes = boardBlockReqServerTask.doWork(
					TEST_DBCP_NAME, firstBlcokBoardBlockReq);

			// log.info(messageResultRes.toString());

			if (!messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}

		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}

		int pageNo = 1;
		int pageSize = boardTree.getHashSize();

		BoardListReq boardListReq = new BoardListReq();
		boardListReq.setRequestedUserID("admin");
		boardListReq.setBoardID(boardType.getBoardID());
		boardListReq.setPageNo(pageNo);
		boardListReq.setPageSize(pageSize);

		BoardListReqServerTask boardListReqServerTask = null;
		try {
			boardListReqServerTask = new BoardListReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardListRes boardListRes = null;

		try {
			boardListRes = boardListReqServerTask.doWork(TEST_DBCP_NAME,
					boardListReq);

		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		for (BoardListRes.Board board : boardListRes.getBoardList()) {
			String boardState = board.getBoardSate();
			int groupSeq = board.getGroupSeq();

			if (firstBlockBoardTreeNode.getGroupSeq() == groupSeq) {
				assertEquals("첫번째 블락 대상 게시글 상태 점검",
						BoardStateType.BLOCK.getValue(), boardState);
			} else if (deleteBoardTreeNode.getGroupSeq() == groupSeq) {
				assertEquals("삭제 대상 게시글 상태 점검",
						BoardStateType.DELETE.getValue(), boardState);
			} else if ((firstBlockBoardTreeNode.getGroupSeq() - 1) <= groupSeq
					&& groupSeq < firstBlockBoardTreeNode.getGroupSeq()) {
				assertEquals(
						"첫번째 블락 트리에서 블락과 삭제 상태의 노드를 제외한 나머지 글들의 게시글 상태 점검",
						BoardStateType.TREEBLOCK.getValue(), boardState);
			} else {
				assertEquals("첫번째 블락 트리 제외한 글들의 게시글 상태 점검",
						BoardStateType.OK.getValue(), boardState);
			}
		}

		BoardBlockReq secondBlcokBoardBlockReq = new BoardBlockReq();
		secondBlcokBoardBlockReq.setRequestedUserID("admin");
		secondBlcokBoardBlockReq.setBoardID(secondBlockBoardTreeNode
				.getBoardID());
		secondBlcokBoardBlockReq.setBoardNo(secondBlockBoardTreeNode
				.getBoardNo());

		try {
			MessageResultRes messageResultRes = boardBlockReqServerTask.doWork(
					TEST_DBCP_NAME, secondBlcokBoardBlockReq);

			log.info(messageResultRes.toString());

			if (!messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}

		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}

		try {
			boardListRes = boardListReqServerTask.doWork(TEST_DBCP_NAME,
					boardListReq);

		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		for (BoardListRes.Board board : boardListRes.getBoardList()) {
			String boardState = board.getBoardSate();
			int groupSeq = board.getGroupSeq();

			if (firstBlockBoardTreeNode.getGroupSeq() == groupSeq) {
				assertEquals("첫번째 블락 대상 게시글 상태 점검",
						BoardStateType.BLOCK.getValue(), boardState);
			} else if (deleteBoardTreeNode.getGroupSeq() == groupSeq) {
				assertEquals("삭제 대상 게시글 상태 점검",
						BoardStateType.DELETE.getValue(), boardState);
			} else if (secondBlockBoardTreeNode.getGroupSeq() == groupSeq) {
				assertEquals("두번째 블락 대상 게시글 상태 점검",
						BoardStateType.BLOCK.getValue(), boardState);
			} else if ((secondBlockBoardTreeNode.getGroupSeq() - 6) <= groupSeq
					&& groupSeq < secondBlockBoardTreeNode.getGroupSeq()) {
				assertEquals(
						"두번째 최종 블락 트리에서 블락과 삭제 상태의 노드를 제외한 나머지 글들의 게시글 상태 점검",
						BoardStateType.TREEBLOCK.getValue(), boardState);
			} else {
				assertEquals("두번째 블락 트리를 제외한 글들의 게시글 상태 점검",
						BoardStateType.OK.getValue(), boardState);
			}
		}
	}

	@Test
	public void 게시글차단_루트정상() {
		final BoardType boardType = BoardType.FREE;

		class VirtualBoardTreeBuilder implements VirtualBoardTreeBuilderIF {
			@Override
			public BoardTree build(final BoardType boardType) {
				String writerID = "test01";
				String otherID = "test02";

				BoardTree boardTree = new BoardTree();

				final short boardID = boardType.getBoardID();
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
		BoardTree boardTree = virtualBoardTreeBuilder.build(boardType);
		boardTree.makeDBRecord(TEST_DBCP_NAME);

		BoardTreeNode blockBoardTreeNode = boardTree.find("루트1_자식1_자식1_자식1");

		if (null == blockBoardTreeNode) {
			fail("목표 게시글(제목:루트1_자식1_자식1_자식1) 찾기 실패");
		}

		BoardTreeNode treeBlock1BoardTreeNode = boardTree
				.find("루트1_자식1_자식1_자식1_자식1");

		if (null == treeBlock1BoardTreeNode) {
			fail("목표 게시글(제목:루트1_자식1_자식1_자식1_자식1) 찾기 실패");
		}

		BoardBlockReq blcokBoardBlockReq = new BoardBlockReq();
		blcokBoardBlockReq.setRequestedUserID("admin");
		blcokBoardBlockReq.setBoardID(blockBoardTreeNode.getBoardID());
		blcokBoardBlockReq.setBoardNo(blockBoardTreeNode.getBoardNo());

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

		{
			BoardDetailReq boardDetailReq = new BoardDetailReq();
			boardDetailReq.setBoardID(blockBoardTreeNode.getBoardID());
			boardDetailReq.setBoardNo(blockBoardTreeNode.getBoardNo());
			boardDetailReq.setRequestedUserID("admin");

			BoardDetailReqServerTask boardDetailReqServerTask = null;
			try {
				boardDetailReqServerTask = new BoardDetailReqServerTask();
			} catch (DynamicClassCallException e1) {
				fail("dead code");
			}

			try {
				BoardDetailRes boardDetailRes = boardDetailReqServerTask
						.doWork(TEST_DBCP_NAME, boardDetailReq);

				log.info(boardDetailRes.toString());

				assertEquals("게시판 상태 비교", BoardStateType.BLOCK.getValue(),
						boardDetailRes.getBoardSate());

			} catch (Exception e) {
				log.warn("fail to execuate doTask", e);
				fail("fail to execuate doTask");
			}
		}

		{
			BoardDetailReq boardDetailReq = new BoardDetailReq();
			boardDetailReq.setBoardID(treeBlock1BoardTreeNode.getBoardID());
			boardDetailReq.setBoardNo(treeBlock1BoardTreeNode.getBoardNo());
			boardDetailReq.setRequestedUserID("admin");

			BoardDetailReqServerTask boardDetailReqServerTask = null;
			try {
				boardDetailReqServerTask = new BoardDetailReqServerTask();
			} catch (DynamicClassCallException e1) {
				fail("dead code");
			}
			try {
				BoardDetailRes boardDetailRes = boardDetailReqServerTask
						.doWork(TEST_DBCP_NAME, boardDetailReq);

				log.info(boardDetailRes.toString());

				assertEquals("게시판 상태 비교", BoardStateType.TREEBLOCK.getValue(),
						boardDetailRes.getBoardSate());

			} catch (Exception e) {
				log.warn("fail to execuate doTask", e);
				fail("fail to execuate doTask");
			}
		}

		int pageNo = 1;
		int pageSize = boardTree.getHashSize();

		BoardListReq boardListReq = new BoardListReq();
		boardListReq.setRequestedUserID("guest");
		boardListReq.setBoardID(boardType.getBoardID());
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

		assertEquals("게시판 트리 노드 총 갯수와 게시판 레코드 총 갯수 비교",
				boardTree.getHashSize() - 2, firstBoardListRes.getTotal());
	}

	@Test
	public void 게시판해제_일반유저() {
		String writerID = "test01";

		BoardUnBlockReqServerTask boardUnBlockReqServerTask = null;
		try {
			boardUnBlockReqServerTask = new BoardUnBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardUnBlockReq boardUnBlockReq = new BoardUnBlockReq();
		boardUnBlockReq.setRequestedUserID(writerID);
		boardUnBlockReq.setBoardID(BoardType.FREE.getBoardID());
		boardUnBlockReq.setBoardNo(1);

		try {
			boardUnBlockReqServerTask.doWork(TEST_DBCP_NAME, boardUnBlockReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "게시글 해제는 관리자 전용 서비스입니다";

			assertEquals("게시글 해제 기능 호출자 관리자 여부 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판해제_대상글없음() {
		String adminID = "admin";

		BoardUnBlockReqServerTask boardUnBlockReqServerTask = null;
		try {
			boardUnBlockReqServerTask = new BoardUnBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardUnBlockReq boardUnBlockReq = new BoardUnBlockReq();
		boardUnBlockReq.setRequestedUserID(adminID);
		boardUnBlockReq.setBoardID(BoardType.FREE.getBoardID());
		boardUnBlockReq.setBoardNo(1);

		try {
			boardUnBlockReqServerTask.doWork(TEST_DBCP_NAME, boardUnBlockReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "1.해당 게시글이 존재 하지 않습니다";

			assertEquals("해제 대상 글이 없을때  경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판해제_블락이아닌글_정상삭제혹은트리블락() {
		String writerID = "test01";
		String adminID = "admin";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(writerID);
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("제목1");
		boardWriteReq.setContent("내용1");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();

		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);

		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardUnBlockReqServerTask boardUnBlockReqServerTask = null;
		try {
			boardUnBlockReqServerTask = new BoardUnBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardUnBlockReq boardUnBlockReq = new BoardUnBlockReq();
		boardUnBlockReq.setRequestedUserID(adminID);
		boardUnBlockReq.setBoardID(boardWriteRes.getBoardID());
		boardUnBlockReq.setBoardNo(boardWriteRes.getBoardNo());

		try {
			boardUnBlockReqServerTask.doWork(TEST_DBCP_NAME, boardUnBlockReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = new StringBuilder().append("차단된 글[")
					.append(BoardStateType.OK.getName()).append("]이 아닙니다")
					.toString();

			assertEquals("해제 대상 글이 없을때  경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판해제_블락포함된블락() {
		final BoardType boardType = BoardType.FREE;

		class VirtualBoardTreeBuilder implements VirtualBoardTreeBuilderIF {
			@Override
			public BoardTree build(final BoardType boardType) {
				String writerID = "test01";
				String otherID = "test02";

				BoardTree boardTree = new BoardTree();

				final short boardID = boardType.getBoardID();
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
		BoardTree boardTree = virtualBoardTreeBuilder.build(boardType);
		boardTree.makeDBRecord(TEST_DBCP_NAME);

		BoardTreeNode firstBlockBoardTreeNode = boardTree
				.find("루트1_자식2_자식1_자식2");

		if (null == firstBlockBoardTreeNode) {
			fail("첫번째 블락 대상 글(제목:루트1_자식2_자식1_자식2) 찾기 실패");
		}

		BoardTreeNode secondBlockBoardTreeNode = boardTree.find("루트1_자식2");

		if (null == secondBlockBoardTreeNode) {
			fail("두번째 블락 대상 글(제목:루트1_자식2) 찾기 실패");
		}

		BoardTreeNode deleteBoardTreeNode = boardTree
				.find("루트1_자식2_자식1_자식2_자식1");

		if (null == deleteBoardTreeNode) {
			fail("삭제 대상 글(제목:루트1_자식2_자식1_자식2_자식1) 찾기 실패");
		}

		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(deleteBoardTreeNode.getWriterID());
		boardDeleteReq.setBoardID(deleteBoardTreeNode.getBoardID());
		boardDeleteReq.setBoardNo(deleteBoardTreeNode.getBoardNo());

		try {
			MessageResultRes messageResultRes = boardDeleteReqServerTask
					.doWork(TEST_DBCP_NAME, boardDeleteReq);
			if (!messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardBlockReqServerTask boardBlockReqServerTask = null;
		try {
			boardBlockReqServerTask = new BoardBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardBlockReq firstBlcokBoardBlockReq = new BoardBlockReq();
		firstBlcokBoardBlockReq.setRequestedUserID("admin");
		firstBlcokBoardBlockReq
				.setBoardID(firstBlockBoardTreeNode.getBoardID());
		firstBlcokBoardBlockReq
				.setBoardNo(firstBlockBoardTreeNode.getBoardNo());

		try {
			MessageResultRes messageResultRes = boardBlockReqServerTask.doWork(
					TEST_DBCP_NAME, firstBlcokBoardBlockReq);

			// log.info(messageResultRes.toString());

			if (!messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}

		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}

		BoardBlockReq secondBlcokBoardBlockReq = new BoardBlockReq();
		secondBlcokBoardBlockReq.setRequestedUserID("admin");
		secondBlcokBoardBlockReq.setBoardID(secondBlockBoardTreeNode
				.getBoardID());
		secondBlcokBoardBlockReq.setBoardNo(secondBlockBoardTreeNode
				.getBoardNo());

		try {
			MessageResultRes messageResultRes = boardBlockReqServerTask.doWork(
					TEST_DBCP_NAME, secondBlcokBoardBlockReq);

			log.info(messageResultRes.toString());

			if (!messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}

		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}

		BoardUnBlockReqServerTask boardUnBlockReqServerTask = null;
		try {
			boardUnBlockReqServerTask = new BoardUnBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardUnBlockReq boardUnBlockReq = new BoardUnBlockReq();
		boardUnBlockReq.setRequestedUserID("admin");
		boardUnBlockReq.setBoardID(secondBlockBoardTreeNode.getBoardID());
		boardUnBlockReq.setBoardNo(secondBlockBoardTreeNode.getBoardNo());

		try {
			boardUnBlockReqServerTask.doWork(TEST_DBCP_NAME, boardUnBlockReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		int pageNo = 1;
		int pageSize = boardTree.getHashSize();

		BoardListReq boardListReq = new BoardListReq();
		boardListReq.setRequestedUserID("admin");
		boardListReq.setBoardID(boardType.getBoardID());
		boardListReq.setPageNo(pageNo);
		boardListReq.setPageSize(pageSize);

		BoardListReqServerTask boardListReqServerTask = null;
		try {
			boardListReqServerTask = new BoardListReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardListRes boardListRes = null;

		try {
			boardListRes = boardListReqServerTask.doWork(TEST_DBCP_NAME,
					boardListReq);

		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		for (BoardListRes.Board board : boardListRes.getBoardList()) {
			String boardState = board.getBoardSate();
			int groupSeq = board.getGroupSeq();

			if (firstBlockBoardTreeNode.getGroupSeq() == groupSeq) {
				assertEquals("첫번째 블락 대상 게시글 상태 점검",
						BoardStateType.BLOCK.getValue(), boardState);
			} else if (deleteBoardTreeNode.getGroupSeq() == groupSeq) {
				assertEquals("삭제 대상 게시글 상태 점검",
						BoardStateType.DELETE.getValue(), boardState);
			} else if ((firstBlockBoardTreeNode.getGroupSeq() - 1) <= groupSeq
					&& groupSeq < firstBlockBoardTreeNode.getGroupSeq()) {
				assertEquals(
						"첫번째 블락 트리에서 블락과 삭제 상태의 노드를 제외한 나머지 글들의 게시글 상태 점검",
						BoardStateType.TREEBLOCK.getValue(), boardState);
			} else {
				assertEquals("첫번째 블락 트리 제외한 글들의 게시글 상태 점검",
						BoardStateType.OK.getValue(), boardState);
			}
		}

		boardUnBlockReq = new BoardUnBlockReq();
		boardUnBlockReq.setRequestedUserID("admin");
		boardUnBlockReq.setBoardID(firstBlockBoardTreeNode.getBoardID());
		boardUnBlockReq.setBoardNo(firstBlockBoardTreeNode.getBoardNo());

		try {
			boardUnBlockReqServerTask.doWork(TEST_DBCP_NAME, boardUnBlockReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		try {
			boardListRes = boardListReqServerTask.doWork(TEST_DBCP_NAME,
					boardListReq);

		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		for (BoardListRes.Board board : boardListRes.getBoardList()) {
			String boardState = board.getBoardSate();
			int groupSeq = board.getGroupSeq();

			if (deleteBoardTreeNode.getGroupSeq() == groupSeq) {
				assertEquals("삭제 대상 게시글 상태 점검",
						BoardStateType.DELETE.getValue(), boardState);
			} else {
				assertEquals("게시글 상태 점검", BoardStateType.OK.getValue(),
						boardState);
			}
		}
	}

	@Test
	public void 게시판해제_정상() {
		final BoardType boardType = BoardType.FREE;

		class VirtualBoardTreeBuilder implements VirtualBoardTreeBuilderIF {
			@Override
			public BoardTree build(final BoardType boardType) {
				String writerID = "test01";
				String otherID = "test02";

				BoardTree boardTree = new BoardTree();

				final short boardID = boardType.getBoardID();
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
		BoardTree boardTree = virtualBoardTreeBuilder.build(boardType);
		boardTree.makeDBRecord(TEST_DBCP_NAME);

		BoardTreeNode blockBoardTreeNode = boardTree.find("루트1_자식2_자식1_자식3");

		if (null == blockBoardTreeNode) {
			fail("목표 게시글(제목:루트1_자식2_자식1_자식3) 찾기 실패");
		}

		BoardTreeNode treeBlock1BoardTreeNode = boardTree
				.find("루트1_자식2_자식1_자식3_자식1");

		if (null == treeBlock1BoardTreeNode) {
			fail("목표 게시글(제목:루트1_자식2_자식1_자식3_자식1) 찾기 실패");
		}

		BoardBlockReq blcokBoardBlockReq = new BoardBlockReq();
		blcokBoardBlockReq.setRequestedUserID("admin");
		blcokBoardBlockReq.setBoardID(blockBoardTreeNode.getBoardID());
		blcokBoardBlockReq.setBoardNo(blockBoardTreeNode.getBoardNo());

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

		{
			BoardDetailReq boardDetailReq = new BoardDetailReq();
			boardDetailReq.setBoardID(blockBoardTreeNode.getBoardID());
			boardDetailReq.setBoardNo(blockBoardTreeNode.getBoardNo());
			boardDetailReq.setRequestedUserID("admin");

			BoardDetailReqServerTask boardDetailReqServerTask = null;
			try {
				boardDetailReqServerTask = new BoardDetailReqServerTask();
			} catch (DynamicClassCallException e1) {
				fail("dead code");
			}
			try {
				BoardDetailRes boardDetailRes = boardDetailReqServerTask
						.doWork(TEST_DBCP_NAME, boardDetailReq);

				log.info(boardDetailRes.toString());

				assertEquals("게시판 상태 비교", BoardStateType.BLOCK.getValue(),
						boardDetailRes.getBoardSate());

			} catch (Exception e) {
				log.warn("fail to execuate doTask", e);
				fail("fail to execuate doTask");
			}
		}

		{
			BoardDetailReq boardDetailReq = new BoardDetailReq();
			boardDetailReq.setBoardID(treeBlock1BoardTreeNode.getBoardID());
			boardDetailReq.setBoardNo(treeBlock1BoardTreeNode.getBoardNo());
			boardDetailReq.setRequestedUserID("admin");

			BoardDetailReqServerTask boardDetailReqServerTask = null;
			try {
				boardDetailReqServerTask = new BoardDetailReqServerTask();
			} catch (DynamicClassCallException e1) {
				fail("dead code");
			}
			try {
				BoardDetailRes boardDetailRes = boardDetailReqServerTask
						.doWork(TEST_DBCP_NAME, boardDetailReq);

				log.info(boardDetailRes.toString());

				assertEquals("게시판 상태 비교", BoardStateType.TREEBLOCK.getValue(),
						boardDetailRes.getBoardSate());

			} catch (Exception e) {
				log.warn("fail to execuate doTask", e);
				fail("fail to execuate doTask");
			}
		}

		BoardUnBlockReqServerTask boardUnBlockReqServerTask = null;
		try {
			boardUnBlockReqServerTask = new BoardUnBlockReqServerTask();
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}
		BoardUnBlockReq boardUnBlockReq = new BoardUnBlockReq();
		boardUnBlockReq.setRequestedUserID("admin");
		boardUnBlockReq.setBoardID(blockBoardTreeNode.getBoardID());
		boardUnBlockReq.setBoardNo(blockBoardTreeNode.getBoardNo());

		try {
			boardUnBlockReqServerTask.doWork(TEST_DBCP_NAME, boardUnBlockReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		{
			BoardDetailReq boardDetailReq = new BoardDetailReq();
			boardDetailReq.setBoardID(blockBoardTreeNode.getBoardID());
			boardDetailReq.setBoardNo(blockBoardTreeNode.getBoardNo());
			boardDetailReq.setRequestedUserID("test01");

			BoardDetailReqServerTask boardDetailReqServerTask = null;
			try {
				boardDetailReqServerTask = new BoardDetailReqServerTask();
			} catch (DynamicClassCallException e1) {
				fail("dead code");
			}
			try {
				BoardDetailRes boardDetailRes = boardDetailReqServerTask
						.doWork(TEST_DBCP_NAME, boardDetailReq);

				log.info(boardDetailRes.toString());

				assertEquals("게시판 상태 비교", BoardStateType.OK.getValue(),
						boardDetailRes.getBoardSate());

			} catch (Exception e) {
				log.warn("fail to execuate doTask", e);
				fail("fail to execuate doTask");
			}
		}

		{
			BoardDetailReq boardDetailReq = new BoardDetailReq();
			boardDetailReq.setBoardID(treeBlock1BoardTreeNode.getBoardID());
			boardDetailReq.setBoardNo(treeBlock1BoardTreeNode.getBoardNo());
			boardDetailReq.setRequestedUserID("test01");

			BoardDetailReqServerTask boardDetailReqServerTask = null;
			try {
				boardDetailReqServerTask = new BoardDetailReqServerTask();
			} catch (DynamicClassCallException e1) {
				fail("dead code");
			}
			try {
				BoardDetailRes boardDetailRes = boardDetailReqServerTask
						.doWork(TEST_DBCP_NAME, boardDetailReq);

				log.info(boardDetailRes.toString());

				assertEquals("게시판 상태 비교", BoardStateType.OK.getValue(),
						boardDetailRes.getBoardSate());

			} catch (Exception e) {
				log.warn("fail to execuate doTask", e);
				fail("fail to execuate doTask");
			}
		}
	}

	@Test
	public void 게시판최상위글등록및수정테스트() {
		String writerID = "test01";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(writerID);
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("테스트 주제");
		boardWriteReq.setContent("내용::그림2 하나를 그리다");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> newAttachedFileListForWrite = new ArrayList<BoardWriteReq.NewAttachedFile>();
		{
			BoardWriteReq.NewAttachedFile attachedFile = new BoardWriteReq.NewAttachedFile();
			attachedFile.setAttachedFileName("임시첨부파일01.jpg");
			attachedFile.setAttachedFileSize(1024);

			newAttachedFileListForWrite.add(attachedFile);
		}

		boardWriteReq.setNewAttachedFileCnt((short) newAttachedFileListForWrite
				.size());
		boardWriteReq.setNewAttachedFileList(newAttachedFileListForWrite);

		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setBoardID(boardWriteRes.getBoardID());
		boardDetailReq.setBoardNo(boardWriteRes.getBoardNo());
		boardDetailReq.setRequestedUserID("guest");

		BoardDetailReqServerTask boardDetailReqServerTask = null;
		try {
			boardDetailReqServerTask = new BoardDetailReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		short oldNextAttachedFileSeq = 0;
		try {
			BoardDetailRes boardDetailRes = boardDetailReqServerTask.doWork(
					TEST_DBCP_NAME, boardDetailReq);
			oldNextAttachedFileSeq = boardDetailRes.getNextAttachedFileSeq();
			log.info(boardDetailRes.toString());

			assertEquals("제목 비교", boardWriteReq.getSubject(),
					boardDetailRes.getSubject());
			assertEquals("내용 비교", boardWriteReq.getContent(),
					boardDetailRes.getContent());
			assertEquals("작성자 아이디 비교", boardWriteReq.getRequestedUserID(),
					boardDetailRes.getWriterID());
			assertEquals("작성 아이피 주소 비교", boardWriteReq.getIp(),
					boardDetailRes.getWriterIP());
			assertEquals("최종 수정자 아이디 비교", boardWriteReq.getRequestedUserID(),
					boardDetailRes.getLastModifierID());
			assertEquals("최종 수정자 아이피 주소 비교", boardWriteReq.getIp(),
					boardDetailRes.getLastModifierIP());
			assertEquals("첨부 파일 갯수 비교", newAttachedFileListForWrite.size(),
					boardDetailRes.getAttachedFileCnt());
			assertEquals("다음 첨부 파일 시퀀스 비교", newAttachedFileListForWrite.size(),
					boardDetailRes.getNextAttachedFileSeq());

			List<BoardDetailRes.AttachedFile> actualAttachedFileList = boardDetailRes
					.getAttachedFileList();
			for (int i = 0; i < newAttachedFileListForWrite.size(); i++) {
				BoardWriteReq.NewAttachedFile expectedAttachedFile = newAttachedFileListForWrite
						.get(i);
				BoardDetailRes.AttachedFile acutalAttachedFile = actualAttachedFileList
						.get(i);

				assertEquals(i + "번째 첨부 파일명 비교",
						expectedAttachedFile.getAttachedFileName(),
						acutalAttachedFile.getAttachedFileName());

				assertEquals(i + "번째 첨부 파일의 순번 비교", i,
						acutalAttachedFile.getAttachedFileSeq());
			}
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}

		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID(boardWriteReq.getRequestedUserID());
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("수정 테스트를 위한 최상위 본문글#1");
		boardModifyReq.setContent("내용::수정 테스트를 위한 최상위 본문글#1");
		boardModifyReq.setIp("172.16.0.3");
		boardModifyReq.setNextAttachedFileSeq(oldNextAttachedFileSeq);

		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();
		{
			{
				for (int i = 0; i < newAttachedFileListForWrite.size(); i++) {
					BoardModifyReq.OldAttachedFile oldAttachedFile = new BoardModifyReq.OldAttachedFile();
					oldAttachedFile.setAttachedFileSeq((short) i);
					oldAttachedFileList.add(oldAttachedFile);
				}
			}

			boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList
					.size());
			boardModifyReq.setOldAttachedFileList(oldAttachedFileList);
		}

		List<BoardModifyReq.NewAttachedFile> newAttachedFileListForModify = new ArrayList<BoardModifyReq.NewAttachedFile>();
		{

			BoardModifyReq.NewAttachedFile newAttachedFile = new BoardModifyReq.NewAttachedFile();
			newAttachedFile.setAttachedFileName("임시첨부파일02.jpg");
			newAttachedFile.setAttachedFileSize(1024);

			newAttachedFileListForModify.add(newAttachedFile);

			boardModifyReq.setNewAttachedFileCnt(newAttachedFileListForModify
					.size());
			boardModifyReq.setNewAttachedFileList(newAttachedFileListForModify);
		}

		BoardModifyReqServerTask boardModifyReqServerTask = null;
		try {
			boardModifyReqServerTask = new BoardModifyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			BoardModifyRes boardModifyRes = boardModifyReqServerTask.doWork(
					TEST_DBCP_NAME, boardModifyReq);
			log.info(boardModifyRes.toString());
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		try {
			BoardDetailRes boardDetailRes = boardDetailReqServerTask.doWork(
					TEST_DBCP_NAME, boardDetailReq);

			log.info(boardDetailRes.toString());

			assertEquals("제목 비교", boardModifyReq.getSubject(),
					boardModifyReq.getSubject());
			assertEquals("내용 비교", boardModifyReq.getContent(),
					boardModifyReq.getContent());
			assertEquals("작성자 아이디 비교", boardWriteReq.getRequestedUserID(),
					boardDetailRes.getWriterID());
			assertEquals("작성 아이피 주소 비교", boardWriteReq.getIp(),
					boardDetailRes.getWriterIP());
			assertEquals("최종 수정자 아이디 비교", boardModifyReq.getRequestedUserID(),
					boardDetailRes.getLastModifierID());
			assertEquals("최종 수정자 아이피 주소 비교", boardModifyReq.getIp(),
					boardDetailRes.getLastModifierIP());

			assertEquals("첨부 파일 갯수 비교", newAttachedFileListForWrite.size()
					+ newAttachedFileListForModify.size(),
					boardDetailRes.getAttachedFileCnt());
			assertEquals("다음 첨부 파일 시퀀스 비교", oldNextAttachedFileSeq
					+ newAttachedFileListForModify.size(),
					boardDetailRes.getNextAttachedFileSeq());

			List<BoardDetailRes.AttachedFile> actualAttachedFileList = boardDetailRes
					.getAttachedFileList();
			for (int i = 0; i < newAttachedFileListForWrite.size(); i++) {
				BoardWriteReq.NewAttachedFile expectedAttachedFile = newAttachedFileListForWrite
						.get(i);
				BoardDetailRes.AttachedFile acutalAttachedFile = actualAttachedFileList
						.get(i);

				assertEquals(i + "번째 첨부 파일명 비교",
						expectedAttachedFile.getAttachedFileName(),
						acutalAttachedFile.getAttachedFileName());

				assertEquals(i + "번째 첨부 파일의 순번 비교", i,
						acutalAttachedFile.getAttachedFileSeq());
			}

			for (int i = 0; i < newAttachedFileListForModify.size(); i++) {
				BoardModifyReq.NewAttachedFile expectedAttachedFile = newAttachedFileListForModify
						.get(i);
				BoardDetailRes.AttachedFile acutalAttachedFile = actualAttachedFileList
						.get(i + newAttachedFileListForWrite.size());

				assertEquals((i + newAttachedFileListForWrite.size())
						+ "번째 첨부 파일명 비교",
						expectedAttachedFile.getAttachedFileName(),
						acutalAttachedFile.getAttachedFileName());

				assertEquals((i + newAttachedFileListForWrite.size())
						+ "번째 첨부 파일의 순번 비교",
						i + newAttachedFileListForWrite.size(),
						acutalAttachedFile.getAttachedFileSeq());
			}
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 댓글등록_부모없음() {
		String otherID = "test02";

		BoardReplyReq boardReplyReq = new BoardReplyReq();
		boardReplyReq.setBoardID(BoardType.FREE.getBoardID());
		boardReplyReq.setParentBoardNo(1);
		boardReplyReq.setSubject("테스트 주제01-1");
		boardReplyReq.setContent("내용::그림01-1하나를 그리다");
		boardReplyReq.setRequestedUserID(otherID);
		boardReplyReq.setIp("127.0.0.1");

		{
			List<BoardReplyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardReplyReq.NewAttachedFile>();

			{
				BoardReplyReq.NewAttachedFile newAttachedFile = new BoardReplyReq.NewAttachedFile();
				newAttachedFile.setAttachedFileName("임시첨부파일01-1.jpg");
				newAttachedFile.setAttachedFileSize(1024);

				newAttachedFileList.add(newAttachedFile);
			}

			{
				BoardReplyReq.NewAttachedFile newAttachedFile = new BoardReplyReq.NewAttachedFile();
				newAttachedFile.setAttachedFileName("임시첨부파일01-2.jpg");
				newAttachedFile.setAttachedFileSize(1025);

				newAttachedFileList.add(newAttachedFile);
			}

			boardReplyReq.setNewAttachedFileCnt((short) newAttachedFileList
					.size());
			boardReplyReq.setNewAttachedFileList(newAttachedFileList);
		}

		BoardReplyReqServerTask boardReplyReqServerTask = null;
		try {
			boardReplyReqServerTask = new BoardReplyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			boardReplyReqServerTask.doWork(TEST_DBCP_NAME, boardReplyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = new StringBuilder().append("부모글[")
					.append(boardReplyReq.getParentBoardNo())
					.append("] 이 존재하지 않습니다").toString();

			assertEquals("이미 차단된 글 다시 차단할때 경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 댓글등록_정상_최초() {
		String writerID = "test01";
		String otherID = "test02";

		BoardWriteRes boardWriteRes = null;
		{

			BoardWriteReqServerTask boardWriteReqServerTask = null;
			try {
				boardWriteReqServerTask = new BoardWriteReqServerTask();
			} catch (DynamicClassCallException e1) {
				fail("dead code");
			}
			BoardWriteReq boardWriteReq = new BoardWriteReq();
			boardWriteReq.setRequestedUserID(writerID);
			boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
			boardWriteReq.setSubject("테스트 주제01");
			boardWriteReq.setContent("내용::그림01 하나를 그리다");
			boardWriteReq.setIp("172.16.0.1");

			List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();
			{
				BoardWriteReq.NewAttachedFile attachedFile = new BoardWriteReq.NewAttachedFile();
				attachedFile.setAttachedFileName("임시첨부파일01.jpg");
				attachedFile.setAttachedFileSize(1024);

				attachedFileList.add(attachedFile);
			}

			boardWriteReq
					.setNewAttachedFileCnt((short) attachedFileList.size());
			boardWriteReq.setNewAttachedFileList(attachedFileList);

			try {
				boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
						boardWriteReq);
			} catch (ServerServiceException e) {
				log.warn(e.getMessage(), e);
				fail("fail to execuate doTask");
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to execuate doTask");
			}
		}

		UShort parentOrderSeq = UShort.valueOf(0);

		BoardReplyReq boardReplyReq = new BoardReplyReq();
		boardReplyReq.setBoardID(boardWriteRes.getBoardID());
		boardReplyReq.setParentBoardNo(boardWriteRes.getBoardNo());
		boardReplyReq.setSubject("테스트 주제01-1");
		boardReplyReq.setContent("내용::그림01-1하나를 그리다");
		boardReplyReq.setRequestedUserID(otherID);
		boardReplyReq.setIp("127.0.0.1");

		{
			List<BoardReplyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardReplyReq.NewAttachedFile>();

			{
				BoardReplyReq.NewAttachedFile newAttachedFile = new BoardReplyReq.NewAttachedFile();
				newAttachedFile.setAttachedFileName("임시첨부파일03_1.jpg");
				newAttachedFile.setAttachedFileSize(1024);

				newAttachedFileList.add(newAttachedFile);
			}

			{
				BoardReplyReq.NewAttachedFile newAttachedFile = new BoardReplyReq.NewAttachedFile();
				newAttachedFile.setAttachedFileName("임시첨부파일03_2.jpg");
				newAttachedFile.setAttachedFileSize(1025);

				newAttachedFileList.add(newAttachedFile);
			}

			boardReplyReq.setNewAttachedFileCnt((short) newAttachedFileList
					.size());
			boardReplyReq.setNewAttachedFileList(newAttachedFileList);
		}

		BoardReplyReqServerTask boardReplyReqServerTask = null;
		try {
			boardReplyReqServerTask = new BoardReplyReqServerTask();
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}

		BoardReplyRes boardReplyRes = null;
		try {
			boardReplyRes = boardReplyReqServerTask.doWork(TEST_DBCP_NAME,
					boardReplyReq);
			log.info(boardReplyRes.toString());
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setBoardID(boardReplyRes.getBoardID());
		boardDetailReq.setBoardNo(boardReplyRes.getBoardNo());
		boardDetailReq.setRequestedUserID("guest");

		BoardDetailReqServerTask boardDetailReqServerTask = null;
		try {
			boardDetailReqServerTask = new BoardDetailReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		try {
			BoardDetailRes boardDetailRes = boardDetailReqServerTask.doWork(
					TEST_DBCP_NAME, boardDetailReq);

			assertEquals("댓글 제목 비교", boardReplyReq.getSubject(),
					boardDetailRes.getSubject());
			assertEquals("댓글 내용 비교", boardReplyReq.getContent(),
					boardDetailRes.getContent());
			assertEquals("댓글 작성자 아이디 비교", boardReplyReq.getRequestedUserID(),
					boardDetailRes.getWriterID());
			assertEquals("댓글 작성자 아이피 주소 비교", boardReplyReq.getIp(),
					boardDetailRes.getWriterIP());
			assertEquals("댓글 첨부 파일 갯수 비교",
					boardReplyReq.getNewAttachedFileCnt(),
					boardDetailRes.getAttachedFileCnt());
			assertEquals("댓글 추천수 비교", 0, boardDetailRes.getVotes());
			assertEquals("댓글 조회갯수 비교", 1, boardDetailRes.getViewCount());

			assertEquals("댓글 그룹 최상위 번호 비교", boardWriteRes.getBoardNo(),
					boardDetailRes.getGroupNo());
			assertEquals("댓글 그룹 시퀀스번호 비교", parentOrderSeq.intValue(),
					boardDetailRes.getGroupSeq());
			assertEquals("댓글 그룹 부모 번호 비교", boardWriteRes.getBoardNo(),
					boardDetailRes.getParentNo());
			assertEquals("댓글 깊이 비교", 1, boardDetailRes.getDepth());

			assertEquals("다음 첨부 파일 시퀀스 비교",
					boardReplyReq.getNewAttachedFileCnt(),
					boardDetailRes.getNextAttachedFileSeq());

			// log.info(boardDetailRes.toString());
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}

	}

	@Test
	public void 댓글등록_정상_중간() {
		final BoardType boardType = BoardType.FREE;

		class VirtualBoardTreeBuilder implements VirtualBoardTreeBuilderIF {
			@Override
			public BoardTree build(final BoardType boardType) {
				String writerID = "test01";
				String otherID = "test02";

				BoardTree boardTree = new BoardTree();

				final short boardID = boardType.getBoardID();
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
		BoardTree boardTree = virtualBoardTreeBuilder.build(boardType);
		boardTree.makeDBRecord(TEST_DBCP_NAME);

		BoardTreeNode parentBoardTreeNode = boardTree.find("루트1_자식1_자식1");

		if (null == parentBoardTreeNode) {
			fail("부모로 목표한 게시글(제목:루트1_자식1_자식1) 찾기 실패");
		}

		BoardTreeNode toGroupSeqBoardTreeNode = boardTree
				.find("루트1_자식1_자식1_자식1_자식1");

		if (null == toGroupSeqBoardTreeNode) {
			fail("부모글을 루트로 하는 트리의 그룹 시퀀스에서 가장 최소 값을 갖는 글 찾기 실패");
		}

		BoardReplyReq boardReplyReq = new BoardReplyReq();
		boardReplyReq.setBoardID(parentBoardTreeNode.getBoardID());
		boardReplyReq.setParentBoardNo(parentBoardTreeNode.getBoardNo());
		boardReplyReq.setSubject("제목::루트1_자식1_자식1_자식2");
		boardReplyReq.setContent("내용::루트1_자식1_자식1_자식2");
		boardReplyReq.setRequestedUserID("test01");
		boardReplyReq.setIp("127.0.0.1");

		{
			List<BoardReplyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardReplyReq.NewAttachedFile>();

			boardReplyReq.setNewAttachedFileCnt((short) newAttachedFileList
					.size());
			boardReplyReq.setNewAttachedFileList(newAttachedFileList);
		}

		BoardReplyReqServerTask boardReplyReqServerTask = null;
		try {
			boardReplyReqServerTask = new BoardReplyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardReplyRes boardReplyRes = null;
		try {
			boardReplyRes = boardReplyReqServerTask.doWork(TEST_DBCP_NAME,
					boardReplyReq);
			log.info(boardReplyRes.toString());
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		int pageNo = 1;
		int pageSize = boardTree.getHashSize() + 1;

		BoardListReq boardListReq = new BoardListReq();
		boardListReq.setRequestedUserID("guest");
		boardListReq.setBoardID(boardType.getBoardID());
		boardListReq.setPageNo(pageNo);
		boardListReq.setPageSize(pageSize);

		BoardListReqServerTask boardListReqServerTask = null;
		try {
			boardListReqServerTask = new BoardListReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardListRes afterBoardListRes = null;

		try {
			afterBoardListRes = boardListReqServerTask.doWork(TEST_DBCP_NAME,
					boardListReq);

		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		log.info(afterBoardListRes.toString());

		boolean isFromGroupSeqForUpdate = false, isToGroupSeqForUpdate = false, isParent = false, isNewReply = false, isFromWithoutUpdate = false;

		for (BoardListRes.Board board : afterBoardListRes.getBoardList()) {

			String subject = board.getSubject();
			// int groupSeq = board.getGroupSeq();

			if (subject.equals("루트1")) {
				isFromGroupSeqForUpdate = true;

				BoardTreeNode targetBoardTreeNode = boardTree.find(subject);

				if (null == targetBoardTreeNode) {
					fail("목표 게시글(제목:루트1) 찾기 실패");
				}

				assertEquals("게시판 그룹 순위 비교",
						targetBoardTreeNode.getGroupSeq() + 1,
						board.getGroupSeq());
			} else if (subject.equals(toGroupSeqBoardTreeNode.getSubject())) {
				isToGroupSeqForUpdate = true;
				assertEquals("게시판 그룹 순위 비교",
						toGroupSeqBoardTreeNode.getGroupSeq() + 1,
						board.getGroupSeq());
			} else if (subject.equals(parentBoardTreeNode.getSubject())) {
				isParent = true;

				BoardTreeNode targetBoardTreeNode = boardTree.find(subject);

				if (null == targetBoardTreeNode) {
					fail("목표 부모 게시글 찾기 실패");
				}

				assertEquals("게시판 그룹 순위 비교",
						targetBoardTreeNode.getGroupSeq() + 1,
						board.getGroupSeq());
			} else if (subject.equals(boardReplyReq.getSubject())) {
				/** 신규 추가된 댓글 */
				isNewReply = true;

				assertEquals("댓글 그룹 최상위 번호 비교",
						parentBoardTreeNode.getGroupNo(), board.getGroupNo());
				assertEquals("게시판 그룹 순위 비교",
						toGroupSeqBoardTreeNode.getGroupSeq(),
						board.getGroupSeq());
				assertEquals("댓글 그룹 부모 번호 비교",
						parentBoardTreeNode.getBoardNo(), board.getParentNo());
				assertEquals("댓글 깊이 비교", parentBoardTreeNode.getDepth() + 1,
						board.getDepth());

			} else if (subject.equals("루트1_자식2")) {
				/** 그룹 순위 변경에 영향 없는 글들중 최대 그룹 순서를 갖는 글 */
				isFromWithoutUpdate = true;

				BoardTreeNode targetBoardTreeNode = boardTree.find(subject);

				if (null == targetBoardTreeNode) {
					fail("목표 게시글(제목:루트1_자식2) 찾기 실패");
				}

				assertEquals("게시판 그룹 순위 비교", targetBoardTreeNode.getGroupSeq(),
						board.getGroupSeq());
			}
		}

		if (!isFromGroupSeqForUpdate) {
			fail("신규 댓글 추가시 영향 받는 그룹 시퀀스의 최대값을 갖는 글인 루트 미 존재");
		}

		if (!isToGroupSeqForUpdate) {
			fail("신규 댓글 추가시 영향 받는 그룹 시퀀스의 최소값을 갖는 글이자 댓글의 부모를 루트로한 트리에서 그룹 시퀀스가 가장 작은 글 미 존재");
		}

		if (!isParent) {
			fail("댓글의 부모글 미 존재");
		}

		if (!isNewReply) {
			fail("신규 추가된 댓글 미 존재");
		}

		if (!isFromWithoutUpdate) {
			fail("그룹 순위 변경에 영향 없는 글들중 최대 그룹 순서를 갖는 글 미 존재");
		}
	}

	@Test
	public void 게시판수정_첨부파일최대등록갯수초과() {
		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setBoardID(BoardType.FREE.getBoardID());
		boardModifyReq.setBoardNo(1);
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContent("내용::그림5-1하나를 그리다#1");
		boardModifyReq.setRequestedUserID("test01");
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setNextAttachedFileSeq((short) 1);

		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();
		{
			{
				for (int i = 0; i < 1; i++) {
					BoardModifyReq.OldAttachedFile oldAttachedFile = new BoardModifyReq.OldAttachedFile();
					oldAttachedFile.setAttachedFileSeq((short) i);
					oldAttachedFileList.add(oldAttachedFile);
				}
			}

			boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList
					.size());
			boardModifyReq.setOldAttachedFileList(oldAttachedFileList);
		}

		List<BoardModifyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardModifyReq.NewAttachedFile>();
		{
			for (int i = 0; i < ServerCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT; i++) {
				BoardModifyReq.NewAttachedFile newAttachedFile = new BoardModifyReq.NewAttachedFile();
				newAttachedFile.setAttachedFileName("newAttachedFile" + i);
				newAttachedFile.setAttachedFileSize(1024 + i);
				newAttachedFileList.add(newAttachedFile);
			}
			boardModifyReq.setNewAttachedFileCnt(newAttachedFileList.size());
			boardModifyReq.setNewAttachedFileList(newAttachedFileList);
		}

		BoardModifyReqServerTask boardModifyReqServerTask = null;
		try {
			boardModifyReqServerTask = new BoardModifyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			boardModifyReqServerTask.doWork(TEST_DBCP_NAME, boardModifyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder()
					.append("총 첨부 파일 갯수(=신규 첨부 파일 등록 갯수[")
					.append(boardModifyReq.getNewAttachedFileCnt())
					.append("] + 기존 첨부 파일들중 남은 갯수[")
					.append(boardModifyReq.getOldAttachedFileCnt())
					.append("]) 가 첨부 파일 최대 갯수[")
					.append(ServerCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT)
					.append("]를 초과하였습니다").toString();

			assertEquals("첨부 파일 최대 등록 갯수 초과 검사", expectedErrorMessage,
					acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판수정_잘못된신규첨부파일이름() {
		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setBoardID(BoardType.FREE.getBoardID());
		boardModifyReq.setBoardNo(1);
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContent("내용::그림5-1하나를 그리다#1");
		boardModifyReq.setRequestedUserID("test01");
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setNextAttachedFileSeq((short) 1);

		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();
		{
			boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList
					.size());
			boardModifyReq.setOldAttachedFileList(oldAttachedFileList);
		}

		List<BoardModifyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardModifyReq.NewAttachedFile>();
		{
			for (int i = 0; i < ServerCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT; i++) {
				BoardModifyReq.NewAttachedFile newAttachedFile = new BoardModifyReq.NewAttachedFile();
				newAttachedFile.setAttachedFileName("\\한글" + i);
				newAttachedFile.setAttachedFileSize(1024 + i);
				newAttachedFileList.add(newAttachedFile);
			}
			boardModifyReq.setNewAttachedFileCnt(newAttachedFileList.size());
			boardModifyReq.setNewAttachedFileList(newAttachedFileList);
		}

		BoardModifyReqServerTask boardModifyReqServerTask = null;
		try {
			boardModifyReqServerTask = new BoardModifyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			boardModifyReqServerTask.doWork(TEST_DBCP_NAME, boardModifyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder().append(0)
					.append("번째 파일 이름 유효성 검사 에러 메시지::").append("첨부 파일명[")
					.append("\\한글0").append("]에 금지된 문자[").append("\\")
					.append("]가 존재합니다").toString();

			assertEquals("첨부 파일 최대 등록 갯수 초과 검사", expectedErrorMessage,
					acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판수정_중복된보존을원하는구파일시퀀스() {
		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setBoardID(BoardType.FREE.getBoardID());
		boardModifyReq.setBoardNo(1);
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContent("내용::그림5-1하나를 그리다#1");
		boardModifyReq.setRequestedUserID("test01");
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setNextAttachedFileSeq((short) 5);

		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();
		{
			for (int i = 0; i < ServerCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT; i++) {
				BoardModifyReq.OldAttachedFile oldAttachedFile = new BoardModifyReq.OldAttachedFile();
				oldAttachedFile.setAttachedFileSeq((short) 1);
				oldAttachedFileList.add(oldAttachedFile);
			}
			boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList
					.size());
			boardModifyReq.setOldAttachedFileList(oldAttachedFileList);
		}

		List<BoardModifyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardModifyReq.NewAttachedFile>();
		{

			boardModifyReq.setNewAttachedFileCnt(newAttachedFileList.size());
			boardModifyReq.setNewAttachedFileList(newAttachedFileList);
		}

		BoardModifyReqServerTask boardModifyReqServerTask = null;
		try {
			boardModifyReqServerTask = new BoardModifyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			boardModifyReqServerTask.doWork(TEST_DBCP_NAME, boardModifyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder()
					.append("보존을 원하는 구 첨부 파일 목록에서 증복된 첨부 파일 시퀀스[").append(1)
					.append("]가 존재합니다").toString();

			assertEquals("첨부 파일 최대 등록 갯수 초과 검사", expectedErrorMessage,
					acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판수정_보존을원하는구파일시퀀스가다음시퀀스번호보다큰경우() {
		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setBoardID(BoardType.FREE.getBoardID());
		boardModifyReq.setBoardNo(1);
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContent("내용::그림5-1하나를 그리다#1");
		boardModifyReq.setRequestedUserID("test01");
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setNextAttachedFileSeq((short) 1);

		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();
		{
			for (int i = 0; i < ServerCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT; i++) {
				BoardModifyReq.OldAttachedFile oldAttachedFile = new BoardModifyReq.OldAttachedFile();
				oldAttachedFile.setAttachedFileSeq((short) i);
				oldAttachedFileList.add(oldAttachedFile);
			}
			boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList
					.size());
			boardModifyReq.setOldAttachedFileList(oldAttachedFileList);
		}

		List<BoardModifyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardModifyReq.NewAttachedFile>();
		{

			boardModifyReq.setNewAttachedFileCnt(newAttachedFileList.size());
			boardModifyReq.setNewAttachedFileList(newAttachedFileList);
		}

		BoardModifyReqServerTask boardModifyReqServerTask = null;
		try {
			boardModifyReqServerTask = new BoardModifyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			boardModifyReqServerTask.doWork(TEST_DBCP_NAME, boardModifyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder()
					.append("보존을 원하는 구 첨부 파일 시퀀스[").append(1)
					.append("]가 다음 첨부 파일 시퀀스[")
					.append(boardModifyReq.getNextAttachedFileSeq())
					.append("]보다 크거나 같습니다").toString();

			assertEquals("첨부 파일 최대 등록 갯수 초과 검사", expectedErrorMessage,
					acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판수정_타인글() {
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID("test01");
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContent("차단::내용");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();

		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);

		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID("test02");
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContent("내용::그림5-1하나를 그리다#1");
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setNextAttachedFileSeq((short) 0);

		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();
		{
			boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList
					.size());
			boardModifyReq.setOldAttachedFileList(oldAttachedFileList);
		}

		List<BoardModifyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardModifyReq.NewAttachedFile>();
		{

			boardModifyReq.setNewAttachedFileCnt(newAttachedFileList.size());
			boardModifyReq.setNewAttachedFileList(newAttachedFileList);
		}

		BoardModifyReqServerTask boardModifyReqServerTask = null;
		try {
			boardModifyReqServerTask = new BoardModifyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			boardModifyReqServerTask.doWork(TEST_DBCP_NAME, boardModifyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder("타인[")
					.append(boardWriteReq.getRequestedUserID())
					.append("] 게시글은 수정 할 수 없습니다").toString();

			assertEquals("타인 수정시 에러 검사", expectedErrorMessage,
					acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판수정_비정상상태_삭제() {
		String writerID = "test01";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(writerID);
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContent("차단::내용");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();

		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);

		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(writerID);
		boardDeleteReq.setBoardID(boardWriteRes.getBoardID());
		boardDeleteReq.setBoardNo(boardWriteRes.getBoardNo());

		try {
			boardDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardDeleteReq);

		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID(writerID);
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContent("내용::그림5-1하나를 그리다#1");
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setNextAttachedFileSeq((short) 0);

		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();
		{
			boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList
					.size());
			boardModifyReq.setOldAttachedFileList(oldAttachedFileList);
		}

		List<BoardModifyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardModifyReq.NewAttachedFile>();
		{

			boardModifyReq.setNewAttachedFileCnt(newAttachedFileList.size());
			boardModifyReq.setNewAttachedFileList(newAttachedFileList);
		}

		BoardModifyReqServerTask boardModifyReqServerTask = null;
		try {
			boardModifyReqServerTask = new BoardModifyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			boardModifyReqServerTask.doWork(TEST_DBCP_NAME, boardModifyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "해당 게시글은 삭제된 글입니다";

			assertEquals("게시글 삭제후 수정시 에러 검사", expectedErrorMessage,
					acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판수정_비정상상태_차단() {
		String writerID = "test01";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(writerID);
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContent("차단::내용");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();

		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);

		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardBlockReq blcokBoardBlockReq = new BoardBlockReq();
		blcokBoardBlockReq.setRequestedUserID("admin");
		blcokBoardBlockReq.setBoardID(boardWriteRes.getBoardID());
		blcokBoardBlockReq.setBoardNo(boardWriteRes.getBoardNo());

		BoardBlockReqServerTask boardBlockReqServerTask = null;
		try {
			boardBlockReqServerTask = new BoardBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			MessageResultRes messageResultRes = boardBlockReqServerTask.doWork(
					TEST_DBCP_NAME, blcokBoardBlockReq);

			// log.info(messageResultRes.toString());

			if (!messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}

		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}

		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID(writerID);
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContent("내용::그림5-1하나를 그리다#1");
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setNextAttachedFileSeq((short) 0);

		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();
		{
			boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList
					.size());
			boardModifyReq.setOldAttachedFileList(oldAttachedFileList);
		}

		List<BoardModifyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardModifyReq.NewAttachedFile>();
		{

			boardModifyReq.setNewAttachedFileCnt(newAttachedFileList.size());
			boardModifyReq.setNewAttachedFileList(newAttachedFileList);
		}

		BoardModifyReqServerTask boardModifyReqServerTask = null;
		try {
			boardModifyReqServerTask = new BoardModifyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			boardModifyReqServerTask.doWork(TEST_DBCP_NAME, boardModifyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "해당 게시글은 관리자에 의해 차단된 글입니다";

			assertEquals("게시글 차단후 수정시 에러 검사", expectedErrorMessage,
					acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판수정_비정상상태_트리차단() {
		String writerID = "test01";
		String otherID = "test02";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(writerID);
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContent("차단::내용");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();

		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);

		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardReplyReq boardReplyReq = new BoardReplyReq();
		boardReplyReq.setBoardID(boardWriteRes.getBoardID());
		boardReplyReq.setParentBoardNo(boardWriteRes.getBoardNo());
		boardReplyReq.setSubject("테스트 주제01-1");
		boardReplyReq.setContent("내용::그림01-1하나를 그리다");
		boardReplyReq.setRequestedUserID(otherID);
		boardReplyReq.setIp("127.0.0.1");

		{
			List<BoardReplyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardReplyReq.NewAttachedFile>();
			boardReplyReq.setNewAttachedFileCnt((short) newAttachedFileList
					.size());
			boardReplyReq.setNewAttachedFileList(newAttachedFileList);
		}

		BoardReplyReqServerTask boardReplyReqServerTask = null;
		try {
			boardReplyReqServerTask = new BoardReplyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardReplyRes boardReplyRes = null;
		try {
			boardReplyRes = boardReplyReqServerTask.doWork(TEST_DBCP_NAME,
					boardReplyReq);
			// log.info(boardReplyRes.toString());
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardBlockReq blcokBoardBlockReq = new BoardBlockReq();
		blcokBoardBlockReq.setRequestedUserID("admin");
		blcokBoardBlockReq.setBoardID(boardWriteRes.getBoardID());
		blcokBoardBlockReq.setBoardNo(boardWriteRes.getBoardNo());

		BoardBlockReqServerTask boardBlockReqServerTask = null;
		try {
			boardBlockReqServerTask = new BoardBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			MessageResultRes messageResultRes = boardBlockReqServerTask.doWork(
					TEST_DBCP_NAME, blcokBoardBlockReq);

			// log.info(messageResultRes.toString());

			if (!messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}

		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}

		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID(otherID);
		boardModifyReq.setBoardID(boardReplyRes.getBoardID());
		boardModifyReq.setBoardNo(boardReplyRes.getBoardNo());
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContent("내용::그림5-1하나를 그리다#1");
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setNextAttachedFileSeq((short) 0);

		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();
		{
			boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList
					.size());
			boardModifyReq.setOldAttachedFileList(oldAttachedFileList);
		}

		List<BoardModifyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardModifyReq.NewAttachedFile>();
		{

			boardModifyReq.setNewAttachedFileCnt(newAttachedFileList.size());
			boardModifyReq.setNewAttachedFileList(newAttachedFileList);
		}

		BoardModifyReqServerTask boardModifyReqServerTask = null;
		try {
			boardModifyReqServerTask = new BoardModifyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			boardModifyReqServerTask.doWork(TEST_DBCP_NAME, boardModifyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "해당 게시글은 관리자에 의해 차단된 글을 루트로 하는 트리에 속한 글입니다";

			assertEquals("게시글 차단후 수정시 에러 검사", expectedErrorMessage,
					acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판수정_새로운다음첨부파일시퀀스최대값초과() {
		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID("test01");
		boardModifyReq.setBoardID(BoardType.FREE.getBoardID());
		boardModifyReq.setBoardNo(1);
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContent("내용::그림5-1하나를 그리다#1");
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setNextAttachedFileSeq((short) 255);

		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();
		{
			boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList
					.size());
			boardModifyReq.setOldAttachedFileList(oldAttachedFileList);
		}

		List<BoardModifyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardModifyReq.NewAttachedFile>();
		{
			{
				BoardModifyReq.NewAttachedFile newAttachedFile = new BoardModifyReq.NewAttachedFile();
				newAttachedFile.setAttachedFileName("임시첨부파일02.jpg");
				newAttachedFile.setAttachedFileSize(1024);

				newAttachedFileList.add(newAttachedFile);
			}
			boardModifyReq.setNewAttachedFileCnt(newAttachedFileList.size());
			boardModifyReq.setNewAttachedFileList(newAttachedFileList);
		}

		BoardModifyReqServerTask boardModifyReqServerTask = null;
		try {
			boardModifyReqServerTask = new BoardModifyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			boardModifyReqServerTask.doWork(TEST_DBCP_NAME, boardModifyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder()
					.append("새로운 '다음 첨부 파일 시퀀스 번호'(= 기존 '다음 첨부 파일 시퀀스 번호'[")
					.append(boardModifyReq.getNextAttachedFileSeq())
					.append("] + 신규 첨부 파일 갯수[")
					.append(boardModifyReq.getNewAttachedFileCnt())
					.append("])가 최대 값(=255)을 초과하였습니다").toString();

			assertEquals("새로운 다음 첨부 파일 시퀀스 최대값 초과 검사", expectedErrorMessage,
					acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판수정_다음첨부파일시퀀스번호DB값과다름() {
		String writerID = "test01";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(writerID);
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContent("차단::내용");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();

		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);

		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID(writerID);
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContent("내용::그림5-1하나를 그리다#1");
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setNextAttachedFileSeq((short) 10);

		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();
		{
			boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList
					.size());
			boardModifyReq.setOldAttachedFileList(oldAttachedFileList);
		}

		List<BoardModifyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardModifyReq.NewAttachedFile>();
		{

			boardModifyReq.setNewAttachedFileCnt(newAttachedFileList.size());
			boardModifyReq.setNewAttachedFileList(newAttachedFileList);
		}

		BoardModifyReqServerTask boardModifyReqServerTask = null;
		try {
			boardModifyReqServerTask = new BoardModifyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			boardModifyReqServerTask.doWork(TEST_DBCP_NAME, boardModifyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder()
					.append("입력 메시지로 받은 '다음 첨부 파일 시퀀스 번호'[")
					.append(boardModifyReq.getNextAttachedFileSeq())
					.append("]가 DB 값[")
					.append(boardWriteReq.getNewAttachedFileCnt())
					.append("]과 다릅니다").toString();

			assertEquals("다음 첨부 파일 시퀀스번호 DB값과 다름 검사", expectedErrorMessage,
					acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판수정_구첨부파일시퀀스_중복() {
		String writerID = "test01";
		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID(writerID);
		boardModifyReq.setBoardID(BoardType.FREE.getBoardID());
		boardModifyReq.setBoardNo(1);
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContent("내용::그림5-1하나를 그리다#1");
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setNextAttachedFileSeq((short) 1);

		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();
		{
			{
				BoardModifyReq.OldAttachedFile oldAttachedFile = new BoardModifyReq.OldAttachedFile();

				oldAttachedFile.setAttachedFileSeq((short) 0);

				oldAttachedFileList.add(oldAttachedFile);
			}

			{
				BoardModifyReq.OldAttachedFile oldAttachedFile = new BoardModifyReq.OldAttachedFile();

				oldAttachedFile.setAttachedFileSeq((short) 0);

				oldAttachedFileList.add(oldAttachedFile);
			}
			boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList
					.size());
			boardModifyReq.setOldAttachedFileList(oldAttachedFileList);
		}

		List<BoardModifyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardModifyReq.NewAttachedFile>();
		{

			boardModifyReq.setNewAttachedFileCnt(newAttachedFileList.size());
			boardModifyReq.setNewAttachedFileList(newAttachedFileList);
		}

		BoardModifyReqServerTask boardModifyReqServerTask = null;
		try {
			boardModifyReqServerTask = new BoardModifyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			boardModifyReqServerTask.doWork(TEST_DBCP_NAME, boardModifyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder()
					.append("보존을 원하는 구 첨부 파일 목록에서 증복된 첨부 파일 시퀀스[").append(0)
					.append("]가 존재합니다").toString();

			assertEquals("보존을 원하는 구 첨부파일 시쿼스 중복 에러 검사", expectedErrorMessage,
					acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판수정_구첨부파일시퀀스_다음첨부파일시퀀보다큰경우() {
		String writerID = "test01";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(writerID);
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContent("차단::내용");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();

		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);

		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID(writerID);
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContent("내용::그림5-1하나를 그리다#1");
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setNextAttachedFileSeq((short) 0);

		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();
		{
			{
				BoardModifyReq.OldAttachedFile oldAttachedFile = new BoardModifyReq.OldAttachedFile();

				oldAttachedFile.setAttachedFileSeq((short) 0);

				oldAttachedFileList.add(oldAttachedFile);
			}
			boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList
					.size());
			boardModifyReq.setOldAttachedFileList(oldAttachedFileList);
		}

		List<BoardModifyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardModifyReq.NewAttachedFile>();
		{

			boardModifyReq.setNewAttachedFileCnt(newAttachedFileList.size());
			boardModifyReq.setNewAttachedFileList(newAttachedFileList);
		}

		BoardModifyReqServerTask boardModifyReqServerTask = null;
		try {
			boardModifyReqServerTask = new BoardModifyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			boardModifyReqServerTask.doWork(TEST_DBCP_NAME, boardModifyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder()
					.append("보존을 원하는 구 첨부 파일 시퀀스[").append(0)
					.append("]가 다음 첨부 파일 시퀀스[")
					.append(boardModifyReq.getNextAttachedFileSeq())
					.append("]보다 크거나 같습니다").toString();

			assertEquals("구 첨부 파일 시퀀가 다음 첨부 파일 시퀀 보다 큰 경우 에러 검사",
					expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판수정_구첨부파일시퀀스_첨부파일없는경우에DB와불일치() {
		String writerID = "test01";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(writerID);
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContent("차단::내용");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();
		{
			BoardWriteReq.NewAttachedFile newAttachedFile = new BoardWriteReq.NewAttachedFile();
			newAttachedFile.setAttachedFileName("test01.jpg");
			newAttachedFile.setAttachedFileSize(1024);

			newAttachedFileList.add(newAttachedFile);

		}

		{
			BoardWriteReq.NewAttachedFile newAttachedFile = new BoardWriteReq.NewAttachedFile();
			newAttachedFile.setAttachedFileName("test02.jpg");
			newAttachedFile.setAttachedFileSize(1025);

			newAttachedFileList.add(newAttachedFile);

		}

		boardWriteReq.setNewAttachedFileCnt((short) newAttachedFileList.size());
		boardWriteReq.setNewAttachedFileList(newAttachedFileList);

		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID(writerID);
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContent("내용::그림5-1하나를 그리다#1");
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setNextAttachedFileSeq((short) 2);

		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();
		boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList.size());
		boardModifyReq.setOldAttachedFileList(oldAttachedFileList);

		List<BoardModifyReq.NewAttachedFile> newAttachedFileListForModify = new ArrayList<BoardModifyReq.NewAttachedFile>();
		boardModifyReq.setNewAttachedFileCnt(newAttachedFileListForModify
				.size());
		boardModifyReq.setNewAttachedFileList(newAttachedFileListForModify);

		BoardModifyReqServerTask boardModifyReqServerTask = null;
		try {
			boardModifyReqServerTask = new BoardModifyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			boardModifyReqServerTask.doWork(TEST_DBCP_NAME, boardModifyReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		{
			BoardModifyReq.OldAttachedFile oldAttachedFile 
				= new BoardModifyReq.OldAttachedFile();
			oldAttachedFile.setAttachedFileSeq((short) 0);
			oldAttachedFileList.add(oldAttachedFile);
		}

		boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList.size());

		try {
			boardModifyReqServerTask.doWork(TEST_DBCP_NAME, boardModifyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "구 첨부 파일들이 존재 하지 않는데 보존을 원하는 구 첨부 파일 목록을 요청하셨습니다";

			assertEquals("구 첨부 파일이 없는데 보존을 원하는 구첨부 파일 시퀀스가 파라미터로 넘어온 경우 에러 검사",
					expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판수정_구첨부파일시퀀스_첨부파일있는경우DB불일치() {
		String writerID = "test01";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(writerID);
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContent("차단::내용");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();
		{
			BoardWriteReq.NewAttachedFile newAttachedFile = new BoardWriteReq.NewAttachedFile();
			newAttachedFile.setAttachedFileName("test01.jpg");
			newAttachedFile.setAttachedFileSize(1024);

			newAttachedFileList.add(newAttachedFile);

		}

		{
			BoardWriteReq.NewAttachedFile newAttachedFile = new BoardWriteReq.NewAttachedFile();
			newAttachedFile.setAttachedFileName("test02.jpg");
			newAttachedFile.setAttachedFileSize(1025);

			newAttachedFileList.add(newAttachedFile);

		}

		boardWriteReq.setNewAttachedFileCnt((short) newAttachedFileList.size());
		boardWriteReq.setNewAttachedFileList(newAttachedFileList);

		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID(writerID);
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContent("내용::그림5-1하나를 그리다#1");
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setNextAttachedFileSeq((short) 2);

		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();
		{
			BoardModifyReq.OldAttachedFile oldAttachedFile 
				= new BoardModifyReq.OldAttachedFile();
			oldAttachedFile.setAttachedFileSeq((short) 1);
			oldAttachedFileList.add(oldAttachedFile);
		}
		boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList.size());
		boardModifyReq.setOldAttachedFileList(oldAttachedFileList);

		List<BoardModifyReq.NewAttachedFile> newAttachedFileListForModify = new ArrayList<BoardModifyReq.NewAttachedFile>();
		boardModifyReq.setNewAttachedFileCnt(newAttachedFileListForModify
				.size());
		boardModifyReq.setNewAttachedFileList(newAttachedFileListForModify);

		BoardModifyReqServerTask boardModifyReqServerTask = null;
		try {
			boardModifyReqServerTask = new BoardModifyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			boardModifyReqServerTask.doWork(TEST_DBCP_NAME, boardModifyReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		{
			BoardModifyReq.OldAttachedFile oldAttachedFile 
				= new BoardModifyReq.OldAttachedFile();
			oldAttachedFile.setAttachedFileSeq((short) 0);
			oldAttachedFileList.add(oldAttachedFile);
		}

		boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList.size());

		try {
			boardModifyReqServerTask.doWork(TEST_DBCP_NAME, boardModifyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = new StringBuilder(
					"보존을 원하는 구 첨부 파일 목록[=[0, 1]]중 실제 구 첨부 파일 목록[=[1]]에 존재하지 않는 첨부 파일이 존재합니다")
					.toString();

			assertEquals("구 첨부 파일 목록 DB 불일치 에러 검사",
					expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판수정_정상_본인() {
		String writerID = "test01";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(writerID);
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContent("차단::내용");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();
		{
			BoardWriteReq.NewAttachedFile newAttachedFile = new BoardWriteReq.NewAttachedFile();
			newAttachedFile.setAttachedFileName("test01.jpg");
			newAttachedFile.setAttachedFileSize(1024);

			newAttachedFileList.add(newAttachedFile);

		}

		{
			BoardWriteReq.NewAttachedFile newAttachedFile = new BoardWriteReq.NewAttachedFile();
			newAttachedFile.setAttachedFileName("test02.jpg");
			newAttachedFile.setAttachedFileSize(1025);

			newAttachedFileList.add(newAttachedFile);

		}

		boardWriteReq.setNewAttachedFileCnt((short) newAttachedFileList.size());
		boardWriteReq.setNewAttachedFileList(newAttachedFileList);

		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID(writerID);
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContent("내용::그림5-1하나를 그리다#1");
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setNextAttachedFileSeq((short) 2);

		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();
		{
			BoardModifyReq.OldAttachedFile oldAttachedFile 
				= new BoardModifyReq.OldAttachedFile();
			oldAttachedFile.setAttachedFileSeq((short) 1);
			oldAttachedFileList.add(oldAttachedFile);
		}
		boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList.size());
		boardModifyReq.setOldAttachedFileList(oldAttachedFileList);

		List<BoardModifyReq.NewAttachedFile> newAttachedFileListForModify = new ArrayList<BoardModifyReq.NewAttachedFile>();
		boardModifyReq.setNewAttachedFileCnt(newAttachedFileListForModify
				.size());
		boardModifyReq.setNewAttachedFileList(newAttachedFileListForModify);

		BoardModifyReqServerTask boardModifyReqServerTask = null;
		try {
			boardModifyReqServerTask = new BoardModifyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			boardModifyReqServerTask.doWork(TEST_DBCP_NAME, boardModifyReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
		}
				
		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setBoardID(boardWriteRes.getBoardID());
		boardDetailReq.setBoardNo(boardWriteRes.getBoardNo());
		boardDetailReq.setRequestedUserID(writerID);

		BoardDetailReqServerTask boardDetailReqServerTask = null;
		try {
			boardDetailReqServerTask = new BoardDetailReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardDetailRes boardDetailRes = null;
		try {
			boardDetailRes = boardDetailReqServerTask.doWork(TEST_DBCP_NAME, boardDetailReq);
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
		
		assertEquals("제목 비교", boardModifyReq.getSubject(),
				boardDetailRes.getSubject());
		assertEquals("내용 비교", boardModifyReq.getContent(),
				boardDetailRes.getContent());
		assertEquals("마지막 수정자 비교", boardModifyReq.getRequestedUserID(),
				boardDetailRes.getLastModifierID());
		
		assertEquals("첨부 파일 갯수 비교", 1,
				boardDetailRes.getAttachedFileCnt());
		assertEquals("다음 첨부 파일 시퀀스 비교", 2,
				boardDetailRes.getNextAttachedFileSeq());

		List<BoardDetailRes.AttachedFile> actualAttachedFileList = boardDetailRes
				.getAttachedFileList();
		
		for (BoardDetailRes.AttachedFile attachedFile: actualAttachedFileList) {
			assertEquals("수정후 남아 있는 첨부 파일 시퀀스 비교", 1, attachedFile.getAttachedFileSeq());
		}
	}

	@Test
	public void 게시판수정_정상_관리자() {
		String writerID = "test01";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(writerID);
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContent("차단::내용");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();
		{
			BoardWriteReq.NewAttachedFile newAttachedFile = new BoardWriteReq.NewAttachedFile();
			newAttachedFile.setAttachedFileName("test01.jpg");
			newAttachedFile.setAttachedFileSize(1024);

			newAttachedFileList.add(newAttachedFile);

		}

		{
			BoardWriteReq.NewAttachedFile newAttachedFile = new BoardWriteReq.NewAttachedFile();
			newAttachedFile.setAttachedFileName("test02.jpg");
			newAttachedFile.setAttachedFileSize(1025);

			newAttachedFileList.add(newAttachedFile);

		}

		boardWriteReq.setNewAttachedFileCnt((short) newAttachedFileList.size());
		boardWriteReq.setNewAttachedFileList(newAttachedFileList);

		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID("admin");
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContent("내용::그림5-1하나를 그리다#1");
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setNextAttachedFileSeq((short) 2);

		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();
		{
			BoardModifyReq.OldAttachedFile oldAttachedFile 
				= new BoardModifyReq.OldAttachedFile();
			oldAttachedFile.setAttachedFileSeq((short) 1);
			oldAttachedFileList.add(oldAttachedFile);
		}
		boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList.size());
		boardModifyReq.setOldAttachedFileList(oldAttachedFileList);

		List<BoardModifyReq.NewAttachedFile> newAttachedFileListForModify = new ArrayList<BoardModifyReq.NewAttachedFile>();
		boardModifyReq.setNewAttachedFileCnt(newAttachedFileListForModify
				.size());
		boardModifyReq.setNewAttachedFileList(newAttachedFileListForModify);

		BoardModifyReqServerTask boardModifyReqServerTask = null;
		try {
			boardModifyReqServerTask = new BoardModifyReqServerTask();
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}

		try {
			boardModifyReqServerTask.doWork(TEST_DBCP_NAME, boardModifyReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
		}
				
		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setBoardID(boardWriteRes.getBoardID());
		boardDetailReq.setBoardNo(boardWriteRes.getBoardNo());
		boardDetailReq.setRequestedUserID(writerID);

		BoardDetailReqServerTask boardDetailReqServerTask = null;
		try {
			boardDetailReqServerTask = new BoardDetailReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardDetailRes boardDetailRes = null;
		try {
			boardDetailRes = boardDetailReqServerTask.doWork(TEST_DBCP_NAME, boardDetailReq);
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
		
		assertEquals("제목 비교", boardModifyReq.getSubject(),
				boardDetailRes.getSubject());
		assertEquals("내용 비교", boardModifyReq.getContent(),
				boardDetailRes.getContent());
		assertEquals("마지막 수정자 비교", boardModifyReq.getRequestedUserID(),
				boardDetailRes.getLastModifierID());
		
		assertEquals("첨부 파일 갯수 비교", 1,
				boardDetailRes.getAttachedFileCnt());
		assertEquals("다음 첨부 파일 시퀀스 비교", 2,
				boardDetailRes.getNextAttachedFileSeq());

		List<BoardDetailRes.AttachedFile> actualAttachedFileList = boardDetailRes
				.getAttachedFileList();
		
		for (BoardDetailRes.AttachedFile attachedFile: actualAttachedFileList) {
			assertEquals("수정후 남아 있는 첨부 파일 시퀀스 비교", 1, attachedFile.getAttachedFileSeq());
		}
	}

}
