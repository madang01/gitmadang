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
import kr.pe.codda.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.codda.impl.message.BoardReplyRes.BoardReplyRes;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public class BoardReplyReqServerTaskTest extends AbstractJunitTest {
	final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();		
		
		ServerDBUtil.initializeDBEnvoroment(TEST_DBCP_NAME);	
		
		String userID = "test01";
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = "단위테스터용아이디1";
		String pwdHint = "힌트 그것이 알고싶다";
		String pwdAnswer = "힌트답변 말이여 방구여";
		String ip = "127.0.0.1";
		
		try {
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, 
					pwdHint, pwdAnswer, passwordBytes, ip, new java.sql.Timestamp(System.currentTimeMillis()));
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
	
	
	
	@Test
	public void testDoService_ok() {
		final short boardID = 3;
		
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("테스트 주제");
		boardWriteReq.setContents("내용::그림3 하나를 그리다");		
		boardWriteReq.setRequestedUserID("test01");
		boardWriteReq.setIp("172.16.0.2");		
		
		{
			List<BoardWriteReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();
			
			{
				BoardWriteReq.NewAttachedFile newAttachedFile = new BoardWriteReq.NewAttachedFile();
				newAttachedFile.setAttachedFileName("임시첨부파일01.jpg");
				newAttachedFile.setAttachedFileSize(2017);
				
				newAttachedFileList.add(newAttachedFile);
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
		boardReplyReq.setSubject("테스트 주제03-1");
		boardReplyReq.setContents("내용::그림3-1하나를 그리다");		
		boardReplyReq.setRequestedUserID("test01");
		boardReplyReq.setIp("127.0.0.1");		
		
		{			
			List<BoardReplyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardReplyReq.NewAttachedFile>();
			
			{
				BoardReplyReq.NewAttachedFile newAttachedFile = new BoardReplyReq.NewAttachedFile();
				newAttachedFile.setAttachedFileName("임시첨부파일03_1.jpg");
				newAttachedFile.setAttachedFileSize(2018);
				
				newAttachedFileList.add(newAttachedFile);
			}
			
			{
				BoardReplyReq.NewAttachedFile newAttachedFile = new BoardReplyReq.NewAttachedFile();
				newAttachedFile.setAttachedFileName("임시첨부파일03_2.jpg");
				newAttachedFile.setAttachedFileSize(2019);
				
				newAttachedFileList.add(newAttachedFile);
			}
			
			boardReplyReq.setNewAttachedFileCnt((short)newAttachedFileList.size());
			boardReplyReq.setNewAttachedFileList(newAttachedFileList);
		}
		
		BoardReplyReqServerTask boardReplyReqServerTask = null;
		try {
			boardReplyReqServerTask = new BoardReplyReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			BoardReplyRes boardReplyRes = boardReplyReqServerTask.doWork(TEST_DBCP_NAME, boardReplyReq);
			log.info(boardReplyRes.toString());
		} catch(ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
}
