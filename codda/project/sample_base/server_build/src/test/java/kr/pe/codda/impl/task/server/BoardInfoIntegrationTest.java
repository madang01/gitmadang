package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardVoteTb.SB_BOARD_VOTE_TB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DBCPDataSourceNotFoundException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.BoardInfoAddReq.BoardInfoAddReq;
import kr.pe.codda.impl.message.BoardInfoAddRes.BoardInfoAddRes;
import kr.pe.codda.impl.message.BoardInfoDeleteReq.BoardInfoDeleteReq;
import kr.pe.codda.impl.message.BoardInfoListReq.BoardInfoListReq;
import kr.pe.codda.impl.message.BoardInfoListRes.BoardInfoListRes;
import kr.pe.codda.impl.message.BoardInfoModifyReq.BoardInfoModifyReq;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardListType;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.BoardReplyPolicyType;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public class BoardInfoIntegrationTest extends AbstractJunitTest {
	private final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;
	
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
						MemberRoleType.MEMBER, userID, nickname, pwdHint,
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
						MemberRoleType.MEMBER, userID, nickname, pwdHint,
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

			create.delete(SB_BOARD_VOTE_TB).execute();
			create.delete(SB_BOARD_FILELIST_TB).execute();
			create.delete(SB_BOARD_HISTORY_TB).execute();
			create.delete(SB_BOARD_TB).execute();

			/** sample_base 프로젝에 예약된 0 ~ 3 까지의 게시판 식별자를 제외한 게시판 정보 삭제  */
			create.delete(SB_BOARD_INFO_TB).where(SB_BOARD_INFO_TB.BOARD_ID.ge(UByte.valueOf(4))).execute();
			
			create.update(SB_BOARD_INFO_TB)
					.set(SB_BOARD_INFO_TB.CNT, 0)
					.set(SB_BOARD_INFO_TB.TOTAL, 0)
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
	
	@Test
	public void 게시판정보목록조회_일반인() {
		String requestedUserIDForUser = "test01";
		
		BoardInfoListReqServerTask boardInfoListReqServerTask = null;
		try {
			boardInfoListReqServerTask = new BoardInfoListReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		BoardInfoListReq boardInfoListReq = new BoardInfoListReq();
		boardInfoListReq.setRequestedUserID(requestedUserIDForUser);
		
		try {
			boardInfoListReqServerTask.doWork(TEST_DBCP_NAME, boardInfoListReq);
			
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "게시판 정보 목록 조회 서비스는 관리자 전용 서비스입니다";

			assertEquals("일반인 요청시 관리자 전용 서비스임을 알리며 거부하는지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 게시판정보목록조회_정상() {
		String requestedUserIDForAdmin = "admin";
		
		BoardInfoListReqServerTask boardInfoListReqServerTask = null;
		try {
			boardInfoListReqServerTask = new BoardInfoListReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		BoardInfoListReq boardInfoListReq = new BoardInfoListReq();
		boardInfoListReq.setRequestedUserID(requestedUserIDForAdmin);
		
		BoardInfoListRes boardInfoListRes = null;
		try {
			boardInfoListRes = boardInfoListReqServerTask.doWork(TEST_DBCP_NAME, boardInfoListReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		
		log.info(boardInfoListRes.toString());
	}
	
	@Test
	public void 게시판정보추가_일반인() {
		String requestedUserIDForUser = "test01";
		
		BoardInfoAddReqServerTask boardInfoAddReqServerTask = null;
		try {
			boardInfoAddReqServerTask = new BoardInfoAddReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		BoardInfoAddReq boardInfoAddReq = new BoardInfoAddReq();
		boardInfoAddReq.setRequestedUserID(requestedUserIDForUser);
		boardInfoAddReq.setBoardName("단위테스트용 추가 게시판");
		boardInfoAddReq.setBoardInformation("단위 테스트에서 사용된 게시판");
		boardInfoAddReq.setBoardListType(BoardListType.TREE.getValue());
		boardInfoAddReq.setBoardReplyPolicyType(BoardReplyPolicyType.NO_SUPPORTED.getValue());
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.ADMIN.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.MEMBER.getValue());
		
		
		try {
			boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
			
			 fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "게시판 정보 추가 서비스는 관리자 전용 서비스입니다";

			assertEquals("일반인 요청시 관리자 전용 서비스임을 알리며 거부하는지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 게시판정보추가_최대값초과() {
		String requestedUserIDForAdmin = "admin";
		
		BoardInfoAddReq maxBoardInfoAddReq = new BoardInfoAddReq();
		maxBoardInfoAddReq.setRequestedUserID(requestedUserIDForAdmin);
		maxBoardInfoAddReq.setBoardName("단위테스트용 추가 게시판");
		maxBoardInfoAddReq.setBoardInformation("단위 테스트에서 사용된 게시판");
		maxBoardInfoAddReq.setBoardListType(BoardListType.TREE.getValue());
		maxBoardInfoAddReq.setBoardReplyPolicyType(BoardReplyPolicyType.NO_SUPPORTED.getValue());
		maxBoardInfoAddReq.setBoardWritePermissionType(PermissionType.ADMIN.getValue());
		maxBoardInfoAddReq.setBoardReplyPermissionType(PermissionType.MEMBER.getValue());
		
		DataSource dataSource = null;

		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(TEST_DBCP_NAME);
		} catch (IllegalArgumentException | DBCPDataSourceNotFoundException e) {
			String errorMessage = e.getMessage();

			log.warn(errorMessage, e);

			/** 지정한 이름의 dbcp 를 받지 못할 경우 아무 처리도 하지 않는다 */
			return;
		}

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(true);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));

			create.insertInto(SB_BOARD_INFO_TB).set(SB_BOARD_INFO_TB.BOARD_ID, UByte.valueOf(CommonStaticFinalVars.UNSIGNED_BYTE_MAX))
					.set(SB_BOARD_INFO_TB.BOARD_NAME, "게시판 식별자 최대값 게시판")
					.set(SB_BOARD_INFO_TB.BOARD_INFO, "단위테스트용으로 게시판 식별자 값이 최대값을 갖는 게시판")
					.set(SB_BOARD_INFO_TB.LIST_TYPE, BoardListType.TREE.getValue())
					.set(SB_BOARD_INFO_TB.REPLY_POLICY_TYPE, BoardReplyPolicyType.ALL.getValue())
					.set(SB_BOARD_INFO_TB.WRITE_PERMISSION_TYPE, PermissionType.ADMIN.getValue())
					.set(SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE, PermissionType.MEMBER.getValue())
					.set(SB_BOARD_INFO_TB.CNT, 0).set(SB_BOARD_INFO_TB.TOTAL, 0)
					.set(SB_BOARD_INFO_TB.NEXT_BOARD_NO, UInteger.valueOf(1)).execute();
			

		} catch (Exception e) {
			log.warn("error", e);

			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
			}

			fail("게시판 식별자 값이 최대값을 갖는 게시판 정보 추가 실패");
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
		
		
		BoardInfoAddReqServerTask boardInfoAddReqServerTask = null;
		try {
			boardInfoAddReqServerTask = new BoardInfoAddReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		BoardInfoAddReq boardInfoAddReq = new BoardInfoAddReq();
		boardInfoAddReq.setRequestedUserID(requestedUserIDForAdmin);
		boardInfoAddReq.setBoardName("단위테스트용 추가 게시판");
		boardInfoAddReq.setBoardInformation("단위 테스트에서 사용된 게시판");
		boardInfoAddReq.setBoardListType(BoardListType.TREE.getValue());
		boardInfoAddReq.setBoardReplyPolicyType(BoardReplyPolicyType.NO_SUPPORTED.getValue());
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.ADMIN.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.MEMBER.getValue());
		
		
		try {
			boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "새롭게 얻은 게시판 식별자 값이 최대값을 초과하여 더 이상 추가할 수 없습니다";

			assertEquals("일반인 요청시 관리자 전용 서비스임을 알리며 거부하는지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		
	}
	
	
	@Test
	public void 게시판정보추가_정상() {
		String requestedUserIDForAdmin = "admin";
		
		BoardInfoAddReqServerTask boardInfoAddReqServerTask = null;
		try {
			boardInfoAddReqServerTask = new BoardInfoAddReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		BoardInfoAddReq boardInfoAddReq = new BoardInfoAddReq();
		boardInfoAddReq.setRequestedUserID(requestedUserIDForAdmin);
		boardInfoAddReq.setBoardName("단위테스트용 추가 게시판");
		boardInfoAddReq.setBoardInformation("단위 테스트에서 사용된 게시판");
		boardInfoAddReq.setBoardListType(BoardListType.TREE.getValue());
		boardInfoAddReq.setBoardReplyPolicyType(BoardReplyPolicyType.NO_SUPPORTED.getValue());
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.ADMIN.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.MEMBER.getValue());
		
		BoardInfoAddRes boardInfoAddRes = null;
		
		try {
			boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		boolean isSuccess = false;
		
		BoardInfoListReqServerTask boardInfoListReqServerTask = null;
		try {
			boardInfoListReqServerTask = new BoardInfoListReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		BoardInfoListReq boardInfoListReq = new BoardInfoListReq();
		boardInfoListReq.setRequestedUserID(requestedUserIDForAdmin);
		
		BoardInfoListRes boardInfoListRes = null;
		try {
			boardInfoListRes = boardInfoListReqServerTask.doWork(TEST_DBCP_NAME, boardInfoListReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		for (BoardInfoListRes.BoardInfo boardInfo : boardInfoListRes.getBoardInfoList()) {
			short boardID = boardInfo.getBoardID();
			if (boardInfoAddRes.getBoardID() == boardID) {
				isSuccess = true;
				
				assertEquals("게시판 이름 비교",  boardInfoAddReq.getBoardName(), boardInfo.getBoardName());
				assertEquals("게시판 정보 비교",  boardInfoAddReq.getBoardInformation(), boardInfo.getBoardInformation());
				assertEquals("게시판 목록 유형 비교",  boardInfoAddReq.getBoardListType(), boardInfo.getBoardListType());
				assertEquals("게시판 댓글 정책 유형 비교",  boardInfoAddReq.getBoardReplyPolicyType(), boardInfo.getBoardReplyPolicyType());
				assertEquals("게시판 본문 쓰기 권한 유형 비교",  boardInfoAddReq.getBoardWritePermissionType(), boardInfo.getBoardWritePermissionType());
				assertEquals("게시판 댓글 쓰기 권한 유형 비교",  boardInfoAddReq.getBoardReplyPermissionType(), boardInfo.getBoardReplyPermissionType());
				assertEquals("게시판 목록 갯수 비교",  0, boardInfo.getCnt());
				assertEquals("게시판 전체 레코드 갯수 비교",  0, boardInfo.getTotal());
				assertEquals("다음 게시판 번호 비교",  1L, boardInfo.getNextBoardNo());
			}
		}
		
		if (! isSuccess) {
			fail("게시판 정보 목록내 추가된 게시판 식별자와 일치하는 게시판 식별자가 존재하지 않습니다");
		}
	}
	
	@Test
	public void 게시판정보삭제_일반인() {
		String requestedUserIDForUser = "test01";
		
		BoardInfoDeleteReq boardInfoDeleteReq = new BoardInfoDeleteReq();
		boardInfoDeleteReq.setBoardID((short)5);
		boardInfoDeleteReq.setRequestedUserID(requestedUserIDForUser);
		
		BoardInfoDeleteReqServerTask boardInfoDeleteReqServerTask = null;
		try {
			boardInfoDeleteReqServerTask = new BoardInfoDeleteReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		try {
			boardInfoDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardInfoDeleteReq);
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "게시판 정보 삭제 서비스는 관리자 전용 서비스입니다";

			assertEquals("일반인 요청시 관리자 전용 서비스임을 알리며 거부하는지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 게시판정보삭제_게시판정보미존재() {
		String requestedUserIDForAdmin = "admin";
		
		BoardInfoDeleteReq boardInfoDeleteReq = new BoardInfoDeleteReq();
		boardInfoDeleteReq.setBoardID((short)10);
		boardInfoDeleteReq.setRequestedUserID(requestedUserIDForAdmin);
		
		BoardInfoDeleteReqServerTask boardInfoDeleteReqServerTask = null;
		try {
			boardInfoDeleteReqServerTask = new BoardInfoDeleteReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		try {
			boardInfoDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardInfoDeleteReq);
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "지정한 게시판 식별자에 대한 게시판 정보가 존재하지 않습니다";

			assertEquals("일반인 요청시 관리자 전용 서비스임을 알리며 거부하는지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 게시판정보삭제_게시글존재() {
		String requestedUserIDForUser = "test01";
		String requestedUserIDForAdmin = "admin";
		
		BoardInfoAddReqServerTask boardInfoAddReqServerTask = null;
		try {
			boardInfoAddReqServerTask = new BoardInfoAddReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		BoardInfoAddReq boardInfoAddReq = new BoardInfoAddReq();
		boardInfoAddReq.setRequestedUserID(requestedUserIDForAdmin);
		boardInfoAddReq.setBoardName("단위테스트용 추가 게시판");
		boardInfoAddReq.setBoardInformation("단위 테스트에서 사용된 게시판");
		boardInfoAddReq.setBoardListType(BoardListType.TREE.getValue());
		boardInfoAddReq.setBoardReplyPolicyType(BoardReplyPolicyType.NO_SUPPORTED.getValue());
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.MEMBER.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.MEMBER.getValue());
		
		BoardInfoAddRes boardInfoAddRes = null;
		
		try {
			boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForUser);
		boardWriteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardWriteReq.setSubject("제목");
		boardWriteReq.setContents("내용");
		boardWriteReq.setIp("172.16.0.1");

		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();

		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);

		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(
					TEST_DBCP_NAME, boardWriteReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		log.info(boardWriteRes.toString());
		
		BoardInfoDeleteReq boardInfoDeleteReq = new BoardInfoDeleteReq();
		boardInfoDeleteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardInfoDeleteReq.setRequestedUserID(requestedUserIDForAdmin);
		
		BoardInfoDeleteReqServerTask boardInfoDeleteReqServerTask = null;
		try {
			boardInfoDeleteReqServerTask = new BoardInfoDeleteReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		try {
			boardInfoDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardInfoDeleteReq);
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "삭제를 원하는 게시판 식별자를 갖는 게시글이 존재하여 게시판 정보를 삭제 할 수 없습니다";

			assertEquals("일반인 요청시 관리자 전용 서비스임을 알리며 거부하는지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 게시판정보삭제_정상() {
		String requestedUserIDForAdmin = "admin";
		
		BoardInfoAddReqServerTask boardInfoAddReqServerTask = null;
		try {
			boardInfoAddReqServerTask = new BoardInfoAddReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		BoardInfoAddReq boardInfoAddReq = new BoardInfoAddReq();
		boardInfoAddReq.setRequestedUserID(requestedUserIDForAdmin);
		boardInfoAddReq.setBoardName("삭제를 위한 게시판");
		boardInfoAddReq.setBoardInformation("게시판 식별자에 대응하는 게시판 정보 삭제용 레코드");
		boardInfoAddReq.setBoardListType(BoardListType.TREE.getValue());
		boardInfoAddReq.setBoardReplyPolicyType(BoardReplyPolicyType.NO_SUPPORTED.getValue());
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.MEMBER.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.MEMBER.getValue());
		
		BoardInfoAddRes boardInfoAddRes = null;
		
		try {
			boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardInfoDeleteReq boardInfoDeleteReq = new BoardInfoDeleteReq();
		boardInfoDeleteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardInfoDeleteReq.setRequestedUserID(requestedUserIDForAdmin);
		
		BoardInfoDeleteReqServerTask boardInfoDeleteReqServerTask = null;
		try {
			boardInfoDeleteReqServerTask = new BoardInfoDeleteReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		MessageResultRes messageResultRes = null;
		try {
			messageResultRes = boardInfoDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardInfoDeleteReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		if (! messageResultRes.getIsSuccess()) {
			fail(messageResultRes.getResultMessage());
		}
		
		
		BoardInfoListReqServerTask boardInfoListReqServerTask = null;
		try {
			boardInfoListReqServerTask = new BoardInfoListReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		BoardInfoListReq boardInfoListReq = new BoardInfoListReq();
		boardInfoListReq.setRequestedUserID(requestedUserIDForAdmin);
		
		BoardInfoListRes boardInfoListRes = null;
		try {
			boardInfoListRes = boardInfoListReqServerTask.doWork(TEST_DBCP_NAME, boardInfoListReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		for (BoardInfoListRes.BoardInfo boardInfo : boardInfoListRes.getBoardInfoList()) {
			short boardID = boardInfo.getBoardID();
			if (boardInfoDeleteReq.getBoardID() == boardID) {
				fail("게시판 정보 목록에 삭제한 게시판 식별자 존재");
			}
		}
		
	}
	
	@Test
	public void 게시판정보수정_일반인() {
		String requestedUserIDForUser = "test01";
		
		BoardInfoModifyReqServerTask boardInfoModifyReqServerTask = null;
		try {
			boardInfoModifyReqServerTask = new BoardInfoModifyReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		BoardInfoModifyReq boardInfoModifyReq = new BoardInfoModifyReq();
		boardInfoModifyReq.setRequestedUserID(requestedUserIDForUser);
		boardInfoModifyReq.setBoardID((short)5);
		boardInfoModifyReq.setBoardName("수정를 위한 게시판");
		boardInfoModifyReq.setBoardInformation("수정용 게시판");
		boardInfoModifyReq.setBoardReplyPolicyType(BoardReplyPolicyType.NO_SUPPORTED.getValue());
		boardInfoModifyReq.setBoardWritePermissionType(PermissionType.MEMBER.getValue());
		boardInfoModifyReq.setBoardReplyPermissionType(PermissionType.MEMBER.getValue());
		
		try {
			boardInfoModifyReqServerTask.doWork(TEST_DBCP_NAME, boardInfoModifyReq);
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "게시판 정보 수정 서비스는 관리자 전용 서비스입니다";

			assertEquals("일반인 요청시 관리자 전용 서비스임을 알리며 거부하는지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 게시판정보수정_게시판정보미존재() {
		String requestedUserIDForAdmin = "admin";
		
		BoardInfoModifyReqServerTask boardInfoModifyReqServerTask = null;
		try {
			boardInfoModifyReqServerTask = new BoardInfoModifyReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		BoardInfoModifyReq boardInfoModifyReq = new BoardInfoModifyReq();
		boardInfoModifyReq.setRequestedUserID(requestedUserIDForAdmin);
		boardInfoModifyReq.setBoardID((short)5);
		boardInfoModifyReq.setBoardName("수정를 위한 게시판");
		boardInfoModifyReq.setBoardInformation("수정용 게시판");
		boardInfoModifyReq.setBoardReplyPolicyType(BoardReplyPolicyType.NO_SUPPORTED.getValue());
		boardInfoModifyReq.setBoardWritePermissionType(PermissionType.MEMBER.getValue());
		boardInfoModifyReq.setBoardReplyPermissionType(PermissionType.MEMBER.getValue());
		
		try {
			boardInfoModifyReqServerTask.doWork(TEST_DBCP_NAME, boardInfoModifyReq);
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "지정한 게시판 식별자에 대한 게시판 정보가 존재하지 않습니다";

			assertEquals("일반인 요청시 관리자 전용 서비스임을 알리며 거부하는지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
		
	
	@Test
	public void 게시판정보수정_정상() {
		String requestedUserIDForAdmin = "admin";
		
		BoardInfoAddReqServerTask boardInfoAddReqServerTask = null;
		try {
			boardInfoAddReqServerTask = new BoardInfoAddReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		BoardInfoAddReq boardInfoAddReq = new BoardInfoAddReq();
		boardInfoAddReq.setRequestedUserID(requestedUserIDForAdmin);
		boardInfoAddReq.setBoardName("수정전 게시판");
		boardInfoAddReq.setBoardInformation("수정전 게시판");
		boardInfoAddReq.setBoardListType(BoardListType.TREE.getValue());
		boardInfoAddReq.setBoardReplyPolicyType(BoardReplyPolicyType.NO_SUPPORTED.getValue());
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.MEMBER.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.ADMIN.getValue());
		
		BoardInfoAddRes boardInfoAddRes = null;
		
		try {
			boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardInfoModifyReqServerTask boardInfoModifyReqServerTask = null;
		try {
			boardInfoModifyReqServerTask = new BoardInfoModifyReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		BoardInfoModifyReq boardInfoModifyReq = new BoardInfoModifyReq();
		boardInfoModifyReq.setRequestedUserID(requestedUserIDForAdmin);
		boardInfoModifyReq.setBoardID(boardInfoAddRes.getBoardID());
		boardInfoModifyReq.setBoardName("수정완료후 게시판");
		boardInfoModifyReq.setBoardInformation("수정완료후 게시판");
		boardInfoModifyReq.setBoardReplyPolicyType(BoardReplyPolicyType.ALL.getValue());
		boardInfoModifyReq.setBoardWritePermissionType(PermissionType.ADMIN.getValue());
		boardInfoModifyReq.setBoardReplyPermissionType(PermissionType.MEMBER.getValue());
		
		try {
			boardInfoModifyReqServerTask.doWork(TEST_DBCP_NAME, boardInfoModifyReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		
		boolean isSuccess = false;
		
		BoardInfoListReqServerTask boardInfoListReqServerTask = null;
		try {
			boardInfoListReqServerTask = new BoardInfoListReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		BoardInfoListReq boardInfoListReq = new BoardInfoListReq();
		boardInfoListReq.setRequestedUserID(requestedUserIDForAdmin);
		
		BoardInfoListRes boardInfoListRes = null;
		try {
			boardInfoListRes = boardInfoListReqServerTask.doWork(TEST_DBCP_NAME, boardInfoListReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		for (BoardInfoListRes.BoardInfo boardInfo : boardInfoListRes.getBoardInfoList()) {
			short boardID = boardInfo.getBoardID();
			if (boardInfoAddRes.getBoardID() == boardID) {
				isSuccess = true;
				
				assertEquals("게시판 이름 비교",  boardInfoModifyReq.getBoardName(), boardInfo.getBoardName());
				assertEquals("게시판 정보 비교",  boardInfoModifyReq.getBoardInformation(), boardInfo.getBoardInformation());				
				assertEquals("게시판 댓글 정책 유형 비교",  boardInfoModifyReq.getBoardReplyPolicyType(), boardInfo.getBoardReplyPolicyType());
				assertEquals("게시판 본문 쓰기 권한 유형 비교",  boardInfoModifyReq.getBoardWritePermissionType(), boardInfo.getBoardWritePermissionType());
				assertEquals("게시판 댓글 쓰기 권한 유형 비교",  boardInfoModifyReq.getBoardReplyPermissionType(), boardInfo.getBoardReplyPermissionType());
				assertEquals("게시판 목록 갯수 비교",  0, boardInfo.getCnt());
				assertEquals("게시판 전체 레코드 갯수 비교",  0, boardInfo.getTotal());
				assertEquals("다음 게시판 번호 비교",  1L, boardInfo.getNextBoardNo());
			}
		}
		
		if (! isSuccess) {
			fail("게시판 정보 목록내 추가된 게시판 식별자와 일치하는 게시판 식별자가 존재하지 않습니다");
		}
	}
}
