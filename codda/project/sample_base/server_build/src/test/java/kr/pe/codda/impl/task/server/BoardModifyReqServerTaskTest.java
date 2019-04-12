package kr.pe.codda.impl.task.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import junitlib.AbstractBoardTest;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.message.BoardBlockReq.BoardBlockReq;
import kr.pe.codda.impl.message.BoardDeleteReq.BoardDeleteReq;
import kr.pe.codda.impl.message.BoardDetailReq.BoardDetailReq;
import kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes;
import kr.pe.codda.impl.message.BoardInfoAddReq.BoardInfoAddReq;
import kr.pe.codda.impl.message.BoardInfoAddRes.BoardInfoAddRes;
import kr.pe.codda.impl.message.BoardModifyReq.BoardModifyReq;
import kr.pe.codda.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.codda.impl.message.BoardReplyRes.BoardReplyRes;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.lib.BoardListType;
import kr.pe.codda.server.lib.BoardReplyPolicyType;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;

public class BoardModifyReqServerTaskTest extends AbstractBoardTest {

	@Test
	public void 게시글수정_잘못된게시판식별자() {
		String requestedUserIDForMember = "test01";
		short badBoardID = 7;
		
		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID(requestedUserIDForMember);
		boardModifyReq.setBoardID(badBoardID);
		boardModifyReq.setBoardNo(1);
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContents("내용::그림5-1하나를 그리다#1");		
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
			String expectedErrorMessage = new StringBuilder("입력 받은 게시판 식별자[").append(badBoardID)
					.append("]가 게시판 정보 테이블에 존재하지  않습니다").toString();

			assertEquals("첨부 파일 최대 등록 갯수 초과 검사", expectedErrorMessage,
					acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글수정_게시판목록유형이트리_제목없음() {
		String requestedUserIDForGuest = "guest";
		final short boardID = 3;
		
		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID(requestedUserIDForGuest);
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setBoardID(boardID);
		boardModifyReq.setBoardNo(1);
		boardModifyReq.setSubject("");
		boardModifyReq.setContents("내용::그림5-1하나를 그리다#1");
		boardModifyReq.setNextAttachedFileSeq((short) 1);
		
	
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
			String expectedErrorMessage = "게시글 제목을 입력해 주세요";
	
			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글수정_첨부파일최대등록갯수초과() {
		String requestedUserIDForMebmer = "test01";
		final short boardID = 3;
		
		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID(requestedUserIDForMebmer);
		boardModifyReq.setBoardID(boardID);
		boardModifyReq.setBoardNo(1);
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContents("내용::그림5-1하나를 그리다#1");
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
	public void 게시글수정_잘못된신규첨부파일이름() {
		final short boardID = 3;
		
		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setBoardID(boardID);
		boardModifyReq.setBoardNo(1);
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContents("내용::그림5-1하나를 그리다#1");
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
	public void 게시글수정_중복된보존을원하는구파일시퀀스() {
		final short boardID = 3;
		
		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setBoardID(boardID);
		boardModifyReq.setBoardNo(1);
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContents("내용::그림5-1하나를 그리다#1");
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
	public void 게시글수정_보존을원하는구파일시퀀스가다음시퀀스번호보다큰경우() {
		final short boardID = 3;
		
		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setBoardID(boardID);
		boardModifyReq.setBoardNo(1);
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContents("내용::그림5-1하나를 그리다#1");
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
	public void 게시글수정_존재하지않은게시글() {
		String requestedUserIDForMebmer = "test01";
		final short boardID = 3;		
		final long badBoardNo = 112;
		

		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID(requestedUserIDForMebmer);
		boardModifyReq.setBoardID(boardID);
		boardModifyReq.setBoardNo(badBoardNo);
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContents("내용::그림5-1하나를 그리다#1");
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
			String expectedErrorMessage = new StringBuilder()
					.append("해당 게시글[boardID=")
					.append(boardID)
					.append(", boardNo=")
					.append(badBoardNo)
					.append("]이 존재 하지 않습니다").toString();

			assertEquals("타인 수정시 에러 검사", expectedErrorMessage,
					acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 게시글수정_타인글() {
		final short boardID = 3;
		String requestedUserIDForMebmer = "test01";
		String requestedUserIDForOtherMebmer = "test02";
		
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMebmer);
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContents("차단::내용");
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
		boardModifyReq.setRequestedUserID(requestedUserIDForOtherMebmer);
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContents("내용::그림5-1하나를 그리다#1");
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
	public void 게시글수정_목록유형그룹_본문글에제목없음() {
		String requestedUserIDForAdmin = "admin";
		
		
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
		boardInfoAddReq.setBoardReplyPolicyType(BoardReplyPolicyType.ONLY_ROOT.getValue());
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.ADMIN.getValue());
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
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForAdmin);
		boardWriteReq.setBoardID(boardInfoAddRes.getBoardID());
		boardWriteReq.setSubject("원본::제목");
		boardWriteReq.setContents("원본::내용");
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
		boardModifyReq.setRequestedUserID(requestedUserIDForAdmin);
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("");
		boardModifyReq.setContents("수정::그림5-1하나를 그리다#1");
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
			String expectedErrorMessage = "게시글 제목을 입력해 주세요";

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글수정_게시글비정상상태_삭제() {
		final short boardID = 3;
		String requestedUserIDForMebmer = "test01";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMebmer);
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContents("차단::내용");
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
		boardDeleteReq.setRequestedUserID(requestedUserIDForMebmer);
		boardDeleteReq.setIp("172.16.0.7");
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
		boardModifyReq.setRequestedUserID(requestedUserIDForMebmer);
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContents("내용::그림5-1하나를 그리다#1");		
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
	public void 게시글수정_게시글비정상상태_차단() {
		String requestedUserIDForMebmer = "test01";
		final short boardID = 3;

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMebmer);
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContents("차단::내용");
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
		blcokBoardBlockReq.setIp("172.16.0.7");
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
		boardModifyReq.setRequestedUserID(requestedUserIDForMebmer);
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContents("내용::그림5-1하나를 그리다#1");		
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
	public void 게시글수정_게시글비정상상태_트리차단() {
		final short boardID = 3;
		String requestedUserIDForMebmer = "test01";
		String requestedUserIDForOtherMebmer = "test02";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMebmer);
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContents("차단::내용");
		

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
		boardReplyReq.setRequestedUserID(requestedUserIDForOtherMebmer);
		boardReplyReq.setIp("127.0.0.1");
		boardReplyReq.setBoardID(boardWriteRes.getBoardID());		
		boardReplyReq.setParentBoardNo(boardWriteRes.getBoardNo());
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
		blcokBoardBlockReq.setIp("172.3.0.12");

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
		boardModifyReq.setRequestedUserID(requestedUserIDForOtherMebmer);
		boardModifyReq.setBoardID(boardReplyRes.getBoardID());
		boardModifyReq.setBoardNo(boardReplyRes.getBoardNo());
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContents("내용::그림5-1하나를 그리다#1");
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
	public void 게시글수정_새로운다음첨부파일시퀀스최대값초과() {
		final short boardID = 3;
		
		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID("test01");
		boardModifyReq.setBoardID(boardID);
		boardModifyReq.setBoardNo(1);
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContents("내용::그림5-1하나를 그리다#1");
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
	public void 게시글수정_다음첨부파일시퀀스번호DB값과다름() {
		final short boardID = 3;
		String requestedUserIDForMebmer = "test01";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMebmer);
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContents("차단::내용");
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
		boardModifyReq.setRequestedUserID(requestedUserIDForMebmer);
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContents("내용::그림5-1하나를 그리다#1");
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
	public void 게시글수정_구첨부파일시퀀스_중복() {
		final short boardID = 3;		
		String requestedUserIDForMebmer = "test01";
		
		
		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID(requestedUserIDForMebmer);
		boardModifyReq.setBoardID(boardID);
		boardModifyReq.setBoardNo(1);
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContents("내용::그림5-1하나를 그리다#1");
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
	public void 게시글수정_구첨부파일시퀀스_다음첨부파일시퀀보다큰경우() {
		final short boardID = 3;
		String requestedUserIDForMebmer = "test01";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMebmer);
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContents("차단::내용");
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
		boardModifyReq.setRequestedUserID(requestedUserIDForMebmer);
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContents("내용::그림5-1하나를 그리다#1");
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
	public void 게시글수정_구첨부파일시퀀스_첨부파일없는경우에DB와불일치() {
		final short boardID = 3;
		String requestedUserIDForMebmer = "test01";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMebmer);
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContents("차단::내용");
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
		boardModifyReq.setRequestedUserID(requestedUserIDForMebmer);
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContents("내용::그림5-1하나를 그리다#1");
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
	public void 게시글수정_구첨부파일시퀀스_첨부파일있는경우DB불일치() {
		final short boardID = 3;
		String requestedUserIDForMebmer = "test01";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMebmer);
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContents("차단::내용");
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
		boardModifyReq.setRequestedUserID(requestedUserIDForMebmer);
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContents("내용::그림5-1하나를 그리다#1");
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
	public void 게시글수정_작성자일반회원_게시글비밀번호있음_수정요청자본인_비밀번호미입력() {
		final short boardID = 3;
		String requestedUserIDForMebmer = "test01";
		
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
		boardWriteReq.setRequestedUserID(requestedUserIDForMebmer);
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("테스트 주제");
		boardWriteReq.setContents("내용::그림2 하나를 그리다");
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setPwdHashBase64(boardPwdHashBase64);
	
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
		
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e2) {
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
	
		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setBoardID(boardWriteRes.getBoardID());
		boardDetailReq.setBoardNo(boardWriteRes.getBoardNo());
		boardDetailReq.setRequestedUserID(boardWriteReq.getRequestedUserID());
	
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
	
		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID(boardWriteReq.getRequestedUserID());
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("수정 테스트를 위한 최상위 본문글#1");
		boardModifyReq.setContents("내용::수정 테스트를 위한 최상위 본문글#1");
		boardModifyReq.setIp("172.16.0.3");
		boardModifyReq.setNextAttachedFileSeq(oldNextAttachedFileSeq);
		boardModifyReq.setPwdHashBase64("");
	
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
			boardModifyReqServerTask.doWork(
					TEST_DBCP_NAME, boardModifyReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "게시글 비밀번호를 입력해 주세요";

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	
	@Test
	public void 게시글수정_작성자일반회원_게시글비밀번호있음_수정요청자본인_비밀번호틀림() {
		final short boardID = 3;
		String requestedUserIDForMebmer = "test01";
		
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
		boardWriteReq.setRequestedUserID(requestedUserIDForMebmer);
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("테스트 주제");
		boardWriteReq.setContents("내용::그림2 하나를 그리다");
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setPwdHashBase64(boardPwdHashBase64);
	
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
		
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e2) {
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
	
		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setBoardID(boardWriteRes.getBoardID());
		boardDetailReq.setBoardNo(boardWriteRes.getBoardNo());
		boardDetailReq.setRequestedUserID(boardWriteReq.getRequestedUserID());
	
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
	
		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID(boardWriteReq.getRequestedUserID());
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("수정 테스트를 위한 최상위 본문글#1");
		boardModifyReq.setContents("내용::수정 테스트를 위한 최상위 본문글#1");
		boardModifyReq.setIp("172.16.0.3");
		boardModifyReq.setNextAttachedFileSeq(oldNextAttachedFileSeq);
		boardModifyReq.setPwdHashBase64("aa");
	
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
			boardModifyReqServerTask.doWork(
					TEST_DBCP_NAME, boardModifyReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String acutalErrorMessage = e.getMessage();
			String expectedErrorMessage = "설정한 게시글 비밀 번호와 일치하지 않습니다";

			assertEquals(expectedErrorMessage, acutalErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글수정_작성자일반회원_게시글비밀번호없음_수정요청자본인_OK() {
		final short boardID = 3;
		
		String requestedUserIDForMebmer = "test01";
		String requestedUserIDForOtherMebmer = "test02";

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMebmer);
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("차단::제목");
		boardWriteReq.setContents("차단::내용");
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
		boardModifyReq.setRequestedUserID(requestedUserIDForMebmer);
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContents("내용::그림5-1하나를 그리다#1");
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
		boardDetailReq.setRequestedUserID(requestedUserIDForOtherMebmer);

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
		assertEquals("내용 비교", boardModifyReq.getContents(),
				boardDetailRes.getContents());
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
	public void 게시글수정_작성자일반회원_비밀번호있음_수정요청자관리자_비밀번호미입력_OK() {
		String requestedUserIDForAdmin = "admin";
		String requestedUserIDForMebmer = "test01";
		
		final short boardID = 3;
		
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
		

		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMebmer);
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("원본::제목");
		boardWriteReq.setContents("원본::내용");
		boardWriteReq.setIp("172.16.0.1");
		boardWriteReq.setPwdHashBase64(boardPwdHashBase64);

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
		boardModifyReq.setRequestedUserID(requestedUserIDForAdmin);
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("수정::테스트 주제05-1#1");
		boardModifyReq.setContents("수정::그림5-1하나를 그리다#1");
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setNextAttachedFileSeq((short) 2);
		boardModifyReq.setPwdHashBase64("");

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
			
			fail(e.getMessage());
		}
				
		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setBoardID(boardWriteRes.getBoardID());
		boardDetailReq.setBoardNo(boardWriteRes.getBoardNo());
		boardDetailReq.setRequestedUserID(requestedUserIDForMebmer);

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
		assertEquals("내용 비교", boardModifyReq.getContents(),
				boardDetailRes.getContents());
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
