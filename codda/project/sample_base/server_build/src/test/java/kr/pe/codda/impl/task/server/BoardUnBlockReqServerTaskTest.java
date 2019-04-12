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
import kr.pe.codda.impl.message.BoardUnBlockReq.BoardUnBlockReq;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.BoardTree;
import kr.pe.codda.server.lib.BoardTreeNode;
import kr.pe.codda.server.lib.VirtualBoardTreeBuilderIF;

public class BoardUnBlockReqServerTaskTest extends AbstractBoardTest {

	@Test
	public void 게시판해제_손님() {
		final short boardID = 3;
		String requestedUserIDForGuest = "guest";
	
		BoardUnBlockReqServerTask boardUnBlockReqServerTask = null;
		try {
			boardUnBlockReqServerTask = new BoardUnBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardUnBlockReq boardUnBlockReq = new BoardUnBlockReq();
		boardUnBlockReq.setRequestedUserID(requestedUserIDForGuest);
		boardUnBlockReq.setIp("172.8.0.12");
		boardUnBlockReq.setBoardID(boardID);
		boardUnBlockReq.setBoardNo(1);
	
		try {
			boardUnBlockReqServerTask.doWork(TEST_DBCP_NAME, boardUnBlockReq);
	
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
	
			String expectedErrorMessage = "게시글 차단 해제 서비스는 관리자 전용 서비스입니다";
	
			assertEquals("게시글 해제 기능 호출자 관리자 여부 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판해제_일반유저() {
		final short boardID = 3;
		String requestedUserIDForMember = "test01";
	
		BoardUnBlockReqServerTask boardUnBlockReqServerTask = null;
		try {
			boardUnBlockReqServerTask = new BoardUnBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardUnBlockReq boardUnBlockReq = new BoardUnBlockReq();
		boardUnBlockReq.setRequestedUserID(requestedUserIDForMember);
		boardUnBlockReq.setIp("172.3.0.19");
		boardUnBlockReq.setBoardID(boardID);
		boardUnBlockReq.setBoardNo(1);
		
	
		try {
			boardUnBlockReqServerTask.doWork(TEST_DBCP_NAME, boardUnBlockReq);
	
			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
	
			String expectedErrorMessage = "게시글 차단 해제 서비스는 관리자 전용 서비스입니다";
	
			assertEquals("게시글 해제 기능 호출자 관리자 여부 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판해제_잘못된게시판식별자() {
		String requestedUserIDForAdmin = "admin";
		short badBoardID = 7;

		BoardUnBlockReqServerTask boardUnBlockReqServerTask = null;
		try {
			boardUnBlockReqServerTask = new BoardUnBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardUnBlockReq boardUnBlockReq = new BoardUnBlockReq();
		boardUnBlockReq.setRequestedUserID(requestedUserIDForAdmin);
		boardUnBlockReq.setIp("172.3.0.20");
		boardUnBlockReq.setBoardID(badBoardID);
		boardUnBlockReq.setBoardNo(1);

		try {
			boardUnBlockReqServerTask.doWork(TEST_DBCP_NAME, boardUnBlockReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = new StringBuilder("입력 받은 게시판 식별자[").append(badBoardID)
					.append("]가 게시판 정보 테이블에 존재하지  않습니다").toString();

			assertEquals("해제 대상 글이 없을때  경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판해제_대상글없음() {
		final short boardID = 3;
		String requestedUserIDForAdmin = "admin";

		BoardUnBlockReqServerTask boardUnBlockReqServerTask = null;
		try {
			boardUnBlockReqServerTask = new BoardUnBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardUnBlockReq boardUnBlockReq = new BoardUnBlockReq();
		boardUnBlockReq.setRequestedUserID(requestedUserIDForAdmin);
		boardUnBlockReq.setIp("172.16.0.1");
		boardUnBlockReq.setBoardID(boardID);
		boardUnBlockReq.setBoardNo(1);
		

		try {
			boardUnBlockReqServerTask.doWork(TEST_DBCP_NAME, boardUnBlockReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = new StringBuilder()
					.append("해당 게시글[boardID=")
					.append(boardUnBlockReq.getBoardID())
					.append(", boardNo=")
					.append(boardUnBlockReq.getBoardNo())
					.append("]이 존재 하지 않습니다").toString();

			assertEquals("해제 대상 글이 없을때  경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판해제_블락이아닌글_정상삭제혹은트리블락() {
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
		boardWriteReq.setSubject("제목1");
		boardWriteReq.setContents("내용1");
		

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

		BoardUnBlockReqServerTask boardUnBlockReqServerTask = null;
		try {
			boardUnBlockReqServerTask = new BoardUnBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardUnBlockReq boardUnBlockReq = new BoardUnBlockReq();
		boardUnBlockReq.setRequestedUserID(requestedUserIDForAdmin);
		boardUnBlockReq.setIp("172.1.3.20");
		boardUnBlockReq.setBoardID(boardWriteRes.getBoardID());
		boardUnBlockReq.setBoardNo(boardWriteRes.getBoardNo());
		

		try {
			boardUnBlockReqServerTask.doWork(TEST_DBCP_NAME, boardUnBlockReq);

			fail("no ServerServiceException");
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();

			String expectedErrorMessage = new StringBuilder().append("차단된 글[")
					.append(BoardStateType.OK.getName()).append("]이 아닙니다")
					.toString();

			assertEquals("해제 대상 글이 없을때  경고 메시지인지 검사", expectedErrorMessage,
					errorMessage);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
	}

	@Test
	public void 게시판해제_블락포함된블락() {
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

		BoardTreeNode secondBlockBoardTreeNode = boardTree.find("루트1_자식2");

		if (null == secondBlockBoardTreeNode) {
			fail("두번째 블락 대상 글(제목:루트1_자식2) 찾기 실패");
		}

		BoardTreeNode deleteBoardTreeNode = boardTree
				.find("루트1_자식2_자식1_자식2_자식1");

		if (null == deleteBoardTreeNode) {
			fail("삭제 대상 글(제목:루트1_자식2_자식1_자식2_자식1) 찾기 실패");
		}

		BoardDeleteReqServerTask boardDeleteReqServerTask = null;
		try {
			boardDeleteReqServerTask = new BoardDeleteReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(deleteBoardTreeNode.getWriterID());
		boardDeleteReq.setIp("172.3.4.11");
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
		firstBlcokBoardBlockReq.setIp("172.3.4.12");
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

		BoardBlockReq secondBlcokBoardBlockReq = new BoardBlockReq();
		secondBlcokBoardBlockReq.setRequestedUserID("admin");
		secondBlcokBoardBlockReq.setIp("172.3.4.12");
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

		BoardUnBlockReqServerTask boardUnBlockReqServerTask = null;
		try {
			boardUnBlockReqServerTask = new BoardUnBlockReqServerTask();
		} catch (DynamicClassCallException e1) {
			fail("dead code");
		}

		BoardUnBlockReq boardUnBlockReq = new BoardUnBlockReq();
		boardUnBlockReq.setRequestedUserID("admin");
		boardUnBlockReq.setIp("172.3.4.13");
		boardUnBlockReq.setBoardID(secondBlockBoardTreeNode.getBoardID());
		boardUnBlockReq.setBoardNo(secondBlockBoardTreeNode.getBoardNo());

		try {
			boardUnBlockReqServerTask.doWork(TEST_DBCP_NAME, boardUnBlockReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
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

		boardUnBlockReq = new BoardUnBlockReq();
		boardUnBlockReq.setRequestedUserID("admin");
		boardUnBlockReq.setIp("172.3.4.14");
		boardUnBlockReq.setBoardID(firstBlockBoardTreeNode.getBoardID());
		boardUnBlockReq.setBoardNo(firstBlockBoardTreeNode.getBoardNo());

		try {
			boardUnBlockReqServerTask.doWork(TEST_DBCP_NAME, boardUnBlockReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
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

			if (deleteBoardTreeNode.getGroupSeq() == groupSeq) {
				assertEquals("삭제 대상 게시글 상태 점검",
						BoardStateType.DELETE.getValue(), boardState);
			} else {
				assertEquals("게시글 상태 점검", BoardStateType.OK.getValue(),
						boardState);
			}
		}
	}

	@Test
	public void 게시판해제_정상() {
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

		BoardTreeNode blockBoardTreeNode = boardTree.find("루트1_자식2_자식1_자식3");

		if (null == blockBoardTreeNode) {
			fail("목표 게시글(제목:루트1_자식2_자식1_자식3) 찾기 실패");
		}

		BoardTreeNode treeBlock1BoardTreeNode = boardTree
				.find("루트1_자식2_자식1_자식3_자식1");

		if (null == treeBlock1BoardTreeNode) {
			fail("목표 게시글(제목:루트1_자식2_자식1_자식3_자식1) 찾기 실패");
		}

		BoardBlockReq blcokBoardBlockReq = new BoardBlockReq();
		blcokBoardBlockReq.setRequestedUserID("admin");
		blcokBoardBlockReq.setIp("172.3.4.15");
		blcokBoardBlockReq.setBoardID(blockBoardTreeNode.getBoardID());
		blcokBoardBlockReq.setBoardNo(blockBoardTreeNode.getBoardNo());

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
			boardDetailReq.setRequestedUserID("admin");
			boardDetailReq.setBoardID(blockBoardTreeNode.getBoardID());
			boardDetailReq.setBoardNo(blockBoardTreeNode.getBoardNo());
			

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
			boardDetailReq.setRequestedUserID("admin");
			boardDetailReq.setBoardID(treeBlock1BoardTreeNode.getBoardID());
			boardDetailReq.setBoardNo(treeBlock1BoardTreeNode.getBoardNo());
			

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

		BoardUnBlockReqServerTask boardUnBlockReqServerTask = null;
		try {
			boardUnBlockReqServerTask = new BoardUnBlockReqServerTask();
		} catch (DynamicClassCallException e2) {
			fail("dead code");
		}
		BoardUnBlockReq boardUnBlockReq = new BoardUnBlockReq();
		boardUnBlockReq.setRequestedUserID("admin");
		boardUnBlockReq.setIp("172.3.4.16");
		boardUnBlockReq.setBoardID(blockBoardTreeNode.getBoardID());
		boardUnBlockReq.setBoardNo(blockBoardTreeNode.getBoardNo());

		try {
			boardUnBlockReqServerTask.doWork(TEST_DBCP_NAME, boardUnBlockReq);
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}

		{
			BoardDetailReq boardDetailReq = new BoardDetailReq();
			boardDetailReq.setRequestedUserID("test01");
			boardDetailReq.setBoardID(blockBoardTreeNode.getBoardID());
			boardDetailReq.setBoardNo(blockBoardTreeNode.getBoardNo());
			

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

				assertEquals("게시판 상태 비교", BoardStateType.OK.getValue(),
						boardDetailRes.getBoardSate());

			} catch (Exception e) {
				log.warn("fail to execuate doTask", e);
				fail("fail to execuate doTask");
			}
		}

		{
			BoardDetailReq boardDetailReq = new BoardDetailReq();
			boardDetailReq.setRequestedUserID("test01");
			boardDetailReq.setBoardID(treeBlock1BoardTreeNode.getBoardID());
			boardDetailReq.setBoardNo(treeBlock1BoardTreeNode.getBoardNo());
			

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

				assertEquals("게시판 상태 비교", BoardStateType.OK.getValue(),
						boardDetailRes.getBoardSate());

			} catch (Exception e) {
				log.warn("fail to execuate doTask", e);
				fail("fail to execuate doTask");
			}
		}
	}
	

}
