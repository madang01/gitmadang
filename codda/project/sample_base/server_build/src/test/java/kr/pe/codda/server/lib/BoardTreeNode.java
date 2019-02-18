package kr.pe.codda.server.lib;

import java.util.ArrayList;
import java.util.List;

public class BoardTreeNode {
	private short boardID;
	private long boardNo;
	private long groupNo;
	private int groupSeq;
	private long parentNo;
	private short depth;
	private String writerID;
	private String subject;
	private String contents;
	
	private List<BoardTreeNode> childBoardTreeNodeList = new ArrayList<BoardTreeNode>();
	
	
	public List<BoardTreeNode> getChildBoardTreeNodeList() {
		return childBoardTreeNodeList;
	}
	
	/*
	public void setChildBoardTreeNodeList(List<BoardTreeNode> childBoardTreeNodeList) {
		this.childBoardTreeNodeList = childBoardTreeNodeList;
	}*/
	public void addChildNode(BoardTreeNode childBoardTreeNode) {
		childBoardTreeNodeList.add(childBoardTreeNode);
	}
	
	public short getBoardID() {
		return boardID;
	}
	public void setBoardID(short boardID) {
		this.boardID = boardID;
	}
	public long getBoardNo() {
		return boardNo;
	}
	public void setBoardNo(long boardNo) {
		this.boardNo = boardNo;
	}
	public long getGroupNo() {
		return groupNo;
	}
	public void setGroupNo(long groupNo) {
		this.groupNo = groupNo;
	}
	public int getGroupSeq() {
		return groupSeq;
	}
	public void setGroupSeq(int groupSeq) {
		this.groupSeq = groupSeq;
	}
	public long getParentNo() {
		return parentNo;
	}
	public void setParentNo(long parentNo) {
		this.parentNo = parentNo;
	}
	public short getDepth() {
		return depth;
	}
	public void setDepth(short depth) {
		this.depth = depth;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getWriterID() {
		return writerID;
	}
	public void setWriterID(String writerID) {
		this.writerID = writerID;
	}
	
	public int getTotal() {
		int total = 1;
		
		if (null != childBoardTreeNodeList) {
			for (BoardTreeNode childBoardTreeNode : childBoardTreeNodeList) {
				total += childBoardTreeNode.getTotal();
			}
		}
		
		return total;
	}
	
	public BoardTreeNode find(String subject) {
		if (null == subject) {
			throw new IllegalArgumentException("the parameter subject is null");
		}
		
		if (subject.equals(this.subject)) {
			return this;
		}
		
		if (null == childBoardTreeNodeList) {
			return null;
		}
		
		for (BoardTreeNode childBoardTreeNode : childBoardTreeNodeList) {
			BoardTreeNode findedBoardTreeNode = childBoardTreeNode.find(subject);
			if (null != findedBoardTreeNode) {
				return findedBoardTreeNode;
			}
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BoardTreeNode [boardID=");
		builder.append(boardID);
		builder.append(", boardNo=");
		builder.append(boardNo);
		builder.append(", groupNo=");
		builder.append(groupNo);
		builder.append(", groupSeq=");
		builder.append(groupSeq);
		builder.append(", parentNo=");
		builder.append(parentNo);
		builder.append(", depth=");
		builder.append(depth);
		builder.append(", writerID=");
		builder.append(writerID);
		builder.append(", subject=");
		builder.append(subject);
		builder.append(", content=");
		builder.append(contents);
		builder.append(", childBoardTreeNodeList=");
		builder.append(childBoardTreeNodeList);
		builder.append("]");
		return builder.toString();
	}
	
	
}
