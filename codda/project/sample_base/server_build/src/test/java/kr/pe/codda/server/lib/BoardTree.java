package kr.pe.codda.server.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BoardTree {
	private List<BoardTreeNode> rootBoardTreeNodeList = new ArrayList<BoardTreeNode>();
	private HashMap<Long, BoardTreeNode> boardNoToBoardTreeNodeHash =
			new HashMap<Long, BoardTreeNode>();

	public List<BoardTreeNode> getRootBoardTreeNodeList() {
		return rootBoardTreeNodeList;
	}

	public void setRootBoardTreeNodeList(List<BoardTreeNode> rootBoardTreeNodeList) {
		this.rootBoardTreeNodeList = rootBoardTreeNodeList;
	}
	
	public int getTotal() {
		int total = 0;
		
		if (null != rootBoardTreeNodeList) {
			for (BoardTreeNode rootBoardTreeNode : rootBoardTreeNodeList) {
				total += rootBoardTreeNode.getTotal();
			}
		}
		
		return total;
	}
	
	public int getHashSize() {
		return boardNoToBoardTreeNodeHash.size();
	}
	
	public BoardTreeNode find(String subject) {
		if (null == subject) {
			throw new IllegalArgumentException("the parameter subject is null");
		}
		
		if (null == rootBoardTreeNodeList) {
			return null;
		}
		
		for (BoardTreeNode rootBoardTreeNode : rootBoardTreeNodeList) {
			BoardTreeNode findedBoardTreeNode = rootBoardTreeNode.find(subject);
			
			if (null != findedBoardTreeNode) {
				return findedBoardTreeNode;
			}
		}
		
		return null;
	}
	
	public void put(Long key, BoardTreeNode value) {
		boardNoToBoardTreeNodeHash.put(key, value);
	}
	
	public BoardTreeNode  get(Long key) {
		return boardNoToBoardTreeNodeHash.get(key);
	}
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BoardTree [rootBoardTreeNodeList=");
		builder.append(rootBoardTreeNodeList);
		builder.append("]");
		return builder.toString();
	}
}
