package kr.pe.codda.impl.task.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import junitlib.AbstractBoardTest;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.BoardInfoAddReq.BoardInfoAddReq;
import kr.pe.codda.impl.message.BoardInfoAddRes.BoardInfoAddRes;
import kr.pe.codda.impl.message.BoardMoveReq.BoardMoveReq;
import kr.pe.codda.impl.message.BoardMoveRes.BoardMoveRes;
import kr.pe.codda.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.codda.impl.message.BoardReplyRes.BoardReplyRes;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.server.lib.BoardListType;
import kr.pe.codda.server.lib.BoardReplyPolicyType;
import kr.pe.codda.server.lib.BoardTree;
import kr.pe.codda.server.lib.BoardTreeNode;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.VirtualBoardTreeBuilderIF;

public class BoardMoveReqServerTaskTest extends AbstractBoardTest {
	
	@Test
	public void 게시글이동_동일게시판이동() {
		final short sourceBoardID = 2;
		final long sourceBoardNo = 1;
		final short targetBoardID = sourceBoardID;
		final String requestedUserIDForMember = "test01";
		final String ip = "172.16.0.1";
		
		BoardMoveReqServerTask boardMoveReqServerTask = null;
		try {
			boardMoveReqServerTask = new BoardMoveReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
	
		BoardMoveReq boardMoveReq = new BoardMoveReq();
		boardMoveReq.setRequestedUserID(requestedUserIDForMember);
		boardMoveReq.setIp(ip);
		boardMoveReq.setSourceBoardID(sourceBoardID);
		boardMoveReq.setSourceBoardNo(sourceBoardNo);
		boardMoveReq.setTargetBoardID(targetBoardID);
		
		
		try {
			boardMoveReqServerTask.doWork(
					TEST_DBCP_NAME, boardMoveReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String actualErrorMessage = e.getMessage();
			
			String expectedErrorMessage = "동일한 게시판으로 이동할 수 없습니다";
	
			assertEquals(expectedErrorMessage, actualErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글이동_일반인() {
		final short sourceBoardID = 2;
		final long sourceBoardNo = 1;
		final short targetBoardID = 3;
		final String requestedUserIDForMember = "test01";
		final String ip = "172.16.0.1";
		
		BoardMoveReqServerTask boardMoveReqServerTask = null;
		try {
			boardMoveReqServerTask = new BoardMoveReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
	
		BoardMoveReq boardMoveReq = new BoardMoveReq();
		boardMoveReq.setRequestedUserID(requestedUserIDForMember);
		boardMoveReq.setIp(ip);
		boardMoveReq.setSourceBoardID(sourceBoardID);
		boardMoveReq.setSourceBoardNo(sourceBoardNo);
		boardMoveReq.setTargetBoardID(targetBoardID);
		
		
		try {
			boardMoveReqServerTask.doWork(
					TEST_DBCP_NAME, boardMoveReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String actualErrorMessage = e.getMessage();
			
			String expectedErrorMessage = "게시글 이동 서비스는 관리자 전용 서비스입니다";
	
			assertEquals(expectedErrorMessage, actualErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 게시글이동_손님() {
		final short sourceBoardID = 2;
		final long sourceBoardNo = 1;
		final short targetBoardID = 3;
		final String requestedUserIDForGuest = "guest";
		final String ip = "172.16.0.1";
		
		
	
		BoardMoveReq boardMoveReq = new BoardMoveReq();
		boardMoveReq.setRequestedUserID(requestedUserIDForGuest);
		boardMoveReq.setIp(ip);
		boardMoveReq.setSourceBoardID(sourceBoardID);
		boardMoveReq.setSourceBoardNo(sourceBoardNo);
		boardMoveReq.setTargetBoardID(targetBoardID);
		
		BoardMoveReqServerTask boardMoveReqServerTask = null;
		try {
			boardMoveReqServerTask = new BoardMoveReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			boardMoveReqServerTask.doWork(
					TEST_DBCP_NAME, boardMoveReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String actualErrorMessage = e.getMessage();
			
			String expectedErrorMessage = "게시글 이동 서비스는 관리자 전용 서비스입니다";
	
			assertEquals(expectedErrorMessage, actualErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글이동_이동전게시글식별자_미존재() {
		final short sourceBoardID = 7;
		final long sourceBoardNo = 1;
		final short targetBoardID = 3;
		final String requestedUserIDForAdmin = "admin";
		final String ip = "172.16.0.1";
	
		BoardMoveReq boardMoveReq = new BoardMoveReq();
		boardMoveReq.setRequestedUserID(requestedUserIDForAdmin);
		boardMoveReq.setIp(ip);
		boardMoveReq.setSourceBoardID(sourceBoardID);
		boardMoveReq.setSourceBoardNo(sourceBoardNo);
		boardMoveReq.setTargetBoardID(targetBoardID);
		
		BoardMoveReqServerTask boardMoveReqServerTask = null;
		try {
			boardMoveReqServerTask = new BoardMoveReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			boardMoveReqServerTask.doWork(
					TEST_DBCP_NAME, boardMoveReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String actualErrorMessage = e.getMessage();
			
			String expectedErrorMessage = new StringBuilder("이동 전 게시판 식별자[").append(sourceBoardID)
					.append("]가 게시판 정보 테이블에 존재하지  않습니다").toString();
	
			assertEquals(expectedErrorMessage, actualErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 게시글이동_이동후게시글식별자_미존재() {
		/** 자유 게시판 식별자:1, 이슈 게시판 식별자:2 */
		final short sourceBoardID = 1;
		final short targetBoardID = 7;
		final String requestedUserIDForAdmin = "admin";
		final String requestedUserIDForMember = "test01";
		final String ip = "172.16.0.1";
		
		List<BoardWriteReq.NewAttachedFile> newAttachedFileListForWrite = new ArrayList<BoardWriteReq.NewAttachedFile>();
	
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMember);
		boardWriteReq.setIp(ip);
		boardWriteReq.setBoardID(sourceBoardID);
		boardWriteReq.setSubject("자유::테스트 주제");
		boardWriteReq.setContents("자유::내용::그림2 하나를 그리다");
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
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		
	
		BoardMoveReq boardMoveReq = new BoardMoveReq();
		boardMoveReq.setRequestedUserID(requestedUserIDForAdmin);
		boardMoveReq.setIp(ip);
		boardMoveReq.setSourceBoardID(boardWriteRes.getBoardID());
		boardMoveReq.setSourceBoardNo(boardWriteRes.getBoardNo());
		boardMoveReq.setTargetBoardID(targetBoardID);
		
		BoardMoveReqServerTask boardMoveReqServerTask = null;
		try {
			boardMoveReqServerTask = new BoardMoveReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			boardMoveReqServerTask.doWork(
					TEST_DBCP_NAME, boardMoveReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String actualErrorMessage = e.getMessage();
			
			String expectedErrorMessage = new StringBuilder("이동 후 게시판 식별자[").append(targetBoardID)
					.append("]가 게시판 정보 테이블에 존재하지  않습니다").toString();
	
			assertEquals(expectedErrorMessage, actualErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 게시글이동_게시판유형불일치() {
		/** 자유 게시판 식별자:1, 이슈 게시판 식별자:2, 3:계층형 게시판 식별자 */
		final short sourceBoardID = 1;
		final short targetBoardID = 3;
		final String requestedUserIDForAdmin = "admin";
		final String requestedUserIDForMember = "test01";
		final String ip = "172.16.0.1";
		
		List<BoardWriteReq.NewAttachedFile> newAttachedFileListForWrite = new ArrayList<BoardWriteReq.NewAttachedFile>();
	
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMember);
		boardWriteReq.setIp(ip);
		boardWriteReq.setBoardID(sourceBoardID);
		boardWriteReq.setSubject("자유::테스트 주제");
		boardWriteReq.setContents("자유::내용::그림2 하나를 그리다");
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
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		
	
		BoardMoveReq boardMoveReq = new BoardMoveReq();
		boardMoveReq.setRequestedUserID(requestedUserIDForAdmin);
		boardMoveReq.setIp(ip);
		boardMoveReq.setSourceBoardID(boardWriteRes.getBoardID());
		boardMoveReq.setSourceBoardNo(boardWriteRes.getBoardNo());
		boardMoveReq.setTargetBoardID(targetBoardID);
		
		BoardMoveReqServerTask boardMoveReqServerTask = null;
		try {
			boardMoveReqServerTask = new BoardMoveReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			boardMoveReqServerTask.doWork(
					TEST_DBCP_NAME, boardMoveReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String actualErrorMessage = e.getMessage();
			
			String expectedErrorMessage = "이동 하고자 하는 목적지의 게시판 유형이 일치 하지 않아 이동할 수 없습니다";
	
			assertEquals(expectedErrorMessage, actualErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 게시글이동_이동대상게시글없음() {
		/** 자유 게시판 식별자:1, 이슈 게시판 식별자:2, 3:계층형 게시판 식별자 */
		final short sourceBoardID = 1;
		final long sourceBoardNo = 1;
		final short targetBoardID = 2;
		final String requestedUserIDForAdmin = "admin";
		final String ip = "172.16.0.1";
		
		BoardMoveReq boardMoveReq = new BoardMoveReq();
		boardMoveReq.setRequestedUserID(requestedUserIDForAdmin);
		boardMoveReq.setIp(ip);
		boardMoveReq.setSourceBoardID(sourceBoardID);
		boardMoveReq.setSourceBoardNo(sourceBoardNo);
		boardMoveReq.setTargetBoardID(targetBoardID);
		
		BoardMoveReqServerTask boardMoveReqServerTask = null;
		try {
			boardMoveReqServerTask = new BoardMoveReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			boardMoveReqServerTask.doWork(
					TEST_DBCP_NAME, boardMoveReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String actualErrorMessage = e.getMessage();
			
			String expectedErrorMessage = new StringBuilder()
					.append("해당 게시글[boardID=")
					.append(sourceBoardID)
					.append(", boardNo=")
					.append(sourceBoardNo)
					.append("]이 존재 하지 않습니다").toString();
	
			assertEquals(expectedErrorMessage, actualErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 게시글이동_댓글() {
		/** 자유 게시판 식별자:1, 이슈 게시판 식별자:2, 3:계층형 게시판 식별자 */
		final short sourceBoardID = 1;
		final short targetBoardID = 2;
		final String requestedUserIDForAdmin = "admin";
		final String requestedUserIDForMember = "test01";
		final String ip = "172.16.0.1";
		
		List<BoardWriteReq.NewAttachedFile> newAttachedFileListForWrite = new ArrayList<BoardWriteReq.NewAttachedFile>();
	
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMember);
		boardWriteReq.setIp(ip);
		boardWriteReq.setBoardID(sourceBoardID);
		boardWriteReq.setSubject("자유::테스트 주제");
		boardWriteReq.setContents("자유::내용::그림2 하나를 그리다");
		boardWriteReq.setNewAttachedFileCnt((short) newAttachedFileListForWrite
				.size());
		boardWriteReq.setNewAttachedFileList(newAttachedFileListForWrite);
		boardWriteReq.setPwdHashBase64("");
		
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

		BoardMoveReq boardMoveReq = new BoardMoveReq();
		boardMoveReq.setRequestedUserID(requestedUserIDForAdmin);
		boardMoveReq.setIp(ip);
		boardMoveReq.setSourceBoardID(boardReplyRes.getBoardID());
		boardMoveReq.setSourceBoardNo(boardReplyRes.getBoardNo());
		boardMoveReq.setTargetBoardID(targetBoardID);
		
		BoardMoveReqServerTask boardMoveReqServerTask = null;
		try {
			boardMoveReqServerTask = new BoardMoveReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		try {
			boardMoveReqServerTask.doWork(
					TEST_DBCP_NAME, boardMoveReq);
			
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String actualErrorMessage = e.getMessage();
			
			String expectedErrorMessage = "이동할 게시글이 그룹의 루트인 본문글이 아닙니다";
	
			assertEquals(expectedErrorMessage, actualErrorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 게시글이동_본문글1개_OK() {
		/** 자유 게시판 식별자:1, 이슈 게시판 식별자:2, 3:계층형 게시판 식별자 */
		final short sourceBoardID = 1;
		final short targetBoardID = 2;
		final String requestedUserIDForAdmin = "admin";
		final String requestedUserIDForMember = "test01";
		final String ip = "172.16.0.1";
		
		List<BoardWriteReq.NewAttachedFile> newAttachedFileListForWrite = new ArrayList<BoardWriteReq.NewAttachedFile>();
	
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestedUserID(requestedUserIDForMember);
		boardWriteReq.setIp(ip);
		boardWriteReq.setBoardID(sourceBoardID);
		boardWriteReq.setSubject("자유::테스트 주제");
		boardWriteReq.setContents("자유::내용::그림2 하나를 그리다");
		boardWriteReq.setNewAttachedFileCnt((short) newAttachedFileListForWrite
				.size());
		boardWriteReq.setNewAttachedFileList(newAttachedFileListForWrite);
		boardWriteReq.setPwdHashBase64("");
		
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
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}		

		BoardMoveReq boardMoveReq = new BoardMoveReq();
		boardMoveReq.setRequestedUserID(requestedUserIDForAdmin);
		boardMoveReq.setIp(ip);
		boardMoveReq.setSourceBoardID(boardWriteRes.getBoardID());
		boardMoveReq.setSourceBoardNo(boardWriteRes.getBoardNo());
		boardMoveReq.setTargetBoardID(targetBoardID);
		
		BoardMoveReqServerTask boardMoveReqServerTask = null;
		try {
			boardMoveReqServerTask = new BoardMoveReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		BoardMoveRes boardMoveRes = null;
		try {
			boardMoveRes = boardMoveReqServerTask.doWork(
					TEST_DBCP_NAME, boardMoveReq);		
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		log.warn(boardMoveRes.toString());
		
		List<BoardMoveRes.BoardMoveInfo> boardMoveInfoList = boardMoveRes.getBoardMoveInfoList();
		
		for (BoardMoveRes.BoardMoveInfo boardMoveInfo : boardMoveInfoList) {
			
			if (boardMoveInfo.getFromBoardNo() != boardWriteRes.getBoardNo()) {
				fail("이동할 게시글 번호 불일치");
			}
		}
	}
	
	@Test
	public void 게시글이동_시나리오1_OK() {
		final String ip = "172.16.0.1";
		String requestedUserIDForAdmin = "admin";
		
		BoardInfoAddReqServerTask boardInfoAddReqServerTask = null;
		try {
			boardInfoAddReqServerTask = new BoardInfoAddReqServerTask();
		} catch (DynamicClassCallException e) {
			fail("dead code");
		}
		
		BoardInfoAddReq boardInfoAddReq = new BoardInfoAddReq();
		boardInfoAddReq.setRequestedUserID(requestedUserIDForAdmin);
		boardInfoAddReq.setBoardName("계층1테스트");
		boardInfoAddReq.setBoardListType(BoardListType.TREE.getValue());
		boardInfoAddReq.setBoardReplyPolicyType(BoardReplyPolicyType.ALL.getValue());
		boardInfoAddReq.setBoardWritePermissionType(PermissionType.MEMBER.getValue());
		boardInfoAddReq.setBoardReplyPermissionType(PermissionType.MEMBER.getValue());
		
		BoardInfoAddRes treeType1BoardInfoAddRes = null;
		
		try {
			treeType1BoardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		
		boardInfoAddReq.setBoardName("계층2테스트");
		
		BoardInfoAddRes treeType2BoardInfoAddRes = null;
		
		try {
			treeType2BoardInfoAddRes = boardInfoAddReqServerTask.doWork(TEST_DBCP_NAME, boardInfoAddReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		
		class Tree1BoardTreeBuilder implements VirtualBoardTreeBuilderIF {
			@Override
			public BoardTree build(final short boardID) {
				String writerID = "test01";
				String otherID = "test02";

				BoardTree boardTree = new BoardTree();
				{
					BoardTreeNode root1BoardTreeNode = BoardTree
							.makeBoardTreeNodeWithoutTreeInfomation(boardID,
									writerID, "트리1::루트1", "트리1::루트1");

					{
						BoardTreeNode root1Child1BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, otherID, "트리1::루트1_자식1", "트리1::루트1_자식1");
						{
							BoardTreeNode root1Child1Child1BoardTreeNode = BoardTree
									.makeBoardTreeNodeWithoutTreeInfomation(
											boardID, otherID, "트리1::루트1_자식1_자식1",
											"트리1::루트1_자식1_자식1");
							{
								BoardTreeNode root1Child1Child1Child1BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, otherID,
												"트리1::루트1_자식1_자식1_자식1",
												"트리1::루트1_자식1_자식1_자식1");
								{
									BoardTreeNode root1Child1Child1Child1Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"트리1::루트1_자식1_자식1_자식1_자식1",
													"트리1::루트1_자식1_자식1_자식1_자식1");
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
										boardID, writerID, "트리1::루트1_자식2", "트리1::루트1_자식2");

						{
							BoardTreeNode root1Child2Child1BoardTreeNode = BoardTree
									.makeBoardTreeNodeWithoutTreeInfomation(
											boardID, otherID, "트리1::루트1_자식2_자식1",
											"트리1::루트1_자식2_자식1");
							{
								BoardTreeNode root1Child2Child1Child1BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"트리1::루트1_자식2_자식1_자식1",
												"트리1::루트1_자식2_자식1_자식1");

								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child1BoardTreeNode);
							}

							{
								BoardTreeNode root1Child2Child1Child2BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"트리1::루트1_자식2_자식1_자식2",
												"트리1::루트1_자식2_자식1_자식2");
								{
									BoardTreeNode root1Child2Child1Child2Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"트리1::루트1_자식2_자식1_자식2_자식1",
													"트리1::루트1_자식2_자식3_자식2_자식1");
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
												"트리1::루트1_자식2_자식1_자식3",
												"트리1::루트1_자식2_자식1_자식3");
								{
									BoardTreeNode root1Child2Child1Child3Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"트리1::루트1_자식2_자식1_자식3_자식1",
													"트리1::루트1_자식2_자식3_자식3_자식1");
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
										boardID, otherID, "트리1::루트1_자식3", "트리1::루트2_자식3");
						root1BoardTreeNode
								.addChildNode(root1Child3BoardTreeNode);
					}

					boardTree.addRootBoardTreeNode(root1BoardTreeNode);
				}
				
				{
					BoardTreeNode root1BoardTreeNode = BoardTree
							.makeBoardTreeNodeWithoutTreeInfomation(boardID,
									writerID, "트리1::루트2", "트리1::루트2");

					{
						BoardTreeNode root1Child1BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, otherID, "트리1::루트2_자식1", "트리1::루트2_자식1");
						{
							BoardTreeNode root1Child1Child1BoardTreeNode = BoardTree
									.makeBoardTreeNodeWithoutTreeInfomation(
											boardID, otherID, "트리1::루트2_자식1_자식1",
											"트리1::루트2_자식1_자식1");
							{
								BoardTreeNode root1Child1Child1Child1BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, otherID,
												"트리1::루트2_자식1_자식1_자식1",
												"트리1::루트2_자식1_자식1_자식1");
								{
									BoardTreeNode root1Child1Child1Child1Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"트리1::루트2_자식1_자식1_자식1_자식1",
													"트리1::루트2_자식1_자식1_자식1_자식1");
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

					boardTree.addRootBoardTreeNode(root1BoardTreeNode);
				}

				
				{
					BoardTreeNode root1BoardTreeNode = BoardTree
							.makeBoardTreeNodeWithoutTreeInfomation(boardID,
									writerID, "트리1::루트3", "트리1::루트3");

					{
						BoardTreeNode root1Child1BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, otherID, "트리1::루트3_자식1", "트리1::루트3_자식1");
						{
							BoardTreeNode root1Child1Child1BoardTreeNode = BoardTree
									.makeBoardTreeNodeWithoutTreeInfomation(
											boardID, otherID, "트리1::루트3_자식1_자식1",
											"트리1::루트3_자식1_자식1");
							{
								BoardTreeNode root1Child1Child1Child1BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, otherID,
												"트리1::루트3_자식1_자식1_자식1",
												"트리1::루트3_자식1_자식1_자식1");
								{
									BoardTreeNode root1Child1Child1Child1Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"트리1::루트3_자식1_자식1_자식1_자식1",
													"트리1::루트3_자식1_자식1_자식1_자식1");
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
										boardID, writerID, "트리1::루트3_자식2", "트리1::루트3_자식2");

						{
							BoardTreeNode root1Child2Child1BoardTreeNode = BoardTree
									.makeBoardTreeNodeWithoutTreeInfomation(
											boardID, otherID, "트리1::루트3_자식2_자식1",
											"트리1::루트3_자식2_자식1");
							{
								BoardTreeNode root1Child2Child1Child1BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"트리1::루트3_자식2_자식1_자식1",
												"트리1::루트3_자식2_자식1_자식1");

								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child1BoardTreeNode);
							}

							{
								BoardTreeNode root1Child2Child1Child2BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, writerID,
												"트리1::루트3_자식2_자식1_자식2",
												"트리1::루트3_자식2_자식1_자식2");
								{
									BoardTreeNode root1Child2Child1Child2Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"트리1::루트3_자식2_자식1_자식2_자식1",
													"트리1::루트3_자식2_자식3_자식2_자식1");
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
												"트리1::루트3_자식2_자식1_자식3",
												"트리1::루트3_자식2_자식1_자식3");
								{
									BoardTreeNode root1Child2Child1Child3Child1BoardTreeNode = BoardTree
											.makeBoardTreeNodeWithoutTreeInfomation(
													boardID, otherID,
													"트리1::루트3_자식2_자식1_자식3_자식1",
													"트리1::루트3_자식2_자식3_자식3_자식1");
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
										boardID, otherID, "트리1::루트3_자식3", "트리1::루트2_자식3");
						root1BoardTreeNode
								.addChildNode(root1Child3BoardTreeNode);
					}

					boardTree.addRootBoardTreeNode(root1BoardTreeNode);
				}
				return boardTree;
			}
		}
		
		class Tree2BoardTreeBuilder implements VirtualBoardTreeBuilderIF {
			@Override
			public BoardTree build(final short boardID) {
				String writerID = "test01";
				String otherID = "test02";

				BoardTree boardTree = new BoardTree();
				{
					BoardTreeNode root1BoardTreeNode = BoardTree
							.makeBoardTreeNodeWithoutTreeInfomation(boardID,
									writerID, "트리2::루트1", "트리2::루트1");

					{
						BoardTreeNode root1Child1BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, otherID, "트리2::루트1_자식1", "트리2::루트1_자식1");						
						root1BoardTreeNode
								.addChildNode(root1Child1BoardTreeNode);
					}					

					{
						BoardTreeNode root1Child3BoardTreeNode = BoardTree
								.makeBoardTreeNodeWithoutTreeInfomation(
										boardID, otherID, "트리2::루트1_자식3", "루트1_자식3");
						root1BoardTreeNode
								.addChildNode(root1Child3BoardTreeNode);
					}

					boardTree.addRootBoardTreeNode(root1BoardTreeNode);
				}

				return boardTree;
			}
		}
		
		VirtualBoardTreeBuilderIF tree1BoardTreeBuilder = new Tree1BoardTreeBuilder();
		BoardTree tree1BoardTree = tree1BoardTreeBuilder.build(treeType1BoardInfoAddRes.getBoardID());
		tree1BoardTree.makeDBRecord(TEST_DBCP_NAME);
		
		
		VirtualBoardTreeBuilderIF tree2BoardTreeBuilder = new Tree2BoardTreeBuilder();
		BoardTree tree2BoardTree = tree2BoardTreeBuilder.build(treeType2BoardInfoAddRes.getBoardID());
		tree2BoardTree.makeDBRecord(TEST_DBCP_NAME);
		
		
		BoardTreeNode boardTreeNode = tree1BoardTree.find("트리1::루트2");
		
		BoardMoveReq boardMoveReq = new BoardMoveReq();
		boardMoveReq.setRequestedUserID(requestedUserIDForAdmin);
		boardMoveReq.setIp(ip);
		boardMoveReq.setSourceBoardID(boardTreeNode.getBoardID());
		boardMoveReq.setSourceBoardNo(boardTreeNode.getBoardNo());
		boardMoveReq.setTargetBoardID(treeType2BoardInfoAddRes.getBoardID());
		
		BoardMoveReqServerTask boardMoveReqServerTask = null;
		try {
			boardMoveReqServerTask = new BoardMoveReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		
		BoardMoveRes boardMoveRes = null;
		try {
			boardMoveRes = boardMoveReqServerTask.doWork(
					TEST_DBCP_NAME, boardMoveReq);		
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		log.warn(boardMoveRes.toString());
		
		List<BoardMoveRes.BoardMoveInfo> boardMoveInfoList = boardMoveRes.getBoardMoveInfoList();

/* 이동 전 게시글 번호 -> 이동 후 게시글 번호
14 -> 4
15 -> 5
16 -> 6
17 -> 7
18 -> 8
*/
		for (BoardMoveRes.BoardMoveInfo boardMoveInfo : boardMoveInfoList) {			
			if (boardMoveInfo.getFromBoardNo() == 14) {
				long expectedToBoardNo = 4;
				if (boardMoveInfo.getToBoardNo() != expectedToBoardNo) {
					fail("예상된 이동 후  게시글 번호["+expectedToBoardNo+"] 와 실제 얻어온 이동 후 게시글 번호 ["+boardMoveInfo.getToBoardNo()+"]가 불일치합니다");
				}
			} else if (boardMoveInfo.getFromBoardNo() == 15) {
				long expectedToBoardNo = 5;
				if (boardMoveInfo.getToBoardNo() != expectedToBoardNo) {
					fail("예상된 이동 후  게시글 번호["+expectedToBoardNo+"] 와 실제 얻어온 이동 후 게시글 번호 ["+boardMoveInfo.getToBoardNo()+"]가 불일치합니다");
				}
			} else if (boardMoveInfo.getFromBoardNo() == 16) {
				long expectedToBoardNo = 6;
				if (boardMoveInfo.getToBoardNo() != expectedToBoardNo) {
					fail("예상된 이동 후  게시글 번호["+expectedToBoardNo+"] 와 실제 얻어온 이동 후 게시글 번호 ["+boardMoveInfo.getToBoardNo()+"]가 불일치합니다");
				}
			} else if (boardMoveInfo.getFromBoardNo() == 17) {
				long expectedToBoardNo = 7;
				if (boardMoveInfo.getToBoardNo() != expectedToBoardNo) {
					fail("예상된 이동 후  게시글 번호["+expectedToBoardNo+"] 와 실제 얻어온 이동 후 게시글 번호 ["+boardMoveInfo.getToBoardNo()+"]가 불일치합니다");
				}
			} else if (boardMoveInfo.getFromBoardNo() == 18) {
				long expectedToBoardNo = 8;
				if (boardMoveInfo.getToBoardNo() != expectedToBoardNo) {
					fail("예상된 이동 후  게시글 번호["+expectedToBoardNo+"] 와 실제 얻어온 이동 후 게시글 번호 ["+boardMoveInfo.getToBoardNo()+"]가 불일치합니다");
				}
			} else {
				fail("예상 범위 벗어난 게시글 번호["+boardMoveInfo.getFromBoardNo()+"]");
			}
		}
		
	}
}
