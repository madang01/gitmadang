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
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public class BoardWriteReqServerTaskTest extends AbstractJunitTest {
	private final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.LOAD_TEST_DBCP_NAME;
	
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
			ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberRoleType.MEMBER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
		} catch (ServerServiceException e) {
			String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[")
					.append(userID)
					.append("] 입니다").toString();
			String actualErrorMessag = e.getMessage();
			
			// log.warn(actualErrorMessag, e);
			
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
		boardWriteReq.setContents("내용::그림2 하나를 그리다");		
		boardWriteReq.setRequestedUserID("test01");
		boardWriteReq.setIp("172.16.0.1");
		
		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();
		{
			/*BoardWriteReq.NewAttachedFile attachedFile = new BoardWriteReq.NewAttachedFile();
			attachedFile.setAttachedFileName("임시첨부파일01.jpg");
			
			attachedFileList.add(attachedFile);*/
		}
		
		boardWriteReq.setNewAttachedFileCnt((short)attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);
		
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			BoardWriteRes boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME, boardWriteReq);
			log.info(boardWriteRes.toString());
		} catch(ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void testDoService_자유게시판1천만레코드생성() {
		final short boardID = 3;
		
		BoardWriteReqServerTask boardWriteReqServerTask = null;
		try {
			boardWriteReqServerTask = new BoardWriteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setBoardID(boardID);
		boardWriteReq.setSubject("테스트 주제");
		boardWriteReq.setContents("내용::그림2 하나를 그리다");		
		boardWriteReq.setRequestedUserID("test01");
		boardWriteReq.setIp("172.16.0.1");
		
		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();
		{
			BoardWriteReq.NewAttachedFile attachedFile = new BoardWriteReq.NewAttachedFile();
			attachedFile.setAttachedFileName("임시첨부파일01.jpg");
			attachedFile.setAttachedFileSize(2612);
			
			attachedFileList.add(attachedFile);
		}
		
		boardWriteReq.setNewAttachedFileCnt((short)attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);
		
		for (int i=1; i <= 10; i++) {
			String str = new StringBuilder().append("계층형 게시판")
					.append(i).toString();
			boardWriteReq.setSubject(str);
			boardWriteReq.setContents(str);
			
			try {
				boardWriteReqServerTask.doWork(TEST_DBCP_NAME, boardWriteReq);
			} catch(ServerServiceException e) {
				log.warn(e.getMessage(), e);
				fail("fail to execuate doTask");
			} catch (Exception e) {
				log.warn("unknown error", e);
				fail("fail to execuate doTask");
			}
		}
	}
}
