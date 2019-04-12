package kr.pe.codda.impl.task.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import junitlib.AbstractBoardTest;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.BoardBlockReq.BoardBlockReq;
import kr.pe.codda.impl.message.BoardDeleteReq.BoardDeleteReq;
import kr.pe.codda.impl.message.BoardDetailReq.BoardDetailReq;
import kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes;
import kr.pe.codda.impl.message.BoardListReq.BoardListReq;
import kr.pe.codda.impl.message.BoardListRes.BoardListRes;
import kr.pe.codda.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.codda.impl.message.BoardReplyRes.BoardReplyRes;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.BoardTree;
import kr.pe.codda.server.lib.BoardTreeNode;
import kr.pe.codda.server.lib.VirtualBoardTreeBuilderIF;

public class BoardBlockReqServerTaskTest extends AbstractBoardTest {

	@Test
	public void 게시글차단_손님() {
		final short boardID = 3;
		String requestedUserIDForMember = "test01";
		String requestedUserIDForGuest = "guest";
		
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
		boardBlockReq.setIp("127.0.0.3");
		boardBlockReq.setRequestedUserID(requestedUserIDForGuest);
		boardBlockReq.setBoardID(blockSateBoardWriteRes.getBoardID());
		boardBlockReq.setBoardNo(blockSateBoardWriteRes.getBoardNo());
		
	
		try {
			boardBlockReqServerTask.doWork(TEST_DBCP_NAME, boardBlockReq);
	
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
	
			String expectedErrorMessage = "게시글 차단 서비스는 관리자 전용 서비스입니다";
	
			assertEquals("게시글 차단 기능 호출자 관리자 여부 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글차단_일반유저() {
		final short boardID = 3;
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
		boardBlockReq.setRequestedUserID(requestedUserIDForMember);
		boardBlockReq.setIp("127.0.0.8");
		boardBlockReq.setBoardID(blockSateBoardWriteRes.getBoardID());
		boardBlockReq.setBoardNo(blockSateBoardWriteRes.getBoardNo());
		
	
		try {
			boardBlockReqServerTask.doWork(TEST_DBCP_NAME, boardBlockReq);
	
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
	
			String expectedErrorMessage = "게시글 차단 서비스는 관리자 전용 서비스입니다";
	
			assertEquals("게시글 차단 기능 호출자 관리자 여부 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글차단_잘못된게시판식별자() {
		String requestedUserIDForAdmin = "admin";
		short badBoardID = 7;
	
		BoardBlockReqServerTask boardBlockReqServerTask = null;
		try {
			boardBlockReqServerTask = new BoardBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardBlockReq boardBlockReq = new BoardBlockReq();
		boardBlockReq.setRequestedUserID(requestedUserIDForAdmin);
		boardBlockReq.setIp("127.0.0.8");
		boardBlockReq.setBoardID(badBoardID);
		boardBlockReq.setBoardNo(1);
	
		try {
			boardBlockReqServerTask.doWork(TEST_DBCP_NAME, boardBlockReq);
	
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
	
			String expectedErrorMessage = new StringBuilder("입력 받은 게시판 식별자[").append(badBoardID)
					.append("]가 게시판 정보 테이블에 존재하지  않습니다").toString();
	
			assertEquals("잘못된 게시판 식별자 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글차단_대상글없음() {
		final short boardID = 3;
		String requestedUserIDForAdmin = "admin";
	
		BoardBlockReqServerTask boardBlockReqServerTask = null;
		try {
			boardBlockReqServerTask = new BoardBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardBlockReq boardBlockReq = new BoardBlockReq();
		boardBlockReq.setRequestedUserID(requestedUserIDForAdmin);
		boardBlockReq.setIp("172.8.0.11");
		boardBlockReq.setBoardID(boardID);
		boardBlockReq.setBoardNo(1);
		
	
		try {
			boardBlockReqServerTask.doWork(TEST_DBCP_NAME, boardBlockReq);
	
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
	
			String expectedErrorMessage = new StringBuilder()
					.append("해당 게시글[boardID=")
					.append(boardBlockReq.getBoardID())
					.append(", boardNo=")
					.append(boardBlockReq.getBoardNo())
					.append("]이 존재 하지 않습니다").toString();
	
			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글차단_삭제된글차단() {
		final short boardID = 3;
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

		BoardWriteRes delteSateBoardWriteRes = null;
		try {
			delteSateBoardWriteRes = boardWriteReqServerTask.doWork(
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
		boardDeleteReq.setIp("172.1.3.17");
		boardDeleteReq.setBoardID(delteSateBoardWriteRes.getBoardID());
		boardDeleteReq.setBoardNo(delteSateBoardWriteRes.getBoardNo());
		

		try {
			boardDeleteReqServerTask.doWork(TEST_DBCP_NAME, boardDeleteReq);
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
		boardBlockReq.setIp("172.1.3.17");
		boardBlockReq.setBoardID(delteSateBoardWriteRes.getBoardID());
		boardBlockReq.setBoardNo(delteSateBoardWriteRes.getBoardNo());

		try {
			boardBlockReqServerTask.doWork(TEST_DBCP_NAME, boardBlockReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "해당 게시글은 삭제된 글입니다";

			assertEquals("이미 삭제된 글 차단할때 경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시글차단_차단된글재차단() {
		final short boardID = 3;
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

		BoardWriteRes delteSateBoardWriteRes = null;
		try {
			delteSateBoardWriteRes = boardWriteReqServerTask.doWork(
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
		boardBlockReq.setIp("172.2.3.1");
		boardBlockReq.setBoardID(delteSateBoardWriteRes.getBoardID());
		boardBlockReq.setBoardNo(delteSateBoardWriteRes.getBoardNo());
		

		try {
			boardBlockReqServerTask.doWork(TEST_DBCP_NAME, boardBlockReq);
		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		try {
			boardBlockReqServerTask.doWork(TEST_DBCP_NAME, boardBlockReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "해당 게시글은 관리자에 의해 차단된 글입니다";

			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}
	
	@Test
	public void 게시글차단_차단된글에속한글차단() {
		final short boardID = 3;
		
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
	
		try {
			BoardBlockReq boardBlockReq = new BoardBlockReq();
			boardBlockReq.setRequestedUserID(requestedUserIDForAdmin);
			boardBlockReq.setIp("172.4.0.11");
			boardBlockReq.setBoardID(boardWriteRes.getBoardID());
			boardBlockReq.setBoardNo(boardWriteRes.getBoardNo());
			
			MessageResultRes messageResultRes = boardBlockReqServerTask.doWork(
					TEST_DBCP_NAME, boardBlockReq);
			log.info(messageResultRes.toString());		
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		try {
			BoardBlockReq boardBlockReq = new BoardBlockReq();
			boardBlockReq.setRequestedUserID(requestedUserIDForAdmin);
			boardBlockReq.setIp("172.4.0.15");
			boardBlockReq.setBoardID(boardReplyRes.getBoardID());
			boardBlockReq.setBoardNo(boardReplyRes.getBoardNo());
			
			boardBlockReqServerTask.doWork(
					TEST_DBCP_NAME, boardBlockReq);
			
			fail("no ServerServiceException");
		} catch(ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = "해당 게시글은 관리자에 의해 차단된 글에 속한 글입니다";

			assertEquals(expectedErrorMessage, errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
	}

	@Test
	public void 게시판차단_블락포함된블락_OK() {
		final short boardID = 3;

		class VirtualBoardTreeBuilder implements VirtualBoardTreeBuilderIF {
			@Override
			public BoardTree build(final short boardID) {
				String requestedUserIDForMember = "test01";
				String otherID = "test02";

				BoardTree boardTree = new BoardTree();
				{
					BoardTreeNode root1BoardTreeNode = BoardTree
							.makeBoardTreeNodeWithoutTreeInfomation(boardID,
									requestedUserIDForMember, "루트1", "루트1");

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
										boardID, requestedUserIDForMember, "루트1_자식2", "루트1_자식2");

						{
							BoardTreeNode root1Child2Child1BoardTreeNode = BoardTree
									.makeBoardTreeNodeWithoutTreeInfomation(
											boardID, otherID, "루트1_자식2_자식1",
											"루트1_자식2_자식1");
							{
								BoardTreeNode root1Child2Child1Child1BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, requestedUserIDForMember,
												"루트1_자식2_자식1_자식1",
												"루트1_자식2_자식1_자식1");

								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child1BoardTreeNode);
							}

							{
								BoardTreeNode root1Child2Child1Child2BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, requestedUserIDForMember,
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
												boardID, requestedUserIDForMember,
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

		BoardTreeNode firstBlockBoardTreeNode = boardTree
				.find("루트1_자식2_자식1_자식2");

		if (null == firstBlockBoardTreeNode) {
			fail("첫번째 블락 대상 글(제목:루트1_자식2_자식1_자식2) 찾기 실패");
		}

		BoardTreeNode deleteBoardTreeNode = boardTree
				.find("루트1_자식2_자식1_자식2_자식1");

		if (null == deleteBoardTreeNode) {
			fail("삭제 대상 글(제목:루트1_자식2_자식1_자식2_자식1) 찾기 실패");
		}

		BoardTreeNode secondBlockBoardTreeNode = boardTree.find("루트1_자식2");

		if (null == secondBlockBoardTreeNode) {
			fail("두번째 블락 대상 글(제목:루트1_자식2) 찾기 실패");
		}

		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();		
		boardDeleteReq.setRequestedUserID(deleteBoardTreeNode.getWriterID());
		boardDeleteReq.setIp("172.1.3.14");
		boardDeleteReq.setBoardID(deleteBoardTreeNode.getBoardID());
		boardDeleteReq.setBoardNo(deleteBoardTreeNode.getBoardNo());
		

		try {
			MessageResultRes messageResultRes = boardDeleteReqServerTask
					.doWork(TEST_DBCP_NAME, boardDeleteReq);
			if (!messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}
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

		BoardBlockReq firstBlcokBoardBlockReq = new BoardBlockReq();
		firstBlcokBoardBlockReq.setRequestedUserID("admin");
		firstBlcokBoardBlockReq.setIp("172.3.0.1");
		firstBlcokBoardBlockReq
				.setBoardID(firstBlockBoardTreeNode.getBoardID());
		firstBlcokBoardBlockReq
				.setBoardNo(firstBlockBoardTreeNode.getBoardNo());

		try {
			MessageResultRes messageResultRes = boardBlockReqServerTask.doWork(
					TEST_DBCP_NAME, firstBlcokBoardBlockReq);

			// log.info(messageResultRes.toString());

			if (!messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}

		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}

		int pageNo = 1;
		int pageSize = boardTree.getHashSize();

		BoardListReq boardListReq = new BoardListReq();
		boardListReq.setRequestedUserID("admin");
		boardListReq.setBoardID(boardID);
		boardListReq.setPageNo(pageNo);
		boardListReq.setPageSize(pageSize);

		BoardListReqServerTask boardListReqServerTask = null;
		try {
			boardListReqServerTask = new BoardListReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardListRes boardListRes = null;

		try {
			boardListRes = boardListReqServerTask.doWork(TEST_DBCP_NAME,
					boardListReq);

		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		for (BoardListRes.Board board : boardListRes.getBoardList()) {
			byte boardState = board.getBoardSate();
			int groupSeq = board.getGroupSeq();

			if (firstBlockBoardTreeNode.getGroupSeq() == groupSeq) {
				assertEquals("첫번째 블락 대상 게시글 상태 점검",
						BoardStateType.BLOCK.getValue(), boardState);
			} else if (deleteBoardTreeNode.getGroupSeq() == groupSeq) {
				assertEquals("삭제 대상 게시글 상태 점검",
						BoardStateType.DELETE.getValue(), boardState);
			} else if ((firstBlockBoardTreeNode.getGroupSeq() - 1) <= groupSeq
					&& groupSeq < firstBlockBoardTreeNode.getGroupSeq()) {
				assertEquals(
						"첫번째 블락 트리에서 블락과 삭제 상태의 노드를 제외한 나머지 글들의 게시글 상태 점검",
						BoardStateType.TREEBLOCK.getValue(), boardState);
			} else {
				assertEquals("첫번째 블락 트리 제외한 글들의 게시글 상태 점검",
						BoardStateType.OK.getValue(), boardState);
			}
		}

		BoardBlockReq secondBlcokBoardBlockReq = new BoardBlockReq();
		secondBlcokBoardBlockReq.setRequestedUserID("admin");
		secondBlcokBoardBlockReq.setIp("172.3.0.2");
		secondBlcokBoardBlockReq.setBoardID(secondBlockBoardTreeNode
				.getBoardID());
		secondBlcokBoardBlockReq.setBoardNo(secondBlockBoardTreeNode
				.getBoardNo());

		try {
			MessageResultRes messageResultRes = boardBlockReqServerTask.doWork(
					TEST_DBCP_NAME, secondBlcokBoardBlockReq);

			log.info(messageResultRes.toString());

			if (!messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}

		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}

		try {
			boardListRes = boardListReqServerTask.doWork(TEST_DBCP_NAME,
					boardListReq);

		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		for (BoardListRes.Board board : boardListRes.getBoardList()) {
			byte boardState = board.getBoardSate();
			int groupSeq = board.getGroupSeq();

			if (firstBlockBoardTreeNode.getGroupSeq() == groupSeq) {
				assertEquals("첫번째 블락 대상 게시글 상태 점검",
						BoardStateType.BLOCK.getValue(), boardState);
			} else if (deleteBoardTreeNode.getGroupSeq() == groupSeq) {
				assertEquals("삭제 대상 게시글 상태 점검",
						BoardStateType.DELETE.getValue(), boardState);
			} else if (secondBlockBoardTreeNode.getGroupSeq() == groupSeq) {
				assertEquals("두번째 블락 대상 게시글 상태 점검",
						BoardStateType.BLOCK.getValue(), boardState);
			} else if ((secondBlockBoardTreeNode.getGroupSeq() - 6) <= groupSeq
					&& groupSeq < secondBlockBoardTreeNode.getGroupSeq()) {
				assertEquals(
						"두번째 최종 블락 트리에서 블락과 삭제 상태의 노드를 제외한 나머지 글들의 게시글 상태 점검",
						BoardStateType.TREEBLOCK.getValue(), boardState);
			} else {
				assertEquals("두번째 블락 트리를 제외한 글들의 게시글 상태 점검",
						BoardStateType.OK.getValue(), boardState);
			}
		}
	}

	@Test
	public void 게시글차단_루트_OK() {
		final short boardID = 3;
		String requestedUserIDForGuest = "guest";
		
		class VirtualBoardTreeBuilder implements VirtualBoardTreeBuilderIF {
			@Override
			public BoardTree build(final short boardID) {
				String requestedUserIDForMember = "test01";
				String otherID = "test02";

				BoardTree boardTree = new BoardTree();
				{
					BoardTreeNode root1BoardTreeNode = BoardTree
							.makeBoardTreeNodeWithoutTreeInfomation(boardID,
									requestedUserIDForMember, "루트1", "루트1");

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
										boardID, requestedUserIDForMember, "루트1_자식2", "루트1_자식2");

						{
							BoardTreeNode root1Child2Child1BoardTreeNode = BoardTree
									.makeBoardTreeNodeWithoutTreeInfomation(
											boardID, otherID, "루트1_자식2_자식1",
											"루트1_자식2_자식1");
							{
								BoardTreeNode root1Child2Child1Child1BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, requestedUserIDForMember,
												"루트1_자식2_자식1_자식1",
												"루트1_자식2_자식1_자식1");

								root1Child2Child1BoardTreeNode
										.addChildNode(root1Child2Child1Child1BoardTreeNode);
							}

							{
								BoardTreeNode root1Child2Child1Child2BoardTreeNode = BoardTree
										.makeBoardTreeNodeWithoutTreeInfomation(
												boardID, requestedUserIDForMember,
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
												boardID, requestedUserIDForMember,
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

		BoardTreeNode blockBoardTreeNode = boardTree.find("루트1_자식1_자식1_자식1");

		if (null == blockBoardTreeNode) {
			fail("목표 게시글(제목:루트1_자식1_자식1_자식1) 찾기 실패");
		}

		BoardTreeNode treeBlock1BoardTreeNode = boardTree
				.find("루트1_자식1_자식1_자식1_자식1");

		if (null == treeBlock1BoardTreeNode) {
			fail("목표 게시글(제목:루트1_자식1_자식1_자식1_자식1) 찾기 실패");
		}

		BoardBlockReq blcokBoardBlockReq = new BoardBlockReq();
		blcokBoardBlockReq.setRequestedUserID("admin");
		blcokBoardBlockReq.setBoardID(blockBoardTreeNode.getBoardID());
		blcokBoardBlockReq.setBoardNo(blockBoardTreeNode.getBoardNo());
		blcokBoardBlockReq.setIp("127.0.0.9");

		BoardBlockReqServerTask boardBlockReqServerTask = null;
		try {
			boardBlockReqServerTask = new BoardBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		try {
			MessageResultRes messageResultRes = boardBlockReqServerTask.doWork(
					TEST_DBCP_NAME, blcokBoardBlockReq);

			log.info(messageResultRes.toString());

			if (!messageResultRes.getIsSuccess()) {
				fail(messageResultRes.getResultMessage());
			}

		} catch (Exception e) {
			log.warn("fail to execuate doTask", e);
			fail("fail to execuate doTask");
		}

		{
			BoardDetailReq boardDetailReq = new BoardDetailReq();
			boardDetailReq.setBoardID(blockBoardTreeNode.getBoardID());
			boardDetailReq.setBoardNo(blockBoardTreeNode.getBoardNo());
			boardDetailReq.setRequestedUserID("admin");

			BoardDetailReqServerTask boardDetailReqServerTask = null;
			try {
				boardDetailReqServerTask = new BoardDetailReqServerTask();
			} catch (DynamicClassCallException e1) {
				fail("dead code");
			}

			try {
				BoardDetailRes boardDetailRes = boardDetailReqServerTask
						.doWork(TEST_DBCP_NAME, boardDetailReq);

				log.info(boardDetailRes.toString());

				assertEquals("게시판 상태 비교", BoardStateType.BLOCK.getValue(),
						boardDetailRes.getBoardSate());

			} catch (Exception e) {
				log.warn("fail to execuate doTask", e);
				fail("fail to execuate doTask");
			}
		}

		{
			BoardDetailReq boardDetailReq = new BoardDetailReq();
			boardDetailReq.setBoardID(treeBlock1BoardTreeNode.getBoardID());
			boardDetailReq.setBoardNo(treeBlock1BoardTreeNode.getBoardNo());
			boardDetailReq.setRequestedUserID("admin");

			BoardDetailReqServerTask boardDetailReqServerTask = null;
			try {
				boardDetailReqServerTask = new BoardDetailReqServerTask();
			} catch (DynamicClassCallException e1) {
				fail("dead code");
			}
			try {
				BoardDetailRes boardDetailRes = boardDetailReqServerTask
						.doWork(TEST_DBCP_NAME, boardDetailReq);

				log.info(boardDetailRes.toString());

				assertEquals("게시판 상태 비교", BoardStateType.TREEBLOCK.getValue(),
						boardDetailRes.getBoardSate());

			} catch (Exception e) {
				log.warn("fail to execuate doTask", e);
				fail("fail to execuate doTask");
			}
		}

		int pageNo = 1;
		int pageSize = boardTree.getHashSize();

		BoardListReq boardListReq = new BoardListReq();
		boardListReq.setRequestedUserID(requestedUserIDForGuest);
		boardListReq.setBoardID(boardID);
		boardListReq.setPageNo(pageNo);
		boardListReq.setPageSize(pageSize);

		BoardListReqServerTask boardListReqServerTask = null;
		try {
			boardListReqServerTask = new BoardListReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardListRes firstBoardListRes = null;

		try {
			firstBoardListRes = boardListReqServerTask.doWork(TEST_DBCP_NAME,
					boardListReq);

		} catch (ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		assertEquals("게시판 트리 노드 총 갯수와 게시판 레코드 총 갯수 비교",
				boardTree.getHashSize() - 2, firstBoardListRes.getTotal());
	}


}
