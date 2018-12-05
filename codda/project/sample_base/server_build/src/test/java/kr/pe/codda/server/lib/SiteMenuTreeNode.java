package kr.pe.codda.server.lib;

import java.util.ArrayList;

public class SiteMenuTreeNode {
	private long menuNo;
	private long parentNo;
	private short depth;
	private short orderSeq;
	private String menuName;
	private String linkURL;
	private java.util.List<SiteMenuTreeNode> childSiteMenuNodeList = new ArrayList<SiteMenuTreeNode>();
	
	public long getMenuNo() {
		return menuNo;
	}

	public void setMenuNo(long menuNo) {
		this.menuNo = menuNo;
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
	public short getOrderSeq() {
		return orderSeq;
	}

	public void setOrderSeq(short orderSeq) {
		this.orderSeq = orderSeq;
	}
	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public String getLinkURL() {
		return linkURL;
	}

	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}
	
	
	public java.util.List<SiteMenuTreeNode> getChildSiteMenuNodeList() {
		return childSiteMenuNodeList;
	}
	
	public void addChildSiteMenuNode(SiteMenuTreeNode childSiteMenuTreeNode) {
		childSiteMenuNodeList.add(childSiteMenuTreeNode);
	}
}
