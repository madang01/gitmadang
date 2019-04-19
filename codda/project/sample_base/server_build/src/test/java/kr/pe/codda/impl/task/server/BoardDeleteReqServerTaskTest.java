package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.junit.Test;

import junitlib.AbstractBoardTest;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.message.BoardBlockReq.BoardBlockReq;
import kr.pe.codda.impl.message.BoardDeleteReq.BoardDeleteReq;
import kr.pe.codda.impl.message.BoardDetailReq.BoardDetailReq;
import kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes;
import kr.pe.codda.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.codda.impl.message.BoardReplyRes.BoardReplyRes;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public class BoardDeleteReqServerTaskTest extends AbstractBoardTest {
	
	// ServerCommonStaticFinalVars.LOAD_TEST_DBCP_NAME;
	// ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;
	private final static short boardID = 3;

	@Test
	public void 게시글삭제_잘못된게시판식별자() {
		String requestedUserIDForMember = "test01";
		short badBoardID = 7;
		
		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(requestedUserIDForMember);
		boardDeleteReq.setBoardID(badBoardID);
		boardDeleteReq.setBoardNo(1);
		boardDeleteReq.setIp("172.3.14.112");
	
		try {
			boardDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardDeleteReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
	
			String expectedErrorMessage = new StringBuilder("입력 받은 게시판 식별자[").append(badBoardID)
					.append("]가 게시판 정보 테이블에 존재하지  않습니다").toString();
	
			assertEquals("잘못된 게시판 식별자 검사",
					expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글삭제_대상글없음() {
		String requestedUserIDForMember = "test01";
	
		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(requestedUserIDForMember);
		boardDeleteReq.setBoardID(boardID);
		boardDeleteReq.setBoardNo(1);
		boardDeleteReq.setIp("172.1.14.5");
	
		try {
			boardDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardDeleteReq);
	
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
	
			String expectedErrorMessage = new StringBuilder()
					.append("해당 게시글[boardID=")
					.append(boardID)
					.append(", boardNo=")
					.append(1)
					.append("]이 존재 하지 않습니다").toString();
	
			assertEquals("삭제 대상 글이 없을때의  경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글삭제_이미삭제된글() {
		String requestedUserIDForMember = "test01";
	
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
	
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMember);
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("삭제::제목");
		boardWriteReq.setContents("삭제::내용");
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
		boardDeleteReq.setRequestedUserID(requestedUserIDForMember);
		boardDeleteReq.setIp("172.16.0.15");
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
	
		boardDeleteReq.setRequestedUserID(requestedUserIDForMember);
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
	public void 게시글삭제_차단된글() {
		String requestedUserIDForMember = "test01";
		String requestedUserIDForAdmin = "admin";
	
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
	
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMember);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContents("차단::내용");
		
	
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
		boardBlockReq.setRequestedUserID(requestedUserIDForAdmin);
		boardBlockReq.setIp("172.4.0.11");
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
		boardDeleteReq.setRequestedUserID(requestedUserIDForMember);
		boardDeleteReq.setIp("172.4.0.31");
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
	public void 게시글삭제_차단된글에속한글삭제() {
		String requestedUserIDForMember = "test01";
		String requestedUserIDForAdmin = "admin";
		
	
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
	
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMember);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContents("차단::내용");
		boardWriteReq.setPwdHashBase64("");
		
	
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
		
		BoardReplyReq boardReplyReq = new BoardReplyReq();
		boardReplyReq.setRequestedUserID(requestedUserIDForMember);
		boardReplyReq.setIp("127.0.0.1");
		boardReplyReq.setBoardID(boardWriteRes.getBoardID());
		boardReplyReq.setParentBoardNo(boardWriteRes.getBoardNo());
		boardReplyReq.setSubject("테스트 주제01-1");
		boardReplyReq.setContents("내용::그림01-1하나를 그리다");
		boardReplyReq.setPwdHashBase64("");		
		
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
		
	
		BoardBlockReqServerTask boardBlockReqServerTask = null;
		try {
			boardBlockReqServerTask = new BoardBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardBlockReq boardBlockReq = new BoardBlockReq();
		boardBlockReq.setRequestedUserID(requestedUserIDForAdmin);
		boardBlockReq.setIp("172.4.0.11");
		boardBlockReq.setBoardID(boardWriteRes.getBoardID());
		boardBlockReq.setBoardNo(boardWriteRes.getBoardNo());
		
	
		try {
			MessageResultRes messageResultRes = boardBlockReqServerTask.doWork(
					TEST_DBCP_NAME, boardBlockReq);
			log.info(messageResultRes.toString());		
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
		boardDeleteReq.setRequestedUserID(requestedUserIDForMember);
		boardDeleteReq.setIp("172.4.0.31");
		boardDeleteReq.setBoardID(boardReplyRes.getBoardID());
		boardDeleteReq.setBoardNo(boardReplyRes.getBoardNo());
	
		try {
			boardDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardDeleteReq);
	
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
	
			String expectedErrorMessage = "해당 게시글은 관리자에 의해 차단된 글에 속한 글입니다";
	
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	/**
	 * 관리자라도 타인글은 삭제 할 수 없음, 삭제는 오직 본인글만 가능함.
	 */
	@Test
	public void 게시글삭제_본인글아님_관리자() {
		String requestedUserIDForMember = "test01";
		String requestedUserIDForAdmin = "admin";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMember);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("삭제::타인::제목");
		boardWriteReq.setContents("삭제::타인::내용");		

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
		boardDeleteReq.setRequestedUserID(requestedUserIDForAdmin);
		boardDeleteReq.setIp("172.16.0.41");
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
	public void 게시글삭제_본인글아님_일반사용자() {
		String requestedUserIDForMember = "test01";
		String requestedUserIDForOtherMember = "test02";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMember);
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("삭제::타인::제목");
		boardWriteReq.setContents("삭제::타인::내용");
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
		boardDeleteReq.setRequestedUserID(requestedUserIDForOtherMember);
		boardDeleteReq.setBoardID(deleteSateBoardWriteRes.getBoardID());
		boardDeleteReq.setBoardNo(deleteSateBoardWriteRes.getBoardNo());
		boardDeleteReq.setIp("172.1.3.15");

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
	public void 게시글삭제_손님이작성한비밀번호설정된게시글_db에설정된비밀번호값이null인경우() {
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
	
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForGuest);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContents("차단::내용");
		boardWriteReq.setPwdHashBase64(boardPwdHashBase64);
		
	
		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();
	
		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);
		
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
	
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
		
		final UByte boardID = UByte.valueOf(boardWriteRes.getBoardID());
		final UInteger boardNo = UInteger.valueOf(boardWriteRes.getBoardNo());		
		final String newBoardPwdBase64 = null;
		
		try {
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				
				create.update(SB_BOARD_TB)
				.set(SB_BOARD_TB.PWD_BASE64, newBoardPwdBase64)
				.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
				.and(SB_BOARD_TB.BOARD_NO.eq(boardNo))
				.execute();	
				
				conn.commit();
				
			});
		} catch (Exception e) {
			log.warn("unknwon error", e);
 			fail("알수 없는 에러로 테스트 실패");
		}
		
		
		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(requestedUserIDForGuest);
		boardDeleteReq.setIp("172.1.3.16");
		boardDeleteReq.setBoardID(boardWriteRes.getBoardID());
		boardDeleteReq.setBoardNo(boardWriteRes.getBoardNo());
		boardDeleteReq.setPwdHashBase64("");
	
		try {
			boardDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardDeleteReq);
	
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
	
			String expectedErrorMessage = "손님으로 작성한 게시글의 비밀번호가 없습니다";
	
			assertEquals("손님으로 작성한 비밀번호가 설정된 게시글인데 DB에 설정된 게시글 비밀번호 값이  null 일때 경고 메시지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 게시글삭제_비밀번호설정된게시글_비밀번호미입력_null() {
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
	
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForGuest);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContents("차단::내용");
		boardWriteReq.setPwdHashBase64(boardPwdHashBase64);
		
	
		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();
	
		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);
		
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
	
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
		
	
		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(requestedUserIDForGuest);
		boardDeleteReq.setIp("172.1.3.16");
		boardDeleteReq.setBoardID(boardWriteRes.getBoardID());
		boardDeleteReq.setBoardNo(boardWriteRes.getBoardNo());
		boardDeleteReq.setPwdHashBase64(null);
	
		try {
			boardDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardDeleteReq);
	
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
	
			String expectedErrorMessage = "게시글 비밀번호를 입력해 주세요";
	
			assertEquals("작성한 비밀번호가 설정된 게시글에 비밀번호 입력이 없다는 뜻의 null 값으로 삭제 요청할때의 경고 메시지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
	}
	
	@Test
	public void 게시글삭제_비밀번호설정된게시글_비밀번호미입력_empty() {
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
	
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForGuest);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContents("차단::내용");
		boardWriteReq.setPwdHashBase64(boardPwdHashBase64);
		
	
		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();
	
		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);
		
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
	
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
		
	
		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(requestedUserIDForGuest);
		boardDeleteReq.setIp("172.1.3.16");
		boardDeleteReq.setBoardID(boardWriteRes.getBoardID());
		boardDeleteReq.setBoardNo(boardWriteRes.getBoardNo());
		boardDeleteReq.setPwdHashBase64("");
	
		try {
			boardDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardDeleteReq);
	
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
	
			String expectedErrorMessage = "게시글 비밀번호를 입력해 주세요";
	
			assertEquals("작성한 비밀번호가 설정된 게시글에 비밀번호 입력 없다는 뜻의 빈 문자열로 삭제 요청할때의 경고 메시지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
	}
	
	
	@Test
	public void 게시글삭제_비밀번호설정된게시글_게시글비밀번호불일치() {
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
		
		
		byte[] otherBoardPasswrdBytes = {'t', 'e', 's', 't', '1', '2', '3', '4', '$'};
		
		md.reset();
		md.update(otherBoardPasswrdBytes);
		String otherBoardPwdHashBase64 = CommonStaticUtil.Base64Encoder.encodeToString(md.digest());
	
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForGuest);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContents("차단::내용");
		boardWriteReq.setPwdHashBase64(boardPwdHashBase64);
		
	
		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();
	
		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);
		
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
	
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
		
	
		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(requestedUserIDForGuest);
		boardDeleteReq.setIp("172.1.3.16");
		boardDeleteReq.setBoardID(boardWriteRes.getBoardID());
		boardDeleteReq.setBoardNo(boardWriteRes.getBoardNo());
		boardDeleteReq.setPwdHashBase64(otherBoardPwdHashBase64);
	
		try {
			boardDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardDeleteReq);
	
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
	
			String expectedErrorMessage = "설정한 게시글 비밀 번호와 일치하지 않습니다";
	
			assertEquals("설정한 게시글 비밀번호와 다를 경우의 경고 메시지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 게시글삭제_비밀번호설정된게시글_OK() {
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
	
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForGuest);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContents("차단::내용");
		boardWriteReq.setPwdHashBase64(boardPwdHashBase64);
		
	
		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();
	
		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);
		
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
	
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
		
	
		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(requestedUserIDForGuest);
		boardDeleteReq.setIp("172.1.3.16");
		boardDeleteReq.setBoardID(boardWriteRes.getBoardID());
		boardDeleteReq.setBoardNo(boardWriteRes.getBoardNo());
		boardDeleteReq.setPwdHashBase64(boardPwdHashBase64);
	
		try {
			boardDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardDeleteReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setRequestedUserID(requestedUserIDForGuest);
		boardDetailReq.setBoardID(boardDeleteReq.getBoardID());
		boardDetailReq.setBoardNo(boardDeleteReq.getBoardNo());		
		
	
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
	public void 게시글삭제_비밀번호설정된게시글_작성자가관리자_비밀번호무시여부_OK() {
		String requestedUserIDForAdmin = "admin";
		
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
	
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForAdmin);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContents("차단::내용");
		boardWriteReq.setPwdHashBase64(boardPwdHashBase64);
		
	
		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();
	
		boardWriteReq.setNewAttachedFileCnt((short) attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);
		
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
	
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
		
	
		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(requestedUserIDForAdmin);
		boardDeleteReq.setIp("172.1.3.16");
		boardDeleteReq.setBoardID(boardWriteRes.getBoardID());
		boardDeleteReq.setBoardNo(boardWriteRes.getBoardNo());
		boardDeleteReq.setPwdHashBase64("aaaa");
	
		try {
			boardDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardDeleteReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		
		
		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setRequestedUserID("guest");
		boardDetailReq.setBoardID(boardDeleteReq.getBoardID());
		boardDetailReq.setBoardNo(boardDeleteReq.getBoardNo());		
		

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
	public void 게시글삭제_비밀번호없는게시글_OK() {
		String requestedUserIDForMember = "test01";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMember);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("삭제::제목");
		boardWriteReq.setContents("삭제::내용");
		

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
		boardDeleteReq.setRequestedUserID(requestedUserIDForMember);
		boardDeleteReq.setIp("172.1.3.19");
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
		boardDetailReq.setRequestedUserID(requestedUserIDForMember);
		boardDetailReq.setBoardID(deleteSateBoardWriteRes.getBoardID());
		boardDetailReq.setBoardNo(deleteSateBoardWriteRes.getBoardNo());		
		

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

}
