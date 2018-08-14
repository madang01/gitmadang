package kr.pe.codda.impl.task.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.junit.BeforeClass;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.sessionkey.ClientSessionKeyIF;
import kr.pe.codda.common.sessionkey.ClientSessionKeyManager;
import kr.pe.codda.common.sessionkey.ClientSymmetricKeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.impl.message.BoardDetailReq.BoardDetailReq;
import kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes;
import kr.pe.codda.impl.message.BoardModifyReq.BoardModifyReq;
import kr.pe.codda.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.codda.impl.message.BoardReplyRes.BoardReplyRes;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.impl.message.MemberRegisterReq.MemberRegisterReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.lib.BoardType;
import kr.pe.codda.server.lib.MemberType;
import kr.pe.codda.server.lib.ServerDBUtil;

public class BoardModifyReqServerTaskTest extends AbstractJunitTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();		
		
		ServerDBUtil.initializeDBEnvoroment("testAdmin");	
		
		createUser("test01", "단위테스터용아이디1");
		createUser("test02", "단위테스터용아이디2");
	}
	
	/**
	 * 테스트 아이디 'test01' 을 생성한다.
	 */
	private static void createUser(String wantedUserID, String watntedNickname) {
		String userID = wantedUserID;
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		String nickname = watntedNickname;
		String pwdHint = "힌트 그것이 알고싶다";
		String pwdAnswer = "말이여 방구여";
		
		
		ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
		// serverSessionkeyManager.getMainProjectServerSessionkey().getDupPublicKeyBytes();
		
		ClientSessionKeyManager clientSessionKeyManager = ClientSessionKeyManager.getInstance();
		ClientSessionKeyIF clientSessionKey = null;
		try {
			clientSessionKey = clientSessionKeyManager
					.getNewClientSessionKey(serverSessionkeyManager.getMainProjectServerSessionkey().getDupPublicKeyBytes());
		} catch (SymmetricException e) {
			fail("fail to get a ClientSessionKey");
		}
		
		ClientSymmetricKeyIF clientSymmetricKey = clientSessionKey.getClientSymmetricKey();
		
		byte[] idCipherTextBytes = null;
		try {
			idCipherTextBytes = clientSymmetricKey.encrypt(userID.getBytes(CommonStaticFinalVars.CIPHER_CHARSET));
		} catch (Exception e) {
			fail("fail to encrypt id");
		}
		byte[] passwordCipherTextBytes = null;
		
		try {
			passwordCipherTextBytes = clientSymmetricKey.encrypt(passwordBytes);
		} catch (Exception e) {
			fail("fail to encrypt password");
		}
		
		Arrays.fill(passwordBytes, CommonStaticFinalVars.ZERO_BYTE);
		
		byte[] nicknameCipherTextBytes = null;
		try {
			nicknameCipherTextBytes = clientSymmetricKey.encrypt(nickname.getBytes(CommonStaticFinalVars.CIPHER_CHARSET));
		} catch (Exception e) {
			fail("fail to encrypt id");
		}
		
		byte[] pwdHintCipherTextBytes = null;
		try {
			pwdHintCipherTextBytes = clientSymmetricKey.encrypt(pwdHint.getBytes(CommonStaticFinalVars.CIPHER_CHARSET));
		} catch (Exception e) {
			fail("fail to encrypt id");
		}
		
		byte[] pwdAnswerCipherTextBytes = null;
		try {
			pwdAnswerCipherTextBytes = clientSymmetricKey.encrypt(pwdAnswer.getBytes(CommonStaticFinalVars.CIPHER_CHARSET));
		} catch (Exception e) {
			fail("fail to encrypt id");
		}
		
		MemberRegisterReq memberRegisterReq = new MemberRegisterReq();
		memberRegisterReq.setIdCipherBase64(Base64.encodeBase64String(idCipherTextBytes));
		memberRegisterReq.setPwdCipherBase64(Base64.encodeBase64String(passwordCipherTextBytes));
		memberRegisterReq.setNicknameCipherBase64(Base64.encodeBase64String(nicknameCipherTextBytes));
		memberRegisterReq.setHintCipherBase64(Base64.encodeBase64String(pwdHintCipherTextBytes));
		memberRegisterReq.setAnswerCipherBase64(Base64.encodeBase64String(pwdAnswerCipherTextBytes));
		memberRegisterReq.setSessionKeyBase64(Base64.encodeBase64String(clientSessionKey.getDupSessionKeyBytes()));
		memberRegisterReq.setIvBase64(Base64.encodeBase64String(clientSessionKey.getDupIVBytes()));
	
		MemberRegisterReqServerTask memberRegisterReqServerTask= new MemberRegisterReqServerTask();
		
		try {
			@SuppressWarnings("unused")
			MessageResultRes messageResultRes = 
					memberRegisterReqServerTask.doService(memberRegisterReq, MemberType.USER);
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
	public void testDoService_최상위글_ok() {
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("수정 테스트를 위한 최상위 본문글");
		boardWriteReq.setContent("내용::수정 테스트를 위한 최상위 본문글");		
		boardWriteReq.setWriterID("test01");
		boardWriteReq.setIp("172.16.0.3");
		
		List<BoardWriteReq.NewAttachedFile> newAttachedFileListForBoardWriteReq = new ArrayList<BoardWriteReq.NewAttachedFile>();
		{
			BoardWriteReq.NewAttachedFile attachedFile = new BoardWriteReq.NewAttachedFile();
			attachedFile.setAttachedFileName("임시첨부파일01.jpg");
			
			newAttachedFileListForBoardWriteReq.add(attachedFile);
		}
		
		boardWriteReq.setNewAttachedFileCnt((short)newAttachedFileListForBoardWriteReq.size());
		boardWriteReq.setNewAttachedFileList(newAttachedFileListForBoardWriteReq);
		
		BoardWriteReqServerTask boardWriteReqServerTask= new BoardWriteReqServerTask();
		
		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doService(boardWriteReq);
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
		boardModifyReq.setContent("내용::수정 테스트를 위한 최상위 본문글#1");	
		boardModifyReq.setModifierID(boardWriteReq.getWriterID());
		boardModifyReq.setIp("172.16.0.4");
		
		List<BoardModifyReq.OldAttachedFileSeq> oldAttachedFileSeqList = new ArrayList<BoardModifyReq.OldAttachedFileSeq>();		
		{
			{
				for (int i=0; i < newAttachedFileListForBoardWriteReq.size(); i++) {
					BoardModifyReq.OldAttachedFileSeq oldAttachedFileSeq = new BoardModifyReq.OldAttachedFileSeq();
					oldAttachedFileSeq.setAttachedFileSeq((short)i);
					oldAttachedFileSeqList.add(oldAttachedFileSeq);
				}
			}
			
			boardModifyReq.setOldAttachedFileSeqCnt(oldAttachedFileSeqList.size());
			boardModifyReq.setOldAttachedFileSeqList(oldAttachedFileSeqList);
		}
		
		List<BoardModifyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardModifyReq.NewAttachedFile>();
		{
			boardModifyReq.setNewAttachedFileCnt(newAttachedFileList.size());
			boardModifyReq.setNewAttachedFileList(newAttachedFileList);
		}
		
		BoardModifyReqServerTask boardModifyReqServerTask = new BoardModifyReqServerTask();
		
		try {
			MessageResultRes messageResultRes = boardModifyReqServerTask.doService(boardModifyReq);
			log.info(messageResultRes.toString());
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
		boardDetailReq.setRequestUserID(boardWriteReq.getWriterID());
		
		
		BoardDetailReqServerTask boardDetailReqServerTask = new BoardDetailReqServerTask();
		try {
			BoardDetailRes boardDetailRes = boardDetailReqServerTask.doService(boardDetailReq);
			
			assertEquals(boardModifyReq.getSubject(), boardDetailRes.getSubject());
			assertEquals(boardModifyReq.getContent(), boardDetailRes.getContent());
			assertEquals(boardWriteReq.getWriterID(), boardDetailRes.getWriterID());
			assertEquals(boardModifyReq.getIp(), boardDetailRes.getFinalModifierIP());			
			
			
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
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setBoardID(BoardType.FREE.getBoardID());
		boardWriteReq.setSubject("테스트를 위한 최상위 본문글");
		boardWriteReq.setContent("테스트를 위한 최상위 본문글");		
		boardWriteReq.setWriterID("test01");
		boardWriteReq.setIp("172.16.0.5");
		
		{
			List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();
			{
				BoardWriteReq.NewAttachedFile attachedFile = new BoardWriteReq.NewAttachedFile();
				attachedFile.setAttachedFileName("임시첨부파일01.jpg");
				
				attachedFileList.add(attachedFile);
			}
			
			boardWriteReq.setNewAttachedFileCnt((short)attachedFileList.size());
			boardWriteReq.setNewAttachedFileList(attachedFileList);
		}
		
		
		BoardWriteReqServerTask boardWriteReqServerTask= new BoardWriteReqServerTask();
		
		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doService(boardWriteReq);
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
		boardReplyReq.setContent("내용::그림5-1하나를 그리다");		
		boardReplyReq.setWriterID("test02");
		boardReplyReq.setIp("172.16.0.6");		
		
					
			List<BoardReplyReq.AttachedFile> attachedFileList = new ArrayList<BoardReplyReq.AttachedFile>();
			
			{
				BoardReplyReq.AttachedFile attachedFile = new BoardReplyReq.AttachedFile();
				attachedFile.setAttachedFileName("임시첨부파일03_1.jpg");
				
				attachedFileList.add(attachedFile);
			}
			
			{
				BoardReplyReq.AttachedFile attachedFile = new BoardReplyReq.AttachedFile();
				attachedFile.setAttachedFileName("임시첨부파일03_2.jpg");
				
				attachedFileList.add(attachedFile);
			}
			
			boardReplyReq.setAttachedFileCnt((short)attachedFileList.size());
			boardReplyReq.setAttachedFileList(attachedFileList);
		
		
		BoardReplyReqServerTask boardReplyReqServerTask= new BoardReplyReqServerTask();
		
		BoardReplyRes boardReplyRes = null;
		try {
			boardReplyRes = boardReplyReqServerTask.doService(boardReplyReq);
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
		boardModifyReq.setContent("내용::그림5-1하나를 그리다#1");	
		boardModifyReq.setModifierID(boardReplyReq.getWriterID());
		boardModifyReq.setIp("172.16.0.7");
		
		List<BoardModifyReq.OldAttachedFileSeq> oldAttachedFileSeqList = new ArrayList<BoardModifyReq.OldAttachedFileSeq>();		
		{
			{
				for (int i=0; i < attachedFileList.size(); i++) {
					BoardModifyReq.OldAttachedFileSeq oldAttachedFileSeq = new BoardModifyReq.OldAttachedFileSeq();
					oldAttachedFileSeq.setAttachedFileSeq((short)i);
					oldAttachedFileSeqList.add(oldAttachedFileSeq);
				}
			}
			
			boardModifyReq.setOldAttachedFileSeqCnt(oldAttachedFileSeqList.size());
			boardModifyReq.setOldAttachedFileSeqList(oldAttachedFileSeqList);
		}
		
		List<BoardModifyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardModifyReq.NewAttachedFile>();
		{
			boardModifyReq.setNewAttachedFileCnt(newAttachedFileList.size());
			boardModifyReq.setNewAttachedFileList(newAttachedFileList);
		}
		
		BoardModifyReqServerTask boardModifyReqServerTask = new BoardModifyReqServerTask();
		
		try {
			MessageResultRes messageResultRes = boardModifyReqServerTask.doService(boardModifyReq);
			log.info(messageResultRes.toString());
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
		boardDetailReq.setRequestUserID(boardReplyReq.getWriterID());
		
		
		BoardDetailReqServerTask boardDetailReqServerTask = new BoardDetailReqServerTask();
		try {
			BoardDetailRes boardDetailRes = boardDetailReqServerTask.doService(boardDetailReq);
			
			assertEquals(boardModifyReq.getSubject(), boardDetailRes.getSubject());
			assertEquals(boardModifyReq.getContent(), boardDetailRes.getContent());
			assertEquals(boardReplyReq.getWriterID(), boardDetailRes.getWriterID());
			assertEquals(boardModifyReq.getIp(), boardDetailRes.getFinalModifierIP());			
			
			
			assertEquals(boardReplyReq.getAttachedFileList().get(0).getAttachedFileName(), 
					boardDetailRes.getAttachedFileList().get(0).getAttachedFileName());			
			
			log.info(boardDetailRes.toString());
		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}
	}

}
