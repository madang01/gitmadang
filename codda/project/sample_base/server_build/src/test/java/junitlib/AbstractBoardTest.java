package junitlib;

import static kr.pe.codda.impl.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardVoteTb.SB_BOARD_VOTE_TB;
import static kr.pe.codda.impl.jooq.tables.SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB;
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

import kr.pe.codda.common.exception.DBCPDataSourceNotFoundException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardListType;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public abstract class AbstractBoardTest extends AbstractJunitTest {
	protected final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;
	
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

}
