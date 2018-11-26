package kr.pe.codda.server.lib;

import static kr.pe.codda.impl.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardVoteTb.SB_BOARD_VOTE_TB;
import static kr.pe.codda.impl.jooq.tables.SbSeqTb.SB_SEQ_TB;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.sql.DataSource;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.DBCPDataSourceNotFoundException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.codda.impl.message.BoardReplyRes.BoardReplyRes;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.impl.task.server.BoardReplyReqServerTask;
import kr.pe.codda.impl.task.server.BoardWriteReqServerTask;
import kr.pe.codda.server.dbcp.DBCPManager;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ServerDBUtilTest extends AbstractJunitTest {
	private final static String TEST_DBCP_NAME = ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AbstractJunitTest.setUpBeforeClass();
		ServerDBUtil.initializeDBEnvoroment(TEST_DBCP_NAME);		
	}
	
	@Before
	public void setUp() {
		UByte freeBoardSequenceID = UByte.valueOf(SequenceType.FREE_BOARD.getSequenceID());
		
		{
			String userID = "admin";
			byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
			String nickname = "단위테스터용어드민";
			String pwdHint = "힌트 그것이 알고싶다";
			String pwdAnswer = "힌트답변 말이여 방구여";
			String ip = "127.0.0.1";
			
			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberType.ADMIN, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
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
			String userID = "test01";
			byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
			String nickname = "단위테스터용아이디1";
			String pwdHint = "힌트 그것이 알고싶다";
			String pwdAnswer = "힌트답변 말이여 방구여";
			String ip = "127.0.0.1";
			
			try {
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberType.USER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
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
				ServerDBUtil.registerMember(TEST_DBCP_NAME, MemberType.USER, userID, nickname, pwdHint, pwdAnswer, passwordBytes, ip);
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
		
		
		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance()
					.getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}		
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			create.update(SB_SEQ_TB).set(SB_SEQ_TB.SQ_VALUE, UInteger.valueOf(1))
			.where(SB_SEQ_TB.SQ_ID.eq(freeBoardSequenceID)).execute();			
			
			create.delete(SB_BOARD_VOTE_TB).execute();
			create.delete(SB_BOARD_FILELIST_TB).execute();
			create.delete(SB_BOARD_HISTORY_TB).execute();
			create.delete(SB_BOARD_TB).execute();
			
			create.update(SB_BOARD_INFO_TB)
			.set(SB_BOARD_INFO_TB.ADMIN_TOTAL, 0)
			.set(SB_BOARD_INFO_TB.USER_TOTAL, 0)
			.execute();
			
			conn.commit();
			
		} catch (Exception e) {

			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
			}

			log.warn(e.getMessage(), e);

			fail(e.getMessage());
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
	}
	
	@After
	public void tearDown(){		
		
		DataSource dataSource = null;
		try {
			dataSource = DBCPManager.getInstance()
					.getBasicDataSource(TEST_DBCP_NAME);
		} catch (DBCPDataSourceNotFoundException e) {
			log.warn(e.getMessage(), e);
			fail(e.getMessage());
		}		
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			for (BoardType boardType : BoardType.values()) {
				UByte boardID = UByte.valueOf(boardType.getBoardID());
				int adminCount = create.selectCount()
						.from(SB_BOARD_TB)
						.where(SB_BOARD_TB.BOARD_ID.eq(boardID)).fetchOne().value1();
				
				int userCount = create.selectCount().from(SB_BOARD_TB)
				.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
				.and(SB_BOARD_TB.BOARD_ST.eq(BoardStateType.OK.getValue())).fetchOne().value1();
				
				Record2<Integer, Integer>  boardInfoRecord = create.select(SB_BOARD_INFO_TB.ADMIN_TOTAL, SB_BOARD_INFO_TB.USER_TOTAL)
				.from(SB_BOARD_INFO_TB)
				.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID))
				.fetchOne();
				
				int adminCountOfBoardInfoTable = boardInfoRecord.getValue(SB_BOARD_INFO_TB.ADMIN_TOTAL);
				int userCountOfBoardInfoTable = boardInfoRecord.getValue(SB_BOARD_INFO_TB.USER_TOTAL);
				
				assertEquals("어드민인 경우 게시판 전체 글 갯수 비교",  adminCount, adminCountOfBoardInfoTable);
				assertEquals("유저인 경우 게시판 전체 글 갯수 비교",  userCount, userCountOfBoardInfoTable);
			}
			
			
			conn.commit();
			
		} catch (Exception e) {

			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
			}

			log.warn(e.getMessage(), e);

			fail(e.getMessage());
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
	}
	
	/*private BoardTreeNode makeBoardTreeNodeWithoutTreeInfomation(short boardID, short depth, 
			String writerID, String subject, String content) {
		BoardTreeNode boardTreeNode = new BoardTreeNode();
		boardTreeNode.setBoardID(boardID);
		boardTreeNode.setDepth(depth);
		boardTreeNode.setWriterID(writerID);
		boardTreeNode.setSubject(subject);
		boardTreeNode.setContent(content);
		
		return boardTreeNode;
	}*/
	
	private BoardTree makeBoardTreeWithoutTreeInfomation(BoardType boardType) {
		String writerID = "test01";
		String otherID = "test02";
		
		BoardTree boardTree = new BoardTree();
		
		
		final short boardID = boardType.getBoardID();
		{
			BoardTreeNode root1BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, (short)0, writerID, "루트1", "루트1");
			
			List<BoardTreeNode> root1ChildBoardTreeNodeList = new ArrayList<BoardTreeNode>();
			{
				BoardTreeNode root1Child1BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, (short)1, otherID, "루트1_자식1", "루트1_자식1");				
				root1ChildBoardTreeNodeList.add(root1Child1BoardTreeNode);
			}
			
			{
				BoardTreeNode root1Child2BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, (short)1, writerID, "루트1_자식2", "루트1_자식2");
				List<BoardTreeNode> root1Child2BoardTreeNodeList = new ArrayList<BoardTreeNode>();
				{
					BoardTreeNode root1Child2Child1BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, (short)2, otherID, "루트1_자식2_자식1", "루트1_자식2_자식1");
					root1Child2BoardTreeNodeList.add(root1Child2Child1BoardTreeNode);
				}
				{
					BoardTreeNode root1Child2Child2BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, (short)2, writerID, "루트1_자식2_자식2", "루트1_자식2_자식2");
					root1Child2BoardTreeNodeList.add(root1Child2Child2BoardTreeNode);
				}
				{
					BoardTreeNode root1Child2Child3BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, (short)2, otherID, "루트1_자식2_자식3", "루트1_자식2_자식3");
					List<BoardTreeNode> root1Child2Child3BoardTreeNodeList = new ArrayList<BoardTreeNode>();
					{
						BoardTreeNode root1Child2Child3Child1BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, (short)3, writerID, "루트1_자식2_자식3_자식1", "루트1_자식2_자식3_자식1");
						List<BoardTreeNode> root1Child2Child3Child1BoardTreeNodeList = new ArrayList<BoardTreeNode>();
						{
							BoardTreeNode root1Child2Child3Child1Child1BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, (short)4, otherID, "루트1_자식2_자식3_자식1_자식1", "루트1_자식2_자식3_자식1_자식1");
							root1Child2Child3Child1BoardTreeNodeList.add(root1Child2Child3Child1Child1BoardTreeNode);
						}
						{
							BoardTreeNode root1Child2Child3Child1Child2BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, (short)4, writerID, "루트1_자식2_자식3_자식1_자식2", "루트1_자식2_자식3_자식1_자식2");
							root1Child2Child3Child1BoardTreeNodeList.add(root1Child2Child3Child1Child2BoardTreeNode);
						}
						{
							BoardTreeNode root1Child2Child3Child1Child3BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, (short)4, otherID, "루트1_자식2_자식3_자식1_자식3", "루트1_자식2_자식3_자식1_자식3");
							root1Child2Child3Child1BoardTreeNodeList.add(root1Child2Child3Child1Child3BoardTreeNode);
						}
						root1Child2Child3Child1BoardTreeNode.setChildBoardTreeNodeList(root1Child2Child3Child1BoardTreeNodeList);
						
						root1Child2Child3BoardTreeNodeList.add(root1Child2Child3Child1BoardTreeNode);
					}
					root1Child2Child3BoardTreeNode.setChildBoardTreeNodeList(root1Child2Child3BoardTreeNodeList);
					root1Child2BoardTreeNodeList.add(root1Child2Child3BoardTreeNode);
				}
				root1Child2BoardTreeNode.setChildBoardTreeNodeList(root1Child2BoardTreeNodeList);
				
				
				root1ChildBoardTreeNodeList.add(root1Child2BoardTreeNode);
			}
			
			
			{
				BoardTreeNode root1Child3BoardTreeNode = BoardTree.makeBoardTreeNodeWithoutTreeInfomation(boardID, (short)1, otherID, "루트1_자식3", "루트1_자식3");				
				root1ChildBoardTreeNodeList.add(root1Child3BoardTreeNode);
			}
			root1BoardTreeNode.setChildBoardTreeNodeList(root1ChildBoardTreeNodeList);
			
			boardTree.add(root1BoardTreeNode);
		}
		
		return boardTree;
	}
	
	private void makeChildBoardTreeRecordUsingChildBoardTreeNode(
			BoardReplyReqServerTask boardReplyReqServerTask,
			long parentNo,
			BoardTreeNode childBoardTreeNode) {
		
		// UShort parentOrderSeq = UShort.valueOf(0);
		
		BoardReplyReq boardReplyReq = new BoardReplyReq();
		boardReplyReq.setBoardID(childBoardTreeNode.getBoardID());
		boardReplyReq.setParentBoardNo(parentNo);
		boardReplyReq.setSubject(childBoardTreeNode.getSubject());
		boardReplyReq.setContent(childBoardTreeNode.getContent());		
		boardReplyReq.setRequestUserID(childBoardTreeNode.getWriterID());
		boardReplyReq.setIp("127.0.0.1");		
		
		List<BoardReplyReq.AttachedFile> attachedFileList = new ArrayList<BoardReplyReq.AttachedFile>();
		boardReplyReq.setAttachedFileCnt((short)attachedFileList.size());
		boardReplyReq.setAttachedFileList(attachedFileList);
		
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
		
		childBoardTreeNode.setBoardNo(boardReplyRes.getBoardNo());
		
		List<BoardTreeNode> childChildBoardTreeNodeList = childBoardTreeNode.getChildBoardTreeNodeList();
		int childChildBoardTreeNodeListSize = childChildBoardTreeNodeList.size();
		for (int i=childChildBoardTreeNodeListSize - 1; i >= 0 ; i--) {
			BoardTreeNode childChildBoardTreeNode = childChildBoardTreeNodeList.get(i);
			makeChildBoardTreeRecordUsingChildBoardTreeNode(
					boardReplyReqServerTask, childBoardTreeNode.getBoardNo(), 
					childChildBoardTreeNode);
		}	
	}
	
	private void makeRootBoardTreeRecordUsingRootBoardTreeNode(
			BoardWriteReqServerTask boardWriteReqServerTask,
			BoardReplyReqServerTask boardReplyReqServerTask,
			BoardTreeNode rootBoardTreeNode) {
		BoardWriteReq boardWriteReq = new BoardWriteReq();
		boardWriteReq.setRequestUserID(rootBoardTreeNode.getWriterID());
		boardWriteReq.setBoardID(rootBoardTreeNode.getBoardID());
		boardWriteReq.setSubject(rootBoardTreeNode.getSubject());
		boardWriteReq.setContent(rootBoardTreeNode.getContent());
		boardWriteReq.setIp("172.16.0.1");
		List<BoardWriteReq.NewAttachedFile> attachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();
		boardWriteReq.setNewAttachedFileCnt((short)attachedFileList.size());
		boardWriteReq.setNewAttachedFileList(attachedFileList);
		BoardWriteRes boardWriteRes = null;
		try {
			boardWriteRes = boardWriteReqServerTask.doWork(TEST_DBCP_NAME, boardWriteReq);
		} catch(ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		rootBoardTreeNode.setBoardNo(boardWriteRes.getBoardNo());
		// rootBoardTreeNode.setGroupSeq(rootBoardTreeNode.getTotalNodes()-1);
		
		
		List<BoardTreeNode> childBoardTreeNodeList = rootBoardTreeNode.getChildBoardTreeNodeList();
		int childBoardTreeNodeListSize = childBoardTreeNodeList.size();
		for (int i=childBoardTreeNodeListSize - 1; i >= 0 ; i--) {
			BoardTreeNode childBoardTreeNode = childBoardTreeNodeList.get(i);
			makeChildBoardTreeRecordUsingChildBoardTreeNode(
					boardReplyReqServerTask, rootBoardTreeNode.getBoardNo(), 
					childBoardTreeNode);
		}
	}
	
	/**
	 * <pre>
	 * 지정한 루트 노드를 루트로한 를 전위 순환하며 방문한 루트를 지정한 스택에 넣는다.
	 * 이렇게 저장된 게시판 트리 노드 스택은 게시판 트리 노드가 그룹 시퀀스 역순으로 쌓인 스택이다.
	 * </pre>
	 * 
	 * @param boardTreeNodeStack
	 * @param relativeRootBoardTreeNode
	 */
	private void preOrder(Stack<BoardTreeNode> boardTreeNodeStack,
			BoardTreeNode relativeRootBoardTreeNode) {
		boardTreeNodeStack.push(relativeRootBoardTreeNode);
		
		List<BoardTreeNode> childBoardTreeNodeList = relativeRootBoardTreeNode.getChildBoardTreeNodeList();
		for (BoardTreeNode childBoardTreeNode : childBoardTreeNodeList) {
			preOrder(boardTreeNodeStack, childBoardTreeNode);
		}
	}	

	@Test
	public void testGetToGroupSeqOfRelativeRootBoard() {
		final Stack<BoardTreeNode> boardTreeNodeStack =
				new Stack<BoardTreeNode>();
		final BoardWriteReqServerTask boardWriteReqServerTask = new BoardWriteReqServerTask();
		final BoardReplyReqServerTask boardReplyReqServerTask = new BoardReplyReqServerTask();
		final BoardType boardType = BoardType.FREE;
		
		BoardTree boardTree = makeBoardTreeWithoutTreeInfomation(boardType);		
		List<BoardTreeNode> rootBoardTreeNodeList = boardTree.getRootBoardTreeNodeList();
		
		for (BoardTreeNode rootBoardTreeNode : rootBoardTreeNodeList) {
			makeRootBoardTreeRecordUsingRootBoardTreeNode(
					boardWriteReqServerTask, 
					boardReplyReqServerTask, rootBoardTreeNode);
			
			boardTreeNodeStack.clear();
			
			preOrder(boardTreeNodeStack, rootBoardTreeNode);
			
			int groupSeq = 0;
			while (! boardTreeNodeStack.isEmpty()) {
				BoardTreeNode boardTreeNode = boardTreeNodeStack.pop();
				boardTreeNode.setGroupSeq(groupSeq);
				groupSeq++;
				
				// boardNoToBoardTreeNodeHash.put(boardTreeNode.getBoardNo(), boardTreeNode);
			}
		}
				
		DataSource dataSource = null;
		
		try {
		dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME);
		} catch(Exception e) {
			log.warn("", e);
			fail("fail to get a instance of DataSource class");
		}

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(TEST_DBCP_NAME));
			
			BoardTreeNode boardTreeNode = boardTree.find("루트1_자식2_자식1_자식3");
			UShort expectedToGroupSeq = UShort.valueOf(1);			
			UShort acutalToGroupSeq = ServerDBUtil.getToGroupSeqOfRelativeRootBoard(create, 
					UByte.valueOf(boardType.getBoardID()), 
					UInteger.valueOf(boardTreeNode.getGroupNo()), 
					UShort.valueOf(boardTreeNode.getGroupSeq()), 
					UInteger.valueOf(boardTreeNode.getParentNo()));
			
			assertEquals("1.트리의 마지막 그룹 시퀀스 번호 비교", expectedToGroupSeq, acutalToGroupSeq);
			
			
			boardTreeNode = boardTree.find("루트1_자식3");
			expectedToGroupSeq = UShort.valueOf(0);			
			acutalToGroupSeq = ServerDBUtil.getToGroupSeqOfRelativeRootBoard(create, 
					UByte.valueOf(boardType.getBoardID()), 
					UInteger.valueOf(boardTreeNode.getGroupNo()), 
					UShort.valueOf(boardTreeNode.getGroupSeq()), 
					UInteger.valueOf(boardTreeNode.getParentNo()));
			
			assertEquals("2.트리의 마지막 그룹 시퀀스 번호 비교", expectedToGroupSeq, acutalToGroupSeq);
		
		} catch (Exception e) {
			log.warn("error", e);
			
			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
			}
			fail("에러 발생::errmsg="+e.getMessage());
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
	}

}
