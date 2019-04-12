package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;
import org.junit.Test;

import junitlib.AbstractBoardTest;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.message.BoardDetailReq.BoardDetailReq;
import kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes;
import kr.pe.codda.impl.message.BoardInfoAddReq.BoardInfoAddReq;
import kr.pe.codda.impl.message.BoardInfoAddRes.BoardInfoAddRes;
import kr.pe.codda.impl.message.BoardListReq.BoardListReq;
import kr.pe.codda.impl.message.BoardListRes.BoardListRes;
import kr.pe.codda.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.codda.impl.message.BoardReplyRes.BoardReplyRes;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardListType;
import kr.pe.codda.server.lib.BoardReplyPolicyType;
import kr.pe.codda.server.lib.BoardTree;
import kr.pe.codda.server.lib.BoardTreeNode;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.VirtualBoardTreeBuilderIF;

public class BoardReplyReqServerTaskTest extends AbstractBoardTest {

	@Test
	public void 댓글등록_잘못된게시판식별자() {
		String requestedUserIDForMember = "test01";
		short badBoardID = 7;
	
		BoardReplyReq boardReplyReq = new BoardReplyReq();
		boardReplyReq.setRequestedUserID(requestedUserIDForMember);
		boardReplyReq.setIp("127.0.0.1");
		boardReplyReq.setBoardID(badBoardID);
		boardReplyReq.setParentBoardNo(1);
		boardReplyReq.setSubject("테스트 주제01-1");
		boardReplyReq.setContents("내용::그림01-1하나를 그리다");
		
	
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
	
			String expectedErrorMessage = new StringBuilder("입력 받은 게시판 식별자[").append(badBoardID)
					.append("]가 게시판 정보 테이블에 존재하지  않습니다").toString();
	
			assertEquals("댓글 등록시 잘못된 게시판 식별자일때 경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	
	@Test
	public void 댓글등록_최대갯수초과() {
		String requestedUserIDForAdmin = "admin";
		String requestedUserIDForMember = "test01";
		
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
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.GUEST.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.GUEST.getValue());
		
		BoardInfoAddRes boardInfoAddRes = null;
		
		try {
			boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		try {
			DataSource dataSource = DBCPManager.getInstance().getBasicDataSource(TEST_DBCP_NAME);

			Connection conn = null;
			try {
				conn = dataSource.getConnection();
				conn.setAutoCommit(false);

				DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
				
				create.update(SB_BOARD_INFO_TB).set(SB_BOARD_INFO_TB.NEXT_BOARD_NO, UInteger.valueOf(CommonStaticFinalVars.UNSIGNED_INTEGER_MAX))
				.where(SB_BOARD_INFO_TB.BOARD_ID.eq(UByte.valueOf(boardInfoAddRes.getBoardID())))
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
				
				throw e;
			} finally {
				if (null != conn) {
					try {
						conn.close();
					} catch (Exception e) {
						log.warn("fail to close the db connection", e);
					}
				}
			}
		} catch(Exception e) {
			log.warn(e.getMessage(), e);

			fail(e.getMessage());
		}
		
		
		BoardReplyReq boardReplyReq = new BoardReplyReq();
		boardReplyReq.setRequestedUserID(requestedUserIDForMember);
		boardReplyReq.setIp("127.0.0.1");
		boardReplyReq.setBoardID(boardInfoAddRes.getBoardID());
		boardReplyReq.setParentBoardNo(1);
		boardReplyReq.setSubject("테스트 주제01-1");
		boardReplyReq.setContents("내용::그림01-1하나를 그리다");		

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

		try {
			boardReplyReqServerTask.doWork(TEST_DBCP_NAME, boardReplyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "단위테스트용 게시판[4]은 최대 갯수까지 글이 등록되어 더 이상 글을 추가 할 수 없습니다";

			assertEquals("최대 갯수 초과 경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 댓글등록_목록유형트리_제목미입력() {
		String requestedUserIDForAdmin = "admin";
		String requestedUserIDForMember = "test01";
		
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
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.GUEST.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.GUEST.getValue());
		
		BoardInfoAddRes boardInfoAddRes = null;
		
		try {
			boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardReplyReq boardReplyReq = new BoardReplyReq();
		boardReplyReq.setRequestedUserID(requestedUserIDForMember);
		boardReplyReq.setIp("127.0.0.1");
		boardReplyReq.setBoardID(boardInfoAddRes.getBoardID());
		boardReplyReq.setParentBoardNo(1);
		boardReplyReq.setSubject("");
		boardReplyReq.setContents("내용::그림01-1하나를 그리다");		

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

		try {
			boardReplyReqServerTask.doWork(TEST_DBCP_NAME, boardReplyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "게시글 제목을 입력해 주세요";

			assertEquals("제목 미입력 경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	
	@Test
	public void 댓글등록_댓글유형댓글없음() {
		String requestedUserIDForAdmin = "admin";
		String requestedUserIDForMember = "test01";
		
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
		boardInfoAddReq.setBoardReplyPolicyType(BoardReplyPolicyType.NO_REPLY.getValue());
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.GUEST.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.GUEST.getValue());
		
		BoardInfoAddRes boardInfoAddRes = null;
		
		try {
			boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardReplyReq boardReplyReq = new BoardReplyReq();
		boardReplyReq.setRequestedUserID(requestedUserIDForMember);
		boardReplyReq.setIp("127.0.0.1");
		boardReplyReq.setBoardID(boardInfoAddRes.getBoardID());
		boardReplyReq.setParentBoardNo(1);
		boardReplyReq.setSubject("하하호호");
		boardReplyReq.setContents("내용::그림01-1하나를 그리다");		

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

		try {
			boardReplyReqServerTask.doWork(TEST_DBCP_NAME, boardReplyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "단위테스트용 게시판[4]은 댓글 쓰기가 금지되었습니다";

			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 댓글등록_잘못된게시글비밀번호() {
		String requestedUserIDForAdmin = "admin";
		String requestedUserIDForGuest = "guest";
		
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
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.GUEST.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.GUEST.getValue());
		
		BoardInfoAddRes boardInfoAddRes = null;
		
		try {
			boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardReplyReq boardReplyReq = new BoardReplyReq();
		boardReplyReq.setRequestedUserID(requestedUserIDForGuest);
		boardReplyReq.setIp("127.0.0.1");
		boardReplyReq.setBoardID(boardInfoAddRes.getBoardID());
		boardReplyReq.setParentBoardNo(1);
		boardReplyReq.setSubject("하하호호");
		boardReplyReq.setContents("내용::그림01-1하나를 그리다");	
		boardReplyReq.setPwdHashBase64("aa##");

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

		try {
			boardReplyReqServerTask.doWork(TEST_DBCP_NAME, boardReplyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "베이스64(base64)로 인코딩한 게시글 비밀번호 해쉬가 잘못되었습니다";

			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 댓글등록_접근권한이관리자_손님() {
		String requestedUserIDForAdmin = "admin";
		String requestedUserIDForGuest = "guest";
		
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
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.GUEST.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.ADMIN.getValue());
		
		BoardInfoAddRes boardInfoAddRes = null;
		
		try {
			boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardReplyReq boardReplyReq = new BoardReplyReq();
		boardReplyReq.setRequestedUserID(requestedUserIDForGuest);
		boardReplyReq.setIp("127.0.0.1");
		boardReplyReq.setBoardID(boardInfoAddRes.getBoardID());
		boardReplyReq.setParentBoardNo(1);
		boardReplyReq.setSubject("하하호호");
		boardReplyReq.setContents("내용::그림01-1하나를 그리다");		

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

		try {
			boardReplyReqServerTask.doWork(TEST_DBCP_NAME, boardReplyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "게시판 댓글 등록 서비스는 관리자 전용 서비스입니다";

			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	
	@Test
	public void 댓글등록_접근권한이관리자_일반회원() {
		String requestedUserIDForAdmin = "admin";
		String requestedUserIDForMember = "test01";
		
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
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.GUEST.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.ADMIN.getValue());
		
		BoardInfoAddRes boardInfoAddRes = null;
		
		try {
			boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardReplyReq boardReplyReq = new BoardReplyReq();
		boardReplyReq.setRequestedUserID(requestedUserIDForMember);
		boardReplyReq.setIp("127.0.0.1");
		boardReplyReq.setBoardID(boardInfoAddRes.getBoardID());
		boardReplyReq.setParentBoardNo(1);
		boardReplyReq.setSubject("하하호호");
		boardReplyReq.setContents("내용::그림01-1하나를 그리다");		

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

		try {
			boardReplyReqServerTask.doWork(TEST_DBCP_NAME, boardReplyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "게시판 댓글 등록 서비스는 관리자 전용 서비스입니다";

			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 댓글등록_접근권한이관리자_관리자_OK() {
		String requestedUserIDForAdmin = "admin";
		String requestedUserIDForMember = "test01";
		
		
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
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.GUEST.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.ADMIN.getValue());
		
		BoardInfoAddRes boardInfoAddRes = null;
		
		try {
			boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMember);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardWriteReq.setSubject("테스트 주제01");
		boardWriteReq.setContents("내용::그림01 하나를 그리다");
		

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

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
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
		boardReplyReq.setRequestedUserID(requestedUserIDForAdmin);
		boardReplyReq.setIp("127.0.0.1");
		boardReplyReq.setBoardID(boardWriteRes.getBoardID());
		boardReplyReq.setParentBoardNo(boardWriteRes.getBoardNo());
		boardReplyReq.setSubject("하하호호");
		boardReplyReq.setContents("내용::그림01-1하나를 그리다");		

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
			boardReplyRes = boardReplyReqServerTask.doWork(TEST_DBCP_NAME, boardReplyReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		
		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setRequestedUserID(boardReplyReq.getRequestedUserID());
		boardDetailReq.setBoardID(boardReplyRes.getBoardID());
		boardDetailReq.setBoardNo(boardReplyRes.getBoardNo());
		

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
			assertEquals("댓글 내용 비교", boardReplyReq.getContents(),
					boardDetailRes.getContents());
			assertEquals("댓글 작성자 아이디 비교", boardReplyReq.getRequestedUserID(),
					boardDetailRes.getFirstWriterID());
			
			assertEquals("댓글 첨부 파일 갯수 비교",
					boardReplyReq.getNewAttachedFileCnt(),
					boardDetailRes.getAttachedFileCnt());
			assertEquals("댓글 추천수 비교", 0, boardDetailRes.getVotes());
			assertEquals("댓글 조회갯수 비교", 1, boardDetailRes.getViewCount());

			assertEquals("댓글 그룹 최상위 번호 비교", boardWriteRes.getBoardNo(),
					boardDetailRes.getGroupNo());
			assertEquals("댓글 그룹 시퀀스번호 비교", 0,
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
	public void 댓글등록_접근권한이일반회원_손님() {
		String requestedUserIDForAdmin = "admin";
		String requestedUserIDForGuest = "guest";
		
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
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.GUEST.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.MEMBER.getValue());
		
		BoardInfoAddRes boardInfoAddRes = null;
		
		try {
			boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardReplyReq boardReplyReq = new BoardReplyReq();
		boardReplyReq.setRequestedUserID(requestedUserIDForGuest);
		boardReplyReq.setIp("127.0.0.1");
		boardReplyReq.setBoardID(boardInfoAddRes.getBoardID());
		boardReplyReq.setParentBoardNo(1);
		boardReplyReq.setSubject("하하호호");
		boardReplyReq.setContents("내용::그림01-1하나를 그리다");		

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

		try {
			boardReplyReqServerTask.doWork(TEST_DBCP_NAME, boardReplyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "게시판 댓글 등록 서비스는 로그인 해야만 이용할 수 있습니다";

			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 댓글등록_접근권한이일반회원_일반회원_OK() {
		String requestedUserIDForAdmin = "admin";
		String requestedUserIDForMember = "test01";
		
		
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
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.GUEST.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.MEMBER.getValue());
		
		BoardInfoAddRes boardInfoAddRes = null;
		
		try {
			boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMember);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardWriteReq.setSubject("테스트 주제01");
		boardWriteReq.setContents("내용::그림01 하나를 그리다");
		

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

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
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
		boardReplyReq.setRequestedUserID(requestedUserIDForMember);
		boardReplyReq.setIp("127.0.0.1");
		boardReplyReq.setBoardID(boardWriteRes.getBoardID());
		boardReplyReq.setParentBoardNo(boardWriteRes.getBoardNo());
		boardReplyReq.setSubject("하하호호");
		boardReplyReq.setContents("내용::그림01-1하나를 그리다");		

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
			boardReplyRes = boardReplyReqServerTask.doWork(TEST_DBCP_NAME, boardReplyReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		
		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setRequestedUserID(boardReplyReq.getRequestedUserID());
		boardDetailReq.setBoardID(boardReplyRes.getBoardID());
		boardDetailReq.setBoardNo(boardReplyRes.getBoardNo());
		

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
			assertEquals("댓글 내용 비교", boardReplyReq.getContents(),
					boardDetailRes.getContents());
			assertEquals("댓글 작성자 아이디 비교", boardReplyReq.getRequestedUserID(),
					boardDetailRes.getFirstWriterID());
			
			assertEquals("댓글 첨부 파일 갯수 비교",
					boardReplyReq.getNewAttachedFileCnt(),
					boardDetailRes.getAttachedFileCnt());
			assertEquals("댓글 추천수 비교", 0, boardDetailRes.getVotes());
			assertEquals("댓글 조회갯수 비교", 1, boardDetailRes.getViewCount());

			assertEquals("댓글 그룹 최상위 번호 비교", boardWriteRes.getBoardNo(),
					boardDetailRes.getGroupNo());
			assertEquals("댓글 그룹 시퀀스번호 비교", 0,
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
	public void 댓글등록_접근권한이일반회원_관리자_OK() {
		String requestedUserIDForAdmin = "admin";
		String requestedUserIDForMember = "test01";
		
		
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
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.GUEST.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.MEMBER.getValue());
		
		BoardInfoAddRes boardInfoAddRes = null;
		
		try {
			boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMember);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardWriteReq.setSubject("테스트 주제01");
		boardWriteReq.setContents("내용::그림01 하나를 그리다");
		

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

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
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
		boardReplyReq.setRequestedUserID(requestedUserIDForAdmin);
		boardReplyReq.setIp("127.0.0.1");
		boardReplyReq.setBoardID(boardWriteRes.getBoardID());
		boardReplyReq.setParentBoardNo(boardWriteRes.getBoardNo());
		boardReplyReq.setSubject("하하호호");
		boardReplyReq.setContents("내용::그림01-1하나를 그리다");		

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
			boardReplyRes = boardReplyReqServerTask.doWork(TEST_DBCP_NAME, boardReplyReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		
		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setRequestedUserID(boardReplyReq.getRequestedUserID());
		boardDetailReq.setBoardID(boardReplyRes.getBoardID());
		boardDetailReq.setBoardNo(boardReplyRes.getBoardNo());
		

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
			assertEquals("댓글 내용 비교", boardReplyReq.getContents(),
					boardDetailRes.getContents());
			assertEquals("댓글 작성자 아이디 비교", boardReplyReq.getRequestedUserID(),
					boardDetailRes.getFirstWriterID());
			
			assertEquals("댓글 첨부 파일 갯수 비교",
					boardReplyReq.getNewAttachedFileCnt(),
					boardDetailRes.getAttachedFileCnt());
			assertEquals("댓글 추천수 비교", 0, boardDetailRes.getVotes());
			assertEquals("댓글 조회갯수 비교", 1, boardDetailRes.getViewCount());

			assertEquals("댓글 그룹 최상위 번호 비교", boardWriteRes.getBoardNo(),
					boardDetailRes.getGroupNo());
			assertEquals("댓글 그룹 시퀀스번호 비교", 0,
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
	public void 댓글등록_접근권한이손님_손님_비밀번호미입력() {
		String requestedUserIDForAdmin = "admin";
		String requestedUserIDForGuest = "guest";
		
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
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.GUEST.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.GUEST.getValue());
		
		BoardInfoAddRes boardInfoAddRes = null;
		
		try {
			boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardReplyReq boardReplyReq = new BoardReplyReq();
		boardReplyReq.setRequestedUserID(requestedUserIDForGuest);
		boardReplyReq.setIp("127.0.0.1");
		boardReplyReq.setBoardID(boardInfoAddRes.getBoardID());
		boardReplyReq.setParentBoardNo(1);
		boardReplyReq.setSubject("하하호호");
		boardReplyReq.setContents("내용::그림01-1하나를 그리다");		

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

		try {
			boardReplyReqServerTask.doWork(TEST_DBCP_NAME, boardReplyReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "손님의 경우 반듯이 게시글에 대한 비밀번호를 입력해야 합니다";

			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	
	@Test
	public void 댓글등록_접근권한이손님_손님_OK() {
		String requestedUserIDForAdmin = "admin";
		String requestedUserIDForMember = "test01";
		String requestedUserIDForGuest = "guest";
		
		byte[] boardPasswrdBytes = {'t', 'e', 's', 't', '1', '2', '3', '4', '%'};
		
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(ServerCommonStaticFinalVars.BOARD_PASSWORD_HASH_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = "fail to get a MessageDigest class instance";
			log.warn(errorMessage, e);			
			
			fail(errorMessage);
		}
		
		md.update(boardPasswrdBytes);
		String boardPwdHashBase64 = CommonStaticUtil.Base64Encoder.encodeToString(md.digest());
		
		
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
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.GUEST.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.GUEST.getValue());
		
		BoardInfoAddRes boardInfoAddRes = null;
		
		try {
			boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMember);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardWriteReq.setSubject("테스트 주제01");
		boardWriteReq.setContents("내용::그림01 하나를 그리다");
		

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

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
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
		boardReplyReq.setRequestedUserID(requestedUserIDForGuest);
		boardReplyReq.setIp("127.0.0.1");
		boardReplyReq.setBoardID(boardWriteRes.getBoardID());
		boardReplyReq.setParentBoardNo(boardWriteRes.getBoardNo());
		boardReplyReq.setSubject("하하호호");
		boardReplyReq.setContents("내용::그림01-1하나를 그리다");	
		boardReplyReq.setPwdHashBase64(boardPwdHashBase64);

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
			boardReplyRes = boardReplyReqServerTask.doWork(TEST_DBCP_NAME, boardReplyReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		
		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setRequestedUserID(boardReplyReq.getRequestedUserID());
		boardDetailReq.setBoardID(boardReplyRes.getBoardID());
		boardDetailReq.setBoardNo(boardReplyRes.getBoardNo());
		

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
			assertEquals("댓글 내용 비교", boardReplyReq.getContents(),
					boardDetailRes.getContents());
			assertEquals("댓글 작성자 아이디 비교", boardReplyReq.getRequestedUserID(),
					boardDetailRes.getFirstWriterID());
			
			assertEquals("댓글 첨부 파일 갯수 비교",
					boardReplyReq.getNewAttachedFileCnt(),
					boardDetailRes.getAttachedFileCnt());
			assertEquals("댓글 추천수 비교", 0, boardDetailRes.getVotes());
			assertEquals("댓글 조회갯수 비교", 1, boardDetailRes.getViewCount());

			assertEquals("댓글 그룹 최상위 번호 비교", boardWriteRes.getBoardNo(),
					boardDetailRes.getGroupNo());
			assertEquals("댓글 그룹 시퀀스번호 비교", 0,
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
	public void 댓글등록_접근권한이손님_일반회원_OK() {
		String requestedUserIDForAdmin = "admin";
		String requestedUserIDForMember = "test01";
		
		byte[] boardPasswrdBytes = {'t', 'e', 's', 't', '1', '2', '3', '4', '%'};
		
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(ServerCommonStaticFinalVars.BOARD_PASSWORD_HASH_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = "fail to get a MessageDigest class instance";
			log.warn(errorMessage, e);			
			
			fail(errorMessage);
		}
		
		md.update(boardPasswrdBytes);
		String boardPwdHashBase64 = CommonStaticUtil.Base64Encoder.encodeToString(md.digest());
		
		
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
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.GUEST.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.GUEST.getValue());
		
		BoardInfoAddRes boardInfoAddRes = null;
		
		try {
			boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMember);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardWriteReq.setSubject("테스트 주제01");
		boardWriteReq.setContents("내용::그림01 하나를 그리다");
		

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

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
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
		boardReplyReq.setRequestedUserID(requestedUserIDForMember);
		boardReplyReq.setIp("127.0.0.1");
		boardReplyReq.setBoardID(boardWriteRes.getBoardID());
		boardReplyReq.setParentBoardNo(boardWriteRes.getBoardNo());
		boardReplyReq.setSubject("하하호호");
		boardReplyReq.setContents("내용::그림01-1하나를 그리다");	
		boardReplyReq.setPwdHashBase64(boardPwdHashBase64);

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
			boardReplyRes = boardReplyReqServerTask.doWork(TEST_DBCP_NAME, boardReplyReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		
		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setRequestedUserID(boardReplyReq.getRequestedUserID());
		boardDetailReq.setBoardID(boardReplyRes.getBoardID());
		boardDetailReq.setBoardNo(boardReplyRes.getBoardNo());
		

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
			assertEquals("댓글 내용 비교", boardReplyReq.getContents(),
					boardDetailRes.getContents());
			assertEquals("댓글 작성자 아이디 비교", boardReplyReq.getRequestedUserID(),
					boardDetailRes.getFirstWriterID());
			
			assertEquals("댓글 첨부 파일 갯수 비교",
					boardReplyReq.getNewAttachedFileCnt(),
					boardDetailRes.getAttachedFileCnt());
			assertEquals("댓글 추천수 비교", 0, boardDetailRes.getVotes());
			assertEquals("댓글 조회갯수 비교", 1, boardDetailRes.getViewCount());

			assertEquals("댓글 그룹 최상위 번호 비교", boardWriteRes.getBoardNo(),
					boardDetailRes.getGroupNo());
			assertEquals("댓글 그룹 시퀀스번호 비교", 0,
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
	public void 댓글등록_접근권한이손님_관리자_OK() {
		String requestedUserIDForAdmin = "admin";
		String requestedUserIDForMember = "test01";
		
		byte[] boardPasswrdBytes = {'t', 'e', 's', 't', '1', '2', '3', '4', '%'};
		
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(ServerCommonStaticFinalVars.BOARD_PASSWORD_HASH_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = "fail to get a MessageDigest class instance";
			log.warn(errorMessage, e);			
			
			fail(errorMessage);
		}
		
		md.update(boardPasswrdBytes);
		String boardPwdHashBase64 = CommonStaticUtil.Base64Encoder.encodeToString(md.digest());
		
		
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
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.GUEST.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.GUEST.getValue());
		
		BoardInfoAddRes boardInfoAddRes = null;
		
		try {
			boardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMember);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardWriteReq.setSubject("테스트 주제01");
		boardWriteReq.setContents("내용::그림01 하나를 그리다");
		

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

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
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
		boardReplyReq.setRequestedUserID(requestedUserIDForAdmin);
		boardReplyReq.setIp("127.0.0.1");
		boardReplyReq.setBoardID(boardWriteRes.getBoardID());
		boardReplyReq.setParentBoardNo(boardWriteRes.getBoardNo());
		boardReplyReq.setSubject("하하호호");
		boardReplyReq.setContents("내용::그림01-1하나를 그리다");	
		boardReplyReq.setPwdHashBase64(boardPwdHashBase64);

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
			boardReplyRes = boardReplyReqServerTask.doWork(TEST_DBCP_NAME, boardReplyReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		
		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setRequestedUserID(boardReplyReq.getRequestedUserID());
		boardDetailReq.setBoardID(boardReplyRes.getBoardID());
		boardDetailReq.setBoardNo(boardReplyRes.getBoardNo());
		

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
			assertEquals("댓글 내용 비교", boardReplyReq.getContents(),
					boardDetailRes.getContents());
			assertEquals("댓글 작성자 아이디 비교", boardReplyReq.getRequestedUserID(),
					boardDetailRes.getFirstWriterID());
			
			assertEquals("댓글 첨부 파일 갯수 비교",
					boardReplyReq.getNewAttachedFileCnt(),
					boardDetailRes.getAttachedFileCnt());
			assertEquals("댓글 추천수 비교", 0, boardDetailRes.getVotes());
			assertEquals("댓글 조회갯수 비교", 1, boardDetailRes.getViewCount());

			assertEquals("댓글 그룹 최상위 번호 비교", boardWriteRes.getBoardNo(),
					boardDetailRes.getGroupNo());
			assertEquals("댓글 그룹 시퀀스번호 비교", 0,
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
	public void 댓글등록_부모없음() {
		String otherID = "test02";
		final short boardID = 3;

		BoardReplyReq boardReplyReq = new BoardReplyReq();
		boardReplyReq.setRequestedUserID(otherID);
		boardReplyReq.setIp("127.0.0.1");
		boardReplyReq.setBoardID(boardID);
		boardReplyReq.setParentBoardNo(1);
		boardReplyReq.setSubject("테스트 주제01-1");
		boardReplyReq.setContents("내용::그림01-1하나를 그리다");
		

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

			String expectedErrorMessage = new StringBuilder().append("해당 게시글[boardID=")
					.append(boardReplyReq.getBoardID())
					.append(", boardNo=")
					.append(boardReplyReq.getParentBoardNo())
					.append("]이 존재 하지 않습니다").toString();

			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 댓글등록_정상_최초() {
		String writerID = "test01";
		String otherID = "test02";
		final short boardID = 3;

		BoardWriteRes boardWriteRes = null;
		{

			
			BoardWriteReq boardWriteReq = new BoardWriteReq();
			boardWriteReq.setRequestedUserID(writerID);
			boardWriteReq.setIp("172.16.0.1");
			boardWriteReq.setBoardID(boardID);
			boardWriteReq.setSubject("테스트 주제01");
			boardWriteReq.setContents("내용::그림01 하나를 그리다");
			

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

			BoardWriteReqServerTask boardWriteReqServerTask = null;
			try {
				boardWriteReqServerTask = new BoardWriteReqServerTask();
			} catch (DynamicClassCallException e1) {
				fail("dead code");
			}
			
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
		boardReplyReq.setRequestedUserID(otherID);
		boardReplyReq.setIp("127.0.0.1");
		boardReplyReq.setBoardID(boardWriteRes.getBoardID());
		boardReplyReq.setParentBoardNo(boardWriteRes.getBoardNo());
		boardReplyReq.setSubject("테스트 주제01-1");
		boardReplyReq.setContents("내용::그림01-1하나를 그리다");
		

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
		boardDetailReq.setRequestedUserID(boardReplyReq.getRequestedUserID());
		boardDetailReq.setBoardID(boardReplyRes.getBoardID());
		boardDetailReq.setBoardNo(boardReplyRes.getBoardNo());
		

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
			assertEquals("댓글 내용 비교", boardReplyReq.getContents(),
					boardDetailRes.getContents());
			assertEquals("댓글 작성자 아이디 비교", boardReplyReq.getRequestedUserID(),
					boardDetailRes.getFirstWriterID());
			
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
		boardReplyReq.setContents("내용::루트1_자식1_자식1_자식2");
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
		boardListReq.setBoardID(boardID);
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

}
