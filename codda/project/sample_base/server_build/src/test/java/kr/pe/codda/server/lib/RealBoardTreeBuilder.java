package kr.pe.codda.server.lib;

import static org.junit.Assert.fail;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.codda.impl.message.BoardReplyRes.BoardReplyRes;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.impl.task.server.BoardReplyReqServerTask;
import kr.pe.codda.impl.task.server.BoardWriteReqServerTask;

public final class RealBoardTreeBuilder implements RealBoardTreeBuilderIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(RealBoardTreeBuilder.class);
	
	private String workingDBName = null;
	private Stack<BoardTreeNode> boardTreeNodeStack =
			new Stack<BoardTreeNode>();
	private  final BoardWriteReqServerTask boardWriteReqServerTask = new BoardWriteReqServerTask();
	private  final BoardReplyReqServerTask boardReplyReqServerTask = new BoardReplyReqServerTask();
	

	@Override
	public BoardTree build(String workingDBName, VirtualBoardTreeBuilderIF  virtualBoardTreeBuilder, BoardType boardType) {
		this.workingDBName = workingDBName;
		
		BoardTree boardTree = virtualBoardTreeBuilder.build(boardType);
		
		
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
				
				boardTree.put(boardTreeNode.getBoardNo(), boardTreeNode);
			}
		}
		return boardTree;
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
			boardWriteRes = boardWriteReqServerTask.doWork(workingDBName, boardWriteReq);
		} catch(ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}
		
		rootBoardTreeNode.setBoardNo(boardWriteRes.getBoardNo());
		rootBoardTreeNode.setGroupNo(boardWriteRes.getBoardNo());
		rootBoardTreeNode.setDepth((short)0);
		// rootBoardTreeNode.setGroupSeq(rootBoardTreeNode.getTotalNodes()-1);
		
		
		List<BoardTreeNode> childBoardTreeNodeList = rootBoardTreeNode.getChildBoardTreeNodeList();
		/*int childBoardTreeNodeListSize = childBoardTreeNodeList.size();
		for (int i=childBoardTreeNodeListSize - 1; i >= 0 ; i--) {
			BoardTreeNode childBoardTreeNode = childBoardTreeNodeList.get(i);
			makeChildBoardTreeRecordUsingChildBoardTreeNode(
					boardReplyReqServerTask, rootBoardTreeNode.getGroupNo(), 
					rootBoardTreeNode.getBoardNo(), 
					childBoardTreeNode);
		}*/
		
		for (BoardTreeNode childBoardTreeNode : childBoardTreeNodeList) {
			makeChildBoardTreeRecordUsingChildBoardTreeNode(
					boardReplyReqServerTask, rootBoardTreeNode.getGroupNo(), 
					rootBoardTreeNode.getBoardNo(), 
					(short)1,
					childBoardTreeNode);
		}
	}
	
	private void makeChildBoardTreeRecordUsingChildBoardTreeNode(
			BoardReplyReqServerTask boardReplyReqServerTask,
			long groupNo,
			long parentNo,
			short depth,
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
			boardReplyRes = boardReplyReqServerTask.doWork(workingDBName, boardReplyReq);
			// log.info(boardReplyRes.toString());
		} catch(ServerServiceException e) {
			log.warn(e.getMessage(), e);
			fail("fail to execuate doTask");
		} catch (Exception e) {
			log.warn("unknown error", e);
			fail("fail to execuate doTask");
		}		
		
		childBoardTreeNode.setGroupNo(groupNo);
		childBoardTreeNode.setParentNo(parentNo);
		childBoardTreeNode.setBoardNo(boardReplyRes.getBoardNo());
		childBoardTreeNode.setDepth(depth);
		
		List<BoardTreeNode> childChildBoardTreeNodeList = childBoardTreeNode.getChildBoardTreeNodeList();
		/*int childChildBoardTreeNodeListSize = childChildBoardTreeNodeList.size();
		for (int i=childChildBoardTreeNodeListSize - 1; i >= 0 ; i--) {
			BoardTreeNode childChildBoardTreeNode = childChildBoardTreeNodeList.get(i);
			makeChildBoardTreeRecordUsingChildBoardTreeNode(
					boardReplyReqServerTask, groupNo, childBoardTreeNode.getBoardNo(), 
					childChildBoardTreeNode);
		}	*/
		
		for (BoardTreeNode childChildBoardTreeNode : childChildBoardTreeNodeList) {
			makeChildBoardTreeRecordUsingChildBoardTreeNode(
					boardReplyReqServerTask, groupNo, childBoardTreeNode.getBoardNo(), 
					(short)(depth + 1),
					childChildBoardTreeNode);
		}
	}
	
	private void preOrder(Stack<BoardTreeNode> boardTreeNodeStack,
			BoardTreeNode relativeRootBoardTreeNode) {
		boardTreeNodeStack.push(relativeRootBoardTreeNode);
		
		List<BoardTreeNode> childBoardTreeNodeList = relativeRootBoardTreeNode.getChildBoardTreeNodeList();
		for (BoardTreeNode childBoardTreeNode : childBoardTreeNodeList) {
			preOrder(boardTreeNodeStack, childBoardTreeNode);
		}
	}	
}
