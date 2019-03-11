package kr.pe.codda.impl.task.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.BoardDetailReq.BoardDetailReq;
import kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes;
import kr.pe.codda.impl.message.BoardModifyReq.BoardModifyReq;
import kr.pe.codda.impl.message.BoardModifyRes.BoardModifyRes;
import kr.pe.codda.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.codda.impl.message.BoardReplyRes.BoardReplyRes;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public class BoardModifyReqServerTaskTest extends AbstractJunitTest {
	final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();		
		
		ServerDBUtil.initializeDBEnvoroment(TEST_DBCP_NAME);	
		
		
		{
			String userID = "test01";
			byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
			String nickname = "단위테스터용아이디1";
			String pwdHint = "힌트 그것이 알고싶다";
			String pwdAnswer = "힌트답변 말이여 방구여";
			String ip = "127.0.0.1";
			
			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[")
						.append(userID)
						.append("] 입니다").toString();
				String actualErrorMessag = e.getMessage();
				
				log.warn(actualErrorMessag, e);
				
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
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
			} catch (ServerServiceException e) {
				String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[")
						.append(userID)
						.append("] 입니다").toString();
				String actualErrorMessag = e.getMessage();
				
				log.warn(actualErrorMessag, e);
				
				assertEquals(expectedErrorMessage, actualErrorMessag);
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to create a test ID");
			}	
		}
	}
	

	@Test
	public void testDoService_최상위글_ok() {
		final short boardID = 3;
		
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("수정 테스트를 위한 최상위 본문글");
		boardWriteReq.setContents("내용::수정 테스트를 위한 최상위 본문글");		
		boardWriteReq.setRequestedUserID("test01");
		boardWriteReq.setIp("172.16.0.3");
		
		List<BoardWriteReq.NewAttachedFile> newAttachedFileListForBoardWriteReq = new ArrayList<BoardWriteReq.NewAttachedFile>();
		{
			BoardWriteReq.NewAttachedFile attachedFile = new BoardWriteReq.NewAttachedFile();
			attachedFile.setAttachedFileName("임시첨부파일01.jpg");
			attachedFile.setAttachedFileSize(1025);
			
			newAttachedFileListForBoardWriteReq.add(attachedFile);
		}
		
		boardWriteReq.setNewAttachedFileCnt((short)newAttachedFileListForBoardWriteReq.size());
		boardWriteReq.setNewAttachedFileList(newAttachedFileListForBoardWriteReq);
		
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME, boardWriteReq);
			log.info(boardWriteRes.toString());
		} catch(ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}		
		
		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setBoardID(boardWriteRes.getBoardID());
		boardModifyReq.setBoardNo(boardWriteRes.getBoardNo());
		boardModifyReq.setSubject("수정 테스트를 위한 최상위 본문글#1");
		boardModifyReq.setContents("내용::수정 테스트를 위한 최상위 본문글#1");	
		boardModifyReq.setNextAttachedFileSeq((short)newAttachedFileListForBoardWriteReq.size());
		boardModifyReq.setRequestedUserID(boardWriteReq.getRequestedUserID());		
		boardModifyReq.setIp("172.16.0.4");
		
		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();		
		{
			{
				for (int i=0; i < newAttachedFileListForBoardWriteReq.size(); i++) {
					BoardModifyReq.OldAttachedFile oldAttachedFile = new BoardModifyReq.OldAttachedFile();
					oldAttachedFile.setAttachedFileSeq((short)i);
					oldAttachedFileList.add(oldAttachedFile);
				}
			}
			
			boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList.size());
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
			BoardModifyRes boardModifyRes = boardModifyReqServerTask.doWork(TEST_DBCP_NAME, boardModifyReq);
			log.info(boardModifyRes.toString());
		} catch(ServerServiceException e) {
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
		try {
			BoardDetailRes boardDetailRes = boardDetailReqServerTask.doWork(TEST_DBCP_NAME, boardDetailReq);
			
			assertEquals(boardModifyReq.getSubject(), boardDetailRes.getSubject());
			assertEquals(boardModifyReq.getContents(), boardDetailRes.getContents());
			assertEquals(boardWriteReq.getRequestedUserID(), boardDetailRes.getFirstWriterID());
			
			
			assertEquals(boardWriteReq.getNewAttachedFileList().get(0).getAttachedFileName(), 
					boardDetailRes.getAttachedFileList().get(0).getAttachedFileName());			
			
			log.info(boardDetailRes.toString());
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void testDoService_댓글_ok() {
		final short boardID = 3;
		
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("테스트를 위한 최상위 본문글");
		boardWriteReq.setContents("테스트를 위한 최상위 본문글");		
		boardWriteReq.setRequestedUserID("test01");
		boardWriteReq.setIp("172.16.0.5");
		
		{
			List<BoardWriteReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();
			{
				BoardWriteReq.NewAttachedFile attachedFile = new BoardWriteReq.NewAttachedFile();
				attachedFile.setAttachedFileName("임시첨부파일01.jpg");
				attachedFile.setAttachedFileSize(1027);
				
				newAttachedFileList.add(attachedFile);
			}
			
			boardWriteReq.setNewAttachedFileCnt((short)newAttachedFileList.size());
			boardWriteReq.setNewAttachedFileList(newAttachedFileList);
		}
		
		
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME, boardWriteReq);
			log.info(boardWriteRes.toString());
		} catch(ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}		
		
		
		BoardReplyReq boardReplyReq = new BoardReplyReq();
		boardReplyReq.setBoardID(boardWriteRes.getBoardID());
		boardReplyReq.setParentBoardNo(boardWriteRes.getBoardNo());
		boardReplyReq.setSubject("테스트 주제05-1");
		boardReplyReq.setContents("내용::그림5-1하나를 그리다");		
		boardReplyReq.setRequestedUserID("test02");
		boardReplyReq.setIp("172.16.0.6");
		
					
		List<BoardReplyReq.NewAttachedFile> newAttachedFileListForReply = new ArrayList<BoardReplyReq.NewAttachedFile>();
		
		{
			BoardReplyReq.NewAttachedFile newAttachedFile = new BoardReplyReq.NewAttachedFile();
			newAttachedFile.setAttachedFileName("임시첨부파일03_1.jpg");
			newAttachedFile.setAttachedFileSize(1028);
			
			newAttachedFileListForReply.add(newAttachedFile);
		}
		
		{
			BoardReplyReq.NewAttachedFile newAttachedFile = new BoardReplyReq.NewAttachedFile();
			newAttachedFile.setAttachedFileName("임시첨부파일03_2.jpg");
			newAttachedFile.setAttachedFileSize(1029);
			
			newAttachedFileListForReply.add(newAttachedFile);
		}
		
		boardReplyReq.setNewAttachedFileCnt((short)newAttachedFileListForReply.size());
		boardReplyReq.setNewAttachedFileList(newAttachedFileListForReply);
		
		
		BoardReplyReqServerTask boardReplyReqServerTask = null;
		try {
			boardReplyReqServerTask = new BoardReplyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		BoardReplyRes boardReplyRes = null;
		try {
			boardReplyRes = boardReplyReqServerTask.doWork(TEST_DBCP_NAME, boardReplyReq);
			log.info(boardReplyRes.toString());
		} catch(ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		
		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setBoardID(boardReplyReq.getBoardID());
		boardModifyReq.setBoardNo(boardReplyRes.getBoardNo());
		boardModifyReq.setSubject("테스트 주제05-1#1");
		boardModifyReq.setContents("내용::그림5-1하나를 그리다#1");	
		boardModifyReq.setRequestedUserID(boardReplyReq.getRequestedUserID());
		boardModifyReq.setIp("172.16.0.7");
		boardModifyReq.setNextAttachedFileSeq((short)newAttachedFileListForReply.size());
		
		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();		
		{
			{
				for (int i=0; i < newAttachedFileListForReply.size(); i++) {
					BoardModifyReq.OldAttachedFile oldAttachedFile = new BoardModifyReq.OldAttachedFile();
					oldAttachedFile.setAttachedFileSeq((short)i);
					oldAttachedFileList.add(oldAttachedFile);
				}
			}
			
			boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList.size());
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
			BoardModifyRes boardModifyRes = boardModifyReqServerTask.doWork(TEST_DBCP_NAME, boardModifyReq);
			log.info(boardModifyRes.toString());
		} catch(ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}		
		
		BoardDetailReq boardDetailReq = new BoardDetailReq();
		boardDetailReq.setBoardID(boardReplyRes.getBoardID());
		boardDetailReq.setBoardNo(boardReplyRes.getBoardNo());
		boardDetailReq.setRequestedUserID(boardReplyReq.getRequestedUserID());
		
		
		BoardDetailReqServerTask boardDetailReqServerTask = null;
		try {
			boardDetailReqServerTask = new BoardDetailReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			BoardDetailRes boardDetailRes = boardDetailReqServerTask.doWork(TEST_DBCP_NAME, boardDetailReq);
			
			assertEquals(boardModifyReq.getSubject(), boardDetailRes.getSubject());
			assertEquals(boardModifyReq.getContents(), boardDetailRes.getContents());
			assertEquals(boardReplyReq.getRequestedUserID(), boardDetailRes.getFirstWriterID());	
			
			
			assertEquals(boardReplyReq.getNewAttachedFileList().get(0).getAttachedFileName(), 
					boardDetailRes.getAttachedFileList().get(0).getAttachedFileName());			
			
			log.info(boardDetailRes.toString());
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}

}
