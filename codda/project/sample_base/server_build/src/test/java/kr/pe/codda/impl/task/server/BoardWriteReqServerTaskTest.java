package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
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
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.message.BoardDetailReq.BoardDetailReq;
import kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes;
import kr.pe.codda.impl.message.BoardInfoAddReq.BoardInfoAddReq;
import kr.pe.codda.impl.message.BoardInfoAddRes.BoardInfoAddRes;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.server.lib.BoardListType;
import kr.pe.codda.server.lib.BoardReplyPolicyType;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public class BoardWriteReqServerTaskTest extends AbstractBoardTest {

	@Test
	public void 본문글등록_잘못된게시판식별자() {
		final short badBoardID = 7;
		final String requestedUserIDForMember = "test01";
		
	
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}
	
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMember);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(badBoardID);
		boardWriteReq.setSubject("테스트 주제");
		boardWriteReq.setContents("내용::그림2 하나를 그리다");
		
	
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
	
		
		try {
			boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
	
			String expectedErrorMessage = new StringBuilder("입력 받은 게시판 식별자[").append(badBoardID)
					.append("]가 게시판 정보 테이블에 존재하지  않습니다").toString();
	
			assertEquals("본문글 작성시 잘못된 게시판 식별자일때 경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 본문글등록_최대갯수초과() {
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
		
		final UByte boardID = UByte.valueOf(boardInfoAddRes.getBoardID());
		
		try {			
			ServerDBUtil.execute(TEST_DBCP_NAME, (conn, create) -> {
				create.update(SB_BOARD_INFO_TB).set(SB_BOARD_INFO_TB.NEXT_BOARD_NO, UInteger.valueOf(CommonStaticFinalVars.UNSIGNED_INTEGER_MAX))
				.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID))
				.execute();
				
				conn.commit();
			});
		} catch(Exception e) {
			log.warn(e.getMessage(), e);

			fail(e.getMessage());
		}
		
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMember);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardWriteReq.setSubject("테스트 주제");
		boardWriteReq.setContents("내용::그림2 하나를 그리다");
		

		List<BoardWriteReq.NewAttachedFile> newAttachedFileListForWrite = new ArrayList<BoardWriteReq.NewAttachedFile>();
		boardWriteReq.setNewAttachedFileCnt((short) newAttachedFileListForWrite
				.size());
		boardWriteReq.setNewAttachedFileList(newAttachedFileListForWrite);

		
		try {
			boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "단위테스트용 게시판[4]은 최대 갯수까지 글이 등록되어 더 이상 글을 추가 할 수 없습니다";

			assertEquals("본문글 작성 요청자가 회원 테이블 미존재일 경우의 경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 본문글등록_회원테이블미존재아이디() {
		final short boardID = 1;
		final String requestedUserIDForNoMember = "aabbcc33";
		
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForNoMember);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("테스트 주제");
		boardWriteReq.setContents("내용::그림2 하나를 그리다");
		

		List<BoardWriteReq.NewAttachedFile> newAttachedFileListForWrite = new ArrayList<BoardWriteReq.NewAttachedFile>();
		boardWriteReq.setNewAttachedFileCnt((short) newAttachedFileListForWrite
				.size());
		boardWriteReq.setNewAttachedFileList(newAttachedFileListForWrite);

		
		try {
			boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = new StringBuilder("서비스 요청자[").append(requestedUserIDForNoMember).append("]가 회원 테이블에 존재하지 않습니다")
					.toString();;

			assertEquals("본문글 작성 요청자가 회원 테이블 미존재일 경우의 경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 본문글등록_관리자이상접근권한_손님() {
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
		boardInfoAddReq.setBoardReplyPolicyType(BoardReplyPolicyType.NO_REPLY.getValue());
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.ADMIN.getValue());
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
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForGuest);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardWriteReq.setSubject("테스트 주제");
		boardWriteReq.setContents("내용::그림2 하나를 그리다");
		

		List<BoardWriteReq.NewAttachedFile> newAttachedFileListForWrite = new ArrayList<BoardWriteReq.NewAttachedFile>();
		boardWriteReq.setNewAttachedFileCnt((short) newAttachedFileListForWrite
				.size());
		boardWriteReq.setNewAttachedFileList(newAttachedFileListForWrite);

		
		try {
			boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "게시판 본문 글 등록 서비스는 관리자 전용 서비스입니다";

			assertEquals("관리자 이상 접근 권한을 갖는 게시판의 본문글 작성 서비스에 손님으로 접근할때 경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 본문글등록_관리자이상접근권한_일반회원() {
		String requestedUserIDForAdmin = "admin";
		String requestedUserIDForMmebrer = "test01";
		
		
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
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.ADMIN.getValue());
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
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMmebrer);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardWriteReq.setSubject("테스트 주제");
		boardWriteReq.setContents("내용::그림2 하나를 그리다");
		

		List<BoardWriteReq.NewAttachedFile> newAttachedFileListForWrite = new ArrayList<BoardWriteReq.NewAttachedFile>();
		boardWriteReq.setNewAttachedFileCnt((short) newAttachedFileListForWrite
				.size());
		boardWriteReq.setNewAttachedFileList(newAttachedFileListForWrite);

		
		try {
			boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "게시판 본문 글 등록 서비스는 관리자 전용 서비스입니다";

			assertEquals("관리자 이상 접근 권한을 갖는 게시판의 본문글 작성 서비스에 손님으로 접근할때 경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	
	@Test
	public void 본문글등록_관리자이상접근권한_관리자_정상() {
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
		boardInfoAddReq.setBoardReplyPolicyType(BoardReplyPolicyType.NO_REPLY.getValue());
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.ADMIN.getValue());
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
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForAdmin);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardWriteReq.setSubject("테스트 주제");
		boardWriteReq.setContents("내용::그림2 하나를 그리다");
		

		List<BoardWriteReq.NewAttachedFile> newAttachedFileListForWrite = new ArrayList<BoardWriteReq.NewAttachedFile>();
		boardWriteReq.setNewAttachedFileCnt((short) newAttachedFileListForWrite
				.size());
		boardWriteReq.setNewAttachedFileList(newAttachedFileListForWrite);

		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setRequestedUserID(requestedUserIDForGuest);
		boardDetailReq.setBoardID(boardWriteRes.getBoardID());
		boardDetailReq.setBoardNo(boardWriteRes.getBoardNo());		
		

		BoardDetailReqServerTask boardDetailReqServerTask = null;
		try {
			boardDetailReqServerTask = new BoardDetailReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			BoardDetailRes boardDetailRes = boardDetailReqServerTask.doWork(
					TEST_DBCP_NAME, boardDetailReq);
			
			log.info(boardDetailRes.toString());

			assertEquals("제목 비교", boardWriteReq.getSubject(),
					boardDetailRes.getSubject());
			assertEquals("내용 비교", boardWriteReq.getContents(),
					boardDetailRes.getContents());
			assertEquals("작성자 아이디 비교", boardWriteReq.getRequestedUserID(),
					boardDetailRes.getFirstWriterID());
			
			assertEquals("최종 수정자 아이디 비교", boardWriteReq.getRequestedUserID(),
					boardDetailRes.getLastModifierID());			
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
	}
	
	@Test
	public void 본문글등록_일반회원이상접근권한_손님() {
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
		boardInfoAddReq.setBoardReplyPolicyType(BoardReplyPolicyType.NO_REPLY.getValue());
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
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForGuest);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardWriteReq.setSubject("테스트 주제");
		boardWriteReq.setContents("내용::그림2 하나를 그리다");
		

		List<BoardWriteReq.NewAttachedFile> newAttachedFileListForWrite = new ArrayList<BoardWriteReq.NewAttachedFile>();
		boardWriteReq.setNewAttachedFileCnt((short) newAttachedFileListForWrite
				.size());
		boardWriteReq.setNewAttachedFileList(newAttachedFileListForWrite);

		
		try {
			boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "게시판 본문 글 등록 서비스는 로그인 해야만 이용할 수 있습니다";

			assertEquals("관리자 이상 접근 권한을 갖는 게시판의 본문글 작성 서비스에 손님으로 접근할때 경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 본문글등록_일반회원이상접근권한_일반회원_정상() {
		String requestedUserIDForAdmin = "admin";
		String requestedUserIDForMmebrer = "test01";
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
		boardInfoAddReq.setBoardReplyPolicyType(BoardReplyPolicyType.NO_REPLY.getValue());
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
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMmebrer);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardWriteReq.setSubject("테스트 주제");
		boardWriteReq.setContents("내용::그림2 하나를 그리다");
		

		List<BoardWriteReq.NewAttachedFile> newAttachedFileListForWrite = new ArrayList<BoardWriteReq.NewAttachedFile>();
		boardWriteReq.setNewAttachedFileCnt((short) newAttachedFileListForWrite
				.size());
		boardWriteReq.setNewAttachedFileList(newAttachedFileListForWrite);

		
		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setRequestedUserID(requestedUserIDForGuest);
		boardDetailReq.setBoardID(boardWriteRes.getBoardID());
		boardDetailReq.setBoardNo(boardWriteRes.getBoardNo());		
		

		BoardDetailReqServerTask boardDetailReqServerTask = null;
		try {
			boardDetailReqServerTask = new BoardDetailReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			BoardDetailRes boardDetailRes = boardDetailReqServerTask.doWork(
					TEST_DBCP_NAME, boardDetailReq);
			
			log.info(boardDetailRes.toString());

			assertEquals("제목 비교", boardWriteReq.getSubject(),
					boardDetailRes.getSubject());
			assertEquals("내용 비교", boardWriteReq.getContents(),
					boardDetailRes.getContents());
			assertEquals("작성자 아이디 비교", boardWriteReq.getRequestedUserID(),
					boardDetailRes.getFirstWriterID());
			
			assertEquals("최종 수정자 아이디 비교", boardWriteReq.getRequestedUserID(),
					boardDetailRes.getLastModifierID());			
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
	}
	
	@Test
	public void 본문글등록_일반회원이상접근권한_관리자_정상() {
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
		boardInfoAddReq.setBoardReplyPolicyType(BoardReplyPolicyType.NO_REPLY.getValue());
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
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForAdmin);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardWriteReq.setSubject("테스트 주제");
		boardWriteReq.setContents("내용::그림2 하나를 그리다");
		

		List<BoardWriteReq.NewAttachedFile> newAttachedFileListForWrite = new ArrayList<BoardWriteReq.NewAttachedFile>();
		boardWriteReq.setNewAttachedFileCnt((short) newAttachedFileListForWrite
				.size());
		boardWriteReq.setNewAttachedFileList(newAttachedFileListForWrite);

		
		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setRequestedUserID(requestedUserIDForGuest);
		boardDetailReq.setBoardID(boardWriteRes.getBoardID());
		boardDetailReq.setBoardNo(boardWriteRes.getBoardNo());		
		

		BoardDetailReqServerTask boardDetailReqServerTask = null;
		try {
			boardDetailReqServerTask = new BoardDetailReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			BoardDetailRes boardDetailRes = boardDetailReqServerTask.doWork(
					TEST_DBCP_NAME, boardDetailReq);
			
			log.info(boardDetailRes.toString());

			assertEquals("제목 비교", boardWriteReq.getSubject(),
					boardDetailRes.getSubject());
			assertEquals("내용 비교", boardWriteReq.getContents(),
					boardDetailRes.getContents());
			assertEquals("작성자 아이디 비교", boardWriteReq.getRequestedUserID(),
					boardDetailRes.getFirstWriterID());
			
			assertEquals("최종 수정자 아이디 비교", boardWriteReq.getRequestedUserID(),
					boardDetailRes.getLastModifierID());			
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
	}
	
	
	@Test
	public void 본문글등록_손님이상접근권한_손님_게시글비밀번호미설정() {
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
		

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForGuest);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardWriteReq.setSubject("테스트 주제");
		boardWriteReq.setContents("내용::그림2 하나를 그리다");
		

		List<BoardWriteReq.NewAttachedFile> newAttachedFileListForWrite = new ArrayList<BoardWriteReq.NewAttachedFile>();
		boardWriteReq.setNewAttachedFileCnt((short) newAttachedFileListForWrite
				.size());
		boardWriteReq.setNewAttachedFileList(newAttachedFileListForWrite);

		
		try {
			boardWriteReqServerTask.doWork(TEST_DBCP_NAME,
					boardWriteReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "손님의 경우 반듯이 게시글에 대한 비밀번호를 입력해야 합니다";

			assertEquals("손님으로 접근할때 게시글 비밀번호 미 입력시 경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 본문글등록_손님이상접근권한_손님_정상() {
		String requestedUserIDForAdmin = "admin";
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
		

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForGuest);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardWriteReq.setSubject("테스트 주제");
		boardWriteReq.setContents("내용::그림2 하나를 그리다");
		boardWriteReq.setPwdHashBase64(boardPwdHashBase64);
		

		List<BoardWriteReq.NewAttachedFile> newAttachedFileListForWrite = new ArrayList<BoardWriteReq.NewAttachedFile>();
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
		boardDetailReq.setRequestedUserID(requestedUserIDForGuest);
		boardDetailReq.setBoardID(boardWriteRes.getBoardID());
		boardDetailReq.setBoardNo(boardWriteRes.getBoardNo());
		
		

		BoardDetailReqServerTask boardDetailReqServerTask = null;
		try {
			boardDetailReqServerTask = new BoardDetailReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			BoardDetailRes boardDetailRes = boardDetailReqServerTask.doWork(
					TEST_DBCP_NAME, boardDetailReq);
			
			log.info(boardDetailRes.toString());

			assertEquals("제목 비교", boardWriteReq.getSubject(),
					boardDetailRes.getSubject());
			assertEquals("내용 비교", boardWriteReq.getContents(),
					boardDetailRes.getContents());
			assertEquals("작성자 아이디 비교", boardWriteReq.getRequestedUserID(),
					boardDetailRes.getFirstWriterID());
			
			assertEquals("최종 수정자 아이디 비교", boardWriteReq.getRequestedUserID(),
					boardDetailRes.getLastModifierID());			
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
	}
	
	
	@Test
	public void 본문글등록_손님이상접근권한_일반회원_정상() {
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
		

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMember);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardWriteReq.setSubject("테스트 주제");
		boardWriteReq.setContents("내용::그림2 하나를 그리다");
		boardWriteReq.setPwdHashBase64(boardPwdHashBase64);
		

		List<BoardWriteReq.NewAttachedFile> newAttachedFileListForWrite = new ArrayList<BoardWriteReq.NewAttachedFile>();
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
		boardDetailReq.setRequestedUserID(requestedUserIDForGuest);
		boardDetailReq.setBoardID(boardWriteRes.getBoardID());
		boardDetailReq.setBoardNo(boardWriteRes.getBoardNo());		

		BoardDetailReqServerTask boardDetailReqServerTask = null;
		try {
			boardDetailReqServerTask = new BoardDetailReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			BoardDetailRes boardDetailRes = boardDetailReqServerTask.doWork(
					TEST_DBCP_NAME, boardDetailReq);
			
			log.info(boardDetailRes.toString());

			assertEquals("제목 비교", boardWriteReq.getSubject(),
					boardDetailRes.getSubject());
			assertEquals("내용 비교", boardWriteReq.getContents(),
					boardDetailRes.getContents());
			assertEquals("작성자 아이디 비교", boardWriteReq.getRequestedUserID(),
					boardDetailRes.getFirstWriterID());
			
			assertEquals("최종 수정자 아이디 비교", boardWriteReq.getRequestedUserID(),
					boardDetailRes.getLastModifierID());			
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
	}
	
	@Test
	public void 본문글등록_손님이상접근권한_관리자_정상() {
		String requestedUserIDForAdmin = "admin";
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
		

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForAdmin);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardWriteReq.setSubject("테스트 주제");
		boardWriteReq.setContents("내용::그림2 하나를 그리다");
		boardWriteReq.setPwdHashBase64(boardPwdHashBase64);
		

		List<BoardWriteReq.NewAttachedFile> newAttachedFileListForWrite = new ArrayList<BoardWriteReq.NewAttachedFile>();
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
		boardDetailReq.setRequestedUserID(requestedUserIDForGuest);
		boardDetailReq.setBoardID(boardWriteRes.getBoardID());
		boardDetailReq.setBoardNo(boardWriteRes.getBoardNo());
		

		BoardDetailReqServerTask boardDetailReqServerTask = null;
		try {
			boardDetailReqServerTask = new BoardDetailReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			BoardDetailRes boardDetailRes = boardDetailReqServerTask.doWork(
					TEST_DBCP_NAME, boardDetailReq);
			
			log.info(boardDetailRes.toString());

			assertEquals("제목 비교", boardWriteReq.getSubject(),
					boardDetailRes.getSubject());
			assertEquals("내용 비교", boardWriteReq.getContents(),
					boardDetailRes.getContents());
			assertEquals("작성자 아이디 비교", boardWriteReq.getRequestedUserID(),
					boardDetailRes.getFirstWriterID());
			
			assertEquals("최종 수정자 아이디 비교", boardWriteReq.getRequestedUserID(),
					boardDetailRes.getLastModifierID());			
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
	}
}
